package com.media.mediaview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.media.mediaview.PreviewActivity.Companion.LIST
import com.media.mediaview.PreviewActivity.Companion.POSITION
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.media_item.view.*
import java.io.File

class ContentListFragment : Fragment(), ContentListPresenter.View {

    private lateinit var mediaAdapter: MediaAdapter
    private lateinit var activityContext: Context
    private var viewPresenter = ContentListPresenter()

    companion object {
        fun newInstance() = ContentListFragment()
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.content_list_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        viewPresenter.bind(this)
        viewPresenter.requestMedia(activityContext.contentResolver)
    }

    override fun onStop() {
        super.onStop()
        viewPresenter.unbind()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycler = view.findViewById<RecyclerView>(R.id.media_list)
        mediaAdapter = MediaAdapter(viewPresenter)
        recycler.adapter = mediaAdapter
        recycler.layoutManager = GridLayoutManager(activityContext, 2)
    }

    override fun openMedia(mediaList: List<ContentListPresenter.Media>, position: Int) {
        startActivity(Intent(activityContext, PreviewActivity::class.java)
                .putParcelableArrayListExtra(LIST, mediaList as ArrayList<out Parcelable>)
                .putExtra(POSITION, position))
    }

    override fun updateList() {
        mediaAdapter.notifyDataSetChanged()
    }

    inner class MediaAdapter internal constructor(private val viewPresenter: ContentListPresenter)
        : RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
            return MediaViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.media_item, parent, false))
        }

        override fun getItemCount(): Int {
            return viewPresenter.getMediaCount()
        }

        override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
            viewPresenter.onBindRowViewAtPosition(position, holder)
        }

        inner class MediaViewHolder(view: View) : RecyclerView.ViewHolder(view), MediaRow {

            val title: TextView = view.title_tv
            val logo: ImageView = view.logo_iv

            override fun setTitle(text: String) {
                title.text = text
            }

            override fun setLogo(path: String) {
                Picasso.get()
                        .load(File(path))
                        .fit()
                        .centerCrop()
                        .into(logo)
            }

            override fun setOnItemClickListener(listener: View.OnClickListener) {
                itemView.setOnClickListener(listener)
            }
        }
    }

    interface MediaRow {
        fun setTitle(text: String)

        fun setLogo(path: String)

        fun setOnItemClickListener(listener: View.OnClickListener)
    }
}
