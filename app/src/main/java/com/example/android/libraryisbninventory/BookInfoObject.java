package com.example.android.libraryisbninventory;

/**
 * Created by nick on 10/24/2017.
 */

public class BookInfoObject {
    public String title;
    public String author;
    public String imgUrl;

    public String[] returnInfo(){
        String[] info = {title, author, imgUrl};
        return info;
    }
}
