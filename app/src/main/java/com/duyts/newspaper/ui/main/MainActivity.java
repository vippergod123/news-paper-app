package com.duyts.newspaper.ui.main;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.duyts.newspaper.adapter.LinksAdapter;
import com.duyts.newspaper.databinding.ActivityMainBinding;
import com.duyts.newspaper.ui.BaseActivity;

import java.util.Arrays;
import java.util.List;

import kotlin.random.Random;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding viewBinding;
    private LinksAdapter linksAdapter;
    MainActivityViewModel viewModel;

    @Override
    public void initView() {
        super.initView();
        viewBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        linksAdapter = new LinksAdapter(this);
        viewBinding.urlRecyclerView.setAdapter(linksAdapter);
        viewBinding.urlRecyclerView.setLayoutManager(layoutManager);


    }

    @Override
    public void initListener() {
        super.initListener();

        viewModel.getLinks().observe(this, links -> {
            linksAdapter.setLinks(links);
        });

        viewBinding.addFloatingAction.setOnClickListener(v -> {
            viewModel.addLink(listUrl.get(Random.Default.nextInt(listUrl.size())));
        });
    }

    List<String> listUrl = Arrays.asList(
//            "https://sg.yahoo.com/",
            "https://www.channelnewsasia.com/",
//            "https://stackoverflow.com/questions/1005073/initialization-of-an-arraylist-in-one-line",
            "https://developer.android.com/topic/libraries/architecture/viewmodel#java"
    );
}