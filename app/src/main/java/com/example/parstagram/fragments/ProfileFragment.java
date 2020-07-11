package com.example.parstagram.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.parstagram.MainActivity;
import com.example.parstagram.Post;
import com.example.parstagram.ProfileAdapter;
import com.example.parstagram.databinding.FragmentProfileBinding;
import com.google.common.collect.ImmutableList;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

/**
 * Fragment in which user can view the profile of a user, including all their posts.
 */
public class ProfileFragment extends Fragment {
    private static final int POST_LIMIT = 5;
    private static final String TAG = "ProfileFragment";
    private static final int GALLERY_REQUEST_CODE = 20;
    private ProfileAdapter adapter;
    private String username;
    private ParseUser user;
    protected ImmutableList<Post> allPostsImmutable;
    FragmentProfileBinding binding;

    /** Required empty constructor */
    public ProfileFragment() {}

    /** On a new instance, pass in the username and user whose profile will be shown. */
    public static ProfileFragment newInstance(String username, ParseUser user) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("username", username);
        args.putParcelable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    /** Execute onCreate to create the fragment. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /** Define and return the view for this fragment. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();
        return view;
    }

    /** On creation, update views with user information, set profile adapter, and click listener if it is the current user's profile. */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity) getContext()).getSupportActionBar().setLogo(null);

        binding.pbLoading.setVisibility(View.VISIBLE);
        username = getArguments().getString("username");
        user = getArguments().getParcelable("user");

        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.include("profilepic");
        query.setLimit(1);
        query.whereEqualTo("objectId", user.getObjectId());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                ParseFile file = objects.get(0).getParseFile("profilepic");
                Glide.with(getContext()).load(file.getUrl()).circleCrop().into(binding.ivProfilePic);
            }
        });

        binding.tvUsername.setText(username);

        if (user.getObjectId() == ParseUser.getCurrentUser().getObjectId()) {
            binding.ivProfilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("ProfileFragment", "clicked profile pic");
                    pickFromGallery();
                }
            });
        } else {
            binding.ivAddProfilePic.setVisibility(View.GONE);
        }

        allPostsImmutable = ImmutableList.of();
        adapter = new ProfileAdapter((Activity) getContext(), allPostsImmutable);
        binding.rvPosts.setAdapter(adapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        binding.rvPosts.setLayoutManager(layoutManager);
        queryPosts();
    }

    /** Get the data and set the profile picture image view to the selected one. */
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE) {
            Uri selectedImageUri = data.getData();
            saveProfilePic(selectedImageUri);
            binding.ivProfilePic.setImageURI(selectedImageUri);
        } else {
            Log.e("ProfileFragment", "could not get data");
        }
    }

    /** Defines the field photoFile to be a ParseFile represented by the imague uri parameter. */
    private void saveProfilePic(Uri selectedImageUri) {
        InputStream imageStream = null;
        try {
            imageStream = getContext().getContentResolver().openInputStream(selectedImageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bmp = BitmapFactory.decodeStream(imageStream);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapBytes = stream.toByteArray();
        final ParseFile newProfilePic = new ParseFile(bitmapBytes);
        ParseUser user = ParseUser.getCurrentUser();
        user.put("profilepic", newProfilePic);
        user.saveInBackground();
    }

    /** Create an intent and start the gallery activity */
    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    /** Query the first set of posts from the database and notify the adapter. */
    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
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
                allPostsImmutable = ImmutableList.copyOf(posts);
                adapter.updateData(allPostsImmutable);
                binding.pbLoading.setVisibility(View.GONE);
            }
        });
    }
}