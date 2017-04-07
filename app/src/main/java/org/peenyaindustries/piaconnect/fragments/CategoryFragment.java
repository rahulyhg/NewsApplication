package org.peenyaindustries.piaconnect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.peenyaindustries.piaconnect.R;
import org.peenyaindustries.piaconnect.activity.NewsActivity;
import org.peenyaindustries.piaconnect.adapters.CategoryAdapter;
import org.peenyaindustries.piaconnect.helper.DatabaseHelper;
import org.peenyaindustries.piaconnect.models.Category;

import java.util.List;

public class CategoryFragment extends Fragment {

    List<Category> categoryList;
    CategoryAdapter adapter;
    DatabaseHelper db;

    public CategoryFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        db = new DatabaseHelper(getActivity());
        categoryList = db.getAllCategories();

        RecyclerView categoryListView = (RecyclerView) view.findViewById(R.id.categoryListView);
        categoryListView.setHasFixedSize(true);
        categoryListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new CategoryAdapter(getActivity(), categoryList);
        categoryListView.setAdapter(adapter);

        Button viewAll = (Button) view.findViewById(R.id.viewAll);
        viewAll.setText("VIEW ALL");
        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), NewsActivity.class);
                i.putExtra("title", "N/A");
                getActivity().finish();
                getActivity().startActivity(i);
            }
        });

        return view;
    }
}
