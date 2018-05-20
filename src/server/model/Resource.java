/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.model;

import server.auxilary.AccessLevels;
import server.auxilary.IO;

/**
 *
 * @author ghost
 */
public class Resource extends MVGObject
{
    private String brand_name;
    private String resource_description;
    private String resource_serial;
    private String resource_type;
    private double resource_value;
    private long quantity;
    private long date_acquired;
    private long date_exhausted;
    private String unit;
    public static final String TAG = "Resource";

    public Resource()
    {}

    public Resource(String _id)
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

    public String getBrand_name()
    {
        return brand_name;
    }

    public void setResource_name(String brand_name)
    {
        this.brand_name = brand_name;
    }

    public String getResource_description()
    {
        return resource_description;
    }

    public void setResource_description(String description)
    {
        this.resource_description = description;
    }

    public String getResource_serial()
    {
        return resource_serial;
    }

    public void setResource_serial(String resource_serial)
    {
        this.resource_serial = resource_serial;
    }

    public String getResource_type()
    {
        return resource_type;
    }

    public void setResource_type(String resource_type)
    {
        this.resource_type = resource_type;
    }

    public double getResource_value()
    {
        return resource_value;
    }

    public void setResource_value(double resource_value)
    {
        this.resource_value = resource_value;
    }

    public String getUnit()
    {
        return unit;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    public long getQuantity()
    {
        return quantity;
    }

    public void setQuantity(long quantity)
    {
        this.quantity = quantity;
    }

    public long getDate_acquired()
    {
        return date_acquired;
    }

    public void setDate_acquired(long date_acquired)
    {
        this.date_acquired = date_acquired;
    }

    public long getDate_exhausted()
    {
        return date_exhausted;
    }

    public void setDate_exhausted(long date_exhausted)
    {
        this.date_exhausted = date_exhausted;
    }

    @Override
    public String[] isValid()
    {
        /*if(getResource_name()==null)
            return new String[]{"false", "invalid brand_name value."};*/
        if(getResource_description()==null)
            return new String[]{"false", "invalid resource_description value."};
        if(getResource_value()<=0)
            return new String[]{"false", "invalid resource_value value."};
        if(getUnit()==null)
            return new String[]{"false", "invalid unit value."};
        if(getQuantity()<=0)
            return new String[]{"false", "invalid quantity value."};
        if(getDate_acquired()<=0)
            return new String[]{"false", "invalid date_acquired value."};
        if(getResource_serial()==null)
            return new String[]{"false", "invalid resource_serial value."};
        if(getResource_type()==null)
            return new String[]{"false", "invalid resource_type value."};

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
                case "brand_name":
                    brand_name = (String)val;
                    break;
                case "resource_type":
                    resource_type = (String)val;
                    break;
                case "resource_description":
                    resource_description = (String)val;
                    break;
                case "resource_serial":
                    resource_serial = (String)val;
                    break;
                case "resource_value":
                    resource_value = Double.parseDouble(String.valueOf(val));
                    break;
                case "date_acquired":
                    date_acquired = Long.parseLong(String.valueOf(val));
                    break;
                case "date_exhausted":
                    date_exhausted = Long.parseLong(String.valueOf(val));
                    break;
                case "quantity":
                    quantity = Long.parseLong(String.valueOf(val));
                    break;
                case "unit":
                    unit = String.valueOf(val);
                    break;
                default:
                    IO.log(TAG, IO.TAG_ERROR,"Unknown "+TAG+" attribute '" + var + "'.");
                    break;
            }
        } catch (NumberFormatException e)
        {
            IO.log(TAG, IO.TAG_ERROR, e.getMessage());
        }
    }

    @Override
    public Object get(String var)
    {
        switch (var.toLowerCase())
        {
            case "name":
            case "brand_name":
                return getBrand_name();
            case "resource_type":
                return resource_type;
            case "resource_description":
                return resource_description;
            case "resource_serial":
                return resource_serial;
            case "cost":
            case "value":
            case "resource_value":
                return getResource_value();
            case "date_acquired":
                return date_acquired;
            case "date_exhausted":
                return date_exhausted;
            case "quantity":
                return quantity;
            case "unit":
                return unit;
        }
        return super.get(var);
    }

    @Override
    public String toString()
    {
        return getResource_description();
    }

    @Override
    public String apiEndpoint()
    {
        return "/resources";
    }
}
