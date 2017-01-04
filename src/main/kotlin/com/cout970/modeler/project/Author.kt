package com.cout970.modeler.project

import com.google.gson.annotations.Expose

/**
 * Created by cout970 on 2017/01/04.
 */
class Author(@Expose var name: String, @Expose var email: String) {

    @Expose var web: String? = null
}