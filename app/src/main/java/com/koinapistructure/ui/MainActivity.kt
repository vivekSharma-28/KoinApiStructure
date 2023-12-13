package com.koinapistructure.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.braintechnosys.qickjob.utils.Loading
import com.google.android.gms.common.api.GoogleApiClient
import com.koinapistructure.Apirequest.Request
import com.koinapistructure.databinding.ActivityMainBinding
import com.koinapistructure.social_sign_in.GoogleLogin
import com.koinapistructure.utils.DataStatus
import com.koinapistructure.utils.LogoutDialog
import com.koinapistructure.utils.NewYesNoListener
import com.koinapistructure.utils.isConnected
import com.koinapistructure.utils.toast
import com.koinapistructure.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(),GoogleLogin.OnClientConnectedListener {

    lateinit var binding:ActivityMainBinding
    private val viewModel: MainViewModel by inject()
    private val loading: Loading by inject()
    private lateinit var plusLogin: GoogleLogin


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if(isConnected(this@MainActivity))
            toast("IS Connected")


        val requestData= Request(category_id = 0, news_per_page = 10, page_no = 1)
        lifecycleScope.launch {
            viewModel.product(requestData)
            viewModel.data.observe(this@MainActivity){
                when(it.status){
                    DataStatus.Status.LOADING->{
                        loading.show(this@MainActivity)
                    }
                    DataStatus.Status.SUCCESS->{
                        loading.hide(this@MainActivity)
                        binding.products.text = it.data.toString()
                    }
                    DataStatus.Status.ERROR->{
                        Toast.makeText(this@MainActivity,it.message,Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        binding.confirmButton.setOnClickListener {
            plusLogin.signIn()
        }

        binding.btLogout.setOnClickListener {
            LogoutDialog(this,object :NewYesNoListener{
                override fun onAffirmative() {
                    toast("LogOut Is Working Good")
                }
            }).show()
        }
        googleInit()

    }

    fun googleInit(){
        plusLogin = GoogleLogin(this,null , this)
        plusLogin.mGoogleApiClient.connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.e("RequestCode", requestCode.toString())
        if (requestCode == GoogleLogin.RC_SIGN_IN) {
            plusLogin.onActivityResult(requestCode, resultCode, data!!)
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

        plusLogin.signOut()
    }

    override fun onClientFailed(msg: String?) {
        Log.d("GOOGLE_SIGN_IN", "$msg")
        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
    }
}