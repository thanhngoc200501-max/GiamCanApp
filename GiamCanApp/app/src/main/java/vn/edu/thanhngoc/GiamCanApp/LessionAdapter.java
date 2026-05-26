package vn.edu.thanhngoc.GiamCanApp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import vn.edu.thanhngoc.GiamCanApp.databinding.ItemLessionBinding;

public class LessionAdapter extends RecyclerView.Adapter<LessionAdapter.ViewHolder> {

    private ArrayList<Lession> list;
    private Context context;

    public LessionAdapter(ArrayList<Lession> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        ItemLessionBinding binding = ItemLessionBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Lession lession = list.get(position);

        holder.binding.tvLessionTitle.setText(lession.getTieuDe());
        holder.binding.tvLessionTime.setText(lession.getThoiGian());


        if (lession.getPicPath() != null) {
            int drawableId = context.getResources().getIdentifier(
                    lession.getPicPath(), "drawable", context.getPackageName());
            if (drawableId != 0) {
                holder.binding.imgLession.setImageResource(drawableId);
            }
        }
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlayVideoActivity.class);
            // Lấy link video từ đối tượng Lession và gửi sang PlayVideoActivity
            intent.putExtra("video_url", lession.getLink());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ItemLessionBinding binding;

        public ViewHolder(ItemLessionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}