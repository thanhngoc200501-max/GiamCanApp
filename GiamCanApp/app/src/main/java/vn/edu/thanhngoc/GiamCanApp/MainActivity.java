package vn.edu.thanhngoc.GiamCanApp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private YogaAdapter yogaAdapter;
    private FirebaseFirestore db;

    private TextView tvName;
    private ImageView imgAvatar;

    private BottomNavigationView bottomNavigationView;
    private TextView tvWorkoutTitle;
    private TextView tvWorkoutDescription;
    private ImageView imgWorkoutBanner;
    private MaterialButton btnStartWorkout;
    private RecyclerView rvExplore;
    private TextView tvHeight;
    private TextView tvWeight;
    private TextView tvAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
        initViews();
        setupBottomNavigation();
        loadDataFromFirestore();
        loadUserProfileInfo();
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        tvWorkoutTitle = findViewById(R.id.tv_workout_title);
        tvWorkoutDescription = findViewById(R.id.tv_workout_description);
        imgWorkoutBanner = findViewById(R.id.img_workout_banner);
        btnStartWorkout = findViewById(R.id.btn_start_workout);
        rvExplore = findViewById(R.id.rv_explore);

        tvName = findViewById(R.id.tv_name);
        imgAvatar = findViewById(R.id.img_avatar);
        tvHeight = findViewById(R.id.tv_height);
        tvWeight = findViewById(R.id.tv_weight);
        tvAge = findViewById(R.id.tv_age);
    }

    private void setupBottomNavigation() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);

            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    return true;
                } else if (itemId == R.id.nav_yoga) {
                    Intent intent = new Intent(MainActivity.this, CourseActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                }
                else if (itemId == R.id.nav_favorite) {
                    Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                }
                return false;
            });
        }
    }

    private void loadDataFromFirestore() {
        ArrayList<Yoga> listYoga = new ArrayList<>();
        db.collection("YogaCourses")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Yoga yoga = new Yoga();
                            yoga.setId(document.getId());
                            yoga.setTieuDe(document.getString("TieuDe"));
                            yoga.setMota(document.getString("mota"));
                            yoga.setPicPath(document.getString("picPath"));
                            yoga.setCapDo(document.getString("capDo"));
                            yoga.setMucTieu(document.getString("mucTieu"));

                            Double caloDouble = document.getDouble("calo");
                            if (caloDouble != null) {
                                yoga.setCalo(caloDouble.intValue());
                            }

                            ArrayList<Map<String, Object>> lessionsMapList =
                                    (ArrayList<Map<String, Object>>) document.get("lessions");

                            ArrayList<Lession> listLession = new ArrayList<>();
                            if (lessionsMapList != null) {
                                for (Map<String, Object> lessonMap : lessionsMapList) {
                                    String title = (String) lessonMap.get("TieuDe");
                                    String time = (String) lessonMap.get("ThoiGian");
                                    String link = (String) lessonMap.get("link");
                                    String pic = (String) lessonMap.get("picPath");

                                    listLession.add(new Lession(title, link, time, pic));
                                }
                            }
                            yoga.setLessions(listLession);
                            listYoga.add(yoga);
                        }

                        if (!listYoga.isEmpty()) {
                            setupFeaturedWorkout(listYoga.get(0));

                            if (listYoga.size() > 1) {
                                ArrayList<Yoga> exploreList = new ArrayList<>(listYoga.subList(1, listYoga.size()));
                                setupRecyclerView(exploreList);
                            }
                        }

                    }
                });
    }

    private void setupFeaturedWorkout(Yoga featuredYoga) {
        if (tvWorkoutTitle != null) {
            tvWorkoutTitle.setText(featuredYoga.getTieuDe());
        }

        if (featuredYoga.getMota() != null && tvWorkoutDescription != null) {
            tvWorkoutDescription.setText(featuredYoga.getMota());
        }

        if (featuredYoga.getPicPath() != null && imgWorkoutBanner != null) {
            int drawableId = getResources().getIdentifier(
                    featuredYoga.getPicPath(), "drawable", getPackageName());
            if (drawableId != 0) {
                imgWorkoutBanner.setImageResource(drawableId);
            }
        }

        if (btnStartWorkout != null) {
            btnStartWorkout.setOnClickListener(v -> {
                if (featuredYoga.getLessions() != null && !featuredYoga.getLessions().isEmpty()) {
                    Lession firstLesson = featuredYoga.getLessions().get(0);
                    Intent intent = new Intent(MainActivity.this, PlayVideoActivity.class);
                    intent.putExtra("video_url", firstLesson.getLink());
                    startActivity(intent);
                }
            });
        }
    }

    private void setupRecyclerView(ArrayList<Yoga> list) {
        if (rvExplore != null) {
            rvExplore.setLayoutManager(
                    new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            yogaAdapter = new YogaAdapter(list);
            rvExplore.setAdapter(yogaAdapter);
        }
    }

    private void loadUserProfileInfo() {
        com.google.firebase.auth.FirebaseAuth mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        com.google.firebase.auth.FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String userName = documentSnapshot.getString("displayName");
                            if (tvName != null) {
                                if (userName != null && !userName.trim().isEmpty()) {
                                    tvName.setText(userName);
                                } else {
                                    tvName.setText("Khách");
                                }
                            }

                            String avatarUrl = documentSnapshot.getString("avatarUrl");
                            if (imgAvatar != null && avatarUrl != null && !avatarUrl.isEmpty()) {
                                Glide.with(MainActivity.this)
                                        .load(avatarUrl)
                                        .placeholder(android.R.drawable.ic_menu_gallery)
                                        .into(imgAvatar);
                            }

                            Double height = documentSnapshot.getDouble("height");
                            Double weight = documentSnapshot.getDouble("weight");
                            Long age = documentSnapshot.getLong("age");

                            if (tvHeight != null && height != null) {
                                tvHeight.setText(String.format(java.util.Locale.US, "%.0f cm", height));
                            }

                            if (tvWeight != null && weight != null) {
                                tvWeight.setText(String.format(java.util.Locale.US, "%.0f kg", weight));
                            }

                            if (tvAge != null && age != null) {
                                tvAge.setText(String.format(java.util.Locale.US, "%d tuổi", age));
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (tvName != null) tvName.setText("Khách");
                    });

        } else {
            if (tvName != null) tvName.setText("Khách");
        }
    }
}