/*
 * Created by Muhammad Utsman on 28/11/20 5:00 PM
 * Copyright (c) 2020 . All rights reserved.
 */

package com.utsman.detail.domain

import android.content.Context
import androidx.work.Operation
import com.utsman.abstraction.interactor.ResultState
import com.utsman.abstraction.interactor.fetch
import com.utsman.abstraction.interactor.stateOf
import com.utsman.data.model.dto.detail.DetailView
import com.utsman.data.model.dto.detail.toDetailView
import com.utsman.data.model.dto.list.AppVersion
import com.utsman.data.model.dto.list.AppsSealedView
import com.utsman.data.model.dto.worker.FileDownload
import com.utsman.data.model.dto.worker.WorkInfoResult
import com.utsman.data.repository.download.DownloadRepository
import com.utsman.data.repository.list.InstalledAppsRepository
import com.utsman.data.repository.meta.MetaRepository
import com.utsman.data.utils.DownloadUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class DetailUseCase @Inject constructor(
    private val context: Context,
    private val metaRepository: MetaRepository,
    private val installedAppsRepository: InstalledAppsRepository,
    private val downloadRepository: DownloadRepository
) {

    val detailView = stateOf<DetailView>()
    val workerState = MutableStateFlow<Operation.State?>(null)
    val workInfoState = MutableStateFlow<WorkInfoResult>(WorkInfoResult.Stopped())

    suspend fun getDetail(packageName: String) {
        fetch {
            val response = metaRepository.getDetail(packageName)
            val detailView = response?.toDetailView() ?: DetailView()
            installedAppsRepository.checkInstalledApps(detailView)
        }.collect {
            detailView.value = it
        }
    }

    fun checkDownloadedApks(fileName: String): Boolean {
        return DownloadUtils.checkAppIsDownloaded(context, fileName)
    }

    suspend fun observerWorkInfoResult(packageName: String) {
        downloadRepository.observerWorkInfo(packageName).collect {
            workInfoState.value = it
        }
    }

    suspend fun requestDownload(file: FileDownload) {
        downloadRepository.requestDownload(file).collect {
            workerState.value = it
        }
    }

    suspend fun cancelDownload(downloadId: Long?) {
        downloadRepository.cancelDownload(downloadId)
    }

    suspend fun restartState() {
        detailView.value = ResultState.Idle()
        workerState.value = null
        workInfoState.value = WorkInfoResult.Stopped()
    }
}