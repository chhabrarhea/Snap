package com.example.snap

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),LoginFragment.LoginListener,SignupFragment.SignListener  {
     lateinit var e: CharSequence
     lateinit var p: CharSequence
    var mauth:FirebaseAuth=FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       var tabLayout: TabLayout =findViewById(R.id.tab)
        var vp:ViewPager=findViewById(R.id.viewPager)
        vp.setAdapter(DemoAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager)
        if (mauth.getCurrentUser() != null) {
            finish()
             move()
        }


    }

    class DemoAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(
            fm,
            BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        ) {
        var data = arrayOf("Login", "Sign Up")
        override fun getItem(position: Int): Fragment {
             if(position==0) {
                 var fragment:Fragment=LoginFragment()
                 return fragment
             }
            else  {
                 var fragment=SignupFragment()
            return fragment}

        }


        override fun getCount(): Int {
            return data.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return data[position]
        }
    }
    override fun InputLogin(email: CharSequence?, password: CharSequence?) {
        if (email != null && password!=null && !email.equals("") && !password.equals("")) {
            e=email
            p=password
            login(e,p)
        }
        else
            Toast.makeText(this,"Invalid Credentials",Toast.LENGTH_SHORT).show()
    }

    override fun InputSign(email: CharSequence?, password: CharSequence?) {
        if (email != null && password!=null && !email.equals("") && !password.equals("")) {
            e=email
            p=password
            signup(e,p)
        }

        else
            Toast.makeText(this,"Invalid Credentials",Toast.LENGTH_SHORT).show()
    }

fun login(email: CharSequence?,password: CharSequence?)
{
    mauth.signInWithEmailAndPassword(email.toString(), password.toString())
        .addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.i("login", "signInWithEmail:success")
                val user = mauth.currentUser
                move()

            } else {
                // If sign in fails, display a message to the user.
                Log.i("login", "signInWithEmail:failure", task.exception)
                Toast.makeText(baseContext, "Authentication failed.",
                    Toast.LENGTH_SHORT).show()

            }
}}
    fun signup(email: CharSequence?,password: CharSequence?)
    {
        mauth.createUserWithEmailAndPassword(email.toString(), password.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.i("signup", "createUserWithEmail:success")
                     FirebaseDatabase.getInstance().getReference().child("users").child(task.result?.user!!.uid).child("email").setValue(email)
                    move()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.i("signup", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()


                }
    }}
fun move()
{
    val intent:Intent=Intent(this,SnapList::class.java)
    startActivity(intent)
}}



