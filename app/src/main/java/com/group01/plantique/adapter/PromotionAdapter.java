package com.group01.plantique.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group01.plantique.R;
import com.group01.plantique.java.AddPromotionCodeActivity;
import com.group01.plantique.model.Promotion;

import java.util.ArrayList;

import io.reactivex.rxjava3.annotations.NonNull;

public class PromotionAdapter extends RecyclerView.Adapter<PromotionAdapter.HolderPromotion>{
    private Context context;
    private ArrayList <Promotion> promotionArrayList;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    public PromotionAdapter(Context context, ArrayList<Promotion> promotionArrayList) {
        this.context = context;
        this.promotionArrayList = promotionArrayList;

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(context.getString(R.string.please_wait_title));
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public HolderPromotion onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.item_promotion_code, parent, false);
        return new HolderPromotion(view);
    }
    @Override
    public  void onBindViewHolder (@NonNull HolderPromotion holder, int position) {
        Promotion promotion = promotionArrayList.get(position);
        String[] promoOptionsArray = context.getResources().getStringArray(R.array.promo_options);
        // Lấy các chuỗi từ mảng chuỗi promoOptionsArray
        String editString = promoOptionsArray[0];
        String deleteString = promoOptionsArray[1];
        String id = promotion.getId();
        String description = promotion.getDescription();
        String promoCode = promotion.getPromoCode();
        String promoPrice = promotion.getPromoPrice();
        String expireDate = promotion.getExpireDate();
        String minimumOrderPrice = promotion.getMinimumOrderPrice();

        holder.descriptionTv.setText(description);
        holder.promoPriceTv.setText(promoPrice);
        holder.minimumOrderPriceTv.setText(minimumOrderPrice);
        holder.promoCodeTv.setText(context.getString(R.string.code_label, promoCode));
        holder.expireDateTv.setText(context.getString(R.string.expire_date_label, expireDate));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDeleteDialog(promotion, holder, editString, deleteString);
            }
        });
    }

    private void editDeleteDialog(Promotion promotion, HolderPromotion holder, String editString, String deleteString) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.choose_option_title))
                .setItems(new CharSequence[]{editString, deleteString}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (i == 0) {
                            editPromoCode(promotion);
                        } else if (i == 1) {
                            deletePromoCode(promotion);
                        }
                    }
                })
                .show();
    }

    private void deletePromoCode(Promotion promotion) {
        progressDialog.setMessage(context.getString(R.string.deleting_promotion));
        progressDialog.show();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("promotions");
        reference.child(promotion.getId())
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(context, context.getString(R.string.delete_success_message), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(context, context.getString(R.string.delete_fail_message) + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void editPromoCode(Promotion promotion) {
        Intent intent = new Intent(context, AddPromotionCodeActivity.class);
        intent.putExtra("promoId", promotion.getId());
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return promotionArrayList.size();
    }
    class HolderPromotion extends RecyclerView.ViewHolder{
        private ImageView iconPromo;
        private TextView promoCodeTv, promoPriceTv, minimumOrderPriceTv, expireDateTv, descriptionTv;

        public HolderPromotion (@NonNull View itemView) {
            super(itemView);

            iconPromo = itemView.findViewById(R.id.iconPromo);
            promoCodeTv = itemView.findViewById(R.id.promoCodeTv);
            promoPriceTv = itemView.findViewById(R.id.promoPriceTv);
            minimumOrderPriceTv = itemView.findViewById(R.id.minimumOrderPriceTv);
            expireDateTv = itemView.findViewById(R.id.expireDateTv);
            descriptionTv = itemView.findViewById(R.id.promoDescriptionTv);
        }

    }
}
