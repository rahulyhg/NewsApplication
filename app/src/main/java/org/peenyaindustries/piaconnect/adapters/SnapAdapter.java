package org.peenyaindustries.piaconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;

import org.peenyaindustries.piaconnect.R;
import org.peenyaindustries.piaconnect.activity.NewsActivity;
import org.peenyaindustries.piaconnect.models.Snap;

import java.util.ArrayList;

public class SnapAdapter extends RecyclerView.Adapter<SnapAdapter.ViewHolder> implements GravitySnapHelper.SnapListener {

    private ArrayList<Snap> mSnaps;
    private Context context;
    // Disable touch detection for parent recyclerView if we use vertical nested recyclerViews
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        }
    };

    public SnapAdapter(Context context) {
        mSnaps = new ArrayList<>();
        this.context = context;
    }

    public void addSnap(Snap snap) {
        mSnaps.add(snap);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.base_adapter_snap, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Snap snap = mSnaps.get(position);
        holder.snapTextView.setText(snap.getmText());

        if (snap.getmGravity() == Gravity.START || snap.getmGravity() == Gravity.END) {
            holder.categoryView.setLayoutManager(new LinearLayoutManager(holder
                    .categoryView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            holder.categoryView.setOnFlingListener(null);
            new GravitySnapHelper(snap.getmGravity(), false, this).attachToRecyclerView(holder.categoryView);
        } else if (snap.getmGravity() == Gravity.CENTER_HORIZONTAL) {
            holder.categoryView.setLayoutManager(new LinearLayoutManager(holder
                    .categoryView.getContext(), snap.getmGravity() == Gravity.CENTER_HORIZONTAL ?
                    LinearLayoutManager.HORIZONTAL : LinearLayoutManager.VERTICAL, false));
            holder.categoryView.setOnFlingListener(null);
            new LinearSnapHelper().attachToRecyclerView(holder.categoryView);
        } else if (snap.getmGravity() == Gravity.CENTER) { // Pager snap
            holder.categoryView.setLayoutManager(new LinearLayoutManager(holder
                    .categoryView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            holder.categoryView.setOnFlingListener(null);
            new PagerSnapHelper().attachToRecyclerView(holder.categoryView);
        } else { // Top / Bottom
            holder.categoryView.setLayoutManager(new LinearLayoutManager(holder
                    .categoryView.getContext()));
            holder.categoryView.setOnFlingListener(null);
            new GravitySnapHelper(snap.getmGravity()).attachToRecyclerView(holder.categoryView);
        }

        BaseViewAdapter adapter = new BaseViewAdapter(context, snap.getmPosts(),
                snap.getmGravity() == Gravity.CENTER);
        holder.categoryView.setAdapter(adapter);

        holder.viewAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, NewsActivity.class);
                i.putExtra("title", snap.getmText());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSnaps.size();
    }

    @Override
    public void onSnap(int position) {
        Log.d("Snapped: ", position + "");
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView snapTextView;
        public RecyclerView categoryView;
        public Button viewAllBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            snapTextView = (TextView) itemView.findViewById(R.id.snapTextView);
            categoryView = (RecyclerView) itemView.findViewById(R.id.categoryView);
            viewAllBtn = (Button) itemView.findViewById(R.id.viewAllBtn);
        }

    }
}
