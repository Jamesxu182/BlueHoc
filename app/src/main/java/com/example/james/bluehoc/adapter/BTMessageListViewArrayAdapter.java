package com.example.james.bluehoc.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.bluehoclibrary.controller.base.BTBaseController;
import com.example.bluehoclibrary.model.BTMessage;
import com.example.james.bluehoc.R;

import java.util.List;

/**
 * Created by james on 2/6/17.
 */

public class BTMessageListViewArrayAdapter extends ArrayAdapter<BTMessage> {
    private TextView messageTextView;
    private TextView nameTextView;

    public BTMessageListViewArrayAdapter(Context context, int resource, List<BTMessage> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BTMessage message = getItem(position);

        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = convertView;

        if (convertView == null) {
            if(message.getFromMember().getAddress().equals(BTBaseController.myBluetoothAddress)) {
                rowView = inflater.inflate(R.layout.message_right, parent, false);

                messageTextView = (TextView)rowView.findViewById(R.id.message_text_view_right);
                nameTextView = (TextView)rowView.findViewById(R.id.right_name_text_view);

                nameTextView.setText(BTBaseController.myBluetoothName);
            } else {
                rowView = inflater.inflate(R.layout.message_left, parent, false);

                messageTextView = (TextView)rowView.findViewById(R.id.message_text_view_left);
                nameTextView = (TextView)rowView.findViewById(R.id.left_name_text_view);

                nameTextView.setText(message.getFromMember().getName());
            }
        }

        messageTextView.setText(message.getContent());

        return rowView;
    }
}
