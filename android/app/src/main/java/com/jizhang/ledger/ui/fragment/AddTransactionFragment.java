package com.jizhang.ledger.ui.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.jizhang.ledger.R;
import com.jizhang.ledger.model.Transaction;
import com.jizhang.ledger.model.TransactionRequest;
import com.jizhang.ledger.network.ApiResponse;
import com.jizhang.ledger.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.*;

public class AddTransactionFragment extends Fragment {
    private RadioGroup rgType;
    private RadioButton rbExpense, rbIncome;
    private EditText etAmount, etRemark;
    private Spinner spinnerCategory;
    private Button btnSelectDate, btnSubmit;
    private String selectedDate;
    private Map<String, List<String>> categories;
    private List<String> currentCategories = new ArrayList<>();

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
}
