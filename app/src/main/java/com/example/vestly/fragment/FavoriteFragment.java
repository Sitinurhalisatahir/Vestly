package com.example.vestly.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vestly.R;
import com.example.vestly.activity.DetailActivity;
import com.example.vestly.adapter.FavoriteAdapter;
import com.example.vestly.model.FavoritePhoto;
import com.example.vestly.repository.FavoriteRepository;
import java.util.List;

public class FavoriteFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavoriteAdapter favoriteAdapter;
    private LinearLayout layoutEmpty;
    private Button btnClearAll;
    private FavoriteRepository favoriteRepository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        btnClearAll = view.findViewById(R.id.btn_clear_all);

        favoriteRepository = new FavoriteRepository(requireContext());

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        loadFavorites();

        btnClearAll.setOnClickListener(v -> showClearAllDialog());
    }

    private void loadFavorites() {
        favoriteRepository.getAllFavorites(new FavoriteRepository.OnFavoritesLoadedCallback() {
            @Override
            public void onLoaded(List<FavoritePhoto> favorites) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (favorites.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        layoutEmpty.setVisibility(View.VISIBLE);
                        btnClearAll.setVisibility(View.GONE);
                        return;
                    }

                    recyclerView.setVisibility(View.VISIBLE);
                    layoutEmpty.setVisibility(View.GONE);
                    btnClearAll.setVisibility(View.VISIBLE);

                    favoriteAdapter = new FavoriteAdapter(getContext(), favorites,
                            new FavoriteAdapter.OnFavoriteClickListener() {
                                @Override
                                public void onPhotoClick(FavoritePhoto photo) {
                                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                                    intent.putExtra(DetailActivity.EXTRA_PHOTO_ID, photo.getId());
                                    intent.putExtra(DetailActivity.EXTRA_POTO_URL, photo.getPortraitUrl());
                                    intent.putExtra(DetailActivity.EXTRA_PHOTOGRAPHER, photo.getPhotographer());
                                    intent.putExtra(DetailActivity.EXTRA_CATEGORY, photo.getCategory());
                                    startActivity(intent);
                                }

                                @Override
                                public void onRemoveFavorite(FavoritePhoto photo) {
                                    favoriteRepository.deleteFavorite(photo.getId(), new FavoriteRepository.OnFavoriteCallback() {
                                        @Override
                                        public void onComplete() {
                                            new Handler(Looper.getMainLooper()).post(() -> loadFavorites());
                                        }
                                    });
                                }
                            });
                    recyclerView.setAdapter(favoriteAdapter);
                });
            }
        });
    }

    private void showClearAllDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Clear All Favorites")
                .setMessage("Are you sure you want to clear all favorites?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes, Clear", (dialog, which) -> {
                    favoriteRepository.deleteAllFavorites(new FavoriteRepository.OnFavoriteCallback() {
                        @Override
                        public void onComplete() {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                loadFavorites();
                                Toast.makeText(getContext(), "All favorites cleared", Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();
    }
}