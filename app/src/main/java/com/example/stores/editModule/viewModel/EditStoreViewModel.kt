package com.example.stores.editModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stores.common.entities.StoreEntity
import com.example.stores.common.utils.StoreException
import com.example.stores.common.utils.TypeError
import com.example.stores.editModule.model.EditStoreInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class EditStoreViewModel: ViewModel() {
    private var storeId: Long = 0
    private val showFab = MutableLiveData<Boolean>()
    private val result = MutableLiveData<Any>()

    private val interactor: EditStoreInteractor = EditStoreInteractor()

    private val typeError: MutableLiveData<TypeError> = MutableLiveData()

    fun getTypeError(): MutableLiveData<TypeError> = typeError

    fun setTypeError(typeError: TypeError){
        this.typeError.value = typeError
    }

    fun setStoreSelected(storeEntity: StoreEntity){
        storeId = storeEntity.id
        //storeSelected.value = storeEntity
    }

    fun getStoreSelected(): LiveData<StoreEntity>{
        return interactor.getStoreById(storeId)
    }

    fun setShowFab(isVisible: Boolean){
        showFab.value = isVisible
    }

    fun getShowFab(): LiveData<Boolean> {
        return  showFab
    }

    fun setResult(value: Any){
        result.value = value
    }

    fun getResult(): LiveData<Any>{
        return result
    }

    fun saveStore(storeEntity: StoreEntity){
        executeAction(storeEntity) { interactor.saveStore(storeEntity) }
    }

    fun updateStore(storeEntity: StoreEntity){
        executeAction(storeEntity) { interactor.updateStore(storeEntity) }
    }

    private fun executeAction(storeEntity: StoreEntity, block: suspend () -> Unit): Job {
        return  viewModelScope.launch {
            try {
                block()
                result.value = storeEntity
            }catch (ex: StoreException){
                typeError.value = ex.typeError
            }
        }
    }
}