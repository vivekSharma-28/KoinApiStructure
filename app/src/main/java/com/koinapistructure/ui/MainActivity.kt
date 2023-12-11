package com.koinapistructure.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.api.GoogleApiClient
import com.koinapistructure.Apirequest.Request
import com.koinapistructure.databinding.ActivityMainBinding
import com.koinapistructure.social_sign_in.GoogleLogin
import com.koinapistructure.utils.DataStatus
import com.koinapistructure.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(),GoogleLogin.OnClientConnectedListener {

    lateinit var binding:ActivityMainBinding
    private val viewModel: MainViewModel by inject()
    private lateinit var Login: GoogleLogin


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val requestData= Request(category_id = 0, news_per_page = 10, page_no = 1)
        lifecycleScope.launch {
            viewModel.product(requestData)
            viewModel.data.observe(this@MainActivity){
                when(it.status){
                    DataStatus.Status.LOADING->{
                        Toast.makeText(this@MainActivity,"LOADING.....",Toast.LENGTH_LONG).show()
                    }
                    DataStatus.Status.SUCCESS->{
                        binding.products.text = it.data.toString()
                    }
                    DataStatus.Status.ERROR->{
                        Toast.makeText(this@MainActivity,it.message,Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        binding.confirmButton.setOnClickListener {
            Login.signIn()
        }
        googleInit()

    }

    fun googleInit(){
        Login = GoogleLogin(this,null , this)
        Login.mGoogleApiClient.connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.e("RequestCode", requestCode.toString())
        if (requestCode == GoogleLogin.RC_SIGN_IN) {
            Login.onActivityResult(requestCode, resultCode, data!!)
        }
    }


    override fun onGoogleProfileFetchComplete(
        id: String?,
        name: String?,
        email: String?,
        picURL: String,
        gender: String,
        firstname: String,
        lastname: String
    ) {
        Log.d(
            "GOOGLE_SIGN_IN",
            "onGoogleProfileFetchComplete: $id  $name  $email   $picURL  $gender"
        )

        Login.signOut()
    }

    override fun onClientFailed(msg: String?) {
        Log.d("GOOGLE_SIGN_IN", "${msg} ")
        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
    }
}