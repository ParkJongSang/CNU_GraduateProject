package com.cse.grow.finalgraduationproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.test.espresso.remote.EspressoRemoteMessage;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cse.grow.finalgraduationproject.model.ChatModel;
import com.cse.grow.finalgraduationproject.model.UserModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimetableActivity extends AppCompatActivity {

    TextView[] monday = new TextView[13];
    TextView[] tuesday = new TextView[13];
    TextView[] wednsday = new TextView[13];
    TextView[] thursday = new TextView[13];
    TextView[] friday = new TextView[13];

    List<UserModel> userModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        //시간표 초기화
        Button add_timetable = (Button) findViewById(R.id.timetableActivity_button);
        Button redraw = (Button) findViewById(R.id.timetableActivity_button_redraw);
        final List<UserModel> userModelList = new ArrayList<>();

        monday[0] = (TextView)findViewById(R.id.monday_9);
        monday[1] = (TextView)findViewById(R.id.monday_10);
        monday[2] = (TextView)findViewById(R.id.monday_11);
        monday[3] = (TextView)findViewById(R.id.monday_12);
        monday[4] = (TextView)findViewById(R.id.monday_13);
        monday[5] = (TextView)findViewById(R.id.monday_14);
        monday[6] = (TextView)findViewById(R.id.monday_15);
        monday[7] = (TextView)findViewById(R.id.monday_16);
        monday[8] = (TextView)findViewById(R.id.monday_17);
        monday[9] = (TextView)findViewById(R.id.monday_18);
        monday[10] = (TextView)findViewById(R.id.monday_19);
        monday[11] = (TextView)findViewById(R.id.monday_20);

        tuesday[0] = (TextView)findViewById(R.id.tuesday_9);
        tuesday[1] = (TextView)findViewById(R.id.tuesday_10);
        tuesday[2] = (TextView)findViewById(R.id.tuesday_11);
        tuesday[3] = (TextView)findViewById(R.id.tuesday_12);
        tuesday[4] = (TextView)findViewById(R.id.tuesday_13);
        tuesday[5] = (TextView)findViewById(R.id.tuesday_14);
        tuesday[6] = (TextView)findViewById(R.id.tuesday_15);
        tuesday[7] = (TextView)findViewById(R.id.tuesday_16);
        tuesday[8] = (TextView)findViewById(R.id.tuesday_17);
        tuesday[9] = (TextView)findViewById(R.id.tuesday_18);
        tuesday[10] = (TextView)findViewById(R.id.tuesday_19);
        tuesday[11] = (TextView)findViewById(R.id.tuesday_20);

        wednsday[0] = (TextView)findViewById(R.id.wendsday_9);
        wednsday[1] = (TextView)findViewById(R.id.wendsday_10);
        wednsday[2] = (TextView)findViewById(R.id.wendsday_11);
        wednsday[3] = (TextView)findViewById(R.id.wendsday_12);
        wednsday[4] = (TextView)findViewById(R.id.wendsday_13);
        wednsday[5] = (TextView)findViewById(R.id.wendsday_14);
        wednsday[6] = (TextView)findViewById(R.id.wendsday_15);
        wednsday[7] = (TextView)findViewById(R.id.wendsday_16);
        wednsday[8] = (TextView)findViewById(R.id.wendsday_17);
        wednsday[9] = (TextView)findViewById(R.id.wendsday_18);
        wednsday[10] = (TextView)findViewById(R.id.wendsday_19);
        wednsday[11] = (TextView)findViewById(R.id.wendsday_20);

        thursday[0] = (TextView)findViewById(R.id.thursday_9);
        thursday[1] = (TextView)findViewById(R.id.thursday_10);
        thursday[2] = (TextView)findViewById(R.id.thursday_11);
        thursday[3] = (TextView)findViewById(R.id.thursday_12);
        thursday[4] = (TextView)findViewById(R.id.thursday_13);
        thursday[5] = (TextView)findViewById(R.id.thursday_14);
        thursday[6] = (TextView)findViewById(R.id.thursday_15);
        thursday[7] = (TextView)findViewById(R.id.thursday_16);
        thursday[8] = (TextView)findViewById(R.id.thursday_17);
        thursday[9] = (TextView)findViewById(R.id.thursday_18);
        thursday[10] = (TextView)findViewById(R.id.thursday_19);
        thursday[11] = (TextView)findViewById(R.id.thursday_20);

        friday[0] = (TextView)findViewById(R.id.friday_9);
        friday[1] = (TextView)findViewById(R.id.friday_10);
        friday[2] = (TextView)findViewById(R.id.friday_11);
        friday[3] = (TextView)findViewById(R.id.friday_12);
        friday[4] = (TextView)findViewById(R.id.friday_13);
        friday[5] = (TextView)findViewById(R.id.friday_14);
        friday[6] = (TextView)findViewById(R.id.friday_15);
        friday[7] = (TextView)findViewById(R.id.friday_16);
        friday[8] = (TextView)findViewById(R.id.friday_17);
        friday[9] = (TextView)findViewById(R.id.friday_18);
        friday[10] = (TextView)findViewById(R.id.friday_19);
        friday[11] = (TextView)findViewById(R.id.friday_20);

        //초기화 끝
        drawTimetable();

        //Toast.makeText(this, "open Timetable.", Toast.LENGTH_SHORT).show();

        add_timetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(v.getContext());
            }
        });

        redraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawTimetable();
            }
        });

        drawTimetable();


    }

    void showDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_timetable, null);


        final RadioButton monday = (RadioButton)view.findViewById(R.id.monday);
        final RadioButton tuesday = (RadioButton)view.findViewById(R.id.tuesday);
        final RadioButton wendsday = (RadioButton)view.findViewById(R.id.wendsday);
        final RadioButton thursday = (RadioButton)view.findViewById(R.id.thursday);
        final RadioButton friday = (RadioButton)view.findViewById(R.id.friday);



        final CheckBox check_9 = (CheckBox)view.findViewById(R.id.time_9);
        final CheckBox check_10 = (CheckBox)view.findViewById(R.id.time_10);
        final CheckBox check_11 = (CheckBox)view.findViewById(R.id.time_11);
        final CheckBox check_12 = (CheckBox)view.findViewById(R.id.time_12);
        final CheckBox check_13 = (CheckBox)view.findViewById(R.id.time_13);
        final CheckBox check_14 = (CheckBox)view.findViewById(R.id.time_14);
        final CheckBox check_15 = (CheckBox)view.findViewById(R.id.time_15);
        final CheckBox check_16 = (CheckBox)view.findViewById(R.id.time_16);
        final CheckBox check_17 = (CheckBox)view.findViewById(R.id.time_17);
        final CheckBox check_18 = (CheckBox)view.findViewById(R.id.time_18);
        final CheckBox check_19 = (CheckBox)view.findViewById(R.id.time_19);
        final CheckBox check_20 = (CheckBox)view.findViewById(R.id.time_20);

        builder.setView(view);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Map<String, Object> map = new HashMap<>();
                StringBuffer sb = new StringBuffer();
                String key = null;

                if(monday.isChecked() == true){
                    key = "monday";
                }else if(tuesday.isChecked() == true){
                    key = "tuesday";
                }else if(wendsday.isChecked() == true){
                    key = "wendsday";
                }else if(thursday.isChecked() == true){
                    key = "thursday";
                }else if(friday.isChecked() == true){
                    key = "friday";
                }

                if (check_9.isChecked() == true) {
                    sb.append("9,");
                }
                if (check_10.isChecked() == true) {
                    sb.append("10,");
                }
                if (check_11.isChecked() == true) {
                    sb.append("11,");
                }
                if (check_12.isChecked() == true) {
                    sb.append("12,");
                }
                if (check_13.isChecked() == true) {
                    sb.append("13,");
                }
                if (check_14.isChecked() == true) {
                    sb.append("14,");
                }
                if (check_15.isChecked() == true) {
                    sb.append("15,");
                }
                if (check_16.isChecked() == true) {
                    sb.append("16,");
                }
                if (check_17.isChecked() == true) {
                    sb.append("17,");
                }
                if (check_18.isChecked() == true) {
                    sb.append("18,");
                }
                if (check_19.isChecked() == true) {
                    sb.append("19,");
                }
                if (check_20.isChecked() == true) {
                    sb.append("20,");
                }
                map.put(key, sb.toString());
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        drawTimetable();
                    }
                });

            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    void drawTimetable(){
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Toast.makeText(this, "draw Table.", Toast.LENGTH_SHORT).show();

        String[] str_monday      = null;
        String[] str_tuesday     = null;//FirebaseDatabase.getInstance().getReference().child("users").child(currentUid).child("tuesday").getKey().split(",");
        String[] str_wednsday    = null;//FirebaseDatabase.getInstance().getReference().child("users").child(currentUid).child("wednsdya").getKey().split(",");
        String[] str_thursday    = null;//FirebaseDatabase.getInstance().getReference().child("users").child(currentUid).child("thursday").getKey().split(",");
        String[] str_friday      = null;//FirebaseDatabase.getInstance().getReference().child("users").child(currentUid).child("friday").getKey().split(",");

        int[] times_monday = null;
        int[] times_tuesday = null;
        int[] times_wednsday = null;
        int[] times_thursday = null;
        int[] times_friday = null;



        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userModels.clear();
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    userModels.add(item.getValue(UserModel.class));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        for(int i = 0; i < userModels.size(); i++){
            if(currentUid.equals(userModels.get(i).userUid)){
                if(userModels.get(i).monday != null){
                    str_monday = userModels.get(i).monday.split(",");
                    times_monday = new int[str_monday.length];
                }
                if(userModels.get(i).tuesday!= null){
                    str_tuesday = userModels.get(i).tuesday.split(",");
                    times_tuesday = new int[str_tuesday.length];
                }
                if(userModels.get(i).wendsday != null){
                    str_wednsday = userModels.get(i).wendsday.split(",");
                    times_wednsday = new int[str_wednsday.length];
                }
                if(userModels.get(i).thursday != null){
                    str_thursday = userModels.get(i).thursday.split(",");
                    times_thursday = new int[str_thursday.length];
                }
                if(userModels.get(i).friday != null){
                    str_friday = userModels.get(i).friday.split(",");
                    times_friday = new int[str_friday.length];
                }
            }
        }

        if(times_monday != null && !str_monday[0].equals("")){
            for(int i = 0; i < 12; i++){
                monday[i].setBackgroundColor(Color.rgb(255,255,255));
            }
            for(int i = 0; i < times_monday.length; i++){
                Log.d(this.getClass().getName(), "times_monday");
                times_monday[i] = Integer.parseInt(str_monday[i]);
                monday[times_monday[i]-9].setBackgroundColor(Color.rgb(0,0,0));
            }
        }
        if(times_monday != null && str_monday[0].equals("")){
            for(int i = 0; i < 12; i++){
                monday[i].setBackgroundColor(Color.rgb(255,255,255));
            }
        }

        if(times_tuesday != null && !str_tuesday[0].equals("")){
            for(int i = 0; i < 12; i++){
                tuesday[i].setBackgroundColor(Color.rgb(255,255,255));
            }
            for(int i = 0; i < times_tuesday.length; i++){
                Log.d(this.getClass().getName(), "times_tuesday");
                times_tuesday[i] = Integer.parseInt(str_tuesday[i]);
                tuesday[times_tuesday[i]-9].setBackgroundColor(Color.rgb(0,0,0));
            }
        }
        if(times_tuesday != null && str_tuesday[0].equals("")){
            for(int i = 0; i < 12; i++){
                tuesday[i].setBackgroundColor(Color.rgb(255,255,255));
            }
        }

        if(times_wednsday != null && !str_wednsday[0].equals("")){
            for(int i = 0; i < 12; i++){
                wednsday[i].setBackgroundColor(Color.rgb(255,255,255));
            }
            for(int i = 0; i < times_wednsday.length; i++){
                Log.d(this.getClass().getName(), "times_wednsday");
                times_wednsday[i] = Integer.parseInt(str_wednsday[i]);
                wednsday[times_wednsday[i]-9].setBackgroundColor(Color.rgb(0,0,0));
            }
        }
        if(times_wednsday != null && str_wednsday[0].equals("")){
            for(int i = 0; i < 12; i++){
                wednsday[i].setBackgroundColor(Color.rgb(255,255,255));
            }
        }

        if(times_thursday != null && !str_thursday[0].equals("")){
            for(int i = 0; i < 12; i++){
                thursday[i].setBackgroundColor(Color.rgb(255,255,255));
            }
            for(int i = 0; i < times_thursday.length; i++){
                Log.d(this.getClass().getName(), "times_thursday");
                times_thursday[i] = Integer.parseInt(str_thursday[i]);
                thursday[times_thursday[i]-9].setBackgroundColor(Color.rgb(0,0,0));
            }
        }
        if(times_thursday != null && str_thursday[0].equals("")){
            for(int i = 0; i < 12; i++){
                thursday[i].setBackgroundColor(Color.rgb(255,255,255));
            }
        }

        if(times_friday != null && !str_friday[0].equals("")){
            for(int i = 0; i < 12; i++){
                friday[i].setBackgroundColor(Color.rgb(255,255,255));
            }
            for(int i = 0; i < times_friday.length; i++){
                Log.d(this.getClass().getName(), "times_friday");
                times_friday[i] = Integer.parseInt(str_friday[i]);
                friday[times_friday[i]-9].setBackgroundColor(Color.rgb(0,0,0));
            }
        }
        if(times_friday != null && str_friday[0].equals("")){
            for(int i = 0; i < 12; i++){
                friday[i].setBackgroundColor(Color.rgb(255,255,255));
            }
        }
    }
}
