package com.example.parstagram;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parstagram.databinding.ItemProfileBinding;
import com.google.common.collect.ImmutableList;
import com.parse.ParseFile;

import org.parceler.Parcels;

/**
 * Adapter for the list of posts in the profile of a user.
 * */
public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    private Activity context;
    private ImmutableList<Post> postsImmutable;

    /** Assign the context and list of data of the adapter. */
    public ProfileAdapter(Activity context, ImmutableList<Post> posts) {
        this.context = context;
        this.postsImmutable = posts;
    }

    /** Define and return the view for this adapter. */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProfileBinding binding = ItemProfileBinding.inflate(context.getLayoutInflater());
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

        ItemProfileBinding binding;

        /** Define the view binding for the adapter. */
        public ViewHolder(@NonNull View itemView, ItemProfileBinding bind) {
            super(itemView);
            bind.getRoot();
            binding = bind;
        }

        /** Bind all views of a single item with data from the item and set an on click listener for the image. */
        public void bind(Post post) {
            ParseFile image = post.getImage();
            binding.ivImage.setVisibility(View.GONE);
            if (image != null) {
                Glide.with(context).load(post.getImage().getUrl()).into(binding.ivImage);
                binding.ivImage.setVisibility(View.VISIBLE);
            }
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
        }
    }
}
