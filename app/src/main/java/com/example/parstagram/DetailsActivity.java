package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.parstagram.databinding.ActivityDetailsBinding;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.Date;

public class DetailsActivity extends AppCompatActivity {

    ActivityDetailsBinding binding;
    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.nav_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        post = (Post) Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));

        binding.tvDescription.setText(post.getDescription());
        binding.tvUsername.setText(post.getUser().getUsername());
        binding.tvSmallUsername.setText(post.getUser().getUsername());
        binding.tvCreatedAt.setText(getRelativeTime(post.getCreatedAt()));
        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(this).load(post.getImage().getUrl()).into(binding.ivImage);
        }
        ParseFile profilePic = post.getProfilePic();
        if (profilePic != null) {
            Glide.with(this).load(post.getProfilePic().getUrl()).circleCrop().into(binding.ivProfilePic);
            binding.ivProfilePic.setVisibility(View.VISIBLE);
        }
    }

    private String getRelativeTime(Date date) {
        long mills = date.getTime();
        return DateUtils.getRelativeTimeSpanString(mills).toString();
    }

}