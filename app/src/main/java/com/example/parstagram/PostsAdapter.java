package com.example.parstagram;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parstagram.databinding.ItemPostBinding;
import com.example.parstagram.fragments.ProfileFragment;
import com.google.common.collect.ImmutableList;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;

/**
 * Adapter for the list of posts.
 * */
public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Activity context;
    private ImmutableList<Post> postsImmutable;

    /** Assign the context and list of data of the adapter. */
    public PostsAdapter(Activity context, ImmutableList<Post> posts) {
        this.context = context;
        this.postsImmutable = posts;
    }

    /** Define and return the view for this adapter. */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPostBinding binding = ItemPostBinding.inflate(context.getLayoutInflater());
        View view = binding.getRoot();
        return new ViewHolder(view, binding);
    }

    /** Get the post at the current position and bind the image views of the item. */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postsImmutable.get(position);
        holder.bind(post);
    }

    /** @return the number of items. */
    @Override
    public int getItemCount() {
        return postsImmutable.size();
    }

    /** Update the data and notify the adapter. */
    public void updateData(ImmutableList<Post> posts) {
        postsImmutable = posts;
        notifyDataSetChanged();
    }

    /** The ViewHolder for the adapter. */
    class ViewHolder extends RecyclerView.ViewHolder {

        ItemPostBinding binding;

        /** Define the view binding for the adapter. */
        public ViewHolder(@NonNull View itemView, ItemPostBinding bind) {
            super(itemView);
            bind.getRoot();
            binding = bind;
        }

        /** Bind all views of a single item with data from the item and set on click listeners for image, username, and like icon. */
        public void bind(final Post post) {
            binding.tvDescription.setText(post.getDescription());
            binding.tvUsername.setText(post.getUser().getUsername());
            binding.tvSmallUsername.setText(post.getUser().getUsername());
            binding.tvCreatedAt.setText(post.getRelativeTime());
            ParseFile image = post.getImage();
            binding.ivImage.setVisibility(View.GONE);
            if (image != null) {
                Glide.with(context).load(post.getImage().getUrl()).placeholder(R.drawable.camera_shadow_fill).into(binding.ivImage);
                binding.ivImage.setVisibility(View.VISIBLE);
            }
            ParseFile profilePic = post.getProfilePic();
            binding.ivProfilePic.setVisibility(View.GONE);
            if (profilePic != null) {
                Glide.with(context).load(post.getProfilePic().getUrl()).circleCrop().into(binding.ivProfilePic);
                binding.ivProfilePic.setVisibility(View.VISIBLE);
            }
            updateLikes(post.getLikes());
            binding.ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Post post = postsImmutable.get(position);
                        Intent intent = new Intent(context, DetailsActivity.class);
                        intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
                        context.startActivity(intent);
                    }
                }
            });
            binding.tvUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = ((MainActivity) context).getSupportFragmentManager();
                    ProfileFragment profileFragment = ProfileFragment.newInstance(post.getUser().getUsername(), post.getUser());
                    fm.beginTransaction().replace(R.id.flContainer, profileFragment).commit();
                }
            });
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
        }

        /** Update the like icon and number based on the likes array. */
        private void updateLikes(ArrayList<String> likes) {
            if (likes == null) {
                setLikesNone();
            } else {
                ColorStateList csl;
                if (likes.contains(ParseUser.getCurrentUser().getUsername())) {
                    csl = AppCompatResources.getColorStateList(context, R.color.colorRed);
                } else {
                    csl = AppCompatResources.getColorStateList(context, R.color.colorGray);
                }
                ImageViewCompat.setImageTintList(binding.ivLike, csl);
                if (likes.size() == 1) {
                    binding.tvLikes.setText(String.format("1 like"));
                } else {
                    binding.tvLikes.setText(String.format("%s likes", likes.size()));
                }
            }
        }

        /** Sets like icon and number when there are none. */
        private void setLikesNone() {
            ColorStateList csl = AppCompatResources.getColorStateList(context, R.color.colorGray);
            ImageViewCompat.setImageTintList(binding.ivLike, csl);
            binding.tvLikes.setText("0 likes");
        }
    }
}
