package com.jizhang.ledger.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.jizhang.ledger.R;
import com.jizhang.ledger.model.User;
import com.jizhang.ledger.network.ApiResponse;
import com.jizhang.ledger.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    private EditText etName, etAge, etOccupation;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etOccupation = findViewById(R.id.etOccupation);
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        btnSave = findViewById(R.id.btnSave);

        loadUserInfo();

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadUserInfo() {
        RetrofitClient.getApiService().getProfile().enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    etName.setText(user.getName());
                    if (user.getAge() != null) {
                        etAge.setText(String.valueOf(user.getAge()));
                    }
                    etOccupation.setText(user.getOccupation());
                    if ("MALE".equals(user.getGender())) {
                        rbMale.setChecked(true);
                    } else if ("FEMALE".equals(user.getGender())) {
                        rbFemale.setChecked(true);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile() {
        String name = etName.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String occupation = etOccupation.getText().toString().trim();
        
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "请输入姓名", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User();
        user.setName(name);
        
        if (!TextUtils.isEmpty(ageStr)) {
            try {
                user.setAge(Integer.parseInt(ageStr));
            } catch (NumberFormatException e) {
                Toast.makeText(this, "年龄格式不正确", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        
        user.setOccupation(occupation);
        
        int selectedId = rgGender.getCheckedRadioButtonId();
        if (selectedId == R.id.rbMale) {
            user.setGender("MALE");
        } else if (selectedId == R.id.rbFemale) {
            user.setGender("FEMALE");
        }

        btnSave.setEnabled(false);
        RetrofitClient.getApiService().updateProfile(user).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                btnSave.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(EditProfileActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "保存失败";
                    Toast.makeText(EditProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                btnSave.setEnabled(true);
                Toast.makeText(EditProfileActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
