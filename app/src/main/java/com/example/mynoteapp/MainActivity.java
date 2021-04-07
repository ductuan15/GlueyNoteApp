package com.example.mynoteapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.example.mynoteapp.data.NoteDetail;
import com.example.mynoteapp.data.NotesAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    SharedPreferences sharedPreferences;

    SharedPreferences.Editor editor;
    RecyclerView noteRecyclerView;
    NotesAdapter notesAdapter;
    LinkedList<String> tagsLinkedList;
    HashMap<String,Integer> tagsHashSet = new HashMap<>();
    boolean isGridView = false;
    FloatingActionButton addFloatingActionButton;
    ActionBarDrawerToggle toggle;


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        this.toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(this);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationview);
        navigationView.setNavigationItemSelectedListener(this);
        this.toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(toggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        this.noteRecyclerView = findViewById(R.id.noteList);

        this.addFloatingActionButton = findViewById(R.id.floatingActionButton);
        addFloatingActionButton.setOnClickListener(this);

        this.notesAdapter = new NotesAdapter(getAllNotes(), this,this);
        this.noteRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.noteRecyclerView.setAdapter(notesAdapter);
    }

    private ArrayList<NoteDetail> getAllNotes() {
        ArrayList<NoteDetail> noteDetailArrayList = new ArrayList<>();

        Map<String, ?> allNoteData = this.sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allNoteData.entrySet()) {

            try {
                Integer.parseInt(entry.getKey());
            } catch (NumberFormatException e) {
                continue;
            }
            String data = this.sharedPreferences.getString(entry.getKey(), "");
            NoteDetail noteDetail = NoteDetail.parseString(data);

            noteDetailArrayList.add(noteDetail);
            NoteDetail.updateID();
        }

        return noteDetailArrayList;
    }

    @Override
    public void onResume() {
        super.onResume();

        // data change
        notesAdapter.setArrayList(getAllNotes());

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent noteDetailIntent = new Intent(this, NoteDetailActivity.class);
        Bundle data = new Bundle();
        long ID = this.notesAdapter.getNoteID(position);

        data.putLong("NoteID", ID);

        noteDetailIntent.putExtras(data);
        startActivity(noteDetailIntent);
    }


    @Override
    public void onClick(View v) {
        // add new note
        if (v.getId() == this.addFloatingActionButton.getId()) {
            Intent noteDetailIntent = new Intent(this, NoteDetailActivity.class);
            Bundle data = new Bundle();

            // get next available  ID
            long ID = NoteDetail.getNextID();

            data.putLong("NoteID", ID);

            noteDetailIntent.putExtras(data);
            startActivity(noteDetailIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topmenu, menu);
        MenuItem searchItem = menu.findItem(R.id.search_view);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                notesAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.view__item) {
            if (!this.isGridView) {
                this.noteRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
                isGridView = true;
            } else {
                this.noteRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                isGridView = false;
            }
        }
        if (this.toggle.onOptionsItemSelected(item)) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        this.tagsHashSet.clear();
        for(NoteDetail noteDetail:this.notesAdapter.getNoteDetailsArrayList()){
            ArrayList<String> tagArrayList = noteDetail.getAllTags();
            for(String tag:tagArrayList){
                if(this.tagsHashSet.containsKey(tag))
                    this.tagsHashSet.put(tag,this.tagsHashSet.get(tag) + 1);
                else
                    this.tagsHashSet.put(tag,1);
            }
        }

        if (item.getItemId() == R.id.nav_alltag_item) {
            Log.e("wegweg","WEGwegweg");
            Intent tagIntent = new Intent(this, TagviewActivity.class);
            StringBuilder dataStringBuilder = new StringBuilder();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                this.tagsHashSet.forEach((k,v) ->
                    dataStringBuilder.append(k).append("\n").append(v).append("\n"));
            }
            else{
                Iterator hmIterator = this.tagsHashSet.entrySet().iterator();
                while(hmIterator.hasNext()){
                    Map.Entry mapEntry = (Map.Entry) hmIterator.next();
                    dataStringBuilder.append(mapEntry.getKey()).append("\n").append(mapEntry.getValue()).append("\n");
                }
            }

            Bundle data = new Bundle();
            data.putString("alltags", dataStringBuilder.toString());
            tagIntent.putExtras(data);
            startActivity(tagIntent);
        }
        return true;
    }
}