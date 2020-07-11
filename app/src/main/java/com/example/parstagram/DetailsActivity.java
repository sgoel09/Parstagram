package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The details activity of a post.
 */
public class DetailsActivity extends AppCompatActivity {

    private Post post;
    private LinearLayoutManager layoutManager;
    protected CommentAdapter adapter;
    protected ImmutableList<Comment> allCommentsImmutable;
    ActivityDetailsBinding binding;

    /** Set view binding, update the views with the post information, and set click listeners for the like icon and comment button. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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

        binding.btnPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String body = binding.etComment.getText().toString();
                saveComment(body);
            }
        });

        queryComments();
    }

    /** Creates and saves a comment object. */
    private void saveComment(String body) {
        Comment comment = new Comment();
        comment.setBody(body);
        comment.setUser(ParseUser.getCurrentUser());
        comment.setPost(post);
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("DetailsActivity", "Error while saving", e);
                    return;
                }
                Log.i("DetailsActivity", "Comment saved successfully");
                binding.etComment.setText("");
                queryComments();
            }
        });
    }

    /** Update the like icon and number based on the likes array. */
    private void updateLikes(ArrayList<String> likes) {
        ColorStateList csl;
        if (likes.contains(ParseUser.getCurrentUser().getUsername())) {
            csl = AppCompatResources.getColorStateList(this, R.color.colorRed);
        } else {
            csl = AppCompatResources.getColorStateList(this, R.color.colorGray);
        }
        ImageViewCompat.setImageTintList(binding.ivLike, csl);
        if (likes.size() == 1) {
            binding.tvLikes.setText(String.format("1 like"));
        } else {
            binding.tvLikes.setText(String.format("%s likes", likes.size()));
        }
    }

    /** Sets like icon and number when there are none. */
    private void setLikesNone() {
        ColorStateList csl = AppCompatResources.getColorStateList(this, R.color.colorGray);
        ImageViewCompat.setImageTintList(binding.ivLike, csl);
        binding.tvLikes.setText("0 likes");
    }

    /** @return the relative time of the post. */
    private String getRelativeTime(Date date) {
        long mills = date.getTime();
        return DateUtils.getRelativeTimeSpanString(mills).toString();
    }

    /** Query all the comments of the post. */
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
                allCommentsImmutable = ImmutableList.<Comment>builder().addAll(comments).build();
                adapter.updateData(allCommentsImmutable);
            }
        });
    }
}