package com.media.mediaview

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import com.tomerrosenfeld.customanalogclockview.CustomAnalogClock
import kotlinx.android.synthetic.main.preview_activity.*


class PreviewActivity : AppCompatActivity(), PreviewPresenter.View {

    companion object {
        const val LIST: String = "list"
        const val POSITION: String = "position"
    }

    private lateinit var list: List<ContentListPresenter.Media>
    private var position: Int = 0
    private var viewPresenter = PreviewPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.preview_activity)
        list = intent.extras.getParcelableArrayList(LIST)
        position = intent.extras.getInt(POSITION)

        val pager = findViewById<ViewPager>(R.id.pager)
        pager.setPageTransformer(true, CrossFadePageTransformer())
        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                viewPresenter.resetTimer()
            }
        })
        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager, list)
        pager.adapter = pagerAdapter
        pager.currentItem = intent.extras.getInt(POSITION)
        val customAnalogClock = findViewById<CustomAnalogClock>(R.id.analog_clock)
        customAnalogClock.init(this, R.drawable.default_face, R.drawable.default_hour_hand,
                R.drawable.default_minute_hand, 0, false, false)
        customAnalogClock.setAutoUpdate(true)
        customAnalogClock.setScale(0.5f)
    }

    override fun onStart() {
        super.onStart()
        viewPresenter.bind(this)
        viewPresenter.setAutoPlayTimeInterval(5)
    }

    override fun next() {
        pager.beginFakeDrag()
        pager.fakeDragBy(100F)
        pager.endFakeDrag()
//        pager.setCurrentItem(pager.currentItem + 1, true)
    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        private lateinit var list: List<ContentListPresenter.Media>

        constructor(fm: FragmentManager, list: List<ContentListPresenter.Media>) : this(fm) {
            this.list = list
        }

        override fun getItem(position: Int): Fragment {
            return PreviewFragment.newInstance(list[position].path)
        }

        override fun getCount() = list.size
    }

    inner class CrossFadePageTransformer : ViewPager.PageTransformer {

        override fun transformPage(page: View, position: Float) {
            val imageView = page.findViewById<ImageView>(R.id.image_holder)
            page.translationX = page.width * -position

            if ((position >= -1.0f || position <= 1.0f) && position != 0.0F) {
                imageView.alpha = 1.0f - Math.abs(position)
            }
        }
    }
}