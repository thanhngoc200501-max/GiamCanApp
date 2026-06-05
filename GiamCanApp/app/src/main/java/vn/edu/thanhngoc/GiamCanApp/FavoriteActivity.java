package vn.edu.thanhngoc.GiamCanApp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView rvFavorite;
    private LinearLayout layoutEmpty;

    private YogaAdapter adapter;
    private ArrayList<Yoga> favoriteList;
    private FirebaseFirestore db;
    private String currentUserId;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        rvFavorite = findViewById(R.id.rvFavorite);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getUid();
        favoriteList = new ArrayList<>();
        rvFavorite.setLayoutManager(new LinearLayoutManager(this));
        adapter = new YogaAdapter(favoriteList, true);
        rvFavorite.setAdapter(adapter);

        setupBottomNavigation();
    }

    private void loadFavoriteWorkouts() {
        if (currentUserId == null) {
            checkEmptyState();
            return;
        }

        db.collection("favorites")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> favoriteCourseIds = new ArrayList<>();

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        String courseId = doc.getString("courseId");
                        if (courseId != null) {
                            favoriteCourseIds.add(courseId);
                        }
                    }

                    if (favoriteCourseIds.isEmpty()) {
                        favoriteList.clear();
                        checkEmptyState();
                    } else {
                        fetchYogaCoursesDetails(favoriteCourseIds);
                    }
                })
                .addOnFailureListener(e -> {
                    checkEmptyState();
                });
    }

    private void fetchYogaCoursesDetails(List<String> courseIds) {
        db.collection("YogaCourses")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    favoriteList.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        if (courseIds.contains(doc.getId())) {
                            Yoga yoga = doc.toObject(Yoga.class);
                            if (yoga != null) {
                                yoga.setId(doc.getId());
                                yoga.setFavorite(true);
                                favoriteList.add(yoga);
                            }
                        }
                    }
                    checkEmptyState();
                })
                .addOnFailureListener(e -> {
                    checkEmptyState();
                });
    }

    private void checkEmptyState() {
        if (favoriteList == null || favoriteList.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvFavorite.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvFavorite.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }

    private void setupBottomNavigation() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_favorite);

            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_favorite) {
                    return true;
                } else if (itemId == R.id.nav_home) {
                    Intent intent = new Intent(FavoriteActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_yoga) {
                    Intent intent = new Intent(FavoriteActivity.this, CourseActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    Intent intent = new Intent(FavoriteActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                }
                return false;
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavoriteWorkouts();
    }
}