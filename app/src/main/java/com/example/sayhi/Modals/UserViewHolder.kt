package com.example.sayhi.Modals

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sayhi.R
import com.squareup.picasso.Picasso

class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    lateinit var countTv: TextView
    lateinit var timeTv: TextView
    lateinit var titleTv: TextView
    lateinit var subTitleTv: TextView
    lateinit var userImgView: ImageView

    fun bind(
        user: User, onClick: (name: String, photo: String, id: String) -> Unit
    ) =
        with(itemView) {

            countTv = itemView.findViewById(R.id.countTv)
            timeTv = findViewById(R.id.timeTv)
            titleTv = findViewById(R.id.titleTv)
            subTitleTv = findViewById(R.id.subTitleTv)
            userImgView = findViewById(R.id.userImgView)

            countTv.isVisible = false
            timeTv.isVisible = false
            titleTv.text = user.name
            subTitleTv.text = user.status

//            Glide.with(context)
//                .load(user.thumbImage)
//                .placeholder(R.drawable.defaultavatar)
//                .into(userImgView)

            Picasso.get().load(user.thumbImage)
                .placeholder(R.drawable.defaultavatar)
                .error(R.drawable.defaultavatar)
                .into(userImgView)

            setOnClickListener {
                onClick.invoke(user.name, user.thumbImage, user.uid)
            }

        }
}