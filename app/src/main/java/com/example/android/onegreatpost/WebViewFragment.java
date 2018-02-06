package com.example.android.onegreatpost;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.android.onegreatpost.WebInteraction.MyJavascriptInterface;
import com.example.android.onegreatpost.WebInteraction.MyWebViewClient;
import com.github.ybq.android.spinkit.SpinKitView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.onegreatpost.Constants.FORUM_URL;
import static com.example.android.onegreatpost.Utils.ConnectionUtils.showNoConnection_withSnackBarFinalFix;

public class WebViewFragment extends Fragment {

    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.spin_kit) SpinKitView spinKitView;
    @BindView(R.id.coordinator_layout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.no_connection_iv) ImageView noConnection_iv;
    @BindView(R.id.spinner_nav) Spinner spinner;
    @BindView(R.id.nav_spin_kit) SpinKitView navSpinKit;
    @BindView(R.id.refresh_btn) ImageButton refresh_btn;

    private WebView webView;
    private Snackbar snackbar;
    private Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_web_view, container, false);
        ButterKnife.bind(this, getActivity());

        bundle = this.getArguments();

        refresh_btn.setVisibility(View.GONE);
        spinner.setVisibility(View.GONE);
        webView = rootView.findViewById(R.id.web_view);
        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("JavascriptInterface")
            @Override
            public void onClick(View view) {
                setupWebView(webView);
                Snackbar.make(view, "Loading..", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.performClick();

        setHasOptionsMenu(true);
        return rootView;
    }


    @SuppressLint("SetJavaScriptEnabled")
     void setupWebView(WebView webView) {
        webView.getSettings()
                .setUserAgentString("Mozilla/5.0 (Linux; Android 4.4.2; X1 7.0 Build/HuaweiMediaPad) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36");

        webView.getSettings().setJavaScriptEnabled(true);
        MyJavascriptInterface.setContext(getActivity());
        webView.addJavascriptInterface(new MyJavascriptInterface(), "HTMLOUT");
        webView.setWebViewClient(new MyWebViewClient(getActivity(), spinKitView, snackbar
                , noConnection_iv, coordinatorLayout, bundle));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        webView.loadUrl(FORUM_URL);

        snackbar = showNoConnection_withSnackBarFinalFix(snackbar, true
                , spinKitView, noConnection_iv, coordinatorLayout);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.web_fragment_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        SwitchCompat Switch = menu.findItem(R.id.app_bar_switch_scroll).getActionView().findViewById(R.id.option_menu_switch);
        Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    navSpinKit.setVisibility(View.VISIBLE);
                    MyJavascriptInterface.scroll();
                } else{
                    MyJavascriptInterface.autoScroll = false;
                    navSpinKit.setVisibility(View.GONE);
                    // TODO: make the navSpinKit disappear when the autoScroll work really stops not
                    // on just pressing (when changing the app work to the background)
                }

            }
        });
        super.onPrepareOptionsMenu(menu);
    }
}