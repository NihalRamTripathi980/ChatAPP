package com.example.nrtchatsapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.nrtchatsapp.Adapters.TopStatusesAdapter;
import com.example.nrtchatsapp.Adapters.userAdapter;
import com.example.nrtchatsapp.Modelss.Status;
import com.example.nrtchatsapp.Modelss.UserStatus;
import com.example.nrtchatsapp.R;
import com.example.nrtchatsapp.databinding.ActivityMainBinding;
import com.example.nrtchatsapp.Modelss.users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    FirebaseDatabase database;
    ArrayList<com.example.nrtchatsapp.Modelss.users> users;
    com.example.nrtchatsapp.Adapters.userAdapter userAdapter ;


    TopStatusesAdapter statusAdapter;
    ArrayList<UserStatus> userStatuses;
   ProgressDialog dialog;

   users user ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog= new ProgressDialog(this);
        dialog.setMessage("Uploading Image....");


        database=FirebaseDatabase.getInstance();
        users= new ArrayList<>();
        userAdapter = new userAdapter(this,users);
        binding.recyclerView.setAdapter(userAdapter);

        // For Status or story
        userStatuses = new ArrayList<>();
        statusAdapter = new TopStatusesAdapter(this,userStatuses);
        binding.statusListRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true));
        binding.statusListRecyclerView.setAdapter(statusAdapter);

        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user = snapshot.getValue(users.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    users user =snapshot1.getValue(users.class);
                    users.add(user);


                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.story:

                        Intent i = new Intent();
                        i.setType("image/*");
                        i.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(i,75);
                        break;
                }
                return false;
            }
        });

        // share here

        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    userStatuses.clear();
                    for (DataSnapshot storySnapshot : snapshot.getChildren()){
                        // **
                        UserStatus status = new UserStatus();
                        status.setName(storySnapshot.child("name").getValue(String.class));
                        status.setProfileImage(storySnapshot.child("ProfileImage").getValue(String.class));
                        status.setLastUpdated(storySnapshot.child("lastUpdated").getValue(Long.class));

                        ArrayList<Status>  statuses = new ArrayList<>();
                        for (DataSnapshot statusSnapshot : storySnapshot.child("statuses").getChildren()){

                            Status sampleStatus = statusSnapshot.getValue(Status.class);
                            statuses.add(sampleStatus);
                        }
                        status.setStatuses(statuses);
                        userStatuses.add(status);
                    }
                    statusAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null){
            if (data.getData() != null){
                dialog.show();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                Date date = new Date();
                StorageReference reference = storage.getReference().child("status").child(date.getTime() + "");

                reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                  dialog.dismiss();


                                  UserStatus userStatus = new UserStatus();
                                  userStatus.setName(user.getName());
                                  userStatus.setProfileImage(user.getProfilePic());
                                  userStatus.setLastUpdated(date.getTime());

                                    HashMap<String,Object> obj = new HashMap<>();
                                    obj.put("name",userStatus.getName());
                                    obj.put("ProfileImage",userStatus.getProfileImage());
                                    obj.put("lastUpdated",userStatus.getLastUpdated());

                                    String imageUrl = uri.toString();
                                    Status status = new Status(imageUrl, userStatus.getLastUpdated());
                                  database.getReference()
                                          .child("stories")
                                          .child(FirebaseAuth.getInstance().getUid())
                                          .updateChildren(obj);

                                  database.getReference().child("stories")
                                          .child(FirebaseAuth.getInstance().getUid())
                                          .push()
                                          .setValue(status);
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.search:
                Toast.makeText(this, "Search Clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.setting:
                Toast.makeText(this, "Setting clicked", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }
}