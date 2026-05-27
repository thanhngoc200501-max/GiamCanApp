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

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private ImageView btnBack;
    private EditText edtEmail, edtPassword;
    private TextView tvdk;
    private Button btnDN;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);

        mAuth = FirebaseAuth.getInstance();

        btnBack = findViewById(R.id.btn_back);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        btnDN = findViewById(R.id.btn_sign_in);
        tvdk = findViewById(R.id.tv_go_to_signup);


        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnDN.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ Email và Mật khẩu", Toast.LENGTH_SHORT).show();
            } else {
                loginWithFirebase(email, password);
            }
        });

        tvdk.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });


    }
    private void loginWithFirebase(String email, String password) {
        Toast.makeText(LoginActivity.this, "Đang xử lý đăng nhập...", Toast.LENGTH_SHORT).show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định";
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }
}