package com.quest.countryinfo

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.listview_item.view.*
import model.DataPoint

class ListDataAdapter(val listItems : List<DataPoint>, val context: Context) : RecyclerView.Adapter<ListDataViewHolder>() {

    // Gets the number of items in the list
    override fun getItemCount(): Int {
        return listItems.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ListDataViewHolder {
        return ListDataViewHolder(LayoutInflater.from(context).inflate(R.layout.listview_item, parent, false))
    }

    // Binds each item in the ArrayList to a view
    override fun onBindViewHolder(holderListData: ListDataViewHolder?, position: Int) {
        val data = listItems[position]
        val heading = holderListData?.itemView?.heading
        val subHeading = holderListData?.itemView?.subHeading
        val img=holderListData?.itemView?.img
        heading?.text = data.title?.trim()
        subHeading?.text = data.description?.trim()
        Picasso.get()
            .load(data.imageHref)
            .into(img)
    }
}

class ListDataViewHolder (view: View) : RecyclerView.ViewHolder(view) {

}