package com.example.james.bluehoc.holder;


import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.bluehoclibrary.controller.BTClientController;
import com.example.bluehoclibrary.model.BTGroup;
import com.example.james.bluehoc.R;

/**
 * Created by james on 12/23/16.
 */

public class BTGroupViewHolder extends RecyclerView.ViewHolder {
    private CardView cardView;
    private TextView nameTextView;
    private TextView addressTextView;

    private Button joinGroupButton;

    private Activity activity;

    private BTGroup group;

    public BTGroupViewHolder(View itemView, final Activity activity) {
        super(itemView);

        this.activity = activity;

        cardView = (CardView)itemView.findViewById(R.id.card_view);
        nameTextView = (TextView)itemView.findViewById(R.id.group_name_text_view);
        addressTextView = (TextView)itemView.findViewById(R.id.group_address_text_view);

        joinGroupButton = (Button)itemView.findViewById(R.id.join_group_button);
        joinGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BTClientController controller = new BTClientController(activity);
                controller.startClientService();
                controller.connectToDevice(group.getServerDevice());
            }
        });

    }

    public CardView getCardView() {
        return cardView;
    }

    public BTGroup getBTGroup() {
        return group;
    }

    public void setCardView(CardView cardView) {
        this.cardView = cardView;
    }

    public TextView getNameTextView() {
        return nameTextView;
    }

    public void setNameTextView(TextView nameTextView) {
        this.nameTextView = nameTextView;
    }

    public TextView getAddressTextView() {
        return addressTextView;
    }

    public void setAddressTextView(TextView addressTextView) {
        this.addressTextView = addressTextView;
    }

    public void setBTGroup(BTGroup BTGroup) {
        this.group = BTGroup;
    }

}
