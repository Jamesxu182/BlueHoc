package com.example.james.bluehoc.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.james.bluehoc.R;

import java.util.ArrayList;

/**
 * Created by james on 2/27/17.
 */

public class PairedDevicesListCustomArrayAdapter extends ArrayAdapter<BluetoothDevice> {

    public PairedDevicesListCustomArrayAdapter(Context context, int resource, ArrayList<BluetoothDevice> list) {
        super(context, resource, list);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BluetoothDevice device = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_paired_devices_list_layout, parent, false);
        }

        TextView nameTextView = (TextView) convertView.findViewById(R.id.name_text_view);
        TextView addressTextView = (TextView) convertView.findViewById(R.id.address_text_view);

        nameTextView.setText(device.getName());
        addressTextView.setText(device.getAddress());

        return convertView;
    }
}
