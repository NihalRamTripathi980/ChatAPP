package com.example.nrtchatsapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nrtchatsapp.Activities.MainActivity;
import com.example.nrtchatsapp.Activities.ViewStory;
import com.example.nrtchatsapp.Modelss.Status;
import com.example.nrtchatsapp.Modelss.UserStatus;
import com.example.nrtchatsapp.R;
import com.example.nrtchatsapp.databinding.ItemStatusBinding;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

//import omari.hamza.storyview.StoryView;
//import omari.hamza.storyview.callback.StoryClickListeners;
//import omari.hamza.storyview.model.MyStory;

public class TopStatusesAdapter extends RecyclerView.Adapter<TopStatusesAdapter.statusViewHolder> {

    public Context context;
    public ArrayList<UserStatus> userStatuses;

    public TopStatusesAdapter(Context context, ArrayList<UserStatus> userStatuses) {
        this.context = context;
        this.userStatuses = userStatuses;
    }

    @NonNull
    @Override
    public statusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_status,parent,false);
        return new statusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull statusViewHolder holder, int position) {

        UserStatus userStatus = userStatuses.get(position);

//

       // Status lastStatus = userStatus.getStatuses().get(userStatus.getStatuses().size() - 1);
       // Glide.with(context).load(lastStatus.getImageUrl()).into(holder.binding.image);
      //  holder.binding.circularStatusView.setPortionsCount(userStatus.getStatuses().size());



        holder.binding.circularStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<MyStory> myStories = new ArrayList<>();



                for (Status status : userStatus.getStatuses()) {
                    myStories.add(new MyStory(status.getImageUrl()));


                }

                    new StoryView.Builder(((MainActivity) context).getSupportFragmentManager())
                            .setStoriesList(myStories) // Required
                            .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                            .setTitleText(userStatus.getName()) // Default is Hidden
                            .setSubtitleText("") // Default is Hidden
                            .setTitleLogoUrl(userStatus.getProfileImage()) // Default is Hidden
                            .setStoryClickListeners(new StoryClickListeners() {
                                @Override
                                public void onDescriptionClickListener(int position) {
                                    //your action
                                }

                                @Override
                                public void onTitleIconClickListener(int position) {
                                    //your action
                                }
                            }) // Optional Listeners
                            .build() // Must be called before calling show method
                            .show();




            }


        });


    }

    @Override
    public int getItemCount() {
        return userStatuses.size();
    }

    public  class statusViewHolder  extends RecyclerView.ViewHolder{

         ItemStatusBinding binding;
        public statusViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemStatusBinding.bind(itemView);
        }
    }
}
