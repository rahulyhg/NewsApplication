package org.peenyaindustries.piaconnect.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;

import org.peenyaindustries.piaconnect.R;
import org.peenyaindustries.piaconnect.adapters.CommentAdapter;
import org.peenyaindustries.piaconnect.app.Config;
import org.peenyaindustries.piaconnect.callback.VolleyResponseCallback;
import org.peenyaindustries.piaconnect.helper.Constants;
import org.peenyaindustries.piaconnect.helper.DatabaseHelper;
import org.peenyaindustries.piaconnect.helper.PrefManager;
import org.peenyaindustries.piaconnect.json.FetchData;
import org.peenyaindustries.piaconnect.json.ParseData;
import org.peenyaindustries.piaconnect.models.CommentModel;
import org.peenyaindustries.piaconnect.models.Post;
import org.peenyaindustries.piaconnect.network.VolleySingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewsDetailActivity extends AppCompatActivity {

    private ImageLoader imageLoader;
    private DatabaseHelper db;
    private PrefManager pref;
    private TextView detailTitle;
    private ImageView detailImage;
    private WebView webView;
    private EditText comment;
    private Button commentSubmit;
    private Post post;
    private FetchData f;
    private ParseData p;
    private List<CommentModel> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        f = new FetchData(this);
        p = new ParseData(this);
        commentList = new ArrayList<>();
        db = new DatabaseHelper(this);
        pref = new PrefManager(this);
        post = new Post();

        long postId = Integer.parseInt(pref.getPostId());
        post = db.getPost(postId);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.share);

        detailImage = (ImageView) findViewById(R.id.detailImage);
        detailTitle = (TextView) findViewById(R.id.detailTitle);
        webView = (WebView) findViewById(R.id.webView);

        imageLoader = VolleySingleton.getInstance(this).getImageLoader();

        detailTitle.setText(post.getTitle());
        loadImages(post.getFullImage());
        webView.getSettings().setJavaScriptEnabled(false);
        webView.loadData(post.getContent(), "text/html", null);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, post.getUrl());
                sendIntent.setType("text/plain");
                Intent.createChooser(sendIntent, "Share via");
                startActivity(sendIntent);
            }
        });
        downloadComments();

        comment = (EditText) findViewById(R.id.comment);
        commentSubmit = (Button) findViewById(R.id.commentSubmit);

        commentSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentPost = comment.getText().toString().trim();

                if (commentPost.isEmpty()) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(NewsDetailActivity.this);
                    builder.setTitle("Something went wrong...");
                    builder.setMessage("CommentModel cannot be empty");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            comment.setText("");
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {

                    HashMap<String, String> user = pref.getUserDetails();
                    String username = user.get("name");
                    String email = user.get("email");

                    String postId = String.valueOf(post.getId());

                    username = username.replace(" ", "%20");
                    email = email.replace(" ", "%20");
                    commentPost = commentPost.replace(" ", "%20");

                    String url = Config.URL_POST_COMMENT + "?post_id=" + postId + "&name=" + username + "&email=" + email + "&content=" + commentPost;

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response != null && response.length() > 0) {
                                Toast.makeText(NewsDetailActivity.this, "COMMENT POSTED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                                comment.setText("");
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                    VolleySingleton.getInstance(NewsDetailActivity.this).addToRequestQueue(request);
                }
            }
        });

        final LinearLayout readCommentContainer = (LinearLayout) findViewById(R.id.readCommentContainer);
        final Button readCommentButton = (Button) findViewById(R.id.readCommentButton);
        final RecyclerView readCommentList = (RecyclerView) findViewById(R.id.readCommentList);
        readCommentList.setHasFixedSize(true);
        readCommentList.setNestedScrollingEnabled(false);

        readCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readCommentContainer.setVisibility(View.VISIBLE);

                readCommentList.setLayoutManager(new LinearLayoutManager(NewsDetailActivity.this));

                CommentAdapter adapter = new CommentAdapter(NewsDetailActivity.this, commentList);
                readCommentList.setAdapter(adapter);
                readCommentButton.setText("END OF COMMENTS");
                readCommentButton.setEnabled(false);

            }
        });


    }

    private void downloadComments() {
        String url = "http://peenyaindustries.org/api/get_post?id=" + post.getId();
        f.fetchData(new VolleyResponseCallback() {
            @Override
            public void onSuccess(String response) {
                commentList = p.fetchComments(response);
            }
        }, url);
    }

    private void loadImages(String urlThumbnail) {
        if (!urlThumbnail.equals(Constants.NA)) {
            imageLoader.get(urlThumbnail, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    detailImage.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
