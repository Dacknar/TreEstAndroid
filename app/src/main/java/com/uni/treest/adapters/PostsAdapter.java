package com.uni.treest.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uni.treest.R;
import com.uni.treest.models.Line;
import com.uni.treest.models.Post;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private List<Post> postList = new ArrayList<>();
    private String userId;

    public PostsAdapter(String userId) {
        this.userId = userId;
        Log.d("PostFragment", "uID is: " + userId);
    }

    public void setPosts(List<Post> allPosts){
        postList = new ArrayList<>(allPosts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_posts_item, parent, false);

        return new PostsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.authorName.setText(postList.get(position).getAuthorName());
        holder.publishedDate.setText(postList.get(position).getPostDate());
        holder.follow.setText(postList.get(position).isFollowingAuthor());
        if (postList.get(position).getAuthorID().equals(userId)){
            holder.follow.setVisibility(View.GONE);
        }
        holder.state.setText(postList.get(position).getStatus());
        holder.delay.setText(postList.get(position).getDelay());
        holder.comment.setText(postList.get(position).getComment());
        holder.authorImage.setImageBitmap(postList.get(position).getAuthorImage());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView authorName, publishedDate, state, delay, comment;
        Button follow;
        CircleImageView authorImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            authorName = itemView.findViewById(R.id.postRecycler_author);
            publishedDate = itemView.findViewById(R.id.postRecycler_published);
            state = itemView.findViewById(R.id.postRecycler_state);
            delay = itemView.findViewById(R.id.postRecycler_delay);
            comment = itemView.findViewById(R.id.postRecycler_comment);

            follow = itemView.findViewById(R.id.postRecycler_button);

            authorImage = itemView.findViewById(R.id.postRecycler_avatar);
        }
    }
}
