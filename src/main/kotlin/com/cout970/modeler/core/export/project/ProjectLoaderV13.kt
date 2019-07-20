package com.cout970.modeler.core.export.project

import com.cout970.modeler.api.animation.*
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.`object`.*
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.animation.*
import com.cout970.modeler.core.export.*
import com.cout970.modeler.core.log.print
import com.cout970.modeler.core.model.Model
import com.cout970.modeler.core.model.`object`.GroupTree
import com.cout970.modeler.core.model.`object`.Object
import com.cout970.modeler.core.model.`object`.ObjectCube
import com.cout970.modeler.core.model.`object`.biMultimapOf
import com.cout970.modeler.core.model.material.ColoredMaterial
import com.cout970.modeler.core.model.material.MaterialNone
import com.cout970.modeler.core.model.material.TexturedMaterial
import com.cout970.modeler.core.model.mesh.FaceIndex
import com.cout970.modeler.core.model.mesh.Mesh
import com.cout970.modeler.core.model.selection.ObjectRefNone
import com.cout970.modeler.core.project.ProjectProperties
import com.cout970.modeler.core.resource.ResourcePath
import com.cout970.modeler.util.toResourcePath
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.google.gson.*
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableMap
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import java.io.File
import java.lang.reflect.Type
import java.net.URI
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream


object ProjectLoaderV13 {

    const val VERSION = "1.3"

    val gson = GsonBuilder()
        .setExclusionStrategies(ProjectExclusionStrategy())
        .setPrettyPrinting()
        .enableComplexMapKeySerialization()
        .registerTypeAdapter(UUID::class.java, UUIDSerializer())
        .registerTypeAdapter(IVector3::class.java, Vector3Serializer())
        .registerTypeAdapter(IVector2::class.java, Vector2Serializer())
        .registerTypeAdapter(IQuaternion::class.java, QuaternionSerializer())
        .registerTypeAdapter(IGroupRef::class.java, GroupRefSerializer())
        .registerTypeAdapter(IMaterialRef::class.java, MaterialRefSerializer())
        .registerTypeAdapter(IObjectRef::class.java, ObjectRefSerializer())
        .registerTypeAdapter(ITransformation::class.java, TransformationSerializer())
        .registerTypeAdapter(ImmutableMap::class.java, ImmutableMapSerializer())
        .registerTypeAdapter(ImmutableGroupTree::class.java, ImmutableGroupTreeSerializer())
        .registerTypeAdapter(IModel::class.java, ModelSerializer())
        .registerTypeAdapter(IMaterial::class.java, MaterialSerializer())
        .registerTypeAdapter(IObject::class.java, ObjectSerializer())
        .registerTypeAdapter(IGroupTree::class.java, GroupTreeSerializer())
        .registerTypeAdapter(IGroup::class.java, serializerOf<Group>())
        .registerTypeAdapter(IMesh::class.java, MeshSerializer())
        .registerTypeAdapter(IAnimation::class.java, AnimationSerializer())
        .registerTypeAdapter(IAnimationRef::class.java, AnimationRefSerializer())
        .registerTypeAdapter(AnimationTarget::class.java, AnimationTargetSerializer())
        .create()!!

    fun loadProject(zip: ZipFile, path: String): ProgramSave {

        val properties = zip.load<ProjectProperties>("project.json", gson)
            ?: throw IllegalStateException("Missing file 'project.json' inside '$path'")

        val model = zip.load<IModel>("model.json", gson)
            ?: throw IllegalStateException("Missing file 'model.json' inside '$path'")

        val animations = zip.load<List<IAnimation>>("animation.json", gson) ?: emptyList()

        checkIntegrity(listOf(model.objectMap, model.materialMap, model.groupMap, model.tree, model.animationMap))
        checkIntegrity(listOf(animations))

        val finalModel = animations.fold(model) { tmpModel, anim -> tmpModel.addAnimation(anim) }
        return ProgramSave(VERSION, properties, finalModel, emptyList())
    }

    fun saveProject(path: String, save: ProgramSave) {
        val file = File(path)
        val tmp = createTempFile(directory = file.parentFile)
        val zip = ZipOutputStream(tmp.outputStream())

        zip.let {

            save.textures.forEach { mat ->
                try {
                    val name = FilenameUtils.getName(mat.path.uri.toURL().path)
                    it.putNextEntry(ZipEntry("textures/$name"))
                    IOUtils.copy(mat.path.inputStream(), it)
                } catch (e: Exception) {
                    e.print()
                } finally {
                    it.closeEntry()
                }
            }

            it.putNextEntry(ZipEntry("version.json"))
            it.write(gson.toJson(save.version).toByteArray())
            it.closeEntry()

            it.putNextEntry(ZipEntry("project.json"))
            it.write(gson.toJson(save.projectProperties).toByteArray())
            it.closeEntry()

            val model = save.textures.fold(save.model) { acc, mat ->
                val name = FilenameUtils.getName(mat.path.uri.toURL().path)
                val newPath = File(path).toResourcePath().enterZip("textures/$name")

                acc.modifyMaterial(mat.copy(path = newPath))
            }

            it.putNextEntry(ZipEntry("model.json"))
            it.write(gson.toJson(model, IModel::class.java).toByteArray())
            it.closeEntry()
        }
        zip.close()

        Files.copy(tmp.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING)

        if (tmp != file) {
            tmp.delete()
        }
    }

    class ModelSerializer : JsonSerializer<IModel>, JsonDeserializer<IModel> {

        override fun serialize(src: IModel, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonObject().apply {
                add("objectMap", context.serializeT(src.objectMap))
                add("materialMap", context.serializeT(src.materialMap))
                add("groupMap", context.serializeT(src.groupMap))
                add("animationMap", context.serializeT(src.animationMap))
                add("groupTree", context.serializeT(src.tree))
            }
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): IModel {
            val obj = json.asJsonObject
            return Model.of(
                objectMap = context.deserializeT(obj["objectMap"]),
                materialMap = context.deserializeT(obj["materialMap"]),
                groupMap = context.deserializeT(obj["groupMap"]),
                groupTree = context.deserializeT(obj["groupTree"]),
                animationMap = obj["animationMap"]?.let {
                    context.deserializeT<Map<IAnimationRef, IAnimation>>(it)
                        .filter { (key, value) -> key == value.ref }
                } ?: emptyMap()
            )
        }
    }

    class MaterialSerializer : JsonSerializer<IMaterial>, JsonDeserializer<IMaterial> {

        override fun serialize(src: IMaterial, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonObject().apply {
                addProperty("name", src.name)
                when (src) {
                    is TexturedMaterial -> {
                        addProperty("type", "texture")
                        addProperty("path", src.path.uri.toString())
                        add("id", context.serializeT(src.id))
                    }
                    is ColoredMaterial -> {
                        addProperty("type", "color")
                        add("color", context.serializeT(src.color))
                        add("id", context.serializeT(src.id))
                    }
                    else -> addProperty("type", "none")
                }
            }
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): IMaterial {
            val obj = json.asJsonObject
            if (obj.has("type")) {
                return when (obj["type"].asString) {
                    "texture" -> {
                        val id = context.deserialize<UUID>(obj["id"], UUID::class.java)
                        TexturedMaterial(obj["name"].asString, ResourcePath(URI(obj["path"].asString)), id)
                    }
                    "color" -> {
                        val id = context.deserialize<UUID>(obj["id"], UUID::class.java)
                        ColoredMaterial(obj["name"].asString, context.deserializeT(obj["color"]), id)
                    }
                    else -> MaterialNone
                }
            }
            return when {
                obj["name"].asString == "noTexture" -> MaterialNone
                else -> {
                    val id = context.deserialize<UUID>(obj["id"], UUID::class.java)
                    TexturedMaterial(obj["name"].asString, ResourcePath(URI(obj["path"].asString)), id)
                }
            }
        }
    }

    class ObjectSerializer : JsonSerializer<IObject>, JsonDeserializer<IObject> {

        override fun serialize(src: IObject, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return context.serialize(src).asJsonObject.apply {
                addProperty("class", src.javaClass.simpleName)
            }
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): IObject {
            val obj = json.asJsonObject
            return when (obj["class"].asString) {
                "ObjectCube" -> {
                    ObjectCube(
                        name = context.deserialize(obj["name"], String::class.java),
                        transformation = context.deserialize(obj["transformation"], ITransformation::class.java),
                        material = context.deserialize(obj["material"], IMaterialRef::class.java),
                        textureOffset = context.deserialize(obj["textureOffset"], IVector2::class.java),
                        textureSize = context.deserialize(obj["textureSize"], IVector2::class.java),
                        mirrored = context.deserialize(obj["mirrored"], Boolean::class.java),
                        visible = context.deserialize(obj["visible"], Boolean::class.java),
                        id = context.deserialize(obj["id"], UUID::class.java)
                    )
                }
                "Object" -> Object(
                    name = context.deserialize(obj["name"], String::class.java),
                    mesh = context.deserialize(obj["mesh"], IMesh::class.java),
                    material = context.deserialize(obj["material"], IMaterialRef::class.java),
                    transformation = context.deserialize(obj["transformation"], ITransformation::class.java),
                    visible = context.deserialize(obj["visible"], Boolean::class.java),
                    id = context.deserialize(obj["id"], UUID::class.java)
                )

                else -> throw IllegalStateException("Unknown Class: ${obj["class"]}")
            }
        }
    }

    class GroupTreeSerializer : JsonSerializer<IGroupTree>, JsonDeserializer<IGroupTree> {

        data class Aux(val key: IGroupRef, val value: Set<IGroupRef>)
        data class Aux2(val key: IGroupRef, val value: IGroupRef)

        override fun serialize(src: IGroupTree, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            val tree = src as GroupTree
            return JsonObject().apply {
                add("childMap", JsonArray().also { array ->
                    tree.childMap.map { Aux(it.key, it.value) }.map { context.serializeT(it) }.forEach { it -> array.add(it) }
                })
                add("parentMap", JsonArray().also { array ->
                    tree.parentMap.map { Aux2(it.key, it.value) }.map { context.serializeT(it) }.forEach { it -> array.add(it) }
                })
            }
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): IGroupTree {
            if (json.isJsonNull) return GroupTree.emptyTree()
            val obj = json.asJsonObject

            val childMapArray = obj["childMap"].asJsonArray
            val parentMapArray = obj["parentMap"].asJsonArray

            val childMap = childMapArray
                .map { context.deserialize(it, Aux::class.java) as Aux }
                .map { it.key to it.value }
                .toMap()
                .toImmutableMap()

            val parentMap = parentMapArray
                .map { context.deserialize(it, Aux2::class.java) as Aux2 }
                .map { it.key to it.value }
                .toMap()
                .toImmutableMap()

            return GroupTree(parentMap, childMap)
        }
    }

    class MeshSerializer : JsonSerializer<IMesh>, JsonDeserializer<IMesh> {

        override fun serialize(src: IMesh, typeOfSrc: Type?, context: JsonSerializationContext): JsonElement {
            val pos = context.serializeT(src.pos)
            val tex = context.serializeT(src.tex)

            val faces = JsonArray().apply {

                src.faces.forEach { face ->
                    add(JsonArray().apply {

                        repeat(face.vertexCount) {
                            add(JsonArray().apply {
                                add(face.pos[it]); add(face.tex[it])
                            })
                        }
                    })
                }
            }

            return JsonObject().apply {
                add("pos", pos)
                add("tex", tex)
                add("faces", faces)
            }
        }

        override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): IMesh {
            val obj = json.asJsonObject

            val pos = context.deserializeT<List<IVector3>>(obj["pos"])
            val tex = context.deserializeT<List<IVector2>>(obj["tex"])

            val faces = obj["faces"].asJsonArray.map { face ->
                val vertex = face.asJsonArray
                val posIndices = ArrayList<Int>(vertex.size())
                val texIndices = ArrayList<Int>(vertex.size())

                repeat(vertex.size()) {
                    val pair = vertex[it].asJsonArray

                    posIndices.add(pair[0].asInt)
                    texIndices.add(pair[1].asInt)
                }

                FaceIndex.from(posIndices, texIndices)
            }

            return Mesh(pos, tex, faces)
        }
    }

    class AnimationSerializer : JsonSerializer<IAnimation>, JsonDeserializer<IAnimation> {

        override fun serialize(src: IAnimation, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonObject().apply {
                add("id", context.serializeT(src.id))
                addProperty("name", src.name)
                addProperty("timeLength", src.timeLength)

                add("channels", src.channels.values.toJsonArray { v ->
                    JsonObject().apply {
                        add("id", context.serializeT(v.id))
                        addProperty("name", v.name)
                        addProperty("interpolation", v.interpolation.name)
                        addProperty("enabled", v.enabled)
                        addProperty("type", v.type.toString())

                        add("keyframes", v.keyframes.toJsonArray {
                            JsonObject().apply {
                                addProperty("time", it.time)
                                add("value", context.serializeT(it.value))
                            }
                        })

                    }
                })

                add("mapping", src.channelMapping.entries.toJsonArray { (key, value) ->
                    JsonObject().apply {
                        add("key", context.serializeT(key.id))
                        add("value", context.serializeT(value))
                    }
                })
            }
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): IAnimation {
            if (json.isJsonNull) return Animation.of()

            val obj = json.asJsonObject
            val name = if (obj.has("name")) obj["name"].asString else "animation"

            val id = if (obj.has("id")) context.deserializeT<UUID>(obj["id"]) else UUID.randomUUID()

            val channelMapping = obj["mapping"].asJsonArray
                .map { it.asJsonObject }
                .map { context.deserializeT<UUID>(it["key"]) to context.deserializeT<AnimationTarget>(it["value"]) }
                .map { (ChannelRef(it.first) as IChannelRef) to it.second }
                .toMap()

            val channels = obj["channels"].asJsonArray.map { elem ->
                val channel = elem.asJsonObject

                val interName = channel["interpolation"].asString
                val keyframesJson = channel["keyframes"].asJsonArray

                val type = if (channel.has("type")) {
                    ChannelType.valueOf(channel["type"].asString)
                } else {
                    ChannelType.TRANSLATION
                }

                val keyframes = keyframesJson.map { it.asJsonObject }.map {
                    Keyframe(
                        time = it["time"].asFloat,
                        value = context.deserializeT(it["value"])
                    )
                }

                Channel(
                    name = channel["name"].asString,
                    interpolation = InterpolationMethod.valueOf(interName),
                    enabled = channel["enabled"].asBoolean,
                    keyframes = keyframes,
                    type = type,
                    id = context.deserializeT(channel["id"])
                )
            }

            return Animation(
                channels = channels.associateBy { it.ref },
                timeLength = obj["timeLength"].asFloat,
                channelMapping = channelMapping,
                name = name,
                id = id
            )
        }
    }

    class ImmutableGroupTreeSerializer : JsonSerializer<ImmutableGroupTree>, JsonDeserializer<ImmutableGroupTree> {

        data class Aux(val key: IGroupRef, val value: List<IObjectRef>)
        data class Aux2(val key: IGroupRef, val value: List<IGroupRef>)

        override fun serialize(src: ImmutableGroupTree, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            val a = src.objects.map { Aux(it.first, it.second) }
            val b = src.groups.map { Aux2(it.first, it.second) }

            return JsonObject().apply {
                add("objects", context.serializeT(a))
                add("groups", context.serializeT(b))
            }
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ImmutableGroupTree {
            val obj = json.asJsonObject

            val objects = obj["objects"].asJsonArray
            val groups = obj["groups"].asJsonArray

            return ImmutableGroupTree(
                biMultimapOf(*objects.map { context.deserializeT<Aux>(it) }.map { it.key to it.value }.toTypedArray()),
                biMultimapOf(*groups.map { context.deserializeT<Aux2>(it) }.map { it.key to it.value }.toTypedArray())
            )
        }
    }

    class AnimationTargetSerializer : JsonSerializer<AnimationTarget>, JsonDeserializer<AnimationTarget> {

        override fun serialize(src: AnimationTarget, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return when (src) {
                is AnimationTargetGroup -> JsonObject().apply {
                    addProperty("type", "group")
                    add("ref", context.serializeT(src.ref))
                }
                is AnimationTargetObject -> JsonObject().apply {
                    addProperty("type", "object")
                    // TODO
                    add("ref", context.serializeT(src.refs.first()))
                }
            }
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): AnimationTarget {
            if (!json.isJsonObject) return AnimationTargetObject(listOf(ObjectRefNone))
            val obj = json.asJsonObject
            if (!obj.has("type")) return AnimationTargetObject(listOf(ObjectRefNone))

            // TODO
            return when (obj["type"].asString) {
                "group" -> AnimationTargetGroup(context.deserializeT(obj["ref"]))
                "object" -> AnimationTargetObject(listOf(context.deserializeT(obj["ref"])))
                else -> error("Invalid AnimationTarget type: ${obj["type"].asString}")
            }
        }
    }
}