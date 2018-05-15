package com.media.mediaview.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns
import android.support.v4.app.ActivityCompat
import android.view.View
import com.media.mediaview.R
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.io.File


class MainViewModel : ViewModel() {

    val liveData = MutableLiveData<List<Media>>()
    private var mediaList = emptyList<Media>()

    companion object {
        private const val READ_STORAGE_PERMISSION_REQUEST_CODE = 1000
    }

    fun init(context: Context) {
        if (checkPermissionForReadExternalStorage(context)) {
            requestMedia(context.contentResolver)
        } else {
            requestPermissionForReadExternalStorage(context)
        }
    }

    private fun checkPermissionForReadExternalStorage(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val result = context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            return result == PackageManager.PERMISSION_GRANTED
        }
        return false
    }

    @Throws(Exception::class)
    private fun requestPermissionForReadExternalStorage(context: Context) {
        try {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_REQUEST_CODE)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    @SuppressLint("Recycle")
    private fun requestMedia(contentResolver: ContentResolver) {
        val uri: Uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val list = mutableListOf<Media>()
        val projection = arrayOf(MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.DISPLAY_NAME)
        val cursor = contentResolver.query(uri, projection, null, null, null)

        cursor.use { localCursor ->
            val titleIndex = localCursor.getColumnIndexOrThrow(MediaColumns.DISPLAY_NAME)
            val dataIndex = localCursor.getColumnIndexOrThrow(MediaColumns.DATA)
            while (localCursor.moveToNext()) {
                list.add(Media(localCursor.getString(titleIndex),
                        localCursor.getString(dataIndex), MediaType.IMAGE))
            }
        }

        if (list.isNotEmpty()) {
            mediaList = list.toList()
            liveData.value = mediaList
        }
    }

    fun getMediaCount() = mediaList.size

    private fun openMedia(position: Int) {

    }

    fun onBindRowViewAtPosition(position: Int, holder: MainFragment.MediaRow) {
        holder.setTitle(mediaList[position].title)
        Picasso.get()
                .load(File( mediaList[position].path))
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                        holder.setLogo(bitmap)
                    }

                    override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {}
                    override fun onPrepareLoad(placeHolderDrawable: Drawable) {}
                })
        holder.setOnItemClickListener(View.OnClickListener {
            val previewFragment = PreviewFragment.newInstance(mediaList[position].path)
        })
    }

    fun onRequestPermissionsResult(context: Context, requestCode: Int,
                                   grantResults: IntArray) {
        if (requestCode == READ_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestMedia(context.contentResolver)
            } else {
                requestPermissionForReadExternalStorage(context)
            }
        }
    }

    enum class MediaType {
        IMAGE,
        VIDEO
    }

    data class Media(val title: String, val path: String, val type: MediaType)
}
