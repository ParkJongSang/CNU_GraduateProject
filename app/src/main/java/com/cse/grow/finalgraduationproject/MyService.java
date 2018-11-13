package com.cse.grow.finalgraduationproject;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.cse.grow.finalgraduationproject.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

public class MyService extends Service {
    public MyService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        Log.d("Service","Start Service");

        try {
            searchCurrentPosition();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return START_NOT_STICKY;
    }

    private Geocoder geocoder;
    private List<UserModel> userModelList = new ArrayList<>();


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



}
