package com.jithvar.gambhirmudda.handler;

/**
 * Created by Arvindo Mondal on 2/8/17.
 * Company name Jithvar
 * Email arvindo@jithvar.com
 */
public class Comments {
    private final String postId;
    private final String name;
    private final String phone;
    private final String comment;
    private final String date;
    private final String time;

    public Comments(String postId, String name, String phone, String comment, String date, String time) {

        this.postId = postId;
        this.name = name;
        this.phone = phone;
        this.comment = comment;
        this.date = date;
        this.time = time;
    }

    public String getPostId() {
        return postId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
