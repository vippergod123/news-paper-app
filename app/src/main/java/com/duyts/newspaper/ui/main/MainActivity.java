package com.duyts.newspaper.ui.main;

import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.duyts.newspaper.R;
import com.duyts.newspaper.databinding.ActivityMainBinding;
import com.duyts.newspaper.ui.BaseActivity;
import com.duyts.newspaper.ui.component.EditTextDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kotlin.random.Random;

public class MainActivity extends BaseActivity implements EditTextDialog.Callback {

    private ActivityMainBinding viewBinding;
    MainActivityViewModel viewModel;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_sort_list) {
            viewModel.sortList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void initView() {
        super.initView();
        viewBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        viewBinding.urlRecyclerView.setAdapter(viewModel.getAdapter());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        viewBinding.urlRecyclerView.setLayoutManager(layoutManager);

    }

    @Override
    public void initListener() {
        super.initListener();
        viewBinding.addFloatingAction.setOnClickListener(v -> {
//            startAutoAdd();
            openDialog();
        });
    }

    @Override
    public void onSubmitInputLinkWithCount(String text, int count) {
        for (int i=0; i < count ; i++) {
            viewModel.addLink(text);
        }
    }

    @Override
    public void onRandomLinkWithCount(int count) {
        startRandomAddLinks(count);
    }

    private void openDialog() {
        EditTextDialog editTextDialog = new EditTextDialog(this);
        editTextDialog.show(getSupportFragmentManager(),"edit_dialog");
    }

    private void startRandomAddLinks(int count) {
        new Thread(() -> {
            ArrayList<String> result = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                String randomString = listUrl.get(Random.Default.nextInt(listUrl.size()));
                result.add(randomString);
                viewModel.addLink(randomString);
            }

//            viewModel.addLinksList(result);
        }).start();
    }
    List<String> listUrl = Arrays.asList(
            "https://sg.yahoo.com",
            "https://www.channelnewsasia.com",
            "https://www.google.com",
            "https://hopamchuan.com/",
            "https://edition.cnn.com/",
            "https://www.abc.net.au/",
            "https://howtodoinjava.com/java/string/java-string-startswith-example/#:~:text=Java%20String%20startsWith()%20method,a%20prefix%20of%20the%20String",
            "https://www.youtube.com/watch?v=XyQvoONPMng&ab_channel=CodinginFlow"

    );

}