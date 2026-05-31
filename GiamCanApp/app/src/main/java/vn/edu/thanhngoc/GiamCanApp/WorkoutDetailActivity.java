package vn.edu.thanhngoc.GiamCanApp;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class WorkoutDetailActivity extends AppCompatActivity {

    private ImageView imgDetailBanner, btnDetailBack, btnDetailHeart;
    private TextView tvDetailTitle, tvDetailInfo, tvDetailDescription;
    private RecyclerView rvLessions;

    private LessionAdapter adapter;
    private Yoga yoga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_detail);

        imgDetailBanner = findViewById(R.id.imgDetailBanner);
        btnDetailBack = findViewById(R.id.btnDetailBack);
        btnDetailHeart = findViewById(R.id.btnDetailHeart);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailInfo = findViewById(R.id.tvDetailInfo);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        rvLessions = findViewById(R.id.rvLessions);

        yoga = (Yoga) getIntent().getSerializableExtra("object");

        if (yoga != null) {
            tvDetailTitle.setText(yoga.getTieuDe());
            tvDetailDescription.setText(yoga.getMota());

            int sizeLessions = yoga.getLessions() != null ? yoga.getLessions().size() : 0;
            tvDetailInfo.setText(sizeLessions + " Bài tập • " + yoga.getCalo() + " Kcal");

            if (yoga.getPicPath() != null) {
                int drawableId = getResources().getIdentifier(
                        yoga.getPicPath(), "drawable", getPackageName());
                if (drawableId != 0) {
                    imgDetailBanner.setImageResource(drawableId);
                }
            }
            if (yoga.isFavorite()) {
                btnDetailHeart.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FF3B30"))); // Màu đỏ
            } else {
                btnDetailHeart.setImageTintList(ColorStateList.valueOf(Color.WHITE)); // Màu trắng
            }

            btnDetailHeart.setOnClickListener(v -> {
                String currentUserId = FirebaseAuth.getInstance().getUid();
                if (currentUserId == null) {
                    Toast.makeText(WorkoutDetailActivity.this, "Vui lòng đăng nhập để lưu bài tập!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String courseId = yoga.getId();
                if (courseId == null) {
                    Toast.makeText(WorkoutDetailActivity.this, "Không tìm thấy ID bài tập!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String favDocumentId = currentUserId + "_" + courseId;
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                if (!yoga.isFavorite()) {
                    yoga.setFavorite(true);
                    btnDetailHeart.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FF3B30")));
                    Toast.makeText(WorkoutDetailActivity.this, "Đã thêm vào yêu thích!", Toast.LENGTH_SHORT).show();
                    Map<String, Object> favData = new HashMap<>();
                    favData.put("userId", currentUserId);
                    favData.put("courseId", courseId);
                    db.collection("favorites").document(favDocumentId)
                            .set(favData)
                            .addOnFailureListener(e -> {
                                yoga.setFavorite(false);
                                btnDetailHeart.setImageTintList(ColorStateList.valueOf(Color.WHITE));
                                Toast.makeText(WorkoutDetailActivity.this, "Lỗi kết nối, không thể lưu!", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    yoga.setFavorite(false);
                    btnDetailHeart.setImageTintList(ColorStateList.valueOf(Color.WHITE));
                    Toast.makeText(WorkoutDetailActivity.this, "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
                    db.collection("favorites").document(favDocumentId)
                            .delete()
                            .addOnFailureListener(e -> {
                                yoga.setFavorite(true);
                                btnDetailHeart.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FF3B30")));
                                Toast.makeText(WorkoutDetailActivity.this, "Hủy lưu thất bại!", Toast.LENGTH_SHORT).show();
                            });
                }
            });

            rvLessions.setLayoutManager(new LinearLayoutManager(this));
            adapter = new LessionAdapter(yoga.getLessions());
            rvLessions.setAdapter(adapter);
        }

        btnDetailBack.setOnClickListener(v -> finish());
    }
}