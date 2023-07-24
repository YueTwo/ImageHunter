package com.example.sag.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sag.R;
import com.example.sag.newClass.generateItem;

import java.util.List;

public class generateItemAdapter extends RecyclerView.Adapter<generateItemAdapter.ViewHolder> {
    private List<generateItem> list;
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView itemTitle;
        TextView itemText;
        ImageView itemImage;
        public ViewHolder(@NonNull View view) {
            super(view);
            itemTitle = (TextView)view.findViewById(R.id.confidence);
            itemText = (TextView)view.findViewById(R.id.depict);
            itemImage=(ImageView)view.findViewById(R.id.bitmap);
        }
    }

    public generateItemAdapter(List<generateItem> itemList){
        list=itemList;
    }
    //添加item
    public void addItem(int position,generateItem item){
        list.add(item);
        notifyItemInserted(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.generate_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        generateItem item=list.get(position);
        holder.itemTitle.setText(item.getTitle());
        holder.itemText.setText(item.getText());
        if(item.getImage()!=null) {
            holder.itemImage.setImageBitmap(item.getImage());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
