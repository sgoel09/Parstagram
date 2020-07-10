package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.parstagram.databinding.ActivityDetailsBinding;
import com.google.common.collect.ImmutableList;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.parstagram.R.drawable.nav_logo;

public class DetailsActivity extends AppCompatActivity {

    ActivityDetailsBinding binding;
    private Post post;
    protected CommentAdapter adapter;
    protected ImmutableList<Comment> allCommentsImmutable;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(nav_logo);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        post = (Post) Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));

        binding.tvDescription.setText(post.getDescription());
        binding.tvUsername.setText(post.getUser().getUsername());
        binding.tvSmallUsername.setText(post.getUser().getUsername());
        binding.tvCreatedAt.setText(getRelativeTime(post.getCreatedAt()));
        if (post.getLikes() != null) {
            updateLikes(post.getLikes());
        } else {
            setLikesNone();
        }
        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(this).load(post.getImage().getUrl()).into(binding.ivImage);
        }
        ParseFile profilePic = post.getProfilePic();
        if (profilePic != null) {
            Glide.with(this).load(post.getProfilePic().getUrl()).circleCrop().into(binding.ivProfilePic);
            binding.ivProfilePic.setVisibility(View.VISIBLE);
        }

        allCommentsImmutable = ImmutableList.of();
        adapter = new CommentAdapter(this, allCommentsImmutable);
        binding.rvComments.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this);
        binding.rvComments.setLayoutManager(layoutManager);

        binding.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> likes = post.getLikes();
                if (likes == null) {
                    likes = new ArrayList<>();
                }
                if (likes.contains(ParseUser.getCurrentUser().getUsername())) {
                    likes.remove(ParseUser.getCurrentUser().getUsername());
                } else {
                    likes.add(ParseUser.getCurrentUser().getUsername());
                }
                updateLikes(likes);
                post.setLikes(likes);
                post.saveInBackground();
            }
        });

        queryComments();
    }

    private void updateLikes(ArrayList<String> likes) {
        ColorStateList csl;
        if (likes.contains(ParseUser.getCurrentUser().getUsername())) {
            csl = AppCompatResources.getColorStateList(this, R.color.colorRed);
        } else {
            csl = AppCompatResources.getColorStateList(this, R.color.colorGray);
        }
        ImageViewCompat.setImageTintList(binding.ivLike, csl);
        binding.tvLikes.setText(String.format("%s likes", likes.size()));
    }

    private void setLikesNone() {
        ColorStateList csl = AppCompatResources.getColorStateList(this, R.color.colorGray);
        ImageViewCompat.setImageTintList(binding.ivLike, csl);
        binding.tvLikes.setText("0 likes");
    }

    private String getRelativeTime(Date date) {
        long mills = date.getTime();
        return DateUtils.getRelativeTimeSpanString(mills).toString();
    }

    private void queryComments() {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.include(Comment.KEY_USER);
        query.include(Comment.KEY_POST);
        query.whereEqualTo(Comment.KEY_POST, post);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, ParseException e) {
                if (e != null) {
                    Log.e("DetailsActivity", "Issue with getting comments", e);
                    return;
                }
                //adapter.clear();
                //adapter.addAll(posts);
                allCommentsImmutable = ImmutableList.<Comment>builder().addAll(comments).build();
                //adapter.notifyDataSetChanged();
                adapter.updateData(allCommentsImmutable);
            }
        });
    }

}