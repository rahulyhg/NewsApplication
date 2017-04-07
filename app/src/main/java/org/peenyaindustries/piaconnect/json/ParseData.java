package org.peenyaindustries.piaconnect.json;


import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.peenyaindustries.piaconnect.helper.Constants;
import org.peenyaindustries.piaconnect.helper.Validations;
import org.peenyaindustries.piaconnect.models.Category;
import org.peenyaindustries.piaconnect.models.CommentModel;
import org.peenyaindustries.piaconnect.models.Post;

import java.util.ArrayList;
import java.util.List;

public class ParseData {

    private Context context;
    private Validations v;

    public ParseData(Context context) {
        this.context = context;
        this.v = new Validations();
    }

    public List<Category> parseCategories(String response) {
        List<Category> catList = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(response);
            JSONArray catArray = object.getJSONArray("categories");

            for (int i = 0; i < catArray.length(); i++) {

                JSONObject catObject = catArray.getJSONObject(i);

                String id = v.contains(catObject, "id") ? catObject.getString("id") : Constants.NA;
                String title = v.contains(catObject, "title") ? catObject.getString("title") : Constants.NA;
                String description = v.contains(catObject, "description") ? catObject.getString("description") : Constants.NA;
                String postCount = v.contains(catObject, "post_count") ? catObject.getString("post_count") : Constants.NA;

                Category cat = new Category();

                cat.setId(Integer.parseInt(id));
                cat.setTitle(title);
                cat.setDescription(description);
                cat.setPostCount(Integer.parseInt(postCount));

                catList.add(cat);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return catList;
    }

    public List<Post> parseTotalPosts(String response) {
        List<Post> postList = new ArrayList<>();

        try {
            JSONObject object = new JSONObject(response);
            String countTotal = v.contains(object, "count_total") ? object.getString("count_total") : Constants.NA;

            Post post = new Post();
            post.setTotalPost(Integer.parseInt(countTotal));
            postList.add(post);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postList;
    }

    public List<Post> parsePosts(String response) {
        List<Post> postList = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(response);
            JSONArray postArray = object.getJSONArray("posts");

            for (int i = 0; i < postArray.length(); i++) {

                JSONObject postObject = postArray.getJSONObject(i);

                String id = v.contains(postObject, "id") ? postObject.getString("id") : Constants.NA;
                String type = v.contains(postObject, "type") ? postObject.getString("type") : Constants.NA;
                String url = v.contains(postObject, "url") ? postObject.getString("url") : Constants.NA;
                String status = v.contains(postObject, "status") ? postObject.getString("status") : Constants.NA;
                String title = v.contains(postObject, "title") ? postObject.getString("title") : Constants.NA;
                String content = v.contains(postObject, "content") ? postObject.getString("content") : Constants.NA;
                String excerpt = v.contains(postObject, "excerpt") ? postObject.getString("excerpt") : Constants.NA;
                String date = v.contains(postObject, "date") ? postObject.getString("date") : Constants.NA;
                String modified = v.contains(postObject, "modified") ? postObject.getString("modified") : Constants.NA;

                JSONObject thumbnailImages = v.contains(postObject, "thumbnail_images") ?
                        postObject.getJSONObject("thumbnail_images") : null;

                JSONObject thumbnailImage = thumbnailImages != null && v.contains(thumbnailImages, "medium")
                        ? thumbnailImages.getJSONObject("medium") : null;

                String thumbnail = thumbnailImage != null && v.contains(thumbnailImage, "url")
                        ? thumbnailImage.getString("url") : Constants.NA;

                JSONObject fullscreenImage = thumbnailImages != null && v.contains(thumbnailImages, "accesspress-mag-block-big-thumb")
                        ? thumbnailImages.getJSONObject("accesspress-mag-block-big-thumb") : null;

                String fullImage = fullscreenImage != null && v.contains(fullscreenImage, "url")
                        ? fullscreenImage.getString("url") : Constants.NA;

                JSONArray categories = v.contains(postObject, "categories") ?
                        postObject.getJSONArray("categories") : null;

                Post post = new Post();

                List<String> catList = new ArrayList<>();
                for (int j = 0; j < categories.length(); j++) {

                    JSONObject catObj = categories.getJSONObject(j);

                    String category = v.contains(catObj, "id") ? catObj.getString("id") : Constants.NA;

                    catList.add(category);
                    post.setCategories(catList);

                }

                if (Build.VERSION.SDK_INT >= 24) {
                    title = String.valueOf(Html.fromHtml(title, Html.FROM_HTML_MODE_LEGACY));
                    excerpt = String.valueOf(Html.fromHtml(excerpt, Html.FROM_HTML_MODE_LEGACY));
                    //content = String.valueOf(Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY));
                } else {
                    title = String.valueOf(Html.fromHtml(title));
                    excerpt = String.valueOf(Html.fromHtml(excerpt));
                    //content = String.valueOf(Html.fromHtml(content));
                }

                post.setId(Integer.parseInt(id));
                post.setType(type);
                post.setUrl(url);
                post.setStatus(status);
                post.setTitle(title);
                post.setContent(content);
                post.setExcerpt(excerpt);
                post.setDate(date);
                post.setModified(modified);
                post.setThumbnail(thumbnail);
                post.setFullImage(fullImage);

                postList.add(post);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return postList;
    }

    public List<CommentModel> fetchComments(String response) {
        List<CommentModel> commentList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject obj = jsonObject.getJSONObject("post");
            JSONArray commentArray = obj.getJSONArray("comments");
            for (int i = 0; i < commentArray.length(); i++) {

                String id = Constants.NA;
                String name = Constants.NA;
                String url = Constants.NA;
                String date = Constants.NA;
                String content = Constants.NA;

                JSONObject commentObject = commentArray.getJSONObject(i);

                if (commentObject.has("id")) {
                    id = commentObject.getString("id");
                }

                if (commentObject.has("name")) {
                    name = commentObject.getString("name");
                }

                if (commentObject.has("url")) {
                    url = commentObject.getString("url");
                }

                if (commentObject.has("date")) {
                    date = commentObject.getString("date");
                }


                if (commentObject.has("content")) {
                    content = commentObject.getString("content");
                }

                Log.e("Comment :", id + " - " + url + " - " + name + " - " + date + " - " + content);
                CommentModel commentModel = new CommentModel();
                commentModel.setId(id);
                commentModel.setUrl(url);
                commentModel.setName(name);
                commentModel.setDate(date);
                commentModel.setContent(content);

                commentList.add(commentModel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return commentList;
    }
}
