package vn.edu.thanhngoc.GiamCanApp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText edtEmail, edtPassword, edtConfirmPassword;
    private Button btnDK;
    private TextView tvGoToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // Ánh xạ các View từ file XML
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
                Toast.makeText(RegisterActivity.this, "Mật khẩu nhập lại không được trùng khớp!", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

            finish();
        });
    }
}