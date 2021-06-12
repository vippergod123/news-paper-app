package com.duyts.newspaper.ui.main;

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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlin.random.Random;
import timber.log.Timber;

public class MainActivityViewModel extends ViewModel {

    private static final int ADD_ITEM_CODE = 10001;
    private static final int ADD_ITEM_BY_STRING_CODE = 10002;
    private final MutableLiveData<SortedList<LinkModel>> sortedLinksMutableLiveData =
            new MutableLiveData<>();
    private final SortedList<LinkModel> sortedLinks;
    private final Handler handler;
    private final LinksAdapter adapter;
//    private final ExecutorService executorService;

    private final HandlerThread handlerThread;
    private final Handler backgroundHandler;

    public MainActivityViewModel() {
//        executorService = Executors.newFixedThreadPool(50);

        handlerThread = new HandlerThread(MainActivityViewModel.class.getSimpleName());
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.what == ADD_ITEM_CODE) {
                    runOnUiThread(() -> {
                        sortedLinks.add((LinkModel) msg.obj);
                    });
                }
                else if (msg.what == ADD_ITEM_BY_STRING_CODE) {
                    getLinkInfo((String) msg.obj);
                }
                return false;
            }
        });
        handler = new Handler();
        sortedLinks = new SortedList<>(
                LinkModel.class,
                new SortedList.Callback<LinkModel>() {

                    @Override
                    public void onInserted(int position, int count) {
                        runOnUiThread(() -> adapter.notifyItemRangeInserted(position, count));
                    }

                    @Override
                    public void onRemoved(int position, int count) {
                        runOnUiThread(() -> adapter.notifyItemRangeRemoved(position, count));
                    }

                    @Override
                    public void onMoved(int fromPosition, int toPosition) {
                        runOnUiThread(() -> adapter.notifyItemMoved(fromPosition, toPosition));
                    }

                    @Override
                    public int compare(LinkModel o1, LinkModel o2) {
                        return o1.getTitle().compareTo(o2.getTitle());
                    }

                    @Override
                    public void onChanged(int position, int count) {
                        runOnUiThread(() -> adapter.notifyItemRangeChanged(position, count));
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

        adapter = new LinksAdapter(MainApplication.getAppContext());
        adapter.setLinks(sortedLinks);
        sortedLinksMutableLiveData.setValue(sortedLinks);
    }


    public LinksAdapter getAdapter() {
        return adapter;
    }

    public void setLinks(ArrayList<LinkModel> l) {
    }

    public void addLink(String link) {
        Timber.w(link);
//        executorService.execute(() -> {
//            getLinkInfo(link);
//        });
        sendMessage(ADD_ITEM_BY_STRING_CODE, link);
    }

    public void removeLinkAt(int pos) {
        sortedLinks.removeItemAt(pos);
        sortedLinksMutableLiveData.setValue(sortedLinks);
    }

    public void removeRandom() {
        int random = Random.Default.nextInt(sortedLinks.size());
        sortedLinks.removeItemAt(random);
        sortedLinksMutableLiveData.setValue(sortedLinks);
    }

    public void removeAll() {
        sortedLinks.clear();
        sortedLinksMutableLiveData.setValue(sortedLinks);
    }


    private void getLinkInfo(String link) {
        InputStream response = null;
        try {
            response = new URL(link).openStream();
            Scanner scanner = new Scanner(response);
            String responseBody = scanner.useDelimiter("\\A").next();
            String pageTitle = getPageTitle(responseBody);
            String pageImage = getPageImage(responseBody);
            LinkModel res = new LinkModel(link, pageTitle, pageImage);
//            sendMessage(res);
            runOnUiThread(() -> {
                sortedLinks.add(res);
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

    private String getPageTitle(String body) {
        return body.substring(body.indexOf("<title>") + 7, body.indexOf("</title>"));
    }

    private String getPageImage(String body) {
        String image = "";
//        String imgRegex = "<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>";
//        Pattern pattern = Pattern.compile(imgRegex);
//        Matcher matcher = pattern.matcher(imgRegex);
//        if (matcher.find()) {
//            image = matcher.group(2);
//        }

        int start = body.indexOf("src=\"") + 5;
        int end = body.indexOf("\"", start);

        image = body.substring(start, end);
        return image;
    }

    private void runOnUiThread(Runnable run) {
        handler.post(run);
    }

    private void sendMessage(int code, Object o) {
        Message msg = new Message();
        msg.what = code;
        msg.obj = o;
        backgroundHandler.sendMessage(msg);
    }
}
