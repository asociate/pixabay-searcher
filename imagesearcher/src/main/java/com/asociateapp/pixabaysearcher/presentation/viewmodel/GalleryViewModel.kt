package com.asociateapp.pixabaysearcher.presentation.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.asociateapp.pixabaysearcher.config.Configurator
import com.asociateapp.pixabaysearcher.data.ImagesRepository
import com.asociateapp.pixabaysearcher.data.models.Image
import com.asociateapp.pixabaysearcher.data.models.Response
import com.asociateapp.pixabaysearcher.domain.GalleryUseCase
import com.asociateapp.pixabaysearcher.presentation.models.Default
import com.asociateapp.pixabaysearcher.presentation.models.State
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

internal class GalleryViewModel(
    private val useCase: GalleryUseCase,
    private val galleryConfig: Configurator
) : ViewModel() {

    val images = MutableLiveData<State<List<Image>>>()

    init {
        images.value = Default(emptyList(), 0)
    }

    fun getActivityTitle() = galleryConfig.getActivityTitle()

    fun showUpButton() = galleryConfig.showUpButton()

    fun expectingUriResult() = galleryConfig.expectingUriResult()

    @SuppressLint("CheckResult")
    fun search() {
        useCase.getImages(galleryConfig)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onImagesReceived, this::onError)
    }

    private fun onImagesReceived(response: Response<List<Image>>) {
        updateCurrentData(response.hits)
    }

    private fun onError(error: Throwable) {
        // TODO handle error
        error.printStackTrace()
    }

    private fun updateCurrentData(newImages: List<Image>) {
        val loadedImages = currentData().toMutableList().also { it.addAll(newImages) }
        val newPage = currentPage() + 1
        this.images.value = Default(loadedImages, newPage)
    }

    private fun currentPage() = this.images.value?.page ?: 0

    private fun currentData() = this.images.value?.data ?: emptyList()
}