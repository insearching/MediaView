package com.media.mediaview

import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


class PreviewPresenter : BasePresenter<PreviewPresenter.View> {

    private lateinit var view: View
    private var disposable = Disposables.disposed()
    private var intervalSeconds: Long = 5

    override fun bind(view: View) {
        this.view = view
        resetTimer()
    }

    fun setAutoPlayTimeInterval(intervalSeconds: Long){
        this.intervalSeconds= intervalSeconds
    }

    override fun unbind() {
        if(!disposable.isDisposed){
            disposable.dispose()
        }
    }

    fun resetTimer(){
        if(!disposable.isDisposed){
            disposable.dispose()
        }
        disposable = Flowable.interval(intervalSeconds, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    view.next()
                }
    }


    interface View {
        fun next()
    }
}
