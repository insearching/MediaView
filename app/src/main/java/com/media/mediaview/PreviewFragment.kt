package com.media.mediaview

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.io.File

class PreviewFragment : Fragment(){

    companion object {
        private const val PATH: String = "path"

        fun newInstance(path: String) =
                PreviewFragment().apply {
                    arguments = Bundle().apply {
                        putString(PATH, path)
                    }
                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.preview_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageView = view.findViewById<ImageView>(R.id.image_holder)
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        val path = arguments?.getString(PATH)
        progressBar.visibility = View.VISIBLE
        Picasso.get()
                .load(File(path))
                .fit()
                .centerInside()
                .into(imageView, object : Callback {
                    override fun onSuccess() {
                        progressBar.visibility = View.GONE
                    }

                    override fun onError(e: Exception) {
                        Toast.makeText(activity, e.localizedMessage, Toast.LENGTH_LONG).show()
                        progressBar.visibility = View.GONE
                    }
                })
    }


}