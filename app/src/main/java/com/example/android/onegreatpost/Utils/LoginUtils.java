package com.example.android.onegreatpost.Utils;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;

import com.example.android.onegreatpost.R;
import com.example.android.onegreatpost.WebViewFragment;

/**
   Created by dasse on 02-Feb-18.
 */

public class LoginUtils {

    private static boolean ENABLE_LOGGING_IN = true;
    public static boolean LOGGED_IN;

    public static void openWebViewIfLoggedIn(FragmentManager fragmentManager, CoordinatorLayout coordinatorLayout, Bundle bundle) {
        if(ENABLE_LOGGING_IN){
            WebViewFragment webViewFragment = new WebViewFragment();
            webViewFragment.setArguments(bundle);

            fragmentManager.beginTransaction()
                    .add(R.id.main_frame, webViewFragment).addToBackStack("WebViewFragment_tag").commit();
        }else {
            Snackbar.make(coordinatorLayout, "Login is required" +
                    ", and this layout will be provided after revealing the source code", Snackbar.LENGTH_INDEFINITE)
                    .show();
        }
    }
}
