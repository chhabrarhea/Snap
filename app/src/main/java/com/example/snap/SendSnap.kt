package com.example.snap

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import java.util.ArrayList

class SendSnap : AppCompatActivity() {
    var lv:ListView=findViewById(R.id.lv)
    var emails=ArrayList<String>()
    var keys=ArrayList<String?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_snap)
        var ad=ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,emails)
        lv.adapter=ad
        FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(object :ChildEventListener
            {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    var email=p0.child("email").value.toString()
                    emails.add(email)
                    keys.add(p0.key)
                    ad.notifyDataSetChanged()
                }

                override fun onChildRemoved(p0: DataSnapshot) {}

            })
        lv.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                var map= mapOf<String,String>("from" to FirebaseAuth.getInstance().currentUser!!.email.toString(),"imageURL" to intent.getStringExtra("url"),"imageName" to intent.getStringExtra("name"),"message" to intent.getStringExtra("message"))
                 FirebaseDatabase.getInstance().getReference().child("users").child(keys.get(position)!!).child("snaps").push().setValue(map)
                val intent= Intent(this@SendSnap,SnapList::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        })
    }
}