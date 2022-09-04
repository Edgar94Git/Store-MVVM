package com.example.stores.mainModule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.stores.R
import com.example.stores.common.entities.StoreEntity
import com.example.stores.databinding.ItemStoreBinding

class StoreListAdapter(private var listener: OnClickListener) :
   ListAdapter<StoreEntity, RecyclerView.ViewHolder>(StoreDiffCallback()) {

   private lateinit var context: Context

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      context = parent.context

      val view = LayoutInflater.from(context).inflate(R.layout.item_store, parent, false)

      return ViewHolder(view)
   }

   override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
      val store = getItem(position)

      with(holder as ViewHolder){
         setListener(store)
         binding.tvNombre.text = store.name
         binding.cbFavorite.isChecked = store.isFavorite
         Glide.with(context)
            .load(store.photoUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(binding.imgPhoto)
      }
   }

   inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
      val binding = ItemStoreBinding.bind(view)

      fun setListener(storeEntity: StoreEntity){
         with(binding.root){
            setOnClickListener {
               listener.onClick(storeEntity)
            }
            setOnLongClickListener {
               listener.onDeleteStore(storeEntity)
               true
            }
         }

         binding.cbFavorite.setOnClickListener {
            listener.onFavoriteStore(storeEntity)
         }
      }
   }

   class StoreDiffCallback: DiffUtil.ItemCallback<StoreEntity>(){
      override fun areItemsTheSame(oldItem: StoreEntity, newItem: StoreEntity): Boolean {
         return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: StoreEntity, newItem: StoreEntity): Boolean {
         return oldItem == newItem
      }
   }
}