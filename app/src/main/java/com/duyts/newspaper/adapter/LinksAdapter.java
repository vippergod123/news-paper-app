package com.duyts.newspaper.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.PrecomputedText;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.duyts.newspaper.R;
import com.duyts.newspaper.model.LinkModel;
import com.duyts.newspaper.ui.main.MainActivityViewModel;
import com.duyts.newspaper.util.GlideApp;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LinksAdapter extends RecyclerView.Adapter<LinksAdapter.LinkViewHolder> {


    private Boolean isSelectedMode = false;
    private ArrayList<LinkModel> selectedLinks;
    private final Context context;
    private SortedList<LinkModel> links;
    private final Callback cb;

    public LinksAdapter(Context context, Callback cb) {
        this.context = context;
        this.selectedLinks = new ArrayList<>();
        this.cb = cb;
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
        LinkModel item = links.get(holder.getAdapterPosition());
        String url = item.getUrl();
        String title = item.getTitle();
        String imageLink = item.getImage();

        holder.linkTextView.setText(url);
        holder.titleTextView.setText(title);

        if (!TextUtils.isEmpty(imageLink)) {
            Glide.with(context).load(imageLink)
                    .apply(RequestOptions.centerCropTransform())
                    .circleCrop()
                    .into(holder.thumbnailImageView);
        }

        holder.itemView.setOnLongClickListener(v -> {
            if (!isSelectedMode) {
                ActionMode.Callback callback = new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        MenuInflater menuInflater = mode.getMenuInflater();
                        menuInflater.inflate(R.menu.is_selected_menu_main,menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        isSelectedMode = true;
                        selectedItem(holder,item);
                        return true;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_remove:
                                cb.onRemoveSelectedList(selectedLinks);
                                mode.finish();
                                break;
                            case R.id.action_remove_all:
                                cb.onRemoveAllList();
                                mode.finish();
                                break;
                        }
                        return true;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        isSelectedMode = false;
                        selectedLinks.clear();
                        notifyDataSetChanged();
                    }
                };
                ((AppCompatActivity)v.getContext()).startActionMode(callback);
                return true;
            }
            else {
                selectedItem(holder,item);
            }
            return false;
        });

        holder.itemView.setOnClickListener(v -> {
            if (isSelectedMode) {
                selectedItem(holder,item);
            }
            else {
                Log.d("CHRIS", "ITEM: " + item.getTitle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return links == null ? 0 : links.size();
    }


    @Override
    public void onViewAttachedToWindow(@NonNull @NotNull LinkViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        LinkModel item = links.get(holder.getAdapterPosition());
        String imageLink = item.getImage();

        if (!TextUtils.isEmpty(imageLink)) {
            Glide.with(context).load(imageLink)
                    .apply(RequestOptions.centerCropTransform())
                    .circleCrop()
                    .into(holder.thumbnailImageView);
        }


    }

    @Override
    public void onViewDetachedFromWindow(@NonNull @NotNull LinkViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        Glide.with(context)
                .clear(holder.thumbnailImageView);

//        holder.itemView.setOnLongClickListener(null);
//        holder.itemView.setOnClickListener(null);
    }


    private void selectedItem(LinkViewHolder v, LinkModel item) {
        ImageView isSelectedImageView = v.isSelectedImageView;
        if (isSelectedImageView.getVisibility() == View.GONE) {
            v.setSelected(true);
            v.itemView.setBackgroundColor(Color.LTGRAY);
            selectedLinks.add(item);
        }
        else {
            v.setSelected(false);
            v.itemView.setBackgroundColor(Color.TRANSPARENT);
            selectedLinks.remove(item);
        }
    }

    static class LinkViewHolder extends RecyclerView.ViewHolder {
        TextView linkTextView;
        TextView titleTextView;
        ImageView thumbnailImageView;
        ImageView isSelectedImageView;
        boolean isSelected;

        public LinkViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            linkTextView = itemView.findViewById(R.id.linkTextView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            thumbnailImageView = itemView.findViewById(R.id.thumbnailImageView);
            isSelectedImageView = itemView.findViewById(R.id.isSelectedImageView);
            isSelected = false;
        }

        public void setSelected(boolean isSelected) {
            this.isSelected = true;
            isSelectedImageView.setVisibility(isSelected?View.VISIBLE :View.GONE);
        }
    }

    public interface Callback {
        default void onRemoveSelectedList(List<LinkModel> selectedLinks){}
        default void onRemoveAllList(){}
        default void onChangeSelectedMode(boolean isSelectedMode){}
    }
}
