package com.koinapistructure.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.koinapistructure.Apirequest.Request
import com.koinapistructure.databinding.ActivityMainBinding
import com.koinapistructure.utils.DataStatus
import com.koinapistructure.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    lateinit var binding:ActivityMainBinding
    private val viewModel: MainViewModel by inject()
//    private val adaptor by lazy { }


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

    }
}