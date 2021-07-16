package com.geniecustomer.view.register

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.facebook.FacebookSdk
import com.geniecustomer.R
import com.geniecustomer.base.BaseActivity
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : BaseActivity()
{
    lateinit var layouts: IntArray
    lateinit var adapter: MyViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        FacebookSdk.setAutoLogAppEventsEnabled(true)
        FacebookSdk.setAutoInitEnabled(true)
        FacebookSdk.fullyInitialize()

        initLayoutsArray()
        inits()
        initiateListeners()

        tvgetStart.setOnClickListener {
            navigate(LoginActivity::class.java)
            finish()
        }

    }

    private fun initiateListeners() {

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
            }
        })
    }

    private fun initLayoutsArray() {
        layouts = intArrayOf(
            R.layout.welcome_packers,
            R.layout.welcome_plumber,
            R.layout.welcome_electrician,
            R.layout.welcome_computerrepair
        )
    }

    fun inits() {
        adapter = MyViewPagerAdapter(this)
        viewPager_intro.adapter = MyViewPagerAdapter(this)
        tabLayout.setupWithViewPager(viewPager_intro, true)
    }

    class MyViewPagerAdapter(var context: Context) : PagerAdapter() {

        var layoutInflater: LayoutInflater? = null

        override fun instantiateItem(container: ViewGroup, position: Int): Any {

            layoutInflater = LayoutInflater.from(context)
            val view = layoutInflater!!.inflate((context as IntroActivity).layouts[position], container, false)
            try {
                container.addView(view)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return view
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj
        }

        override fun getCount(): Int {
            return (context as IntroActivity).layouts.size
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as View
            container.removeView(view)
        }
    }
}
