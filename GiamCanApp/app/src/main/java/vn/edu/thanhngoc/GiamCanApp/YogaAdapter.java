package vn.edu.thanhngoc.GiamCanApp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import vn.edu.thanhngoc.GiamCanApp.databinding.ItemOtherWorkoutBinding;

public class YogaAdapter extends RecyclerView.Adapter<YogaAdapter.ViewHolder> {

    private ArrayList<Yoga> list;
    private Context context;

    public YogaAdapter(ArrayList<Yoga> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ItemOtherWorkoutBinding binding = ItemOtherWorkoutBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Yoga yoga = list.get(position);


        holder.binding.TieuDe.setText(yoga.getTieuDe());


        int sizeLessions = 0;
        if (yoga.getLessions() != null) {
            sizeLessions = yoga.getLessions().size();
        }
        holder.binding.thoiGian.setText(sizeLessions + " bài tập");


        holder.binding.calo.setText(yoga.getCalo() + " kcal");


        if (yoga.getPicPath() != null) {
            int drawableResourceId = context.getResources().getIdentifier(
                    yoga.getPicPath(), "drawable", context.getPackageName());

            if (drawableResourceId != 0) {
                holder.binding.imageView2.setImageResource(drawableResourceId);
            } else {
                holder.binding.imageView2.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        }


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
        public final ItemOtherWorkoutBinding binding;

        public ViewHolder(ItemOtherWorkoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}