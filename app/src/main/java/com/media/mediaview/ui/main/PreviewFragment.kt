package com.media.mediaview.ui.main

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.media.mediaview.R
import com.squareup.picasso.Picasso

class PreviewFragment : Fragment() {

    companion object {
        private const val PATH: String = "path"

        fun newInstance(path: String) =
                PreviewFragment().apply {
                    arguments = Bundle().apply {
                        putString(PATH, path)
                    }
                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View {
        return inflater.inflate(R.layout.preview_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle) {
        super.onViewCreated(view, savedInstanceState)
        val imageView = view.findViewById<ImageView>(R.id.image_holder)
        Picasso.get()
                .load(arguments.getString(PATH))
                .into(imageView)
    }
}