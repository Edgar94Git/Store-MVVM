package com.example.stores.mainModule.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import com.example.stores.common.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainInteractor {

    /*fun getStores(callback: (MutableList<StoreEntity>) -> Unit){
        val url = Constants.STORES_URL + Constants.GET_ALL_PATH
        var storeList = mutableListOf<StoreEntity>()
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            val status = response.optInt(Constants.STATUS_PROPERTY, Constants.ERROR)
            if(status == Constants.SUCCESS){
                val jsonList = response.optJSONArray(Constants.STORES_PROPERTY)?.toString()
                if(jsonList != null){
                    val mutableListType = object : TypeToken<MutableList<StoreEntity>>(){}.type
                    storeList = Gson().fromJson(jsonList, mutableListType)
                }
            }
            callback(storeList)
        }, {
            it.printStackTrace()
            callback(storeList)
        })

        StoreApplication.storeAPI.addToRequestQueue(jsonObjectRequest)
    }*/

    /*fun getStoresRoom(callback: (MutableList<StoreEntity>) -> Unit){
        doAsync {
            val storeList = StoreApplication.dataBase.storeDao().getAllStores()
            uiThread {
                callback(storeList)
            }
        }
    }*/

    val stores: LiveData<MutableList<StoreEntity>> = liveData {
        delay(1_000) //Temporal para pruebas
        val storeLiveData = StoreApplication.dataBase.storeDao().getAllStores()
        emitSource(storeLiveData.map { stores ->
            stores.sortedBy { it.name }.toMutableList()
        })
    }

    fun deleteStore(storeEntity: StoreEntity, callback: (StoreEntity) -> Unit){
        doAsync {
            StoreApplication.dataBase.storeDao().deleteStore(storeEntity)
            uiThread {
                callback(storeEntity)
            }
        }
    }

    suspend fun updateStore(storeEntity: StoreEntity){
        delay(300)
        StoreApplication.dataBase.storeDao().updateStore(storeEntity)
    }
}