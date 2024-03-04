package com.bcit.budgetapp.Views.AuthenticationFragments

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import com.bcit.budgetapp.R
import com.bcit.budgetapp.Views.FragmentNavigation
import com.bcit.budgetapp.Views.MainActivity
import com.bcit.budgetapp.databinding.FragmentBudgetBinding
import com.bcit.budgetapp.databinding.FragmentSignUpBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app

class SignUpFragment : Fragment()
{

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var firebaseAuth: FirebaseAuth
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
//        var view = inflater.inflate(R.layout.fragment_sign_up, container, false)

        username = binding.edittextSignUpFragmentUsername
//            view.findViewById(R.id.edittext_signUpFragment_username)
        password = binding.edittextSignUpFragmentPassword
//            view.findViewById(R.id.edittext_signUpFragment_password)
        confirmPassword = binding.edittextSignUpFragmentConfirmPassword
//            view.findViewById(R.id.edittext_signUpFragment_confirmPassword)

        firebaseAuth = Firebase.auth

//        view.findViewById<Button>(R.id.button_signUpFragment_login)
            binding.buttonSignUpFragmentLogin.setOnClickListener{
                var navRegister = activity as FragmentNavigation
                navRegister.navigateFrag(LoginFragment(), false)
            }

//        view.findViewById<Button>(R.id.button_signUpFragment_register)
            binding.buttonSignUpFragmentRegister.setOnClickListener {
            validateEmptyForm()
        }

        return binding.root
    }

    private fun validateEmptyForm(){
        val icon = AppCompatResources.getDrawable(requireContext(),
        R.drawable.ic_baseline_warning_24)

        icon?.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)
        when{
            TextUtils.isEmpty(username.text.toString().trim()) -> {
                username.setError("Please Enter Username", icon)
            }

            TextUtils.isEmpty(password.text.toString().trim()) -> {
                password.setError("Please Enter Password", icon)
            }

            TextUtils.isEmpty(confirmPassword.text.toString().trim()) -> {
                confirmPassword.setError("Please Confirm Password", icon)
            }

            username.text.toString().isNotEmpty() &&
                    password.text.toString().isNotEmpty() &&
                    confirmPassword.text.toString().isNotEmpty() -> {

                        if (username.text.toString()
                                .matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))){

                            if (password.text.toString().length >= 5){

                                if (password.text.toString() == confirmPassword.text.toString()){
                                    firebaseSignUp()
                                } else {
                                    confirmPassword.setError("Password does not match", icon)

                                }

                            } else {
                                password.setError("Password should be bigger then 5 letters", icon)
                            }

                        } else {
                            username.setError("Please Enter a Valid Email ID", icon)
                        }

                    }
        }
    }

    private fun firebaseSignUp(){
        firebaseAuth.createUserWithEmailAndPassword(username.text.toString(), password.text.toString())
            .addOnCompleteListener{ task ->
                if (task.isSuccessful){
                    val intent = Intent(this.context, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()

                }
            }
    }

}