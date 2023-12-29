package com.koinapistructure.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.braintechnosys.qickjob.utils.Loading
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.GoogleApiClient
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.koinapistructure.Apirequest.Request
import com.koinapistructure.R
import com.koinapistructure.databinding.ActivityMainBinding
import com.koinapistructure.social_sign_in.GoogleLogin
import com.koinapistructure.utils.CameraGalleryDialog
import com.koinapistructure.utils.CameraGalleryListener
import com.koinapistructure.utils.Constant.REQUEST_GET_PHOTO
import com.koinapistructure.utils.Constant.REQUEST_TAKE_PHOTO
import com.koinapistructure.utils.DataStatus
import com.koinapistructure.utils.FileUtils
import com.koinapistructure.utils.LogoutDialog
import com.koinapistructure.utils.NewYesNoListener
import com.koinapistructure.utils.getCurrentDate
import com.koinapistructure.utils.isConnected
import com.koinapistructure.utils.toast
import com.koinapistructure.viewmodel.MainViewModel
import com.yalantis.ucrop.UCrop
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity(), GoogleLogin.OnClientConnectedListener {

    lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by inject()
    private val loading: Loading by inject()
    private lateinit var plusLogin: GoogleLogin
    private var mCurrentPhotoPath: String? = null
    private var uriTemp: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (isConnected(this@MainActivity)) toast("IS Connected")


        val requestData = Request(category_id = 0, news_per_page = 10, page_no = 1)
        lifecycleScope.launch {
            viewModel.product(requestData)
            viewModel.data.observe(this@MainActivity) {
                when (it.status) {
                    DataStatus.Status.LOADING -> {
                        loading.show(this@MainActivity)
                    }

                    DataStatus.Status.SUCCESS -> {
                        loading.hide(this@MainActivity)
                        binding.products.text = it.data.toString()
                    }

                    DataStatus.Status.ERROR -> {
                        Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        binding.confirmButton.setOnClickListener {
            plusLogin.signIn()
        }

        binding.btLogout.setOnClickListener {
            LogoutDialog(this, object : NewYesNoListener {
                override fun onAffirmative() {
                    toast("LogOut Is Working Good")
                }
            }).show()
        }

        binding.galleryButton.setOnClickListener {
            galleryCameraPermission()
        }
        googleInit()

    }

    fun googleInit() {
        plusLogin = GoogleLogin(this, null, this)
        plusLogin.mGoogleApiClient.connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.e("RequestCode", requestCode.toString())
        if (requestCode == GoogleLogin.RC_SIGN_IN) {
            plusLogin.onActivityResult(requestCode, resultCode, data!!)
        }

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_TAKE_PHOTO -> {
                    val f = File(mCurrentPhotoPath!!)
                    uriTemp = FileProvider.getUriForFile(
                        this@MainActivity, this@MainActivity.packageName + ".utils.provider", f
                    )

                    UCrop.of(
                        uriTemp!!, Uri.fromFile(File(this@MainActivity.cacheDir, getCurrentDate()))
                    ).withAspectRatio(1f, 1f).start(this@MainActivity)

                }

                REQUEST_GET_PHOTO -> {
                    uriTemp = data?.data
                    UCrop.of(
                        uriTemp!!, Uri.fromFile(File(this.cacheDir, getCurrentDate()))
                    ).withAspectRatio(1f, 1f).start(this@MainActivity)


                }

                UCrop.REQUEST_CROP -> {

                    val resultUri = UCrop.getOutput(data!!)
                    binding.products.visibility = View.GONE
                    binding.image.visibility = View.VISIBLE
                    binding.image.setImageURI(resultUri)

                    val file = FileUtils.getFile(this@MainActivity, resultUri)
//                    Glide.with(this@MainActivity).load(resultUri.toString()).placeholder(R.drawable.icon_placeholder1).into(viewDataBinding.circleImageView)

                    //calling from global scope

                    GlobalScope.launch {
                        val compressedImageFile =
                            Compressor.compress(baseContext, file, Dispatchers.Main)


                        //  viewModel.uploadImage(compressedImageFile.toString())

                    }
                }


                else -> super.onActivityResult(requestCode, resultCode, data)

            }
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
            "GOOGLE_SIGN_IN", "onGoogleProfileFetchComplete: $id  $name  $email   $picURL  $gender"
        )

        plusLogin.signOut()
    }

    override fun onClientFailed(msg: String?) {
        Log.d("GOOGLE_SIGN_IN", "$msg")
        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
    }

    private fun galleryCameraPermission() {
        Dexter.withContext(this@MainActivity).withPermissions(
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                openPopUp()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                p1: PermissionToken?,
            ) {
            }
        }).check()
    }

    private fun openPopUp() {
        CameraGalleryDialog(this@MainActivity, object : CameraGalleryListener {
            override fun onCameraClicked() {
                takePicture()
            }

            override fun onGalleryClicked() {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type = "image/*"
                startActivityForResult(
                    Intent.createChooser(intent, getString(R.string.pick_from)), REQUEST_GET_PHOTO
                )
            }
        }).show()
    }

    private fun takePicture() {

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(this@MainActivity.packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(
                    MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(
                        this@MainActivity,
                        this@MainActivity.packageName + ".utils.provider",
                        photoFile
                    )
                )
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = this@MainActivity.getExternalFilesDir("")
        storageDir?.mkdirs()
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }
}