package vn.edu.thanhngoc.GiamCanApp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText edtEmail, edtPassword, edtConfirmPassword;
    private Button btnDK;
    private TextView tvGoToLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        mAuth = FirebaseAuth.getInstance();
        btnBack = findViewById(R.id.btn_back_reg);
        edtEmail = findViewById(R.id.edt_email_reg);
        edtPassword = findViewById(R.id.edt_password_reg);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password_reg);
        btnDK = findViewById(R.id.btn_sign_up);
        tvGoToLogin = findViewById(R.id.tv_go_to_login);

        btnBack.setOnClickListener(v -> finish());
        tvGoToLogin.setOnClickListener(v -> finish());

        btnDK.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Vui lòng điền đầy đủ tất cả các ô!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "Mật khẩu nhập lại không trùng khớp!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(RegisterActivity.this, "Mật khẩu phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
                return;
            }
            registerWithFirebase(email, password);
        });
    }
    private void registerWithFirebase(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);

                        finish();
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định";
                        Toast.makeText(this, "Đăng ký thất bại: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }
}