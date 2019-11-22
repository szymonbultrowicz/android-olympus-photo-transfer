package org.szymonbultrowicz.olympusphototransfer.app.photolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_photo_item.view.*
import org.szymonbultrowicz.olympusphototransfer.R
import org.szymonbultrowicz.olympusphototransfer.app.photolist.PhotoListFragment.OnListFragmentInteractionListener
import org.szymonbultrowicz.olympusphototransfer.lib.client.FileInfo
import org.szymonbultrowicz.olympusphototransfer.lib.client.PhotoInfo

/**
 * [RecyclerView.Adapter] that can display a [FileInfo] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyItemRecyclerViewAdapter(
    private var mValues: List<PhotoInfo>,
    private val fragment: Fragment,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as PhotoInfo
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_photo_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mNameView.text = item.name
        holder.mDateTakenView.text = item.dateTaken.toString()
        Glide.with(fragment)
            .load(item.thumbnailUrl.toString())
            .into(holder.mThumbnailView)


        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    fun updateData(data: List<PhotoInfo>) {
        mValues = data
        notifyDataSetChanged()
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mNameView: TextView = mView.photo_name
        val mDateTakenView: TextView = mView.photo_date
        val mThumbnailView: ImageView = mView.thumbnail
    }
}
