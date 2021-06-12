package com.duyts.newspaper.ui.main;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.SortedList;

import com.duyts.newspaper.MainApplication;
import com.duyts.newspaper.adapter.LinksAdapter;
import com.duyts.newspaper.model.LinkModel;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlin.random.Random;
import timber.log.Timber;

import static androidx.recyclerview.widget.SortedList.INVALID_POSITION;

public class MainActivityViewModel extends ViewModel {

    private static final int ADD_ITEM_CODE = 10001;
    private static final int GET_ITEM_INFO_CODE = 10002;
    private static final int REMOVE_RANDOM_CODE = 10003;
    private static final int REMOVE_ALL_CODE = 10004;

    private final MutableLiveData<SortedList<LinkModel>> sortedLinksMutableLiveData =
            new MutableLiveData<>();
    private final SortedList<LinkModel> sortedLinks;
    private final Handler handler;
    private final LinksAdapter adapter;
    private final ExecutorService executorService;

    private final HandlerThread handlerThread;
    private final Handler backgroundHandler;

    public MainActivityViewModel() {
        executorService = Executors.newSingleThreadExecutor();

        handlerThread = new HandlerThread(MainActivityViewModel.class.getSimpleName());
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                int code = msg.what;
                switch (code) {
                    case ADD_ITEM_CODE:
                        LinkModel res = (LinkModel) msg.obj;
                        runOnUiThread(() -> {
                            addOrUpdate(res);
                            sortedLinksMutableLiveData.setValue(sortedLinks);
                        });
                        break;
                    case GET_ITEM_INFO_CODE:
                        getLinkInfoWebView((String) msg.obj);
                        break;
                    case REMOVE_RANDOM_CODE:
                        removeRandomInternal();
                        break;
                    case REMOVE_ALL_CODE:
                        removeAllInternal();
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
                        if (o1 == null || o2 == null) {
                            return 1;
                        }
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
                        return item1.getId().equals(item2.getId());
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
        sendMessage(GET_ITEM_INFO_CODE, link);
    }

    public void removeLinkAt(int pos) {
        sortedLinks.removeItemAt(pos);
        sortedLinksMutableLiveData.setValue(sortedLinks);
    }

    public void removeRandom() {
        sendEmptyMessage(REMOVE_RANDOM_CODE);
    }

    private void removeRandomInternal() {
        runOnUiThread(() -> {
            int random = Random.Default.nextInt(sortedLinks.size());
            sortedLinks.removeItemAt(random);
            sortedLinksMutableLiveData.setValue(sortedLinks);
        });

    }

    public void removeAll() {
        sendEmptyMessage(REMOVE_ALL_CODE);
    }

    private void removeAllInternal() {
        runOnUiThread(() -> {
            sortedLinks.clear();
            sortedLinksMutableLiveData.setValue(sortedLinks);
        });
    }

    @SuppressLint("JavascriptInterface")
    private void getLinkInfoWebView(String link) {
        final LinkModel res = new LinkModel(link);
        WebView browser = new WebView(MainApplication.getAppContext());
        browser.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                res.setImageBitMap(icon);
                sendMessage(ADD_ITEM_CODE, res);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                res.setTitle(title);
                sendMessage(ADD_ITEM_CODE, res);
            }
        });
        browser.loadUrl(link);
    }


    private synchronized void addOrUpdate(LinkModel res) {

        int pos = sortedLinks.indexOf(res);
        if (pos == INVALID_POSITION) {
            sortedLinks.add(res);
        } else {
            sortedLinks.updateItemAt(pos, res);
        }
    }
//    private void getLinkInfo(String link) {
//        BufferedReader in = null;
//        try {
//
////            response = new URL(link).openStream();
////            Scanner scanner = new Scanner(response);
////            String responseBody = scanner.useDelimiter("\\A").next();
//
//            URL google = new URL(link);
//            in = new BufferedReader(new InputStreamReader(google.openStream()));
//            String input;
//            StringBuilder stringBuffer = new StringBuilder();
//            while ((input = in.readLine()) != null) {
//                stringBuffer.append(input);
//            }
//
//            String responseBody = stringBuffer.toString();
//            String pageTitle = getPageTitle(responseBody);
//            String pageImage = getPageImage(link, responseBody);
//            LinkModel res = new LinkModel(link, pageTitle, pageImage);
////            sendMessage(res);
//            runOnUiThread(() -> {
//                sortedLinks.add(res);
//                sortedLinksMutableLiveData.setValue(sortedLinks);
//            });
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } finally {
//            try {
//                if (in != null) {
//                    in.close();
//                }
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//    }

    private String getPageTitle(String body) {
        try {
            String pattern = "<meta property=\"og:description\" content=\"";
            int start = body.indexOf(pattern) + 41;
            int end = body.indexOf("\"", start);
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
                if (link.endsWith("/") || image.startsWith("/")) {
                    image = link + image;
                } else {
                    image = link + "/" + image;
                }
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
        Message msg = backgroundHandler.obtainMessage();
        msg.what = code;
        msg.obj = o;
        backgroundHandler.sendMessage(msg);
    }

    private void sendEmptyMessage(int code) {
        Message msg = backgroundHandler.obtainMessage();
        msg.what = code;
        backgroundHandler.sendMessage(msg);
    }
}
