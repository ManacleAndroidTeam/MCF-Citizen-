package com.citizen.fmc.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.citizen.fmc.utils.Constants;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import com.citizen.fmc.R;
import com.otaliastudios.cameraview.controls.Facing;

public class RetailerImageActivity extends AppCompatActivity {
    String image = "";
    String path = "";
//    private CameraView.Callback mCallback;
    private CameraView mCameraView;
    private File _capturedImageFile = null;
    private boolean isBackCamera;
    Context context;
    Button captureImage;
    private String TAG = getClass().getSimpleName();
    String uniqueId = "";
    Uri _capturedFileUri;
    Button state_change;
    String cameraChange="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_image);
        context = RetailerImageActivity.this;
        mCameraView = findViewById(R.id.camera);
        isBackCamera = getIntent().getBooleanExtra(Constants.KEY_IS_BACK_CAMERA, false);
        captureImage = findViewById(R.id.attandance_submit_btn);
        state_change  = findViewById(R.id.state_change );
        try {
            cameraChange=getIntent().getStringExtra("changeCamera");
            if (cameraChange==null){
                cameraChange="";
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        if (cameraChange.equalsIgnoreCase("both")){
            state_change.setVisibility(View.VISIBLE);

        }
        else {
            state_change.setVisibility(View.GONE);
        }


        state_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBackCamera) {
                    mCameraView.setFacing(Facing.BACK);
                    isBackCamera=false;
                } else {
                    mCameraView.setFacing(Facing.FRONT);
                    isBackCamera=true;
                }
            }
        });
        permissionCheck();
        initializeCameraCallback();
        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCameraView!=null)
                {
                    mCameraView.takePicture();
                }
            }
        });
    }
    private void initializeCameraCallback() {
        mCameraView.addCameraListener(new CameraListener() {


            public void onCameraOpened(CameraView cameraView) {
                Log.d(TAG, "onCameraOpened");
            }

            public void onCameraClosed(CameraView cameraView) {
                Log.d(TAG, "onCameraClosed");
            }

            public void onPictureTaken(CameraView cameraView, final byte[] data) {
                uniqueId = Constants.generateUniqueId(Integer.valueOf(Constants.getPrefrence(context, "User_id")));
                Log.d(TAG, "onPictureTaken: captured file length ==> " + data.length);
                generateNewFile(data);
            }
        });

        if (mCameraView != null) {
            // Set the facing (back/front) camera
            mCameraView.setFacing(isBackCamera ? Facing.BACK : Facing.FRONT);
            // Enable flash, if desired

//            mCameraView.setFlash(CameraView.FLASH_AUTO);
        }

      /*  if (mCameraView != null) {
            if (isBackCamera) {
                mCameraView.setFacing(CameraView.FACING_BACK);
            } else {
                mCameraView.setFacing(CameraView.FACING_BACK);
            }
            mCameraView.setAutoFocus(true);
            mCameraView.setFlash(CameraView.FLASH_AUTO);
            mCameraView.addCallback(mCallback);
        }*/
    }

    private void generateNewFile(byte[] _dataBytes) {
        try {
            File imageFile = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MsellCreateRetailer");
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
    @Override
    protected void onPause() {
        super.onPause();
//        mCameraView.stop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        mCameraView.start();
    }


    private void writeDataToFile(File _file, byte[] _dataBytes) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(_file);
            os.write(_dataBytes);
            os.close();
            passingImageThroughIntent(Uri.parse(_capturedImageFile.getAbsolutePath()), _capturedImageFile);
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
    private void passingImageThroughIntent(Uri fileUri, File mFile) {
        // Get the File path from the Uri
        try {
            path = FileUtils.getPath(RetailerImageActivity.this, fileUri);
/*
            Toast.makeText(getApplicationContext()," " + _capturedFileUri,Toast.LENGTH_SHORT ).show();
*/

            // Alternatively, use FileUtils.getFile(Context, Uri)
            if (path != null) {
                mFile = FileUtils.getFile(RetailerImageActivity.this, fileUri);
                double size = Constants.getFileSize(mFile);
                Log.e(TAG, "final_file_size_after_compression(MB): " + size
                        + "\n file_name: " + mFile.getName()
                        + "\n file_path: " + path);
            }
            _capturedFileUri = FileUtils.getUri(mFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void permissionCheck() {
        Dexter.withActivity( this )
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION )
                .withListener( new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
//                            mCameraView.start();
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                } )
                .onSameThread()
                .check();
    }

    public void SubmitData()
    {
        if (_capturedImageFile!=null)
        {
            image = _capturedImageFile.toString();
            Intent intent = new Intent();
            intent.putExtra("Retailer_Image", image);
            setResult(RESULT_OK, intent);
            finish();
        }
        else
        {
            Constants.customToast(context,"Plase Captured Retailer Image First",1);
        }
    }
}