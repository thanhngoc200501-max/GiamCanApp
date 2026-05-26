package vn.edu.thanhngoc.GiamCanApp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import vn.edu.thanhngoc.GiamCanApp.databinding.ActivityWorkoutDetailBinding;

public class WorkoutDetailActivity extends AppCompatActivity {
    private ActivityWorkoutDetailBinding binding;
    private LessionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWorkoutDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Yoga yoga = (Yoga) getIntent().getSerializableExtra("object");

        if (yoga != null) {

            binding.tvDetailTitle.setText(yoga.getTieuDe());
            binding.tvDetailDescription.setText(yoga.getMota());

            int sizeLessions = yoga.getLessions() != null ? yoga.getLessions().size() : 0;
            binding.tvDetailInfo.setText(sizeLessions + " Bài tập • " + yoga.getCalo() + " Kcal");

            if (yoga.getPicPath() != null) {
                int drawableId = getResources().getIdentifier(
                        yoga.getPicPath(), "drawable", getPackageName());
                if (drawableId != 0) {
                    binding.imgDetailBanner.setImageResource(drawableId);
                }
            }


            binding.rvLessions.setLayoutManager(new LinearLayoutManager(this));
            adapter = new LessionAdapter(yoga.getLessions());
            binding.rvLessions.setAdapter(adapter);
        }

        binding.btnDetailBack.setOnClickListener(v -> finish());
    }
}