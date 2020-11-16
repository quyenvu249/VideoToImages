package com.example.videotoimages.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.videotoimages.R;
import com.example.videotoimages.activity.PhotoDetailActivity;
import com.example.videotoimages.adapter.CreatedPhotosAdapter;
import com.example.videotoimages.model.CreatedPhotos;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class PhotosGalleryFragment extends Fragment {
    ArrayList<CreatedPhotos> arrayList;
    GridView grPhotos;
    int count = 0;
    ArrayList<CreatedPhotos> arrSelected;
    CreatedPhotosAdapter createdPhotosAdapter;
    TextView textView;

    public PhotosGalleryFragment() {

    }

    public static PhotosGalleryFragment newInstance() {
        PhotosGalleryFragment fragment = new PhotosGalleryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos_gallery, container, false);
        grPhotos = (GridView) view.findViewById(R.id.grPhotos);
        arrayList = new ArrayList<>();
        arrSelected = new ArrayList<>();
        getAllPhotos();
        grPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), PhotoDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("bitmap", String.valueOf(arrayList.get(position).getBitmap()));
                bundle.putString("path", arrayList.get(position).getPhotoPath());
                bundle.putString("name", arrayList.get(position).getPhotoName());
                bundle.putInt("size", arrayList.get(position).getPhotoSize());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("bundlePhoto", bundle);
                startActivity(intent);
            }
        });
        return view;
    }

    private void getAllPhotos() {
        File photoFolder = new File(Environment.getExternalStorageDirectory() + "/screenshots/");
        if (photoFolder.exists()) {
            File[] allFiles = photoFolder.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png"));
                }
            });
            for (int i = 0; i < allFiles.length; i++) {
                CreatedPhotos createdPhotos = new CreatedPhotos();
                //BitmapFactory.decodeFile(allFiles[i].getAbsolutePath(), new BitmapFactory.Options()), allFiles[i].getName(), allFiles[i].getAbsolutePath(), Integer.parseInt(allFiles[i].length() + ""), false
                createdPhotos.setBitmap(BitmapFactory.decodeFile(allFiles[i].getAbsolutePath(), new BitmapFactory.Options()));
                createdPhotos.setPhotoName(allFiles[i].getName());
                createdPhotos.setPhotoPath(allFiles[i].getAbsolutePath());
                createdPhotos.setPhotoSize(Integer.parseInt(allFiles[i].length() + ""));
                arrayList.add(createdPhotos);
            }

        }
        createdPhotosAdapter = new CreatedPhotosAdapter(getActivity().getApplicationContext(), arrayList);
        grPhotos.setAdapter(createdPhotosAdapter);
        grPhotos.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        grPhotos.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if (checked) {
                    arrayList.get(position).setChecked(true);
                    arrSelected.add(arrayList.get(position));
                    createdPhotosAdapter.notifyDataSetChanged();
                } else {
                    arrayList.get(position).setChecked(false);
                    arrSelected.remove(arrayList.get(position));
                    createdPhotosAdapter.notifyDataSetChanged();
                }

                mode.setTitle(arrSelected.size() + " items selected");
//                Toast.makeText(getContext(), arrSelected.size() + "", Toast.LENGTH_SHORT).show();
            }


            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater = mode.getMenuInflater();
                menuInflater.inflate(R.menu.add_images, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.icReload) {
                    count = 0;
                    for (int i = 0; i < arrSelected.size(); i++) {
                        arrSelected.get(i).setChecked(false);
                    }
                    arrSelected.clear();
                    createdPhotosAdapter.notifyDataSetChanged();
                    mode.finish();
                } else if (item.getItemId() == R.id.icDeleteMultiple) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Delete " + arrSelected.size() + " items");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (int i = 0; i < arrSelected.size(); i++) {
                                arrayList.remove(arrSelected.get(i));
                                File fileToDelete = new File(arrSelected.get(i).getPhotoPath());
                                if (fileToDelete.exists()) {
                                    if (!fileToDelete.delete()) {
                                        Toast.makeText(getContext(), "Fail to delete", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            createdPhotosAdapter.notifyDataSetChanged();
                            mode.finish();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                    return false;
                } else if (item.getItemId() == R.id.icShare) {
                    for (int i = 0; i < arrSelected.size(); i++) {

                    }
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                count = 0;
                arrSelected.clear();
                mode.finish();
            }
        });
    }

}

