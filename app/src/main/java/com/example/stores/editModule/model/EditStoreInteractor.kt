package com.example.stores.editModule.model

import androidx.lifecycle.LiveData
import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import com.example.stores.common.utils.StoreException
import com.example.stores.common.utils.TypeError
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

class EditStoreInteractor {

    fun getStoreById(id: Long): LiveData<StoreEntity>{
        return StoreApplication.dataBase.storeDao().getStoreById(id)
    }

    suspend fun saveStore(storeEntity: StoreEntity){
        val result = StoreApplication.dataBase.storeDao().addStore(storeEntity)
        if(result == 0L) throw StoreException(TypeError.INSERT)
    }

    suspend fun  updateStore(storeEntity: StoreEntity){
        val result = StoreApplication.dataBase.storeDao().updateStore(storeEntity)
        if(result == 0) throw StoreException(TypeError.UPDATE)
    }
}