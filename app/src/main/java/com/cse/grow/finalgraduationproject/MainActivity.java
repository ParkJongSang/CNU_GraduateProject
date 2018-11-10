package com.cse.grow.finalgraduationproject;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.test.espresso.remote.EspressoRemoteMessage;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.cse.grow.finalgraduationproject.fragment.ChatFragment;
import com.cse.grow.finalgraduationproject.fragment.PeopleFragment;
import com.cse.grow.finalgraduationproject.fragment.AccountFragment;
import com.cse.grow.finalgraduationproject.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;

public class MainActivity extends AppCompatActivity {

    private List<UserModel> userModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.mainactivity_bottonnavigationview);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_people:
                        try {
                            updateComment();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout, new PeopleFragment()).commit();
                        return true;
                    case R.id.action_chat:
                        try {
                            updateComment();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout, new ChatFragment()).commit();
                        return true;
                    case R.id.action_account:
                        try {
                            updateComment();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout, new AccountFragment()).commit();
                        return true;
                }
                return false;
            }
        });
        passPushTokenToServer();
    }

    void passPushTokenToServer(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();
        Map<String, Object> map = new HashMap<>();
        map.put("pushToken",token);

        FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);
    }

    void updateComment() throws Exception {

        //Toast.makeText(this,"updateCommentFail",Toast.LENGTH_SHORT).show();
        //Toast.makeText(this,currentDay,Toast.LENGTH_SHORT).show();


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());

        String currentHour = simpleDateFormat.format(date).toString();
        String currentDay = getDateDay(date, "yyyyMMdd");

        Toast.makeText(this,currentHour,Toast.LENGTH_SHORT).show();

        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userModelList.clear();
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    userModelList.add(item.getValue(UserModel.class));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        //Toast.makeText(this,FirebaseDatabase.getInstance().getReference().child("users").child(userModelList.get(0).userUid).toString(),Toast.LENGTH_SHORT).show();

        for(int i = 0; i < userModelList.size(); i++){
            String uid =userModelList.get(i).userUid;

            if(currentDay.equals("monday")){
                isLecture(uid, "monday", currentHour);
            }else if(currentDay.equals("tuesday")){
                isLecture(uid, "tuesday", currentHour);
            }else if(currentDay.equals("wendsday")){
                isLecture(uid, "wendsday", currentHour);
            }else if(currentDay.equals("thursday")){
                isLecture(uid, "thursday", currentHour);
            }else if(currentDay.equals("friday")){
                isLecture(uid, "friday", currentHour);
            }else{
                Map<String, Object> map = new HashMap<>();
                map.put("comment", "주 말");
                FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);
            }
        }
    }

    void isLecture(String uid, String day, String Hour){
        String[] hours = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child(day).toString().split(",");
        int[] real_hours = new int[hours.length];
        int currentHour = Integer.parseInt(Hour);
        boolean isLec = false;

        for(int i = 0; i < real_hours.length; i++){
            real_hours[i] = Integer.parseInt(hours[i]);
        }
        Map<String, Object> map = new HashMap<>();
        //map.put("comment", )

        for(int i = 0; i < real_hours.length; i++){
            if(currentHour == real_hours[i]){
                isLec = true;
                break;
            }
        }
        if(isLec == true){
            map.put("comment", "수업 중");
            FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);
        }else{
            map.put("comment", "공강");
            FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);
        }


    }

    String getDateDay(Date date, String dateType) throws Exception {
        String day = "";
        Date returnDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateType);
        String str  = dateFormat.format(date);
        returnDate = dateFormat.parse(str);
        Calendar cal = Calendar.getInstance();
        cal.setTime(returnDate);

        int dayNum = cal.get(Calendar.DAY_OF_WEEK);

        switch (dayNum) {
            case 1:
                day = "sunday";
                break;
            case 2:
                day = "monday";
                break;
            case 3:
                day = "tuesday";
                break;
            case 4:
                day = "wendsday";
                break;
            case 5:
                day = "thursday";
                break;
            case 6:
                day = "friday";
                break;
            case 7:
                day = "saturday";
                break;

        }

        return day;
    }
}
