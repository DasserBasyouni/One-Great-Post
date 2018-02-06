package com.example.android.onegreatpost.Utils;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.novoda.merlin.NetworkStatus;
import com.novoda.merlin.registerable.bind.Bindable;
import com.novoda.merlin.registerable.connection.Connectable;
import com.novoda.merlin.registerable.disconnection.Disconnectable;

import static com.example.android.onegreatpost.MainActivity.merlin;

/**
   Created by dasse on 03-Feb-18.
 */

public class ConnectionUtils {

    // this method is to let us use final snackBar in showNoConnection().
    public static Snackbar showNoConnection_withSnackBarFinalFix(Snackbar snackbar, final boolean emptyView
            , final SpinKitView spinKitView, final ImageView noConnection_iv
            , final CoordinatorLayout coordinatorLayout){

        if(snackbar == null)
            snackbar = Snackbar.make(coordinatorLayout, "Check your Internet Connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Dismiss", null);

        return showNoConnection(snackbar, emptyView, spinKitView, noConnection_iv);
    }

    private static Snackbar showNoConnection(final Snackbar snackbar, final boolean emptyView
            , final SpinKitView spinKitView, final ImageView noConnection_iv){

        merlin.registerConnectable(new Connectable() {
            @Override
            public void onConnect() {
                if (snackbar != null)
                    if (snackbar.isShown())
                        snackbar.dismiss();
                noConnection_iv.setVisibility(View.GONE);
                spinKitView.setVisibility(View.VISIBLE);
                // Do something you haz internet!
            }
        });
        merlin.registerDisconnectable(new Disconnectable() {
            @Override
            public void onDisconnect() {
                showIViewIfNoDataAndHidePB();
                showUndismissableSnackBar();
            }

            private void showIViewIfNoDataAndHidePB() {
                if(emptyView) {
                    spinKitView.setVisibility(View.GONE);
                    noConnection_iv.setVisibility(View.VISIBLE);
                }else {
                    spinKitView.setVisibility(View.GONE);
                    noConnection_iv.setVisibility(View.GONE);
                }
            }

            private void showUndismissableSnackBar() {
                snackbar.show();
                snackbar.getView().getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        snackbar.getView().getViewTreeObserver().removeOnPreDrawListener(this);
                        ((CoordinatorLayout.LayoutParams) snackbar.getView().getLayoutParams()).setBehavior(null);
                        return true;
                    }
                });
            }
        });
        merlin.registerBindable(new Bindable() {

            @Override
            public void onBind(NetworkStatus networkStatus) {

            }
        });
        return snackbar;
    }
}
