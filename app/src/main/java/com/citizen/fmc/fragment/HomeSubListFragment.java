package com.citizen.fmc.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.citizen.fmc.R;
import com.citizen.fmc.activity.TrafficMapActivity;
import com.citizen.fmc.activity.WebViewActivity;
import com.citizen.fmc.adapter.HomeSubListAdapter;
import com.citizen.fmc.model.DrawerListItemModel;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * ======> Created by dheeraj-gangwar on 2018-04-18 <======
 */

public class HomeSubListFragment extends Fragment {
    private Activity activity;
    private ListView subItemListView;
    private int MODULE_ID;
    private String TAG = getClass().getSimpleName();
    private String Module_name;
    String language_code="";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.layout_list_view, container, false);
        activity = getActivity();
        subItemListView = mView.findViewById(R.id.list_view);
        final String toolbarTitle = getArguments().getString(Constants.KEY_TOOL_BAR_TITLE);
        Utils.setToolbarTitle(activity, toolbarTitle);
        language_code = Constants.getPrefrence(getContext(),"locale");
        if (!TextUtils.isEmpty(language_code))
        {
            setLocale(language_code);
        }

        MODULE_ID = getArguments().getInt(Constants.KEY_MODULE_ID, Constants.DEFAULT_INTEGER_VALUE);
        Module_name = getArguments().getString(Constants.KEY_MODULE_NAME);
        Log.v(TAG, Constants.KEY_MODULE_ID + ": " + MODULE_ID);

        final ArrayList<DrawerListItemModel> itemModels = getArguments().getParcelableArrayList(Constants.KEY_COMPLAINT_DATA);


        if (itemModels != null) {
            HomeSubListAdapter adapter = new HomeSubListAdapter(activity, itemModels);
            subItemListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            subItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Whats Near Me sub list on itemClick
                    if (MODULE_ID == Constants.MODULE_ID_WHATS_NEAR_ME) {
                        WhatsNearMeSubModuleFragment whatsNearMeSubModuleFragment = new WhatsNearMeSubModuleFragment();
                        Bundle mBundle = new Bundle();
                        mBundle.putString(Constants.KEY_TOOL_BAR_TITLE, itemModels.get(position).getModuleTitleName());
                        mBundle.putInt(Constants.KEY_MODULE_ID, itemModels.get(position).getModuleId());
                        whatsNearMeSubModuleFragment.setArguments(mBundle);
                        Utils.changeFragment(getFragmentManager(), whatsNearMeSubModuleFragment);
                    }


                    else if (MODULE_ID == Constants.MODULE_ID_WHATS_APP) {
                        if (itemModels.get(position).getModuleTitleName().equalsIgnoreCase("Email")) {
                            String mailid = itemModels.get(position).getModuleURL();
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setType("message/rfc822");
                            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                            intent.putExtra(Intent.EXTRA_SUBJECT, "");
                            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mailid});
                            //intent.putExtra(Intent.EXTRA_CC,"deepak@gmail.com");
                            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                activity.startActivity(Intent.createChooser(intent, "Send email..."));
                            }


                        } else if (itemModels.get(position).getModuleTitleName().replace("(+91858888773)", "").equalsIgnoreCase("Whatsapp")) {
                            String phone = itemModels.get(position).getModuleURL().replace("+", "").trim();
                            PackageManager pm = getActivity().getPackageManager();
                            try {
                                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                                String whatsAppWebUrl = "https://api.whatsapp.com/send?phone=" + phone + "&text=" + URLEncoder.encode("Type here...", "UTF-8");
                                sendIntent.setPackage("com.whatsapp");
                                sendIntent.setData(Uri.parse(whatsAppWebUrl));
                                if(sendIntent.resolveActivity(pm) != null){
                                    startActivity(sendIntent);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(), "WhatsApp is not Installed !", Toast.LENGTH_LONG).show();

                            }


                            //================

                                /* PackageManager packageManager = activity.getPackageManager();
                            Intent i = new Intent(Intent.ACTION_SEND);

                            try {
                                String url = "https://api.whatsapp.com/send?phone="+ phone +"&text=" + URLEncoder.encode("hello", "UTF-8");
                                i.setType("text/plain");
                                i.setPackage("com.whatsapp");
                                i.setData(Uri.parse(url));
                                 i.putExtra(Intent.EXTRA_TEXT, "NDMC");
                               // if (i.resolveActivity(packageManager) != null) {
                                    activity.startActivity(i);
                               // }
                            } catch (Exception e){
                                e.printStackTrace();
                            }*/

                            /* Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                            whatsappIntent.setType("text/plain");
                            whatsappIntent.setPackage("com.whatsapp");
                            whatsappIntent.putExtra(Intent.EXTRA_TEXT, "NDMC");
                            try {
                                activity.startActivity(whatsappIntent);
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(activity,"Whatsapp have not been installed.",Toast.LENGTH_LONG).show();
                            }*/

                        } else if (itemModels.get(position).getModuleTitleName().equalsIgnoreCase("Write Feedback")) {
                            // toolbarTitle ="";
                            //  toolbar.setTitle(Constants.TOOLBAR_TITLE_FEEDBACK);
                            Utils.setToolbarTitle(activity, Constants.TOOLBAR_TITLE_FEEDBACK);
                            Utils.changeFragment(getFragmentManager(), new DepartmentWiseFeedbackFragment());

                        } else if (itemModels.get(position).getModuleURL().equalsIgnoreCase("1533")) {
                            // Module_name.equalsIgnoreCase("NDMC Toll Free Number-1533");
                            startActivity(new Intent(Intent.ACTION_DIAL)
                                    .setData(Uri.parse("tel:" + itemModels.get(position).getModuleURL())));

                        }
                        else {
                            String language_code =  Constants.getPrefrence(getContext(),"locale");
                            if (!TextUtils.isEmpty(language_code) && language_code.equalsIgnoreCase("en")) {

                                String moduleTitleName = itemModels.get(position).getModuleTitleName();
                                int m_id = itemModels.get(position).getModuleId();
                                String titleName = "";
                                Intent intent = new Intent(getActivity() , WebViewActivity.class);
                                intent.putExtra(Constants.KEY_WEB_VIEW_URL, itemModels.get(position).getModuleURL());
                                intent.putExtra(Constants.KEY_TOOL_BAR_TITLE, itemModels.get(position).getModuleTitleName());
//                            intent.putExtra("m_id",String.valueOf(m_id));

                                Log.d("ModuleTitle",m_id + " " + moduleTitleName);
                                startActivity(intent);
                            }
                            else
                            {
                                String titleHindi = itemModels.get(position).getTitleHindi();
                                if (!TextUtils.isEmpty(titleHindi))
                                {
                                    String moduleTitleName = itemModels.get(position).getModuleTitleName();
                                    int m_id = itemModels.get(position).getModuleId();
                                    String titleName = "";
                                    Intent intent = new Intent(getActivity() , WebViewActivity.class);
                                    intent.putExtra(Constants.KEY_TOOL_BAR_TITLE, itemModels.get(position).getModuleTitleName());
                                    intent.putExtra(Constants.KEY_WEB_VIEW_URL, itemModels.get(position).getModuleURL());
//                            intent.putExtra("m_id",String.valueOf(m_id));

                                    Log.d("ModuleTitle",m_id + " " + moduleTitleName);
                                    startActivity(intent);
                                }
                                else {
                                    String moduleTitleName = itemModels.get(position).getModuleTitleName();
                                    int m_id = itemModels.get(position).getModuleId();
                                    String titleName = "";
                                    Intent intent = new Intent(getActivity(), WebViewActivity.class);
                                    intent.putExtra(Constants.KEY_TOOL_BAR_TITLE, itemModels.get(position).getTitleHindi());
                                    intent.putExtra(Constants.KEY_WEB_VIEW_URL, itemModels.get(position).getModuleURL());
//                            intent.putExtra("m_id",String.valueOf(m_id));

                                    Log.d("ModuleTitle", m_id + " " + moduleTitleName);
                                    startActivity(intent);
                                }
                            }
                        }

                    } else if (itemModels.get(position).getModuleId() == Constants.MODULE_ID_TRAFIC) {
                        /*Uri navigationIntentUri = Uri.parse("google.navigation:q=" + 12f +"," + 2f);//creating intent with latlng
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);*/

                      /*  Uri.Builder builder = new Uri.Builder();
                        builder.scheme("https")
                                .authority("www.google.com").appendPath("maps").appendPath("dir").appendPath("").appendQueryParameter("api", "1")
                                .appendQueryParameter("destination", 80.00023 + "," + 13.0783);
                        String url = builder.build().toString();
                        Log.d("Directions", url);
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);*/

                        //Intent intent = new Intent(getActivity(),MapsActivity.class);
                        Intent intent = new Intent(getActivity(), TrafficMapActivity.class);
                        startActivity(intent);
                    } else if (itemModels.get(position).getModuleId() == Constants.MODULE_ID_SMARTCITYPARKING) {
                       /* Intent intent = new Intent(getActivity(),ParkingSummaryActivity.class);
                        startActivity(intent);*/

                        Utils.customToast(getApplicationContext(),
                                Constants.MESSAGE_ADDED_SOON, 1);
                    }
                    // other modules
                    else {
                        String language_code =  Constants.getPrefrence(getContext(),"locale");
                        if (!TextUtils.isEmpty(language_code) && language_code.equalsIgnoreCase("en")) {
                            Intent intent = new Intent(getActivity(), WebViewActivity.class);
                            intent.putExtra(Constants.KEY_TOOL_BAR_TITLE, itemModels.get(position).getModuleTitleName());
                            intent.putExtra(Constants.KEY_WEB_VIEW_URL, itemModels.get(position).getModuleURL());
                            startActivity(intent);
                        }
                        else
                        {
                            String hindi_url = itemModels.get(position).getHindiURL();
                            if (!TextUtils.isEmpty(hindi_url))
                            {
                                Intent intent = new Intent(getActivity() , WebViewActivity.class);
                                intent.putExtra(Constants.KEY_TOOL_BAR_TITLE, itemModels.get(position).getTitleHindi());
                                intent.putExtra(Constants.KEY_WEB_VIEW_URL, itemModels.get(position).getHindiURL());
                                startActivity(intent);
                            }
                            else
                            {

                                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                                intent.putExtra(Constants.KEY_TOOL_BAR_TITLE, itemModels.get(position).getModuleTitleName());
                                intent.putExtra(Constants.KEY_WEB_VIEW_URL, itemModels.get(position).getModuleURL());
                                startActivity(intent);
                            }

                        }
                    }
                }
            });
        }
        return mView;
    }

    public void setLocale( String language_code) {
//        Resources res =  this.getResources();
        // Change locale settings in the app.
//        DisplayMetrics dm = res.getDisplayMetrics();
//        android.content.res.Configuration conf = res.getConfiguration();
//        conf.setLocale(new Locale(language_code.toLowerCase())); // API 17+ only.
// Use conf.locale = new Locale(...) if targeting lower versions
//        res.updateConfiguration(conf, dm);
        Locale myLocale = new Locale(language_code);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
//        Intent refresh = new Intent(getApplicationContext(),getClass());
////        finish();
//        startActivity(refresh);

    }
}
