package com.duyts.newspaper.adapter;

import android.content.Context;
import android.text.PrecomputedText;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.duyts.newspaper.R;
import com.duyts.newspaper.model.LinkModel;
import com.duyts.newspaper.ui.main.MainActivityViewModel;

import org.jetbrains.annotations.NotNull;

public class LinksAdapter extends RecyclerView.Adapter<LinksAdapter.LinkViewHolder> {

    private final Context context;
    private SortedList<LinkModel> links;

    public LinksAdapter(Context context) {
        this.context = context;
    }

    public void setLinks(SortedList<LinkModel> links) {
        this.links = links;
    }

    @NonNull
    @Override
    public LinkViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new LinkViewHolder(inflater.inflate(R.layout.url_view_holder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull LinkViewHolder holder, int position) {
        String url = links.get(position).getUrl();
        String title = links.get(position).getTitle();
        String imageLink = links.get(position).getImage();

        holder.linkTextView.setText(url);
        holder.titleTextView.setText(title);

        if (!TextUtils.isEmpty(imageLink)) {
            Glide.with(context).load(imageLink)
                    .apply(RequestOptions.centerCropTransform())
                    .circleCrop()
                    .into(holder.thumbnailImageView);
        }
    }

    @Override
    public int getItemCount() {
        return links == null ? 0 : links.size();
    }

    static class LinkViewHolder extends RecyclerView.ViewHolder {
        TextView linkTextView;
        TextView titleTextView;
        ImageView thumbnailImageView;

        public LinkViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            linkTextView = itemView.findViewById(R.id.linkTextView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            thumbnailImageView = itemView.findViewById(R.id.thumbnailImageView);
        }
    }
}
