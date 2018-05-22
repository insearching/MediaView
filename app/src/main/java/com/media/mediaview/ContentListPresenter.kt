package com.media.mediaview

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*


class ContentListPresenter : BasePresenter<ContentListPresenter.View> {

    private var mediaList = emptyList<Media>()
    private lateinit var view: View

    override fun bind(view: View) {
        this.view = view
    }

    override fun unbind() {
    }

    fun requestMedia(contentResolver: ContentResolver) {
        val uri: Uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val list = mutableListOf<Media>()
        val projection = arrayOf(MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.DATE_TAKEN)
        val cursor = contentResolver.query(uri, projection, null, null,
                MediaStore.Video.Media.DATE_TAKEN + " DESC")
        val dateFormat = SimpleDateFormat("d MMM yyyy", Locale.getDefault())

        cursor.use { localCursor ->
            localCursor.moveToFirst()
            do {
                val titleIndex = localCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN)
                val dataIndex = localCursor.getColumnIndexOrThrow(MediaColumns.DATA)
                list.add(Media(dateFormat.format(Date(localCursor.getLong(titleIndex))),
                        localCursor.getString(dataIndex), MediaType.IMAGE))
            } while (localCursor.moveToNext())
        }

        if (list.isNotEmpty()) {
            mediaList = list.toList()
            view.updateList()
        }
    }

    fun getMediaCount() = mediaList.size

    private fun openMedia(position: Int) {
        view.openMedia(mediaList, position)
    }

    fun onBindRowViewAtPosition(position: Int, holder: ContentListFragment.MediaRow) {
        holder.setTitle(mediaList[position].title)
        holder.setLogo(mediaList[position].path)
        holder.setOnItemClickListener(android.view.View.OnClickListener {
            openMedia(position)
        })
    }

    enum class MediaType {
        IMAGE,
        VIDEO
    }

    @SuppressLint("ParcelCreator")
    @Parcelize
    data class Media(val title: String, val path: String, val type: MediaType) :Parcelable

    interface View {
        fun openMedia(mediaList: List<Media>, position: Int)

        fun updateList()
    }
}
