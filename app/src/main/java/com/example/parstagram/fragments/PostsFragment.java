package com.example.parstagram.fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.parstagram.EndlessRecyclerViewScrollListener;
import com.example.parstagram.Post;
import com.example.parstagram.PostsAdapter;
import com.example.parstagram.R;
import com.example.parstagram.databinding.FragmentPostsBinding;
import com.google.common.collect.ImmutableList;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostsFragment extends Fragment {

    private static final String TAG = "PostsFragment";
    private static final int POST_LIMIT = 5;
    private EndlessRecyclerViewScrollListener scrollListener;
    protected PostsAdapter adapter;
    //protected List<Post> allPosts;
    protected ImmutableList<Post> allPostsImmutable;
    FragmentPostsBinding binding;
    private LinearLayoutManager layoutManager;

    public PostsFragment() {
        // Required empty public constructor
    }

    public static PostsFragment newInstance(String param1, String param2) {
        PostsFragment fragment = new PostsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_posts, container, false);
        binding = FragmentPostsBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //adapter.clear();
                allPostsImmutable = ImmutableList.of();
                adapter.updateData(allPostsImmutable);
                queryPosts();
                Log.i(TAG, "Fetched new data");
            }
        });

        //allPosts = new ArrayList<>();
        allPostsImmutable = ImmutableList.of();
        adapter = new PostsAdapter((Activity) getContext(), allPostsImmutable);
        binding.rvPosts.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getContext());
        binding.rvPosts.setLayoutManager(layoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "onLoadMore");
                loadMoreData(page);
            }
        };
        binding.rvPosts.addOnScrollListener(scrollListener);

        queryPosts();
    }

    private void loadMoreData(int page) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(POST_LIMIT);
        query.setSkip(POST_LIMIT*page);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }
                //allPosts.addAll(posts);
                List<Post> allPosts = allPostsImmutable;
                allPostsImmutable = ImmutableList.<Post>builder().addAll(allPosts).addAll(posts).build();
                //adapter.notifyDataSetChanged();
                adapter.updateData(allPostsImmutable);
                binding.swipeContainer.setRefreshing(false);
            }
        });
    }

    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(POST_LIMIT);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }
                //adapter.clear();
                //adapter.addAll(posts);
                allPostsImmutable = ImmutableList.<Post>builder().addAll(posts).build();
                //adapter.notifyDataSetChanged();
                adapter.updateData(allPostsImmutable);
                binding.swipeContainer.setRefreshing(false);
            }
        });
    }
}