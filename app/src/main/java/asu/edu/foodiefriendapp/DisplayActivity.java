package asu.edu.foodiefriendapp;

/*This activity starts after the Next Actvivity and retrieves the output files from SD card and displays
the sample image of the food item and the details such as ingredients, type of the food, its origin etc.,
*/


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class DisplayActivity extends AppCompatActivity {
    private String time2;
    private FirebaseAuth auth;
    Bitmap bitmap;
    File myFile1, myFile2, myFile3;
    int flag;
    TextView display;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        auth = FirebaseAuth.getInstance();
        display = (TextView) findViewById(R.id.display_text);
        imageView = (ImageView) findViewById(R.id.display);

        time2 = getIntent().getStringExtra("timestamp");

        StringBuilder text1 = new StringBuilder();
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory()+
                    "/DCIM/Camera/download_files/"+time2+".txt"));

            String line;

            flag = 0;

            while ((line = br.readLine()) != null)
            {

                text1.append(line);
                text1.append('\n');

            }
            br.close();
        }
        catch (IOException e) {
            Toast.makeText(DisplayActivity.this, "Display text Failed -> " + e, Toast.LENGTH_SHORT).show();
        }

            myFile1 = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/download_files/" + time2 + ".JPG");
            myFile2 = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/download_files/" + time2 + ".jpg");
            myFile3 = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/download_files/" + time2 + ".png");

            if (myFile1.exists()) {
                bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() +
                        "/DCIM/Camera/download_files/" + time2 + ".JPG");
            } else if (myFile2.exists()) {
                bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() +
                        "/DCIM/Camera/download_files/" + time2 + ".jpg");
            } else if (myFile3.exists()) {
                bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() +
                        "/DCIM/Camera/download_files/" + time2 + ".png");
            }

            imageView.setImageBitmap(bitmap);

            display.setText(text1);
            display.setTypeface(null, Typeface.ITALIC);
            display.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
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
                Intent in = new Intent(this,NextActivity.class);
                startActivity(in);
                finish();
                return true;
            case R.id.action_signout:
                auth.signOut();
                startActivity(new Intent(DisplayActivity.this, LoginActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

