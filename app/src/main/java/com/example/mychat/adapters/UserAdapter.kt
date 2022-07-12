package com.example.mychat.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mychat.R
import com.example.mychat.activities.ChatActivity
import com.example.mychat.databinding.ItemUsersBinding
import com.example.mychat.models.User

class UserAdapter(private val context: Context, private val usersList: ArrayList<User>): RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(val binding: ItemUsersBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            return UserViewHolder(ItemUsersBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ))
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currUser = usersList[position]
        holder.binding.userName.text = currUser.userName
        Glide.with(context).load(currUser.profileImage).placeholder(R.drawable.profile_img).into(holder.binding.userImage)

        holder.binding.layoutUser.setOnClickListener {
            val intent = Intent(context,ChatActivity::class.java)
            intent.putExtra("userId",currUser.userId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return usersList.size
    }
}