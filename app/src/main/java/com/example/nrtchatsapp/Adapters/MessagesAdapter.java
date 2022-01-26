package com.example.nrtchatsapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nrtchatsapp.Modelss.Messages;
import com.example.nrtchatsapp.R;
import com.example.nrtchatsapp.databinding.MsgReceiveBinding;
import com.example.nrtchatsapp.databinding.MsgSentBinding;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class MessagesAdapter  extends RecyclerView.Adapter{

    Context context;
    ArrayList<Messages> messages;

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage ;

    final  int MSG_SENT = 1;
    final int MSG_RECEIVE = 2;

     String senderRoom ;
     String receiverRoom;

    public MessagesAdapter(Context context, ArrayList<Messages> messages,String senderRoom, String receiverRoom){
        this.context=context;
        this.messages=messages;
        this.senderRoom=senderRoom;
        this.receiverRoom=receiverRoom;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType== MSG_SENT){
            View view = LayoutInflater.from(context).inflate(R.layout.msg_sent,parent,false);
            return  new SentViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.msg_receive,parent,false);
            return  new ReceiveViewHolder(view);
        }

    }

    @Override
    public int getItemViewType(int position) {

        Messages message = messages.get(position);
        if (FirebaseAuth.getInstance().getUid().equals(message.getSenderId())){
            return  MSG_SENT;
        }
        else {
            return  MSG_RECEIVE;
        }

    }




    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Messages message = messages.get(position);




       int[] reactions =  new int[]{
               R.drawable.ic_fb_like,
               R.drawable.ic_fb_love,
               R.drawable.ic_fb_laugh,
               R.drawable.ic_fb_wow,
               R.drawable.ic_fb_sad,
               R.drawable.ic_fb_angry};



        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();


        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {

            if (pos < 0)
                return false;

            if (holder.getClass()==SentViewHolder.class){
                SentViewHolder viewHolder =(SentViewHolder)holder ;
                viewHolder.binding.feelings.setImageResource(reactions[pos]);
                viewHolder.binding.feelings.setVisibility(View.VISIBLE);


            }
            else{
                ReceiveViewHolder viewHolder =(ReceiveViewHolder) holder;
                viewHolder.binding.feelings.setImageResource(reactions[pos]);
                viewHolder.binding.feelings.setVisibility(View.VISIBLE);
            }


    message.setFeeling(pos);

                FirebaseDatabase.getInstance().getReference()
                        .child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(message.getMessageId()).setValue(message);



                FirebaseDatabase.getInstance().getReference()
                        .child("chats")
                        .child(receiverRoom)
                        .child("messages")
                        .child(message.getMessageId()).setValue(message);




            return true;
        });

        if (holder.getClass()==SentViewHolder.class){
            SentViewHolder viewHolder= (SentViewHolder) holder;
            viewHolder.binding.message.setText(message.getMessage());

            if (message.getFeeling() >= 0){
               viewHolder.binding.feelings.setImageResource(reactions[ message.getFeeling()]);
                viewHolder.binding.feelings.setVisibility(View.VISIBLE);
            }
            else {
                viewHolder.binding.feelings.setVisibility(View.GONE);
            }


            viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {


                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    popup.onTouch(v,event);

                    return false;
                }
            });
        }

        else {
            ReceiveViewHolder viewHolder =(ReceiveViewHolder) holder;
            viewHolder.binding.message.setText(message.getMessage());

            if (message.getFeeling()>=0){
                viewHolder.binding.feelings.setImageResource(reactions[ message.getFeeling()]);
                viewHolder.binding.feelings.setVisibility(View.VISIBLE);
            }
            else {
                viewHolder.binding.feelings.setVisibility(View.GONE);
            }

            viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    popup.onTouch(v,event);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class  SentViewHolder extends RecyclerView.ViewHolder{

        MsgSentBinding binding ;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = MsgSentBinding.bind(itemView);
        }
    }

    public  class  ReceiveViewHolder extends  RecyclerView.ViewHolder{

        MsgReceiveBinding binding ;
        public ReceiveViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=MsgReceiveBinding.bind(itemView);
        }
    }
}
