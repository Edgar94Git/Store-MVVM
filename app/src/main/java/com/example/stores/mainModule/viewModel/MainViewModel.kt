package com.example.stores.mainModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import com.example.stores.mainModule.model.MainInteractor
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainViewModel: ViewModel() {
    private var interactor: MainInteractor
    private var storeList: MutableList<StoreEntity>

    init {
        interactor = MainInteractor()
        storeList = mutableListOf()
    }

    private val stores: MutableLiveData<List<StoreEntity>> by lazy {
        MutableLiveData<List<StoreEntity>>().also {
            loadStores()
        }
    }

    fun getStores(): LiveData<List<StoreEntity>>{
        return stores
    }

    private fun loadStores(){
        interactor.getStores {
            stores.value = it
        }
    }

    fun deleteStore(storeEntity: StoreEntity){
        interactor.deleteStore(storeEntity, {
            val index = storeList.indexOf(storeEntity)
            if(index != -1){
                storeList.removeAt(index)
                stores.value = storeList
            }
        })
    }

    fun updateStore(storeEntity: StoreEntity){
        interactor.updateStore(storeEntity, {
            val index = storeList.indexOf(storeEntity)
            if(index != -1){
                storeList.set(index, storeEntity)
                stores.value = storeList
            }
        })
    }
}