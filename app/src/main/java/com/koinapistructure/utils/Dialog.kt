package com.koinapistructure.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import com.koinapistructure.R
import com.koinapistructure.databinding.DialogCameraGalleryBinding
import com.koinapistructure.databinding.DialogWordPdfBinding

interface CameraGalleryListener {
    fun onCameraClicked()
    fun onGalleryClicked()
}

interface WordPDFListener {
    fun onWORDClicked()
    fun onPDFClicked()
}

class CameraGalleryDialog(context: Context, private var listener: CameraGalleryListener) :
    Dialog(context, R.style.Theme_Dialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = DialogCameraGalleryBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        val window = window
        val wlp = window!!.attributes
        window.attributes = wlp
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.tvCamera.setOnClickListener {
            listener.onCameraClicked()
            dismiss()
        }
        binding.tvGallery.setOnClickListener {
            listener.onGalleryClicked()
            dismiss()
        }
    }
}

class PDFWORDDialog(context: Context, private var listener: WordPDFListener) :
    Dialog(context, R.style.Theme_Dialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = DialogWordPdfBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        val window = window
        val wlp = window!!.attributes
        window.attributes = wlp
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.tvPdf.setOnClickListener {
            listener.onPDFClicked()
            dismiss()
        }
        binding.tvWord.setOnClickListener {
            listener.onWORDClicked()
            dismiss()
        }
    }
}