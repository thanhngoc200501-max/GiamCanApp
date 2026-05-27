package vn.edu.thanhngoc.GiamCanApp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Map;

public class CourseActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private BottomNavigationView bottomNavigationView;
    private EditText edtSearch;
    private TextView tvResultTitle;
    private RecyclerView rvByGoal, rvByLevel, rvRelax, rvPopular, rvExploreMore;

    private TextView tvTitleLevel, tvTitleRelax, tvTitlePopular, tvTitleExplore;

    private TextView tvLevelAll, tvLevelBeginner, tvLevelMedium, tvLevelAdvanced;
    private TextView tvGoalAll, tvGoalFatBurn, tvGoalFlexibility, tvGoalRelax;

    private ArrayList<Yoga> masterYogaList = new ArrayList<>();

    private String selectedLevel = "Tất cả";
    private String selectedGoal = "Mọi mục tiêu";
    private String searchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        db = FirebaseFirestore.getInstance();
        initViews();
        setupBottomNavigation();
        setupFilterClickListeners();
        setupSearchListener();
        loadDataFromFirestore();
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        edtSearch = findViewById(R.id.edt_search);
        tvResultTitle = findViewById(R.id.tvResultTitle);

        rvByGoal = findViewById(R.id.rvByGoal);
        rvByLevel = findViewById(R.id.rvByLevel);
        rvRelax = findViewById(R.id.rvRelax);
        rvPopular = findViewById(R.id.rvPopular);
        rvExploreMore = findViewById(R.id.rvExploreMore);

        tvTitleLevel = findViewById(R.id.tvTitleLevel);
        tvTitleRelax = findViewById(R.id.tvTitleRelax);
        tvTitlePopular = findViewById(R.id.tvTitlePopular);
        tvTitleExplore = findViewById(R.id.tvTitleExplore);

        tvLevelAll = findViewById(R.id.tvLevelAll);
        tvLevelBeginner = findViewById(R.id.tvLevelBeginner);
        tvLevelMedium = findViewById(R.id.tvLevelMedium);
        tvLevelAdvanced = findViewById(R.id.tvLevelAdvanced);

        tvGoalAll = findViewById(R.id.tvGoalAll);
        tvGoalFatBurn = findViewById(R.id.tvGoalFatBurn);
        tvGoalFlexibility = findViewById(R.id.tvGoalFlexibility);
        tvGoalRelax = findViewById(R.id.tvGoalRelax);
    }

    private void setupBottomNavigation() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_yoga);

            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_yoga) {
                    return true;
                } else if (itemId == R.id.nav_home) {
                    Intent intent = new Intent(CourseActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    Intent intent = new Intent(CourseActivity.this, ProfileActivity.class);
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
        db.collection("YogaCourses")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        masterYogaList.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Yoga yoga = new Yoga();
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
                            masterYogaList.add(yoga);
                        }
                        filterAndRefreshUI();

                    } else {
                        Log.e("Firestore_Debug", "Lỗi tải dữ liệu: ", task.getException());
                    }
                });
    }

    private void filterAndRefreshUI() {
        boolean isFiltering = !selectedLevel.equals("Tất cả") || !selectedGoal.equals("Mọi mục tiêu") || !searchQuery.isEmpty();

        if (isFiltering) {
            ArrayList<Yoga> filteredList = new ArrayList<>();
            for (Yoga yoga : masterYogaList) {
                boolean matchesLevel = selectedLevel.equals("Tất cả") ||
                        (yoga.getCapDo() != null && yoga.getCapDo().equalsIgnoreCase(selectedLevel));

                boolean matchesGoal = selectedGoal.equals("Mọi mục tiêu") ||
                        (yoga.getMucTieu() != null && yoga.getMucTieu().equalsIgnoreCase(selectedGoal));

                boolean matchesSearch = searchQuery.isEmpty() ||
                        (yoga.getTieuDe() != null && yoga.getTieuDe().toLowerCase().contains(searchQuery.toLowerCase())) ||
                        (yoga.getMota() != null && yoga.getMota().toLowerCase().contains(searchQuery.toLowerCase()));

                if (matchesLevel && matchesGoal && matchesSearch) {
                    filteredList.add(yoga);
                }
            }

            if (tvResultTitle != null) tvResultTitle.setText("Kết quả tìm thấy");

            setupRecyclerView(rvByGoal, filteredList);
            setAlternativeListsVisibility(View.GONE);

        } else {
            if (tvResultTitle != null) tvResultTitle.setText("Theo Mục Tiêu");

            setAlternativeListsVisibility(View.VISIBLE);

            ArrayList<Yoga> goalList = new ArrayList<>();
            ArrayList<Yoga> levelList = new ArrayList<>();
            ArrayList<Yoga> relaxList = new ArrayList<>();
            ArrayList<Yoga> popularList = new ArrayList<>();
            ArrayList<Yoga> exploreMoreList = new ArrayList<>(masterYogaList);

            for (Yoga yoga : masterYogaList) {
                if (yoga.getMucTieu() != null && yoga.getMucTieu().contains("Giảm mỡ bụng")) {
                    goalList.add(yoga);
                }
                if (yoga.getCapDo() != null && yoga.getCapDo().contains("Người mới")) {
                    levelList.add(yoga);
                }
                if (yoga.getMucTieu() != null && yoga.getMucTieu().contains("Thư giãn")) {
                    relaxList.add(yoga);
                }
                if (yoga.getCalo() > 200) {
                    popularList.add(yoga);
                }
            }

            setupRecyclerView(rvByGoal, goalList);
            setupRecyclerView(rvByLevel, levelList);
            setupRecyclerView(rvRelax, relaxList);
            setupRecyclerView(rvPopular, popularList);
            setupRecyclerView(rvExploreMore, exploreMoreList);
        }
    }

    private void setAlternativeListsVisibility(int visibility) {
        if (rvByLevel != null) rvByLevel.setVisibility(visibility);
        if (rvRelax != null) rvRelax.setVisibility(visibility);
        if (rvPopular != null) rvPopular.setVisibility(visibility);
        if (rvExploreMore != null) rvExploreMore.setVisibility(visibility);

        if (tvTitleLevel != null) tvTitleLevel.setVisibility(visibility);
        if (tvTitleRelax != null) tvTitleRelax.setVisibility(visibility);
        if (tvTitlePopular != null) tvTitlePopular.setVisibility(visibility);
        if (tvTitleExplore != null) tvTitleExplore.setVisibility(visibility);
    }

    private void setupRecyclerView(RecyclerView recyclerView, ArrayList<Yoga> list) {
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            YogaAdapter adapter = new YogaAdapter(list);
            recyclerView.setAdapter(adapter);
        }
    }

    private void setupSearchListener() {
        if (edtSearch != null) {
            edtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    searchQuery = s.toString().trim();
                    filterAndRefreshUI();
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }

    private void setupFilterClickListeners() {
        if (tvLevelAll != null) tvLevelAll.setOnClickListener(v -> updateLevelFilter("Tất cả", tvLevelAll));
        if (tvLevelBeginner != null) tvLevelBeginner.setOnClickListener(v -> updateLevelFilter("Người mới", tvLevelBeginner));
        if (tvLevelMedium != null) tvLevelMedium.setOnClickListener(v -> updateLevelFilter("Trung bình", tvLevelMedium));
        if (tvLevelAdvanced != null) tvLevelAdvanced.setOnClickListener(v -> updateLevelFilter("Nâng cao", tvLevelAdvanced));

        if (tvGoalAll != null) tvGoalAll.setOnClickListener(v -> updateGoalFilter("Mọi mục tiêu", tvGoalAll));
        if (tvGoalFatBurn != null) tvGoalFatBurn.setOnClickListener(v -> updateGoalFilter("Giảm mỡ bụng", tvGoalFatBurn));
        if (tvGoalFlexibility != null) tvGoalFlexibility.setOnClickListener(v -> updateGoalFilter("Tăng độ dẻo", tvGoalFlexibility));
        if (tvGoalRelax != null) tvGoalRelax.setOnClickListener(v -> updateGoalFilter("Thư giãn", tvGoalRelax));
    }

    private void updateLevelFilter(String level, TextView clickedTextView) {
        selectedLevel = level;

        if (tvLevelAll != null) {
            tvLevelAll.setBackgroundResource(R.drawable.bg_filter_unselected);
            tvLevelAll.setTextColor(Color.parseColor("#A099B0"));
        }
        if (tvLevelBeginner != null) {
            tvLevelBeginner.setBackgroundResource(R.drawable.bg_filter_unselected);
            tvLevelBeginner.setTextColor(Color.parseColor("#A099B0"));
        }
        if (tvLevelMedium != null) {
            tvLevelMedium.setBackgroundResource(R.drawable.bg_filter_unselected);
            tvLevelMedium.setTextColor(Color.parseColor("#A099B0"));
        }
        if (tvLevelAdvanced != null) {
            tvLevelAdvanced.setBackgroundResource(R.drawable.bg_filter_unselected);
            tvLevelAdvanced.setTextColor(Color.parseColor("#A099B0"));
        }

        if (clickedTextView != null) {
            clickedTextView.setBackgroundResource(R.drawable.bg_gradient_button);
            clickedTextView.setTextColor(Color.WHITE);
        }

        filterAndRefreshUI();
    }

    private void updateGoalFilter(String goal, TextView clickedTextView) {
        selectedGoal = goal;

        if (tvGoalAll != null) {
            tvGoalAll.setBackgroundResource(R.drawable.bg_filter_unselected);
            tvGoalAll.setTextColor(Color.parseColor("#A099B0"));
        }
        if (tvGoalFatBurn != null) {
            tvGoalFatBurn.setBackgroundResource(R.drawable.bg_filter_unselected);
            tvGoalFatBurn.setTextColor(Color.parseColor("#A099B0"));
        }
        if (tvGoalFlexibility != null) {
            tvGoalFlexibility.setBackgroundResource(R.drawable.bg_filter_unselected);
            tvGoalFlexibility.setTextColor(Color.parseColor("#A099B0"));
        }
        if (tvGoalRelax != null) {
            tvGoalRelax.setBackgroundResource(R.drawable.bg_filter_unselected);
            tvGoalRelax.setTextColor(Color.parseColor("#A099B0"));
        }

        if (clickedTextView != null) {
            clickedTextView.setBackgroundResource(R.drawable.bg_gradient_button);
            clickedTextView.setTextColor(Color.WHITE);
        }
        filterAndRefreshUI();
    }
}