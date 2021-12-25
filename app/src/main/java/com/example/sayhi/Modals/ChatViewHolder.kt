package com.example.sayhi.Modals

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sayhi.R
import com.example.sayhi.utils.formatAsListItem
import com.squareup.picasso.Picasso

class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    lateinit var countTv: TextView
    lateinit var timeTv: TextView
    lateinit var titleTv: TextView
    lateinit var subTitleTv: TextView
    lateinit var userImgView: ImageView

    fun bind(item: Inbox, onClick: (name: String, photo: String, id: String) -> Unit) =
        with(itemView) {

            countTv = itemView.findViewById<TextView>(R.id.countTv)
            timeTv = itemView.findViewById<TextView>(R.id.timeTv)
            titleTv = itemView.findViewById<TextView>(R.id.titleTv)
            subTitleTv = itemView.findViewById<TextView>(R.id.subTitleTv)
            userImgView = itemView.findViewById<ImageView>(R.id.userImgView)

            countTv.isVisible = item.count > 0
            countTv.text = item.count.toString()
            timeTv.text = item.time.formatAsListItem(context)

            titleTv.text = item.name
            subTitleTv.text = item.msg

//            Glide.with(context)
//                .load(item.image)
//                .placeholder(R.drawable.defaultavatar)
//                .into(userImgView)

            Picasso.get()
                .load(item.image)
                .placeholder(R.drawable.defaultavatar)
                .error(R.drawable.defaultavatar)
                .into(userImgView)

            setOnClickListener {
                onClick.invoke(item.name!!, item.image!!, item.from!!)
            }
        }
}