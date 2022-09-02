package com.example.stores.editModule.model

import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class EditStoreInteractor {
    fun saveStore(storeEntity: StoreEntity, callback: (Long) -> Unit){
        doAsync {
            val newId =  StoreApplication.dataBase.storeDao().addStore(storeEntity)
            uiThread {
                callback(newId)
            }
        }
    }

    fun  updateStore(storeEntity: StoreEntity, callback: (StoreEntity) -> Unit){
        doAsync {
            StoreApplication.dataBase.storeDao().updateStore(storeEntity)
            uiThread {
                callback(storeEntity)
            }
        }
    }
}