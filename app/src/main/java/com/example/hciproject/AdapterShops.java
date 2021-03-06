package com.example.hciproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Schoox on 1/20/2017.
 */

public class AdapterShops extends ArrayAdapter {
    private Context context;
    private LayoutInflater li;
    private ArrayList<Shop> products;

    public AdapterShops(Context context, int resource, ArrayList<Shop> objects) {
        super(context, resource, objects);
        this.products =objects;
        this.context=context;
        li = LayoutInflater.from(context);
    }

    class Holder{
        ImageView image;
        TextView name;
        TextView stores;
        TextView price;
        int position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=null;
        if(convertView==null){
            view=li.inflate(R.layout.shop_cell,null);
            Holder holder=new Holder();
            holder.image= (ImageView) view.findViewById(R.id.category_image);
            holder.name=(TextView)view.findViewById(R.id.name);
            holder.stores=(TextView)view.findViewById(R.id.num_stores);
            holder.price=(TextView)view.findViewById(R.id.price);
            view.setTag(holder);
        }else{
            view=convertView;
        }
        Holder holder= (Holder) view.getTag();
        holder.name.setText(products.get(position).getName());
        holder.position=position;
        holder.price.setText(Double.toString(products.get(position).getPrice()));
        holder.stores.setText(products.get(position).isImmediate()?"Immediately available":products.get(position).getAvailabilityString());
        view.setTag(holder);
        return view;
    }
}
