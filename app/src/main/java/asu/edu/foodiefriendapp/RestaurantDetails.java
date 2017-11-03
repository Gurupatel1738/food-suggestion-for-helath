package asu.edu.foodiefriendapp;


/*This class is used in XML file parsing and each of its object has the suggestion information relevant to a
 single restaurant */

public class RestaurantDetails
{
    public double myLatitude, myLongitude;
    public String name, type, address, link, comment;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getLink()
    {
        return link;
    }

    public void setLink(String link)
    {
        this.link = link;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public double getLatitude() {
        return myLatitude;
    }

    public void setLatitude(double latitude)
    {
        this.myLatitude = latitude;
    }

    public double getLongitude()
    {
        return myLongitude;
    }

    public void setLongitude(double longitude)
    {
        this.myLongitude = longitude;
    }
}

