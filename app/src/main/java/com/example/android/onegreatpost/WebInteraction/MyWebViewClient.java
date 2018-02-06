package com.example.android.onegreatpost.WebInteraction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;

import static com.example.android.onegreatpost.Constants.FORUM_URL;
import static com.example.android.onegreatpost.Constants.PART_OF_SCROLL_DOWN_URL;
import static com.example.android.onegreatpost.Utils.ConnectionUtils.showNoConnection_withSnackBarFinalFix;
import static com.example.android.onegreatpost.Utils.LoginUtils.LOGGED_IN;
import static com.example.android.onegreatpost.WebInteraction.MyJavascriptInterface.autoScroll;


/**
   Created by Dasser on 13-Dec-17.
 */

public class MyWebViewClient extends WebViewClient {

    @SuppressLint("StaticFieldLeak")
    private static WebView webView;
    private boolean ScrollDownDataLoadingStarted;
    private int ReceivedDataFromScrolling = 0;
    private Context context;
    private SpinKitView spinKitView;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;
    private ImageView noConnection_iv;
    private final int[] i = {0};
    private int messageBusNumber = 0;
    private String email, pass;

    public MyWebViewClient(Activity context, SpinKitView spinKitView, Snackbar snackbar
            , ImageView noConnection_iv, CoordinatorLayout coordinatorLayout, Bundle bundle) {
        this.spinKitView = spinKitView;
        this.snackbar = snackbar;
        this.coordinatorLayout = coordinatorLayout;
        this.noConnection_iv = noConnection_iv;
        this.context = context;

        if (bundle != null) {
            email = bundle.getString("email", "empty email");
            pass = bundle.getString("pass", "password");
        }
    }


    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        i[0]++;
        // do your handling codes here, which url is the requested url
        // probably you need to open that url rather than redirect:
        Toast.makeText(context, i[0] + " " + url, Toast.LENGTH_SHORT).show();
        Log.wtf("Z_", url);
        return false; // then it is not handled by default action
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        spinKitView.setVisibility(View.GONE);
        snackbar = showNoConnection_withSnackBarFinalFix(snackbar, true
                , spinKitView, noConnection_iv, coordinatorLayout);
        Log.wtf("Z_our url", url);

        switch (url){
            case FORUM_URL:
                LOGGED_IN = true;
                view.loadUrl("javascript: (function() {document.getElementsByClassName('toggler btn-flat ember-view')[0].click();}) ();");
                processHTML(view);
                webView = view;
                return;

            default:
                Log.wtf("Z_", "default");
                view.loadUrl("javascript: (function() {document.getElementsByClassName" +
                        "('index--text-input--ee5HP undefined')[0].value = '" + email + "';}) ();");
                view.loadUrl("javascript: (function() {document.getElementsByClassName" +
                        "('index--text-input--ee5HP undefined')[1].value = '" + pass + "';}) ();");
                view.loadUrl("javascript: (function() {document.getElementsByClassName" +
                        "('index--primary--P14pO index--_btn--9nYKH form--primary-button--1NgrB index--standard--3U4zZ')[0].click();}) ();");
                LOGGED_IN = true;
        }
    }

    private void processHTML(WebView view) {
        view.loadUrl("javascript:window.HTMLOUT.processHTML(document.body.innerHTML)");
    }

    static void scrollDownTheForum() {
        webView.post(new Runnable() {
            @Override
            public void run() {
                Log.wtf("Z_ss", "scrollDownTheForum");
                webView.loadUrl("javascript: (function() {window.scroll(0, 100000000000);}) ();");
            }
        });
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
        Log.d("Z_", "onLoadResource " + url);

        if(LOGGED_IN && isLoadedDataIsCausedByScrolling(url)){
            processHTML(view);
        }
    }

    private boolean isLoadedDataIsCausedByScrolling(String url) {
        boolean isURLContainsMessageBus = url.contains("message-bus");
        if(url.contains(PART_OF_SCROLL_DOWN_URL)){
            ScrollDownDataLoadingStarted = true;
            messageBusNumber = 0;
        } else if(isURLContainsMessageBus){
            messageBusNumber++;
            if(autoScroll && messageBusNumber > 5){
                MyWebViewClient.refreshWebView();
                messageBusNumber = 0;
            }
            ScrollDownDataLoadingStarted = false;
        }

        if(ScrollDownDataLoadingStarted && !isURLContainsMessageBus){
            ReceivedDataFromScrolling++;
        }else{
            if(ReceivedDataFromScrolling > 0){
                ReceivedDataFromScrolling = 0;
                return true;
            }
        }
        return false;
    }

    private static void refreshWebView(){
        webView.loadUrl(FORUM_URL);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        Log.e("Z_WebViewClient", "Error: " + String.valueOf(error) + "- on request: "+ String.valueOf(request));
    }

    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
        Log.e("Z_onReceivedHttpError", "Error: " + String.valueOf(errorResponse) + "- on request: "+ String.valueOf(request));
    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        super.doUpdateVisitedHistory(view, url, isReload);
        Log.d("Z_", "doUpdateVisitedHistory");
    }

    @Override
    public void onFormResubmission(WebView view, Message dontResend, Message resend) {
        super.onFormResubmission(view, dontResend, resend);
        Log.d("Z_", "onFormResubmission");
    }
}