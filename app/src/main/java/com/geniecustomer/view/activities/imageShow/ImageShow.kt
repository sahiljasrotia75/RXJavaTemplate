package com.geniecustomer.view.activities.imageShow

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.geniecustomer.R
import kotlinx.android.synthetic.main.image_view.*

class ImageShow : AppCompatActivity() {

    private lateinit var imageUrl : ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_view)
        getIntentData()
        crossClick()
    }


    /** Get Intent Data */
    private fun getIntentData() {
        try {
            if (intent.hasExtra("imageUrl")){
                imageUrl = intent.getStringArrayListExtra("imageUrl")!!
                val position = intent.getIntExtra("position", 0)
                setImage(position)
            }else{
                Toast.makeText(this,"Image Link not find.",Toast.LENGTH_LONG).show()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    /** Set Image */
    private fun setImage(position: Int) {
        try {
            ivImageView.adapter = ImageShowAdapter(this,imageUrl)
            ivImageView.currentItem = position.toInt()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    /** Cross Click Handle */
    private fun crossClick(){
        try {
            ivCrossImage.setOnClickListener {
                super.onBackPressed()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

}