package com.koinapistructure.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.VideoView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegKitConfig
import com.arthenica.ffmpegkit.ReturnCode
import com.braintechnosys.qickjob.utils.Loading
import com.google.android.gms.common.api.GoogleApiClient
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.koinapistructure.Apirequest.Request
import com.koinapistructure.R
import com.koinapistructure.adapter.HomeViewPagerAdapter
import com.koinapistructure.databinding.ActivityMainBinding
import com.koinapistructure.response.ImageData
import com.koinapistructure.social_sign_in.GoogleLogin
import com.koinapistructure.utils.CameraGalleryDialog
import com.koinapistructure.utils.CameraGalleryListener
import com.koinapistructure.utils.Constant.OPEN_DOCUMENT_REQUEST_CODE
import com.koinapistructure.utils.Constant.REQUEST_GET_PHOTO
import com.koinapistructure.utils.Constant.REQUEST_TAKE_PHOTO
import com.koinapistructure.utils.Constant.imageData
import com.koinapistructure.utils.DataStatus
import com.koinapistructure.utils.FileUtils
import com.koinapistructure.utils.LocalHelper
import com.koinapistructure.utils.LogoutDialog
import com.koinapistructure.utils.NewYesNoListener
import com.koinapistructure.utils.PDFWORDDialog
import com.koinapistructure.utils.WordPDFListener
import com.koinapistructure.utils.downloadPdf
import com.koinapistructure.utils.getCurrentDate
import com.koinapistructure.utils.hideKeyboard
import com.koinapistructure.utils.isConnected
import com.koinapistructure.utils.toast
import com.koinapistructure.viewmodel.MainViewModel
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher
import com.yalantis.ucrop.UCrop
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.indicator.DrawableIndicator
import com.zhpan.bannerview.utils.BannerUtils
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import xdroid.toaster.Toaster
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class MainActivity : AppCompatActivity(), GoogleLogin.OnClientConnectedListener {

    lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by inject()
    private val loading: Loading by inject()
    private var customLogo: Bitmap? = null
    private lateinit var plusLogin: GoogleLogin
    private var mCurrentPhotoPath: String? = null
    private var useDefaultLogo = true
    private var uriTemp: Uri? = null
    private var docType: Int? = null
    private lateinit var smsVerifyCatcher: SmsVerifyCatcher
    private lateinit var mViewPager: BannerViewPager<ImageData?>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        LocalHelper.setLocale(this@MainActivity, "en")

        if (isConnected(this@MainActivity)) toast("Is Connected")

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

        binding.NextActivity.setOnClickListener {
            toast("Work In Progress")
            startActivity(Intent(this@MainActivity,MainActivity2::class.java))
        }

        binding.confirmButton.setOnClickListener {
            plusLogin.signIn()
        }

        binding.youtubePlayer.setOnClickListener {
            toast("Work In Progress")
            startActivity(Intent(this@MainActivity,MainActivity3::class.java))
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

        binding.pdfWordButton.setOnClickListener {
            pdfWordPermission()
        }

        binding.otpField.setOnEditorActionListener { _, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    hideKeyboard()
                    otpData()
                }
            }
            true
        }

        binding.phoneNumber.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_NEXT -> {
                    hideKeyboard()
                    getData()
                }
            }
            true
        }

        binding.pdfDownloadButton.setOnClickListener {

            /*val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"))
            startActivity(browserIntent)*/

            downloadPdf(this,"https://link.testfile.org/PDF10MB", this)


        }

        binding.watermark.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            startActivityForResult(intent, 1)
        }

        smsVerifyCatcher = SmsVerifyCatcher(this@MainActivity) { message ->
            toast("This is the code $message")
        }

        setUpAutoScrollViewPager(imageData())

        googleInit()

    }

    private fun getData() {
        val code = binding.ccpPhone.selectedCountryCode()
        val phoneNumber = binding.phoneNumber.text.toString()
        if(phoneNumber.isNullOrEmpty())
            toast("Please Fill The Correct Number")
        else
            binding.phoneNumber.setText("")
            toast(code + phoneNumber)
    }

    private fun otpData() {
        val otp = binding.otpField.text.toString()
        if(otp.length<6)
            toast("Please Fill The Complete Otp")
        else
            binding.otpField.setText("")
            toast("This is the OTP $otp")
    }

    private fun googleInit() {
        plusLogin = GoogleLogin(this, null, this)
        plusLogin.mGoogleApiClient.connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.e("RequestCode", requestCode.toString())
        when (requestCode) {

            1->{
                data?.data?.let { it1 ->
                    runBlocking {
                        //Run converting process in a new thread as rendering is intense process for main thread
                        withContext(newSingleThreadContext("Converting"))
                        {

                            println("MainActivity.onActivityResult:::::${it1.path}")
                            addWatermarkVideo(it1)

                            /*//Check again for external storage permission if android API >= 30
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                if (Environment.isExternalStorageManager()) {
                                    addWatermarkVideo(it1)
                                }
                            } else {
                            }*/
                        }
                    }
                }
            }

            //GoogleLogin
            GoogleLogin.RC_SIGN_IN->plusLogin.onActivityResult(requestCode, resultCode, data!!)

            //Camera or Gallery
            Activity.RESULT_OK->when (requestCode) {
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

            //Pdf or Word file
            OPEN_DOCUMENT_REQUEST_CODE -> {

                data?.data?.also { documentUri ->

                    this.contentResolver.takePersistableUriPermission(
                        documentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )


                    var minSize = (6 * 1024 * 1024).toLong()

                    if (getFileSize(documentUri)!! < minSize) {

                        //  1 for word and 2 for pdf
                        if (docType == 1) {
                            toast("It is a Document Type")
                        } else {
                            toast("It is a Pdf Type")
                        }

                        docType = null

                        //   if (documentUri.lastPathSegment?.equals())

                        //       viewModel.hitUploadpdf(parentActivity.contentResolver, documentUri)


                    } else {

                        toast("Pdf size must be less than 6 mb")


                    }
                    //      viewModel.hitUploadpdf(parentActivity.contentResolver, documentUri)

                }
            }

        }
    }

    //Add the Logo to the video then save the new video to local storage in device
    private suspend fun addWatermarkVideo(videoUri: Uri) {
        //Get the path of the video needed to be edited
        val videoPath = FFmpegKitConfig.getSafParameterForRead(this.applicationContext, videoUri)
        //Path of default app logo


        var logoPath = withContext(Dispatchers.IO) {
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_login_logo)
            val file = File(this@MainActivity.cacheDir, "logo.png")
            file.createNewFile()
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            file
        }
        //Get current time to make unique name to the video that will be saved
        var now = LocalDateTime.now().toString()
        //Replace ':' in date as FFmpeg library can't deal with it
        now = now.replace(':', '+')
        //Extract the default logo to external directory in device to be used be FFmpeg library
        extractLogo()
        //Command that will be executed by FFmpeg library to add the Watermark , animate it
        // and save the video to external storage in the device
        val command =
            "-i $videoPath -i $logoPath -filter_complex  \"[1]colorchannelmixer = aa =0.8, scale = iw*0.2:-1[a];[0][a] overlay = x ='if(lt(mod(t\\,24)\\,12)\\,W-w-W*10/100\\,W*10/100)':y = 'if(lt(mod(t+6\\,24)\\,12)\\,H-h-H*5/100\\,H*5/100)'\" /storage/emulated/0/DCIM/Camera/LogoAdder$now.mp4"
        try {
            //Switch to IO thread as we will save a video to storage
            withContext(Dispatchers.IO) {
                Toaster.toastLong("Please wait while saving video :>")


                //Execute the command
                FFmpegKit.executeAsync(command) {
                    //Tell user if process is done successfully or not
                    if (ReturnCode.isSuccess(it.returnCode)) {
                        Toaster.toast("Video Saved Successfully :)")
                    } else {
                        Toaster.toast("Error happened while saving !")
                    }
                    //Return to main thread to call the Clear function
                    runOnUiThread(kotlinx.coroutines.Runnable {
                        kotlin.run {
                            clearSelection()
                        }
                    })
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Toast.makeText(
                this.applicationContext,
                "Error in Convert Execution !",
                Toast.LENGTH_SHORT
            ).show()
        }


    }

    private fun extractLogo() {
        val bm = BitmapFactory.decodeResource(resources, R.drawable.logo)
        val extStorageDirectory = "/storage/emulated/0/LogoAdder"
        File(extStorageDirectory).mkdir()
        val file = File(extStorageDirectory, "logo.png")
        if (!file.exists()) {
            try {
                val outStream = FileOutputStream(file)
                bm.compress(Bitmap.CompressFormat.PNG, 100, outStream)
                outStream.flush()
                outStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
                Toaster.toast("Error while loading the logo !")
            }
        }
    }


    private fun clearSelection() {
        customLogo = null
        useDefaultLogo = true
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

    private fun setUpAutoScrollViewPager(bannerList: List<ImageData?>) {

        val indicatorview = DrawableIndicator(this@MainActivity)
        indicatorview.setIndicatorDrawable(
            R.drawable.radio_selector, R.drawable.radio_selected_new
        )
        indicatorview.setIndicatorSize(20, 20, 20, 20)
        indicatorview.setIndicatorGap(20)

        mViewPager = this@MainActivity.findViewById(R.id.viewpager_home)
        mViewPager.apply {
            setCanLoop(true)
            setInterval(3000)
            setIndicatorView(indicatorview)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    BannerUtils.log("position:$position")
//                    viewModel.imagePosition =(position + 1).toString()
//                    viewModel.positionShow=viewModel.imagePosition + "/" + viewModel.imageTotalPosition
                }
            })

            adapter = HomeViewPagerAdapter(
                bannerList,
                object : HomeViewPagerAdapter.OnSubViewClickListener2 {
                    override fun click(position: Int) {
                        //      ImageActivity.open(this@PropertyDetailsActivity, viewModel?.selectedPhotoslist)
                    }
                })
        }.create(bannerList)

    }

    override fun onClientFailed(msg: String?) {
        Log.d("GOOGLE_SIGN_IN", "$msg")
        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
    }

    private fun pdfWordPermission() {
        Dexter.withContext(this@MainActivity).withPermissions(
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
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

    private fun galleryCameraPermission() {
        Dexter.withContext(this@MainActivity).withPermissions(
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                openPopUpImage()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                p1: PermissionToken?,
            ) {
            }
        }).check()
    }

    private fun openPopUpImage() {
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

    private fun openPopUp() {
        PDFWORDDialog(this, object : WordPDFListener {

            override fun onWORDClicked() {
                openDocumentPickerWORD()
                //  viewModel.logotype = 1
                docType = 1
            }

            override fun onPDFClicked() {
                openDocumentPickerPDF()
                //  viewModel.logotype = 0
                docType = 2
            }
        }).show()
    }

    private fun openDocumentPickerPDF() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }

        docType = 2
        startActivityForResult(intent, OPEN_DOCUMENT_REQUEST_CODE)
    }

    private fun openDocumentPickerWORD() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "application/msword"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        docType = 1
        startActivityForResult(intent, OPEN_DOCUMENT_REQUEST_CODE)
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

    private fun getFileSize(fileUri: Uri): Long? {
        val returnCursor: Cursor? = this.getContentResolver().query(fileUri, null, null, null, null)
        val sizeIndex: Int? = returnCursor?.getColumnIndex(OpenableColumns.SIZE)
        returnCursor?.moveToFirst()
        val size: Long? = sizeIndex?.let { returnCursor?.getLong(it) }
        returnCursor?.close()
        return size
    }

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

    override fun onStart() {
        super.onStart()
        smsVerifyCatcher.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        smsVerifyCatcher.onStop()
    }

}