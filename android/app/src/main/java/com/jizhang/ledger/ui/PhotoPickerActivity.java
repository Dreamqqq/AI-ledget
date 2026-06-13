package com.jizhang.ledger.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.jizhang.ledger.R;
import com.jizhang.ledger.ui.adapter.PhotoAdapter;
import java.util.ArrayList;
import java.util.List;

public class PhotoPickerActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private RecyclerView recyclerView;
    private PhotoAdapter adapter;
    private List<String> imagePaths = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_picker);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("选择照片");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        if (checkPermission()) {
            loadImages();
        } else {
            requestPermission();
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadImages();
            } else {
                Toast.makeText(this, "需要存储权限才能选择照片", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void loadImages() {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media.DATA};
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        try {
            Cursor cursor = getContentResolver().query(uri, projection, null, null, sortOrder);
            if (cursor != null) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                while (cursor.moveToNext()) {
                    String imagePath = cursor.getString(columnIndex);
                    if (imagePath != null) {
                        imagePaths.add(imagePath);
                    }
                }
                cursor.close();
            }
        } catch (Exception e) {
            Toast.makeText(this, "加载图片失败", Toast.LENGTH_SHORT).show();
        }

        if (imagePaths.isEmpty()) {
            Toast.makeText(this, "未找到图片", Toast.LENGTH_SHORT).show();
        }

        adapter = new PhotoAdapter(this, imagePaths, this::onImageSelected);
        recyclerView.setAdapter(adapter);
    }

    private void onImageSelected(String imagePath) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("imagePath", imagePath);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
