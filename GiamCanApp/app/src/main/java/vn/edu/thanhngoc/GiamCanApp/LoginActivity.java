package vn.edu.thanhngoc.GiamCanApp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private ImageView btnBack;
    private EditText edtEmail, edtPassword;
    private TextView tvQMK, tvdk;
    private Button btnDN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);
        btnBack = findViewById(R.id.btn_back);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        btnDN = findViewById(R.id.btn_sign_in);
        tvdk = findViewById(R.id.tv_go_to_signup);
        tvQMK = findViewById(R.id.tvQMK);
        btnBack.setOnClickListener(v -> {
            finish();
        });


        btnDN.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ Email và Mật khẩu", Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(LoginActivity.this, "Đang xử lý đăng nhập...", Toast.LENGTH_SHORT).show();
            }
        });
        tvdk.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });


        tvQMK.setOnClickListener(v -> {
            Toast.makeText(LoginActivity.this, "Chức năng khôi phục mật khẩu", Toast.LENGTH_SHORT).show();
        });
    }

}
