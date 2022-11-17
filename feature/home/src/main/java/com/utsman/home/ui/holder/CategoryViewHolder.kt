/*
 * Created by Muhammad Utsman on 28/11/20 3:54 PM
 * Copyright (c) 2020 . All rights reserved.
 */

package com.utsman.home.ui.holder

import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper
import com.utsman.abstraction.extensions.intentTo
import com.utsman.data.model.dto.list.CategorySealedView.CategoryView
import com.utsman.home.databinding.ItemAppsCategoryBinding
import com.utsman.home.ui.adapter.AppsAdapter

class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemAppsCategoryBinding.bind(view)
    fun bind(categoryView: CategoryView) = binding.run {
        val appsAdapter = AppsAdapter().apply {
            updateItems(categoryView.apps)
        }

        txtLabelCategory.text = categoryView.name
        txtLabelCategory.setOnClickListener {
            it.context.intentTo("com.utsman.listing.ui.activity.ListAppActivity") {
                putExtra("title", categoryView.name)
                putExtra("query", categoryView.query)
            }
        }
        rvApps.run {
            val snapHelper = GravitySnapHelper(Gravity.START)
            snapHelper.attachToRecyclerView(this)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = appsAdapter
        }
    }
}