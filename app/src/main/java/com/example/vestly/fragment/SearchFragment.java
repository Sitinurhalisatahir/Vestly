package com.example.vestly.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.example.vestly.repository.PhotoRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private List<Photo> photoList = new ArrayList<>();
    private PhotoRepository repository;
    private EditText etSearch;
    private LinearLayout layoutEmpty;
    private ProgressBar progressBar;
    private Button btnRetry;
    private SwipeRefreshLayout swipeRefresh;
    private String currentQuery = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view);
        etSearch = view.findViewById(R.id.et_search);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        progressBar = view.findViewById(R.id.progress_bar);
        btnRetry = view.findViewById(R.id.btn_retry);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);

        // Setup Swipe Refresh
        swipeRefresh.setOnRefreshListener(() -> {
            if (etSearch.getText().toString().trim().isEmpty()) {
                loadDefaultPhotos();
            } else {
                searchPhotos(currentQuery);
            }
        });

        // Setup RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        photoAdapter = new PhotoAdapter(getContext(), photoList, new PhotoAdapter.OnPhotoClickListener() {
            @Override
            public void onPhotoClick(Photo photo) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(DetailActivity.EXTRA_PHOTO_ID, photo.getId());
                intent.putExtra(DetailActivity.EXTRA_PHOTO_URL, photo.getSrc().getPortrait());
                intent.putExtra(DetailActivity.EXTRA_PHOTOGRAPHER, photo.getPhotographer());
                intent.putExtra(DetailActivity.EXTRA_CATEGORY, "search");
                startActivity(intent);
            }

            @Override
            public void onFavoriteClick(Photo photo, boolean isFavorite) {
                if (isFavorite) {
                    SharedPrefManager.getInstance(getContext()).addFavorite(
                            new FavoritePhoto(photo.getId(), photo.getPhotographer(),
                                    photo.getSrc().getPortrait(), "search")
                    );
                } else {
                    SharedPrefManager.getInstance(getContext()).removeFavorite(photo.getId());
                }
            }
        });
        recyclerView.setAdapter(photoAdapter);

        // Setup Repository
        repository = new PhotoRepository(getContext());

        // Tombol retry
        btnRetry.setOnClickListener(v -> {
            if (currentQuery.isEmpty()) {
                loadDefaultPhotos();
            } else {
                searchPhotos(currentQuery);
            }
        });

        // Load default fashion photos
        loadDefaultPhotos();

        // Search listener
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentQuery = s.toString().trim();
                if (currentQuery.length() > 2) {
                    searchPhotos(currentQuery);
                } else if (currentQuery.length() == 0) {
                    loadDefaultPhotos();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh adapter untuk update icon favorite
        if (photoAdapter != null) {
            photoAdapter.notifyDataSetChanged();
        }
    }

    // ========== CACHE METHODS ==========
    private void saveSearchToCache(String query, List<Photo> photos) {
        Gson gson = new Gson();
        String json = gson.toJson(photos);
        SharedPrefManager.getInstance(requireContext()).saveSearchCache(query, json);
    }

    private List<Photo> loadSearchFromCache(String query) {
        String json = SharedPrefManager.getInstance(requireContext()).getSearchCache(query);
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Photo>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private void loadDefaultPhotos() {
        showLoading();
        int randomPage = new Random().nextInt(10) + 1;
        repository.searchPhotos("fashion outfit style", randomPage, new PhotoRepository.PhotoCallback() {
            @Override
            public void onSuccess(PhotoResponse response) {
                requireActivity().runOnUiThread(() -> {
                    photoList.clear();
                    photoList.addAll(response.getPhotos());
                    photoAdapter.setPhotos(photoList);
                    saveSearchToCache("fashion outfit style", photoList);
                    showContent();
                });
            }

            @Override
            public void onError(String message) {
                requireActivity().runOnUiThread(() -> {
                    List<Photo> cachedPhotos = loadSearchFromCache("fashion outfit style");
                    if (!cachedPhotos.isEmpty()) {
                        photoList.clear();
                        photoList.addAll(cachedPhotos);
                        photoAdapter.setPhotos(photoList);
                        showContent();
                        Toast.makeText(getContext(), "Menampilkan data terakhir (offline mode)", Toast.LENGTH_SHORT).show();
                    } else {
                        showError();
                    }
                });
            }

            @Override
            public void onNoConnection() {
                requireActivity().runOnUiThread(() -> {
                    List<Photo> cachedPhotos = loadSearchFromCache("fashion outfit style");
                    if (!cachedPhotos.isEmpty()) {
                        photoList.clear();
                        photoList.addAll(cachedPhotos);
                        photoAdapter.setPhotos(photoList);
                        showContent();
                        Toast.makeText(getContext(), "Tidak ada koneksi. Menampilkan data terakhir.", Toast.LENGTH_LONG).show();
                    } else {
                        showError();
                    }
                });
            }
        });
    }

    private void searchPhotos(String query) {
        showLoading();
        int randomPage = new Random().nextInt(5) + 1;
        repository.searchPhotos(query, randomPage, new PhotoRepository.PhotoCallback() {
            @Override
            public void onSuccess(PhotoResponse response) {
                requireActivity().runOnUiThread(() -> {
                    photoList.clear();
                    photoList.addAll(response.getPhotos());
                    photoAdapter.setPhotos(photoList);
                    saveSearchToCache(query, photoList);

                    if (photoList.isEmpty()) {
                        showEmpty();
                    } else {
                        showContent();
                    }
                });
            }

            @Override
            public void onError(String message) {
                requireActivity().runOnUiThread(() -> {
                    List<Photo> cachedPhotos = loadSearchFromCache(query);
                    if (!cachedPhotos.isEmpty()) {
                        photoList.clear();
                        photoList.addAll(cachedPhotos);
                        photoAdapter.setPhotos(photoList);
                        showContent();
                        Toast.makeText(getContext(), "Menampilkan data terakhir untuk \"" + query + "\" (offline mode)", Toast.LENGTH_SHORT).show();
                    } else {
                        showError();
                    }
                });
            }

            @Override
            public void onNoConnection() {
                requireActivity().runOnUiThread(() -> {
                    List<Photo> cachedPhotos = loadSearchFromCache(query);
                    if (!cachedPhotos.isEmpty()) {
                        photoList.clear();
                        photoList.addAll(cachedPhotos);
                        photoAdapter.setPhotos(photoList);
                        showContent();
                        Toast.makeText(getContext(), "Tidak ada koneksi. Menampilkan data terakhir untuk \"" + query + "\"", Toast.LENGTH_LONG).show();
                    } else {
                        showError();
                    }
                });
            }
        });
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        if (swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }
    }

    private void showContent() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
        if (swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }
    }

    private void showError() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
        if (swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }
    }

    private void showEmpty() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
        TextView tvEmptyText = layoutEmpty.findViewById(R.id.tv_empty_text);
        if (tvEmptyText != null) {
            tvEmptyText.setText("No results found for \"" + currentQuery + "\"");
        }
        if (swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }
    }
}