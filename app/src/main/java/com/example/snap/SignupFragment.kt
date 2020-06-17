package com.example.snap

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

/**
 * A simple [Fragment] subclass.
 */
class SignupFragment : Fragment() {
    interface SignListener {
        fun InputSign(email: CharSequence?,password:CharSequence?)
    }
    private lateinit var listener:SignListener
    lateinit var email: EditText
    lateinit  var password: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
          email = view.findViewById(R.id.signEmail)
         password = view.findViewById(R.id.signPassword)
        var button=view.findViewById<Button>(R.id.signButton)
        button.setOnClickListener(View.OnClickListener {
            listener.InputSign(email.getText(),password.getText())
        })

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = if (context is SignupFragment.SignListener) {
            context as SignupFragment.SignListener
        } else {
            throw RuntimeException(
                "$context must implement FragmentAListener"
            )
        }

    }

    override fun onDetach() {
        super.onDetach()
        email.setText("")
        password.setText("")
        listener.equals(null)

    }


}
