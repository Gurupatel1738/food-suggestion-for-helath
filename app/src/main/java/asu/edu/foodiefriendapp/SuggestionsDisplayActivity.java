package asu.edu.foodiefriendapp;

/*This activity takes intent from the main activity and display the nearby restaurants, their location, address
  and the comments about the items to be there and a web link of the restaurant suggesting the user to visit them */

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import android.widget.LinearLayout;

public class SuggestionsDisplayActivity extends AppCompatActivity
{
    private String currentTime2;
    private FirebaseAuth authorization;
    File myFile1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_display);

        authorization = FirebaseAuth.getInstance();
        currentTime2 = getIntent().getStringExtra("timestamp");
        myFile1 = new File(Environment.getExternalStorageDirectory()+ "/DCIM/Camera/download_files/"+currentTime2+".xml");
        FileInputStream fin = null;
        try
        {
            fin = new FileInputStream(myFile1);
            final String ret = convertStreamToString(fin);
            parseXML(ret);


        }

        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    //This converts an input stream to string
    public static String convertStreamToString(InputStream is) throws Exception
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null)
        {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }


   //This function parses the obtained xml file that contains restaurant information that is downloaded from firebase storage
    private void parseXML(String ret)
    {

        try
        {
            InputStream is = new FileInputStream(myFile1);
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser saxParser = spf.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();

            RestaurantDetailsXMLParser myXMLParser = new RestaurantDetailsXMLParser();
            xmlReader.setContentHandler(myXMLParser);
            InputSource inStream = new InputSource(new InputStreamReader(is));
            xmlReader.parse(inStream);
            ArrayList<RestaurantDetails> coordinates = myXMLParser.getRestaurant();

            //programmatially creating layouts and addings views consisting of restaurant information dynamically to them
            for(RestaurantDetails restaurant: coordinates)
            {
                LinearLayout LL = (LinearLayout)findViewById(R.id.linearLayout1);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, 100);
                lp.weight = 1;

                TextView tv = new TextView(this);
                tv.setText("Name: " + restaurant.getName()+"\n"+"Type: " + restaurant.getType()+'\n'+"Address: "
                        + restaurant.getAddress()+'\n'+ "comment: " + restaurant.getComment()+'\n'+"link: " +
                        restaurant.getLink()+ '\n'+"Latitude : " + restaurant.getLatitude()+'\n'+"Longitude : " + '\n'
                        + restaurant.getLongitude()+'\n'+ "-------------------------->");
                LL.addView(tv);

            }

            is.close();

        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //This is for creating sign out, home, back button options for the current activity
    public boolean onCreateOptionsMenu(Menu menu)
    {
        android.util.Log.d(this.getClass().getSimpleName(),"called onCreateOptionsMenu()");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.foodiefriend_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //This is for enabling the functionality of the sign out, home, back button options  for the current activity
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
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
