package com.rodrigosambade.mediastoreselected;

import android.Manifest;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;

import java.util.Map;

public class MainActivity extends ComponentActivity {

    private TextView output;

    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(),
                    this::onPermissionsResult);

    private final ActivityResultLauncher<PickVisualMediaRequest> photoPickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.PickVisualMedia(),
                    this::onPhotoPicked);

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 32, 32, 32);

        Button requestAccess = new Button(this);
        requestAccess.setText("Request image-library access");
        requestAccess.setOnClickListener(view -> requestMediaAccess());
        root.addView(requestAccess);

        Button queryImages = new Button(this);
        queryImages.setText("Query visible images");
        queryImages.setOnClickListener(view -> queryVisibleImages());
        root.addView(queryImages);

        Button pickImage = new Button(this);
        pickImage.setText("Open system photo picker");
        pickImage.setOnClickListener(view -> pickOneImage());
        root.addView(pickImage);

        output = new TextView(this);
        root.addView(
                output,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

        setContentView(root);
    }

    private void requestMediaAccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissionLauncher.launch(new String[] {
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            });
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(new String[] {
                    Manifest.permission.READ_MEDIA_IMAGES
            });
        } else {
            permissionLauncher.launch(new String[] {
                    Manifest.permission.READ_EXTERNAL_STORAGE
            });
        }
    }

    private void onPermissionsResult(Map<String, Boolean> result) {
        boolean anyGranted = false;
        for (Boolean granted : result.values()) {
            if (Boolean.TRUE.equals(granted)) {
                anyGranted = true;
                break;
            }
        }

        if (anyGranted) {
            queryVisibleImages();
        } else {
            output.setText(
                    "Library permission was not granted. The system photo picker still works "
                            + "without broad library access.");
        }
    }

    private void pickOneImage() {
        PickVisualMediaRequest request = new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build();
        photoPickerLauncher.launch(request);
    }

    private void onPhotoPicked(Uri uri) {
        if (uri == null) {
            output.setText("No image selected.");
            return;
        }
        output.setText("Picked URI: " + uri);
    }

    private void queryVisibleImages() {
        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME
        };

        StringBuilder names = new StringBuilder();
        int count = 0;

        try (Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Images.Media.DATE_ADDED + " DESC")) {

            if (cursor != null) {
                int nameColumn = cursor.getColumnIndexOrThrow(
                        MediaStore.Images.Media.DISPLAY_NAME);

                while (cursor.moveToNext()) {
                    count++;
                    if (count <= 10) {
                        names.append(cursor.getString(nameColumn)).append('\n');
                    }
                }
            }
        } catch (SecurityException exception) {
            output.setText(
                    "The app does not currently have permission to query the requested images. "
                            + "Use the Photo Picker for one-off access or grant library access.");
            return;
        }

        output.setText("Visible images: " + count + "\n" + names);
    }
}
