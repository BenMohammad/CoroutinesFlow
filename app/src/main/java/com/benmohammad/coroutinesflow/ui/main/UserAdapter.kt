package com.benmohammad.coroutinesflow.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.benmohammad.coroutinesflow.R
import com.benmohammad.coroutinesflow.databinding.ItemRecyclerUserBinding
import com.benmohammad.coroutinesflow.ui.main.MainContract.UserItem

class UserAdapter:
ListAdapter<UserItem, UserAdapter.VH>(object: DiffUtil.ItemCallback<UserItem>(){

    override fun areItemsTheSame(oldItem: UserItem, newItem: UserItem): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: UserItem, newItem: UserItem): Boolean = oldItem == newItem
}) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemRecyclerUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )

        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(private val binding: ItemRecyclerUserBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: UserItem) {
            binding.run {
                nameTextView.text = item.fullName
                emailTextView.text = item.email
                avatarImage.load(item.avatar) {

                    crossfade(200)
                    placeholder(R.drawable.ic_baseline_person_24)
                    error(R.drawable.ic_baseline_person_24)
                    transformations(CircleCropTransformation())
                }
            }
        }
    }
}