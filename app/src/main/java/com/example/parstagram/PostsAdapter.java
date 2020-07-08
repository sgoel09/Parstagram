package com.example.parstagram;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.parstagram.databinding.ItemPostBinding;
import com.example.parstagram.fragments.ProfileFragment;
import com.google.common.collect.ImmutableList;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Activity context;
    //private List<Post> posts;
    private ImmutableList<Post> postsImmutable;

    public PostsAdapter(Activity context, ImmutableList<Post> posts) {
        this.context = context;
        this.postsImmutable = posts;
        //this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPostBinding binding = ItemPostBinding.inflate(context.getLayoutInflater());
        View view = binding.getRoot();
        return new ViewHolder(view, binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Post post = posts.get(position);
        Post post = postsImmutable.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        //return posts.size();
        return postsImmutable.size();
    }

    // Clean all elements of the recycler
//    public void clear() {
//        posts.clear();
//        notifyDataSetChanged();
//    }
//
//    // Add a list of items -- change to type used
//    public void addAll(List<Post> list) {
//        posts.addAll(list);
//        notifyDataSetChanged();
//    }

    public void updateData(ImmutableList<Post> posts) {
        postsImmutable = posts;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ItemPostBinding binding;

        public ViewHolder(@NonNull View itemView, ItemPostBinding bind) {
            super(itemView);
            bind.getRoot();
            binding = bind;
        }

        public void bind(final Post post) {
            binding.tvDescription.setText(post.getDescription());
            binding.tvUsername.setText(post.getUser().getUsername());
            binding.tvSmallUsername.setText(post.getUser().getUsername());
            binding.tvCreatedAt.setText(post.getRelativeTime());
            ParseFile image = post.getImage();
            binding.ivImage.setVisibility(View.GONE);
            if (image != null) {
                Glide.with(context).load(post.getImage().getUrl()).into(binding.ivImage);
                binding.ivImage.setVisibility(View.VISIBLE);
            }
            ParseFile profilePic = post.getProfilePic();
            binding.ivProfilePic.setVisibility(View.GONE);
            if (profilePic != null) {
                Glide.with(context).load(post.getProfilePic().getUrl()).circleCrop().into(binding.ivProfilePic);
                binding.ivProfilePic.setVisibility(View.VISIBLE);
            }
            binding.ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        //Post post = posts.get(position);
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
        }
    }
}
