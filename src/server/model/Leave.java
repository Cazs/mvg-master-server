package server.model;

import server.auxilary.IO;

/**
 * Created by ghost on 2017/01/21.
 */
public class Leave extends MVGObject
{
    private String usr;
    private long start_date;
    private long end_date;
    private long return_date;
    private int status;
    private String type;
    public static final String TAG = "Leave";
    public static String[] TYPES = {"ANNUAL", "SICK", "UNPAID", "FAMILY RESPONSIBILITY - See BCEA for definition"};
    public static final int STATUS_PENDING =0;
    public static final int STATUS_APPROVED =1;
    public static final int STATUS_ARCHIVED =2;

    public String getUsr()
    {
        return usr;
    }

    public void setUsr(String usr)
    {
        this.usr = usr;
    }

    public long getStart_date()
    {
        return start_date;
    }

    public void setStart_date(long date)
    {
        this.start_date = date;
    }

    public long getEnd_date()
    {
        return end_date;
    }

    public void setEnd_date(long date)
    {
        this.end_date = date;
    }

    public long getReturn_date()
    {
        return return_date;
    }

    public void setReturn_date(long date)
    {
        this.return_date = date;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status= status;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @Override
    public String[] isValid()
    {
        if(getUsr()==null)
            return new String[]{"false", "invalid usr value."};
        if(getType()==null)
            return new String[]{"false", "invalid type value."};
        if(getStart_date()<=0)
            return new String[]{"false", "invalid start_date value."};
        if(getEnd_date()<=0)
            return new String[]{"false", "invalid end_date value."};
        if(getReturn_date()<=0)
            return new String[]{"false", "invalid return_date value."};
        /*if(getStatus()<0)
            return new String[]{"false", "invalid status value."};*/

        return super.isValid();
    }

    @Override
    public void parse(String var, Object val)
    {
        try
        {
            switch (var.toLowerCase())
            {
                case "usr":
                    setUsr(String.valueOf(val));
                    break;
                case "start_date":
                    setStart_date(Long.valueOf(String.valueOf(val)));
                    break;
                case "end_date":
                    setEnd_date(Long.parseLong(String.valueOf(val)));
                    break;
                case "return_date":
                    setReturn_date(Long.parseLong(String.valueOf(val)));
                    break;
                case "status":
                    setStatus(Integer.parseInt(String.valueOf(val)));
                    break;
                case "type":
                    setType((String)val);
                    break;
                default:
                    IO.log(getClass().getName(), IO.TAG_ERROR, "Unknown "+getClass().getName()+" attribute '" + var + "'.");
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
            case "usr":
                return getUsr();
            case "start_date":
                return getStart_date();
            case "end_date":
                return getEnd_date();
            case "return_date":
                return getReturn_date();
            case "date_logged":
                return getDate_logged();
            case "status":
                return getStatus();
            case "type":
                return getType();
        }
        return super.get(var);
    }

    @Override
    public String apiEndpoint()
    {
        return "/leave_records";
    }
}