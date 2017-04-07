package org.peenyaindustries.piaconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import org.peenyaindustries.piaconnect.R;
import org.peenyaindustries.piaconnect.activity.NewsDetailActivity;
import org.peenyaindustries.piaconnect.callback.ClickListener;
import org.peenyaindustries.piaconnect.helper.Constants;
import org.peenyaindustries.piaconnect.helper.CustomFilter;
import org.peenyaindustries.piaconnect.helper.PrefManager;
import org.peenyaindustries.piaconnect.models.Post;
import org.peenyaindustries.piaconnect.network.VolleySingleton;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> implements Filterable {

    Context context;
    public List<Post> postList;
    List<Post> filterList;
    ImageLoader imageLoader;
    CustomFilter filter;
    private PrefManager pref;

    public NewsAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
        this.filterList = postList;
        imageLoader = VolleySingleton.getInstance(context).getImageLoader();
        pref = new PrefManager(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Post post = postList.get(position);
        loadImages(post.getFullImage(), holder);
        holder.postTitle.setText(post.getTitle());
        holder.postContent.setText(post.getExcerpt());

        holder.postId = String.valueOf(post.getId());
    }

    @Override
    public int getItemCount() {
        return postList.size();
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

    //return filter object
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CustomFilter(this, filterList);
        }

        return filter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ClickListener listener;
        ImageView postImage;
        TextView postTitle, postContent;
        String postId;

        public ViewHolder(View itemView) {
            super(itemView);
            postImage = (ImageView) itemView.findViewById(R.id.postImage);
            postTitle = (TextView) itemView.findViewById(R.id.postTitle);
            postContent = (TextView) itemView.findViewById(R.id.postContent);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, NewsDetailActivity.class);
                    pref.setPostId(postId);
                    context.startActivity(i);
                }
            });
        }

        @Override
        public void onClick(View v) {
            this.listener.onItemClick(v, getLayoutPosition());
        }

        public void setItemClickListener(ClickListener listener) {
            this.listener = listener;
        }
    }
}
