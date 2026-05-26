package vn.edu.thanhngoc.GiamCanApp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Map;
import vn.edu.thanhngoc.GiamCanApp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private YogaAdapter yogaAdapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        db = FirebaseFirestore.getInstance();
        loadDataFromFirestore();
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

                            // 🔥 SỬA LỖI 1: Lấy đúng kiểu Double từ Firestore thay vì Long
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

                                    // 🔥 SỬA LỖI 2: Đổi vị trí 'link' lên trước 'time' để khớp chuẩn xác với Constructor của class Lession
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

    // Hàm thiết lập phần video Nổi Bật (Card to nhất)
    private void setupFeaturedWorkout(Yoga featuredYoga) {
        binding.tvWorkoutTitle.setText(featuredYoga.getTieuDe());

        if(featuredYoga.getMota() != null) {
            binding.tvWorkoutDescription.setText(featuredYoga.getMota());
        }

        if (featuredYoga.getPicPath() != null) {
            int drawableId = getResources().getIdentifier(
                    featuredYoga.getPicPath(), "drawable", getPackageName());
            if (drawableId != 0) {
                binding.imgWorkoutBanner.setImageResource(drawableId);
            }
        }


        binding.btnStartWorkout.setOnClickListener(v -> {
            if (featuredYoga.getLessions() != null && !featuredYoga.getLessions().isEmpty()) {
                Lession firstLesson = featuredYoga.getLessions().get(0);
                Intent intent = new Intent(MainActivity.this, PlayVideoActivity.class);
                intent.putExtra("video_url", firstLesson.getLink());
                startActivity(intent);
            }
        });
    }


    private void setupRecyclerView(ArrayList<Yoga> list) {
        binding.rvExplore.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        yogaAdapter = new YogaAdapter(list);
        binding.rvExplore.setAdapter(yogaAdapter);
    }
}