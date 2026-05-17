package vn.edu.thanhngoc.GiamCanApp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class WelcomeActivity extends AppCompatActivity{
    private Button btnDangKy;
    private Button btnDangNhap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.welcome);
        btnDangNhap=findViewById(R.id.buttonDN);
        btnDangKy=findViewById(R.id.buttonDK);
        btnDangNhap.setOnClickListener(v -> {
            Intent intent=new Intent(WelcomeActivity.this,LoginActivity.class);
            startActivity(intent);
        });
        btnDangKy.setOnClickListener(v -> {
            Intent intent=new Intent(WelcomeActivity.this,RegisterActivity.class);
            startActivity(intent);
        });

    }
}
