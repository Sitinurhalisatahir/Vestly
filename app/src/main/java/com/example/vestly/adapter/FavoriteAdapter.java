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
import com.example.vestly.model.FavoritePhoto;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private Context context;
    private List<FavoritePhoto> favoriteList;
    private OnFavoriteClickListener listener;

    public interface OnFavoriteClickListener {
        void onPhotoClick(FavoritePhoto photo);
        void onRemoveFavorite(FavoritePhoto photo);
    }

    public FavoriteAdapter(Context context, List<FavoritePhoto> favoriteList, OnFavoriteClickListener listener) {
        this.context = context;
        this.favoriteList = favoriteList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        FavoritePhoto photo = favoriteList.get(position);

        // Load gambar
        Glide.with(context)
                .load(photo.getPortraitUrl())
                .placeholder(R.color.light_card)
                .into(holder.imgPhoto);

        // Nama photographer
        holder.tvPhotographer.setText("@" + photo.getPhotographer());

        // Favorit selalu selected (sudah tersimpan)
        holder.btnFavorite.setSelected(true);

        // Klik foto → detail
        holder.itemView.setOnClickListener(v -> listener.onPhotoClick(photo));

        // Klik favorit → hapus
        holder.btnFavorite.setOnClickListener(v -> {
            listener.onRemoveFavorite(photo);
            favoriteList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, favoriteList.size());
        });
    }

    @Override
    public int getItemCount() {
        return favoriteList != null ? favoriteList.size() : 0;
    }

    public void setFavorites(List<FavoritePhoto> favorites) {
        this.favoriteList = favorites;
        notifyDataSetChanged();
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPhoto;
        TextView tvPhotographer;
        ImageButton btnFavorite;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.img_photo);
            tvPhotographer = itemView.findViewById(R.id.tv_photographer);
            btnFavorite = itemView.findViewById(R.id.btn_favorite);
        }
    }
}