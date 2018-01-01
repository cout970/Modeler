package core.model

import com.cout970.modeler.core.model.Model
import com.cout970.modeler.core.model.Object
import com.cout970.modeler.core.model.material.MaterialRef
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
        val modelA = Model(emptyList(), emptyList(), emptyList())
        val modelB = Model(emptyList(), emptyList(), emptyList())
        val result = Model(emptyList(), emptyList(), emptyList())

        assertEquals("Fail at merge", result.objects, modelA.merge(modelB).objects)
        assertEquals("Fail at merge", result.materials, modelA.merge(modelB).materials)
        assertEquals("Fail at merge", result.visibilities, modelA.merge(modelB).visibilities)
    }

    @Test
    fun `Check model merge without material`() {
        val obj1 = Object("", MeshFactory.createPlane(Vector2.ONE), MaterialRef(-1))

        val modelA = Model(listOf(obj1), emptyList(), emptyList())
        val modelB = Model(listOf(obj1), emptyList(), emptyList())
        val result = Model(listOf(obj1, obj1), emptyList(), emptyList())

        assertEquals("Fail at merge", result.objects, modelA.merge(modelB).objects)
        assertEquals("Fail at merge", result.materials, modelA.merge(modelB).materials)
        assertEquals("Fail at merge", result.visibilities, modelA.merge(modelB).visibilities)
    }

    @Test
    fun `Check model merge with material`() {
        val obj1 = Object("", MeshFactory.createPlane(Vector2.ONE), MaterialRef(0))
        val material = TexturedMaterial("name", ResourcePath.fromResourceLocation("path"))

        val modelA = Model(listOf(obj1), listOf(material), listOf(true))
        val modelB = Model(listOf(obj1), listOf(material), listOf(true))

        val resultObj = obj1.withMaterial(MaterialRef(1))
        val result = Model(listOf(obj1, resultObj), listOf(material, material), listOf(true, true))

        assertEquals("Fail at merge", result.objects, modelA.merge(modelB).objects)
        assertEquals("Fail at merge", result.materials, modelA.merge(modelB).materials)
        assertEquals("Fail at merge", result.visibilities, modelA.merge(modelB).visibilities)
    }
}