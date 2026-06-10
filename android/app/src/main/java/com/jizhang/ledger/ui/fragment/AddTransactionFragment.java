package com.jizhang.ledger.ui.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.jizhang.ledger.R;
import com.jizhang.ledger.model.*;
import com.jizhang.ledger.network.ApiResponse;
import com.jizhang.ledger.network.RetrofitClient;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.File;
import java.util.*;

public class AddTransactionFragment extends Fragment {
    private RadioGroup rgType;
    private RadioButton rbExpense, rbIncome;
    private EditText etAmount, etRemark;
    private Spinner spinnerCategory;
    private Button btnSelectDate, btnSubmit, btnSelectImage;
    private ProgressBar progressBar;
    private String selectedDate;
    private Map<String, List<String>> categories;
    private List<String> currentCategories = new ArrayList<>();
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        uploadAndRecognizeImage(imageUri);
                    }
                }
            }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        rgType = view.findViewById(R.id.rgType);
        rbExpense = view.findViewById(R.id.rbExpense);
        rbIncome = view.findViewById(R.id.rbIncome);
        etAmount = view.findViewById(R.id.etAmount);
        etRemark = view.findViewById(R.id.etRemark);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        btnSelectDate = view.findViewById(R.id.btnSelectDate);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnSelectImage = view.findViewById(R.id.btnSelectImage);
        progressBar = view.findViewById(R.id.progressBar);

        Calendar cal = Calendar.getInstance();
        selectedDate = String.format("%d-%02d-%02d", 
            cal.get(Calendar.YEAR), 
            cal.get(Calendar.MONTH) + 1, 
            cal.get(Calendar.DAY_OF_MONTH));
        btnSelectDate.setText(selectedDate);

        loadCategories();

        rgType.setOnCheckedChangeListener((group, checkedId) -> updateCategorySpinner());

        btnSelectDate.setOnClickListener(v -> showDatePicker());

        btnSubmit.setOnClickListener(v -> submitTransaction());

        btnSelectImage.setOnClickListener(v -> selectImage());

        return view;
    }

    private void loadCategories() {
        RetrofitClient.getApiService().getCategories().enqueue(new Callback<ApiResponse<Map<String, List<String>>>>() {
            @Override
            public void onResponse(Call<ApiResponse<Map<String, List<String>>>> call, Response<ApiResponse<Map<String, List<String>>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    categories = response.body().getData();
                    updateCategorySpinner();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Map<String, List<String>>>> call, Throwable t) {
                Toast.makeText(getContext(), "加载类目失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCategorySpinner() {
        if (categories == null) return;

        String type = rbExpense.isChecked() ? "expense" : "income";
        currentCategories = categories.get(type);
        
        if (currentCategories != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), 
                android.R.layout.simple_spinner_item, currentCategories);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(adapter);
        }
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        String[] parts = selectedDate.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]) - 1;
        int day = Integer.parseInt(parts[2]);

        DatePickerDialog dialog = new DatePickerDialog(getContext(), (view, y, m, d) -> {
            selectedDate = String.format("%d-%02d-%02d", y, m + 1, d);
            btnSelectDate.setText(selectedDate);
        }, year, month, day);

        dialog.show();
    }

    private void submitTransaction() {
        String amountStr = etAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(getContext(), "请输入金额", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                Toast.makeText(getContext(), "金额必须大于0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "金额格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }

        String type = rbExpense.isChecked() ? "EXPENSE" : "INCOME";
        String category = spinnerCategory.getSelectedItem().toString();
        String remark = etRemark.getText().toString().trim();

        TransactionRequest request = new TransactionRequest(type, category, amount, selectedDate, remark);

        RetrofitClient.getApiService().createTransaction(request).enqueue(new Callback<ApiResponse<Transaction>>() {
            @Override
            public void onResponse(Call<ApiResponse<Transaction>> call, Response<ApiResponse<Transaction>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "添加成功", Toast.LENGTH_SHORT).show();
                    clearForm();
                } else {
                    Toast.makeText(getContext(), "添加失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Transaction>> call, Throwable t) {
                Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearForm() {
        etAmount.setText("");
        etRemark.setText("");
        rbExpense.setChecked(true);
        Calendar cal = Calendar.getInstance();
        selectedDate = String.format("%d-%02d-%02d", 
            cal.get(Calendar.YEAR), 
            cal.get(Calendar.MONTH) + 1, 
            cal.get(Calendar.DAY_OF_MONTH));
        btnSelectDate.setText(selectedDate);
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void uploadAndRecognizeImage(Uri imageUri) {
        progressBar.setVisibility(View.VISIBLE);
        btnSelectImage.setEnabled(false);

        String imagePath = getRealPathFromUri(imageUri);
        if (imagePath == null) {
            Toast.makeText(getContext(), "无法获取图片路径", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            btnSelectImage.setEnabled(true);
            return;
        }

        File imageFile = new File(imagePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);

        RetrofitClient.getApiService().uploadImage(body).enqueue(new Callback<ApiResponse<UploadResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UploadResponse>> call, Response<ApiResponse<UploadResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    String imageUrl = response.body().getData().getImageUrl();
                    recognizeImage(imageUrl);
                } else {
                    Toast.makeText(getContext(), "图片上传失败", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    btnSelectImage.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UploadResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "上传失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                btnSelectImage.setEnabled(true);
            }
        });
    }

    private void recognizeImage(String imageUrl) {
        OcrRequest request = new OcrRequest(imageUrl);
        RetrofitClient.getApiService().recognizeImage(request).enqueue(new Callback<ApiResponse<OcrResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<OcrResponse>> call, Response<ApiResponse<OcrResponse>> response) {
                progressBar.setVisibility(View.GONE);
                btnSelectImage.setEnabled(true);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    OcrResponse ocrData = response.body().getData();
                    fillFormWithOcrData(ocrData);
                    Toast.makeText(getContext(), "识别成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "图片识别失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<OcrResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSelectImage.setEnabled(true);
                Toast.makeText(getContext(), "识别失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillFormWithOcrData(OcrResponse ocrData) {
        if (ocrData.getAmount() != null) {
            etAmount.setText(String.valueOf(ocrData.getAmount()));
        }
        
        if (ocrData.getDate() != null && !ocrData.getDate().isEmpty()) {
            selectedDate = ocrData.getDate();
            btnSelectDate.setText(selectedDate);
        }
        
        if (ocrData.getCategory() != null && !ocrData.getCategory().isEmpty()) {
            String category = ocrData.getCategory();
            if (currentCategories != null && currentCategories.contains(category)) {
                int position = currentCategories.indexOf(category);
                spinnerCategory.setSelection(position);
            }
        }
        
        if (ocrData.getMerchant() != null && !ocrData.getMerchant().isEmpty()) {
            etRemark.setText(ocrData.getMerchant());
        }
    }

    private String getRealPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
        return null;
    }
}
