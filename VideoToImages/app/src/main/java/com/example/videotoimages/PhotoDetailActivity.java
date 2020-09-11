package com.example.videotoimages;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.URIResolver;

public class PhotoDetailActivity extends AppCompatActivity {
    ImageView imageView, icInfo, icDelete, icShare, icEdit, icCrop, icText, icSticker, icPaint;
    String path;
    final static String SCREENSHOTS_LOCATIONS = Environment.getExternalStorageDirectory().toString() + "/screenshots/";
    LinearLayout layoutToEdit, layoutFunction;
    private Uri mCropImageUri;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        setTitle("Images");
        imageView = findViewById(R.id.imgPhoto);
        icInfo = findViewById(R.id.icInfo);
        icDelete = findViewById(R.id.icDelete);
        icShare = findViewById(R.id.icShare);
        icEdit = findViewById(R.id.icEdit);
        icCrop = findViewById(R.id.icCrop);
        icText = findViewById(R.id.icText);
        icSticker = findViewById(R.id.icSticker);
        icPaint = findViewById(R.id.icPaint);
        layoutToEdit = findViewById(R.id.layoutToEdit);
        layoutFunction = findViewById(R.id.layoutFunction);

        androidx.appcompat.widget.Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        final Bundle b = intent.getBundleExtra("bundlePhoto");
        path = b.getString("path");
        Glide.with(this).load(b.getString("path")).into(imageView);

        icInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PhotoDetailActivity.this);
                View view = LayoutInflater.from(PhotoDetailActivity.this).inflate(R.layout.layout_photo_info, null);
                TextView tvPath = view.findViewById(R.id.tvPath);
                TextView tvSize = view.findViewById(R.id.tvSize);
                builder.setTitle("Thông tin ảnh");
                tvPath.setText("Vị trí: " + b.getString("path"));
                tvSize.setText("Kích thước: " + b.getInt("size") / 1024 + "KB");
                builder.setView(view);
                builder.show();
            }
        });
        icDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog diaBox = AskOption();
                diaBox.show();
            }
        });
        icShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Test");
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });
        icEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutFunction.setVisibility(View.INVISIBLE);
                layoutToEdit.setVisibility(View.VISIBLE);
                icCrop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layoutToEdit.setVisibility(View.INVISIBLE);
                        startCropImageActivity(Uri.fromFile(new File(path)));
                    }
                });
            }
        });
    }

    private AlertDialog AskOption() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Do you want to Delete")
                .setIcon(R.drawable.ic_baseline_delete_24)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        File fdelete = new File(path);
                        if (fdelete.exists()) {
                            if (fdelete.delete()) {
                                Toast.makeText(getApplicationContext(), "Successfully Delete", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Fail to delete", Toast.LENGTH_SHORT).show();
                            }
                        }
                        startActivity(new Intent(PhotoDetailActivity.this, GalleryActivity.class));
                        dialog.dismiss();
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return alertDialog;
    }

    @Override
    @SuppressLint("NewApi")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                ((ImageView) findViewById(R.id.imgPhoto)).setImageURI(result.getUri());
                File file = new File(path);
                file.delete();
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(path);
                    if (fileOutputStream != null) {
                        if (result.getBitmap().compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream)) {
                            Toast.makeText(PhotoDetailActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PhotoDetailActivity.this, "Đã lưu", Toast.LENGTH_SHORT).show();
                        }
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Toast.makeText(this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(),GalleryActivity.class));

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCropImageActivity(mCropImageUri);
        } else {
            Toast.makeText(this, "Ko có quyền truy cập", Toast.LENGTH_LONG).show();
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }
}