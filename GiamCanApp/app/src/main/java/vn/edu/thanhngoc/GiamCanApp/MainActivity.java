package vn.edu.thanhngoc.GiamCanApp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private YogaAdapter yogaAdapter;
    private FirebaseFirestore db;

    private BottomNavigationView bottomNavigationView;
    private TextView tvWorkoutTitle;
    private TextView tvWorkoutDescription;
    private ImageView imgWorkoutBanner;
    private MaterialButton btnStartWorkout;
    private RecyclerView rvExplore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        initViews();
        setupBottomNavigation();
        loadDataFromFirestore();
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        tvWorkoutTitle = findViewById(R.id.tv_workout_title);
        tvWorkoutDescription = findViewById(R.id.tv_workout_description);
        imgWorkoutBanner = findViewById(R.id.img_workout_banner);
        btnStartWorkout = findViewById(R.id.btn_start_workout);
        rvExplore = findViewById(R.id.rv_explore);
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
                            yoga.setTieuDe(document.getString("TieuDe"));
                            yoga.setMota(document.getString("mota"));
                            yoga.setPicPath(document.getString("picPath"));

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

                    } else {
                        Log.e("Firestore_Debug", "Lỗi: ", task.getException());
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
}