package com.group01.plantique.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group01.plantique.R;
import com.group01.plantique.model.ModelReview;
import com.group01.plantique.model.User;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.view.View;

public class AdapterReview extends RecyclerView.Adapter<AdapterReview.HolderReview> {
    private final Context context;
    private final ArrayList<ModelReview> reviewArrayList;

    public AdapterReview(Context context, ArrayList<ModelReview> reviewArrayList) {
        this.context = context;
        this.reviewArrayList = reviewArrayList;
    }

    @NonNull
    @Override
    public HolderReview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row review
        View view = LayoutInflater.from(context).inflate(R.layout.row_review,parent,false);
        return new HolderReview(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderReview holder, int position) {
        //get data at position
        ModelReview modelReview = reviewArrayList.get(position);
        String uid = modelReview.getUid();
        String ratings = modelReview.getRatings();
        String timestamp = modelReview.getTimestamp();
        String review = modelReview.getReview();

        //need info of user who wrote the review: profile image, name, do this using uid of us
        loadUserDetail(modelReview,holder);


        //convert timestamp to proper format
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = dateFormat.format(calendar.getTime());


        //set data
        holder.reviewTv.setText(review);
        holder.ratingBar.setRating(Float.parseFloat(ratings));
        holder.dateTv.setText(formattedDate);

    }

    private void loadUserDetail(ModelReview modelReview, HolderReview holder) {
        //uid of user who wrote review
        String uid = modelReview.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get user in4, use same keyname as in firebase
                        String name = ""+snapshot.child("name").getValue();
                        String profileImage = ""+snapshot.child("profileImage").getValue();


                        //set data
                        holder.nameTv.setText(name);
                        try {
                            Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_gray).into(holder.profileLIv);

                        }
                        catch (Exception e) {
                            //if anything goes wrong setting image. set defaut image
                            holder.profileLIv.setImageResource(R.drawable.ic_person_gray);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return reviewArrayList.size(); //return list size
    }

    //view holder class. holds/inits view of recyclerview
    class HolderReview extends RecyclerView.ViewHolder {
        //ui views of layout row review
        private ImageView profileLIv;
        private TextView nameTv, dateTv, reviewTv;
        private RatingBar ratingBar;

      public HolderReview(@NonNull View itemView) {
          super(itemView);

          //init views of row_review
          profileLIv = itemView.findViewById(R.id.profileIv);
          nameTv = itemView.findViewById(R.id.nameTv);
          ratingBar = itemView.findViewById(R.id.ratingBar);
          dateTv = itemView.findViewById(R.id.dateTv);
          reviewTv = itemView.findViewById(R.id.reviewTv);
      }
    }
}
