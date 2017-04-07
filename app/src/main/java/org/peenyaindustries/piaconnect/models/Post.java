package org.peenyaindustries.piaconnect.models;

import java.util.List;

public class Post {

    private int id;
    private String type;
    private String url;
    private String status;
    private String title;
    private String content;
    private String excerpt;
    private String date;
    private String modified;
    private List<String> categories;
    private String thumbnail;
    private String fullImage;
    private int totalPost;

    public Post(int id, String type, String url, String status,
                String title, String content, String excerpt, String date, String modified,
                List<String> categories, String thumbnail, String fullImage, int totalPost) {
        this.id = id;
        this.type = type;
        this.url = url;
        this.status = status;
        this.title = title;
        this.content = content;
        this.excerpt = excerpt;
        this.date = date;
        this.modified = modified;
        this.categories = categories;
        this.thumbnail = thumbnail;
        this.fullImage = fullImage;
        this.totalPost = totalPost;
    }

    public Post() {
    }

    public int getTotalPost() {
        return totalPost;
    }

    public void setTotalPost(int totalPost) {
        this.totalPost = totalPost;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getFullImage() {
        return fullImage;
    }

    public void setFullImage(String fullImage) {
        this.fullImage = fullImage;
    }
}
