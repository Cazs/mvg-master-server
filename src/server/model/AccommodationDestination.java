/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.model;

import server.auxilary.AccessLevels;
import server.auxilary.IO;

import java.io.Serializable;

/**
 * 2018/05/15
 * @author th3gh0st
 */
public class AccommodationDestination extends MVGObject
{
    private String name;
    private String description;
    private double cost_per_night_adults;
    private double cost_per_night_children;
    private String country;
    private String state_province;
    private String city;
    private String town;//optional
    private String street;
    private String unit_number;
    private String zip_code;
    private String gps_coords;//optional
    private double rating;

    public static final String TAG = "AccommodationDestination";

    public AccommodationDestination()
    {}

    public AccommodationDestination(String _id)
    {
        super(_id);
    }

    @Override
    public AccessLevels getReadMinRequiredAccessLevel()
    {
        return AccessLevels.STANDARD;
    }

    @Override
    public AccessLevels getWriteMinRequiredAccessLevel()
    {
        return AccessLevels.ADMIN;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public double getCost_per_night_adults()
    {
        return cost_per_night_adults;
    }

    public void setCost_per_night_adults(double cost_per_night_adults)
    {
        this.cost_per_night_adults = cost_per_night_adults;
    }

    public double getCost_per_night_children()
    {
        return cost_per_night_children;
    }

    public void setCost_per_night_children(double cost_per_night_children)
    {
        this.cost_per_night_children = cost_per_night_children;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getState_province()
    {
        return state_province;
    }

    public void setState_province(String state_province)
    {
        this.state_province = state_province;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getTown()
    {
        return town;
    }

    public void setTown(String town)
    {
        this.town = town;
    }

    public String getStreet()
    {
        return street;
    }

    public void setStreet(String street)
    {
        this.street = street;
    }

    public String getUnit_number()
    {
        return unit_number;
    }

    public void setUnit_number(String unit_number)
    {
        this.unit_number = unit_number;
    }

    public String getGps_coords()
    {
        return gps_coords;
    }

    public void setGps_coords(String gps_coords)
    {
        this.gps_coords = gps_coords;
    }

    public double getRating()
    {
        return rating;
    }

    public void setRating(double rating)
    {
        this.rating = rating;
    }

    public String getZip_code()
    {
        return zip_code;
    }

    public void setZip_code(String zip_code)
    {
        this.zip_code = zip_code;
    }

    @Override
    public String[] isValid()
    {
        if(getName()==null || getName().isEmpty())
            return new String[]{"false", "invalid accommodation name."};
        if(getDescription()==null || getDescription().isEmpty())
            return new String[]{"false", "invalid accommodation description."};
        if(getRating() < 0)
            return new String[]{"false", "invalid rating value."};
        if(getCost_per_night_adults() < 0)
            return new String[]{"false", "invalid cost_per_night_adults value."};
        if(getCost_per_night_children() < 0)
            return new String[]{"false", "invalid cost_per_night_children value."};
        if(getCountry() == null || getCountry().isEmpty())
            return new String[]{"false", "invalid country value."};
        if(getState_province() == null || getState_province().isEmpty())
            return new String[]{"false", "invalid province_state value."};
        if(getCity() == null || getCity().isEmpty())
            return new String[]{"false", "invalid city value."};
        /*if(getTown() == null || getTown().isEmpty())
            return new String[]{"false", "invalid town value."};*/
        if(getStreet() == null || getStreet().isEmpty())
            return new String[]{"false", "invalid street value."};
        if(getUnit_number() == null || getUnit_number().isEmpty())
            return new String[]{"false", "invalid unit_number value."};
        if(getZip_code() == null || getZip_code().isEmpty())
            return new String[]{"false", "invalid zip_code value."};

        return super.isValid();
    }

    @Override
    public void parse(String var, Object val)
    {
        super.parse(var, val);
        try
        {
            switch (var.toLowerCase())
            {
                case "city":
                    city = (String)val;
                    break;
                case "town":
                    town = (String)val;
                case "street":
                    street = (String)val;
                    break;
                case "unit_number":
                    unit_number = (String)val;
                    break;
                case "gps":
                case "coords":
                case "gps_coords":
                    gps_coords = (String)val;
                    break;
                case "zip_code":
                    zip_code = String.valueOf(val);
                    break;
                case "rating":
                    rating = Double.parseDouble(String.valueOf(val));
                    break;
                case "country":
                    country = String.valueOf(val);
                    break;
                case "state":
                case "province":
                    state_province = String.valueOf(val);
                    break;
                default:
                    IO.log(TAG, IO.TAG_ERROR,"Unknown "+getClass().getName()+" attribute '" + var + "'.");
                    break;
            }
        } catch (NumberFormatException e)
        {
            IO.log(getClass().getName(), IO.TAG_ERROR, e.getMessage());
        }
    }

    @Override
    public Object get(String var)
    {
        switch (var.toLowerCase())
        {
            case "city":
                return getCity();
            case "town":
                return getTown();
            case "street":
                return getStreet();
            case "unit_number":
                return getUnit_number();
            case "gps":
            case "coords":
            case "gps_coords":
                return getGps_coords();
            case "zip_code":
                return getZip_code();
            case "rating":
                return getRating();
            case "country":
                return getCountry();
            case "state":
            case "province":
                return getState_province();
        }
        return super.get(var);
    }

    @Override
    public String toString()
    {
        return getName() + " :=> " + getDescription();
    }

    @Override
    public String apiEndpoint()
    {
        return "/destination";
    }
}
