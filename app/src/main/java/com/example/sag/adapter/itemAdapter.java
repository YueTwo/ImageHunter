package com.example.sag.adapter;
import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sag.activities.ImagePreview;
import com.example.sag.activities.SearchActivity;
import com.example.sag.activities.VideoPlayerActivity;
import com.example.sag.newClass.Item;
import com.example.sag.R;
import com.example.sag.newClass.generateItem;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class itemAdapter extends RecyclerView.Adapter<itemAdapter.ViewHolder> {
    private List<Item> list;
    private boolean first=false;
    private MediaPlayer mediaPlayer;
    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView itemImage;
        TextView itemTitle;
        TextView itemTime;
        public ViewHolder(@NonNull View view) {
            super(view);
            itemImage = (ImageView) view.findViewById(R.id.image_item);
            itemTitle = (TextView)view.findViewById(R.id.title_item);
            itemTime = (TextView)view.findViewById(R.id.time_item);
        }
    }

    public itemAdapter(List<Item> itemList){
        list=itemList;
    }

    //添加item
    public void addItem(int position,Item item){
        list.add(item);
        notifyItemInserted(position);
    }
    public void updateItem(List<Item> data){
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        holder.itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Log.e("ZY", String.valueOf(position));
                Item item = list.get(position);
                String filePath=item.getTime();
                if(whatFileType(filePath)==0) {
                    Intent intent =new Intent(v.getContext(), VideoPlayerActivity.class);
                    intent.putExtra("FILE_PATH", filePath);
                    v.getContext().startActivity(intent);
                }else if(whatFileType(filePath)==1){
                    Intent intent =new Intent(v.getContext(), ImagePreview.class);
                    intent.putExtra("FILE_PATH", filePath);
                    v.getContext().startActivity(intent);
                }else{
                    //play audio
                    playAudio(filePath);
                }
            }
        });
        return holder;
    }

    private void playAudio(String audioFilePath) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        } else {
            mediaPlayer.reset();
        }
        try {
            mediaPlayer.setDataSource(audioFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int whatFileType(String filePath) {
        File file = new File(filePath);
        String extension = "";

        int i = filePath.lastIndexOf('.');
        if (i >= 0) {
            extension = filePath.substring(i + 1);
        }

        if (extension.equalsIgnoreCase("jpg") ||
                extension.equalsIgnoreCase("jpeg") ||
                extension.equalsIgnoreCase("png")) {
            return 1;
        } else if (extension.equalsIgnoreCase("mp4") ||
                extension.equalsIgnoreCase("avi") ||
                extension.equalsIgnoreCase("mov")) {
            return 0;
        } else {
            return 2;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item=list.get(position);
        holder.itemTitle.setText(item.getText());
        holder.itemTime.setText(item.getTime());
        if(item.getImage()!=null) {
            holder.itemImage.setImageBitmap(item.getImage());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
