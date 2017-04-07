package org.peenyaindustries.piaconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import org.peenyaindustries.piaconnect.R;
import org.peenyaindustries.piaconnect.activity.NewsDetailActivity;
import org.peenyaindustries.piaconnect.helper.Constants;
import org.peenyaindustries.piaconnect.helper.PrefManager;
import org.peenyaindustries.piaconnect.models.Post;
import org.peenyaindustries.piaconnect.network.VolleySingleton;

import java.util.List;

public class BaseViewAdapter extends RecyclerView.Adapter<BaseViewAdapter.ViewHolder> {

    private List<Post> mPosts;
    private boolean mPager;
    private ImageLoader imageLoader;
    private Context context;
    private PrefManager pref;

    public BaseViewAdapter(Context context, List<Post> mPosts, boolean mPager) {
        this.context = context;
        this.mPosts = mPosts;
        this.mPager = mPager;
        imageLoader = VolleySingleton.getInstance(context).getImageLoader();
        pref = new PrefManager(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mPager) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.base_adapter_pager, parent, false));
        } else {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.base_adapter, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Post post = mPosts.get(position);
        if (mPager) {
            if (post.getFullImage().equals("N/A")) {
                loadImages(post.getThumbnail(), holder);
            } else {
                loadImages(post.getFullImage(), holder);
            }
        } else {
            loadImages(post.getThumbnail(), holder);
        }

        holder.postTitle.setText(post.getTitle());
        holder.postId = String.valueOf(post.getId());
    }

    @Override
    public int getItemCount() {
        if (mPager) {
            if (mPosts.size() < 10) {
                return mPosts.size();
            } else {
                return 10;
            }
        } else {
            if (mPosts.size() < 6) {
                return mPosts.size();
            } else {
                return 6;
            }
        }
    }

    private void loadImages(String urlThumbnail, final ViewHolder holder) {
        if (!urlThumbnail.equals(Constants.NA)) {
            imageLoader.get(urlThumbnail, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    holder.postImage.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView postImage;
        TextView postTitle;
        private String postId;

        public ViewHolder(View itemView) {
            super(itemView);

            postImage = (ImageView) itemView.findViewById(R.id.postImage);
            postTitle = (TextView) itemView.findViewById(R.id.postTitle);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, NewsDetailActivity.class);
                    pref.setPostId(postId);
                    context.startActivity(i);
                }
            });
        }
    }
}
