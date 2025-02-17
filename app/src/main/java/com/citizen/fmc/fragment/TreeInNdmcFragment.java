package com.citizen.fmc.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.citizen.fmc.R;
import com.citizen.fmc.adapter.TreeListAdapter;
import com.citizen.fmc.model.TreeInNdmcTextModel;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class TreeInNdmcFragment extends Fragment {
    //Defining Views:-
    private ListView list_view;
    private List<TreeInNdmcTextModel> optionModalList;
    private String NDMC_TOLL_FREE_NUMBER = "1533";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.layout_list_view, container, false);
        list_view = mView.findViewById(R.id.list_view);
        prepareOptionLisData();
        return mView;
    }

    //ListOption Data:-
    private void prepareOptionLisData() {
        optionModalList = new ArrayList<>();
        optionModalList.clear();
        optionModalList.add(new TreeInNdmcTextModel("Tree Near By Me", R.drawable.ic_complaint_icon, R.drawable.ic_chevron_right_24dp, 0));
        optionModalList.add(new TreeInNdmcTextModel("QR Near By Me", R.drawable.ic_view_icon, R.drawable.ic_chevron_right_24dp, 1));
        TreeListAdapter adapter = new TreeListAdapter(getActivity(), optionModalList);
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
                Utils.changeFragment(getFragmentManager(), new NearByTreeFragment());
                break;
          /*  case 1:
                Utils.changeFragment(getFragmentManager(), new ViewMyComplaintsFragment());
                break;*/

            default:
                Toast.makeText(getActivity(), Constants.MESSAGE_ADDED_SOON, Toast.LENGTH_SHORT).show();
        }
    }
}

