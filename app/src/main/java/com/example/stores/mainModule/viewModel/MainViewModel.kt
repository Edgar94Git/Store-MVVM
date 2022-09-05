package com.example.stores.mainModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stores.common.entities.StoreEntity
import com.example.stores.common.utils.Constants
import com.example.stores.common.utils.TypeError
import com.example.stores.mainModule.model.MainInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel: ViewModel() {
    private var interactor: MainInteractor
    private var storeList: MutableList<StoreEntity>

    init {
        interactor = MainInteractor()
        storeList = mutableListOf()
    }

    private var showProgress: MutableLiveData<Boolean> = MutableLiveData()

    /*private val stores: MutableLiveData<MutableList<StoreEntity>> by lazy {
        MutableLiveData<MutableList<StoreEntity>>().also {
            loadStores()
        }
    }*/

    private val stores = interactor.stores
    private val typeError: MutableLiveData<TypeError> = MutableLiveData()

    fun getTypeError(): MutableLiveData<TypeError> = typeError

    fun getStores(): LiveData<MutableList<StoreEntity>>{
        return stores
    }

    fun isShowProgress(): MutableLiveData<Boolean>{
        return showProgress
    }

    /*private fun loadStores(){
        showProgress.value = Constants.SHOW
        interactor.getStores {
            showProgress.value = Constants.HIDE
            stores.value = it
            storeList = it
        }
    }*/

    fun deleteStore(storeEntity: StoreEntity){
        /*viewModelScope.launch {
            interactor.deleteStore(storeEntity)
        }*/
        executeAction { interactor.deleteStore(storeEntity) }
    }

    fun updateStore(storeEntity: StoreEntity){
        /*viewModelScope.launch {
            showProgress.value = Constants.SHOW
            try {
                storeEntity.isFavorite = ! storeEntity.isFavorite
                interactor.updateStore(storeEntity)
            }catch (ex: Exception){
                ex.printStackTrace()
            }finally {
                showProgress.value = Constants.HIDE
            }
        }*/

        storeEntity.isFavorite = ! storeEntity.isFavorite
        executeAction { interactor.updateStore(storeEntity) }
    }

    private fun executeAction(block: suspend () -> Unit):Job{
        return  viewModelScope.launch {
            showProgress.value = Constants.SHOW
            try {
                block()
            }catch (ex: Exception){
                ex.printStackTrace()
            }finally {
                showProgress.value = Constants.HIDE
            }
        }
    }
}