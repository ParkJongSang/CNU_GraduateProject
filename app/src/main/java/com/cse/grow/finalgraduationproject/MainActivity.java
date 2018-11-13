package com.cse.grow.finalgraduationproject;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.test.espresso.remote.EspressoRemoteMessage;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.cse.grow.finalgraduationproject.chat.MessageActivity;
import com.cse.grow.finalgraduationproject.fragment.ChatFragment;
import com.cse.grow.finalgraduationproject.fragment.PeopleFragment;
import com.cse.grow.finalgraduationproject.fragment.AccountFragment;
import com.cse.grow.finalgraduationproject.model.ChatModel;
import com.cse.grow.finalgraduationproject.model.UserModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {

    private List<UserModel> userModelList = new ArrayList<>();

    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    private Geocoder geocoder;

    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission = false;

    private Gpsinfo gps;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.mainactivity_bottonnavigationview);

        //Intent intent = new Intent(this, MyService.class);
        //startService(intent);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_people:
                        try {
                            updateComment();
                            searchCurrentPosition();
                            printCurrentPosition();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout, new PeopleFragment()).commit();
                        return true;
                    case R.id.action_chat:
                        try {
                            updateComment();
                            searchCurrentPosition();
                            printCurrentPosition();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout, new ChatFragment()).commit();
                        return true;
                    case R.id.action_account:
                        try {
                            updateComment();
                            searchCurrentPosition();
                            printCurrentPosition();
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

    void printCurrentPosition(){
        geocoder = new Geocoder(this);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final List<Address>[] list = new List[]{null};
        if(!isPermission){
            callPermission();
        }

        gps = new Gpsinfo(MainActivity.this);

        if(gps.isGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            try {
                list[0] = geocoder.getFromLocation(latitude, longitude, 10);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (list != null) {
                String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String currentAddress = list[0].toString().split("\"")[1];
                String schoolAddress = null;

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

                for(int i = 0; i < userModelList.size(); i++){
                    if(userModelList.get(i).userUid.equals(currentUid)){
                        schoolAddress = userModelList.get(i).currentPositon;
                        break;
                    }
                }

                Map<String, Object> map = new HashMap<>();
                if(currentAddress.trim().equals(schoolAddress.trim())) {

                    map.put("comment_position", "학교 내부");

                }else{
                    //Map<String, Object> map = new HashMap<>();
                    map.put("comment_position", "학교 외부");
                }

                FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);
                //sendText = "위도 : " + latitude + "\n경도 : " + longitude + "\n" + strings[1] + "\n";
            } else {
                //sendText = "위도 : " + latitude + "\n경도 : " + longitude + "\n주소가 검색되지 않습니다";
            }
        }else{
            gps.showSettingsAlert();
        }
    }

    void searchCurrentPosition() throws IOException {
        geocoder = new Geocoder(this);
        final List<Address>[] list = new List[]{null};

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String school = null;

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

        for(int i = 0; i < userModelList.size(); i++){
            if(userModelList.get(i).userUid.equals(uid)){
                school = userModelList.get(i).userSchool;
                break;
            }
        }

        try {
            //Toast.makeText(this, school, Toast.LENGTH_SHORT).show();
            list[0] = geocoder.getFromLocationName(school, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = null;

        if (list != null) {
            //Toast.makeText(this, list[0].toString(), Toast.LENGTH_SHORT).show();
            address = list[0].toString().split("\"")[1];
            //sendText = "위도 : " + latitude + "\n경도 : " + longitude + "\n" + strings[1] + "\n";
        } else {
            //sendText = "위도 : " + latitude + "\n경도 : " + longitude + "\n주소가 검색되지 않습니다";
        }

        Toast.makeText(this, address, LENGTH_SHORT).show();
        Map<String, Object> map = new HashMap<>();
        map.put("currentPositon", address);

        FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);

    }

    void updateComment() throws Exception {

        //Toast.makeText(this,"updateCommentFail",Toast.LENGTH_SHORT).show();
        //Toast.makeText(this,currentDay,Toast.LENGTH_SHORT).show();


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
        Date date = new Date(System.currentTimeMillis());

        String currentHour = simpleDateFormat.format(date).toString();
        String currentDay = getDateDay(date, "yyyyMMdd");

        //Toast.makeText(this,currentHour,Toast.LENGTH_SHORT).show();

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
                Toast.makeText(this, "is Tuesday?", LENGTH_SHORT).show();
                isLecture(uid, "tuesday", currentHour);
            }else if(currentDay.equals("wendsday")){
                isLecture(uid, "wendsday", currentHour);
            }else if(currentDay.equals("thursday")){
                isLecture(uid, "thursday", currentHour);
            }else if(currentDay.equals("friday")){
                isLecture(uid, "friday", currentHour);
            }else{
                Map<String, Object> map = new HashMap<>();
                map.put("comment_state", "주 말");
                FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);
            }
        }
    }

    void isLecture(String uid, String day, String Hour){

        String[] hours = null;//FirebaseDatabase.getInstance().getReference().child("users").child(uid).child(day);

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

        for(int i = 0; i < userModelList.size(); i++){
            if(day.equals("monday")){
                hours = userModelList.get(i).monday.toString().split(",");
            }else if(day.equals("tuesday")){
                hours = userModelList.get(i).tuesday.toString().split(",");
            }else if(day.equals("wendsday")){
                hours = userModelList.get(i).wendsday.toString().split(",");
            }else if(day.equals("thursday")){
                hours = userModelList.get(i).thursday.toString().split(",");
            }else if(day.equals("friday")){
                hours = userModelList.get(i).friday.toString().split(",");
            }
        }

        int[] real_hours = new int[hours.length];
        int currentHour = Integer.parseInt(Hour);
        boolean isLec = false;

        for(int i = 0; i < real_hours.length; i++){
            real_hours[i] = Integer.parseInt(hours[i]);
        }
        Map<String, Object> map = new HashMap<>();


        for(int i = 0; i < real_hours.length; i++){
            if(currentHour == real_hours[i]){
                isLec = true;
                break;
            }
        }
        if(isLec == true){
            map.put("comment_state", "수업 중");
            FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);
        }else{
            map.put("comment_state", "공강");
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            isAccessFineLocation = true;

        } else if (requestCode == PERMISSIONS_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            isAccessCoarseLocation = true;
        }

        if (isAccessFineLocation && isAccessCoarseLocation) {
            isPermission = true;
        }
    }

    // 전화번호 권한 요청
    private void callPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_ACCESS_FINE_LOCATION);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {
            isPermission = true;
        }
    }
}
