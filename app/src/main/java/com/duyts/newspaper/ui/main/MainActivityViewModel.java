package com.duyts.newspaper.ui.main;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.duyts.newspaper.model.LinkModel;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import timber.log.Timber;

public class MainActivityViewModel extends ViewModel implements Handler.Callback {

    private final MutableLiveData<List<LinkModel>> links = new MutableLiveData<>();
    private final ArrayList<LinkModel> _links;
    private final Handler handler = new Handler();

    public MainActivityViewModel() {
        _links = new ArrayList<>();
        links.setValue(_links);
    }

    public MutableLiveData<List<LinkModel>> getLinks() {
        return links;
    }

    public void addLink(String link) {
        Timber.w(link);
        AsyncTask.execute(() -> getLinkInfo(link));
    }

    private void getLinkInfo(String link) {
        InputStream response = null;
        try {
            response = new URL(link).openStream();
            Scanner scanner = new Scanner(response);
            String responseBody = scanner.useDelimiter("\\A").next();
            String pageTitle = getPageTitle(responseBody);
            String pageImage = getPageImage(responseBody);
            _links.add(new LinkModel(link, pageTitle, pageImage));
            handler.post(() -> links.setValue(_links));
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

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        return false;
    }
}
