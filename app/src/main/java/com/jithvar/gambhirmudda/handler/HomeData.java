package com.jithvar.gambhirmudda.handler;

/**
 * Created by Arvindo Mondal on 4/7/17.
 * Company name Jithvar
 * Email arvindo@jithvar.com
 */
public class HomeData {

    private final String postId;
    private final String category;
    private final String title;
    private final String tags;
    private final String views;
    private final String likes;
    private final String author;
    private final String status;
    private final String date;
    private final String time;
    private final String featuredImage;

    public HomeData(String postId, String category, String title, String tags,
                    String views, String likes, String author, String status,
                    String date, String time, String featuredImage) {
        this.postId = postId;
        this.category = category;
        this.title = title;
        this.tags = tags;
        this.views = views;
        this.likes = likes;
        this.author = author;
        this.status = status;
        this.date = date;
        this.time = time;
        this.featuredImage = featuredImage;
    }

    public String getPostId() {
        return postId;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getTags() {
        return tags;
    }

    public String getViews() {
        return views;
    }

    public String getLikes() {
        return likes;
    }

    public String getAuthor() {
        return author;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getFeaturedImage() {
        return featuredImage;
    }
}
