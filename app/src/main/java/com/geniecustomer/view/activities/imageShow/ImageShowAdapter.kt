package com.geniecustomer.view.activities.imageShow

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.geniecustomer.R
import com.geniecustomer.api.Urls
import com.geniecustomer.utils.GlideApp
import kotlinx.android.synthetic.main.image_show.view.*

class ImageShowAdapter (var context: Context , var imageList : ArrayList<String>) : PagerAdapter()  {
    var layoutInflater: LayoutInflater? = null

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater!!.inflate(R.layout.image_show, container, false)

        GlideApp.with(context).load(Urls.BASE_URL + imageList[position])
            .error(R.drawable.ic_placeholder)
            .into(view.imageView)
        container.addView(view)

        return view
    }


    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun getCount(): Int {
        return imageList.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as View
        container.removeView(view)
    }
}