package asu.edu.foodiefriendapp;

/*This class takes in suggestion information related to a restaurant from the xml data obtained from firebase storage
  and parses it and assigns it to each restaurant details object and sends the information to be displayed by the suggestions
  display activity
 */

import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class RestaurantDetailsXMLParser extends DefaultHandler
{
    boolean currentElement = false;
    String currentValue = "";

    RestaurantDetails myRestaurantInstance;
    ArrayList<RestaurantDetails> restaurant;

    public ArrayList<RestaurantDetails> getRestaurant()
    {
        return restaurant;
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        currentElement = true;

        if (qName.equals("restaurants"))
        {
            restaurant = new ArrayList<>();
        }
        else if (qName.equals("restaurant"))
        {
            myRestaurantInstance = new RestaurantDetails();
        }

    }

    public void endElement(String uri, String localName, String qName) throws SAXException
    {

        currentElement = false;

        if (qName.equalsIgnoreCase("name"))
            myRestaurantInstance.setName(currentValue.trim());
        else if (qName.equalsIgnoreCase("type"))
            myRestaurantInstance.setType(currentValue.trim());
        else if (qName.equalsIgnoreCase("address"))
            myRestaurantInstance.setAddress(currentValue.trim());
        else if (qName.equalsIgnoreCase("link"))
            myRestaurantInstance.setLink(currentValue.trim());
        else if (qName.equalsIgnoreCase("comment"))
            myRestaurantInstance.setComment(currentValue.trim());
        else if (qName.equalsIgnoreCase("latitude"))
            myRestaurantInstance.setLatitude(Double.parseDouble(currentValue.trim()));
        else if (qName.equalsIgnoreCase("longitude"))
            myRestaurantInstance.setLongitude(Double.parseDouble(currentValue.trim()));
        else if (qName.equalsIgnoreCase("restaurant"))
            restaurant.add(myRestaurantInstance);

        currentValue = "";
    }

    public void characters(char[] ch, int start, int length)
            throws SAXException {

        if (currentElement) {
            currentValue = currentValue + new String(ch, start, length);
        }

    }

}

