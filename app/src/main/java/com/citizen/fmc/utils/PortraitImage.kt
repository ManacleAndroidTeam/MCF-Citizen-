package com.citizen.fmc.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PortraitImage(private val context: Context) {

    /* // Orientation Listener to detect device rotation
     private val orientationEventListener by lazy {
         object : OrientationEventListener(context) {
             override fun onOrientationChanged(orientation: Int) {
                 if (orientation == ORIENTATION_UNKNOWN) return

                 val rotation = when (orientation) {
                     in 45 until 135 -> Surface.ROTATION_270
                     in 135 until 225 -> Surface.ROTATION_180
                     in 225 until 315 -> Surface.ROTATION_90
                     else -> Surface.ROTATION_0
                 }

                 // Set rotation to ImageCapture & ImageAnalysis
               //  imageCapture.targetRotation = rotation
                // imageAnalysis.targetRotation = rotation

                 Log.d("CameraHelper", "Updated targetRotation: $rotation")
             }
         }
     }

     fun startOrientationListener() {
         orientationEventListener.enable()
     }
     fun stopOrientationListener() {
         orientationEventListener.disable()
     }
     fun fixImageToPortrait(photoFile: File): File {
         try {
             if (!photoFile.exists()) {
                 Log.e("CameraHelper", "File does not exist: ${photoFile.absolutePath}")
                 return photoFile
             }

             val exif = ExifInterface(photoFile.absolutePath)
             val rotationDegree = when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)) {
                 ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                 ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                 ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                 else -> 0f
             }

             Log.d("CameraHelper", "EXIF Rotation: $rotationDegree degrees")

             val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
             val portraitBitmap = rotateToPortrait(bitmap, rotationDegree)

             // Save the rotated bitmap back to file
             saveBitmapToFile(portraitBitmap, photoFile)

             // Reset EXIF metadata
             resetExifOrientation(photoFile)

             return photoFile

         } catch (e: IOException) {
             e.printStackTrace()
         }
         return photoFile
     }
     private fun rotateToPortrait(bitmap: Bitmap, degrees: Float): Bitmap {
         val rotatedBitmap = if (degrees != 0f) {
             val matrix = Matrix().apply { postRotate(degrees) }
             Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
         } else {
             bitmap
         }

         // Ensure portrait mode (width < height)
         return if (rotatedBitmap.width > rotatedBitmap.height) {
             val matrix = Matrix().apply { postRotate(90f) }
             Bitmap.createBitmap(rotatedBitmap, 0, 0, rotatedBitmap.width, rotatedBitmap.height, matrix, true)
         } else {
             rotatedBitmap
         }
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
     private fun resetExifOrientation(file: File) {
         try {
             val exif = ExifInterface(file.absolutePath)
             exif.setAttribute(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL.toString())
             exif.saveAttributes()
         } catch (e: IOException) {
             e.printStackTrace()
         }
     }
     // ImageAnalysis to Process Images in Real-time
     fun analyzeImage(imageProxy: ImageAnalysis.Analyzer) {
         imageAnalysis.setAnalyzer({ it.run() }) { image ->
             val rotationDegrees = image.imageInfo.rotationDegrees
             Log.d("CameraHelper", "Image rotation: $rotationDegrees degrees")

             // Convert Image to Bitmap
             val buffer: ByteBuffer = image.planes[0].buffer
             val bytes = ByteArray(buffer.remaining())
             buffer.get(bytes)
             val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

             // Ensure it's in Portrait Mode
             val portraitBitmap = rotateToPortrait(bitmap, rotationDegrees.toFloat())

             // Save the corrected image if needed (optional)
             // saveBitmapToFile(portraitBitmap, File("your_file_path"))

             image.close()
         }
     }*/

    fun fixImageRotation(photoFile: File): File {
        return try {
            if (!photoFile.exists()) {
                Log.d("ImageRotation", "File does not exist: ${photoFile.absolutePath}")
                return photoFile
            }

            val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath) ?: return photoFile

            val exif = androidx.exifinterface.media.ExifInterface(photoFile.absolutePath)
            val orientation = exif.getAttributeInt(
                androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION,
                androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL
            )

            val rotationDegree = when (orientation) {
                androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }

            val rotatedBitmap = if (rotationDegree != 0f)
                rotateBitmap(bitmap, rotationDegree)
            else
                bitmap

            val finalBitmap = ensurePortrait(rotatedBitmap)

            saveBitmapToFile(finalBitmap, photoFile)
            return photoFile

        } catch (e: IOException) {
            e.printStackTrace()
            photoFile
        }
    }

    // Ensure the image is in portrait mode
    private fun ensurePortrait(bitmap: Bitmap): Bitmap {
        return if (bitmap.width > bitmap.height) {
            Log.d("ImageProcessor", "Image is landscape, rotating to portrait")
            rotateBitmap(bitmap, 90f) // Rotate to portrait
        } else {
            Log.d("ImageProcessor", "Image is already portrait")
            bitmap
        }
    }

    // Rotate the bitmap
    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    // Save the rotated bitmap back to the original file
    private fun saveBitmapToFile(bitmap: Bitmap, file: File) {
        try {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}