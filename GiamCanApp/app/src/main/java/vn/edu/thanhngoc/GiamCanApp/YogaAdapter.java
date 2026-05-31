package vn.edu.thanhngoc.GiamCanApp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.res.ColorStateList;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class YogaAdapter extends RecyclerView.Adapter<YogaAdapter.ViewHolder> {

    private ArrayList<Yoga> list;
    private Context context;
    private boolean isFavoriteScreen;

    // Định nghĩa hằng số phân biệt loại View
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_FAVORITE = 1;

    public YogaAdapter(ArrayList<Yoga> list) {
        this.list = list;
        this.isFavoriteScreen = false;
    }

    public YogaAdapter(ArrayList<Yoga> list, boolean isFavoriteScreen) {
        this.list = list;
        this.isFavoriteScreen = isFavoriteScreen;
    }

    // THÊM HÀM NÀY: Ép RecyclerView phân tách vùng nhớ layout, chống lỗi tái chế kích thước nhỏ
    @Override
    public int getItemViewType(int position) {
        if (isFavoriteScreen) {
            return TYPE_FAVORITE;
        } else {
            return TYPE_NORMAL;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutId;

        // Dựa vào viewType được phân tách để nạp chuẩn layout
        if (viewType == TYPE_FAVORITE) {
            layoutId = R.layout.item_favorite_workout;
        } else {
            layoutId = R.layout.item_other_workout;
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Yoga yoga = list.get(position);

        holder.tvTieuDe.setText(yoga.getTieuDe());

        int sizeLessions = 0;
        if (yoga.getLessions() != null) {
            sizeLessions = yoga.getLessions().size();
        }
        holder.tvThoiGian.setText(sizeLessions + " bài tập");

        holder.tvCalo.setText(yoga.getCalo() + " kcal");

        holder.tvLevel.setText(yoga.getCapDo());
        holder.tvTag.setText(yoga.getMucTieu());
        holder.tvDescription.setText(yoga.getMota());

        if (yoga.getPicPath() != null) {
            int drawableResourceId = context.getResources().getIdentifier(
                    yoga.getPicPath(), "drawable", context.getPackageName());

            if (drawableResourceId != 0) {
                holder.imageView2.setImageResource(drawableResourceId);
            } else {
                holder.imageView2.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        }

        if (yoga.isFavorite()) {
            holder.btnHeart.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FF3B30")));
        } else {
            holder.btnHeart.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        }

        holder.btnHeart.setOnClickListener(v -> {
            String currentUserId = FirebaseAuth.getInstance().getUid();
            if (currentUserId == null) {
                Toast.makeText(context, "Vui lòng đăng nhập để lưu bài tập!", Toast.LENGTH_SHORT).show();
                return;
            }
            String courseId = yoga.getId();
            if (courseId == null) {
                Toast.makeText(context, "Không tìm thấy ID bài tập!", Toast.LENGTH_SHORT).show();
                return;
            }

            String favDocumentId = currentUserId + "_" + courseId;
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            if (!yoga.isFavorite()) {
                yoga.setFavorite(true);
                holder.btnHeart.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FF3B30")));
                Toast.makeText(context, "Đã thêm vào yêu thích!", Toast.LENGTH_SHORT).show();
                Map<String, Object> favData = new HashMap<>();
                favData.put("userId", currentUserId);
                favData.put("courseId", courseId);

                db.collection("favorites").document(favDocumentId)
                        .set(favData)
                        .addOnFailureListener(e -> {
                            yoga.setFavorite(false);
                            holder.btnHeart.setImageTintList(ColorStateList.valueOf(Color.WHITE));
                            Toast.makeText(context, "Lỗi kết nối mạng, không thể lưu bài tập!", Toast.LENGTH_SHORT).show();
                        });
            } else  {
                yoga.setFavorite(false);
                holder.btnHeart.setImageTintList(ColorStateList.valueOf(Color.WHITE));
                Toast.makeText(context, "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
                db.collection("favorites").document(favDocumentId)
                        .delete()
                        .addOnFailureListener(e -> {
                            yoga.setFavorite(true);
                            holder.btnHeart.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FF3B30")));
                            Toast.makeText(context, "Hủy lưu thất bại, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                        });
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, WorkoutDetailActivity.class);
            intent.putExtra("object", yoga);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTieuDe, tvThoiGian, tvCalo, tvLevel, tvTag, tvDescription;
        ImageView imageView2, btnHeart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTieuDe = itemView.findViewById(R.id.TieuDe);
            tvThoiGian = itemView.findViewById(R.id.thoiGian);
            tvCalo = itemView.findViewById(R.id.calo);
            tvLevel = itemView.findViewById(R.id.tv_level);
            tvTag = itemView.findViewById(R.id.tv_tag);
            tvDescription = itemView.findViewById(R.id.tv_description);
            imageView2 = itemView.findViewById(R.id.imageView2);
            btnHeart = itemView.findViewById(R.id.btn_heart);
        }
    }
}