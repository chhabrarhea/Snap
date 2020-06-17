package com.example.snap

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class SnapList : AppCompatActivity() {
    var list:ListView=findViewById(R.id.snapList)
    var sent=ArrayList<String>()
    var snap=ArrayList<DataSnapshot>()
    val auth=FirebaseAuth.getInstance()
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = MenuInflater(this)
        inflater.inflate(R.menu.snapmenu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.Create -> {
                val intent = Intent(this@SnapList, createSnap::class.java)
                startActivity(intent)

            }
            R.id.signOut -> {
                auth.signOut()
                val i = Intent(this@SnapList, MainActivity::class.java)
                startActivity(i)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snap_list)
        var ad=ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,sent)
        list.adapter=ad
        FirebaseDatabase.getInstance().getReference().child("users").child(auth.currentUser!!.uid).child("snaps").addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
               sent.add(p0.child("fom").value as String)
                ad.notifyDataSetChanged()
                snap.add(p0)
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                var index=0
                for(sn:DataSnapshot in snap)
                {
                    if(sn.key==p0.key)
                    {
                        sent.removeAt(index)
                        snap.removeAt(index)
                    }
                    index++
                }
            }

        })

        list.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val snapshot=snap.get(position)
                val intent=Intent(this@SnapList,viewSnap::class.java)
                intent.putExtra("imageName",snapshot.child("imageName").value as String)
                intent.putExtra("imageURL",snapshot.child("imageURL").value as String)
                intent.putExtra("message",snapshot.child("message").value as String)
                intent.putExtra("snapKey",snapshot.key)
                startActivity(intent)
            }

        })
    }
}
