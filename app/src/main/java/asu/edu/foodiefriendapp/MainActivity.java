package asu.edu.foodiefriendapp;

/*This activity is used to start the crop image activity that enables us with options
  to either take an image from camera or select an image from gallery inorder to prepare the image
  for uploading to the firebase storage bucket*/

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    Button FoodSuggest, FoodContent;
    Bitmap imageBitmap;
    String currentTime;
    private Uri takenImageUri, cropImageUri;
    double myLatitude, myLongitude;
    LocationTracker myLocation;
    File myFile;
    private FirebaseAuth authorization;
    Uri filePath;
    ProgressDialog pd;
    FirebaseStorage storage;
    StorageReference storageRef, mystorageRef;
    int flag = 0;
    private String  Bucket  = "gs://foodiefriend-3e683.appspot.com" ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        authorization = FirebaseAuth.getInstance();

        FoodSuggest = (Button) findViewById(R.id.suggestFood);
        FoodContent = (Button) findViewById(R.id.getContent);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading....");

        //This call gets the current location details using Location tracker class
        myLocation = new LocationTracker(MainActivity.this);

        //This gets the current location's latitude and longitude from the obtained location from the Location tracker class
        if (myLocation.canGetLocation()) {
            myLatitude = myLocation.getLatitude();
            myLongitude = myLocation.getLongitude();
        }

        //On clicking this button directs the application to start th crop image activity
        FoodSuggest.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                CropImage.startPickImageActivity(MainActivity.this);
            }
        });


        //On clicking this button application uploads the latitude and longitude to firebase and starts the
        // suggestions display activity to display the suggestion information restaurant wise
        FoodContent.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                currentTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                myFile = new File(Environment.getExternalStorageDirectory()+ "/DCIM/Camera/"+currentTime+".txt");
                FileOutputStream fOut = null;
                try
                {
                    fOut = new FileOutputStream(myFile);
                }

                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                try
                {
                    myOutWriter.append(Double.toString(myLatitude)+',');
                    myOutWriter.append(Double.toString(myLongitude));
                }

                catch (IOException e)
                {
                    e.printStackTrace();
                }

                try
                {
                    myOutWriter.close();
                }

                catch (IOException e)
                {
                    e.printStackTrace();
                }
                try
                {
                    fOut.close();
                }

                catch (IOException e)
                {
                    e.printStackTrace();
                }
                pd.show();

                storage = FirebaseStorage.getInstance();
                storageRef = storage.getReferenceFromUrl(Bucket);

                //uploading location and latitude to the firebase storage
                filePath = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/" + currentTime + ".txt"));
                if (filePath != null)
                {

                    StorageReference childRef = storageRef.child("/location_input/"+currentTime+".txt");

                    UploadTask uploadTask = childRef.putFile(filePath);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {

                        }
                    }).addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                        }
                    });
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Select an image", Toast.LENGTH_SHORT).show();
                }


                try
                {
                    TimeUnit.SECONDS.sleep(3);
                }

                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }


                SearchFood();

            }
        });

    }

    private void SearchFood()
    {

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl(Bucket);
        mystorageRef = storageRef.child("/location_output/"+currentTime+".xml");

        File rootPath = new File(Environment.getExternalStorageDirectory(), "/DCIM/Camera/download_files/");
        if (!rootPath.exists())
        {
            rootPath.mkdirs();
        }


        final File localFile = new File(rootPath,currentTime +".xml");

        try
        {
            TimeUnit.SECONDS.sleep(3);
        }

        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        //downloading the restaurant suggestion information from the firebase storage
        mystorageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot)
            {
                pd.dismiss();
                Toast.makeText(MainActivity.this, "Showing Results...", Toast.LENGTH_SHORT).show();
                flag = 1;
                if(flag == 1)
                {
                    Intent intent = new Intent(MainActivity.this, SuggestionsDisplayActivity.class);
                    intent.putExtra("timestamp", currentTime);
                    startActivity(intent);
                }

            }

        }).addOnFailureListener(new OnFailureListener()

        {
            @Override
            public void onFailure(@NonNull Exception exception)
            {
                Log.e("firebase ", "local xml file not created" + exception.toString());
            }
        });

    }


    //This is to enable us with selecting the image from gallery or take a picture from device camera and start the crop Image activity
    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE ) {
                takenImageUri = CropImage.getPickImageResultUri(this, data);
                if (CropImage.isReadExternalStoragePermissionsRequired(this, takenImageUri)) {
                    cropImageUri = takenImageUri;
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},   CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
                } else {
                    startCropImageActivity(takenImageUri);

                }
            }

            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    try {
                        imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 60, bytes);

                    File f =  new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/");
                    f.mkdirs();
                    currentTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                    //Creating file to store the cropped image
                    File f1 = new File(f, currentTime+".jpg");


                    String mypath = f1.getPath();
                    Log.d(mypath,"This is the path of the file");

                    FileOutputStream fo = null;
                    try {
                        fo = new FileOutputStream(f1);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        fo.write(bytes.toByteArray());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        fo.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //The application enters into Next Actvity after this call
                    Intent intent = new Intent(MainActivity.this, NextActivity.class);
                    intent.putExtra("timestamp", currentTime);
                    startActivity(intent);

                }

                else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Log.d("Crop Activity Error:",error.toString());
                }
            }
        }
    }

    //This is used for allotting dynamic permissions required for accessing camera and android crop
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (cropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCropImageActivity(cropImageUri);
            } else {
                Toast.makeText(this, "required permissions not available", Toast.LENGTH_LONG).show();
            }
        }
    }

    //This method is used to start the crop Image activity
    private void startCropImageActivity(Uri uri) {
        CropImage.activity(uri)
                .start(this);
    }

    //This is for creating sign out button option on the current activity
    public boolean onCreateOptionsMenu(Menu menu)
    {
        android.util.Log.d(this.getClass().getSimpleName(),"called onCreateOptionsMenu()");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.foodiefriend_mainactivity, menu);
        return super.onCreateOptionsMenu(menu);
    }



    //This is for enabling sign out button functionality from the current activity
    public boolean onOptionsItemSelected(MenuItem item)
    {
        android.util.Log.d(this.getClass().getSimpleName(), "called onOptionsItemSelected()");
        switch (item.getItemId())
        {
            case R.id.action_signout:
                authorization.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

