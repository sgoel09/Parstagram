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

import com.bumptech.glide.Glide;
import com.example.parstagram.EndlessRecyclerViewScrollListener;
import com.example.parstagram.Post;
import com.example.parstagram.PostsAdapter;
import com.example.parstagram.ProfileAdapter;
import com.example.parstagram.R;
import com.example.parstagram.databinding.FragmentPostsBinding;
import com.example.parstagram.databinding.FragmentProfileBinding;
import com.google.common.collect.ImmutableList;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private static final int POST_LIMIT = 5;
    private static final String TAG = "ProfileFragment";
    private ProfileAdapter adapter;
    private String username;
    private ParseUser user;
    FragmentProfileBinding binding;
    protected List<Post> allPosts;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String username, ParseUser user) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("username", username);
        args.putParcelable("user", user);
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
        binding = FragmentProfileBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        username = getArguments().getString("username");
        user = getArguments().getParcelable("user");
        binding.tvUsername.setText(username);

        allPosts = new ArrayList<>();
        //allPostsImmutable = ImmutableList.of();
        adapter = new ProfileAdapter((Activity) getContext(), allPosts);
        //adapter = new PostsAdapter((Activity) getContext(), allPostsImmutable);
        binding.rvPosts.setAdapter(adapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        binding.rvPosts.setLayoutManager(layoutManager);
        //binding.tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        ParseFile profilePic = Post.getProfilePicFromUser(user);
        if (profilePic != null) {
            Glide.with(this).load(profilePic.getUrl()).circleCrop().into(binding.ivProfilePic);
            binding.ivProfilePic.setVisibility(View.VISIBLE);
        }
        queryPosts();
    }

    private void loadMoreData(int page) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
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
                allPosts.addAll(posts);
                //allPostsImmutable = ImmutableList.copyOf(posts);
                adapter.notifyDataSetChanged();
                binding.swipeContainer.setRefreshing(false);
            }
        });
    }

    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        //query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(Post.KEY_USER, user);
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
                allPosts.addAll(posts);
                //allPostsImmutable = ImmutableList.copyOf(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }
}