package com.gelostech.dankmemes.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.gelostech.dankmemes.data.Result
import com.gelostech.dankmemes.data.datasource.FavesDataSource
import com.gelostech.dankmemes.data.datasource.MemesDataSource
import com.gelostech.dankmemes.data.models.Fave
import com.gelostech.dankmemes.data.models.Meme
import com.gelostech.dankmemes.data.models.Report
import com.gelostech.dankmemes.data.models.User
import com.gelostech.dankmemes.data.repositories.MemesRepository
import com.gelostech.dankmemes.data.responses.GenericResponse
import com.gelostech.dankmemes.data.wrappers.ItemViewModel
import com.gelostech.dankmemes.data.wrappers.ObservableMeme
import kotlinx.coroutines.launch

class MemesViewModel constructor(private val repository: MemesRepository): ViewModel() {
    private val _genericResponseLiveData = MutableLiveData<GenericResponse>()
    val genericResponseLiveData: MutableLiveData<GenericResponse>
        get() = _genericResponseLiveData

    /**
     * Function to post new Meme
     * @param imageUri - Uri of the selected meme
     * @param meme - Meme model
     */
    fun postMeme(imageUri: Uri, meme: Meme) {
        viewModelScope.launch {
            _genericResponseLiveData.value = GenericResponse.loading()

            repository.postMeme(imageUri, meme) {
                when(it) {
                    is Result.Success -> {
                        _genericResponseLiveData.postValue(GenericResponse.success(it.data))
                    }

                    is Result.Error -> {
                        _genericResponseLiveData.postValue(GenericResponse.error(it.error))
                    }
                }
            }
        }
    }

    /**
     * Function to fetch memes
     */
    fun fetchMemes(): LiveData<PagedList<ItemViewModel>> {
        val pagingConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .build()

        val memeFactory = MemesDataSource.Factory(repository, viewModelScope)
        return LivePagedListBuilder<String, ItemViewModel>(memeFactory, pagingConfig).build()
    }

    /**
     * Function to fetch memes
     */
    fun fetchFaves(): LiveData<PagedList<Fave>> {
        val pagingConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .build()

        val faveFactory = FavesDataSource.Factory(repository, viewModelScope)
        return LivePagedListBuilder<String, Fave>(faveFactory, pagingConfig).build()
    }

    /**
     * Function to fetch memes
     */
    fun fetchMemesByUser(user: User): LiveData<PagedList<ItemViewModel>> {
        val pagingConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .build()

        val memeFactory = MemesDataSource.Factory(repository, viewModelScope, user)
        return LivePagedListBuilder<String, ItemViewModel>(memeFactory, pagingConfig).build()
    }

    /**
     * Function to like meme
     * @param memeId - ID of the meme
     */
    fun likeMeme(memeId: String, userId: String) {
        viewModelScope.launch {
            _genericResponseLiveData.value = GenericResponse.loading()

            when (val result = repository.likeMeme(memeId, userId)) {
                is Result.Success -> {
                    _genericResponseLiveData.value = GenericResponse.success(result.data)
                }

                is Result.Error -> {
                    _genericResponseLiveData.value = GenericResponse.error(result.error)
                }
            }
        }
    }

    /**
     * Function to like meme
     * @param memeId - ID of the meme
     */
    fun faveMeme(memeId: String, userId: String) {
        viewModelScope.launch {
            _genericResponseLiveData.value = GenericResponse.loading()

            when (val result = repository.faveMeme(memeId, userId)) {
                is Result.Success -> {
                    _genericResponseLiveData.value = GenericResponse.success(result.data)
                }

                is Result.Error -> {
                    _genericResponseLiveData.value = GenericResponse.error(result.error)
                }
            }
        }
    }

    /**
     * Function to delete meme
     * @param memeId - ID of the meme to delete
     */
    fun deleteMeme(memeId: String) {
        viewModelScope.launch {
            _genericResponseLiveData.value = GenericResponse.loading()

            when (val result = repository.deleteMeme(memeId)) {
                is Result.Success -> {
                    _genericResponseLiveData.value = GenericResponse.success(result.data,
                            item = GenericResponse.ITEM_RESPONSE.DELETE_MEME,
                            id = memeId)
                }

                is Result.Error -> {
                    _genericResponseLiveData.value = GenericResponse.error(result.error)
                }
            }
        }
    }

    /**
     * Function to delete meme
     * @param report
     */
    fun reportMeme(report: Report) {
        viewModelScope.launch {
            _genericResponseLiveData.value = GenericResponse.loading()

            when (val result = repository.reportMeme(report)) {
                is Result.Success -> {
                    _genericResponseLiveData.value = GenericResponse.success(result.data,
                            item = GenericResponse.ITEM_RESPONSE.REPORT_MEME)
                }

                is Result.Error -> {
                    _genericResponseLiveData.value = GenericResponse.error(result.error)
                }
            }
        }
    }

    /**
     * Function to delete fave
     * @param faveId - ID of the fave
     * @param userId - ID of the logged in User
     */
    fun deleteFave(faveId: String, userId: String) {
        viewModelScope.launch {
            _genericResponseLiveData.value = GenericResponse.loading()

            when (val result = repository.deleteFave(faveId, userId)) {
                is Result.Success -> {
                    _genericResponseLiveData.value = GenericResponse.success(result.data,
                            item = GenericResponse.ITEM_RESPONSE.DELETE_FAVE,
                            id = faveId)
                }

                is Result.Error -> {
                    _genericResponseLiveData.value = GenericResponse.error(result.error)
                }
            }
        }
    }
}