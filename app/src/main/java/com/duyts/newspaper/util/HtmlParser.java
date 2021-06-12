package com.duyts.newspaper.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HtmlParser {
    String head;
    List<String> metaTags;
    String title;
    String image;
    String link;

    public HtmlParser(String link, String html) {
        this.link = link;
        head = getHead(html);
        metaTags = getAllMetaTag();
    }

    public String getTitle() {
        return title != null ? title : getTitleInternal() ;
    }

    public String getImage() {
        return image;
    }

    private String getTitleInternal() {
        try {
            String pattern = "<title>";
            int start = head.indexOf(pattern) + 7;
            int end = head.indexOf("</title>", start);
            return head.substring(start, end);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "Cannot get Title";
    }

    private String getHead(String html) {
        try {
            String pattern = "<head>";
            int start = html.indexOf(pattern) + 6;
            int end = html.indexOf("</head>", start);
            return html.substring(start, end);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private List<String> getAllMetaTag() {
        String metaPattern = "<meta ";
        String linkPattern = "<link ";
        ArrayList<String> metaTags = new ArrayList<>();
        int start = 0;
        int end = 0;
        while (head != null && end < head.length()) {

            int newStart = Math.min(head.indexOf(metaPattern,end),head.indexOf(linkPattern,end));
            if (newStart == -1) {
                break; //break cause cannot find pattern anymore
            }
            start = newStart + metaPattern.length();
            end = head.indexOf(">", start);
            try {
                String tag = head.substring(start, end);
                String lowerTag = tag.toLowerCase();
                if (lowerTag.contains("name=\"title\"")) {
                    title = parseContentInMetaTag(tag);
                }

                if (lowerTag.contains("itemprop=\"image\"") ||
                        lowerTag.contains("name=\"twitter:image\"") ||
                        lowerTag.contains("name=\"og:image\"") ||
                        lowerTag.contains("property=\"og:image\"")
                ) {
                    image = modifyImageUrl(link,parseContentInMetaTag(tag));
                }
                else  if (lowerTag.contains("rel=\"shortcut icon\"") ||
                            lowerTag.contains("rel=\"icon\"")
                ) {
                    image = modifyImageUrl(link,parseContentInLinkTag(tag));
                }


                metaTags.add(tag);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


        return metaTags;
    }

    private String parseContentInMetaTag(String meta) {
        String pattern = "content=\"";
        int start = meta.indexOf(pattern);
        if (start == -1) {
            return null;
        }
        int newStart = start + pattern.length();;
        int end = meta.indexOf("\"", newStart);
        return meta.substring(newStart,end);
    }

    private String parseContentInLinkTag(String meta) {
        String pattern = "href=\"";
        int start = meta.indexOf(pattern);
        if (start == -1) {
            return null;
        }
        int newStart = start + pattern.length();;
        int end = meta.indexOf("\"", newStart);
        return meta.substring(newStart,end);
    }

    private String modifyImageUrl(String link, String image) {
        if (image == null ) {
            return null;
        }

        if (image.startsWith("http") || image.startsWith("https")) {
            return image;
        }

        String domain = link;
        if (domain.endsWith("/")) {
            domain = link.substring(0, link.length() -1 );
        }
        if (!image.startsWith("/")) {
            return domain + "/" + image;
        }
        return domain + image;
    }
}
