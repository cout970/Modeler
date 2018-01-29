package core.model

import com.cout970.modeler.core.model.*
import com.cout970.modeler.core.model.material.MaterialRefNone
import com.cout970.modeler.core.model.material.TexturedMaterial
import com.cout970.modeler.core.model.mesh.MeshFactory
import com.cout970.modeler.core.resource.ResourcePath
import com.cout970.vector.extensions.Vector2
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

/**
 * Created by cout970 on 2017/06/04.
 */
class TestModel {

    @Test
    fun `Check that copy method doesn't change the auto-generated id`() {
        val a = Model.empty()
        val b = a.copy()

        assertNotEquals(a.id, b.id)
        assertNotEquals(a, b)
    }

    @Test
    fun `Check model identity`() {
        val a = Model.empty()
        val b = Model.empty()

        assertEquals(a, a)
        assertEquals(b, b)
        assertNotEquals(a, b)
    }

    @Test
    fun `Check empty model merge`() {
        val modelA = Model.of(emptyList(), emptyList())
        val modelB = Model.of(emptyList(), emptyList())
        val result = Model.of(emptyList(), emptyList())

        assertEquals("Fail at merge", result.objects, modelA.merge(modelB).objects)
        assertEquals("Fail at merge", result.materials, modelA.merge(modelB).materials)
    }

    @Test
    fun `Check model merge without material`() {
        val obj1 = Object("", MeshFactory.createPlane(Vector2.ONE), MaterialRefNone)

        val modelA = Model.of(mapOf(obj1.toPair()), emptyList())
        val modelB = Model.of(mapOf(obj1.toPair()), emptyList())
        val result = Model.of(mapOf(obj1.toPair(), obj1.toPair()), emptyList())

        assertEquals("Fail at merge", result.objects, modelA.merge(modelB).objects)
        assertEquals("Fail at merge", result.materials, modelA.merge(modelB).materials)
    }

    @Test
    fun `Check model merge with material`() {
        val material = TexturedMaterial("name", ResourcePath.fromResourceLocation("path"))
        val obj1 = Object("", MeshFactory.createPlane(Vector2.ONE), material.ref)

        val modelA = Model.of(mapOf(obj1.toPair()), listOf(material))
        val modelB = Model.of(mapOf(obj1.toPair()), listOf(material))

        val resultObj = obj1.withMaterial(material.ref)
        val result = Model.of(mapOf(obj1.toPair(), resultObj.toPair()), listOf(material))

        assertEquals("Fail at merge", result.objects, modelA.merge(modelB).objects)
        assertEquals("Fail at merge", result.materials, modelA.merge(modelB).materials)
    }

    @Test
    fun `Check Object id is preserved on modification`() {
        val obj = ObjectCube("test", TRSTransformation.IDENTITY)
        val newObj = obj.copy()

        assertEquals(obj.id, newObj.id)
    }

    @Test
    fun `Check makeCopy changes the Object id`() {
        val obj = ObjectCube("test", TRSTransformation.IDENTITY)
        val newObj = obj.makeCopy()

        assertNotEquals(obj.id, newObj.id)
    }
}