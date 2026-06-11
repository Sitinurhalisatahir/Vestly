package com.example.vestly.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.vestly.R;
import com.example.vestly.adapter.PhotoAdapter;
import com.example.vestly.helper.SharedPrefManager;
import com.example.vestly.helper.ThemeManager;
import com.example.vestly.model.FavoritePhoto;
import com.example.vestly.model.Photo;
import com.example.vestly.model.PhotoResponse;
import com.example.vestly.repository.PhotoRepository;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_PHOTO_ID = "photo_id";
    public static final String EXTRA_PHOTO_URL = "photo_url";
    public static final String EXTRA_PHOTOGRAPHER = "photographer";
    public static final String EXTRA_CATEGORY = "category";

    private ImageView imgPhoto;
    private TextView tvPhotographer, tvDescription;
    private android.widget.Button btnFavorite, btnShare;
    private ImageButton btnBack;
    private RecyclerView recyclerSimilar;
    private PhotoAdapter similarAdapter;
    private List<Photo> similarList = new ArrayList<>();

    private int photoId;
    private String photoUrl, photographer, category;
    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.applyTheme(SharedPrefManager.getInstance(this).isDarkTheme());
        setContentView(R.layout.activity_detail);

        photoId = getIntent().getIntExtra(EXTRA_PHOTO_ID, 0);
        photoUrl = getIntent().getStringExtra(EXTRA_PHOTO_URL);
        photographer = getIntent().getStringExtra(EXTRA_PHOTOGRAPHER);
        category = getIntent().getStringExtra(EXTRA_CATEGORY);

        imgPhoto = findViewById(R.id.img_photo);
        tvPhotographer = findViewById(R.id.tv_photographer);
        tvDescription = findViewById(R.id.tv_description);
        btnFavorite = findViewById(R.id.btn_favorite);
        btnShare = findViewById(R.id.btn_share);
        btnBack = findViewById(R.id.btn_back);
        recyclerSimilar = findViewById(R.id.recycler_similar);

        
        Glide.with(this)
                .load(photoUrl)
                .placeholder(R.color.light_card)
                .centerCrop()
                .into(imgPhoto);

        tvPhotographer.setText("Photo by " + photographer);

        
        tvDescription.setText("Capturing the essence of modern fashion through a curated lens. This look explores the intersection of style and elegance.");

        
        isFavorite = SharedPrefManager.getInstance(this).isFavorite(photoId);
        updateFavoriteButton();

        
        btnBack.setOnClickListener(v -> finish());


        btnFavorite.setOnClickListener(v -> {
            isFavorite = !isFavorite;
            updateFavoriteButton();
            if (isFavorite) {
                SharedPrefManager.getInstance(this).addFavorite(
                        new FavoritePhoto(photoId, photographer, photoUrl, category)
                );
                Toast.makeText(this, "Added to favorites!", Toast.LENGTH_SHORT).show();
            } else {
                SharedPrefManager.getInstance(this).removeFavorite(photoId);
                Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
            }
        });

        
        btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "Check out this outfit inspiration from Vestly!\n" + photoUrl);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });


        recyclerSimilar.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        similarAdapter = new PhotoAdapter(this, similarList,
                new PhotoAdapter.OnPhotoClickListener() {
                    @Override
                    public void onPhotoClick(Photo photo) {
                        
                        Intent intent = new Intent(DetailActivity.this, DetailActivity.class);
                        intent.putExtra(EXTRA_PHOTO_ID, photo.getId());
                        intent.putExtra(EXTRA_PHOTO_URL, photo.getSrc().getPortrait());
                        intent.putExtra(EXTRA_PHOTOGRAPHER, photo.getPhotographer());
                        intent.putExtra(EXTRA_CATEGORY, category);
                        startActivity(intent);
                    }

                    @Override
                    public void onFavoriteClick(Photo photo, boolean isFav) {
                        if (isFav) {
                            SharedPrefManager.getInstance(DetailActivity.this).addFavorite(
                                    new FavoritePhoto(photo.getId(), photo.getPhotographer(),
                                            photo.getSrc().getPortrait(), category)
                            );
                        } else {
                            SharedPrefManager.getInstance(DetailActivity.this)
                                    .removeFavorite(photo.getId());
                        }
                    }
                });
        recyclerSimilar.setAdapter(similarAdapter);
        loadSimilarMoods();
    }

    private void updateFavoriteButton() {
        if (isFavorite) {
            btnFavorite.setText("❤️  Saved to Favorites");
            btnFavorite.setBackgroundColor(getResources().getColor(R.color.primary, null));
            btnFavorite.setTextColor(getResources().getColor(R.color.white, null));
        } else {
            btnFavorite.setText("🤍  Save to Favorites");
            btnFavorite.setBackgroundColor(getResources().getColor(R.color.primary, null));
            btnFavorite.setTextColor(getResources().getColor(R.color.white, null));
        }
    }

    private void loadSimilarMoods() {
        PhotoRepository repository = new PhotoRepository(this);
        repository.searchPhotos(category + " outfit", 2, new PhotoRepository.PhotoCallback() {
            @Override
            public void onSuccess(PhotoResponse response) {
                runOnUiThread(() -> {
                    similarList.clear();
                    // Ambil max 3 foto, skip foto yang sama
                    for (Photo p : response.getPhotos()) {
                        if (p.getId() != photoId && similarList.size() < 3) {
                            similarList.add(p);
                        }
                    }
                    similarAdapter.setPhotos(similarList);
                });
            }

            @Override
            public void onError(String message) {}

            @Override
            public void onNoConnection() {}
        });
    }
}