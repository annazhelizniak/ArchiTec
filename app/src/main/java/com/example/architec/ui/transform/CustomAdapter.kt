package com.example.architec.ui.transform

import com.example.architec.R
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.architec.DetailActivity
import com.example.architec.data.ArchitectureStyle


class CustomAdapter(data: MutableList<ArchitectureStyle>, context: Context) :
    ArrayAdapter<ArchitectureStyle>(context, R.layout.row_item, data), View.OnClickListener {
    private val dataSet: ArrayList<ArchitectureStyle>
    var mContext: Context

    // View lookup cache
    private class ViewHolder {
        var txtName: TextView? = null
        var txtTimePeriod: TextView? = null
        var txtOrigin: TextView? = null
        var info: ImageView? = null
    }

    override fun onClick(v: View) {
        val position = v.getTag() as Int
        val `object`: Any? = getItem(position)
        val dataModel: ArchitectureStyle? = `object` as ArchitectureStyle?
        val intent = Intent(mContext, DetailActivity::class.java)
        intent.putExtra("architecture_style_id", dataModel?.id)
        mContext.startActivity(intent)
    }

    private var lastPosition = -1

    init {
        dataSet = data as ArrayList<ArchitectureStyle>
        mContext = context
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: ViewHolder
        val rowView: View

        if (convertView == null) {
            rowView = LayoutInflater.from(context).inflate(R.layout.row_item, parent, false)
            viewHolder = ViewHolder()
            viewHolder.txtName = rowView.findViewById(R.id.name)
            viewHolder.txtTimePeriod = rowView.findViewById(R.id.time_period)
            viewHolder.txtOrigin = rowView.findViewById(R.id.origin)
            rowView.tag = viewHolder
        } else {
            rowView = convertView
            viewHolder = convertView.tag as ViewHolder
        }

        val dataModel = getItem(position)
        viewHolder.txtName?.text = dataModel?.name
        viewHolder.txtTimePeriod?.text = dataModel?.time_period
        viewHolder.txtOrigin?.text = dataModel?.origin

        // Set click listener on the entire row view
        rowView.setOnClickListener(this)
        rowView.tag = position

        return rowView
    }

}