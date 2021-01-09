package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView;
import android.view.View;


import java.util.ArrayList;

public class searchResults extends AppCompatActivity implements View.OnClickListener {
    private ListView listViewBusiness;
    private businessAdapterForUserResultSearch adapter;
    public ImageButton bake_to_search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        populateListView();
        bake_to_search = (ImageButton) findViewById(R.id.backtosearch);
        bake_to_search.setOnClickListener(this);



    }

        public void populateListView() {
            listViewBusiness = (ListView) findViewById(R.id.listOfBusiness);
            ArrayList<businessUser> list = getIntent().getParcelableArrayListExtra("businessList");
            adapter = new businessAdapterForUserResultSearch(this, list);
            listViewBusiness.setAdapter(adapter);
            listViewBusiness.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Toast.makeText(searchResults.this, "Click on item" + position, Toast.LENGTH_LONG).show();
                }
            });

        }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.backtosearch:
                Intent BackToSearchintent = new Intent(this, searchScreen.class);
                startActivity(BackToSearchintent);
                break;
        }

    }
}
