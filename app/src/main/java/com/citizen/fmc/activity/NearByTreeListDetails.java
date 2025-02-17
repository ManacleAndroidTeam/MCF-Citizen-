package com.citizen.fmc.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;

import com.citizen.fmc.R;
import com.citizen.fmc.adapter.NearByTreeListAdapter;
import com.citizen.fmc.adapter.NearByTreeListDetailsAdapter;
import com.citizen.fmc.model.NearByTreeListModel;
import com.citizen.fmc.model.OtherAttributes;

import java.util.ArrayList;
import java.util.Collections;

public class NearByTreeListDetails extends AppCompatActivity {

    private ArrayList<OtherAttributes> otherAttributes;
    ArrayList<NearByTreeListModel> nearByTreeListModels;
    ListView list_view;
    Context mctx;
    String treeId="";
    String tree_no = "";
    String tree_name = "";
    int position=0;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Utils.setToolbarTitle(this, "Tree List Details");
        setContentView(R.layout.activity_near_by_tree_list_details);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tree List Details");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        mctx = NearByTreeListDetails.this;
        list_view = findViewById(R.id.list_view);
        nearByTreeListModels  = new ArrayList<>();
        otherAttributes = new ArrayList<>();
        if (getIntent()!=null)
        {
//            otherAttributes = (ArrayList) getIntent().getParcelableArrayListExtra("data");
            position = getIntent().getIntExtra("position",0);
            tree_no = getIntent().getStringExtra("tree_no");
            tree_name = getIntent().getStringExtra("tree_name");
//            treeId = getIntent().getStringExtra("treeId");
        }

//        NearByTreeListDetailsAdapter adapter = new NearByTreeListDetailsAdapter(NearByTreeListDetails.this,otherAttributes);
        NearByTreeListDetailsAdapter adapter = new NearByTreeListDetailsAdapter(NearByTreeListDetails.this, Collections.singletonList(NearByTreeListAdapter.list.get(position).getOtherAttributes()),tree_no,tree_name);
        list_view.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}