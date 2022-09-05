package com.example.stores.editModule

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.stores.R
import com.example.stores.common.entities.StoreEntity
import com.example.stores.databinding.FragmentEditStoreBinding
import com.example.stores.editModule.viewModel.EditStoreViewModel
import com.example.stores.mainModule.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class EditStoreFragment : Fragment() {

    private  lateinit var mBinding: FragmentEditStoreBinding
    private var mActivity: MainActivity? = null
    private var mIsEditMode: Boolean = false
    private lateinit var mStoreEntity: StoreEntity
    private lateinit var mEditStoreViewModel: EditStoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mEditStoreViewModel = ViewModelProvider(requireActivity())[EditStoreViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentEditStoreBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setHasOptionsMenu(true)
        setupTextFields()
    }

    private fun setupViewModel() {
        mEditStoreViewModel.getStoreSelected().observe(viewLifecycleOwner) {
            mStoreEntity = it ?: StoreEntity()
            if (it != null) {
                mIsEditMode = true
                setUriStore(it)
            } else {
                mIsEditMode = false
            }

            setupActionBar()
        }

        mEditStoreViewModel.getResult().observe(viewLifecycleOwner) { result ->
            hideKeyboard()
            when (result) {
                is Long -> {
                    mStoreEntity.id = result
                    mEditStoreViewModel.setStoreSelected(mStoreEntity)
                    Toast.makeText(
                        mActivity,
                        R.string.edit_store_message_success,
                        Toast.LENGTH_SHORT
                    ).show()
                    mActivity?.onBackPressed()
                }
                is StoreEntity -> {
                    mEditStoreViewModel.setStoreSelected(mStoreEntity)
                    Snackbar.make(
                        mBinding.root,
                        R.string.edit_store_message_update_success,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupActionBar() {
        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title = if(mIsEditMode) getString(R.string.edit_store_title_edit)
                                             else getString(R.string.edit_store_title_add)
    }

    private fun setupTextFields() {
        with(mBinding)
        {
            etPhone.addTextChangedListener { validationFields(tilPhone) }
            etName.addTextChangedListener { validationFields(tilName) }
            etPhotoUrl.addTextChangedListener {
                validationFields(tilPhotoUrl)
                loadImage(it.toString().trim())
            }
        }
    }

    private fun loadImage(url: String){
        Glide.with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(mBinding.imgPhoto)
    }

    private fun setUriStore(storeEntity: StoreEntity) {
        with(mBinding){
            etName.text = storeEntity.name.editable()
            etPhone.text = storeEntity.phone.editable()//setText(storeEntity.phone)
            etWebSite.text = storeEntity.webSite.editable()
            etPhotoUrl.text = storeEntity.photoUrl.editable()
        }
    }

    private fun String.editable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                mActivity?.onBackPressed()
                true
            }
            R.id.action_save ->{
                if(validationFields(mBinding.tilPhotoUrl, mBinding.tilPhone, mBinding.tilName))
                {
                    with(mStoreEntity){
                        name = mBinding.etName.text.toString().trim()
                        phone = mBinding.etPhone.text.toString().trim()
                        webSite = mBinding.etWebSite.text.toString().trim()
                        photoUrl = mBinding.etPhotoUrl.text.toString().trim()
                    }

                    if(mIsEditMode)
                        mEditStoreViewModel.updateStore(mStoreEntity)
                    else
                        mEditStoreViewModel.saveStore(mStoreEntity)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun validationFields(vararg textFields: TextInputLayout): Boolean {
        var isValid = true

        for (textField in textFields)
        {
            if(textField.editText?.text.toString().trim().isEmpty())
            {
                textField.error = getString(R.string.helper_required)
                isValid = false
            }
            else textField.error = null
        }

        if(!isValid)
        {
            Snackbar.make(
                mBinding.root,
                getString(R.string.edit_store_message_valid),
                Snackbar.LENGTH_SHORT
            ).show()
        }

        return isValid
    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        mEditStoreViewModel.setShowFab(true)
        mEditStoreViewModel.setResult(Any())
        setHasOptionsMenu(false)
        super.onDestroy()
    }

    private fun hideKeyboard(){
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }
}