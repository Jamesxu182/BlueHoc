package com.example.james.bluehoc.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bluehoclibrary.model.BTGroup;
import com.example.james.bluehoc.R;
import com.example.james.bluehoc.holder.BTGroupViewHolder;

import java.util.List;

/**
 * Created by james on 3/5/17.
 */

public class BTGroupsRecyclerViewAdapter extends RecyclerView.Adapter<BTGroupViewHolder> {
    private List<BTGroup> groups;
    private Activity activity;

    // initialize BTGroupRecyclerViewAdapter with a list of BTGroups and a MainActivity
    public BTGroupsRecyclerViewAdapter(List<BTGroup> groups, Activity activity) {
        this.groups = groups;
        this.activity = activity;
    }

    @Override
    public BTGroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // get view from item layout xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item, parent, false);

        // construct BTGroupViewHolder
        BTGroupViewHolder groupViewHolder = new BTGroupViewHolder(view, activity);

        return groupViewHolder;
    }

    @Override
    public void onBindViewHolder(BTGroupViewHolder holder, int position) {
        // get current group item
        BTGroup BTGroup = groups.get(position);

        // display group name on item
        holder.getNameTextView().setText(BTGroup.getServerName());

        // display bluetooth address on item
        holder.getAddressTextView().setText(BTGroup.getServerAddress());

        holder.setBTGroup(BTGroup);
    }

    @Override
    public int getItemCount() {
        // get the size of the groups
        return groups.size();
    }
}
