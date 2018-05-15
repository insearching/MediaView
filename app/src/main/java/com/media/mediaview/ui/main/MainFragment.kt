package com.media.mediaview.ui.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.media.mediaview.R
import kotlinx.android.synthetic.main.media_item.view.*

class MainFragment : Fragment() {

    private lateinit var mediaAdapter: MediaAdapter
    private lateinit var activityContext: Context

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityContext = context
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.init(context)
        viewModel.liveData.observe(this, Observer {
            mediaAdapter.notifyDataSetChanged()
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycler = view.findViewById<RecyclerView>(R.id.media_list)
        mediaAdapter = MediaAdapter(viewModel)
        recycler.adapter = mediaAdapter
        recycler.layoutManager = GridLayoutManager(activityContext, 2)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        viewModel.onRequestPermissionsResult(activityContext, requestCode, grantResults)
    }

    inner class MediaAdapter internal constructor(private val viewModel: MainViewModel)
        : RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
            return MediaViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.media_item, parent, false))
        }

        override fun getItemCount(): Int {
            return viewModel.getMediaCount()
        }

        override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
            viewModel.onBindRowViewAtPosition(position, holder)
        }

        inner class MediaViewHolder(view: View) : RecyclerView.ViewHolder(view), MediaRow {

            val title: TextView = view.title_tv
            val logo: ImageView = view.logo_iv

            override fun setTitle(text: String) {
                title.text = text
            }

            override fun setLogo(bitmap: Bitmap) {
                logo.setImageBitmap(bitmap)
            }

            override fun setOnItemClickListener(listener: View.OnClickListener) {
                itemView.setOnClickListener(listener)
            }
        }
    }

    interface MediaRow {
        fun setTitle(text: String)

        fun setLogo(bitmap: Bitmap)

        fun setOnItemClickListener(listener: View.OnClickListener)
    }
}
