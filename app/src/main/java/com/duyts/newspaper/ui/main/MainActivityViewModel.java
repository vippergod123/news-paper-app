package com.duyts.newspaper.ui.main;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

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
import java.util.Scanner;

import kotlin.random.Random;
import timber.log.Timber;

public class MainActivityViewModel extends ViewModel {

    private static final int ADD_ITEM_CODE = 10001;
    private static final int ADD_ITEM_BY_STRING_CODE = 10002;
    private static final int REMOVE_RANDOM_CODE = 10003;
    private static final int REMOVE_ALL_CODE = 10004;
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
                switch (msg.what) {
                    case ADD_ITEM_CODE:
                        runOnUiThread(() -> {
                            sortedLinks.add((LinkModel) msg.obj);
                        });
                        break;
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
        sendMessage(ADD_ITEM_BY_STRING_CODE, link);
    }

    public void removeLinkAt(int pos) {
        sortedLinks.removeItemAt(pos);
        sortedLinksMutableLiveData.setValue(sortedLinks);
    }

    public void removeRandom() {
        sendEmptyMessage(REMOVE_RANDOM_CODE);
    }

    public void removeAll() {
        sendEmptyMessage(REMOVE_ALL_CODE);
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
//            String pageImage = getPageImage(link, htmlString);
            Log.d("CHRIS", String.format("%s - %s",pageTitle,pageImage));
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
        try {
            String pattern = "<title>";
            int start = body.indexOf(pattern) + 7;
            int end = body.indexOf("</title>", start);
            return body.substring(start, end);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "Cannot get Title";
    }

    private String getPageImage(String link, String body) {
        try {
            String pattern = "<meta property=\"og:image\" content=\"";
            int start = body.indexOf(pattern) + 35;
            int end = body.indexOf("\"", start);
            String image = body.substring(start, end);
            if (!image.startsWith("http") && !image.startsWith("https")) {
                image = link + image;
            }
            return image;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
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

    private void sendEmptyMessage(int code) {
        Message msg = new Message();
        msg.what = code;
        backgroundHandler.sendMessage(msg);
    }
}
