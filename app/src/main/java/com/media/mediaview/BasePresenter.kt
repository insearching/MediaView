package com.media.mediaview

interface BasePresenter<V> {

    fun bind(view: V)

    fun unbind()
}