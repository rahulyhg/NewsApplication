package org.peenyaindustries.piaconnect.models;

public class Category {

    private int id;
    private String title;
    private String description;
    private int postCount;

    public Category(int id, String title, String description, int postCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.postCount = postCount;
    }

    public Category() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }
}
