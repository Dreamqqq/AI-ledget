package com.jizhang.ledger;

import android.app.Application;
import com.jizhang.ledger.utils.TokenManager;

public class LedgerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TokenManager.init(this);
    }
}
