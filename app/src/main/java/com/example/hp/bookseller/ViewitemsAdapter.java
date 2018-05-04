package com.example.hp.bookseller;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by HP on 20-08-2017.
 */

public class ViewitemsAdapter extends RecyclerView.Adapter<ViewitemsAdapter.ViewHolder> {

   ArrayList<BookBean> bookitemlist,bookstemplist;
    Context context;
    Picasso picasso;

    public ViewitemsAdapter(Context context,int row,ArrayList<BookBean> bookitemlist)
    {
        this.context=context;
        this.bookitemlist=bookitemlist;

        bookstemplist= new ArrayList<>();
        bookstemplist.addAll(this.bookitemlist);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_listing,parent,false);

        ViewHolder holder=new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        BookBean book=bookitemlist.get(position);
        picasso.with(context).load(book.getImage()).into(holder.bookimage);
        holder.bookname.setText(book.getName());
        holder.bookprice.setText("\u20B9"+book.getPrice());

    }

    @Override
    public int getItemCount()
    {
        return bookitemlist.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView bookimage;
        TextView bookname,bookprice;

        public ViewHolder(View itemView)
        {
            super(itemView);
            bookimage=(ImageView) itemView.findViewById(R.id.imageviewbook);
            bookname=(TextView)itemView.findViewById(R.id.textviewbookname);
            bookprice=(TextView)itemView.findViewById(R.id.textviewbookprice);
        }
    }

    public void filter(String str){

        // Optimise Search Algo here

        bookitemlist.clear();
        if(str.length()==0){
            bookitemlist.addAll(bookstemplist);
        }else{
            for(BookBean user : bookstemplist){
                if(user.getName().toLowerCase().contains(str.toLowerCase())){
                    bookitemlist.add(user);
                }
            }
        }

        notifyDataSetChanged();
    }
}
