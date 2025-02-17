package com.citizen.fmc.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.citizen.fmc.R;
import com.citizen.fmc.adapter.OptionsListAdapter;
import com.citizen.fmc.model.ImageTextModel;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class OptionListFragment extends Fragment {
    //Defining Views:-
    private ListView list_view;
    private List<ImageTextModel> optionModalList;
    private String NDMC_TOLL_FREE_NUMBER = "1533";
    String module_id = "";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.layout_list_view, container, false);
        list_view = mView.findViewById(R.id.list_view);
        module_id = Constants.getPrefrence(requireContext(),"complaint_module_id");
        prepareOptionLisData();
        return mView;
    }

    //ListOption Data:-
    private void prepareOptionLisData() {
        optionModalList = new ArrayList<>();
        optionModalList.clear();
        optionModalList.add(new ImageTextModel("Create New Complaint", R.drawable.ic_complaint_icon, R.drawable.ic_chevron_right_24dp, 0));
        optionModalList.add(new ImageTextModel("My Complaints", R.drawable.ic_view_icon, R.drawable.ic_chevron_right_24dp, 1));
        /*if (!module_id.equals("68") && !module_id.equals("69")) {
            optionModalList.add(new ImageTextModel("View All Complaints", R.drawable.ic_view_icon, R.drawable.ic_chevron_right_24dp, 4));
        }*/
        optionModalList.add(new ImageTextModel("Write Feedback", R.drawable.ic_feedback_icon, R.drawable.ic_chevron_right_24dp, 2));
        optionModalList.add(new ImageTextModel("MCF Toll Free Number", R.drawable.ic_helpline_icon, R.drawable.ic_chevron_right_24dp, 3));
        OptionsListAdapter adapter = new OptionsListAdapter(getActivity(), optionModalList);
        list_view.setAdapter(adapter);

        //List Item ClickEvent:-
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listClickEventPerform(optionModalList.get(position).getId());
            }
        });
    }

    private void listClickEventPerform(int id) {
        switch (id) {
            case 0:
                Utils.changeFragment(getFragmentManager(), new NewComplaintFragment());
                break;
            case 1:
                Utils.changeFragment(getFragmentManager(), new ViewMyComplaintsFragment());
                break;
            case 2:
              //  startActivity(new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:" + NDMC_TOLL_FREE_NUMBER)));
                Utils.changeFragment(getFragmentManager(), new DepartmentWiseFeedbackFragment());
                break;
            case 3:
              //  Utils.changeFragment(getFragmentManager(), new DepartmentWiseFeedbackFragment());
                startActivity(new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:" +
                        Constants.getPrefrence(requireContext(),"TOLL_FREE_NUMBER")/*NDMC_TOLL_FREE_NUMBER*/)));
                break;
            case 4:
                Utils.changeFragment(getFragmentManager(), new ViewAllComplaintsFragment());
                break;
            default:
                Toast.makeText(getActivity(), Constants.MESSAGE_SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show();
        }
    }
}
