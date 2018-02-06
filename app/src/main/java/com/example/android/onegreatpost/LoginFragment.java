package com.example.android.onegreatpost;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.android.onegreatpost.Utils.LoginUtils;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.WanderingCubes;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
   Created by dasse on 06-Feb-18.
 */

public class LoginFragment extends Fragment {

    @BindView(R.id.spinner_nav) Spinner spinner;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.refresh_btn) ImageButton refresh_btn;
    @BindView(R.id.coordinator_layout) CoordinatorLayout coordinatorLayout;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_login, container, false);
        ButterKnife.bind(this, getActivity());

        final EditText input_email = rootView.findViewById(R.id.input_email);
        final EditText input_password = rootView.findViewById(R.id.input_password);
        Button login_btn = rootView.findViewById(R.id.login_btn);

        refresh_btn.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);
        spinner.setVisibility(View.GONE);
        WanderingCubes wanderingCubes = new WanderingCubes();

        AppCompatActivity appCompatActivity = ((AppCompatActivity) getActivity());
        appCompatActivity.setSupportActionBar(toolbar);
        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
        appCompatActivity.setTitle(null);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("email", input_email.getText().toString());
                bundle.putString("pass", input_password.getText().toString());

                LoginUtils.openWebViewIfLoggedIn(getFragmentManager(), coordinatorLayout, bundle);
            }
        });

        return rootView;
    }


}
