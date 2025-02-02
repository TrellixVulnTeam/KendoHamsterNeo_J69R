package com.kendohamster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

public class HistoryDailyRecord extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerAdapterForHistoryList adapter;

    private ArrayList<String> timeList = new ArrayList<>();
    private ArrayList<String> motionsList = new ArrayList<>();
    private ArrayList<Integer> countList = new ArrayList<>();
    private ArrayList<Double> correctRateList = new ArrayList<>();
    private DatabaseReference DB_ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_daily_record);

        setTitle("2022/03/09");


        recyclerView = findViewById(R.id.recyclerViewHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(HistoryDailyRecord.this));

        /*
        timeList.add("20:01");
        timeList.add("20:04");
        timeList.add("20:13");
        timeList.add("20:18");
        timeList.add("20:29");
        timeList.add("20:35");
        timeList.add("20:55");

        motionsList.add("正面劈刀");
        motionsList.add("切返");
        motionsList.add("送足");
        motionsList.add("右挽劈刀");
        motionsList.add("跳躍擺陣");
        motionsList.add("右胴劈刀");
        motionsList.add("打小支");

        countList.add(10);
        countList.add(9);
        countList.add(10);
        countList.add(10);
        countList.add(10);
        countList.add(10);
        countList.add(10);

        correctRateList.add(80.0);
        correctRateList.add(77.8);
        correctRateList.add(90.0);
        correctRateList.add(90.0);
        correctRateList.add(70.0);
        correctRateList.add(60.0);
        correctRateList.add(80.0);

         */

        //讀firebase results
        DB_ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference objRef = DB_ref.child("HistoryDataModel");
        objRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot taskSnapshot) {
                for(DataSnapshot snapshot: taskSnapshot.getChildren()){
                    addRecords(snapshot);
                }
                adapter = new RecyclerAdapterForHistoryList(timeList, motionsList, countList, correctRateList, HistoryDailyRecord.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.myDrawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle( this, drawerLayout, toolbar, R.string.drawer_open , R.string.drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super .onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super .onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                drawerLayout.closeDrawer(GravityCompat.START);

                int id = item.getItemId();
                if (id == R.id.action_action){
                    selectItem(R.id.action_action);

                    item.setChecked(true);
                    drawerLayout.closeDrawers();
                    return true;
                }
                else if (id == R.id.action_menu){
                    selectItem(R.id.action_menu);

                    item.setChecked(true);
                    drawerLayout.closeDrawers();

                    return true;
                }
                else if (id == R.id.action_history){
                    selectItem(R.id.action_history);

                    item.setChecked(true);
                    drawerLayout.closeDrawers();
                    return true;
                }
                else if (id == R.id.action_setting){
                    selectItem(R.id.action_setting);

                    item.setChecked(true);
                    drawerLayout.closeDrawers();
                    return true;
                }
                return false;
            }
        });
    }

    public void selectItem(int position) {
        Intent i = null;
        switch(position) {
            case (R.id.action_action):
                i = new Intent(this,MotionList.class);
                break;
            case R.id.action_history:
                i = new Intent(this, History.class);
                break;
            case R.id.action_menu:
                i = new Intent(this, TrainingMenu.class);
                break;
            case R.id.action_setting:
                i = new Intent(this, Settings.class);
                break;
            case (R.id.btnMotionList):
                i = new Intent(this,MotionList.class);
                break;
            case R.id.btnHistory:
                i = new Intent(this, History.class);
                break;
            case R.id.btnTrainingMenu:
                i = new Intent(this, TrainingMenu.class);
                break;
            case R.id.btnSettings:
                i = new Intent(this, Settings.class);
                break;
            default:
                break;
        }

        startActivity(i);
    }

    public void addRecords(DataSnapshot snapshot){
        String motionName = snapshot.child("action_name").getValue().toString();
        String timestamp_str = (String) snapshot.child("timestamp").getValue();
        Timestamp timestamp = Timestamp.valueOf(timestamp_str);
        Float accuracy = Float.valueOf(snapshot.child("accuracy").getValue().toString()) * 100;
        int practice_time = Integer.valueOf(snapshot.child("practice_time").getValue().toString());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp.getTime());
        Log.d("time_history", calendar.toString());

        timeList.add(String.format("%02d:%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND)));
        motionsList.add(motionName);
        correctRateList.add(Double.valueOf(accuracy.toString()));
        countList.add(practice_time);

        /*
        switch (motionName) {
            case "正面劈刀":

                break;
            case "擦足":

                break;
            case "托刀":

                break;
        }

         */
    }
}