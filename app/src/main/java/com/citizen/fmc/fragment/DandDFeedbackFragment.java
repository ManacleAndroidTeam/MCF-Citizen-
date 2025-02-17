package com.citizen.fmc.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.citizen.fmc.R;
import com.citizen.fmc.utils.ApiInterface;
import com.citizen.fmc.utils.ButtonView;
import com.citizen.fmc.utils.ConnectivityReceiver;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;
import com.google.gson.JsonObject;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.ipaulpro.afilechooser.utils.FileUtils;
//import com.theartofdev.edmodo.cropper.CropImage;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class DandDFeedbackFragment extends Fragment {
    private View mView;
    private final String TAG = getClass().getSimpleName();
    private Activity activity;
    private AppBarLayout appBarLayout;
    private SpotsDialog spotsDialog;
    EditText feedback_edit_text;
    int userId;
    ImageButton feedback_image_button;
    TextView image_capture_text_view;
    ImageView feedback_image_view;
    ButtonView submit_button;
    private final int IMAGE_PICK_REQUEST_CODE = 1001;
    private File capturedFile = null;
    private Bitmap bitmap;
    Uri _mainUri, resultUri;
    String imagePath = "";
    String epc_code="";
    private File _shrinkImageFile = null;
 String customerRating="";
    private String PAGE_TYPE = "";
    private String statusTitle;
    CheckBox cb_clean_bin;
    CheckBox cb_good_behavior;
    RatingBar ratingBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (this.getArguments() != null) {
            /**
             */
            PAGE_TYPE = this.getArguments().getString(Constants.KEY_COMPLAINT_PAGE_TYPE);
            statusTitle = this.getArguments().getString(Constants.KEY_STATUS_TITLE);


        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_d_and_d_feedback, container, false);
        appBarLayout = mView.findViewById(R.id.app_bar_layout);
        activity = getActivity();

        Utils.setToolbarTitle(activity, "Feedback");
        userId = Utils.getUserDetails(activity).getCivilianId();
        epc_code = getArguments().getString("result1");
        feedback_image_button=mView.findViewById(R.id.feedback_image_button);
        feedback_image_view=mView.findViewById(R.id.feedback_image_view);
        image_capture_text_view=mView.findViewById(R.id.image_capture_text_view);
        feedback_edit_text=mView.findViewById(R.id.feedback_edit_text);
        cb_clean_bin=mView.findViewById(R.id.cb_clean_bin);
        cb_good_behavior=mView.findViewById(R.id.cb_good_behavior);
         ratingBar = mView.findViewById(R.id.customer_rating);
        Constants.setRatingBarColor(getActivity(), ratingBar);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                customerRating = String.valueOf(rating);
            }
        });

        submit_button=mView.findViewById(R.id.submit_button);
        spotsDialog = new SpotsDialog(activity, getResources().getString(R.string.please_wait_dialog_text), R.style.CustomSpotsDialogStyle);
        spotsDialog.setCancelable(false);
        imageCaptureButtonOnClick();
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectivityReceiver.isConnected()) {
                    ApiInterface mApiInterface = Utils.getInterfaceService();
//                    try {
//                        if(capturedFile.getName().isEmpty()){
//                         }else {
//                            _shrinkImageFile = new File(capturedFile.getAbsolutePath());
//                            Bitmap imageBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(_shrinkImageFile.getPath()), 768, 1024, false);
//                            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 30, new FileOutputStream(_shrinkImageFile));
//
//                        }
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }

                    MultipartBody.Part imagenPerfil=null;

                    try {

                        if (_mainUri==null) {
//                              imagenPerfil = MultipartBody.Part.createFormData("image_source", "", "");
                            imagenPerfil=  MultipartBody.Part.createFormData("image_source", "",
                                    RequestBody.create(MediaType.parse("multipart/form-data"), ""));
                        }else{
                            _shrinkImageFile = new File(capturedFile.getAbsolutePath());
                            Bitmap imageBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(_shrinkImageFile.getPath()), 768, 1024, false);
                            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 30, new FileOutputStream(_shrinkImageFile));
                            imagenPerfil=  MultipartBody.Part.createFormData("image_source", _shrinkImageFile.getName(),
                                    RequestBody.create(MediaType.parse("multipart/form-data"), _shrinkImageFile));

                        }
                    }
                    catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    String feedback_type="";
                    if(cb_clean_bin.isChecked()){
                        feedback_type= cb_clean_bin.getText().toString();
                    }

                    if(cb_good_behavior.isChecked()){
                        feedback_type=feedback_type+","+cb_good_behavior.getText().toString();
                    }


                    if(feedback_edit_text.getText().toString().isEmpty()){
                        Constants.customToast(activity,"Please fill Comment",1);

                    }else if(customerRating.isEmpty()){
                        Constants.customToast(activity,"Please fill Rating",1);
                    }else {
                        spotsDialog.show();

                        Call<JsonObject> mService = mApiInterface.submitWastage(
                                RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(userId)),
                                RequestBody.create(MediaType.parse("multipart/form-data"), epc_code),
                                RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(Constants.APP_TYPE)),
                                RequestBody.create(MediaType.parse("multipart/form-data"), "4"),
                                RequestBody.create(MediaType.parse("multipart/form-data"), feedback_edit_text.getText().toString()),
                                RequestBody.create(MediaType.parse("multipart/form-data"), ""),
                                RequestBody.create(MediaType.parse("multipart/form-data"), customerRating),
                                RequestBody.create(MediaType.parse("multipart/form-data"), feedback_type),
                                imagenPerfil);


                        mService.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                try {
                                    if (response.body() != null) {
                                        JsonObject jsonObject = response.body();
                                        if (jsonObject.get("response").getAsBoolean()) {
                                            spotsDialog.dismiss();
                                            Constants.customToast(getActivity(), "Data submitted successfully", 1);
                                            getFragmentManager().popBackStackImmediate();

                                        } else {
                                            spotsDialog.dismiss();
                                            String message = jsonObject.get("message").getAsString().trim();
                                            Constants.customToast(getActivity(), message, 1);
                                        }
                                    } else {
                                        spotsDialog.dismiss();
                                        Constants.customToast(getActivity(), Constants.MESSAGE_SOMETHING_WENT_WRONG, 1);

                                    }
                                } catch (Exception e) {
                                    spotsDialog.dismiss();
                                    e.printStackTrace();
                                    Utils.customToast(activity, Constants.MESSAGE_SOMETHING_WENT_WRONG, 0);
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                spotsDialog.dismiss();
                                call.cancel();
                                t.printStackTrace();
                                Utils.customToast(activity, Utils.failureMessage(t), 0);

                            }
                        });

                    }
                }else{

                    Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                            Constants.MESSAGE_CHECK_INTERNET, false);

                }

            }
        });
        return mView;
    }

    private Uri getCaptureImageOutputUri() {
        capturedFile = new File(activity.getExternalCacheDir(), Constants.FILE_NAME_PNG);
        return FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", capturedFile);
    }


    ActivityResultLauncher<CropImageContractOptions> cropImage = registerForActivityResult(new CropImageContract(), result -> {
        if (result.isSuccessful()) {
            Bitmap cropped = BitmapFactory.decodeFile(result.getUriFilePath(getActivity(), true));
        }

        if (result.isSuccessful()) {
            Uri resultUri = result.getOriginalUri();
            // Get the File path from the Uri
            String path = FileUtils.getPath(getActivity(), resultUri);

            // Alternatively, use FileUtils.getFile(Context, Uri)
            if (path != null) {
                capturedFile = FileUtils.getFile(activity, resultUri);
                double size = Constants.getFileSize(capturedFile);
                Log.v(TAG, "file_size(MB): " + size
                        + "\n file_name: " + capturedFile.getName()
                        + "\n file_path: " + path);
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeFile(result.getUriFilePath(getActivity(), true));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        else  {
            result.getError().printStackTrace();
//            Constants.customToast(getActivity(), Constants.SOMETHING_WRONG_MSG, 2);
        }
    });

    private void launchImageCropper(Uri uri) {
        CropImageOptions cropImageOptions = new CropImageOptions();
        cropImageOptions.imageSourceIncludeGallery = false;
        cropImageOptions.imageSourceIncludeCamera = true;
        CropImageContractOptions cropImageContractOptions = new CropImageContractOptions(uri, cropImageOptions);
        cropImage.launch(cropImageContractOptions);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            switch (requestCode) {
                case IMAGE_PICK_REQUEST_CODE:
                    if (resultCode == RESULT_OK) {
                        if (data != null) {
                            _mainUri = data.getData();
                        }
                        if (data == null || _mainUri == null) {
                            _mainUri = FileUtils.getUri(capturedFile);
                        }
                        if (_mainUri != null) {
                            // start cropping activity for pre-acquired image saved on the device
                            launchImageCropper(_mainUri);
                        } else {
//                            Utils.showSnackBar(activity, parentLayout, Constants.MESSAGE_SOMETHING_WENT_WRONG, false);

                        }
                    }
                    break;

               /* case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        resultUri = result.getUri();
                        // Get the File path from the Uri
                        String path = FileUtils.getPath(activity, resultUri);
                        // Alternatively, use FileUtils.getFile(Context, Uri)
                        if (path != null) {
                            capturedFile = FileUtils.getFile(activity, resultUri);
                            double size = Utils.getFileSize(capturedFile);
                            Log.v(TAG, "file_size(MB): " + size
                                    + "\n file_name: " + capturedFile.getName()
                                    + "\n file_path: " + path);

                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), resultUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (imagePath == null) {
                                feedback_image_view.setImageBitmap(bitmap);
                                image_capture_text_view.setVisibility(View.GONE);
                                feedback_image_button.setVisibility(View.GONE);

                                imagePath = FileUtils.getPath(activity, resultUri);
                            } else {
                                feedback_image_view.setImageBitmap(bitmap);
                                image_capture_text_view.setVisibility(View.GONE);
                                feedback_image_button.setVisibility(View.GONE);

                                imagePath = FileUtils.getPath(activity, resultUri);
                            }
                        }
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        result.getError().printStackTrace();
//                        Utils.showSnackBar(activity, parentLayout, Constants.MESSAGE_SOMETHING_WENT_WRONG, false);

                    }
                    break;*/

             }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    private void imageCaptureButtonOnClick() {
        feedback_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                    TedPermission.create()
                            .setPermissionListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted() {
                                    startActivityForResult(Utils.createChooserIntent(activity, getCaptureImageOutputUri()), IMAGE_PICK_REQUEST_CODE);
                                }

                                @Override
                                public void onPermissionDenied(List<String> deniedPermissions) {
                                    Utils.customToast(activity, "PERMISSION DENIED", 0);

                                }

                            /*    @Override
                                public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                                    Utils.customToast(activity, "PERMISSION DENIED", 0);
                                }*/

                            })
                            .setGotoSettingButtonText("SETTINGS")
                            .setDeniedMessage("If you reject permission,you can not use this service" +
                                    "\n\nPlease turn on permissions at [Settings] > [Permission]")
                            .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.CAMERA)
                            .check();
                } else {
                    TedPermission.create()
                            .setPermissionListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted() {
                                    startActivityForResult(Utils.createChooserIntent(activity, getCaptureImageOutputUri()), IMAGE_PICK_REQUEST_CODE);
                                }

                                @Override
                                public void onPermissionDenied(List<String> deniedPermissions) {
                                    Utils.customToast(activity, "PERMISSION DENIED", 0);
                                }

                               /* @Override
                                public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                                    Utils.customToast(activity, "PERMISSION DENIED", 0);
                                }*/
                            })
                            .setGotoSettingButtonText("SETTINGS")
                            .setDeniedMessage("If you reject permission,you can not use this service" +
                                    "\n\nPlease turn on permissions at [Settings] > [Permission]")
                            .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .check();
                }
            }
        });
    }

}
