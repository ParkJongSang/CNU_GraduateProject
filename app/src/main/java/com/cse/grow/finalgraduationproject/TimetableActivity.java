package com.cse.grow.finalgraduationproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class TimetableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        //시간표 초기화
        Button add_timetable = (Button) findViewById(R.id.timetableActivity_button);
        TextView[] monday = new TextView[13];
        TextView[] tuesday = new TextView[13];
        TextView[] wednsday = new TextView[13];
        TextView[] thursday = new TextView[13];
        TextView[] friday = new TextView[13];

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

        add_timetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(v.getContext());
            }
        });
    }

    void showDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_timetable, null);

        final CheckBox check_9 = view.findViewById(R.id.time_9);
        final CheckBox check_10 = view.findViewById(R.id.time_10);
        final CheckBox check_11 = view.findViewById(R.id.time_11);
        final CheckBox check_12 = view.findViewById(R.id.time_12);
        final CheckBox check_13 = view.findViewById(R.id.time_13);
        final CheckBox check_14 = view.findViewById(R.id.time_14);
        final CheckBox check_15 = view.findViewById(R.id.time_15);
        final CheckBox check_16 = view.findViewById(R.id.time_16);
        final CheckBox check_17 = view.findViewById(R.id.time_17);
        final CheckBox check_18 = view.findViewById(R.id.time_18);
        final CheckBox check_19 = view.findViewById(R.id.time_19);
        final CheckBox check_20 = view.findViewById(R.id.time_20);

        builder.setView(view).setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }
}
