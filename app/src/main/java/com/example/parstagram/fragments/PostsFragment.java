package com.example.parstagram.fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.parstagram.EndlessRecyclerViewScrollListener;
import com.example.parstagram.MainActivity;
import com.example.parstagram.Post;
import com.example.parstagram.PostsAdapter;
import com.example.parstagram.R;
import com.example.parstagram.databinding.FragmentPostsBinding;
import com.google.common.collect.ImmutableList;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Fragment in which user can view all posts and corresponding information.
 * */
public class PostsFragment extends Fragment {

    private static final String TAG = "PostsFragment";
    private static final int POST_LIMIT = 20;
    private EndlessRecyclerViewScrollListener scrollListener;
    private LinearLayoutManager layoutManager;
    protected PostsAdapter adapter;
    protected ImmutableList<Post> allPostsImmutable;
    FragmentPostsBinding binding;

    /** Required empty constructor */
    public PostsFragment() {}

    /** Execute onCreate to create the fragment. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /** Define and return the view for this fragment. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPostsBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();
        return view;
    }

    /** On creation, set refresh listener, posts adapter, and scroll listener. */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity) getContext()).getSupportActionBar().setLogo(R.drawable.ic_baseline_photo_camera);

        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                allPostsImmutable = ImmutableList.of();
                adapter.updateData(allPostsImmutable);
                queryPosts();
                Log.i(TAG, "Fetched new data");
            }
        });

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
    }

    /** When the fragment resumes, query posts again to update information. */
    @Override
    public void onResume() {
        super.onResume();
        queryPosts();
    }

    /** Make another query to get more posts, based on the page the user is on, and notify the adapter. */
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
                List<Post> allPosts = allPostsImmutable;
                allPostsImmutable = ImmutableList.<Post>builder().addAll(allPosts).addAll(posts).build();
                adapter.updateData(allPostsImmutable);
                binding.swipeContainer.setRefreshing(false);
            }
        });
    }

    /** Query the first set of posts from the database and notify the adapter. */
    private void queryPosts() {
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
                allPostsImmutable = ImmutableList.<Post>builder().addAll(posts).build();
                adapter.updateData(allPostsImmutable);
                binding.swipeContainer.setRefreshing(false);
            }
        });
    }
}