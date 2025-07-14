package com.citizen.fmc.activity;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import static kotlin.jvm.internal.InlineMarker.mark;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.citizen.fmc.R;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.ImageProcessor;
import com.citizen.fmc.utils.PortraitImage;
import com.citizen.fmc.utils.Utils;
import com.google.android.cameraview.CameraView;
import com.google.common.util.concurrent.ListenableFuture;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Intent;

import androidx.lifecycle.LifecycleOwner;

public class CustomCameraXActivity extends AppCompatActivity {

    String image = "";
    private CameraView.Callback mCallback;
    private CameraView mCameraView;
    private File _capturedImageFile = null;
    private boolean isBackCamera;
    Context mContext;
    ImageButton captureImage;
    private String TAG = getClass().getSimpleName();
    String uniqueId = "";
    ImageButton state_change;
    String cameraChange = "";
    private PortraitImage imageProcessor;
    String imageAnalysis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_camera_x);

        mContext = CustomCameraXActivity.this;
        imageProcessor = new PortraitImage(this);
        mCameraView = findViewById(R.id.previewView);
        isBackCamera = getIntent().getBooleanExtra(Constants.KEY_IS_BACK_CAMERA, false);
        captureImage = findViewById(R.id.custom_image_button);
        state_change = findViewById(R.id.custom_flip_button);
        try {
            cameraChange = getIntent().getStringExtra("changeCamera");
            if (cameraChange == null) {
                cameraChange = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cameraChange.equalsIgnoreCase("both")) {
            state_change.setVisibility(View.VISIBLE);

        } else {
            state_change.setVisibility(View.GONE);
//            state_change.setVisibility(View.VISIBLE);

        }


        state_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBackCamera) {
                    mCameraView.setFacing(CameraView.FACING_BACK);
                    isBackCamera=false;
                } else {
                    mCameraView.setFacing(CameraView.FACING_FRONT);
                    isBackCamera=true;
                }
            }
        });

        permissionCheck();
        initializeCameraCallback();

        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mCameraView != null) {
                    mCameraView.takePicture();
                }
            }
        });
    }


    public void permissionCheck() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            mCameraView.start();
                        }
                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                        permissionToken.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }


    private void initializeCameraCallback() {
        mCallback = new CameraView.Callback() {

            @Override
            public void onCameraOpened(CameraView cameraView) {
                Log.d(TAG, "onCameraOpened");
            }

            @Override
            public void onCameraClosed(CameraView cameraView) {
                Log.d(TAG, "onCameraClosed");
            }

            @Override
            public void onPictureTaken(CameraView cameraView, final byte[] data) {
                uniqueId = Constants.generateUniqueId(Utils.getUserDetails(mContext).getCivilianId());
                Log.d(TAG, "onPictureTaken: captured file length ==> " + data.length);
                generateNewFile(data);
            }
        };

        if (mCameraView != null) {
            if (isBackCamera) {
                mCameraView.setFacing(CameraView.FACING_BACK);
            } else {
                mCameraView.setFacing(CameraView.FACING_BACK);
            }
            mCameraView.setAutoFocus(true);
            mCameraView.setFlash(CameraView.FLASH_AUTO);
            mCameraView.addCallback(mCallback);
        }
    }

    private void generateNewFile(byte[] _dataBytes) {
        try {
            File imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MCF_Citizen");
            if (!imageFile.exists()) {
                imageFile.mkdirs();
            }
            _capturedImageFile = new File(imageFile.getPath(), "" + uniqueId + ".jpg");
            if (!_capturedImageFile.exists()) {
                _capturedImageFile.getParentFile().mkdirs();
                _capturedImageFile.createNewFile();
            }

            writeDataToFile(_capturedImageFile, _dataBytes);
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
    }

    private void writeDataToFile(File _file, byte[] _dataBytes) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(_file);
            os.write(_dataBytes);
            os.close();
            SubmitData();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

   /* public void SubmitData() {
        if (_capturedImageFile!=null)
        {
            image = String.valueOf(getFileFromUri(Uri.fromFile(_capturedImageFile)));
            if (image != null) {
               String image1 = String.valueOf(imageProcessor.fixImageRotation(new File(image)));
                Intent intent = new Intent();
                intent.putExtra("MCF_Citizen_Image", image1);
                intent.putExtra("resultStatus", "DONE");
                setResult(RESULT_OK, intent);
                finish();
            } else {

            }
        }
        else
        {
            Constants.customToast(context,"Please Capture Image First",1);
        }
    }
*/
   private void  SubmitData() {
       if (_capturedImageFile != null) {
           Uri imageUri = Uri.fromFile(_capturedImageFile);
           File image = getFileFromUri(imageUri);
           if (image != null) {
               image = imageProcessor.fixImageRotation(image);
           }
           Intent intent = new Intent();
           intent.putExtra("MCF_Citizen_Image", (image != null) ? image.getPath() : null);
           intent.putExtra("resultStatus", "DONE");

           setResult(Activity.RESULT_OK, intent);
           finish();
       } else {
           Constants.customToast(getApplicationContext(), "Please Capture Image First", 1);
       }
   }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraView.stop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mCameraView.start();
    }
    private File getFileFromUri(Uri uri) {
        File file = new File(getExternalCacheDir(), "cropped_image.jpg");
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(file)) {
            if (inputStream != null) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }
}




