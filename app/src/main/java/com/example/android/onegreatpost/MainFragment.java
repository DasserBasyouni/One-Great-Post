package com.example.android.onegreatpost;

import android.app.Fragment;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.android.onegreatpost.RecyclerView.PostsAndCategoryAdapter;
import com.example.android.onegreatpost.Utils.LoginUtils;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.onegreatpost.Utils.ConnectionUtils.showNoConnection_withSnackBarFinalFix;


/**
   Created by Dasser on 02-Feb-18.
 */

public class MainFragment extends Fragment implements android.app.LoaderManager.LoaderCallbacks<Task<QuerySnapshot>> {

    //public static final String TAG = MainFragment.this.getClass().getSimpleName();
    public static final String TAG = "MainFragment";

    @BindView(R.id.spinner_nav) Spinner spinner;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.coordinator_layout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.spin_kit) SpinKitView spinKitView;
    @BindView(R.id.no_connection_iv) ImageView noConnection_iv;
    @BindView(R.id.refresh_btn) ImageButton refresh_btn;

    static Map<String, List<String>> postsListMap;
    public static List<String> spinnerPostsDatesList = null;
    private Snackbar snackbar;
    private RecyclerView recyclerView;
    static ArrayAdapter<String> spinnerArrayAdapter = null;
    private boolean isLoaderInitialized = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        ButterKnife.bind(this, getActivity());

        WanderingCubes wanderingCubes = new WanderingCubes();
        spinKitView.setIndeterminateDrawable(wanderingCubes);

        AppCompatActivity appCompatActivity = ((AppCompatActivity) getActivity());
        appCompatActivity.setSupportActionBar(toolbar);
        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
        appCompatActivity.setTitle(null);

        setupFAB();
        setupNavSpinner();
        setupRefreshToolbarButton();
        
        return rootView;
    }

    private void setupRefreshToolbarButton() {
        refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction()
                        .add(R.id.main_frame, new LoginFragment()).addToBackStack("WebViewFragment_tag").commit();
            }
        });
    }

    private void setupNavSpinner() {
        spinnerPostsDatesList = new ArrayList<>();
        spinnerPostsDatesList.add("Loading..");
        spinnerArrayAdapter = new ArrayAdapter<>
                (getActivity(), R.layout.spinner_item, spinnerPostsDatesList);

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setEnabled(false);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Z_TAG", "here 1");
                if(postsListMap != null)
                    getThenDisplayPostsFromHashMap(position);

                if(!isLoaderInitialized){
                    getLoaderManager().initLoader(0, null, MainFragment.this).forceLoad();
                    isLoaderInitialized = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getThenDisplayPostsFromHashMap(int position) {
        if(spinner.isEnabled()) {
            Log.wtf("Z_ts2", "enetered");
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(new PostsAndCategoryAdapter(postsListMap.get(spinnerPostsDatesList.get(position))
                    , true));
        }
        Log.wtf("Z_ccc 1", ""+recyclerView.getAdapter().getItemCount());
        showNoConnection_withSnackBarFinalFix(snackbar, recyclerView.getAdapter().getItemCount() == 0
                , spinKitView, noConnection_iv, coordinatorLayout);
    }

    private void setupFAB() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(coordinatorLayout, "Export Button is uncompleted for now", Snackbar.LENGTH_INDEFINITE)
                        .show();
            }
        });
    }


    @Override
    public Loader<Task<QuerySnapshot>> onCreateLoader(int id, Bundle args) {
        Log.d("Z_TAG", "here 2");
        return new asyncTask(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Task<QuerySnapshot>> loader, Task<QuerySnapshot> task) {
        task.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();

                clearSpinnerListExceptLoadingItem();

                postsListMap  = new HashMap<>();
                for (int i = 0; i < documents.size(); i++) {
                    putUncategorizedPostsInSpinner(documents, i);
                }
                spinnerPostsDatesList.remove(0);
                spinner.setEnabled(true);
                spinnerArrayAdapter.notifyDataSetChanged();
                getThenDisplayPostsFromHashMap(0);
                Log.wtf("Z_ccc 2", "" + recyclerView.getAdapter().getItemCount());
                showNoConnection_withSnackBarFinalFix(snackbar, recyclerView.getAdapter().getItemCount() == 0
                        , spinKitView, noConnection_iv, coordinatorLayout);
                spinKitView.setVisibility(View.GONE);
            }

            private void clearSpinnerListExceptLoadingItem() {
                for(int x=1 ;  x < spinnerPostsDatesList.size() ; x++)
                    spinnerPostsDatesList.remove(x);
            }

            private void putUncategorizedPostsInSpinner(List<DocumentSnapshot> documents, int i) {
                DocumentSnapshot documentSnapshot = documents.get(i);
                Map<String, Object> data = documents.get(i).getData();


                for (int t = 0; t < documentSnapshot.getData().size(); t++) {
                    if ((long) data.values().toArray()[t] == 0) {
                        if (t == 0) {
                            Log.d("Z_TAG", "here 4");
                            postsListMap.put(documentSnapshot.getId(), getListStringFromObjectList(data));
                            spinnerPostsDatesList.add(documentSnapshot.getId());
                            Log.d(TAG, "putUncategorizedPostsInSpinner_data: "
                                    + postsListMap.keySet() + " - " + postsListMap.values().toString());
                        }
                    }
                }
            }

            private List<String> getListStringFromObjectList(Map<String, Object> data) {
                List<String> strings = new ArrayList<>();
                for (int i = 0; i < data.size(); i++)
                    strings.add(String.valueOf(data.keySet().toArray()[i]));
                return strings;
            }
        });
        
    }

    @Override
    public void onLoaderReset(Loader<Task<QuerySnapshot>> loader) {

    }


    private static class asyncTask extends android.content.AsyncTaskLoader<Task<QuerySnapshot>> {

        asyncTask(Context context) {
            super(context);
        }

        @Override
        public Task<QuerySnapshot> loadInBackground() {
            Log.d("Z_TAG", "here 3");
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            return db.collection("posts").get();
        }
    }
}