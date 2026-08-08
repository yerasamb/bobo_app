package com.bobo.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bobo.app.R;
import com.bobo.app.PhotoDetailActivity;
import com.bobo.app.model.PhotoResponse;
import com.bumptech.glide.Glide;

import android.content.Intent;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private final List<PhotoResponse> photos;

    public PhotoAdapter(List<PhotoResponse> photos) {
        this.photos = photos;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo, parent, false);

        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {

        PhotoResponse photo = photos.get(position);

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(photo.getUploadTime());
            holder.textTime.setText(outputFormat.format(date));
        } catch (Exception e) {
            holder.textTime.setText(photo.getUploadTime());
        }
        holder.textTeacher.setText("Teacher: " + photo.getTeacher());
        holder.textMealType.setText(photo.getMealType());

        String imageUrl = photo.getImageUrl();
        if (imageUrl != null && !imageUrl.startsWith("http")) {
            imageUrl = "http://192.168.8.123:8080/" + imageUrl;
        }

        android.util.Log.d("PhotoAdapter", "Loading image: " + imageUrl);

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.imagePhoto);

        final String finalImageUrl = imageUrl;
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PhotoDetailActivity.class);
            intent.putExtra("imageUrl", finalImageUrl);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {

        ImageView imagePhoto;
        TextView textTime;
        TextView textTeacher;
        TextView textChild;
        TextView textMealType;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);

            imagePhoto = itemView.findViewById(R.id.imagePhoto);
            textTime = itemView.findViewById(R.id.textTime);
            textChild = itemView.findViewById(R.id.textChild);
            textTeacher = itemView.findViewById(R.id.textTeacher);
            textMealType = itemView.findViewById(R.id.textMealType);
        }
    }
}