package vn.edu.thanhngoc.GiamCanApp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private EditText edtDisplayName;
    private EditText edtHeight;
    private EditText edtWeight;
    private EditText edtAge;
    private Spinner spinnerGender;
    private Spinner spinnerActivity;
    private MaterialButton btnSaveProfile;
    private MaterialButton btnLogout;

    private TextView tvUserName;
    private TextView tvUserEmail;
    private ImageView imgProfileAvatar;

    private CardView cardResult;
    private TextView tvResBmi;
    private TextView tvResStatus;
    private TextView tvResTdee;
    private TextView tvResTargetCalories;

    private BottomNavigationView bottomNavigationView;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private Uri imageUri;
    private String uploadedImageUrl = "";

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    imgProfileAvatar.setImageURI(imageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        initViews();
        setupSpinners();
        setupBottomNavigation();
        loadUserProfile();

        imgProfileAvatar.setOnClickListener(v -> openGallery());
        btnSaveProfile.setOnClickListener(v -> handleSaveAndCalculate());
        btnLogout.setOnClickListener(v -> handleLogout());
    }

    private void initViews() {
        edtDisplayName = findViewById(R.id.edt_display_name);
        edtHeight = findViewById(R.id.edt_height);
        edtWeight = findViewById(R.id.edt_weight);
        edtAge = findViewById(R.id.edt_age);
        spinnerGender = findViewById(R.id.spinner_gender);
        spinnerActivity = findViewById(R.id.spinner_activity);
        btnSaveProfile = findViewById(R.id.btn_save_profile);
        btnLogout = findViewById(R.id.btn_logout);

        tvUserName = findViewById(R.id.tv_user_name);
        tvUserEmail = findViewById(R.id.tv_user_email);
        imgProfileAvatar = findViewById(R.id.img_profile_avatar);

        cardResult = findViewById(R.id.card_result);
        tvResBmi = findViewById(R.id.tv_res_bmi);
        tvResStatus = findViewById(R.id.tv_res_status);
        tvResTdee = findViewById(R.id.tv_res_tdee);
        tvResTargetCalories = findViewById(R.id.tv_res_target_calories);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setupSpinners() {
        String[] genderOptions = {"Nữ", "Nam"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genderOptions);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        String[] activityOptions = {
                "Ít vận động",
                "Vận động nhẹ (Tập thể dục 1-3 ngày/tuần)",
                "Vận động vừa (Tập thể dục 3-5 ngày/tuần)",
                "Vận động nặng (Tập thể dục 6-7 ngày/tuần)"
        };
        ArrayAdapter<String> activityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, activityOptions);
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActivity.setAdapter(activityAdapter);
    }

    private void setupBottomNavigation() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_profile);

            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_profile) {
                    return true;
                } else if (itemId == R.id.nav_home) {
                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_yoga) {
                    Intent intent = new Intent(ProfileActivity.this, CourseActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_favorite) {
                    Intent intent = new Intent(ProfileActivity.this, FavoriteActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                }
                return false;
            });
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            if (currentUser.getEmail() != null) {
                tvUserEmail.setText(currentUser.getEmail());
            }
            String userId = currentUser.getUid();
            db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("displayName");
                    if (name != null) {
                        edtDisplayName.setText(name);
                        tvUserName.setText(name);
                    }
                    String avatarUrl = documentSnapshot.getString("avatarUrl");
                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        uploadedImageUrl = avatarUrl;
                        Glide.with(this).load(avatarUrl).into(imgProfileAvatar);
                    }
                    Double height = documentSnapshot.getDouble("height");
                    if (height != null) {
                        edtHeight.setText(String.format(Locale.US, "%.0f", height));
                    }
                    Double weight = documentSnapshot.getDouble("weight");
                    if (weight != null) {
                        edtWeight.setText(String.format(Locale.US, "%.1f", weight));
                    }
                    Long age = documentSnapshot.getLong("age");
                    if (age != null) {
                        edtAge.setText(String.valueOf(age));
                    }
                    String gender = documentSnapshot.getString("gender");
                    if (gender != null && spinnerGender != null) {
                        if (gender.equals("Nam")) {
                            spinnerGender.setSelection(1);
                        } else {
                            spinnerGender.setSelection(0);
                        }
                    }
                    String activityLevel = documentSnapshot.getString("activityLevel");
                    if (activityLevel != null && spinnerActivity != null) {
                        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerActivity.getAdapter();
                        if (adapter != null) {
                            int position = adapter.getPosition(activityLevel);
                            if (position >= 0) {
                                spinnerActivity.setSelection(position);
                            }
                        }
                    }

                    Double bmi = documentSnapshot.getDouble("bmi");
                    Double tdee = documentSnapshot.getDouble("tdee");
                    Double targetCalories = documentSnapshot.getDouble("targetCalories");
                    String healthStatus = documentSnapshot.getString("healthStatus");

                    if (bmi != null && tdee != null && targetCalories != null) {
                        tvResBmi.setText(String.format(Locale.US, "BMI: %.1f", bmi));
                        tvResStatus.setText(healthStatus != null ? healthStatus : "");
                        tvResTdee.setText(String.format(Locale.US, "TDEE: %.0f kcal", tdee));
                        tvResTargetCalories.setText(String.format(Locale.US, "Mục tiêu: %.0f kcal", targetCalories));
                        cardResult.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    private void handleSaveAndCalculate() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Chưa đăng nhập! Không thể lưu lên máy chủ.", Toast.LENGTH_SHORT).show();
            return;
        }
        String email = currentUser.getEmail() != null ? currentUser.getEmail() : "";

        String name = edtDisplayName.getText().toString().trim();
        String heightStr = edtHeight.getText().toString().trim();
        String weightStr = edtWeight.getText().toString().trim();
        String ageStr = edtAge.getText().toString().trim();

        if (name.isEmpty() || heightStr.isEmpty() || weightStr.isEmpty() || ageStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ toàn bộ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        double height, weight;
        int age;
        try {
            height = Double.parseDouble(heightStr);
            weight = Double.parseDouble(weightStr);
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập số hợp lệ cho Chiều cao, Cân nặng, Tuổi!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (height <= 0 || weight <= 0 || age <= 0) {
            Toast.makeText(this, "Các chỉ số cơ thể phải lớn hơn 0!", Toast.LENGTH_SHORT).show();
            return;
        }

        tvUserName.setText(name);
        tvUserEmail.setText(email);

        double bmi = weight / Math.pow(height / 100.0, 2);
        tvResBmi.setText(String.format(Locale.US, "BMI: %.1f", bmi));

        String status;
        int calorieAdjustment;
        if (bmi < 18.5) {
            status = "Trạng thái: Nhẹ cân ";
            calorieAdjustment = 300;
        } else if (bmi < 23.0) {
            status = "Trạng thái: Bình thường";
            calorieAdjustment = 0;
        } else if (bmi < 25.0) {
            status = "Trạng thái: Thừa cân nhẹ";
            calorieAdjustment = -250;
        } else {
            status = "Trạng thái: Béo phì";
            calorieAdjustment = -500;
        }
        tvResStatus.setText(status);

        boolean isMale = spinnerGender.getSelectedItemPosition() == 1;
        double bmr;
        if (isMale) {
            bmr = (10 * weight) + (6.25 * height) - (5 * age) + 5;
        } else {
            bmr = (10 * weight) + (6.25 * height) - (5 * age) - 161;
        }

        double activityMultiplier;
        switch (spinnerActivity.getSelectedItemPosition()) {
            case 0: activityMultiplier = 1.2; break;
            case 1: activityMultiplier = 1.375; break;
            case 2: activityMultiplier = 1.55; break;
            default: activityMultiplier = 1.725; break;
        }

        double tdee = bmr * activityMultiplier;
        double targetCalories = tdee + calorieAdjustment;

        tvResTdee.setText(String.format(Locale.US, "TDEE: %.0f kcal", tdee));
        tvResTargetCalories.setText(String.format(Locale.US, "Mục tiêu: %.0f kcal", targetCalories));

        cardResult.setVisibility(View.VISIBLE);

        String userId = currentUser.getUid();

        if (imageUri != null) {
            String localPath = saveImageToInternalStorage(imageUri, userId);
            if (localPath != null) {
                uploadedImageUrl = localPath;
            }
        }

        saveDataToFirestore(userId, name, email, height, weight, age, isMale, bmi, tdee, targetCalories, status, uploadedImageUrl);
    }

    private String saveImageToInternalStorage(Uri uri, String userId) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            File directory = getDir("profile_pics", Context.MODE_PRIVATE);
            File myPath = new File(directory, userId + ".jpg");

            FileOutputStream fos = new FileOutputStream(myPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
            return myPath.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveDataToFirestore(String userId, String name, String email, double height, double weight, int age, boolean isMale, double bmi, double tdee, double targetCalories, String status, String avatarUrl) {
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("displayName", name);
        userProfile.put("email", email);
        userProfile.put("height", height);
        userProfile.put("weight", weight);
        userProfile.put("age", age);
        userProfile.put("gender", isMale ? "Nam" : "Nữ");
        userProfile.put("activityLevel", spinnerActivity.getSelectedItem().toString());
        userProfile.put("bmi", bmi);
        userProfile.put("tdee", tdee);
        userProfile.put("targetCalories", targetCalories);
        userProfile.put("healthStatus", status);
        userProfile.put("avatarUrl", avatarUrl);
        userProfile.put("lastUpdated", System.currentTimeMillis());

        db.collection("users").document(userId)
                .set(userProfile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ProfileActivity.this, "Đã lưu hồ sơ thành công!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ProfileActivity.this, "Lỗi khi đồng bộ: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void handleLogout() {
        mAuth.signOut();
        Intent intent = new Intent(ProfileActivity.this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        Toast.makeText(this, "Đã đăng xuất tài khoản thành công!", Toast.LENGTH_SHORT).show();
    }
}