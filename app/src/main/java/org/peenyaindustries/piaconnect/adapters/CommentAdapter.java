package org.peenyaindustries.piaconnect.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.peenyaindustries.piaconnect.R;
import org.peenyaindustries.piaconnect.models.CommentModel;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context context;
    private List<CommentModel> commentArrayList;

    public CommentAdapter(Context context, List<CommentModel> commentArrayList) {
        this.context = context;
        this.commentArrayList = commentArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (commentArrayList.size() > 0) {
            CommentModel commentModel = commentArrayList.get(position);

            holder.nameCommenter.setText("By, " + commentModel.getName());
            holder.dateCommenter.setText("On, " + commentModel.getDate());
            holder.contentCommenter.setText(Html.fromHtml(commentModel.getContent()));
        }
    }

    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameCommenter, dateCommenter, contentCommenter;

        public ViewHolder(View itemView) {
            super(itemView);
            nameCommenter = (TextView) itemView.findViewById(R.id.nameCommenter);
            dateCommenter = (TextView) itemView.findViewById(R.id.dateCommenter);
            contentCommenter = (TextView) itemView.findViewById(R.id.contentCommenter);
        }
    }
}

