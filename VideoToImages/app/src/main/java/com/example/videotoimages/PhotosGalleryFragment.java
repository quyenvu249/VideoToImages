package com.example.videotoimages;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.videotoimages.adapter.CreatedPhotosAdapter;
import com.example.videotoimages.model.CreatedPhotos;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class PhotosGalleryFragment extends Fragment {
    //RecyclerView rcPhotos;
    ArrayList<CreatedPhotos> arrayList;
    GridView grPhotos;

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
        getAllPhotos();
        grPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
                CreatedPhotos createdPhotos = new CreatedPhotos(BitmapFactory.decodeFile(allFiles[i].getAbsolutePath(), new BitmapFactory.Options()),allFiles[i].getName(), allFiles[i].getAbsolutePath(), Integer.parseInt(allFiles[i].length() + ""));
                arrayList.add(createdPhotos);
            }
        }
        CreatedPhotosAdapter createdPhotosAdapter = new CreatedPhotosAdapter(getActivity().getApplicationContext(), arrayList);
        grPhotos.setAdapter(createdPhotosAdapter);
    }

}

