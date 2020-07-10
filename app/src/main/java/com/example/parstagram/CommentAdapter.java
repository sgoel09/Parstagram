package com.example.parstagram;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parstagram.databinding.ItemCommentBinding;
import com.example.parstagram.databinding.ItemPostBinding;
import com.example.parstagram.fragments.ProfileFragment;
import com.google.common.collect.ImmutableList;
import com.parse.ParseFile;
import com.parse.ParseObject;

import org.parceler.Parcels;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Activity context;
    //private List<Post> posts;
    private ImmutableList<Comment> commentsImmutable;

    public CommentAdapter(Activity context, ImmutableList<Comment> comments) {
        this.context = context;
        this.commentsImmutable = comments;
        //this.posts = posts;
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCommentBinding binding = ItemCommentBinding.inflate(context.getLayoutInflater());
        View view = binding.getRoot();
        return new CommentAdapter.ViewHolder(view, binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = commentsImmutable.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        //return posts.size();
        return commentsImmutable.size();
    }

    public void updateData(ImmutableList<Comment> comments) {
        commentsImmutable = comments;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ItemCommentBinding binding;

        public ViewHolder(@NonNull View itemView, ItemCommentBinding bind) {
            super(itemView);
            bind.getRoot();
            binding = bind;
        }

        public void bind(final Comment comment) {
            ParseFile profilePic = comment.getProfilePic();
            binding.ivProfilePic.setVisibility(View.GONE);
            if (profilePic != null) {
                Glide.with(context).load(comment.getProfilePic().getUrl()).circleCrop().into(binding.ivProfilePic);
                binding.ivProfilePic.setVisibility(View.VISIBLE);
            }
            binding.tvUsername.setText(comment.getUser().getUsername());
            binding.tvBody.setText(comment.getBody());
        }
    }
}