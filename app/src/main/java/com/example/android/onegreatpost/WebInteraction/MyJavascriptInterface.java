package com.example.android.onegreatpost.WebInteraction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.widget.TextView;

import com.example.android.onegreatpost.Utils.DataFormattingUtils;
import com.example.android.onegreatpost.WebViewFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

/**
   Created by Dasser on 13-Dec-17.
 */

public final class MyJavascriptInterface {

    public static boolean autoScroll;
    private int startFromPosition = 0;

    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static int lastScrollHeight = 0;


    @JavascriptInterface
    @SuppressWarnings("unused")
    public void scanIfNeedMoreData(String html) {
        Log.d("Z_scanIfNeedMoreData", "reached");

        Document document = Jsoup.parse(html);
        Element element = document.getElementById("ember895");  //changed before - table ID

        uploadDataToDB(element);
    }

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void processHTML(String html) {
        Log.d("Z_processHTML", "reached");

        //displayTestingDialog(html);

        Document document = Jsoup.parse(html);
        Element postsListElement = document.getElementsByClass("topic-list ember-view").get(0);

        uploadDataToDB(postsListElement);
    }

    private void displayTestingDialog(String html) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("html")
                .setMessage(html)
                .setPositiveButton("CONFIRM", null)
                .show();

        Window window = alertDialog.getWindow();
        if (window != null) {
            TextView textView = window.getDecorView().findViewById(android.R.id.message);
            textView.setTextIsSelectable(true);
        }
    }

    private void uploadDataToDB(Element postsListElement) {
        Elements titleElements = postsListElement.getElementsByClass("title");
        Elements pinnedElements = postsListElement.getElementsByClass("topic-excerpt");
        Elements dateElements = postsListElement.getElementsByClass("age activity");

        //Log.d("Z_size", titleElements.size() + ", " +  pinnedElements.size() + ", " +  dateElements.size());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        /*db.collection("Posts-test-" + DataFormattingUtils.getPartialTimestampFormat(timeStamp))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int count = 0;
                    for (DocumentSnapshot document : task.getResult()) {
                        Log.d("Z_TAG", document.getId());
                        count++;
                    }
                    Log.d("Z_TAG", count + "");
                } else {
                    Log.d("Z_TAG", "Error getting documents: ", task.getException());
                }
            }
        });*/


        /*final TaskCompletionSource<Integer> source = new TaskCompletionSource<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int fromWhereToStart = postsCollection.get().getResult().size();
                source.setResult(fromWhereToStart);
            }
        }).start();

        Task<Integer> task = source.getTask();
        task.addOnCompleteListener(new OnCompleteListener<Integer>() {
            @Override
            public void onComplete(@NonNull Task<Integer> task) {
                if (task.isSuccessful())
                    Log.e("Z_fromWhereToStart", "= " + task.getResult());
            }
        });*/

        for (; startFromPosition < titleElements.size(); startFromPosition++) {
            String date = dateElements.get(startFromPosition).attr("title");

            final Map<String, Object> posts = new HashMap<>();
            //posts.put("date", date);
            posts.put(DataFormattingUtils.getDatabaseIDFormat(titleElements.get(startFromPosition).text()), 0);

            DocumentReference postsWithDate = db.collection("posts")
                    .document("Posts-" + DataFormattingUtils.getDateFromTitle(date));

            Log.wtf("Z_count", "= " + startFromPosition);
            checkExistenceAndUploadData(postsWithDate, posts, titleElements, date);

        }
        if (autoScroll){
            MyWebViewClient.scrollDownTheForum();
        }
    }

    private void checkExistenceAndUploadData(final DocumentReference postsWithDate
            , final Map<String, Object> posts, final Elements titleElements, final String date) {
        postsWithDate.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(isDataNew(task))
                    uploadTheNewData();
            }

            private boolean isDataNew(Task<DocumentSnapshot> task) {
                Log.d("Z_11", String.valueOf(posts.keySet()));

                boolean comparisionIsValid = false;
                if (task.getResult().exists()) {
                    Log.d("Z_22", String.valueOf(task.getResult().getData().keySet()));
                    comparisionIsValid = true;
                }

                if (comparisionIsValid) {
                    if (!(task.getResult().getData().keySet().contains(posts.keySet().toArray()[0]))) {
                        return true;
                    }
                } else {
                    return true;
                }
                return false;
            }

            private void uploadTheNewData() {
                Log.d("Z_33", "not existed");

                postsWithDate.set(posts, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Z_newItem", posts.keySet().toArray()[0] + " - " + postsWithDate.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Z_", "Error adding document", e);
                            }
                        });
            }
        });
    }

    @JavascriptInterface
    @SuppressWarnings("unused")
    public static void setLastScrollHeight(int sh) {
        Log.wtf("Z_h 2", "= " + sh);
        if (sh != lastScrollHeight) {
            lastScrollHeight = sh;
        }
    }

    public static int getLastScrollHeight() {
        return lastScrollHeight;
    }

    public static void scroll() {
        MyWebViewClient.scrollDownTheForum();
    }

    public static void setContext(Context context1) {
        context = context1;
    }
}