package joss.polinema.TugasPolimoji1541180041;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_STORAGE = 1;
    private static final String FILE_PROVIDER_AUTHORITY = "joss.polinema.fileprovider";

    @BindView(R.id.imageView) ImageView mImageView;

    @BindView(R.id.btnSave) Button btnSave;
    @RequiresApi(api = Build.VERSION_CODES.N)
    //saveImage
    public void saveImage(View view){
        BitmapUtils.saveImage(this,resultBmp);
    }

    @BindView(R.id.btnClear) Button btnClear;
    //Delete the temporary image file
    public void clearImage(View view){
        mImageView.setImageResource(0);
        BitmapUtils.deleteImageFile(this,tempPath);
    }

    @BindView(R.id.btnShare) Button btnShare;
    //both
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void shareImage(View view){
        BitmapUtils.saveImage(this,resultBmp);
        BitmapUtils.shareImage(this,tempPath);
    }
    @BindView(R.id.btnTakePicture) Button btnTakePicture;

    @BindView(R.id.titleText) TextView titleText;

    private String tempPath;

    private Bitmap resultBmp;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @OnClick(R.id.btnTakePicture)
    public void emojify(View view){
        // Check for the external storage permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
        // If you do not have permission, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CAMERA);
        } else {
        // Launch the camera if the permission exists
           // Toast.makeText(this, "Belum ada permission", Toast.LENGTH_SHORT).show();
            launchCamera();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Called when you request permission to read and write to external storage
        switch (requestCode) {
            case REQUEST_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, launch the camera
                    //Toast.makeText(this, "Sudah ada permission ke kamera dan write external storage", Toast.LENGTH_SHORT).show();
                    launchCamera();
                } else {
                    // If you do not get permission, show a Toast
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    /**
     * Creates a temporary image file and captures a picture to store in it.
     */

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void launchCamera() {
        // Create the capture image intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the temporary File where the photo should go
            File photoFile = null;
            try {
                photoFile = BitmapUtils.createTempImageFile(this);
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                // Get the path of the temporary file
                tempPath = photoFile.getAbsolutePath();

                // Get the content URI for the image file
                Uri photoURI = FileProvider.getUriForFile(this,
                        FILE_PROVIDER_AUTHORITY,
                        photoFile);

                // Add the URI so the camera can store the image
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                // Launch the camera activity
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // If the image capture activity was called and was successful
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            // Process the image and set it to the TextView
            processAndSetImage();
        } else {

            // Otherwise, delete the temporary image file
            BitmapUtils.deleteImageFile(this, tempPath);
        }

    }

    /** Method for processing the captured image and setting it to the TextView.
     */

    private void processAndSetImage() {
        // Resample the saved image to fit the ImageView
        resultBmp= BitmapUtils.resamplePic(this, tempPath);

        // Set the new bitmap to the ImageView
        mImageView.setImageBitmap(resultBmp);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


    }
}
