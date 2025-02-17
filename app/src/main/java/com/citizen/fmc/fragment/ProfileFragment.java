package com.citizen.fmc.fragment;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.citizen.fmc.R;
import com.citizen.fmc.model.User;
import com.citizen.fmc.utils.Utils;

import dmax.dialog.SpotsDialog;

/**
 * ======> Created by dheeraj-gangwar on 2018-02-03 <======
 */

public class ProfileFragment extends Fragment {
    Activity activity;
    RelativeLayout parentLayout;
    EditText firstNameET;
    EditText lastNameET;
    EditText userNameET;
    EditText passwordET;
    EditText confirmPasswordET;
    RadioGroup genderRadioGroup;
    int genderId = 1;
    private String TAG = getClass().getSimpleName();
    private SpotsDialog spotsDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_profile, container, false);
        activity = getActivity();
        Utils.setToolbarTitle(activity, "Profile");
        parentLayout = mView.findViewById(R.id.parent_layout);
        firstNameET = mView.findViewById(R.id.first_name_edit_text);
        lastNameET = mView.findViewById(R.id.last_name_edit_text);
        userNameET = mView.findViewById(R.id.user_name_edit_text);
        passwordET = mView.findViewById(R.id.new_password_edit_text);
        confirmPasswordET = mView.findViewById(R.id.confirm_new_password_edit_text);
        genderRadioGroup = mView.findViewById(R.id.gender_radio_group);

        passwordET.setVisibility(View.GONE);
        confirmPasswordET.setVisibility(View.GONE);

        User mUser = Utils.getUserDetails(activity);
        String firstName = mUser.getFirstName();
        String lastName = mUser.getLastName();
        String email = mUser.getEmail();
        String mobileNum = mUser.getMobileNum();
        int genderId = mUser.getGenderId();

        firstNameET.setText(firstName != null && !firstName.isEmpty() ? firstName : "");
        lastNameET.setText(lastName != null && !lastName.isEmpty() ? lastName : "");
        userNameET.setText(email != null && !email.isEmpty() ? email : (mobileNum != null && !mobileNum.isEmpty() ? mobileNum : ""));
        if (genderId == 1) {
            genderRadioGroup.check(R.id.cb_female);
        } else {
            genderRadioGroup.check(R.id.cb_male);
        }


        return mView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_filter).setVisible(false);
        menu.findItem(R.id.action_notification).setVisible(false);
    }
}
