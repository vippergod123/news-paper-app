package com.duyts.newspaper.ui.main;

import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.duyts.newspaper.R;
import com.duyts.newspaper.databinding.ActivityMainBinding;
import com.duyts.newspaper.ui.BaseActivity;

import java.util.Arrays;
import java.util.List;

import kotlin.random.Random;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding viewBinding;
    MainActivityViewModel viewModel;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_remove:
                viewModel.removeRandom();
                return true;
            case R.id.action_remove_all:
                viewModel.removeAll();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void initView() {
        super.initView();
        viewBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        setSupportActionBar(viewBinding.toolbar);

//        linksAdapter = new LinksAdapter(this, viewModel);
        viewBinding.urlRecyclerView.setAdapter(viewModel.getAdapter());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        viewBinding.urlRecyclerView.setLayoutManager(layoutManager);

    }

    @Override
    public void initListener() {
        super.initListener();
        viewBinding.addFloatingAction.setOnClickListener(v -> {
            new Thread(() -> {
                for (int i = 0; i < 10000; i++) {
                    String randomString = listUrl.get(Random.Default.nextInt(listUrl.size()));
                    viewModel.addLink(randomString);
                }
            }).start();

        });
    }

    List<String> listUrl = Arrays.asList(
//            "https://sg.yahoo.com/",
            "https://www.channelnewsasia.com/",
//            "https://stackoverflow.com/questions/1005073/initialization-of-an-arraylist-in-one-line",
            "https://developer.android.com/topic/libraries/architecture/viewmodel#java"
    );
}