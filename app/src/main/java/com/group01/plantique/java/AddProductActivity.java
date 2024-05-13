package com.group01.plantique.java;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.group01.plantique.R;
import com.group01.plantique.model.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddProductActivity extends AppCompatActivity {
    private ImageButton backBtn;
    private ImageView productIconIv;
    private EditText titleEt, descriptionEt, stockEt, unitEt, priceEt, discountedPriceEt, discountedNoteEt;
    private TextView categoryTv;
    private Switch discountSwitch;
    private Button addProductBtn;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE=500;
    private String[] cameraPermissions;
    private String[] storagePermissions;
    private Uri imageUri;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference categoriesRef;

    private final ActivityResultLauncher<String> getContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    productIconIv.setImageURI(uri);
                    imageUri = uri;
                }
            });

    private final ActivityResultLauncher<Uri> takePicture = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            isSuccess -> {
                if (isSuccess && imageUri != null) {
                    productIconIv.setImageURI(imageUri);
                }
            });
    private Uri createImageUri() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "New Product Image");
        return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        // Khởi tạo FirebaseAuth và ProgressDialog
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        addViews();

        categoriesRef = FirebaseDatabase.getInstance().getReference("categories");

        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<Category> categories = new ArrayList<>();
                    for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                        String cateId = categorySnapshot.child("cateId").getValue(String.class);
                        String cateName = categorySnapshot.child("cateName").getValue(String.class);
                        String imageurl = categorySnapshot.child("imageurl").getValue(String.class);

                        if (cateId != null && cateName != null && imageurl != null) {
                            Category category = new Category(cateId, cateName, imageurl);
                            categories.add(category);
                        }
                    }

                    Constants.updateCategories(categories);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AddProductActivity", "Failed to read categories: " + error.getMessage());
            }
        });


        discountedPriceEt.setVisibility(View.GONE);
        discountedNoteEt.setVisibility(View.GONE);

        progressDialog.setTitle(getString(R.string.progress_dialog_title));
        progressDialog.setCanceledOnTouchOutside(false);

        // Khởi tạo các mảng quyền
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        discountSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    discountedPriceEt.setVisibility(View.GONE);
                    discountedNoteEt.setVisibility(View.VISIBLE);
                }
                else {
                    discountedPriceEt.setVisibility(View.GONE);
                    discountedNoteEt.setVisibility(View.GONE);
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        productIconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });

        categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog();
            }
        });

        addProductBtn.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });
    }
    private  String productName, description, categoryId, cateName, stock, unit, price, discountNote, discount_price;
    private boolean discountAvailable = false;
    private void inputData() {
        productName = titleEt.getText().toString().trim();
        description = descriptionEt.getText().toString().trim();
        cateName = categoryTv.getText().toString().trim();
        stock = stockEt.getText().toString().trim();
        price = priceEt.getText().toString().trim();
        discountAvailable = discountSwitch.isChecked();
        unit=unitEt.getText().toString().trim();

        if (TextUtils.isEmpty(productName)) {
            Toast.makeText(this, R.string.title_required, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(cateName)) {
            Toast.makeText(this, R.string.category_required, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(price)) {
            Toast.makeText(this, R.string.price_required, Toast.LENGTH_SHORT).show();
            return;
        }
        if (discountAvailable) {
            discountNote = discountedNoteEt.getText().toString().trim();
            String discount = discountedNoteEt.getText().toString().trim();
            if (!discountNote.isEmpty()) {
                // Xác định discountRate từ discountNote
                double discountRate = Double.parseDouble(discount.replace("%", "").trim()) / 100.0;


                // Chuyển đổi chuỗi price sang kiểu double
                double productPrice = Double.parseDouble(price);

                // Tính discount_price dựa trên discountRate
                double discountedPrice = productPrice * (1 - discountRate);

                // Cập nhật discount_price
                discount_price = String.valueOf(discountedPrice);
                Log.d("DiscountNoteValue", "Discount Note: " + discountNote);
            } else {
                // Nếu discountNote rỗng, giảm giá và discount_price = 0
                discount_price = "0";
            }
        } else {
            // Nếu không có giảm giá, discount_price = 0
            discount_price = "0";
            discountNote = "";

        }
        addProduct();
    }
    private int productIdCounter = 0;
    private void addProduct() {
        progressDialog.setMessage(getString(R.string.adding_product));
        progressDialog.show();

        String timestamp = String.valueOf(System.currentTimeMillis());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("products");

        // Thực hiện truy vấn để đọc productId cuối cùng trên Firebase
        reference.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        // Lấy productId cuối cùng
                        String lastProductId = dataSnapshot.child("productId").getValue(String.class);

                        // Xác định số cuối cùng trong productId cuối cùng
                        String lastNumber = lastProductId.substring(lastProductId.lastIndexOf("_") + 1);

                        // Tăng số cuối cùng để tạo newProductId
                        int lastNumInt = Integer.parseInt(lastNumber);
                        int newProductIdNum = lastNumInt + 1;

                        // Tạo newProductId mới
                        String newProductId = "ProductID_" + String.format("%03d", newProductIdNum);

                        // Tiếp tục các bước thêm sản phẩm với newProductId
                        // Tìm categoryId từ cateName
                        String categoryId = findCategoryIdFromName(cateName);

                        if (categoryId == null) {
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this, R.string.category_not_found, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        double priceValue = Double.parseDouble(price);
                        int stockValue = Integer.parseInt(stock);
                        double discountPriceValue = discountAvailable ? Double.parseDouble(discount_price) : 0;


                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("productId", newProductId);
                        hashMap.put("productName", productName);
                        hashMap.put("description", description);
                        hashMap.put("categoryId", categoryId);
                        hashMap.put("stock", stockValue);
                        hashMap.put("unit", unit);
                        hashMap.put("imageurl", "");
                        hashMap.put("price", priceValue);
                        hashMap.put("discount_price", discountPriceValue);
                        hashMap.put("discountNote", discountNote);

                        reference.child(newProductId).setValue(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        if (imageUri != null) {
                                            // Upload hình ảnh lên Storage
                                            uploadImage(newProductId, timestamp);
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(AddProductActivity.this, R.string.product_added, Toast.LENGTH_SHORT).show();
                                            clearData();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        String errorMessage = getString(R.string.failed_to_add_product, e.getMessage());
                                        Toast.makeText(AddProductActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } else {
                    // Không có dữ liệu sản phẩm trên Firebase, tạo productId đầu tiên
                    String newProductId = "ProductID_001";

                    // Tiếp tục các bước thêm sản phẩm với newProductId
                    // Tìm categoryId từ cateName
                    String categoryId = findCategoryIdFromName(cateName);

                    if (categoryId == null) {
                        progressDialog.dismiss();
                        Toast.makeText(AddProductActivity.this, getString(R.string.category_not_found), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("productId", newProductId);
                    hashMap.put("productName", productName);
                    hashMap.put("description", description);
                    hashMap.put("categoryId", categoryId);
                    hashMap.put("stock", stock);
                    hashMap.put("unit", unit);
                    hashMap.put("imageurl", ""); // Chưa có URL hình ảnh, sẽ được cập nhật sau khi upload lên Storage
                    hashMap.put("price", price);
                    hashMap.put("discount_price", discount_price);
                    hashMap.put("discountNote", discountNote);

                    reference.child(newProductId).setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    if (imageUri != null) {
                                        // Upload hình ảnh lên Storage
                                        uploadImage(newProductId, timestamp);
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(AddProductActivity.this, getString(R.string.product_added), Toast.LENGTH_SHORT).show();
                                        clearData();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    String errorMessage = getString(R.string.failed_to_add_product, e.getMessage());
                                    Toast.makeText(AddProductActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                String errorMessage = getString(R.string.error_message, error.getMessage());
                Toast.makeText(AddProductActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

            }
        });
    }


    // Phương thức tìm categoryId từ cateName
    private @Nullable String findCategoryIdFromName(String cateName) {
        for (Category category : Constants.productCategories) {
            if (category.getCateName().equals(cateName)) {
                return category.getCateId();
            }
        }
        return null; // Trả về null nếu không tìm thấy categoryId
    }



    // Phương thức upload hình ảnh lên Storage
    private void uploadImage(String productId, String timestamp) {
        String filePathAndName = "product_images/" + productId;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);

        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Tải lên thành công, lấy URL tải xuống
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Lấy URL thành công
                                String downloadImageUri = uri.toString();
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("products")
                                        .child(productId);

                                // Cập nhật URL hình ảnh vào database
                                reference.child("imageurl").setValue(downloadImageUri)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(AddProductActivity.this, getString(R.string.product_added), Toast.LENGTH_SHORT).show();
                                                clearData();
                                                productIdCounter++;
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                String errorMessage = getString(R.string.error_message) + e.getMessage();
                                                Toast.makeText(AddProductActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddProductActivity.this, getString(R.string.product_added_failed), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void clearData(){
        titleEt.setText("");
        descriptionEt.setText("");
        categoryTv.setText("");
        stockEt.setText("");
        unitEt.setText("");
        priceEt.setText("");
        discountedPriceEt.setText("");
        discountedNoteEt.setText("");
        productIconIv.setImageResource(R.drawable.ic_shopping_cart);
        imageUri=null;
        discountNote = "";
    }
    private void categoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_title_product_category));


        // Kiểm tra xem danh sách productCategories có dữ liệu không
        if (Constants.productCategories != null && !Constants.productCategories.isEmpty()) {
            // Lấy danh sách các cateName từ danh sách các đối tượng Category
            List<String> cateNamesList = new ArrayList<>();
            for (Category category : Constants.productCategories) {
                cateNamesList.add(category.getCateName());
            }

            // Chuyển danh sách cateName thành mảng String
            String[] categoriesArray = cateNamesList.toArray(new String[0]);

            // Kiểm tra xem danh sách categoriesArray có dữ liệu không
            if (categoriesArray.length > 0) {
                builder.setItems(categoriesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Lấy đối tượng Category từ danh sách productCategories
                        Category selectedCategory = Constants.productCategories.get(which);
                        String categoryName = selectedCategory.getCateName();
                        categoryTv.setText(categoryName);
                    }
                }).show();
            } else {
                Toast.makeText(this, getString(R.string.no_categories_found), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.categories_data_empty), Toast.LENGTH_SHORT).show();
        }
    }




    private void showImagePickDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image Source")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // Camera
                        imageUri = createImageUri(); // Ensure this is created each time for new images
                        takePicture.launch(imageUri);
                    } else {
                        // Gallery
                        getContent.launch("image/*");
                    }
                })
                .show();
    }
    private void pickFromGallery() {
        // Intent để chọn hình từ thư viện
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image_Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image_Description");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickFromCamera();
            } else {
                Toast.makeText(this, getString(R.string.permission_required_message), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickFromGallery();
            } else {
                Toast.makeText(this, getString(R.string.storage_permission_required_message), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                imageUri = data.getData();
                productIconIv.setImageURI(imageUri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                productIconIv.setImageURI(imageUri);
            }
        }
    }

    private void addViews() {
        backBtn = findViewById(R.id.backBtn);
        productIconIv = findViewById(R.id.productIconIv);
        titleEt = findViewById(R.id.promoCodeEt);
        descriptionEt = findViewById(R.id.promoDescriptionEt);
        categoryTv = findViewById(R.id.categoryTv);
        stockEt = findViewById(R.id.stockEt);
        unitEt = findViewById(R.id.promoPriceEt);
        priceEt = findViewById(R.id.priceEt);
        discountedNoteEt = findViewById(R.id.discountedNoteEt);
        discountedPriceEt = findViewById(R.id.discountedPriceEt);
        discountSwitch = findViewById(R.id.discountSwitch);
        addProductBtn=findViewById(R.id.edtProductBtn);
    }
}