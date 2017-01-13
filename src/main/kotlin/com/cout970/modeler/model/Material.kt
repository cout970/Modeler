package com.cout970.modeler.model

import com.google.gson.annotations.Expose

sealed class Material(@Expose val name: String)

class TexturedMaterial(texture: String) : Material(texture)

object MaterialNone : Material("noTexture")