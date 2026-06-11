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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.vestly.R;
import com.example.vestly.activity.DetailActivity;
import com.example.vestly.adapter.PhotoAdapter;
import com.example.vestly.helper.SharedPrefManager;
import com.example.vestly.model.FavoritePhoto;
import com.example.vestly.model.Photo;
import com.example.vestly.model.PhotoResponse;
import com.example.vestly.repository.FavoriteRepository;
import com.example.vestly.repository.PhotoRepository;
import com.google.android.material.chip.Chip;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private List<Photo> photoList = new ArrayList<>();
    private PhotoRepository repository;
    private LinearLayout layoutError;
    private Button btnRetry;
    private SwipeRefreshLayout swipeRefresh;
    private String currentCategory = "fashion outfit style";
    private FavoriteRepository favoriteRepository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view);
        layoutError = view.findViewById(R.id.layout_error);
        btnRetry = view.findViewById(R.id.btn_retry);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);

        favoriteRepository = new FavoriteRepository(requireContext());

        swipeRefresh.setOnRefreshListener(() -> {
            if (currentCategory.equals("fashion outfit style")) {
                loadPhotos();
            } else {
                loadPhotosByCategory(currentCategory);
            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        photoAdapter = new PhotoAdapter(getContext(), photoList, new PhotoAdapter.OnPhotoClickListener() {
            @Override
            public void onPhotoClick(Photo photo) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(DetailActivity.EXTRA_PHOTO_ID, photo.getId());
                intent.putExtra(DetailActivity.EXTRA_POTO_URL, photo.getSrc().getPortrait());
                intent.putExtra(DetailActivity.EXTRA_PHOTOGRAPHER, photo.getPhotographer());
                intent.putExtra(DetailActivity.EXTRA_CATEGORY, currentCategory);
                startActivity(intent);
            }

            @Override
            public void onFavoriteClick(Photo photo, boolean isFavorite) {
                if (isFavorite) {
                    FavoritePhoto favoritePhoto = new FavoritePhoto(photo.getId(), photo.getPhotographer(),
                            photo.getSrc().getPortrait(), currentCategory);
                    favoriteRepository.insertFavorite(favoritePhoto, null);
                    Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                } else {
                    favoriteRepository.deleteFavorite(photo.getId(), null);
                    Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                }
            }
        });
        recyclerView.setAdapter(photoAdapter);

        repository = new PhotoRepository(getContext());
        setupChips(view);
        loadPhotos();
        btnRetry.setOnClickListener(v -> loadPhotos());
    }

    @Override
    public void onResume() {
        super.onResume();
        android.util.Log.d("TEST", "HomeFragment onResume dipanggil");
        if (photoAdapter != null) {
            android.util.Log.d("TEST", "refreshFavorites dipanggil");
            photoAdapter.refreshFavorites();
        }
    }

    private void setupChips(View view) {
        int[] chipIds = {R.id.chip_all, R.id.chip_casual, R.id.chip_formal,
                R.id.chip_streetwear, R.id.chip_minimalist, R.id.chip_hijab};
        String[] categories = {"fashion outfit style", "casual outfit", "formal outfit",
                "streetwear outfit", "minimalist outfit", "hijab outfit"};

        Chip chipAll = view.findViewById(R.id.chip_all);
        chipAll.setChecked(true);

        for (int i = 0; i < chipIds.length; i++) {
            int index = i;
            Chip chip = view.findViewById(chipIds[i]);
            chip.setOnClickListener(v -> {
                for (int id : chipIds) {
                    ((Chip) view.findViewById(id)).setChecked(false);
                }
                chip.setChecked(true);
                currentCategory = categories[index];
                loadPhotosByCategory(categories[index]);
            });
        }
    }

    private void loadPhotos() {
        showLoading();
        swipeRefresh.setRefreshing(false);
        int randomPage = new Random().nextInt(10) + 1;
        repository.searchPhotos("fashion outfit style", randomPage, new PhotoRepository.PhotoCallback() {
            @Override
            public void onSuccess(PhotoResponse response) {
                requireActivity().runOnUiThread(() -> {
                    photoList.clear();
                    photoList.addAll(response.getPhotos());
                    photoAdapter.setPhotos(photoList);
                    savePhotosToCache(photoList);
                    showContent();
                });
            }

            @Override
            public void onError(String message) {
                requireActivity().runOnUiThread(() -> showCachedPhotos());
            }

            @Override
            public void onNoConnection() {
                requireActivity().runOnUiThread(() -> showCachedPhotos());
            }
        });
    }

    private void loadPhotosByCategory(String category) {
        showLoading();
        swipeRefresh.setRefreshing(false);
        int randomPage = new Random().nextInt(10) + 1;
        repository.getPhotosByCategory(category, randomPage, new PhotoRepository.PhotoCallback() {
            @Override
            public void onSuccess(PhotoResponse response) {
                requireActivity().runOnUiThread(() -> {
                    photoList.clear();
                    photoList.addAll(response.getPhotos());
                    photoAdapter.setPhotos(photoList);
                    savePhotosToCache(photoList);
                    showContent();
                });
            }

            @Override
            public void onError(String message) {
                requireActivity().runOnUiThread(() -> showCachedPhotos());
            }

            @Override
            public void onNoConnection() {
                requireActivity().runOnUiThread(() -> showCachedPhotos());
            }
        });
    }

    private void showLoading() {
        recyclerView.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
    }

    private void showContent() {
        recyclerView.setVisibility(View.VISIBLE);
        layoutError.setVisibility(View.GONE);
        if (swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }
    }

    private void showError() {
        recyclerView.setVisibility(View.GONE);
        layoutError.setVisibility(View.VISIBLE);
        if (swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }
    }

    private void savePhotosToCache(List<Photo> photos) {
        Gson gson = new Gson();
        String json = gson.toJson(photos);
        SharedPrefManager.getInstance(requireContext()).saveHomeCache(json);
    }

    private List<Photo> loadPhotosFromCache() {
        String json = SharedPrefManager.getInstance(requireContext()).getHomeCache();
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Photo>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private void showCachedPhotos() {
        List<Photo> cachedPhotos = loadPhotosFromCache();
        if (cachedPhotos != null && !cachedPhotos.isEmpty()) {
            photoList.clear();
            photoList.addAll(cachedPhotos);
            photoAdapter.setPhotos(photoList);
            showContent();
        } else {
            showError();
        }
    }
}