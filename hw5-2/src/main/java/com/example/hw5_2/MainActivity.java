package com.example.hw5_2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.media.ExifInterface;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    Button button;
    String mCurrentPhotoPath;
    static int ImageCaptureCode = 1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.imageView);
        Button button = (Button)findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String time = new SimpleDateFormat("yyyy_MM_dd_HH:mm:ss").format(new Date());
                File file = null;
                try {
                    file = File.createTempFile(time, ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
                    mCurrentPhotoPath = file.getAbsolutePath();
                } catch (IOException e){
                    e.printStackTrace();
                }
                Uri uri = FileProvider.getUriForFile(MainActivity.this, "Package.fileprovider", file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, ImageCaptureCode);
            }
        });
    }

    public void onActivityResult (int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImageCaptureCode) {
            if (resultCode == RESULT_OK) {
                File bitmapFile = new File(mCurrentPhotoPath);
                Bitmap bitmap = null;
                try{
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(bitmapFile));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int orientation = getOrientationOfImage(mCurrentPhotoPath);
                try {
                    bitmap = getRotatedBitmap(bitmap, orientation);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(bitmap);
            } else if (resultCode == RESULT_CANCELED){
                Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public int getOrientationOfImage(String filepath)  {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        if (orientation != 0){
            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;

            }
        }
        return 0;
    }

    public Bitmap getRotatedBitmap(Bitmap bitmap, int degree) throws Exception{
        if (bitmap == null) return null;
        if (degree == 0) return bitmap;

        Matrix m = new Matrix();
        m.setRotate(degree, (float) bitmap.getWidth() / 2 , (float) bitmap.getHeight() / 2);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true); // Filter = True
    }
}
