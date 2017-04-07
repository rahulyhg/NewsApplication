package org.peenyaindustries.piaconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.peenyaindustries.piaconnect.R;
import org.peenyaindustries.piaconnect.activity.NewsActivity;
import org.peenyaindustries.piaconnect.helper.DatabaseHelper;
import org.peenyaindustries.piaconnect.models.Category;
import org.peenyaindustries.piaconnect.models.Post;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context context;
    private List<Category> categoryList;
    private DatabaseHelper db;

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Category cats = categoryList.get(position);

        holder.catList.setText(cats.getTitle());
        holder.postCount.setText(String.valueOf(cats.getPostCount()));

        DatabaseHelper db = new DatabaseHelper(context);
        List<Post> post = new ArrayList<>();
        post = db.getAllPostsByCategory(cats.getTitle());
        String[] date = post.get(0).getDate().split("\\s+");

        holder.latestDate.setText(date[0]);

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView postCount, latestDate, catList;
        String catId;

        public ViewHolder(View itemView) {
            super(itemView);

            postCount = (TextView) itemView.findViewById(R.id.postCount);
            latestDate = (TextView) itemView.findViewById(R.id.latestDate);
            catList = (TextView) itemView.findViewById(R.id.categoryList);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, NewsActivity.class);
                    i.putExtra("title", catList.getText().toString().trim());
                    ((NewsActivity) context).finish();
                    context.startActivity(i);
                }
            });
        }
    }
}
