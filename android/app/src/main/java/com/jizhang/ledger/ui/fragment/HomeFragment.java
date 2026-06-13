package com.jizhang.ledger.ui.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.jizhang.ledger.R;
import com.jizhang.ledger.model.Transaction;
import com.jizhang.ledger.model.TransactionListResponse;
import com.jizhang.ledger.network.ApiResponse;
import com.jizhang.ledger.network.RetrofitClient;
import com.jizhang.ledger.ui.adapter.TransactionAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.Calendar;

public class HomeFragment extends Fragment {
    private TextView tvMonthYear, tvIncome, tvExpense;
    private Button btnSelectMonth;
    private RecyclerView rvTransactions;
    private TransactionAdapter adapter;
    private int selectedYear, selectedMonth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        tvMonthYear = view.findViewById(R.id.tvMonthYear);
        tvIncome = view.findViewById(R.id.tvIncome);
        tvExpense = view.findViewById(R.id.tvExpense);
        btnSelectMonth = view.findViewById(R.id.btnSelectMonth);
        rvTransactions = view.findViewById(R.id.rvTransactions);
        
        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TransactionAdapter();
        rvTransactions.setAdapter(adapter);

        Calendar cal = Calendar.getInstance();
        selectedYear = cal.get(Calendar.YEAR);
        selectedMonth = cal.get(Calendar.MONTH) + 1;

        btnSelectMonth.setOnClickListener(v -> showMonthYearPicker());

        adapter.setOnItemClickListener(new TransactionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Transaction transaction) {
                editTransaction(transaction);
            }

            @Override
            public void onItemLongClick(Transaction transaction) {
                showDeleteDialog(transaction);
            }
        });

        loadData();
        return view;
    }

    private void showMonthYearPicker() {
        View dialogView = LayoutInflater.from(getContext()).inflate(android.R.layout.select_dialog_item, null);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("选择年月");
        
        final NumberPicker yearPicker = new NumberPicker(getContext());
        yearPicker.setMinValue(2000);
        yearPicker.setMaxValue(2100);
        yearPicker.setValue(selectedYear);
        
        final NumberPicker monthPicker = new NumberPicker(getContext());
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(selectedMonth);
        monthPicker.setDisplayedValues(new String[]{"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"});
        
        android.widget.LinearLayout layout = new android.widget.LinearLayout(getContext());
        layout.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        layout.setGravity(android.view.Gravity.CENTER);
        layout.addView(yearPicker);
        layout.addView(monthPicker);
        
        builder.setView(layout);
        builder.setPositiveButton("确定", (dialog, which) -> {
            selectedYear = yearPicker.getValue();
            selectedMonth = monthPicker.getValue();
            loadData();
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void showDeleteDialog(Transaction transaction) {
        new AlertDialog.Builder(getContext())
            .setTitle("删除账单")
            .setMessage("确定要删除这条账单吗？")
            .setPositiveButton("删除", (dialog, which) -> deleteTransaction(transaction.getId()))
            .setNegativeButton("取消", null)
            .show();
    }

    private void deleteTransaction(Long id) {
        RetrofitClient.getApiService().deleteTransaction(id).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                    loadData();
                } else {
                    Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void editTransaction(Transaction transaction) {
        AddTransactionFragment fragment = new AddTransactionFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("transaction", transaction);
        fragment.setArguments(bundle);
        
        getActivity().getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit();
    }

    private void loadData() {
        tvMonthYear.setText(selectedYear + "年" + selectedMonth + "月");

        RetrofitClient.getApiService().getTransactions(selectedYear, selectedMonth).enqueue(new Callback<ApiResponse<TransactionListResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TransactionListResponse>> call, Response<ApiResponse<TransactionListResponse>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    TransactionListResponse data = response.body().getData();
                    tvIncome.setText(String.format("¥%.2f", data.getTotalIncome()));
                    tvExpense.setText(String.format("¥%.2f", data.getTotalExpense()));
                    adapter.setTransactions(data.getTransactions());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TransactionListResponse>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}
