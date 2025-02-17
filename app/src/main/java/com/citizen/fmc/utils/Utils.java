package com.citizen.fmc.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.citizen.fmc.R;
import com.citizen.fmc.activity.LoginActivity;
import com.citizen.fmc.model.User;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import dmax.dialog.SpotsDialog;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * ======> Created by dheeraj-gangwar on 2017-12-30 <======
 */

@SuppressLint("SimpleDateFormat")
public class Utils {
    private static String TAG = "Utils";
    private static SpotsDialog spotsDialog;


    Gson gson = new GsonBuilder().setLenient().create();


    public static ApiInterface getInterfaceService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(ApiInterface.class);
    }

    public static ApiInterface getTreeService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.TREE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(ApiInterface.class);
    }

    public static ApiInterface getInterfaceServiceParking() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL_PARKING)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(ApiInterface.class);
    }

    public static User getUserDetails(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        if (sharedPreferences != null) {
            return new GsonBuilder().create().fromJson(sharedPreferences.getString(Constants.USER_DATA, null), User.class);
        } else {
            return new User();
        }
    }

    public static String getUserToken(Context mContext) {
        String userToken;
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        if (sharedPreferences != null) {
            userToken = sharedPreferences.getString(Constants.USER_TOKEN, null);
            if (userToken != null && !userToken.isEmpty()) {
                return userToken;
            } else {
                return userToken;
            }
        } else {
            return null;
        }
    }

    public static String getAppVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "NA";
        }
    }

    public static int getAppVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String generateUniqueId(int userId) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
        String uniqueId = df.format(c.getTime()) + userId;
        Log.v("UNIQUE_ID===> ", uniqueId);
        return uniqueId;
    }

    @SuppressLint("HardwareIds")
    public static String uniqueDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /* ====================== Clear User details ====================== */
    public static void logoutUser(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Clearing all data from Shared Preferences
        editor.clear();
        editor.apply();

        // After logout redirect user to LoginActivity
        Intent i = new Intent(mContext, LoginActivity.class);

        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        mContext.startActivity(i);
    }

    public static String getGeoLocationAddress(Context mContext, double latitude, double longitude) {
        String result = null;
        try {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && addresses.size() > 0) {
                /* String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();*/
                result = addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result != null && !result.isEmpty() ? result : "N/A";
    }

    /**
     * Custom Snack-bar for application
     */
   /* public static void showSnackBar(Context context, View view, String message, boolean value) {
        try {
            Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);

            // Changing action button text color
            View sbView = snackbar.getView();
            if (value)
                sbView.setBackgroundColor(context.getResources().getColor(R.color.colorGreen));
            else
                sbView.setBackgroundColor(context.getResources().getColor(R.color.colorRed));

            TextView textView = sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(14);
            snackbar.show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
*/

    public static void showSnackBar(Context context, View view, String message, boolean value) {
        try {
            Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);

            // Customize the Snackbar's background
            View sbView = snackbar.getView();
            if (value) {
                sbView.setBackgroundColor(context.getResources().getColor(R.color.colorGreen));
            } else {
                sbView.setBackgroundColor(context.getResources().getColor(R.color.colorRed));
            }

            // Access the TextView for the message
            TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
            if (textView != null) {
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(14);
            }

            snackbar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setDefaultFont(Context context, String staticTypefaceFieldName, String fontAssetName) {
        final Typeface regular = Typeface.createFromAsset(context.getAssets(),
                fontAssetName);
        replaceFont(staticTypefaceFieldName, regular);
    }

    private static void replaceFont(String staticTypefaceFieldName, final Typeface newTypeface) {
        try {
            final Field staticField = Typeface.class
                    .getDeclaredField(staticTypefaceFieldName);
            staticField.setAccessible(true);
            staticField.set(null, newTypeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Using reflection to override default typeface
     * NOTICE: DO NOT FORGET TO SET TYPEFACE FOR APP THEME AS DEFAULT TYPEFACE WHICH WILL BE OVERRIDDEN
     *
     * @param context                    to work with assets
     * @param defaultFontNameToOverride  for example "monospace"
     * @param customFontFileNameInAssets file name of the font from assets
     */
    public static void overrideFont(Context context, String defaultFontNameToOverride, String customFontFileNameInAssets) {
        final Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), customFontFileNameInAssets);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Map<String, Typeface> newMap = new HashMap<String, Typeface>();
            newMap.put("serif", customFontTypeface);
            try {
                final Field staticField = Typeface.class
                        .getDeclaredField("sSystemFontMap");
                staticField.setAccessible(true);
                staticField.set(null, newMap);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            try {
                final Field defaultFontTypefaceField = Typeface.class.getDeclaredField(defaultFontNameToOverride);
                defaultFontTypefaceField.setAccessible(true);
                defaultFontTypefaceField.set(null, customFontTypeface);
            } catch (Exception e) {
                // Log.e(TypefaceUtil.class.getSimpleName(), "Can not set custom font " + customFontFileNameInAssets + " instead of " + defaultFontNameToOverride);
            }
        }
    }

    public static void enterAnimation(Activity activity) {
        activity.overridePendingTransition(R.anim.animation_enter_from_right,
                R.anim.animation_leave_out_to_left);
    }

    public static void exitAnimation(Activity activity) {
        activity.overridePendingTransition(R.anim.exit_animation_enter_from_right,
                R.anim.exit_animation_leave_to_right);
    }

    public static void changeFragment(FragmentManager fragmentManager, Fragment targetFragment) {
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up,
                        R.anim.slide_in_up, R.anim.slide_out_up)
                .replace(R.id.main_sliding_container, targetFragment).addToBackStack("")
                .commit();
    }

    public static void addFragment(FragmentManager fragmentManager, Fragment targetFragment) {
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up,
                        R.anim.slide_in_up, R.anim.slide_out_up)
                .add(R.id.main_sliding_container, targetFragment).addToBackStack("")
                .commit();
    }

    public static void changeFragmentWithoutBackStack(FragmentManager fragmentManager, Fragment targetFragment) {
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up,
                        R.anim.slide_in_up, R.anim.slide_out_up)
                .replace(R.id.main_sliding_container, targetFragment)
                .commit();
    }

    public static void customToast(Context mContext, String message, int type) {
        View toastLayout = LayoutInflater.from(mContext).inflate(R.layout.layout_custom_toast, null);

        ImageView customImageView = toastLayout.findViewById(R.id.custom_toast_image);
        TextView customTextView = toastLayout.findViewById(R.id.custom_toast_message);
        CardView customCardView = toastLayout.findViewById(R.id.toast_cardView);

        /** {@link type} is used to define the behaviour of this custom Toast.
         * 0 is for ERROR or if SOMETHING WENT WRONG,
         * 1 is for SUCCESS or TRUE or if SOMETHING WENT RIGHT,
         * 2 is for INFORMATION or if SOMETHING ELSE HAPPENS. */

        customTextView.setText(message);

        switch (type) {
            case 0:
                customImageView.setImageResource(R.drawable.ic_error);
                customCardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorRed));
                break;

            case 1:
                customImageView.setImageResource(R.drawable.ic_tick);
                customCardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorGreen));
                break;

            case 2:
                customImageView.setImageResource(R.drawable.ic_information);
                customCardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
                break;

            default:
                break;
        }

        Toast toast = new Toast(mContext);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastLayout);
        toast.show();
    }

    public static Bitmap setBitmapPicture(String mCurrentPhotoPath) {
        // Get the dimensions of the View
        int targetW = 200;
        int targetH = 250;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
    }

    public static Spanned setFontAwesome(Context context, TextView textView, String unicode) {
        try {
            Typeface iconFont = FontAwesomeManager.getTypeface(context, FontAwesomeManager.FONTAWESOME);
            FontAwesomeManager.applyTypeFace(textView, iconFont);
            return Html.fromHtml("&#x" + unicode);
        } catch (Exception e) {
            e.printStackTrace();
            return new SpannableString("*");
        }
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static String generateDateTimeStamp() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
        Log.v("DATE_TIME_STAMP===>", df.format(c.getTime()));
        return df.format(c.getTime());
    }

    public static String accessFirebaseToken(Context context) {
        SharedPreferences pref = context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        String firebaseToken = pref.getString(Constants.FIREBASE_TOKEN, null);
        if (firebaseToken != null && !firebaseToken.isEmpty()) {
            Log.e(TAG, "Firebase Token: " + pref.getString(Constants.FIREBASE_TOKEN, null));
            return firebaseToken;
        } else {
            Log.e(TAG, "Firebase Token is not received yet!");
            return "";
        }
    }

    public static TextView embossTextView(TextView textView) {
        /** ===> MaskFilter <===
         * MaskFilter is the base class for object that perform transformations on an
         * alpha-channel mask before drawing it. A subclass of MaskFilter may be installed
         * into a Paint. Blur and emboss are implemented as subclasses of MaskFilter.*/

//       public EmbossMaskFilter (float[] direction, float ambient, float specular,
//           float blurRadius)
//
//       Create an emboss mask filter
//
//       Parameters
//           direction : array of 3 scalars [x, y, z] specifying the direction of the
//               light source
//           ambient : 0...1 amount of ambient light
//           specular : coefficient for specular highlights (e.g. 8)
//           blurRadius : amount to blur before applying lighting (e.g. 3)
//       Returns
//           the emboss mask filter

        EmbossMaskFilter filter = new EmbossMaskFilter(
                new float[]{0f, -1f, 0.5f}, // direction of the light source
                0.8f, // ambient light between 0 to 1
                13, // specular highlights
                7.0f // blur before applying lighting
        );

        // Set the TextView layer type to software
        textView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        // Finally, make the TextView text effect de-boss.
        textView.getPaint().setMaskFilter(filter);
        return textView;
    }

    public static String formatServerToUserDate(String serverDate) {
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = dt.parse(serverDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // * same for the format String below
        SimpleDateFormat dt1 = new SimpleDateFormat("MMM dd, yyyy");
        System.out.println(dt1.format(date));

        return dt1.format(date);
    }

    public static String formatServerDateAndTimeToUser(String dateTime) {
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = dt.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // * same for the format String below
        SimpleDateFormat dt1 = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");
        System.out.println(dt1.format(date));

        return dt1.format(date);
    }

    public static String formatTime24To12(String time) {
        SimpleDateFormat dt = new SimpleDateFormat("HH:mm:ss");
        Date date = null;
        try {
            date = dt.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // * same for the format String below
        SimpleDateFormat dt1 = new SimpleDateFormat("hh:mm:ss a");
        System.out.println(dt1.format(date));

        return dt1.format(date);
    }

    public static String formatTime12To24(String time) {
        SimpleDateFormat dt = new SimpleDateFormat("hh:mm:ss a");
        Date date = null;
        try {
            date = dt.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // * same for the format String below
        SimpleDateFormat dt1 = new SimpleDateFormat("HH:mm:ss");
        System.out.println(dt1.format(date));

        return dt1.format(date);
    }

    public static String formatUserDateToServer(String userDate) {
        SimpleDateFormat dt = new SimpleDateFormat("MMM dd, yyyy");
        Date date = null;
        try {
            date = dt.parse(userDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // * same for the format String below
        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println(dt1.format(date));

        return dt1.format(date);
    }

    public static double getFileSize(File file) {
        double bytes, kilobytes, megabytes = 0, gigabytes, terabytes, petabytes, exabytes, zettabytes, yottabytes;
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        if (file.exists()) {
            bytes = file.length();
            kilobytes = (bytes / 1024);
            megabytes = (kilobytes / 1024);
            gigabytes = (megabytes / 1024);
            terabytes = (gigabytes / 1024);
            petabytes = (terabytes / 1024);
            exabytes = (petabytes / 1024);
            zettabytes = (exabytes / 1024);
            yottabytes = (zettabytes / 1024);

            System.out.println("File size in BYTES : " + decimalFormat.format(bytes));
            System.out.println("File size in KILOBYTES : " + decimalFormat.format(kilobytes));
            System.out.println("File size in MEGABYTES : " + decimalFormat.format(megabytes));
            System.out.println("File size in GIGABYTES : " + decimalFormat.format(gigabytes));
            System.out.println("File size in TERABYTES : " + decimalFormat.format(terabytes));
            System.out.println("File size in PETABYTES : " + decimalFormat.format(petabytes));
            System.out.println("File size in EXABYTES : " + decimalFormat.format(exabytes));
            System.out.println("File size in ZETTABYTES : " + decimalFormat.format(zettabytes));
            System.out.println("File size in YOTTABYTES : " + decimalFormat.format(yottabytes));
        } else {
            System.out.println("File does not exists!");
        }

        return megabytes;
    }

    public static String failureMessage(Throwable t) {
        if (t instanceof TimeoutException || t instanceof ConnectException
                || t instanceof SocketException || t instanceof SocketTimeoutException) {
            return Constants.MESSAGE_CHECK_INTERNET;
        } else {
            return Constants.MESSAGE_SOMETHING_WENT_WRONG;
        }
    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retrieving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);

            Log.e("Package Name===> ", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.v("Key Hash===> ", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }

    //Calculating Distnace Between Current Latitude , Longitude to API Response Latitude , Longitude:-
    public static double calculationByDistance(double currentLatitude,
                                               double currentLongitude ,
                                               double apiResponseLatitude ,
                                               double apiResponseLongitude) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = currentLatitude;
        double lat2 = apiResponseLatitude;
        double lon1 = currentLongitude;
        double lon2 = apiResponseLongitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####.##");
        double kmInDec = Double.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        double meterInDec = Double.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Double.parseDouble(newFormat.format(Radius * c));
    }


    public static int convertDPtoPX(Activity activity, int DP) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float logicalDensity = metrics.density;
        return (int) Math.ceil(DP * logicalDensity);
    }

    public static void setToolbarTitle(Activity activity, String title) throws NullPointerException {
        ((AppCompatActivity) activity).getSupportActionBar().setTitle(title);
    }

    public static void setToolbarColor(Activity activity, String colorCode) throws NullPointerException {
        ((AppCompatActivity) activity).getSupportActionBar()
                .setBackgroundDrawable(new ColorDrawable(Color.parseColor(colorCode)));
    }

    public static String getComplaintDuration(long numOfDays, long numOfHours, long numOfMinutes) {
        if (numOfDays > 0) {
            return (int) numOfDays + " Day(s)";
        } else if (numOfHours > 0) {
            return (int) numOfHours + " Hour(s)";
        } else if (numOfMinutes > 0) {
            return (int) numOfMinutes + " Minute(s)";
        } else {
            return "Less than a minute !";
        }
    }

    public static DraweeController simpleDraweeController(String path, int width, int height) {
        return Fresco.newDraweeControllerBuilder()
                .setImageRequest(ImageRequestBuilder
                        .newBuilderWithSource(Uri.parse(path))
                        .setLocalThumbnailPreviewsEnabled(true)
                        .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                        .setProgressiveRenderingEnabled(false)
                        .setResizeOptions(new ResizeOptions(width, height))
                        .build())
                .build();
    }


    /* ====================== Image chooser intent method ====================== */
    public static Intent createChooserIntent(Activity activity, Uri uri) {
        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = activity.getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                activity.grantUriPermission(res.activityInfo.packageName, uri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (uri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
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

    public static Intent createChooserIntentNew(Activity activity, Uri uri) {
        List<Intent> allIntents = new ArrayList<>();

        PackageManager packageManager = activity.getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                activity.grantUriPermission(res.activityInfo.packageName, uri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            Intent intent1 = new Intent(captureIntent);
            intent1.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent1.setPackage(res.activityInfo.packageName);
            if (uri != null) {
                intent1.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            }
            allIntents.add(intent1);
        }
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

        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;

       // return intent1;
    }
    /* ================================================================================================= */
}
