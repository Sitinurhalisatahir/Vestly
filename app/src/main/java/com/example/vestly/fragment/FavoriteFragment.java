package com.example.vestly.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vestly.R;
import com.example.vestly.activity.DetailActivity;
import com.example.vestly.adapter.FavoriteAdapter;
import com.example.vestly.helper.SharedPrefManager;
import com.example.vestly.helper.ThemeManager;
import com.example.vestly.model.FavoritePhoto;
import java.util.List;

public class FavoriteFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavoriteAdapter favoriteAdapter;
    private LinearLayout layoutEmpty;
    private SwitchCompat switchTheme;
    private SharedPrefManager pref;

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
        switchTheme = view.findViewById(R.id.switch_theme);

        pref = SharedPrefManager.getInstance(requireContext());

        // Setup Theme Toggle
        setupThemeToggle();

        // Setup RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        loadFavorites();
    }

    private void setupThemeToggle() {
        if (switchTheme != null) {
            switchTheme.setChecked(pref.isDarkTheme());
            switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
                pref.setDarkTheme(isChecked);
                ThemeManager.applyTheme(isChecked);
                requireActivity().recreate();
            });
        }
    }

    private void loadFavorites() {
        List<FavoritePhoto> favorites = pref.getFavorites();

        if (favorites.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
            return;
        }

        recyclerView.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);

        favoriteAdapter = new FavoriteAdapter(getContext(), favorites,
                new FavoriteAdapter.OnFavoriteClickListener() {
                    @Override
                    public void onPhotoClick(FavoritePhoto photo) {
                        Intent intent = new Intent(getActivity(), DetailActivity.class);
                        intent.putExtra(DetailActivity.EXTRA_PHOTO_ID, photo.getId());
                        intent.putExtra(DetailActivity.EXTRA_PHOTO_URL, photo.getPortraitUrl());
                        intent.putExtra(DetailActivity.EXTRA_PHOTOGRAPHER, photo.getPhotographer());
                        intent.putExtra(DetailActivity.EXTRA_CATEGORY, photo.getCategory());
                        startActivity(intent);
                    }

                    @Override
                    public void onRemoveFavorite(FavoritePhoto photo) {
                        pref.removeFavorite(photo.getId());
                        loadFavorites(); // Refresh setelah hapus
                    }
                });
        recyclerView.setAdapter(favoriteAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();
    }
}