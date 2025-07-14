package com.citizen.fmc.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.citizen.fmc.R
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.citizen.fmc.utils.Constants
import com.citizen.fmc.utils.ImageProcessor
import com.citizen.fmc.utils.PortraitImage
import com.citizen.fmc.utils.Utils
import com.google.android.cameraview.CameraView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class CustCameraXActivity : AppCompatActivity() {

    private lateinit var mCameraView: CameraView
    private lateinit var captureImage: ImageButton
    private lateinit var stateChange: ImageButton
    private var isBackCamera: Boolean = false
    private lateinit var imageProcessor: PortraitImage
    private var capturedImageFile: File? = null
    private val tag = javaClass.simpleName
    private var uniqueId: String = ""
    private var cameraChange: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cust_camera_xactivity)
       /* ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
        imageProcessor = PortraitImage(this)
        mCameraView = findViewById(R.id.previewView)
        captureImage = findViewById(R.id.custom_image_button)
        stateChange = findViewById(R.id.custom_flip_button)

        isBackCamera = intent.getBooleanExtra(Constants.KEY_IS_BACK_CAMERA, false)
        cameraChange = intent.getStringExtra("changeCamera") ?: ""

        stateChange.visibility = if (cameraChange.equals("both", ignoreCase = true)) View.VISIBLE else View.GONE
        stateChange.setOnClickListener {
            isBackCamera = !isBackCamera
            mCameraView.facing = if (isBackCamera) CameraView.FACING_BACK else CameraView.FACING_FRONT
        }

        permissionCheck()
        initializeCameraCallback()

        captureImage.setOnClickListener {
            mCameraView.takePicture()
        }
    }

    private fun permissionCheck() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        mCameraView.start()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    requests: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            })
            .onSameThread()
            .check()
    }

    private fun initializeCameraCallback() {
       mCameraView.addCallback(object : CameraView.Callback() {
            override fun onCameraOpened(cameraView: CameraView) {
                Log.d(tag, "onCameraOpened")
            }

            override fun onCameraClosed(cameraView: CameraView) {
                Log.d(tag, "onCameraClosed")
            }

            override fun onPictureTaken(cameraView: CameraView, data: ByteArray) {
                uniqueId = Constants.generateUniqueId(Utils.getUserDetails(this@CustCameraXActivity).civilianId)
                Log.d(tag, "onPictureTaken: captured file length ==> ${data.size}")
                generateNewFile(data)
            }
        })

        mCameraView.facing = if (isBackCamera) CameraView.FACING_BACK else CameraView.FACING_BACK
        mCameraView.setAutoFocus(true)
        mCameraView.flash = CameraView.FLASH_AUTO
    }

    private fun generateNewFile(dataBytes: ByteArray) {
        try {
            val imageFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MCF_Citizen")
            if (!imageFile.exists()) imageFile.mkdirs()

            capturedImageFile = File(imageFile.path, "$uniqueId.jpg").apply {
                if (!exists()) {
                    parentFile?.mkdirs()
                    createNewFile()
                }
            }
            writeDataToFile(capturedImageFile!!, dataBytes)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun writeDataToFile(file: File, dataBytes: ByteArray) {
        try {
            FileOutputStream(file).use { it.write(dataBytes) }
            submitData()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun submitData() {
        capturedImageFile?.let {
            val imageUri = Uri.fromFile(it)
            val image = getFileFromUri(imageUri)?.let { file -> imageProcessor.fixImageRotation(file) }
            val intent = Intent().apply {
                putExtra("MCF_Citizen_Image", image?.path)
                putExtra("resultStatus", "DONE")
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        } ?: run {
            Constants.customToast(applicationContext, "Please Capture Image First", 1)
        }
    }

    override fun onPause() {
        super.onPause()
        mCameraView.stop()
    }

    override fun onRestart() {
        super.onRestart()
        mCameraView.start()
    }

    private fun getFileFromUri(uri: Uri): File? {
        val file = File(externalCacheDir, "cropped_image.jpg")
        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

}