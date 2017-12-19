package core.model

import com.cout970.modeler.core.model.Model
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
}