package vn.edu.thanhngoc.GiamCanApp;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import vn.edu.thanhngoc.GiamCanApp.databinding.ActivityPlayVideoBinding;

public class PlayVideoActivity extends AppCompatActivity {
    private ActivityPlayVideoBinding binding;
    private ExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        String videoUrl = getIntent().getStringExtra("video_url");


        binding.btnBack.setOnClickListener(v -> finish());

        if (videoUrl != null && !videoUrl.isEmpty()) {
            initializePlayer(videoUrl);
        }
    }

    private void initializePlayer(String url) {
        player = new ExoPlayer.Builder(this).build();
        binding.playerView.setPlayer(player);

        MediaItem mediaItem = MediaItem.fromUri(url);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.setPlayWhenReady(true);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.pause();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}