package com.citizen.fmc.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.citizen.fmc.R;
import com.citizen.fmc.model.ComplaintCategoryModel;
import com.citizen.fmc.model.User;
import com.citizen.fmc.utils.ApiInterface;
import com.citizen.fmc.utils.ButtonView;
import com.citizen.fmc.utils.ConnectivityReceiver;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ======> Created by dheeraj-gangwar on 2018-04-21 <======
 */

public class DepartmentWiseFeedbackFragment extends Fragment implements AdapterView.OnItemSelectedListener{
    private Activity activity;
    private TextView emailTextView;
    private TextView mobileNumTextView;
    private EditText feedbackEditText;
    private ImageView feedbackImageView;
    private TextView imageCaptureTextView;
    private ImageButton captureImageButton;
    private ButtonView submitFeedbackButton;
    private File capturedFile;
    private final int IMAGE_PICK_REQUEST_CODE = 987;
    private int userId;
    private String uniqueId;
    private SpotsDialog spotsDialog;
    private String TAG = getClass().getSimpleName();
    private int departmentid;
    private Spinner department_spinner;
    private ArrayList<ComplaintCategoryModel> compCatList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_departmentfeedback, container, false);
        activity = getActivity();
        emailTextView = mView.findViewById(R.id.email_edit_text);
        mobileNumTextView = mView.findViewById(R.id.mobile_number_edit_text);
        feedbackEditText = mView.findViewById(R.id.feedback_edit_text);
        feedbackImageView = mView.findViewById(R.id.feedback_image_view);
        imageCaptureTextView = mView.findViewById(R.id.image_capture_text_view);
        captureImageButton = mView.findViewById(R.id.capture_image_button);
        submitFeedbackButton = mView.findViewById(R.id.submit_button);
        department_spinner = mView.findViewById(R.id.department_spinner);
        compCatList = new ArrayList<>();
        User mUser = Utils.getUserDetails(activity);
        userId = mUser.getCivilianId();
        String userEmail = mUser.getEmail();
        String userMobileNum = mUser.getMobileNum();
        uniqueId = Utils.generateUniqueId(userId);

        emailTextView.setText(userEmail != null && !userEmail.isEmpty() ? userEmail : "Not Available");
        mobileNumTextView.setText(userMobileNum != null && !userMobileNum.isEmpty() ? userMobileNum : "Not Available");

        spotsDialog = new SpotsDialog(activity, getResources().getString(R.string.please_wait_dialog_text),
                R.style.CustomSpotsDialogStyle);
        spotsDialog.setCancelable(false);

        getComplaintCategories();
        submitFeedbackButtonOnClick();
        return mView;
    }


    private void setDepartmentSpinner() {
        final List<String> stringList = new ArrayList<>();
        for (int x = 0; x < compCatList.size(); x++) {
            stringList.add(compCatList.get(x).getDepartmentName());
        }

        department_spinner.setAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, stringList));

        department_spinner.setOnItemSelectedListener(this);


    }

    private void captureImageButtonOnClick() {
        captureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                    TedPermission.create()
                            .setPermissionListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted() {
                                    startActivityForResult(createChooserIntent(), IMAGE_PICK_REQUEST_CODE);
                                }

                                @Override
                                public void onPermissionDenied(List<String> deniedPermissions) {
                                    Utils.customToast(activity, "PERMISSION DENIED", 0);

                                }

                                public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                                    Utils.customToast(activity, "PERMISSION DENIED", 0);
                                }
                            })
                            .setGotoSettingButtonText("SETTINGS")
                            .setDeniedMessage("If you reject permission,you can not use this service" +
                                    "\n\nPlease turn on permissions at [Settings] > [Permission]")
                            .setPermissions(Manifest.permission.CAMERA)
                            .check();
                } else {
                    TedPermission.create()
                            .setPermissionListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted() {
                                    startActivityForResult(createChooserIntent(), IMAGE_PICK_REQUEST_CODE);
                                }

                                @Override
                                public void onPermissionDenied(List<String> deniedPermissions) {
                                    Utils.customToast(activity, "PERMISSION DENIED", 0);
                                }

                                public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                                    Utils.customToast(activity, "PERMISSION DENIED", 0);
                                }
                            })
                            .setGotoSettingButtonText("SETTINGS")
                            .setDeniedMessage("If you reject permission,you can not use this service" +
                                    "\n\nPlease turn on permissions at [Settings] > [Permission]")
                            .setPermissions(Manifest.permission.CAMERA,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .check();
                }
            }
        });
    }

    private void submitFeedbackButtonOnClick() {
        submitFeedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    if (ConnectivityReceiver.isConnected()) {
                        spotsDialog.show();
                        submitFeedback();
                    } else {
                        Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                Constants.MESSAGE_CHECK_INTERNET, false);
                    }
                }
            }
        });
    }

    private boolean validateFields() {
        if (feedbackEditText.getText().toString().trim().isEmpty()) {
            Utils.showSnackBar(activity, activity.findViewById(android.R.id.content), "Please enter feedback !", false);
            return false;
        }
        return true;
    }

    public Intent createChooserIntent() {
        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();
        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = activity.getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                activity.grantUriPermission(res.activityInfo.packageName, outputFileUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        try {
            for (Intent intent : allIntents) {
                if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                    mainIntent = intent;
                    break;
                }
            }
            allIntents.remove(mainIntent);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    /**
     * Get URI to image received from capture by camera.
     */
    private Uri getCaptureImageOutputUri() {
        capturedFile = new File(activity.getExternalCacheDir(), Constants.FILE_NAME_PNG);
        return FileProvider.getUriForFile(activity,
                activity.getPackageName() + ".provider", capturedFile);
    }

    private void submitFeedback() {
        Call<JsonObject> callCreateProject = Utils.getInterfaceService().addFeedbackDepartmentWise(userId,
                departmentid,Constants.APP_TYPE, feedbackEditText.getText().toString(),
                uniqueId,
                Constants.HEADER_TOKEN_BEARER + " " + Utils.getUserToken(activity),
                Constants.HEADER_ACCEPT);

        callCreateProject.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    if (response.body() != null) {
                        JsonObject jsonObject = response.body();
                        if (jsonObject.get("response").getAsBoolean()) {
                            spotsDialog.dismiss();
                            showSuccessDialog();
                        } else {
                            spotsDialog.dismiss();
                            Log.v(TAG, "Message===>" + jsonObject.get("message").getAsString().trim());
                            Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                    jsonObject.get("message").getAsString().trim(), false);
                        }
                    } else {
                        spotsDialog.dismiss();
                        Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                Constants.MESSAGE_TRY_AGAIN_LATER, false);
                    }
                } catch (Exception e) {
                    spotsDialog.dismiss();
                    e.printStackTrace();
                    Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                            Constants.MESSAGE_SOMETHING_WENT_WRONG, false);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                spotsDialog.dismiss();
                call.cancel();
                t.printStackTrace();
                Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                        Utils.failureMessage(t), false);
            }
        });
    }

    /* ====================== Request method to get complaint categories ====================== */
    private void getComplaintCategories() {
        ApiInterface mApiInterface = Utils.getInterfaceService();
        Log.v(TAG, Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(activity));
        String token= Constants.HEADER_TOKEN_BEARER+Utils.getUserToken(activity);
        Call<JsonObject> mService = mApiInterface.getComplaintCategories(Constants.HEADER_TOKEN_BEARER
                        + Utils.getUserToken(activity),
                Constants.HEADER_ACCEPT);
        mService.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    if (activity != null && isAdded()) {
                        if (response.body() != null) {
                            JsonObject jsonObject = response.body();
                            if (jsonObject.get("response").getAsBoolean()) {
                                JsonArray departmentJsonArray = jsonObject.getAsJsonArray("data");
                                if (departmentJsonArray.size() > 0) {
                                    for (int i = 0; i < departmentJsonArray.size(); i++) {
                                        ComplaintCategoryModel categoryModel = new GsonBuilder().create()
                                                .fromJson(departmentJsonArray.get(i), ComplaintCategoryModel.class);
                                        compCatList.add(categoryModel);
                                    }
                                    setDepartmentSpinner();
                                    Log.v(TAG, compCatList.toString());
                                    spotsDialog.dismiss();
                                    //createComplaintCategoryDialog();
                                }
                            } else {
                               // refreshCategoryList = true;
                                spotsDialog.dismiss();
                                Utils.customToast(activity,
                                        jsonObject.get("message").getAsString().trim(), 0);
                            }
                        } else {
                          //  refreshCategoryList = true;
                            spotsDialog.dismiss();
                            Utils.customToast(activity,
                                    Constants.MESSAGE_NO_DATA_FOUND, 0);
                        }
                    }
                } catch (Exception e) {
                   // refreshCategoryList = true;
                    spotsDialog.dismiss();
                    e.printStackTrace();
                    Utils.customToast(activity, Constants.MESSAGE_SOMETHING_WENT_WRONG, 0);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (activity != null && isAdded()) {
                    spotsDialog.dismiss();
                   // refreshCategoryList = true;
                    call.cancel();
                    t.printStackTrace();
                    Utils.customToast(activity,Utils.failureMessage(t), 0);
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.department_spinner) {
            departmentid = compCatList.get(position).getDepartmentId();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


    private void showSuccessDialog() {
        final Dialog dialog = new Dialog(activity, R.style.dialogSlideAnimation);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_logout);
        dialog.setCancelable(false);

        TextView titleText = dialog.findViewById(R.id.dialog_title);
        TextView dialogMessage = dialog.findViewById(R.id.dialog_title_message);
        TextView dialogInfoMessage = dialog.findViewById(R.id.dialog_info_message);
        ImageView titleIcon = dialog.findViewById(R.id.dialog_title_icon);
        Button cancelButton, OKButton;
        cancelButton = dialog.findViewById(R.id.cancel_button);
        OKButton = dialog.findViewById(R.id.ok_button);

        cancelButton.setVisibility(View.GONE);
        dialogInfoMessage.setVisibility(View.GONE);

        titleIcon.setImageResource(R.drawable.ic_information);
        titleText.setText(R.string.Information);
        dialogMessage.setText(Html.fromHtml("<strong>Feedback submitted successfully !</strong>"));

        OKButton.setText(R.string.ok);
        OKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Utils.changeFragmentWithoutBackStack(getFragmentManager(), new HomeFragment());
            }
        });

        dialog.show();
    }



   /* @SuppressLint("StaticFieldLeak")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            Bitmap bitmap;
            switch (requestCode) {
                case IMAGE_PICK_REQUEST_CODE:
                    if (resultCode == RESULT_OK) {
                        final Uri _mainUri;
                        if (data != null) {
                            _mainUri = data.getData();
                        } else {
                            _mainUri = FileUtils.getUri(capturedFile);
                        }

                        if (_mainUri != null) {
                            // start cropping activity for pre-acquired image saved on the device
                            CropImage.activity(_mainUri)
                                    .setOutputCompressQuality(Constants.IMAGE_QUALITY)
                                    .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                                    .start(activity);
                        } else {
                            Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                    Constants.MESSAGE_SOMETHING_WENT_WRONG, false);
                        }
                    }
                    break;

                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        Uri resultUri = result.getUri();

                        // Get the File path from the Uri
                        String path = FileUtils.getPath(activity, resultUri);

                        // Alternatively, use FileUtils.getFile(Context, Uri)
                        if (path != null) {
                            capturedFile = FileUtils.getFile(activity, resultUri);
                            double size = Utils.getFileSize(capturedFile);
                            Log.v(TAG, "file_size(MB): " + size
                                    + "\n file_name: " + capturedFile.getName()
                                    + "\n file_path: " + path);

                            bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), resultUri);
//                                bitmap = ImageHelper.rotateImageIfRequired(bitmap, capturedFile.getAbsolutePath());
//                                bitmap = ImageHelper.getResizedBitmap(bitmap, 500);
                            feedbackImageView.setImageBitmap(bitmap);
                            imageCaptureTextView.setVisibility(View.GONE);
                        }
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        result.getError().printStackTrace();
                        Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                Constants.MESSAGE_SOMETHING_WENT_WRONG, false);
                    }
                    break;
            }
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
    }*/
}
