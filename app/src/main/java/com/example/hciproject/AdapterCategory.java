package com.example.hciproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Schoox on 1/20/2017.
 */

public class AdapterCategory extends ArrayAdapter {
    private Context context;
    private LayoutInflater li;
    private ArrayList<Category> categories;

    public AdapterCategory(Context context, int resource, ArrayList<Category> objects) {
        super(context, resource, objects);
        this.categories=objects;
        this.context=context;
        li = LayoutInflater.from(context);
    }

    class Holder{
        ImageView image;
        TextView name;
        int position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=null;
        if(convertView==null){
            view=li.inflate(R.layout.category_cell,null);
            Holder holder=new Holder();
            holder.image= (ImageView) view.findViewById(R.id.category_image);
            holder.name=(TextView)view.findViewById(R.id.name);
            view.setTag(holder);
        }else{
            view=convertView;
        }
        Holder holder= (Holder) view.getTag();
        holder.name.setText(categories.get(position).getName());
        holder.position=position;
        view.setTag(holder);
        Picasso.with(context).load(categories.get(position).getImageUrl()).into(holder.image);
        return view;
    }
}
