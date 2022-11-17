/*
 * Created by Muhammad Utsman on 28/11/20 3:54 PM
 * Copyright (c) 2020 . All rights reserved.
 */

package com.utsman.listing.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.viewbinding.library.activity.viewBinding
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.utsman.abstraction.base.PagingStateAdapter
import com.utsman.abstraction.extensions.*
import com.utsman.listing.R
import com.utsman.listing.databinding.LayoutRecyclerViewBinding
import com.utsman.listing.ui.adapter.PagingListAdapter
import com.utsman.listing.viewmodel.SearchPagingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchAppActivity : AppCompatActivity() {

    private val binding: LayoutRecyclerViewBinding by viewBinding()
    private val viewModel: SearchPagingViewModel by viewModels()
    private var searchView: SearchView? = null

    private val pagingListAdapter = PagingListAdapter(
        holderType = PagingListAdapter.HolderType.SEARCH,
        lifecycleOwner = this
    )

    private val pagingStateAdapter = PagingStateAdapter {
        pagingListAdapter.retry()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.restartState()

        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
        }

        val linearLayout = LinearLayoutManager(this)

        binding.rvList.run {
            layoutManager = linearLayout
            adapter = pagingListAdapter.withLoadStateFooter(pagingStateAdapter)
        }

        viewModel.pagingData.observe(this, Observer { pagingData ->
            GlobalScope.launch {
                pagingListAdapter.submitData(pagingData)
            }
        })

        pagingListAdapter.addLoadStateListener { combinedLoadStates ->
            val txtMessage = "Application not found"
            binding.layoutEmpty.initialEmptyState(
                combinedLoadStates,
                pagingListAdapter.itemCount,
                R.drawable.ic_fluent_emoji_meh_24_regular,
                txtMessage
            )

            binding.layoutProgress.initialLoadState(combinedLoadStates.refresh) {
                pagingListAdapter.retry()
            }

            binding.chipQuery.isVisible = pagingListAdapter.itemCount == 0
        }

        viewModel.queries.observe(this, Observer { queries ->
            binding.run {
                chipQuery.removeAllViews()
                queries.toMutableList().distinct().run {
                    take(10).map { q ->
                        val chip = Chip(root.context).apply {
                            this.text = q
                            this.isCloseIconVisible = true
                        }

                        chip.setOnCloseIconClickListener {
                            viewModel.removeQuery(q)
                        }
                        chip.setOnClickListener {
                            searchView?.setQuery(q, true)
                        }
                        chipQuery.addView(chip)
                    }
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        menu?.findItem(R.id.search_action)?.also { searchMenu ->
            searchMenu.expandActionView()
            searchMenu.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                    return false
                }

                override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                    onBackPressed()
                    return false
                }
            })

            searchView = searchMenu.actionView as SearchView
            searchView?.run {
                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        if (!query.isNullOrBlank()) {
                            hideKeyboard()
                            clearFocus()
                            supportActionBar?.title = query
                            viewModel.restartState()
                            lifecycleScope.launch {
                                delay(300)
                                viewModel.searchApps(query)
                            }
                        }
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (newText == "") {
                            viewModel.restartState()
                        }
                        return true
                    }
                })
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        super.onPause()
        binding.chipQuery.removeAllViews()
    }
}