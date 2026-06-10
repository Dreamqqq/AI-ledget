package com.jizhang.ledger.network;

import com.jizhang.ledger.utils.TokenManager;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class AuthInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String token = TokenManager.getToken();
        
        if (token != null && !token.isEmpty()) {
            Request request = original.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(request);
        }
        return chain.proceed(original);
    }
}
