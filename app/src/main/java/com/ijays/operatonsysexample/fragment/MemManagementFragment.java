package com.ijays.operatonsysexample.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ijays.operatonsysexample.R;

/**
 * Created by ijaysdev on 16/6/6.
 */
public class MemManagementFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mem_management_layout, null);
        TextView memTip= (TextView) view.findViewById(R.id.mem_tip);

        memTip.setText(getString(R.string.mem_tip));
        return view;
    }

}
