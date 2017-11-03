package asu.edu.foodiefriendapp;


/*This activity starts after the Main Actvivity and retrieves the cropped image stored in the SD Card
  and uploads the image to the Firebase storage bucket and downloads the computed resultant output image and text files
  that are put into the firebase storage by the googel app engine cloud infrastructure from the Firebase storage bucket
  and starts an intent to display the computed results.
*/


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.io.File;
import java.util.concurrent.TimeUnit;


public class NextActivity extends AppCompatActivity
{
    Button Displaytext, NoText;
    ImageView imgView;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth authorization;
    Uri filePath;
    ProgressDialog pd,dp;
    FirebaseStorage storage;
    StorageReference storageRef, myStorageRef, myStorageRef1, myStorageRef2, myStorageRef3;
    public String currentTime1;
    int flag = 0, flag1 = 0, flag2 = 0, flag3 = 0;
    private String  Bucket  = "gs://foodiefriend-3e683.appspot.com" ;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        Displaytext = (Button)findViewById(R.id.Displaytext);
        NoText = (Button) findViewById(R.id.Notext);
        imgView = (ImageView)findViewById(R.id.imgView);

        authorization = FirebaseAuth.getInstance();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null)
                {
                    startActivity(new Intent(NextActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        currentTime1 = getIntent().getStringExtra("timestamp");

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl(Bucket);

        pd = new ProgressDialog(this);
        pd.setMessage("Uploading the image...");
        dp = new ProgressDialog(this);
        dp.setMessage("Displaying the food details.... Please wait...");

        filePath = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/" + currentTime1+".jpg"));

        pd.show();
        if(filePath != null)
        {

            StorageReference childRef = storageRef.child("photo_input/"+currentTime1+".jpg");

            UploadTask uploadTask = childRef.putFile(filePath);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    pd.dismiss();

                    try
                    {
                        TimeUnit.SECONDS.sleep(4);
                    }

                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                }

            }).addOnFailureListener(new OnFailureListener()

            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    pd.dismiss();
                }
            });

        }

        Displaytext.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dp.show();

                myStorageRef = storageRef.child("/photo_output/"+currentTime1 +".txt");
                myStorageRef1 = storageRef.child("/photo_output/"+currentTime1+".jpg");
                myStorageRef2 = storageRef.child("/photo_output/"+currentTime1+".JPG");
                myStorageRef3 = storageRef.child("/photo_output/"+currentTime1+".png");


                File rootPath = new File(Environment.getExternalStorageDirectory(), "/DCIM/Camera/download_files/");
                if (!rootPath.exists()) {
                    rootPath.mkdirs();
                }


                final File localFile = new File(rootPath, currentTime1 + ".txt");

                final File localFile1 = new File(rootPath, currentTime1 +".jpg");

                final File localFile2 = new File(rootPath, currentTime1 + ".JPG");

                final File localFile3 = new File(rootPath, currentTime1 + ".png");

                try
                {
                    TimeUnit.SECONDS.sleep(3);
                }

                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }


                myStorageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot)
                    {
                        Toast.makeText(NextActivity.this, "Showing Results", Toast.LENGTH_SHORT).show();
                        flag = 1;
                        if(flag == 1 && (flag1 == 1 || flag2 == 1 || flag3 == 1))
                        {
                            dp.dismiss();
                            Intent intent = new Intent(NextActivity.this, DisplayActivity.class);
                            intent.putExtra("timestamp", currentTime1);
                            startActivity(intent);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception exception)
                    {
                        Log.e("firebase ", "text file not created" + exception.toString());
                    }
                });

                myStorageRef1.getFile(localFile1).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot)
                    {
                        Toast.makeText(NextActivity.this, "Showing Resuults", Toast.LENGTH_SHORT).show();
                        flag1 = 1;

                        if(flag == 1 && (flag1 == 1 || flag2 == 1 || flag3 == 1))
                        {
                            dp.dismiss();
                            Intent intent = new Intent(NextActivity.this, DisplayActivity.class);
                            intent.putExtra("timestamp", currentTime1);
                            startActivity(intent);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception exception)
                    {
                        Log.e("firebase ", "jpg file not created" + exception.toString());
                    }
                });


                myStorageRef2.getFile(localFile2).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot)
                    {
                        Toast.makeText(NextActivity.this, "Showing Resuults", Toast.LENGTH_SHORT).show();
                        flag2 = 1;
                        if(flag == 1 && (flag1 == 1 || flag2 == 1 || flag3 == 1))
                        {
                            dp.dismiss();
                            Intent intent = new Intent(NextActivity.this, DisplayActivity.class);
                            intent.putExtra("timestamp", currentTime1);
                            startActivity(intent);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception exception)
                    {
                        Log.e("firebase ", "JPG file not created" + exception.toString());
                    }
                });

                myStorageRef3.getFile(localFile3).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot)
                    {
                        Toast.makeText(NextActivity.this, "Showing Resuults", Toast.LENGTH_SHORT).show();

                        flag3 = 1;
                        if(flag == 1 && (flag1 == 1 || flag2 == 1 || flag3 == 1))
                        {
                            dp.dismiss();
                            Intent intent = new Intent(NextActivity.this, DisplayActivity.class);
                            intent.putExtra("timestamp", currentTime1);
                            startActivity(intent);

                        }

                    }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception exception)
                    {
                        Log.e("firebase ", "png file not created" + exception.toString());
                    }
                });
            }
        });

        NoText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Intent i = new Intent(NextActivity.this,MainActivity.class);
                startActivity(i);
            }

        });

    }


    //This is for creating home, back, sign out button options on the current activity
    public boolean onCreateOptionsMenu(Menu menu)
    {
        android.util.Log.d(this.getClass().getSimpleName(),"called onCreateOptionsMenu()");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.foodiefriend_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //This is for enabling home, back, sign out button functionality from the current activity
    public boolean onOptionsItemSelected(MenuItem item)
    {
        android.util.Log.d(this.getClass().getSimpleName(), "called onOptionsItemSelected()");
        switch (item.getItemId())
        {
            case R.id.action_home:
                Intent i = new Intent(this,MainActivity.class);
                startActivity(i);
                finish();
                return true;
            case R.id.action_back:
                Intent in = new Intent(this,MainActivity.class);
                startActivity(in);
                finish();
                return true;
            case R.id.action_signout:
                authorization.signOut();
                startActivity(new Intent(NextActivity.this, LoginActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}


