package com.example.retrofilldemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    EditText name,password;
    CircleImageView img;
    Button submit;

    String PostPath="", filePath="";

    private static String web_url="http://192.168.0.103/myBasicProjects/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name=findViewById(R.id.etname);
        password=findViewById(R.id.etpassword);
        img=findViewById(R.id.circleImageView);
        submit=findViewById(R.id.submitbtn);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveprofile();
            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = CropImage.activity()
                        .setAspectRatio(100,100)
                        .getIntent(MainActivity.this);

                startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

    }
    public void saveprofile()
    {


        Retrofit retrofit=null;

        if (retrofit==null)
        {
            retrofit=new Retrofit.Builder()
                    .baseUrl(web_url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        MultipartBody.Part img=null;

        if (!PostPath.equalsIgnoreCase(""))
        {
            File file=new File(PostPath);

            RequestBody imageData=RequestBody.create(MediaType.parse("image/*"),file);
            img=MultipartBody.Part.createFormData("profileImage", file.getName(),imageData);

        }

        RequestBody username=RequestBody.create(MediaType.parse("plain/text"), name.getText().toString());
        RequestBody pass=RequestBody.create(MediaType.parse("plain/text"), password.getText().toString());

        Interfacedemo myApiInterface;

        myApiInterface=retrofit.create(Interfacedemo.class);

        Map<String,RequestBody> param=new HashMap<>();
        param.put("name", username);
        param.put("password", pass);

        Call<Addprofile_mojo> call=myApiInterface.addProfile(param,img);

        call.enqueue(new Callback<Addprofile_mojo>() {
            @Override
            public void onResponse(Call<Addprofile_mojo> call, Response<Addprofile_mojo> response) {

                if (response.body().getSuccess()==1)
                {
                    Toast.makeText(MainActivity.this,response.body().getMessage(),Toast.LENGTH_LONG).show();
                }
                else if (response.body().getSuccess()==2)
                {
                    Toast.makeText(MainActivity.this,response.body().getMessage(),Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onFailure(Call<Addprofile_mojo> call, Throwable t) {

                Toast.makeText(MainActivity.this, t.getMessage(),Toast.LENGTH_LONG).show();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);

            if(resultCode==RESULT_OK)

            {
                Uri resultUri=result.getUri();

//                filePath=getRealPathFromUri(resultUri);
                filePath=getRealPathFromUri(resultUri);
                Glide.with(MainActivity.this)
                        .load(filePath)
                        .into(img);

                PostPath=filePath;

            }
            else if (resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                result.getError();
            }
        }
    }
    public String getRealPathFromUri(Uri contentUri)
    {

        String result;

        Cursor cursor=getBaseContext().getContentResolver().query(contentUri, null, null, null );

        if (cursor==null)
        {
            result=contentUri.getPath();
        }
        else
        {
            cursor.moveToFirst();
            int index= cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);

            result=cursor.getString(index);
            cursor.close();

        }
        return result;
    }
}