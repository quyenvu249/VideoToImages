package com.example.videotoimages.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ImageEditorIntentBuilder;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import yuku.ambilwarna.AmbilWarnaDialog;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.videotoimages.FileUtils;
import com.example.videotoimages.R;
import com.example.videotoimages.adapter.EmojiAdapter;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class PhotoDetailActivity extends AppCompatActivity {
    PhotoEditorView imageView;
    ImageView icInfo, icDelete, icShare, icEdit, icColor, icEraser, icPickColor, icBrush, icPre;
    ImageView icCrop, icText, icSticker, icPaint, icBack, icUndo, icSave, icRedo;
    SeekBar sbBrushSize, sbBrushOpacity;
    String path;
    ConstraintLayout layoutDo;
    LinearLayout layoutFunction, layoutToEdit, layoutToPaint, layoutText;
    RelativeLayout rlImage;
    FragmentManager fragmentManager;
    EditText edText;
    PhotoEditor mPhotoEditor;
    Typeface mEmojiTypeFace;
    final int PIC_CROP = 1;
    ArrayList emoji;
    private final int PHOTO_EDITOR_REQUEST_CODE = 231;
    public static final int ACTION_REQUEST_EDITIMAGE = 9;
    String bitmap;
    Uri imgUri;


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
        rlImage = findViewById(R.id.rlImage);
        layoutFunction = findViewById(R.id.layoutFunction);
        layoutToEdit = findViewById(R.id.layoutToEdit);
        layoutText = findViewById(R.id.layoutText);
        layoutToPaint = findViewById(R.id.layoutToPaint);
        icColor = findViewById(R.id.icColor);
        icPickColor = findViewById(R.id.icPickColor);
        edText = findViewById(R.id.edText);
        icSave = findViewById(R.id.icSave);
        icEraser = findViewById(R.id.icEraser);
        icCrop = findViewById(R.id.icCrop);
        icSticker = findViewById(R.id.icSticker);
        icPaint = findViewById(R.id.icPaint);
        icText = findViewById(R.id.icText);
        sbBrushOpacity = findViewById(R.id.sbBrushOpacity);
        sbBrushSize = findViewById(R.id.sbBrushSize);
        icBack = findViewById(R.id.icBack);
        layoutDo = findViewById(R.id.layoutDo);
        icUndo = findViewById(R.id.icUndo);
        icRedo = findViewById(R.id.icRedo);
        icBrush = findViewById(R.id.icBrush);
        icPre = findViewById(R.id.icPre);

        fragmentManager = getSupportFragmentManager();

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
        imgUri=Uri.parse(path);
        imageView.getSource().setImageBitmap(BitmapFactory.decodeFile(path));

        // Typeface mTextRobotoTf = ResourcesCompat.getFont(this, R.font.roboto_medium);

        mEmojiTypeFace = Typeface.createFromAsset(getAssets(), "emojione-android.ttf");

        mPhotoEditor = new PhotoEditor.Builder(this, imageView)
                .setPinchTextScalable(true)
                //.setDefaultTextTypeface(mTextRobotoTf)
                .setDefaultEmojiTypeface(mEmojiTypeFace)
                .build();

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
                Bitmap icon = BitmapFactory.decodeFile(path);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/png");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                File f = new File(path);
                try {
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("ABC",e.getMessage());
                }
                share.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
                startActivity(Intent.createChooser(share, "Share Image"));
            }
        });
        icEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTitle("Edit Images");
                layoutFunction.setVisibility(View.INVISIBLE);
                layoutToEdit.setVisibility(View.VISIBLE);
                icSave.setVisibility(View.VISIBLE);
                icPaint.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layoutDo.setVisibility(View.VISIBLE);
                        layoutToEdit.setVisibility(View.INVISIBLE);
                        layoutToPaint.setVisibility(View.VISIBLE);
                        mPhotoEditor.setBrushDrawingMode(true);
                        mPhotoEditor.setBrushSize(10);
                        sbBrushSize.setMax(100);
                        sbBrushSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                seekBar.setProgress(progress);
                                mPhotoEditor.setBrushSize(progress);
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        });
                        sbBrushOpacity.setMax(100);
                        sbBrushOpacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                seekBar.setProgress(progress);
                                mPhotoEditor.setOpacity(progress);
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        });
                        icColor.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AmbilWarnaDialog dialog = new AmbilWarnaDialog(PhotoDetailActivity.this, R.color.colorPrimary, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                                    @Override
                                    public void onOk(AmbilWarnaDialog dialog, int color) {
                                        mPhotoEditor.setBrushColor(color);
                                    }

                                    @Override
                                    public void onCancel(AmbilWarnaDialog dialog) {
                                    }

                                });
                                dialog.show();
                            }

                        });
                        icEraser.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPhotoEditor.brushEraser();
                            }
                        });
                        icBrush.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPhotoEditor.setBrushDrawingMode(true);
                                mPhotoEditor.setBrushSize(10);
                            }
                        });
                        icUndo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPhotoEditor.undo();
                            }
                        });
                        icRedo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPhotoEditor.redo();
                            }
                        });
                    }
                });
                icSticker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layoutDo.setVisibility(View.VISIBLE);
                        addSticker();
                        icUndo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPhotoEditor.undo();
                            }
                        });
                        icRedo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPhotoEditor.redo();
                            }
                        });
                    }
                });
                icText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layoutDo.setVisibility(View.VISIBLE);
                        layoutText.setVisibility(View.VISIBLE);
                        layoutToEdit.setVisibility(View.INVISIBLE);
                        icPickColor.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AmbilWarnaDialog dialog = new AmbilWarnaDialog(PhotoDetailActivity.this, R.color.colorPrimary, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                                    @Override
                                    public void onOk(AmbilWarnaDialog dialog, int color) {
                                        edText.setTextColor(color);
                                        mPhotoEditor.addText(edText.getText().toString(), color);
                                    }

                                    @Override
                                    public void onCancel(AmbilWarnaDialog dialog) {
                                    }

                                });
                                dialog.show();
                            }
                        });
                        icUndo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPhotoEditor.undo();
                            }
                        });
                        icRedo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPhotoEditor.redo();
                            }
                        });
                    }
                });
                icSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ActivityCompat.checkSelfPermission(PhotoDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        mPhotoEditor.saveAsFile(path, new PhotoEditor.OnSaveListener() {
                            @Override
                            public void onSuccess(@NonNull String imagePath) {
                                Toast.makeText(PhotoDetailActivity.this, "Save Image Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(PhotoDetailActivity.this, GalleryActivity.class));
                            }

                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Log.e("PhotoEditor", "Failed to save Image");
                            }
                        });
                    }
                });
                icCrop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layoutToEdit.setVisibility(View.INVISIBLE);
                        try {
                            Intent intent = new ImageEditorIntentBuilder(PhotoDetailActivity.this, path, path)
                                    .withCropFeature()
                                    .forcePortrait(true)
                                    .setSupportActionBarVisibility(false)
                                    .build();

                            EditImageActivity.start(PhotoDetailActivity.this, intent, PHOTO_EDITOR_REQUEST_CODE);
                        } catch (Exception e) {
                            Log.e("Demo App", e.getMessage());
                        }

                    }
                });
                icBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layoutFunction.setVisibility(View.VISIBLE);
                        layoutToEdit.setVisibility(View.INVISIBLE);
                        icSave.setVisibility(View.INVISIBLE);
                    }
                });
                icPre.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layoutText.setVisibility(View.INVISIBLE);
                        layoutToPaint.setVisibility(View.INVISIBLE);
                        layoutToEdit.setVisibility(View.VISIBLE);
                        icSave.setVisibility(View.VISIBLE);
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

    private void addSticker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PhotoDetailActivity.this);
        View view = LayoutInflater.from(PhotoDetailActivity.this).inflate(R.layout.fragment_emoji, null);
        emoji = PhotoEditor.getEmojis(PhotoDetailActivity.this);
        RecyclerView rcEmoji = view.findViewById(R.id.rvEmoji);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(PhotoDetailActivity.this, 5);
        rcEmoji.setLayoutManager(gridLayoutManager);
        EmojiAdapter emojiAdapter = new EmojiAdapter(PhotoDetailActivity.this, emoji);
        rcEmoji.setAdapter(emojiAdapter);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        emojiAdapter.setOnItemClickListener(new EmojiAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos, View v) {
                mPhotoEditor.addEmoji(mEmojiTypeFace, emoji.get(pos).toString());
            }
        });
        builder.setView(view);
        builder.show();
    }

}