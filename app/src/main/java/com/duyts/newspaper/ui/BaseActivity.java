package com.duyts.newspaper.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFirst();
        initView();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initLogic();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanUp();
    }

    public void initFirst() {

    }

    public void initView() {

    }

    public void initListener() {

    }
    public void initLogic() {

    }

    public void cleanUp() {

    }
}
