package org.peenyaindustries.piaconnect.models;

import java.util.List;

public class Snap {

    private int mGravity;
    private String mText;
    private List<Post> mPosts;

    public Snap(int mGravity, String mText, List<Post> mPosts) {
        this.mGravity = mGravity;
        this.mText = mText;
        this.mPosts = mPosts;
    }

    public int getmGravity() {
        return mGravity;
    }

    public void setmGravity(int mGravity) {
        this.mGravity = mGravity;
    }

    public String getmText() {
        return mText;
    }

    public void setmText(String mText) {
        this.mText = mText;
    }

    public List<Post> getmPosts() {
        return mPosts;
    }

    public void setmPosts(List<Post> mPosts) {
        this.mPosts = mPosts;
    }
}
