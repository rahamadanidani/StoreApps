/*
 * Created by Muhammad Utsman on 19/12/20 11:50 PM
 * Copyright (c) 2020 . All rights reserved.
 */

package com.utsman.data.model.dto.list

import com.utsman.abstraction.extensions.capital

data class Category(
    var name: String = "",
    var query: String = "",
    var image: String = "",
    var iconRes: Int? = null,
    var desc: String = ""
) {
    companion object {
        fun simple(category: Category.() -> Unit) = Category().apply(category)

        fun buildFrom(query: String, iconRes: Int? = null) : Category {
            val name = query.replace("-", " & ")
                .capital()

            return Category(
                name = name,
                query = query,
                iconRes = iconRes
            )
        }
    }
}