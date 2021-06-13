package com.duyts.newspaper.ui.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.SortedList;

import com.duyts.newspaper.MainApplication;
import com.duyts.newspaper.adapter.LinksAdapter;
import com.duyts.newspaper.model.LinkModel;
import com.duyts.newspaper.util.HtmlParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import kotlin.random.Random;
import timber.log.Timber;

public class MainActivityViewModel extends ViewModel implements LinksAdapter.Callback,
        Handler.Callback {

    private static final int ADD_ITEM_BY_STRING_CODE = 10002;
    private static final int REMOVE_RANDOM_CODE = 10003;
    private static final int REMOVE_ALL_CODE = 10004;
    private static final int REMOVE_ITEM_CODE = 10005;
    private static final int REMOVE_LIST_ITEM_CODE = 10006;
    private static final int SORT_LIST_CODE = 10007;
    private final MutableLiveData<SortedList<LinkModel>> sortedLinksMutableLiveData =
            new MutableLiveData<>();
    private final SortedList<LinkModel> sortedLinks;
    private final Handler handler;
    private final LinksAdapter adapter;
//    private final ExecutorService executorService;

    private final HandlerThread handlerThread;
    private final Handler backgroundHandler;

    private Boolean isAscendingList = true;

    @Override
    public void onRemoveSelectedList(ArrayList<LinkModel> selectedLinks) {
        if (selectedLinks.size() != 0) {
            removeItems(selectedLinks);
        }
    }

    @Override
    public void onRemoveAllList() {
        removeAll();
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        Bundle bundle = msg.getData();
        switch (msg.what) {
            case ADD_ITEM_BY_STRING_CODE:
                getLinkInfo((String) msg.obj);
                break;
            case REMOVE_ALL_CODE:
                runOnUiThread(() -> {
                    sortedLinks.clear();
                    sortedLinksMutableLiveData.setValue(sortedLinks);
                });
                break;
            case REMOVE_RANDOM_CODE:
                runOnUiThread(() -> {
                    int random = Random.Default.nextInt(sortedLinks.size());
                    sortedLinks.removeItemAt(random);
                    sortedLinksMutableLiveData.setValue(sortedLinks);
                });
                break;
            case REMOVE_ITEM_CODE:
                LinkModel itemRemove = (LinkModel) msg.obj;
                runOnUiThread(() -> {
                    sortedLinks.remove(itemRemove);
                    sortedLinksMutableLiveData.setValue(sortedLinks);
                });
                break;
            case REMOVE_LIST_ITEM_CODE:
                sortedLinks.beginBatchedUpdates();

                ArrayList<LinkModel> removeLinks = bundle.getParcelableArrayList("remove_list_item");
                for (int i = 0; i < removeLinks.size(); ++i) {
                    sortedLinks.remove(removeLinks.get(i));
                }
                sortedLinks.endBatchedUpdates();
                runOnUiThread(() -> {
                    sortedLinksMutableLiveData.setValue(sortedLinks);
                    removeLinks.clear();
                });
                break;
            case SORT_LIST_CODE:
                sortedLinks.beginBatchedUpdates();
                List<LinkModel> tempArray = new ArrayList<>();
                for (int i = 0; i < sortedLinks.size(); ++i) {
                    tempArray.add(sortedLinks.get(i));
                }

                sortedLinks.clear();

                runOnUiThread(() -> {
                    sortedLinks.addAll(tempArray);
                    sortedLinksMutableLiveData.setValue(sortedLinks);
                    sortedLinks.endBatchedUpdates();
                });
                break;
        }
        return false;
    }

    public MainActivityViewModel() {
//        executorService = Executors.newFixedThreadPool(50);

        handlerThread = new HandlerThread(MainActivityViewModel.class.getSimpleName());
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper(), this);
        handler = new Handler();
        sortedLinks = new SortedList<>(
                LinkModel.class,
                new SortedList.Callback<LinkModel>() {

                    @Override
                    public void onInserted(int position, int count) {

                        runOnUiThread(() -> {
                            adapter.notifyItemRangeInserted(position, count);
                        });
                    }

                    @Override
                    public void onRemoved(int position, int count) {
                        runOnUiThread(() -> {
                            adapter.notifyItemRangeRemoved(position, count);
                        });

                    }

                    @Override
                    public void onMoved(int fromPosition, int toPosition) {
                        runOnUiThread(() -> {
                            adapter.notifyItemMoved(fromPosition, toPosition);
                        });

                    }

                    @Override
                    public int compare(LinkModel o1, LinkModel o2) {
                        if (isAscendingList) {
                            return o1.getTitle().compareTo(o2.getTitle());
                        }
                        return o2.getTitle().compareTo(o1.getTitle());
                    }

                    @Override
                    public void onChanged(int position, int count) {
                        runOnUiThread(() -> {
                            adapter.notifyItemRangeChanged(position, count);
                        });

                    }

                    @Override
                    public boolean areContentsTheSame(LinkModel oldItem, LinkModel newItem) {
                        return oldItem.getTitle().equals(newItem.getTitle());
                    }

                    @Override
                    public boolean areItemsTheSame(LinkModel item1, LinkModel item2) {
                        return item1.equals(item2);
                    }
                }
        );

        adapter = new LinksAdapter(MainApplication.getAppContext(), this);
        adapter.setLinks(sortedLinks);
        sortedLinksMutableLiveData.setValue(sortedLinks);
    }

    public LinksAdapter getAdapter() {
        return adapter;
    }

    public void addLink(String link) {
        sendMessage(ADD_ITEM_BY_STRING_CODE, link);
    }

    public void removeItem(LinkModel item) {
        ArrayList<LinkModel> list = new ArrayList<>();
        list.add(item);
        removeItems(list);
    }

    public synchronized void removeItems(final ArrayList<LinkModel> items) {
        Bundle bundle = new Bundle();
        final ArrayList<LinkModel> newArrayList = new ArrayList<>(items);
        bundle.putParcelableArrayList("remove_list_item", newArrayList);
        sendMessageWithBundle(REMOVE_LIST_ITEM_CODE, bundle);
    }

    public void removeRandom() {
        sendEmptyMessage(REMOVE_RANDOM_CODE);
    }

    public void removeAll() {
        backgroundHandler.removeCallbacksAndMessages(null);
        runOnUiThread(() -> {
            sortedLinks.clear();
            sortedLinksMutableLiveData.setValue(sortedLinks);
        });
    }

    public synchronized void sortList() {
        isAscendingList = !isAscendingList;
        sendEmptyMessage(SORT_LIST_CODE);
    }

    private void getLinkInfo(String link) {
        InputStream response = null;
        try {
            response = new URL(link).openStream();
            Scanner scanner = new Scanner(response);
            String htmlString = scanner.useDelimiter("\\A").next();

            HtmlParser htmlParser = new HtmlParser(link, htmlString);

            String pageTitle = htmlParser.getTitle();
            String pageImage = htmlParser.getImage();
            LinkModel itemAdd = new LinkModel(link, pageTitle, pageImage);
            runOnUiThread(() -> {
                Timber.e("Add ITEM: %s", itemAdd.getTitle());
                sortedLinks.add(itemAdd);
                sortedLinksMutableLiveData.setValue(sortedLinks);
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void runOnUiThread(Runnable run) {
        handler.post(run);
    }

    private void sendMessage(int code, Object o) {
        Message msg = backgroundHandler.obtainMessage();
        msg.what = code;
        msg.obj = o;
        backgroundHandler.sendMessage(msg);
    }

    private void sendMessageWithBundle(int code, Bundle b) {
        Message msg = Message.obtain();
        msg.what = code;
        msg.setData(b);
        backgroundHandler.sendMessage(msg);
    }

    private void sendEmptyMessage(int code) {
        Message msg = backgroundHandler.obtainMessage();
        msg.what = code;
        backgroundHandler.sendMessage(msg);
    }

}
