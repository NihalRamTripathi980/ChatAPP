package com.example.nrtchatsapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nrtchatsapp.Activities.ChatsActivity;
import com.example.nrtchatsapp.R;
import com.example.nrtchatsapp.databinding.RowChatslistBinding;
import com.example.nrtchatsapp.Modelss.users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class userAdapter extends RecyclerView.Adapter<userAdapter.userViewHolder> {
   Context context;
   ArrayList<com.example.nrtchatsapp.Modelss.users> users ;
    public  userAdapter(Context context,ArrayList<users> users){
        this.context =context;
        this.users=users;

    }

    @Override
    public userViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_chatslist,parent,false);
        return new userViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  userAdapter.userViewHolder holder, int position) {
users user = users.get(position);

String senderId = FirebaseAuth.getInstance().getUid();

String senderRoom = senderId + user.getUid();

        FirebaseDatabase.getInstance().getReference().child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                            Long time = snapshot.child("lastMsgTime").getValue(Long.class);
                            holder.binding.lastConversation.setText(lastMsg);
                        }
                        else {
                            holder.binding.lastConversation.setText("Tap to Chat");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


holder.binding.userName.setText(user.getName());
        Glide.with(context).load(user.getProfilePic())
                .placeholder(R.drawable.profile_pic)
                .into(holder.binding.userProfile);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatsActivity.class);
                intent.putExtra("name",user.getName());
                intent.putExtra("uid",user.getUid());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public  class userViewHolder extends RecyclerView.ViewHolder{
RowChatslistBinding binding;
        public userViewHolder(@NonNull  View itemView) {

            super(itemView);
            binding=RowChatslistBinding.bind(itemView);
        }
    }
}
