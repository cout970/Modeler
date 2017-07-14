package com.cout970.modeler.view

import com.cout970.glutilities.texture.Texture
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.core.export.ModelImporters
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.model.material.MaterialNone
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.core.resource.fromClasspath
import org.liquidengine.legui.image.BufferedImage

/**
 * Created by cout970 on 2017/06/14.
 */
class GuiResources {

    lateinit var baseCubeMesh: IMesh
    lateinit var lightMesh: IMesh
    lateinit var translationArrow: IMesh

    lateinit var baseCubeTexture: Texture
    lateinit var cursorTexture: Texture
    lateinit var deleteIcon: BufferedImage
    lateinit var showIcon: BufferedImage
    lateinit var hideIcon: BufferedImage
    lateinit var applyMaterial: BufferedImage
    lateinit var loadMaterial: BufferedImage

    fun reload(loader: ResourceLoader) {
        log(Level.FINE) { "[GuiResources] Loading gui resources" }
        baseCubeMesh = ModelImporters.objImporter.importAsMesh("assets/models/cube.obj".fromClasspath(), true)
        lightMesh = ModelImporters.objImporter.importAsMesh("assets/models/light.obj".fromClasspath(), true)
        translationArrow = ModelImporters.objImporter.importAsMesh("assets/models/translation_x.obj".fromClasspath(),
                true)

        baseCubeTexture = loader.getTexture("assets/textures/models/cube.png").apply { magFilter = Texture.PIXELATED }
        cursorTexture = loader.getTexture("assets/textures/cursor.png")
        deleteIcon = BufferedImage("assets/textures/delete.png")
        showIcon = BufferedImage("assets/textures/show.png")
        hideIcon = BufferedImage("assets/textures/hide.png")
        applyMaterial = BufferedImage("assets/textures/applyMaterial.png")
        loadMaterial = BufferedImage("assets/textures/loadMaterial.png")

        MaterialNone.loadTexture(loader)
        log(Level.FINE) { "[GuiResources] Gui resources loaded" }
    }
}