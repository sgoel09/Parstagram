package com.example.parstagram;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parstagram.databinding.ItemCommentBinding;
import com.google.common.collect.ImmutableList;
import com.parse.ParseFile;

/**
 * Adapter for the comments of a post.
 * */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Activity context;
    private ImmutableList<Comment> commentsImmutable;

    /** Assign the context and list of data of the adapter. */
    public CommentAdapter(Activity context, ImmutableList<Comment> comments) {
        this.context = context;
        this.commentsImmutable = comments;
    }

    /** Define and return the view for this adapter. */
    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCommentBinding binding = ItemCommentBinding.inflate(context.getLayoutInflater());
        View view = binding.getRoot();
        return new CommentAdapter.ViewHolder(view, binding);
    }

    /** Get the comment at the current position and bind the image views of the item. */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = commentsImmutable.get(position);
        holder.bind(comment);
    }

    /** @return the number of items. */
    @Override
    public int getItemCount() {
        return commentsImmutable.size();
    }

    /** Update the data and notify the adapter. */
    public void updateData(ImmutableList<Comment> comments) {
        commentsImmutable = comments;
        notifyDataSetChanged();
    }

    /** The ViewHolder for the adapter. */
    class ViewHolder extends RecyclerView.ViewHolder {

        ItemCommentBinding binding;

        /** Define the view binding for the adapter. */
        public ViewHolder(@NonNull View itemView, ItemCommentBinding bind) {
            super(itemView);
            bind.getRoot();
            binding = bind;
        }

        /** Bind all views of a single item with data from the item. */
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
