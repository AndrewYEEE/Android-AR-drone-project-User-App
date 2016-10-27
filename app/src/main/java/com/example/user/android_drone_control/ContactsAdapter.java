package com.example.user.android_drone_control;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by user on 2016/9/8.
 */
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    /*
        處理 click 是 RecyclerView 比  ListView 麻煩的地方，
        因為 RecyclerView 沒有如 ListView.setItemClickListener() 一樣的 method，
        要處理 click event 變得很自由，也很麻煩。
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView nameTextView;
        public MyViewHolderClick mListener;
        public ViewHolder(View itemView){
            super(itemView);

           // nameTextView = (TextView) itemView.findViewById(R.id.tv_name);
        }
        //===========觸發用================
        @Override
        public void onClick(View v) {
            mListener.clickOnView(v, getLayoutPosition());
        }

        public interface MyViewHolderClick {
            void clickOnView(View v, int position);
        }
    }



    private List<Contact> mContacts;

    public ContactsAdapter(List<Contact> contacts){
        mContacts = contacts;
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    /*
    onCreateViewHolder() 和 onBindViewHolder()。前者建立 view，
    並將 view 轉成 ViewHolder，後者將 Contact 顯示在 view 中。
    以往在 ArrayAdapter 的 getview()，
    現在 RecyclerView 中拆散為 onCreateViewHolder() 和 onBindViewHolder()。
    建立 view 和 更新 view 的動作分為兩個 methods ，code 變得更易讀。*/

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        View contactView = LayoutInflater.from(context).inflate(R.layout.item_contact, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(contactView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Contact contact = mContacts.get(position);
        TextView nameTextView = viewHolder.nameTextView;
        nameTextView.setText(contact.getName());
    }
}
