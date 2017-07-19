package core.model

import com.cout970.modeler.core.model.Model
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
}