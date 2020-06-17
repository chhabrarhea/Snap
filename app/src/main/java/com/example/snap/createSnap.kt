package com.example.snap

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.ByteArrayOutputStream
import java.util.*

class createSnap : AppCompatActivity() {
    var messageText:EditText=findViewById(R.id.message)
    var bitmap:Bitmap?=null
     var storage = Firebase.storage
    var imageName=UUID.randomUUID().toString()+".jpg"
    var picture=findViewById<ImageView>(R.id.imageView)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = MenuInflater(this)
        inflater.inflate(R.menu.create_snap_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    fun getCamera()
    {
        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }
    fun getGallery()
    {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, 1888)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if ((requestCode == 1 || requestCode == 1888) && resultCode == Activity.RESULT_OK && data != null) {

            try {
                if (requestCode == 1) {
                    bitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, data.data)
                } else {
                    bitmap = Objects.requireNonNull(data.extras)["data"] as Bitmap
                }
                picture.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    else Toast.makeText(this@createSnap,"Image not selected",Toast.LENGTH_SHORT).show()}

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId)
        {
            R.id.camera ->
            {
                if(ContextCompat.checkSelfPermission(applicationContext,android.Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED)
                {
                    getCamera()
                }
                Dexter.withContext(this)
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(object : PermissionListener {
                        override fun onPermissionGranted(response: PermissionGrantedResponse) {
                            getCamera()
                        }

                        override fun onPermissionDenied(response: PermissionDeniedResponse) {
                            // check for permanent denial of permission
                            if (response.isPermanentlyDenied) {
                                val ad =
                                    AlertDialog.Builder(this@createSnap)
                                ad.setTitle("Permission Denied")
                                    .setMessage("Enable External Storage Access in settings")
                                    .setIcon(R.drawable.ic_launcher_background)
                                    .setNegativeButton("Cancel", null)
                                    .setPositiveButton(
                                        "Go to settings"
                                    ) { dialog, which ->
                                        val intent = Intent(
                                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.parse("package:$packageName")
                                        )
                                        intent.addCategory(Intent.CATEGORY_DEFAULT)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)
                                    }.show()
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            permission: PermissionRequest,
                            token: PermissionToken
                        ) {
                            token.continuePermissionRequest()
                        }
                    }).check()
            }
            R.id.gallery->
            {
                if (ContextCompat.checkSelfPermission(
                        this@createSnap,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    getGallery()
                } else Dexter.withContext(this)
                    .withPermission(Manifest.permission.CAMERA)
                    .withListener(object : PermissionListener {
                        override fun onPermissionGranted(response: PermissionGrantedResponse) {
                            getGallery()
                        }

                        override fun onPermissionDenied(response: PermissionDeniedResponse) {
                            if (response.isPermanentlyDenied) {
                                val intent = Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse("package:$packageName")
                                )
                                intent.addCategory(Intent.CATEGORY_DEFAULT)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                // navigate user to app settings
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            permission: PermissionRequest,
                            token: PermissionToken
                        ) {
                            token.continuePermissionRequest()
                        }
                    }).check()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap)
      val button=findViewById<Button>(R.id.next)
        button.setOnClickListener(View.OnClickListener {
            if(bitmap!=null) {
                picture.setDrawingCacheEnabled(true)
                picture.buildDrawingCache()
                val bitmap = (picture.getDrawable() as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                val uploadTask: UploadTask =  FirebaseStorage.getInstance().getReference().child("images").child(imageName).putBytes(data)
                uploadTask.addOnFailureListener( OnFailureListener() {
                    fun onFailure(@NonNull exception: java.lang.Exception?) {
                        Toast.makeText(this,"Image upload failed.Try again!",Toast.LENGTH_LONG).show()
                    }


                }).addOnSuccessListener(
                    OnSuccessListener<UploadTask.TaskSnapshot?>() {
                    fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot?) {
                        val urlTask = uploadTask.continueWithTask { task ->
                            if (!task.isSuccessful) {
                                task.exception?.let {
                                    throw it
                                }
                            }
                            FirebaseStorage.getInstance().getReference().child("images").child(imageName) .downloadUrl
                        }.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val downloadUri = task.result
                                var intent:Intent= Intent(this@createSnap,SendSnap::class.java)
                                intent.putExtra("url",downloadUri!!.toString())
                                intent.putExtra("name",imageName!!.toString())
                                var message=messageText.getText()
                                intent.putExtra("message",message)
                                startActivity(intent)

                            } else {
                                 Toast.makeText(this,"Image upload failed.Try again!",Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                })
            }
        })

    }
}
