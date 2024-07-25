package com.koinapistructure.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import com.koinapistructure.MyApplication
import com.koinapistructure.R
import com.koinapistructure.databinding.LogoutBinding


interface NewYesNoListener {
    fun onAffirmative()
}

fun toast(context: Context, message: String) {
    Toast.makeText(context, "" + message, Toast.LENGTH_LONG).show()
}

class LogoutDialog(context: Context, var listner: NewYesNoListener) : Dialog(context, R.style.Theme_Dialog) {
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = LogoutBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        val window = window
        val wlp = window!!.attributes
        window.attributes = wlp
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.apply {
            btLogout.setOnClickListener {
                listner.onAffirmative()
            }
            tvDismiss.setOnClickListener {
                dismiss()
            }

        }
    }
}

fun isConnected(mContext: Context): Boolean {
    val connectivityManager = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netInfo = connectivityManager.activeNetworkInfo
    return netInfo != null && netInfo.isConnected
}



