package com.koinapistructure.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.telephony.TelephonyManager
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.permissionx.guolindev.PermissionX
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

@SuppressLint("CommitTransaction")
inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

fun View.snackbar(
    text: CharSequence, duration: Int = Snackbar.LENGTH_SHORT, init: Snackbar.() -> Unit = {}
): Snackbar {
    val snack = Snackbar.make(this, text, duration)
    snack.init()
    snack.show()
    return snack
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int, addToStack: Boolean) {
    supportFragmentManager.inTransaction {
        if (addToStack) add(frameId, fragment, fragment.javaClass.simpleName).addToBackStack(
            fragment.javaClass.simpleName
        )
        else add(frameId, fragment)
    }
}

fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int, addToStack: Boolean) {
    supportFragmentManager.inTransaction {
        //setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
        if (addToStack) replace(frameId, fragment, fragment.javaClass.simpleName).addToBackStack(
            fragment.javaClass.simpleName
        )
        else replace(frameId, fragment, fragment.javaClass.simpleName)
    }
}

fun AppCompatActivity.replaceFragment(
    fragment: Fragment, frameId: Int, addToStack: Boolean, clearBackStack: Boolean
) {
    supportFragmentManager.inTransaction {

        if (clearBackStack && supportFragmentManager.backStackEntryCount > 0) {
            val first = supportFragmentManager.getBackStackEntryAt(0)
            supportFragmentManager.popBackStack(first.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        if (addToStack) replace(frameId, fragment, fragment.javaClass.simpleName).addToBackStack(
            fragment.javaClass.simpleName
        )
        else replace(frameId, fragment, fragment.javaClass.simpleName)

    }
}

fun AppCompatActivity.getCurrentFragment(): Fragment? {
    val fragmentManager = supportFragmentManager
    var fragmentTag: String? = ""

    if (fragmentManager.backStackEntryCount > 0) fragmentTag =
        fragmentManager.getBackStackEntryAt(fragmentManager.backStackEntryCount - 1).name

    return fragmentManager.findFragmentByTag(fragmentTag)
}

fun AppCompatActivity.getBackStackEntryCount(): Int {
    return supportFragmentManager.backStackEntryCount
}

fun FragmentActivity.requestCommonPermission(
    permissionList: List<String>,
    allPermissionApprovedListener: (() -> Unit)? = null,
    anyPermissionDenyListener: (() -> Unit)? = null

) {
    PermissionX.init(this).permissions(permissionList)


//      .explainReasonBeforeRequest()
        /*.onExplainRequestReason { scope, deniedList ->
            scope.showRequestReasonDialog(deniedList, "Core functionlaties are based on these permissions", "OK", "Cancel")
        }*/


        .onForwardToSettings { scope, deniedList ->
            scope.showForwardToSettingsDialog(
                deniedList,
                "You need to allow necessary permissions in Settings manually",
                "OK",
                "Cancel"
            )

            /*SnackbarOnAnyDeniedMultiplePermissionsListener.Builder.with(
                this.findViewById<View>(android.R.id.content),
                R.string.location_permission_needed
            ).withOpenSettingsButton(R.string.permission_rationale_settings_button_text).build()
*/


        }

        .request { allGranted, grantedList, deniedList ->
            if (allGranted) {
                allPermissionApprovedListener?.invoke()

//                Toast.makeText(this, "All permissions are granted", Toast.LENGTH_LONG).show()
            } else {

                anyPermissionDenyListener?.invoke()
//                Toast.makeText(this, "These permissions are denied: $deniedList", Toast.LENGTH_LONG).show()
            }
        }

}

fun Fragment.snackbar(
    text: CharSequence, duration: Int = Snackbar.LENGTH_LONG, init: Snackbar.() -> Unit = {}
): Snackbar {
    return view!!.snackbar(text, duration, init)
}

fun Activity.snackbar(
    view: View,
    text: CharSequence,
    duration: Int = Snackbar.LENGTH_LONG,
    init: Snackbar.() -> Unit = {}
): Snackbar {
    return view.snackbar(text, duration, init)
}

fun Context.toast(message: String) {
    Toast.makeText(this.applicationContext, message, Toast.LENGTH_SHORT).show()
}


fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): String? {
    val theta = lon1 - lon2
    var dist =
        (sin(deg2rad(lat1)) * sin(deg2rad(lat2)) + (cos(deg2rad(lat1)) * cos(deg2rad(lat2)) * cos(
            deg2rad(theta)
        )))
    dist = acos(dist)
    dist = rad2deg(dist)
    dist *= 60 * 1.1515 * 1.609344
    val result = if (dist < 1) {
        val disInMeters = dist * 1000
        disInMeters.round(1).plus(" m")
    } else dist.round(1).plus(" Km")
    return result
}

fun Double.round(decimals: Int): String {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    val value = kotlin.math.round(this * multiplier) / multiplier
    return if (value.toString().split(".")[1].toInt() == 0) value.toString().split(".")[0]
    else value.toString()
}

fun deg2rad(deg: Double): Double {
    return deg * Math.PI / 180.0
}

fun rad2deg(rad: Double): Double {
    return rad * 180.0 / Math.PI
}

fun getRealPathFromURI(context: Context, contentURI: Uri): String {
    val result: String
    val cursor = context.contentResolver.query(contentURI, null, null, null, null)
    if (cursor == null) { // Source is Dropbox or other similar local file path
        result = contentURI.path!!
    } else {
        cursor.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        result = cursor.getString(idx)
        cursor.close()
    }
    return result
}

fun String.isValidUrl(): Boolean = Patterns.WEB_URL.matcher(this).matches()

fun String.isValidDescription(): Boolean {
    val expression = "^[\\w\\.-]+@([\\w\\-]+\\.){2,4}$ && ^ [0-9]$"
    val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
    val matcher = pattern.matcher(this)
    return matcher.matches()
}

fun String.isValidPassword(): Boolean {
    val numRegex = ".*[0-9].*"
    val alphaRegex = ".*[a-zA-Z].*"
    val pattern1 = Pattern.compile(numRegex)
    val pattern2 = Pattern.compile(alphaRegex)
    return this.length >= 9 && pattern1.matcher(this).matches() && pattern2.matcher(this).matches()
}

fun String.isValidName(): Boolean {
    val namepattern =
        "^[A-Z][a-zA-Z]{3,}(?: [A-Z][a-zA-Z]*){0,2}\$".toRegex(RegexOption.IGNORE_CASE)
    return namepattern.containsMatchIn(this)
}

fun String.isValidEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches() && this.isEmpty()
}

fun loadImageURL(imageView: AppCompatImageView, imageURL: String) {
    Glide.with(imageView).load(imageURL).fallback(android.R.drawable.stat_notify_error)
        .timeout(4500).into(imageView)
}

fun getCountryCode(context: Context): String {
    val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    return tm.networkCountryIso
}

fun convertFormFileToMultipartBody(key: String, file: File?): MultipartBody.Part? = file?.let {
    MultipartBody.Part.createFormData(
        key, it.name, it.asRequestBody("image/*".toMediaTypeOrNull())
    )
}

fun getCurrentDate(): String = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(Date())
