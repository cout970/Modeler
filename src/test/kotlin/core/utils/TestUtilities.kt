package core.utils

import com.cout970.matrix.api.IMatrix4
import com.cout970.matrix.extensions.times
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.core.model.toTRS
import com.cout970.modeler.util.fromPivotToOrigin
import com.cout970.modeler.util.quatOfAngles
import com.cout970.modeler.util.transformVertex
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Quaternion
import com.cout970.vector.extensions.quatOfAxisAngled
import com.cout970.vector.extensions.vec3Of
import org.junit.Assert
import org.junit.Test

/**
 * Created by cout970 on 2017/08/29.
 */
class TestUtilities {

    @Test
    fun `Test fromPivotToOrigin with identity`() {

        val pos = vec3Of(0)
        val rot = Quaternion.IDENTITY

        val (resPos, resRot) = (pos to rot).fromPivotToOrigin()

        assertEquals(pos, resPos)
        Assert.assertEquals(rot, resRot)
    }

    @Test
    fun `Test fromPivotToOrigin with x=1 and rot=90degrees in y`() {

        val pos = vec3Of(1, 0, 0)
        val rot = quatOfAngles(0, 90, 0)

        val (resPos, resRot) = (pos to rot).fromPivotToOrigin()

        assertEquals(vec3Of(1, 0, -1), resPos)
        Assert.assertEquals(quatOfAngles(0, 90, 0), resRot)
    }

    @Test
    fun `Test fromPivotToOrigin with z=1 and rot=180degrees in y`() {

        val pos = vec3Of(0, 0, 1)
        val rot = quatOfAngles(0, 90, 0)

        val (resPos, resRot) = (pos to rot).fromPivotToOrigin()

        assertEquals(vec3Of(1, 0, 1), resPos)
        Assert.assertEquals(quatOfAngles(0, 90, 0), resRot)
    }

    @Test
    fun `Test scale from pivot`() {
        val trans1 = TRSTransformation(translation = vec3Of(-3, 0, 0))
        val trans2 = TRSTransformation(scale = vec3Of(2, 1, 1))
        val trans3 = TRSTransformation(translation = vec3Of(3, 0, 0))

        val trs = TRSTransformation.fromScalePivot(vec3Of(3, 0, 0), vec3Of(2, 1, 1))

        Assert.assertEquals(trs, (trans1 + trans2 + trans3))
        Assert.assertEquals(trs.matrix, (trans1 + trans2 + trans3).matrix)

        assertEquals(vec3Of(-5, 0, 0), trs.matrix.transformVertex(vec3Of(-1, 0, 0)))
        assertEquals(vec3Of(-3, 0, 0), trs.matrix.transformVertex(vec3Of(0, 0, 0)))
        assertEquals(vec3Of(-1, 0, 0), trs.matrix.transformVertex(vec3Of(1, 0, 0)))
        assertEquals(vec3Of(1, 0, 0), trs.matrix.transformVertex(vec3Of(2, 0, 0)))
        assertEquals(vec3Of(3, 0, 0), trs.matrix.transformVertex(vec3Of(3, 0, 0)))
    }

    @Test
    fun `Test scale + rotation + translate`() {
        val trans1 = TRSTransformation(translation = vec3Of(1, 0, 0))
        val trans2 = TRSTransformation(rotation = quatOfAxisAngled(90, 0, 1, 0))
        val trans3 = TRSTransformation(scale = vec3Of(1, 1, 2))

        val trs = TRSTransformation(
                translation = vec3Of(1, 0, 0),
                rotation = quatOfAxisAngled(90, 0, 1, 0),
                scale = vec3Of(1, 1, 2)
        )

        Assert.assertEquals(trs, (trans1 * trans2 * trans3))
        Assert.assertEquals(trs.matrix, (trans1 * trans2 * trans3).matrix)
    }

    @Test
    fun `Test Transformation merge is left associative`() {
        val trans1 = TRSTransformation(translation = vec3Of(1, 0, 0))
        val trans2 = TRSTransformation(rotation = quatOfAxisAngled(90, 0, 1, 0))
        val trans3 = TRSTransformation(scale = vec3Of(1, 1, 2))

        Assert.assertNotEquals((trans1 * trans2 * trans3), (trans3 * trans2 * trans1))
        Assert.assertNotEquals((trans3.matrix * trans2.matrix * trans1.matrix), (trans1 * trans2 * trans3).matrix)
        Assert.assertEquals((trans1.matrix * trans2.matrix * trans3.matrix), (trans1 * trans2 * trans3).matrix)
    }

    @Test
    fun `Test Transformation merge is conservative for translation`() {
        val trans1 = TRSTransformation(translation = vec3Of(1, 0, 0))
        val trans2 = TRSTransformation(translation = vec3Of(0, 1, 0))
        val trans3 = TRSTransformation(translation = vec3Of(1, 1, 0))

        Assert.assertEquals(trans3, trans1 + trans2)
        Assert.assertEquals(trans3.matrix, (trans1 + trans2).matrix)
        Assert.assertEquals(trans3.matrix, trans1.matrix * trans2.matrix)
    }

    @Test
    fun `Test Transformation merge is conservative for rotation`() {
        val trans1 = TRSTransformation(rotation = quatOfAxisAngled(180, 1, 0, 0))
        val trans2 = TRSTransformation(rotation = quatOfAxisAngled(180, 0, 1, 0))
        val trans3 = TRSTransformation(rotation = quatOfAxisAngled(180, 0, 0, 1))

        assertEquals(trans3.matrix, (trans1 * trans2).matrix)
        assertEquals(trans3.matrix, trans1.matrix * trans2.matrix)
        assertEquals(trans3.rotation, (trans1 * trans2).toTRS().rotation)
    }

    @Test
    fun `Test Transformation merge is conservative for scale`() {
        val trans1 = TRSTransformation(scale = vec3Of(2, 1, 1))
        val trans2 = TRSTransformation(scale = vec3Of(1, 2, 1))
        val trans3 = TRSTransformation(scale = vec3Of(2, 2, 1))

        Assert.assertEquals(trans3, trans1 + trans2)
        Assert.assertEquals(trans3.matrix, (trans1 + trans2).matrix)
        Assert.assertEquals(trans3.matrix, trans1.matrix * trans2.matrix)
    }

    fun assertEquals(vec1: TRSTransformation, vec2: TRSTransformation) {
        assertEquals(vec1.translation, vec2.translation)
        assertEquals(vec1.rotation, vec2.rotation)
        assertEquals(vec1.scale, vec2.scale)
    }

    fun assertEquals(vec1: IVector3, vec2: IVector3) {
        val msg = "expected:<$vec1>, but was:<$vec2>"
        val epsilon = 1.0000000116860974E-7
        Assert.assertEquals(msg, vec1.xd, vec2.xd, epsilon)
        Assert.assertEquals(msg, vec1.yd, vec2.yd, epsilon)
        Assert.assertEquals(msg, vec1.zd, vec2.zd, epsilon)
    }

    fun assertEquals(vec1: IQuaternion, vec2: IQuaternion) {
        val msg = "$vec1, but was $vec2"
        val epsilon = 1.0000000116860974E-7
        Assert.assertEquals(msg, vec1.xd, vec2.xd, epsilon)
        Assert.assertEquals(msg, vec1.yd, vec2.yd, epsilon)
        Assert.assertEquals(msg, vec1.zd, vec2.zd, epsilon)
        Assert.assertEquals(msg, vec1.wd, vec2.wd, epsilon)
    }

    fun assertEquals(vec1: IMatrix4, vec2: IMatrix4) {
        val msg = "expected:<$vec1>, but was:<$vec2>"
        val epsilon = 1.0000000116860974E-7
        Assert.assertEquals(msg, vec1.m00d, vec2.m00d, epsilon)
        Assert.assertEquals(msg, vec1.m01d, vec2.m01d, epsilon)
        Assert.assertEquals(msg, vec1.m02d, vec2.m02d, epsilon)
        Assert.assertEquals(msg, vec1.m03d, vec2.m03d, epsilon)
        Assert.assertEquals(msg, vec1.m10d, vec2.m10d, epsilon)
        Assert.assertEquals(msg, vec1.m11d, vec2.m11d, epsilon)
        Assert.assertEquals(msg, vec1.m12d, vec2.m12d, epsilon)
        Assert.assertEquals(msg, vec1.m13d, vec2.m13d, epsilon)
        Assert.assertEquals(msg, vec1.m20d, vec2.m20d, epsilon)
        Assert.assertEquals(msg, vec1.m21d, vec2.m21d, epsilon)
        Assert.assertEquals(msg, vec1.m22d, vec2.m22d, epsilon)
        Assert.assertEquals(msg, vec1.m23d, vec2.m23d, epsilon)
        Assert.assertEquals(msg, vec1.m30d, vec2.m30d, epsilon)
        Assert.assertEquals(msg, vec1.m31d, vec2.m31d, epsilon)
        Assert.assertEquals(msg, vec1.m32d, vec2.m32d, epsilon)
        Assert.assertEquals(msg, vec1.m33d, vec2.m33d, epsilon)
    }
}