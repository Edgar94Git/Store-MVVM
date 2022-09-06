package com.example.stores.editModule.model

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import com.example.stores.common.utils.StoreException
import com.example.stores.common.utils.TypeError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EditStoreInteractor {

    fun getStoreById(id: Long): LiveData<StoreEntity>{
        return StoreApplication.dataBase.storeDao().getStoreById(id)
    }

    suspend fun saveStore(storeEntity: StoreEntity) = withContext(Dispatchers.IO){
        try {
            StoreApplication.dataBase.storeDao().addStore(storeEntity)
        }catch (ex: SQLiteConstraintException){
            throw StoreException(TypeError.INSERT)
        }
    }

    suspend fun  updateStore(storeEntity: StoreEntity) = withContext(Dispatchers.IO){
        try {
            val result = StoreApplication.dataBase.storeDao().updateStore(storeEntity)
            if(result == 0) throw StoreException(TypeError.UPDATE)
        }catch (ex: SQLiteConstraintException){
            throw StoreException(TypeError.UPDATE)
        }
    }
}