package com.jizhang.ledger.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.jizhang.ledger.R;
import com.jizhang.ledger.model.StatisticsResponse;
import com.jizhang.ledger.model.User;
import com.jizhang.ledger.network.ApiResponse;
import com.jizhang.ledger.network.RetrofitClient;
import com.jizhang.ledger.ui.LoginActivity;
import com.jizhang.ledger.utils.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private TextView tvUserName, tvUserPhone, tvTotalCount, tvTotalIncome, tvTotalExpense;
    private Button btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserPhone = view.findViewById(R.id.tvUserPhone);
        tvTotalCount = view.findViewById(R.id.tvTotalCount);
        tvTotalIncome = view.findViewById(R.id.tvTotalIncome);
        tvTotalExpense = view.findViewById(R.id.tvTotalExpense);
        btnLogout = view.findViewById(R.id.btnLogout);

        loadUserInfo();
        loadStatistics();

        btnLogout.setOnClickListener(v -> {
            TokenManager.clear();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        });

        return view;
    }

    private void loadUserInfo() {
        RetrofitClient.getApiService().getProfile().enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    tvUserName.setText(user.getName() != null ? user.getName() : "未设置");
                    tvUserPhone.setText(user.getPhone());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
            }
        });
    }

    private void loadStatistics() {
        RetrofitClient.getApiService().getStatistics().enqueue(new Callback<ApiResponse<StatisticsResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<StatisticsResponse>> call, Response<ApiResponse<StatisticsResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    StatisticsResponse stats = response.body().getData();
                    tvTotalCount.setText(String.format("记账次数: %d次", stats.getTotalTransactions()));
                    tvTotalIncome.setText(String.format("总收入: ¥%.2f", stats.getTotalIncome()));
                    tvTotalExpense.setText(String.format("总支出: ¥%.2f", stats.getTotalExpense()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<StatisticsResponse>> call, Throwable t) {
            }
        });
    }
}
