package com.example.james.bluehoc.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bluehoclibrary.model.BTMember;
import com.example.bluehoclibrary.model.BTMessage;
import com.example.james.bluehoc.R;

import java.util.ArrayList;

/**
 * Created by james on 3/2/17.
 */

public class BTMemberListCustomArrayAdapter extends ArrayAdapter<BTMember> {
    private SparseBooleanArray selectedItemsIds;

    public BTMemberListCustomArrayAdapter(Context context, int resource, ArrayList<BTMember> list) {
        super(context, resource, list);
        selectedItemsIds = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BTMember member = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_member_list_layout, parent, false);
        }

        TextView nameTextView = (TextView) convertView.findViewById(R.id.member_name_text_view);
        TextView addressTextView = (TextView) convertView.findViewById(R.id.member_address_text_view);

        LinearLayout itemLayout = (LinearLayout) convertView.findViewById(R.id.member_linear_layout);

        nameTextView.setText(member.getName());
        addressTextView.setText(member.getAddress());

        if(selectedItemsIds.get(position)) {
            itemLayout.setBackgroundColor(Color.GRAY);
            nameTextView.setTextColor(Color.WHITE);
            addressTextView.setTextColor(Color.WHITE);
        } else {
            itemLayout.setBackgroundColor(Color.WHITE);
            nameTextView.setTextColor(Color.BLACK);
            addressTextView.setTextColor(Color.BLACK);
        }

        return convertView;
    }

    public void toggleSelection(int position) {
        selectView(position, !selectedItemsIds.get(position));
    }

    public void selectView(int position, boolean value) {
        if (value) {
            selectedItemsIds.put(position, value);
        } else {
            selectedItemsIds.delete(position);
        }

        notifyDataSetChanged();
    }

    public void removeSelection() {
        selectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public SparseBooleanArray getSelectedIds() {
        return selectedItemsIds;
    }
}
