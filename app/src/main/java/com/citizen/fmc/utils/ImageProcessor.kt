package com.citizen.fmc.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.exifinterface.media.ExifInterface
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class ImageProcessor(
    private val context: Context,
    private val cropImageLauncher: ActivityResultLauncher<CropImageContractOptions>
) {

    fun cropImage(imageUri: Uri) {
        val options = CropImageOptions().apply {
            guidelines = CropImageView.Guidelines.ON
            aspectRatioX = 1
            aspectRatioY = 1
            outputCompressQuality = 100
        }

        val contractOptions = CropImageContractOptions(imageUri, options)
        cropImageLauncher.launch(contractOptions)
    }

    fun fixImageToPortrait(photoFile: File): File {
        try {
            if (!photoFile.exists()) {
                Log.e("ImageProcessor", "File does not exist: ${photoFile.absolutePath}")
                return photoFile
            }

            val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)

            val portraitBitmap = if (bitmap.width > bitmap.height) {
                Log.d("ImageProcessor", "Image is in landscape mode, rotating to portrait...")
                rotateBitmap(bitmap, 90f)
            } else {
                Log.d("ImageProcessor", "Image is already in portrait mode.")
                bitmap
            }

            saveBitmapToFile(portraitBitmap, photoFile)

            return photoFile

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return photoFile
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun saveBitmapToFile(bitmap: Bitmap, file: File) {
        try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}
