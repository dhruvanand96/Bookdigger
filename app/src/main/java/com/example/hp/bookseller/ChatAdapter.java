package com.example.hp.bookseller;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * Created by tania on 29/9/17.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    Context context;

    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;

    private ArrayList<ChatBean> mChats;


    public ChatAdapter(ArrayList<ChatBean> chats) {
        mChats = chats;
    }



    public ChatAdapter(Context context,int row,ArrayList<ChatBean> chatitemlist)
    {
        this.context=context;
        this.mChats=chatitemlist;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType)
        {
            case VIEW_TYPE_ME:
                View viewChatMine = layoutInflater.inflate(R.layout.item_chat_mine, parent, false);
                viewHolder = new MyChatViewHolder(viewChatMine);
                break;
            case VIEW_TYPE_OTHER:
                View viewChatOther = layoutInflater.inflate(R.layout.item_chat_other, parent, false);
                viewHolder = new OtherChatViewHolder(viewChatOther);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {

        if (TextUtils.equals(mChats.get(position).getSenderUid(),
                FirebaseAuth.getInstance().getCurrentUser().getUid()))
        {
            configureMyChatViewHolder((MyChatViewHolder) holder, position);
        }
        else
        {
            configureOtherChatViewHolder((OtherChatViewHolder) holder, position);
        }

    }

    public void add(ChatBean chat)
    {
        mChats.add(chat);
        notifyItemInserted(mChats.size() - 1);
    }


    private void configureMyChatViewHolder(MyChatViewHolder myChatViewHolder, int position)
    {
        ChatBean chat = mChats.get(position);

        String alphabet = chat.getSender().substring(0, 1);

        myChatViewHolder.txtChatMessage.setText(chat.getMessage());
        myChatViewHolder.txtUserAlphabet.setText(alphabet);
    }

    private void configureOtherChatViewHolder(OtherChatViewHolder otherChatViewHolder, int position) {
        ChatBean chat = mChats.get(position);

        String alphabet = chat.sender.substring(0, 1);

        otherChatViewHolder.txtChatMessage.setText(chat.getMessage());
        otherChatViewHolder.txtUserAlphabet.setText(alphabet);
    }

    @Override
    public int getItemCount()
    {
        if (mChats != null) {
            return mChats.size();
        }
        return 0;
    }


    @Override
    public int getItemViewType(int position)
    {
        if (TextUtils.equals(mChats.get(position).getSenderUid(),
                FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return VIEW_TYPE_ME;
        } else {
            return VIEW_TYPE_OTHER;
        }
    }


    private static class MyChatViewHolder extends RecyclerView.ViewHolder {
        private TextView txtChatMessage, txtUserAlphabet;

        public MyChatViewHolder(View itemView) {
            super(itemView);
            txtChatMessage = (TextView) itemView.findViewById(R.id.text_view_chat_message);
            txtUserAlphabet = (TextView) itemView.findViewById(R.id.text_view_user_alphabet);
        }
    }

    private static class OtherChatViewHolder extends RecyclerView.ViewHolder {
        private TextView txtChatMessage, txtUserAlphabet;

        public OtherChatViewHolder(View itemView) {
            super(itemView);
            txtChatMessage = (TextView) itemView.findViewById(R.id.text_view_chat_message);
            txtUserAlphabet = (TextView) itemView.findViewById(R.id.text_view_user_alphabet);
        }
    }
}
