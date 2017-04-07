package org.peenyaindustries.piaconnect.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.peenyaindustries.piaconnect.R;
import org.peenyaindustries.piaconnect.adapters.NewsAdapter;
import org.peenyaindustries.piaconnect.helper.DatabaseHelper;
import org.peenyaindustries.piaconnect.models.Post;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment {

    private RecyclerView newsListView;
    private NewsAdapter adapter;
    private DatabaseHelper db;
    private List<Post> postList;
    private SearchView searchView;
    private String catTitle = "N/A";

    public NewsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        Bundle extras = getActivity().getIntent().getExtras();

        if (extras != null) {
            catTitle = extras.getString("title");
        }

        db = new DatabaseHelper(getActivity());

        postList = new ArrayList<>();

        if (catTitle.equals("N/A")) {
            postList = db.getAllPosts();
        } else {
            postList = db.getAllPostsByCategory(catTitle);
        }

        db.closeDB();

        searchView = (SearchView) view.findViewById(R.id.searchView);

        newsListView = (RecyclerView) view.findViewById(R.id.newsListView);
        newsListView.setHasFixedSize(true);
        newsListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new NewsAdapter(getActivity(), postList);
        newsListView.setAdapter(adapter);

        //Search Functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                //Filter while typing
                adapter.getFilter().filter(query);
                return false;
            }
        });

        return view;
    }
}
