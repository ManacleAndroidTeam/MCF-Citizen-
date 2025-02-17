package com.citizen.fmc.fragment;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.transition.TransitionManager;
import androidx.fragment.app.Fragment;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.citizen.fmc.R;
import com.citizen.fmc.adapter.ViewAllComplaintAdapter;
import com.citizen.fmc.adapter.ViewComplaintFilterAdapter;
import com.citizen.fmc.model.ViewAllComplaintStatusModel;
import com.citizen.fmc.model.ViewAllComplaintsModel;
import com.citizen.fmc.utils.ApiInterface;
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

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAllComplaintsFragment extends Fragment implements
        ConnectivityReceiver.ConnectivityReceiverListener {

    private List<ViewAllComplaintsModel> allCompList;
    private List<ViewAllComplaintStatusModel> compStatusList;
    private Activity activity;
    private String TAG = getClass().getSimpleName();
    private RecyclerView main_recycler_complaint;
    private DialogPlus dialogPlus;
    private ViewAllComplaintAdapter adapter;
    private int STATUS_ID_FIRST_INDEX = 1002;
    private SearchView mSearchView;
    private TextView noDataTextView;
    private SpotsDialog spotsDialog;
    private int PAGE_NUMBER_INITIAL = 1;
    private int currentPage = PAGE_NUMBER_INITIAL;
    private FloatingActionButton complaintsFloatingActionButton;
    private int nextPage;
    private int nextPageCount = 1;
    private int complaintStatus = 0;
    private int totalPage;
    private int totalFeedbackCount;
    private boolean hasMorePage;
    private LinearLayoutManager manager;
    private int scrollOutItems;
    private boolean isLoadingData = false;
    private int currentItems;
    private int totalItems;
    private ProgressBar progress_bar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_view_all_complaints, container, false);
        activity = getActivity();
        Utils.setToolbarTitle(activity, "View All Complaints");
        manager = new LinearLayoutManager(activity);
        mSearchView = mView.findViewById(R.id.search_et);
        complaintsFloatingActionButton = mView.findViewById(R.id.complaints_fab);
        main_recycler_complaint = mView.findViewById(R.id.main_recycler_complaint);
        progress_bar = mView.findViewById(R.id.progress_bar);
        allCompList = new ArrayList<>();
        compStatusList = new ArrayList<>();

        //ProgressBar SetUp:-
        progress_bar.setIndeterminate(false);
        progress_bar.setVisibility(View.VISIBLE);

        spotsDialog = new SpotsDialog(activity, getResources().getString(R.string.please_wait_dialog_text), R.style.CustomSpotsDialogStyle);
        spotsDialog.setCancelable(false);

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

        getModules(0, complaintStatus);
        setAllCompListViewAdapter();
        createFilterDialog();
        //OnScrollListener in RecyclerView to Load More Data:-
        main_recycler_complaint.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isLoadingData = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollOutItems = manager.findFirstVisibleItemPosition();

                if (isLoadingData && (currentItems + scrollOutItems == totalItems) && hasMorePage) {
                    isLoadingData = false;
                    progress_bar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadMoreData();
                        }
                    }, 2000);
                }
            }
        });
        return mView;
    }

    private void loadMoreData() {

        allCompList.clear();
        compStatusList.clear();
        List<ViewAllComplaintsModel> allComplaintsModels = getModules(nextPageCount, complaintStatus);
        allComplaintsModels.size();
        allComplaintsModels.clear();
        adapter.addAll(allComplaintsModels);
        progress_bar.setVisibility(View.GONE);
    }

    /* ====================== Setting all complaints adapter to ListView ====================== */
    private void setAllCompListViewAdapter() {

        allCompList.clear();
        compStatusList.clear();
        adapter = new ViewAllComplaintAdapter(activity, getFragmentManager(), allCompList, compStatusList);
        main_recycler_complaint.setLayoutManager(manager);
        main_recycler_complaint.setItemAnimator(new DefaultItemAnimator());
        main_recycler_complaint.addItemDecoration(new DividerItemDecoration(activity, manager.getOrientation()));
        main_recycler_complaint.setAdapter(adapter);
        spotsDialog.dismiss();
    }
    /* ================================================================================================= */

    /* ====================== Request method to get user's all complaints ====================== */
    private List<ViewAllComplaintsModel> getModules(int pageCount, int complaintStatus) {
        progress_bar.setVisibility(View.VISIBLE);
        allCompList.clear();
        compStatusList.clear();
        ApiInterface mApiInterface = Utils.getInterfaceService();
        String userToken = Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(activity);
        int userId = Utils.getUserDetails(activity).getCivilianId();
        Log.v(TAG, "USER_TOKEN: " + userToken);
        Log.v(TAG, "USER_ID: " + String.valueOf(userId));

        Call<JsonObject> mService = mApiInterface.getAllComplaintsCivilian(
                Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(activity),
                Constants.HEADER_ACCEPT, userId, "", "", complaintStatus, pageCount);
        mService.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    if (activity != null && isAdded()) {
                        if (response.body() != null) {
                            JsonObject jsonObject = response.body();
                            JsonArray allCompJsonArray = jsonObject.getAsJsonArray("data");
                            JsonArray compStatusJsonArray = jsonObject.getAsJsonArray("complaint_status");
                            if (jsonObject.get("response").getAsBoolean()) {
                                int totalCount = jsonObject.get("total_record").getAsInt();
                                nextPage = jsonObject.get("nextPage").getAsInt();
                                totalPage = jsonObject.get("lastPage").getAsInt();
                                hasMorePage = jsonObject.get("hasMorePages").getAsBoolean();
                                nextPageCount = nextPage;
                                // Adding all complaints to list
                                if (allCompJsonArray.size() > 0) {
                                    for (int i = 0; i < allCompJsonArray.size(); i++) {
                                        ViewAllComplaintsModel allComplaintsModel = new GsonBuilder().create()
                                                .fromJson(allCompJsonArray.get(i), ViewAllComplaintsModel.class);
                                        allCompList.add(allComplaintsModel);
                                    }
                                }
                                // Adding complaint status to list
                                if (compStatusJsonArray.size() > 0) {
                                    // Adding an extra item to status list for remove filter, i.e., All
                                    compStatusList.add(new ViewAllComplaintStatusModel(STATUS_ID_FIRST_INDEX, "All"));
                                    for (int i = 0; i < compStatusJsonArray.size(); i++) {
                                        ViewAllComplaintStatusModel statusModel = new GsonBuilder().create()
                                                .fromJson(compStatusJsonArray.get(i), ViewAllComplaintStatusModel.class);
                                        compStatusList.add(statusModel);
                                    }
                                }
                                //Adding Data to Adapter and notify it also showing Search and Map Layout view:-

                                adapter.addAll(allCompList);
                                adapter.notifyDataSetChanged();
                                Log.v(TAG, allCompList.toString());
                                progress_bar.setVisibility(View.GONE);
                                Constants.customToast(getActivity(), String.valueOf("Total Record " + totalCount), 2);

                            } else {
                                progress_bar.setVisibility(View.GONE);
                                Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                        jsonObject.get("message").getAsString(), false);
                            }
                        } else {
                            progress_bar.setVisibility(View.GONE);
                            Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                    Constants.MESSAGE_TRY_AGAIN_LATER, false);
                        }
                    }
                } catch (Exception e) {
                    progress_bar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                            Constants.MESSAGE_SOMETHING_WENT_WRONG, false);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (activity != null && isAdded()) {
                    progress_bar.setVisibility(View.GONE);
                    call.cancel();
                    t.printStackTrace();
                    Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                            Utils.failureMessage(t), false);
                }
            }
        });

        return allCompList;
    }
    /* ================================================================================================= */

    /* ====================== Creating filter dialog for toolbar ====================== */
    private void createFilterDialog() {
        try {
            // Setting adapter to the filter list
            ViewComplaintFilterAdapter filterListAdapter = new ViewComplaintFilterAdapter(activity, compStatusList);
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
                            List<ViewAllComplaintsModel> list = new ArrayList<>();
                            // Checking filtered item whether it is All or anything else
                            if (compStatusList.get(position).getId() != STATUS_ID_FIRST_INDEX) {
                                for (int i = 0; i < allCompList.size(); i++) {
                                    // Adding filtered item to list and setting adapter
                                    if (allCompList.get(i).getCurrentStatus() == compStatusList.get(position).getId()) {
                                        list.add(allCompList.get(i));
                                    }
                                }
                                list.clear();
                                adapter = new ViewAllComplaintAdapter(activity, getFragmentManager(), list, compStatusList);
                                complaintStatus = compStatusList.get(position).getId();
                                getModules(nextPageCount, complaintStatus);

                            } else {
                                // Selected filtered item is All
                                adapter = new ViewAllComplaintAdapter(activity, getFragmentManager(), allCompList, compStatusList);

                            }
                            // in case of set filter by Complaintstatus
                            main_recycler_complaint.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            dialog.dismiss();

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_filter).setVisible(true);
        menu.findItem(R.id.action_notification).setVisible(false);
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
            } else {
                adapter = new ViewAllComplaintAdapter(activity, getFragmentManager(), allCompList, compStatusList);
                main_recycler_complaint.setAdapter(adapter);
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
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }
}
