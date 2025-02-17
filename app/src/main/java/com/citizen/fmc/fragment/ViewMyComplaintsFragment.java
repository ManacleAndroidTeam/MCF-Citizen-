package com.citizen.fmc.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.transition.TransitionManager;
import androidx.fragment.app.Fragment;
import androidx.core.view.MenuItemCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.citizen.fmc.R;
import com.citizen.fmc.adapter.AllCompListAdapter;
import com.citizen.fmc.adapter.ComplaintFilterListAdapter;
import com.citizen.fmc.model.AllComplaintsModel;
import com.citizen.fmc.model.ComplaintStatusModel;
import com.citizen.fmc.model.FeedbackOptionModel;
import com.citizen.fmc.utils.ConnectivityReceiver;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.OnCancelListener;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.OnItemClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ======> Created by dheeraj-gangwar on 2018-01-25 <======
 */

@SuppressLint("SetTextI18n, ResourceType")
public class ViewMyComplaintsFragment extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener {
    private List<AllComplaintsModel> allCompList;
    private List<ComplaintStatusModel> compStatusList;
    private Activity activity;
    private String TAG = getClass().getSimpleName();
    private boolean refreshAllComplaintsList = true;
    private ListView allCompLV;
    private DialogPlus dialogPlus;
    private AllCompListAdapter adapter;
    private int STATUS_ID_FIRST_INDEX = 1002;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView mSearchView;
    private static final String UNICODE_REFRESH = "f01e";
    private static final String UNICODE_DOUBLE_DOWN = "f103";
    private static final String UNICODE_EXCLAMATION = "f12a";
    private TextView swipeDownTextView;
    private TextView noDataTextView;
    private SpotsDialog spotsDialog;
    private int PAGE_NUMBER_INITIAL = 1;
    private int currentPage = PAGE_NUMBER_INITIAL;
    private FloatingActionButton complaintsFloatingActionButton;

    private ArrayList<FeedbackOptionModel> feedbackOptionModelList;
    private FeedbackOptionModel feedbackOptionModel;
    String statusStr = "false";
    String module_id = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_view_my_complaints, container, false);
        activity = getActivity();
        Utils.setToolbarTitle(activity, "Complaints");
        mSearchView = mView.findViewById(R.id.search_et);
        swipeRefreshLayout = mView.findViewById(R.id.swipe_refresh_layout);
        noDataTextView = mView.findViewById(R.id.no_data_text_view);
        swipeDownTextView = mView.findViewById(R.id.swipe_down_text_view);
        complaintsFloatingActionButton = mView.findViewById(R.id.complaints_fab);
        allCompLV = mView.findViewById(R.id.complaints_list_view);
        module_id = Constants.getPrefrence(requireContext(), "complaint_module_id");

        spotsDialog = new SpotsDialog(activity,
                getResources().getString(R.string.please_wait_dialog_text),
                R.style.CustomSpotsDialogStyle);
        spotsDialog.setCancelable(false);

        complaintsFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.changeFragment(getFragmentManager(), new NewComplaintFragment());
            }
        });

        swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.swipe_refresh_color_scheme));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                prepareAllCompDataList();
            }
        });


        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchComplaint(newText);
                return false;
            }
        });

        // Get the search close button image view
        ImageView closeButton = mSearchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        // Set on click listener
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchComplaint("");
                mSearchView.setQuery("", false);
            }
        });

        statusStr = Constants.getPrefrence(getActivity(), "countStatus");
        if (statusStr.equals("true")) {
            prepareAllCompDataList();
        } else {
            prepareAllCompDataList();
        }
        createFilterDialog();
        return mView;
    }

    /* ====================== Adding elements to List ====================== */
    private void prepareAllCompDataList() {
        spotsDialog.show();
        allCompLV.setAdapter(null);
        allCompList = new ArrayList<>();
        compStatusList = new ArrayList<>();

        getAllComplaints();
    }

    /* ====================== Request method to get user's all complaints ====================== */
    private void getAllComplaints() {
        String userToken = Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(activity);
        int userId = Utils.getUserDetails(activity).getCivilianId();
        Log.v(TAG, "USER_TOKEN: " + userToken);
        Log.v(TAG, "USER_ID: " + String.valueOf(userId));
        String status = "";
        String dept_id = "";
        if (module_id.equals("68")) {
            status = "1";
        } else if (module_id.equals("69")) {
            dept_id = "70";
        } else {
            status = "0";
        }

        Utils.getInterfaceService().getAllComplaints(userToken, Constants.HEADER_ACCEPT, userId, status, dept_id)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                            if (activity != null && isAdded()) {
                                if (response.body() != null) {
                                    JsonObject jsonObject = response.body();
                                    Log.d("TATTTYYGuy", jsonObject.toString());
                                    if (jsonObject.get("response").getAsBoolean()) {
                                        JsonArray allCompJsonArray = jsonObject.getAsJsonArray("data");

                                        // Adding all complaints to list
                                        if (allCompJsonArray.size() > 0) {
                                            for (int i = 0; i < allCompJsonArray.size(); i++) {
                                                AllComplaintsModel allComplaintsModel = new GsonBuilder().create()
                                                        .fromJson(allCompJsonArray.get(i), AllComplaintsModel.class);
                                                allCompList.add(allComplaintsModel);
                                            }
                                        }

                                        // Adding complaint status to list
                                        JsonArray compStatusJsonArray = jsonObject.getAsJsonArray("complaint_status");
                                        if (compStatusJsonArray.size() > 0) {
                                            // Adding an extra item to status list for remove filter, i.e., All
                                            compStatusList.add(new ComplaintStatusModel(STATUS_ID_FIRST_INDEX, "All"));
                                            for (int i = 0; i < compStatusJsonArray.size(); i++) {
                                                ComplaintStatusModel statusModel = new GsonBuilder().create()
                                                        .fromJson(compStatusJsonArray.get(i), ComplaintStatusModel.class);
                                                compStatusList.add(statusModel);
                                            }
                                        }

                                        // Adding feedbackOption list
                                        feedbackOptionModelList = new ArrayList<>();
                                        feedbackOptionModelList.clear();
                                        JsonArray feedbackOptionJsonArray = jsonObject.getAsJsonArray("feedback_option");
                                        if (feedbackOptionJsonArray.size() >= 0) {
                                            for (int i = 0; i < feedbackOptionJsonArray.size(); i++) {
                                                feedbackOptionModel = new GsonBuilder().create()
                                                        .fromJson(feedbackOptionJsonArray.get(i), FeedbackOptionModel.class);
                                                feedbackOptionModelList.add(feedbackOptionModel);
                                            }

                                        }

                                        setAllCompListViewAdapter();
                                        Constants.setPreferenceStringData(getActivity(), "countStatus", "false");

                                        Log.v(TAG, allCompList.toString());
                                        Log.v(TAG, compStatusList.toString());
                                        spotsDialog.dismiss();
                                        swipeRefreshLayout.setRefreshing(false);

//                            JsonObject jsonObject = response.body();
//                            if (jsonObject.get("response").getAsBoolean()) {
//                                JsonArray allCompJsonArray = jsonObject.getAsJsonArray("data");
//                                if (pageNum == PAGE_NUMBER_INITIAL) {
//                                    int TOTAL_RECORD = jsonObject.get("total_record").getAsInt();
//                                    int PER_PAGE_RECORD = jsonObject.get("per_page").getAsInt();
//                                    TOTAL_PAGES = TOTAL_RECORD / PER_PAGE_RECORD;
//                                    if (TOTAL_RECORD % PER_PAGE_RECORD > 0) {
//                                        TOTAL_PAGES = TOTAL_PAGES + 1;
//                                    }
//
//                                    // Adding complaint status to list
//                                    JsonArray compStatusJsonArray = jsonObject.getAsJsonArray("complaint_status");
//                                    if (compStatusJsonArray.size() > 0) {
//                                        for (int i = 0; i < compStatusJsonArray.size(); i++) {
//                                            ComplaintStatusModel statusModel = new GsonBuilder().create()
//                                                    .fromJson(compStatusJsonArray.get(i), ComplaintStatusModel.class);
//                                            compStatusList.add(statusModel);
//                                        }
//                                        // Adding an extra item to status list for remove filter, i.e., All
//                                        compStatusList.add(new ComplaintStatusModel(STATUS_ID_FIRST_INDEX, "All"));
//                                    }
//                                }
//
//                                // Adding all complaints to list
//                                List<AllComplaintsModel> list = new ArrayList<>();
//                                if (allCompJsonArray.size() > 0) {
//                                    for (int i = 0; i < allCompJsonArray.size(); i++) {
//                                        AllComplaintsModel allComplaintsModel = new GsonBuilder().create()
//                                                .fromJson(allCompJsonArray.get(i), AllComplaintsModel.class);
//                                        list.add(allComplaintsModel);
//                                    }
//                                    allCompList.addAll(list);
//                                }
//
//                                Log.v(TAG, allCompList.toString());
//                                Log.v(TAG, compStatusList.toString());
//                                swipeRefreshLayout.setRefreshing(false);
//                                swipeDownTextView.setVisibility(View.GONE);
//                                setAllCompListViewAdapter();
                                    } else {
                                        swipeDownTextView.setVisibility(View.GONE);
                                        refreshAllComplaintsList = true;
                                        spotsDialog.dismiss();
                                        swipeRefreshLayout.setRefreshing(false);
                                        Utils.showSnackBar(activity, complaintsFloatingActionButton,
                                                "No complaints found !!!", false);
                                    }
                                } else {
                                    swipeDownTextView.setVisibility(View.VISIBLE);
                                    refreshAllComplaintsList = true;
                                    spotsDialog.dismiss();
                                    swipeRefreshLayout.setRefreshing(false);
                                    Utils.showSnackBar(activity, complaintsFloatingActionButton,
                                            Constants.MESSAGE_NO_DATA_FOUND, false);
                                }
                            }
                        } catch (Exception e) {
                            swipeDownTextView.setVisibility(View.VISIBLE);
                            refreshAllComplaintsList = true;
                            spotsDialog.dismiss();
                            swipeRefreshLayout.setRefreshing(false);
                            e.printStackTrace();
                            Utils.showSnackBar(activity, complaintsFloatingActionButton,
                                    Constants.MESSAGE_SOMETHING_WENT_WRONG, false);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        if (activity != null && isAdded()) {
                            swipeDownTextView.setVisibility(View.VISIBLE);
                            spotsDialog.dismiss();
                            swipeRefreshLayout.setRefreshing(false);
                            refreshAllComplaintsList = true;
                            call.cancel();
                            t.printStackTrace();
                            Utils.showSnackBar(activity,
                                    complaintsFloatingActionButton, Utils.failureMessage(t), false);
                        }
                    }
                });
    }
    /* ================================================================================================= */


    /* ====================== Setting all complaints adapter to ListView ====================== */
    private void setAllCompListViewAdapter() {
        adapter = new AllCompListAdapter(activity, getFragmentManager(), allCompList, compStatusList, feedbackOptionModelList);
        allCompLV.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
//        createFilterDialog();
        // complaintListViewItemClick();
        refreshAllComplaintsList = false;
        swipeDownTextView.setVisibility(View.GONE);
        spotsDialog.dismiss();
    }
    /* ================================================================================================= */


    /* ====================== Creating filter dialog for toolbar ====================== */
    private void createFilterDialog() {
        try {
            // Setting adapter to the filter list
            ComplaintFilterListAdapter filterListAdapter = new ComplaintFilterListAdapter(activity, compStatusList);
            filterListAdapter.notifyDataSetChanged();

            // Creating Filter Dialog
            dialogPlus = DialogPlus.newDialog(activity)
                    .setAdapter(filterListAdapter)
                    .setCancelable(true)
                    .setGravity(Gravity.TOP)
                    .setContentHolder(new ListHolder())
                    .setHeader(R.layout.layout_header_filter_dialog)
                    .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                    .setMargin(0, 60, 0, 0)
                    .setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                            // Creating a list for selected filter item
                            List<AllComplaintsModel> list = new ArrayList<>();

                            // Checking filtered item whether it is All or anything else
                            if (compStatusList.get(position).getStatusId() != STATUS_ID_FIRST_INDEX) {
                                for (int i = 0; i < allCompList.size(); i++) {
                                    // Adding filtered item to list and setting adapter
                                    if (allCompList.get(i).getCurrentStatus() == compStatusList.get(position).getStatusId()) {
                                        list.add(allCompList.get(i));
                                    }
                                }
                                adapter = new AllCompListAdapter(activity, getFragmentManager(), list, compStatusList, feedbackOptionModelList);
                            } else {
                                // Selected filtered item is All
                                adapter = new AllCompListAdapter(activity, getFragmentManager(), allCompList, compStatusList, feedbackOptionModelList);
                            }
                            allCompLV.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            dialog.dismiss();
                            // complaintListViewItemClick();
                        }
                    })
                    .setOnDismissListener(new OnDismissListener() {
                        @Override
                        public void onDismiss(DialogPlus dialog) {

                        }
                    })
                    .setOnCancelListener(new OnCancelListener() {
                        @Override
                        public void onCancel(DialogPlus dialog) {

                        }
                    })
                    .setOnBackPressListener(new OnBackPressListener() {
                        @Override
                        public void onBackPressed(DialogPlus dialogPlus) {

                        }
                    })
                    .create();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    /* ================================================================================================= */


    /* ====================== ListView onItemClickListener ====================== */
//    private void complaintListViewItemClick() {
//        allCompLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                AllComplaintsModel _item = (AllComplaintsModel) parent.getItemAtPosition(position);
//                NewComplaintFragment complaintFragment = new NewComplaintFragment();
//                Bundle bundle = new Bundle();
//                bundle.putString(Constants.KEY_COMPLAINT_PAGE_TYPE, Constants.VALUE_SHOW_COMPLAINT);
//                bundle.putSerializable(Constants.KEY_COMPLAINT_DATA, _item);
//                for (int i = 0; i < compStatusList.size(); i++) {
//                    if (_item.getCurrentStatus() == compStatusList.get(i).getStatusId()) {
//                        bundle.putString(Constants.KEY_STATUS_TITLE, compStatusList.get(i).getStatusName().trim());
//                        bundle.putString(Constants.KEY_STATUS_COLOR_CODE, compStatusList.get(i).getStatusColorCode().toUpperCase().trim());
//                        break;
//                    }
//                }
//                complaintFragment.setArguments(bundle);
//                Utils.changeFragment(getFragmentManager(), complaintFragment);
//            }
//        });
//    }
    /* ================================================================================================= */


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_filter).setVisible(true);
        menu.findItem(R.id.action_notification).setVisible(false);
        menu.findItem(R.id.date_filter).setVisible(true);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);

        final SearchView searchView;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
            if (searchView != null) {
                if (searchManager != null) {
                    searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
                    searchView.setQueryHint("Search here...");


                    // Get the search close button image view
                    ImageView closeButton = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);

                    // Set on click listener
                    closeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            searchView.setQuery("", false);
                        }
                    });

                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            if (adapter != null) {
                                // Performing search
                                adapter.getFilter().filter(newText);
                                // complaintListViewItemClick();
                            }
                            return false;
                        }
                    });
                }
            }
        }
    }

    private void searchComplaint(String character) {
        if (adapter != null) {
            if (!character.isEmpty()) {
                adapter.getFilter().filter(character);
                // complaintListViewItemClick();
            } else {
                // Selected filtered item is All
                adapter = new AllCompListAdapter(activity, getFragmentManager(), allCompList, compStatusList, feedbackOptionModelList);
                // complaintListViewItemClick();
                allCompLV.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.action_search) {
            TransitionManager.beginDelayedTransition((ViewGroup) activity.findViewById(R.id.toolbar));
            MenuItemCompat.expandActionView(item);
            return true;
        } else if (item.getItemId() == R.id.action_filter) {
            dialogPlus.show();
            return true;
        } else if (item.getItemId() == R.id.date_filter) {
            showDateFilterDialog();
            Log.d("DATE SELECTED", "onOptionsItemSelected: DATE SELECTED");
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void showDateFilterDialog() {
        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(activity);
        View dialogView = inflater.inflate(R.layout.dialog_date_filter, null);

        Dialog custDialog= new Dialog(activity);
        custDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        custDialog.setContentView(dialogView);

        // Find views in the custom layout
        TextView textToday = dialogView.findViewById(R.id.text_today);
        TextView textYesterday = dialogView.findViewById(R.id.text_yesterday);
        TextView textLastWeek = dialogView.findViewById(R.id.text_last_week);
        TextView textLastMonth = dialogView.findViewById(R.id.text_last_month);
        TextView textThisMonth = dialogView.findViewById(R.id.text_this_month);
        TextView textAll = dialogView.findViewById(R.id.text_all);
        ImageView closeIv = dialogView.findViewById(R.id.close_icon);


        closeIv.setOnClickListener(v -> custDialog.dismiss());

        // Set click listeners for each option
        textToday.setOnClickListener(v -> {
            filterComplaintsByDate("today");
            custDialog.dismiss();
        });

        textYesterday.setOnClickListener(v -> {
            filterComplaintsByDate("yesterday");
            custDialog.dismiss();
        });

        textLastWeek.setOnClickListener(v -> {
            filterComplaintsByDate("last_week");
            custDialog.dismiss();
        });

        textLastMonth.setOnClickListener(v -> {
            filterComplaintsByDate("last_month");
            custDialog.dismiss();
        });

        textThisMonth.setOnClickListener(v -> {
            filterComplaintsByDate("this_month");
            custDialog.dismiss();
        });

        textAll.setOnClickListener(v -> {
            filterComplaintsByDate("all");
            custDialog.dismiss();
        });

        custDialog.show();

        Window window = custDialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams layoutParams = window.getAttributes();

            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            layoutParams.horizontalMargin = 0;
            layoutParams.verticalMargin = 0;
            window.setAttributes(layoutParams);
        }
    }


    private void filterComplaintsByDate(String filterOption) {
        List<AllComplaintsModel> filteredList = new ArrayList<>();

        // Get the current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(calendar.getTime());

        // Adjust the calendar based on the filter option
        switch (filterOption) {
            case "today":
//                for (AllComplaintsModel complaint : allCompList) {
//                    if (complaint.getComplaintDate().equals(today)) {
//                        filteredList.add(complaint);
//                    }
//                }

                Toast.makeText(getContext(),  "Today's date: " + today, Toast.LENGTH_LONG).show();

                break;

            case "yesterday":
                calendar.add(Calendar.DAY_OF_YEAR, -1);
                String yesterday = sdf.format(calendar.getTime());
                Toast.makeText(getContext(),  "Yesterday's date: " + yesterday, Toast.LENGTH_LONG).show();
                break;

            case "last_week":
                calendar.add(Calendar.DAY_OF_YEAR, -7);
                String lastWeekStart = sdf.format(calendar.getTime());
                Toast.makeText(getContext(), "Last Week: " + lastWeekStart, Toast.LENGTH_LONG).show();
                break;

            case "last_month":
                calendar.add(Calendar.MONTH, -1);
                String lastMonthStart = sdf.format(calendar.getTime());
                Toast.makeText(getContext(), "Last Month: " + lastMonthStart, Toast.LENGTH_LONG).show();

//                for (AllComplaintsModel complaint : allCompList) {
//                    if (complaint.getComplaintDate().compareTo(lastMonthStart) >= 0 && complaint.getComplaintDate().compareTo(today) <= 0) {
//                        filteredList.add(complaint);
//                    }
//                }
                break;

            case "this_month":
                calendar.set(Calendar.DAY_OF_MONTH, 1); // Set to the first day of the current month
                String thisMonthStart = sdf.format(calendar.getTime());
                Toast.makeText(getContext(), "This month: " + thisMonthStart, Toast.LENGTH_LONG).show();

//                for (AllComplaintsModel complaint : allCompList) {
//                    if (complaint.getComplaintDate().compareTo(thisMonthStart) >= 0 && complaint.getComplaintDate().compareTo(today) <= 0) {
//                        filteredList.add(complaint);
//                    }
//                }
                break;

            case "all":
                filteredList = new ArrayList<>(allCompList); // Show all complaints
                break;
        }

        // Update the adapter with the filtered list
        adapter = new AllCompListAdapter(activity, getFragmentManager(), filteredList, compStatusList, feedbackOptionModelList);
        allCompLV.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        prepareAllCompDataList();
    }
}
