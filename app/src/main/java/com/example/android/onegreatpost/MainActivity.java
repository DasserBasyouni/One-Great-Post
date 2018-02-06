package com.example.android.onegreatpost;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.android.onegreatpost.Utils.LoginUtils;
import com.example.android.onegreatpost.WebInteraction.MyJavascriptInterface;
import com.github.ybq.android.spinkit.SpinKitView;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.novoda.merlin.Merlin;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_frame) FrameLayout mainFrame;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.coordinator_layout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.no_connection_iv) ImageView noConnection_iv;
    @BindView(R.id.nav_spin_kit) SpinKitView navSpinKit;
    @BindView(R.id.refresh_btn) ImageButton refresh_btn;
    @BindView(R.id.spinner_nav) Spinner spinner;

    public static Merlin merlin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        merlin = new Merlin.Builder().withConnectableCallbacks()
                .withBindableCallbacks()
                .withDisconnectableCallbacks()
                .build(this);

        getFragmentManager().beginTransaction()
        .add(R.id.main_frame, new MainFragment(),"MainFragment_tag").commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        merlin.bind();
    }

    @Override
    protected void onPause() {
        merlin.unbind();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getFragmentManager();
        int count = fragmentManager.getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
        } else {
            fragmentManager.popBackStack();
            if(fragmentManager.getBackStackEntryAt(0).getName().equals("WebViewFragment_tag")){
                fab.setVisibility(View.VISIBLE);
               fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(coordinatorLayout, "Export Button is uncompleted for now", Snackbar.LENGTH_INDEFINITE)
                                .show();
                    }
                });
                MyJavascriptInterface.autoScroll = false;
                navSpinKit.setVisibility(View.GONE);
                refresh_btn.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }
}