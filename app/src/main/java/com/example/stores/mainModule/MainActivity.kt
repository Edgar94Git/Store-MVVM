package com.example.stores.mainModule

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.*
import com.example.stores.common.entities.StoreEntity
import com.example.stores.common.utils.TypeError
import com.example.stores.databinding.ActivityMainBinding
import com.example.stores.editModule.EditStoreFragment
import com.example.stores.editModule.viewModel.EditStoreViewModel
import com.example.stores.mainModule.adapter.OnClickListener
import com.example.stores.mainModule.adapter.StoreAdapter
import com.example.stores.mainModule.adapter.StoreListAdapter
import com.example.stores.mainModule.viewModel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mGridLayoutManager: GridLayoutManager
    private lateinit var mAdapter: StoreListAdapter

    //MVVM
    private lateinit var mMainViewModel: MainViewModel
    private lateinit var mEditStoreViewModel: EditStoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.fab.setOnClickListener { launchEditFragment() }
        setupViewModel()
        setupRecyclerView()
    }

    private fun setupViewModel() {
        mMainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        mMainViewModel.getStores().observe(this){ stores ->
            mBinding.progressbar.visibility = View.GONE
            mAdapter.submitList(stores)
        }

        mMainViewModel.isShowProgress().observe(this) { isShowProgress ->
            mBinding.progressbar.visibility = if (isShowProgress) View.VISIBLE else View.GONE
        }

        mMainViewModel.getTypeError().observe(this) { typeError ->
            val msgRes = when (typeError) {
                TypeError.GET -> "Error al obtener datos"
                TypeError.INSERT -> "Error al insertar"
                TypeError.UPDATE -> "Error al actualizar"
                TypeError.DELETE -> "Error al eliminar"
                else -> "Error no configurado"
            }
            Snackbar.make(mBinding.root, msgRes, Snackbar.LENGTH_SHORT).show()
        }

        mEditStoreViewModel = ViewModelProvider(this)[EditStoreViewModel::class.java]
        mEditStoreViewModel.getShowFab().observe(this) { isVisible ->
            if (isVisible) mBinding.fab.show() else mBinding.fab.hide()
        }
    }

    private fun setupRecyclerView() {
        mAdapter = StoreListAdapter(this)
        mGridLayoutManager = GridLayoutManager(this, resources.getInteger(R.integer.main_columns))
        //getStores()
        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mGridLayoutManager
            adapter = mAdapter
        }
    }

    override fun onClick(storeEntity: StoreEntity) {
        launchEditFragment(storeEntity)
    }

    override fun onFavoriteStore(storeEntity: StoreEntity) {
        mMainViewModel.updateStore(storeEntity)
    }

    override fun onDeleteStore(storeEntity: StoreEntity) {
        val items = resources.getStringArray(R.array.array_options_item)
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_options_title)
            .setItems(items) { _, i ->
                when (i) {
                    0 -> confirmDelete(storeEntity)
                    1 -> dial(storeEntity.phone)
                    2 -> goToWebSite(storeEntity.webSite)
                }
            }.show()
    }

    private fun dial(phone: String) {
        startIntent(Intent.ACTION_DIAL, "tel:$phone")
    }

    private fun goToWebSite(webSite: String){
        if(webSite.isEmpty())
            Toast.makeText(this, R.string.main_error_no_website, Toast.LENGTH_LONG).show()
        else
            startIntent(Intent.ACTION_VIEW, webSite)
    }

    private fun startIntent(mAction: String, uriParse: String){
        val intent = Intent().apply {
            action = mAction
            data = Uri.parse(uriParse)
        }

        if(intent.resolveActivity(packageManager) != null)
            startActivity(intent)
        else
            Toast.makeText(this, R.string.main_error_no_resolve, Toast.LENGTH_LONG).show()
    }

    private fun confirmDelete(storeEntity: StoreEntity){
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_delete_title)
            .setPositiveButton(R.string.dialog_delte_confirm) { _, _ ->
                mMainViewModel.deleteStore(storeEntity)
            }
            .setNegativeButton(R.string.dialog_delete_cancel, null)
            .show()
    }

    private fun launchEditFragment(storeEntity: StoreEntity = StoreEntity()){
        mEditStoreViewModel.setStoreSelected(storeEntity)
        val fragment = EditStoreFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransition = fragmentManager.beginTransaction()
        fragmentTransition.add(R.id.containerMain, fragment)
        fragmentTransition.addToBackStack(null)
        fragmentTransition.commit()
        mEditStoreViewModel.setShowFab(false)
    }
}