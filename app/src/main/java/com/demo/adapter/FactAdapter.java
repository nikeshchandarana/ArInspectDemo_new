package com.demo.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.demo.R;
import com.demo.model.FactData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * This class is usefull for lazy image loading in the list. And populating other data in the list
 * Downloading and displaying images code at line 69 load().
 * */


@SuppressWarnings("deprecation")
public class FactAdapter extends
        RecyclerView.Adapter<FactAdapter.ViewHolder> {
    private Activity activity;
    private ArrayList<FactData> data = new ArrayList<>();

    public FactAdapter(Activity activity, ArrayList<FactData> data) {
        this.activity = activity;
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = null;
        v = LayoutInflater.from(activity).inflate(R.layout.inflate_fact_list_item,
                null, false);
        return new ViewHolder(v, getItemViewType(i));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Nullable
        @BindView(R.id.textViewTitle)
        TextView textViewTitle;

        @Nullable
        @BindView(R.id.textViewDescription)
        TextView textViewDescription;

        @Nullable
        @BindView(R.id.factImage)
        ImageView factImage;

        public ViewHolder(final View convertView, final int i) {
            super(convertView);
            ButterKnife.bind(this, convertView);
        }
    }

    @Override
    public int getItemViewType(final int position) {
        return position;
    }

    private void bind(final ViewHolder v, final FactData factData) {
        v.textViewTitle.setText((TextUtils.isEmpty(factData.getTitle()) || factData.getTitle()==null) ? "Title Not Available" : factData.getTitle());
        v.textViewDescription.setText((TextUtils.isEmpty(factData.getDescription()) || factData.getDescription()==null)? "Description Not " +
                "Available" :
                factData.getDescription());
        if (factData.getImageHref().equalsIgnoreCase("NA")) {
            v.factImage.setVisibility(View.GONE);
        } else {
            v.factImage.setVisibility(View.VISIBLE);
            Glide.with(activity).load(factData.getImageHref()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    v.factImage.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean
                        isFirstResource) {
                    v.factImage.setVisibility(View.VISIBLE);
                    return false;
                }
            }).thumbnail(0.5f).into(v.factImage);
        }

    }

    @Override
    public void onBindViewHolder(final ViewHolder v, final int arg1) {
        bind(v, data.get(arg1));
    }

}