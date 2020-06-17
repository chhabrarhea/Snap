package com.example.snap

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {
    interface LoginListener {
        fun InputLogin(email: CharSequence?,password:CharSequence?)
    }
    private lateinit var listener:LoginListener
    lateinit var email:EditText
    lateinit  var password:EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        email= view.findViewById(R.id.loginEmail)
       password= view.findViewById(R.id.loginPassword)
        var button=view.findViewById<Button>(R.id.loginButton)
        button.setOnClickListener(View.OnClickListener {
            listener.InputLogin(email.getText(),password.getText())
        })

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = if (context is LoginListener) {
            context as LoginListener
        } else {
            throw RuntimeException(
                "$context must implement FragmentAListener"
            )
        }

    }

    override fun onDetach() {
        super.onDetach()
        listener.equals(null)
        email.setText("")
        password.setText("")
    }


}
