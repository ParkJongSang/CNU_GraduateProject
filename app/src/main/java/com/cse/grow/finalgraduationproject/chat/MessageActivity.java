package com.cse.grow.finalgraduationproject.chat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cse.grow.finalgraduationproject.Gpsinfo;
import com.cse.grow.finalgraduationproject.R;
import com.cse.grow.finalgraduationproject.model.ChatModel;
import com.cse.grow.finalgraduationproject.model.NotificationModel;
import com.cse.grow.finalgraduationproject.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MessageActivity extends Activity {

    private String destinationUid;
    private Button button;
    private EditText editText;

    private String uid;
    private String chatRoomUid;

    private Button location;

    private RecyclerView recyclerView;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    private UserModel destinationUserModel;


    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission = false;

    // GPSTracker class
    private Gpsinfo gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();  //채팅을 요구하는 아이디 즉 단말기에 로그인된 UID
        destinationUid = getIntent().getStringExtra("destinationUid");  //채팅을 당하는 아이디
        button = (Button) findViewById(R.id.messageactivity_button);
        editText = (EditText) findViewById(R.id.messageactivity_editText);
        recyclerView = (RecyclerView) findViewById(R.id.messageactivity_recyclerview);
        location = (Button) findViewById(R.id.messageactivity_location);


        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isPermission){
                    callPermission();
                    return;
                }

                gps = new Gpsinfo(MessageActivity.this);

                if(gps.isGetLocation()){
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    String sendText = "위도 : " + latitude + "\n경도 : " + longitude;

                    ChatModel.Comment comment = new ChatModel.Comment();
                    comment.uid = uid;
                    comment.message = sendText;
                    comment.timeStamp = ServerValue.TIMESTAMP;

                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            sendGcm();
                            editText.setText("");
                        }
                    });

                }else{
                    gps.showSettingsAlert();
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChatModel chatModel = new ChatModel();
                chatModel.users.put(uid, true);
                chatModel.users.put(destinationUid, true);

                if(chatRoomUid == null){
                    button.setEnabled(false);
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checkChatRoom();
                        }
                    });
                }else{
                    ChatModel.Comment comment = new ChatModel.Comment();
                    comment.uid = uid;
                    comment.message = editText.getText().toString();
                    comment.timeStamp = ServerValue.TIMESTAMP;
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            sendGcm();
                            editText.setText("");
                        }
                    });
                }

            }
        });
        checkChatRoom();

    }

    void sendGcm(){
        Gson gson = new Gson();

        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        NotificationModel notificationModel = new NotificationModel();
        notificationModel.to = destinationUserModel.pushToken;
        notificationModel.notification.title = userName;
        notificationModel.notification.text = editText.getText().toString();
        notificationModel.data.title = userName;
        notificationModel.data.text = editText.getText().toString();




        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"), gson.toJson(notificationModel));

        Request request = new Request.Builder()
                .header("Content-Type", "application/json")
                .addHeader("Authorization", "key=AIzaSyCkv747CDRUHOSJmeRhjbJW3Ckrcvo_nzw")
                .url("https://fcm.googleapis.com/fcm/send")
                .post(requestBody)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });

    }

    void checkChatRoom(){
        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    ChatModel chatModel = item.getValue(ChatModel.class);
                    if(chatModel.users.containsKey(destinationUid)){
                        chatRoomUid = item.getKey();
                        button.setEnabled(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                        recyclerView.setAdapter(new RecyclerViewAdaptor());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    class RecyclerViewAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<ChatModel.Comment> comments;

        public RecyclerViewAdaptor() {
            comments = new ArrayList<>();

            FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    destinationUserModel = dataSnapshot.getValue(UserModel.class);
                    getMessageList();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        void getMessageList() {
            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    comments.clear();

                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        comments.add(item.getValue(ChatModel.Comment.class));
                    }
                    //메세지가 갱신
                    notifyDataSetChanged();
                    recyclerView.scrollToPosition(comments.size() - 1);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message, viewGroup, false);

            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            MessageViewHolder messageViewHolder = ((MessageViewHolder) viewHolder);

            //내가보낸 메시지
            if (comments.get(position).uid.equals(uid)) {
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.rightbubble);
                messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);
                messageViewHolder.textView_message.setTextSize(25);
                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);
                //상대방이 보낸거
            } else {
                Glide.with(viewHolder.itemView.getContext())
                        .load(destinationUserModel.profileImageUrl)
                        .apply(new RequestOptions().circleCrop())
                        .into(messageViewHolder.imageView_profile);

                messageViewHolder.textView_name.setText(destinationUserModel.userName);
                messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.leftbubble);
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setTextSize(25);
                messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);
            }

            long unixTime = (long) comments.get(position).timeStamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = simpleDateFormat.format(date);
            messageViewHolder.textview_timestamp.setText(time);
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class MessageViewHolder extends RecyclerView.ViewHolder {
            public TextView textView_message;
            public TextView textView_name;
            public ImageView imageView_profile;
            public LinearLayout linearLayout_destination;
            public LinearLayout linearLayout_main;
            public TextView textview_timestamp;

            public MessageViewHolder(View view) {
                super(view);
                textView_message = (TextView) view.findViewById(R.id.messageitem_textview_message);
                textView_name = (TextView) view.findViewById(R.id.messageitem_textview_name);
                imageView_profile = (ImageView) view.findViewById(R.id.messageitem_imageview_profile);
                linearLayout_destination = (LinearLayout) view.findViewById(R.id.messageitem_linearlayout_destination);
                linearLayout_main = (LinearLayout) view.findViewById(R.id.messageitem_linearlayout_main);
                textview_timestamp = (TextView) view.findViewById(R.id.messageitem_textview_timestamp);
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();;
        overridePendingTransition(R.anim.fromleft, R.anim.toright);
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
