package server.model;

import server.auxilary.IO;

/**
 * Created by ghost on 2017/02/01.
 */
public class Asset extends MVGObject
{
    private String asset_name;
    private String asset_description;
    private String asset_serial;
    private String asset_type;
    private double asset_value;
    private long date_acquired;
    private long date_exhausted;
    private long quantity;
    private String unit;

    public String getAsset_name()
    {
        return asset_name;
    }

    public void setAsset_name(String asset_name)
    {
        this.asset_name = asset_name;
    }

    public String getAsset_description()
    {
        return asset_description;
    }

    public void setAsset_description(String asset_description)
    {
        this.asset_description = asset_description;
    }

    public String getAsset_serial()
    {
        return asset_serial;
    }

    public void setAsset_serial(String asset_serial)
    {
        this.asset_serial = asset_serial;
    }

    public String getAsset_type()
    {
        return asset_type;
    }

    public void setAsset_type(String asset_type)
    {
        this.asset_type = asset_type;
    }

    public double getAsset_value()
    {
        return asset_value;
    }

    public void setAsset_value(double asset_value)
    {
        this.asset_value = asset_value;
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

    public long getQuantity()
    {
        return quantity;
    }

    public void setQuantity(long quantity)
    {
        this.quantity = quantity;
    }

    public String getUnit()
    {
        return unit;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    @Override
    public String[] isValid()
    {
        if(getAsset_name()==null)
            return new String[]{"false", "invalid asset_name value."};
        if(getAsset_description()==null)
            return new String[]{"false", "invalid asset_description value."};
        if(getAsset_value()<=0)
            return new String[]{"false", "invalid asset_value."};
        if(getDate_acquired()<=0)
            return new String[]{"false", "invalid date_acquired value."};
        /*if(getDate_exhausted()<=0)
            return new String[]{"false", "invalid date_exhausted."};*/
        if(getQuantity()<0)
            return new String[]{"false", "invalid quantity value."};
        if(getUnit()==null)
            return new String[]{"false", "invalid unit value."};

        return super.isValid();
    }

    @Override
    public void parse(String var, Object val)
    {
        super.parse(var, val);
        switch (var.toLowerCase())
        {
            case "asset_name":
                asset_name = (String)val;
                break;
            case "asset_description":
                asset_description = (String)val;
                break;
            case "asset_serial":
                asset_serial = (String)val;
                break;
            case "asset_type":
                asset_type = (String)val;
                break;
            case "asset_value":
                asset_value = Double.parseDouble(String.valueOf(val));
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
                unit = (String)val;
                break;
            default:
                IO.log(getClass().getName(), "Unknown Asset attribute '" + var + "'.", IO.TAG_ERROR);
                break;
        }
    }

    @Override
    public Object get(String var)
    {
        Object val = super.get(var);
        if(val==null)
        {
            switch (var.toLowerCase())
            {
                case "name":
                case "asset_name":
                    return getAsset_name();
                case "asset_type":
                    return asset_type;
                case "description":
                case "asset_description":
                    return getAsset_description();
                case "asset_serial":
                    return asset_serial;
                case "cost":
                case "value":
                case "asset_value":
                    return getAsset_value();
                case "date_acquired":
                    return date_acquired;
                case "date_exhausted":
                    return date_exhausted;
                case "quantity":
                    return quantity;
                case "unit":
                    return unit;
                default:
                    IO.log(getClass().getName(), "Unknown "+getClass().getName()+" attribute '" + var + "'.", IO.TAG_ERROR);
                    return null;
            }
        } else return val;
    }

    @Override
    public String toString()
    {
        return getAsset_name();
    }

    @Override
    public String apiEndpoint()
    {
        return "/assets";
    }
}
