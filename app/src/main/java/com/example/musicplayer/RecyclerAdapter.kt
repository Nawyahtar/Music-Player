package com.example.musicplayer

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(var clickListener: onClickListerner) :androidx.recyclerview.widget.ListAdapter<Song, RecyclerAdapter.SongViewHolder>(
    object :DiffUtil.ItemCallback<Song>(){
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
           return oldItem.uri==newItem.uri
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
           return oldItem==newItem
        }

    }
){
    class SongViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val tvSongName=itemView.findViewById<TextView>(R.id.tvSongName)
        @SuppressLint("SetTextI18n")
        fun initialize(item: Song, action:onClickListerner){
            tvSongName.text="${item.title}  ${item.artist}"
            itemView.setOnClickListener{
               action.onItemClick(item,adapterPosition)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
       val itemView=LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        return SongViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
       holder.initialize(getItem(position),clickListener)
    }

    interface onClickListerner{
        fun onItemClick(item:Song,position: Int)
    }



}
