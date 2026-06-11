package com.example.vestly.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
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
import com.example.vestly.model.Photo;
import com.example.vestly.repository.FavoriteRepository;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private Context context;
    private List<Photo> photoList;
    private OnPhotoClickListener listener;
    private FavoriteRepository favoriteRepository;
    private List<Integer> favoriteIds;

    public interface OnPhotoClickListener {
        void onPhotoClick(Photo photo);
        void onFavoriteClick(Photo photo, boolean isFavorite);
    }

    public PhotoAdapter(Context context, List<Photo> photoList, OnPhotoClickListener listener) {
        this.context = context;
        this.photoList = photoList;
        this.listener = listener;
        this.favoriteRepository = new FavoriteRepository(context);
        loadFavoriteIds();
    }

    private void loadFavoriteIds() {
        android.util.Log.d("PhotoAdapter", "loadFavoriteIds mulai");
        favoriteRepository.getAllFavorites(new FavoriteRepository.OnFavoritesLoadedCallback() {
            @Override
            public void onLoaded(List<com.example.vestly.model.FavoritePhoto> favorites) {
                android.util.Log.d("PhotoAdapter", "Data favorit diterima, size: " + favorites.size());
                favoriteIds = new java.util.ArrayList<>();
                for (com.example.vestly.model.FavoritePhoto fav : favorites) {
                    favoriteIds.add(fav.getId());
                    android.util.Log.d("PhotoAdapter", "Favorite ID: " + fav.getId());
                }
                new Handler(Looper.getMainLooper()).post(() -> {
                    notifyDataSetChanged();
                    android.util.Log.d("PhotoAdapter", "notifyDataSetChanged selesai");
                });
            }
        });
    }

    public void refreshFavorites() {
        android.util.Log.d("PhotoAdapter", "refreshFavorites dipanggil");
        loadFavoriteIds();
    }

    private boolean isFavorite(int photoId) {
        return favoriteIds != null && favoriteIds.contains(photoId);
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

        boolean isFav = isFavorite(photo.getId());
        holder.btnFavorite.setSelected(isFav);

        holder.itemView.setOnClickListener(v -> listener.onPhotoClick(photo));

        holder.btnFavorite.setOnClickListener(v -> {
            boolean newState = !holder.btnFavorite.isSelected();
            holder.btnFavorite.setSelected(newState);
            listener.onFavoriteClick(photo, newState);
            if (newState) {
                if (favoriteIds != null && !favoriteIds.contains(photo.getId())) {
                    favoriteIds.add(photo.getId());
                }
            } else {
                if (favoriteIds != null) {
                    favoriteIds.remove(Integer.valueOf(photo.getId()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return photoList != null ? photoList.size() : 0;
    }

    public void setPhotos(List<Photo> photos) {
        this.photoList = photos;
        loadFavoriteIds();
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