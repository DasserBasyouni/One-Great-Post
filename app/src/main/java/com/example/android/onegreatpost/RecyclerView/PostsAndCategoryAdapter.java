package com.example.android.onegreatpost.RecyclerView;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.android.onegreatpost.R;

import java.util.ArrayList;
import java.util.List;

/**
   Created by Dasser on 06-Feb-18.
 */

public class PostsAndCategoryAdapter extends RecyclerView.Adapter<PostsAndCategoryAdapter.ViewHolder> {

    private List<String> dataSet;
    private boolean isPosts;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView header_tv, listItem_tv;
        View view;

        ViewHolder(View v) {
            super(v);
            view = v;
            header_tv = v.findViewById(R.id.header_list_item_tv);
            if(isPosts)
                listItem_tv = v.findViewById(R.id.list_item_tv);
        }
    }

    public void add(int position, String item) {
        dataSet.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        dataSet.remove(position);
        notifyItemRemoved(position);
    }

    public PostsAndCategoryAdapter(List<String> dataSet, boolean isPosts) {
        this.dataSet = dataSet;
        this.isPosts = isPosts;
    }

    @Override
    public PostsAndCategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item_post, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String name = dataSet.get(position);
        holder.header_tv.setText(name);

        if (isPosts) {
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "clicked", Toast.LENGTH_SHORT).show();

                    List<String> stings = new ArrayList<>();
                    stings.add("test 1");
                    stings.add("test 2");

                    new MaterialDialog.Builder(v.getContext())
                            .title(name)
                            .adapter(new PostsAndCategoryAdapter(stings, false)
                                    , new LinearLayoutManager(v.getContext()))
                            .show();
                }
            });
        }else {
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "selected", Toast.LENGTH_SHORT).show();

                    holder.listItem_tv.setVisibility(View.VISIBLE);
                    holder.listItem_tv.setText(dataSet.get(position));

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}