package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView;
import android.view.View;


import java.util.ArrayList;

public class searchResults extends AppCompatActivity {
    private ListView listViewBusiness;
    private businessAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        populateListView();
    }

        public void populateListView() {
            listViewBusiness = (ListView) findViewById(R.id.listOfBusiness);
            ArrayList<businessUser> list = getIntent().getParcelableArrayListExtra("businessList");
            adapter = new businessAdapter(this, list);
            listViewBusiness.setAdapter(adapter);
            listViewBusiness.setOnItemClickListener((parent, view, position, id) -> {
                Toast.makeText(this, "Click on item" + position, Toast.LENGTH_LONG).show();
            });
        }
    }
