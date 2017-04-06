package com.example.james.bluehoc.fragment;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.bluehoclibrary.database.helper.DatabaseHelper;
import com.example.bluehoclibrary.database.model.DBMember;
import com.example.bluehoclibrary.database.model.DBMessage;
import com.example.james.bluehoc.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
    private DatabaseHelper databaseHelper;

    private Button accessButton;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        databaseHelper = new DatabaseHelper(getContext());

        accessButton = (Button)view.findViewById(R.id.access_database_button);

        accessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<DBMember> members = new ArrayList<>(databaseHelper.getMembers());

                for(DBMember member : members) {
                    Log.v("id", Long.toString(member.getMemberId()));
                    Log.v("name", member.getName());
                    Log.v("address", member.getAddress());
                }

                ArrayList<DBMessage> messages = new ArrayList<>(databaseHelper.getMessages());

                for(DBMessage message : messages) {
                    Log.v("id", Long.toString(message.getMessageId()));
                    Log.v("content", message.getContent());
                }
            }
        });

        return view;
    }

}
