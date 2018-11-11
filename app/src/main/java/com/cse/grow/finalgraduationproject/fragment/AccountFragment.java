package com.cse.grow.finalgraduationproject.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;

import com.cse.grow.finalgraduationproject.R;
import com.cse.grow.finalgraduationproject.TimetableActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AccountFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @org.jetbrains.annotations.Nullable @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_account, container, false);
        Button button_dialog = (Button) view.findViewById(R.id.accountFragment_button_comment);
        Button button_timetable = (Button) view.findViewById(R.id.accountFragment_button_timetable);

        button_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(view.getContext());
            }
        });

        button_timetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), TimetableActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    void showDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_comment, null);


        final EditText editText = view.findViewById(R.id.commentDialog_edittext);

        builder.setView(view).setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Map<String, Object> stringObjectMap = new HashMap<>();
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                stringObjectMap.put("comment_state", editText.getText().toString());
                FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(stringObjectMap);
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }


}
