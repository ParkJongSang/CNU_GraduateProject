package com.cse.grow.finalgraduationproject.fragment;

import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.Intent;
import android.os.Message;
import android.os.TestLooperManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cse.grow.finalgraduationproject.R;
import com.cse.grow.finalgraduationproject.chat.MessageActivity;
import com.cse.grow.finalgraduationproject.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PeopleFragment extends Fragment {
    @android.support.annotation.Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @android.support.annotation.Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.peoplefragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new PeopleFragemntRecyclerViewAdapter());

        return view;
    }

    class PeopleFragemntRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        List<UserModel> userModels;

        public PeopleFragemntRecyclerViewAdapter(){
            userModels = new ArrayList<>();
            final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //친구 추가 시, 목록을 비워주는 역할을 함
                    userModels.clear();

                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        UserModel userModel = snapshot.getValue(UserModel.class);

                        if(userModel.userUid.equals(myUid)){
                            continue;
                        }
                        userModels.add(snapshot.getValue(UserModel.class));

                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
            Glide.with(
                    viewHolder.itemView.getContext())
                    .load(userModels.get(i).profileImageUrl)
                    .apply(new RequestOptions().circleCrop()).into(((CustomViewHolder)viewHolder).imageView);

            ((CustomViewHolder)viewHolder).textView.setText(userModels.get(i).userName);

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), MessageActivity.class);
                    intent.putExtra("destinationUid", userModels.get(i).userUid);
                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.fromright, R.anim.toleft);
                    startActivity(intent, activityOptions.toBundle());
                }
            });

            if(userModels.get(i).comment_state != null){
                ((CustomViewHolder) viewHolder).textView_commnet.setText(userModels.get(i).comment_state);
            }
            if(userModels.get(i).comment_position != null){
                ((CustomViewHolder) viewHolder).textView_position.setText(userModels.get(i).comment_position);
            }
            if(userModels.get(i).userSchool != null){
                ((CustomViewHolder) viewHolder).textView_school.setText(userModels.get(i).userSchool);
            }
        }



        @Override
        public int getItemCount() {
            return userModels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView;
            public TextView textView_commnet;
            public TextView textView_position;
            public TextView textView_school;

            public CustomViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.frienditem_imageview);
                textView = (TextView) view.findViewById(R.id.frienditem_textview);
                textView_commnet = (TextView) view.findViewById(R.id.frienditem_textview_comment);
                textView_position = (TextView) view.findViewById(R.id.frienditem_textview_position);
                textView_school = (TextView) view.findViewById(R.id.frienditem_textview_school);
            }
        }
    }
}
