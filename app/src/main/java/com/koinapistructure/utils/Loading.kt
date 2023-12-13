package com.braintechnosys.qickjob.utils

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.koinapistructure.R

class Loading {
    private var iActivity: Activity? = null
    private var dialog: Dialog? = null

    fun show(activity: Activity) {
        iActivity = activity
        dialog = Dialog(iActivity!!)
        val factory = iActivity!!.layoutInflater

        val infaltedView = factory.inflate(R.layout.dialog_loader, null)

        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)

        dialog?.setContentView(infaltedView)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog?.setCanceledOnTouchOutside(false)

        if (!dialog?.isShowing!!)
            dialog?.show()
    }

    fun hide(activity: Activity) {
        iActivity = activity
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }
}