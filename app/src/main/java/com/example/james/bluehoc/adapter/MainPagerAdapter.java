package com.example.james.bluehoc.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.james.bluehoc.fragment.ChatFragment;
import com.example.james.bluehoc.fragment.MemberFragment;

/**
 * Created by james on 12/20/16.
 */

public class MainPagerAdapter extends FragmentStatePagerAdapter {
    private Bundle bundle;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
        this.bundle = null;
    }

    public MainPagerAdapter(FragmentManager fm, Bundle bundle) {
        super(fm);
        this.bundle = bundle;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                MemberFragment memberFragment = new MemberFragment();
                if(bundle != null) {
                    memberFragment.setArguments(bundle);
                }
                return memberFragment;
            case 1:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0:
                return "Contacts";
            case 1:
                return "Chats";
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
