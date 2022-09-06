package com.example.stores.mainModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stores.common.entities.StoreEntity
import com.example.stores.common.utils.Constants
import com.example.stores.common.utils.StoreException
import com.example.stores.common.utils.TypeError
import com.example.stores.mainModule.model.MainInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    private var interactor: MainInteractor = MainInteractor()

    private var showProgress: MutableLiveData<Boolean> = MutableLiveData()

    private val stores = interactor.stores
    private val typeError: MutableLiveData<TypeError> = MutableLiveData()

    fun getTypeError(): MutableLiveData<TypeError> = typeError

    fun getStores(): LiveData<MutableList<StoreEntity>>{
        return stores
    }

    fun isShowProgress(): MutableLiveData<Boolean>{
        return showProgress
    }

    fun deleteStore(storeEntity: StoreEntity){
        executeAction { interactor.deleteStore(storeEntity) }
    }

    fun updateStore(storeEntity: StoreEntity){
        storeEntity.isFavorite = ! storeEntity.isFavorite
        executeAction { interactor.updateStore(storeEntity) }
    }

    private fun executeAction(block: suspend () -> Unit):Job{
        return  viewModelScope.launch {
            showProgress.value = Constants.SHOW
            try {
                block()
            }catch (ex: StoreException){
                typeError.value = ex.typeError
            }finally {
                showProgress.value = Constants.HIDE
            }
        }
    }
}