package com.example.stores.mainModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainViewModel: ViewModel() {
    private var stores: MutableLiveData<List<StoreEntity>>

    init {
        stores = MutableLiveData()
    }

    fun getStores(): LiveData<List<StoreEntity>>{
        return stores
    }

    private fun loadStores(){
        doAsync {
            val storeList = StoreApplication.dataBase.storeDao().getAllStores()
            uiThread {
                stores.value = storeList
            }
        }
    }
}