/*
 * Created by Muhammad Utsman on 28/11/20 3:54 PM
 * Copyright (c) 2020 . All rights reserved.
 */

package com.utsman.listing.domain

import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import com.utsman.data.model.dto.list.AppsSealedView
import com.utsman.data.model.dto.worker.FileDownload
import com.utsman.data.repository.download.DownloadRepository
import com.utsman.data.repository.list.InstalledAppsRepository
import com.utsman.data.source.InstalledPagingSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class InstalledAppUseCase @Inject constructor(
    private val installedAppsRepository: InstalledAppsRepository,
    private val downloadRepository: DownloadRepository
) {

    val pagingData = MutableLiveData<PagingData<AppsSealedView.AppsView>>()

    suspend fun getUpdatedApp() {
        Pager(PagingConfig(pageSize = 4)) {
            InstalledPagingSource(installedAppsRepository)
        }.flow
            .cachedIn(GlobalScope)
            .collect {
                val appView = it.map { ap ->
                    installedAppsRepository.checkInstalledApps(ap)
                }
                pagingData.postValue(appView)
            }
    }

    suspend fun requestDownload(file: FileDownload) {
        downloadRepository.requestDownload(file)
    }

}