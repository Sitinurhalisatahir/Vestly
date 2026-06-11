package com.example.vestly.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.vestly.R;
import com.example.vestly.helper.SharedPrefManager;
import com.example.vestly.model.FavoritePhoto;
import com.example.vestly.model.Photo;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private Context context;
    private List<Photo> photoList;
    private OnPhotoClickListener listener;

    public interface OnPhotoClickListener {
        void onPhotoClick(Photo photo);
        void onFavoriteClick(Photo photo, boolean isFavorite);
    }

    public PhotoAdapter(Context context, List<Photo> photoList, OnPhotoClickListener listener) {
        this.context = context;
        this.photoList = photoList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Photo photo = photoList.get(position);

        Glide.with(context)
                .load(photo.getSrc().getPortrait())
                .placeholder(R.color.light_card)
                .into(holder.imgPhoto);

        holder.tvPhotographer.setText("@" + photo.getPhotographer());

        boolean isFav = SharedPrefManager.getInstance(context).isFavorite(photo.getId());
        holder.btnFavorite.setSelected(isFav);

        holder.itemView.setOnClickListener(v -> listener.onPhotoClick(photo));

        holder.btnFavorite.setOnClickListener(v -> {
            boolean newState = !holder.btnFavorite.isSelected();
            holder.btnFavorite.setSelected(newState);
            listener.onFavoriteClick(photo, newState);
        });
    }

    @Override
    public int getItemCount() {
        return photoList != null ? photoList.size() : 0;
    }

    public void setPhotos(List<Photo> photos) {
        this.photoList = photos;
        notifyDataSetChanged();
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPhoto;
        TextView tvPhotographer;
        ImageButton btnFavorite;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.img_photo);
            tvPhotographer = itemView.findViewById(R.id.tv_photographer);
            btnFavorite = itemView.findViewById(R.id.btn_favorite);
        }
    }
}