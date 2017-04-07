package org.peenyaindustries.piaconnect.helper;

import android.widget.Filter;

import org.peenyaindustries.piaconnect.adapters.NewsAdapter;
import org.peenyaindustries.piaconnect.models.Post;

import java.util.ArrayList;
import java.util.List;

public class CustomFilter extends Filter {

    NewsAdapter adapter;
    List<Post> filterList;

    public CustomFilter(NewsAdapter adapter, List<Post> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();

        //Check Constraint Validity
        if (constraint != null && constraint.length() > 0) {

            //Change to uppercase
            constraint = constraint.toString().toUpperCase();
            List<Post> filterPosts = new ArrayList<>();

            for (int i = 0; i < filterList.size(); i++) {

                if (filterList.get(i).getTitle().toUpperCase().contains(constraint)) {

                    filterPosts.add(filterList.get(i));

                }
            }

            results.count = filterPosts.size();
            results.values = filterPosts;
        } else {

            results.count = filterList.size();
            results.values = filterList;

        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        adapter.postList = (List<Post>) results.values;

        //refresh recyclerview
        adapter.notifyDataSetChanged();
    }
}
