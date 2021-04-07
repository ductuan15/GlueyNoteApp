package com.example.mynoteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.mynoteapp.data.TagAdapter;
import com.example.mynoteapp.data.TagDetail;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class TagviewActivity extends AppCompatActivity {
    RecyclerView tagListRecyclerView;
    ArrayList<TagDetail> tagDetailArrayList = new ArrayList<>();
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tagview);
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        data.getString("alltags");
        String [] allTag = data.getString("alltags").split("\n");

        for(int i = 0;i<allTag.length - 1;i+=2){
            TagDetail tagDetail = new TagDetail(allTag[i],Integer.parseInt(allTag[i + 1]));
            this.tagDetailArrayList.add(tagDetail);
        }
        this.tagListRecyclerView = findViewById(R.id.tagList);
        TagAdapter tagAdapter = new TagAdapter(this.tagDetailArrayList);
        this.tagListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.tagListRecyclerView.setAdapter(tagAdapter);

        // Attaching the layout to the toolbar object
        MaterialToolbar toolbar = findViewById(R.id.alltag_topbar);

        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(toolbar);
    }
}