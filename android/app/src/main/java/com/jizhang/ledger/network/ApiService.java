package com.jizhang.ledger.network;

import com.jizhang.ledger.model.*;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;
import java.util.Map;

public interface ApiService {
    @POST("auth/register")
    Call<ApiResponse<RegisterResponse>> register(@Body RegisterRequest request);

    @POST("auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest request);

    @GET("user/profile")
    Call<ApiResponse<User>> getProfile();

    @GET("categories")
    Call<ApiResponse<Map<String, List<String>>>> getCategories();

    @GET("transactions")
    Call<ApiResponse<TransactionListResponse>> getTransactions(
            @Query("year") int year,
            @Query("month") int month
    );

    @GET("user/statistics")
    Call<ApiResponse<StatisticsResponse>> getStatistics();
}
