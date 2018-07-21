package com.cout970.modeler.api.model.mesh

import com.cout970.matrix.api.IMatrix4
import com.cout970.modeler.api.model.ITransformation
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/05/07.
 */
interface IMesh {

    val pos: List<IVector3>
    val tex: List<IVector2>
    val faces: List<IFaceIndex>

    fun transformPos(selection: List<Int>, func: (Int, IVector3) -> IVector3): IMesh
    fun transformTex(selection: List<Int>, func: (Int, IVector2) -> IVector2): IMesh

    fun transform(trans: ITransformation): IMesh
    fun transform(matrix: IMatrix4): IMesh
    fun transformTexture(trans: ITransformation): IMesh
    fun merge(other: IMesh): IMesh
    fun optimize(): IMesh
}