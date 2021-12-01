package com.example.cryptoking;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Image_home extends AppCompatActivity {

    Button btEncode,btDecode;
    TextView textView;
    ImageView imageView;
    String sImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        //assign variable
        btEncode=findViewById(R.id.bt_encode);
        btDecode=findViewById(R.id.bt_decode);
        textView=findViewById(R.id.text_view);
        imageView=findViewById(R.id.image_view);

        btEncode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check condition
                if(ContextCompat.checkSelfPermission(Image_home.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    //WHEN PERMISSION IS NOT GRANTED REQUEST PERMISSION
                    ActivityCompat.requestPermissions(Image_home.this, new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                }
                else{
                    //when permission is granted create method
                    selectImage();
                }
            }
        });
        btDecode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Decode base64 string
                byte[] bytes= Base64.decode(sImage,Base64.DEFAULT);
                //initialize bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,
                        bytes.length);
                //set bitmap on image view
                imageView.setImageBitmap(bitmap);
            }
        });
    }


    private void selectImage() {
        //clear previous data
        textView.setText("");
        imageView.setImageBitmap(null);
        //initialize intent
        Intent intent = new Intent(Intent.ACTION_PICK);
        //Set Type
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        //start activity result
        startActivityForResult(Intent.createChooser(intent,"Select Image")
                , 100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //CHECK CONDITION
        if(requestCode==100 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            //when permission is granted
            //call method
            selectImage();
        }
        else{
            //when permission is denied
            Toast.makeText(getApplicationContext(),
                    "permission denied.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //check condition
        if(requestCode==100 && resultCode==RESULT_OK && data !=null){
            //when result is ok
            //initialize uri
            Uri uri=data.getData();

            try {
                //initialize bitmap
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),
                        uri);
                //Initialize byte stream
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                //compress bitmap
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
                //Initialize byte array
                byte[] bytes = stream.toByteArray();
                //get base64 encoded string
                sImage = Base64.encodeToString(bytes,Base64.DEFAULT);
                //set encoded text on textView
                textView.setText(sImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
