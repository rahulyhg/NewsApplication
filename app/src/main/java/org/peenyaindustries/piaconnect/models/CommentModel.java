package org.peenyaindustries.piaconnect.models;

public class CommentModel {

    private String id, name, url, date, content;

    public CommentModel(String id, String name, String url, String date, String content) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.date = date;
        this.content = content;
    }

    public CommentModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
