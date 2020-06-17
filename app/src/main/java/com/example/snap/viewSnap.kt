package com.example.snap

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.net.HttpURLConnection
import java.net.URL

class viewSnap : AppCompatActivity() {
    val auth=FirebaseAuth.getInstance()
    val message: TextView =findViewById(R.id.viewMessage)
    val viewSnap: ImageView =findViewById(R.id.viewSnap)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_snap)
        message.setText(intent.getStringExtra("message"))
        val snapBitmap: Bitmap
        try{
            snapBitmap= ImageDownloader().execute(intent.getStringExtra("imageURL")).get()!!
            viewSnap.setImageBitmap(snapBitmap)
        }catch (e:Exception)
        {
            e.printStackTrace()
        }
    }
    inner class ImageDownloader : AsyncTask<String, Void, Bitmap?>() {
        override fun doInBackground(vararg params: String?): Bitmap? {
            try{
                val url= URL(params[0])
                val connection=url.openConnection() as HttpURLConnection
                connection.connect()
                val input=connection.inputStream
                return BitmapFactory.decodeStream(input)

            }catch (e:Exception)
            {
                e.printStackTrace()
                return null
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        FirebaseDatabase.getInstance().getReference().child("users").child(auth.currentUser!!.uid).child("snaps").child(intent.getStringExtra("snapKey")).removeValue()
        FirebaseStorage.getInstance().getReference().child("images").child(intent.getStringExtra("imageName")).delete()
    }
}