package com.example.parstagram;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parstagram.databinding.ItemPostBinding;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Activity context;
    private List<Post> posts;

    public PostsAdapter(Activity context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
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
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ItemPostBinding binding;

        public ViewHolder(@NonNull View itemView, ItemPostBinding bind) {
            super(itemView);
            bind.getRoot();
            binding = bind;
        }

        public void bind(Post post) {
            binding.tvDescription.setText(post.getDescription());
            binding.tvUsername.setText(post.getUser().getUsername());
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
                        Post post = posts.get(position);
                        Intent intent = new Intent(context, DetailsActivity.class);
                        intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
                        context.startActivity(intent);
                    }
                }
            });
            binding.tvUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }
}
