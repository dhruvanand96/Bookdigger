package com.example.hp.bookseller;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatUserListAdapter extends RecyclerView.Adapter<ChatUserListAdapter.ViewHolder>
{
    ArrayList<UserBean> useritemlist;
    Context context;

    public ChatUserListAdapter(Context context, int row, ArrayList<UserBean> useritemlist)
    {
        this.context=context;
        this.useritemlist=useritemlist;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_users_listing,parent,false);

        ViewHolder holder=new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        UserBean user=useritemlist.get(position);

        holder.User_Alphabet.setText(String.valueOf(user.getEmail().charAt(0)));
        holder.UserName.setText(user.getEmail());


    }

    @Override
    public int getItemCount()
    {
        return useritemlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView UserName,User_Alphabet;

        public ViewHolder(View itemView)
        {
            super(itemView);
            User_Alphabet = (TextView)itemView.findViewById(R.id.text_view_user_alphabet);
            UserName=(TextView)itemView.findViewById(R.id.text_view_username);


        }
    }


}
