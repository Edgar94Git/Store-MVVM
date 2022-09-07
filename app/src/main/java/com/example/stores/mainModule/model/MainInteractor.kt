package com.example.stores.mainModule.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import com.example.stores.common.utils.StoreException
import com.example.stores.common.utils.TypeError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class MainInteractor {

    val stores: LiveData<MutableList<StoreEntity>> = liveData {
        delay(1_000) //Temporal para pruebas
        val storeLiveData = StoreApplication.dataBase.storeDao().getAllStores()
        emitSource(storeLiveData.map { stores ->
            stores.sortedBy { it.name }.toMutableList()
        })
    }

    suspend fun deleteStore(storeEntity: StoreEntity) = withContext(Dispatchers.IO){
        //storeEntity.id = - 1
        delay(1_000)
        val result = StoreApplication.dataBase.storeDao().deleteStore(storeEntity)
        if(result == 0) throw StoreException(TypeError.DELETE)
    }

    suspend fun updateStore(storeEntity: StoreEntity) = withContext(Dispatchers.IO){
        //storeEntity.id = - 1
        delay(300)
        val result = StoreApplication.dataBase.storeDao().updateStore(storeEntity)
        if(result == 0) throw StoreException(TypeError.UPDATE)
    }
}