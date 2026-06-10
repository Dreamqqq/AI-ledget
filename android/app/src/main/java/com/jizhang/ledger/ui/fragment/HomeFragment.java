package com.jizhang.ledger.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.jizhang.ledger.R;
import com.jizhang.ledger.model.TransactionListResponse;
import com.jizhang.ledger.network.ApiResponse;
import com.jizhang.ledger.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.Calendar;

public class HomeFragment extends Fragment {
    private TextView tvIncome, tvExpense;
    private RecyclerView rvTransactions;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        tvIncome = view.findViewById(R.id.tvIncome);
        tvExpense = view.findViewById(R.id.tvExpense);
        rvTransactions = view.findViewById(R.id.rvTransactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));

        loadData();
        return view;
    }

    private void loadData() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;

        RetrofitClient.getApiService().getTransactions(year, month).enqueue(new Callback<ApiResponse<TransactionListResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TransactionListResponse>> call, Response<ApiResponse<TransactionListResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    TransactionListResponse data = response.body().getData();
                    tvIncome.setText(String.format("¥%.2f", data.getTotalIncome()));
                    tvExpense.setText(String.format("¥%.2f", data.getTotalExpense()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TransactionListResponse>> call, Throwable t) {
            }
        });
    }
}
