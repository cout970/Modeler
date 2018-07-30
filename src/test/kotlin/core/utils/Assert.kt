package core.utils

import com.cout970.matrix.api.IMatrix4
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3
import org.junit.Assert

fun assertEquals(vec1: TRSTransformation, vec2: TRSTransformation) {
    assertEquals(vec1.translation, vec2.translation, "expected $vec1\nbut was $vec2")
    assertEquals(vec1.rotation, vec2.rotation, "expected $vec1\nbut was $vec2")
    assertEquals(vec1.scale, vec2.scale, "expected $vec1\nbut was $vec2")
}

fun assertEquals(vec1: IVector3, vec2: IVector3, msg: String? = null) {
    val msg1 = msg ?: "expected:<$vec1>, but was:<$vec2>"
    val epsilon = 1.0000000116860974E-7
    Assert.assertEquals(msg1, vec1.xd, vec2.xd, epsilon)
    Assert.assertEquals(msg1, vec1.yd, vec2.yd, epsilon)
    Assert.assertEquals(msg1, vec1.zd, vec2.zd, epsilon)
}

fun assertEquals(vec1: IQuaternion, vec2: IQuaternion, msg: String? = null) {
    val msg1 = msg ?: "expected:<$vec1>, but was:<$vec2>"
    val epsilon = 1.0000000116860974E-7
    Assert.assertEquals(msg1, vec1.xd, vec2.xd, epsilon)
    Assert.assertEquals(msg1, vec1.yd, vec2.yd, epsilon)
    Assert.assertEquals(msg1, vec1.zd, vec2.zd, epsilon)
    Assert.assertEquals(msg1, vec1.wd, vec2.wd, epsilon)
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