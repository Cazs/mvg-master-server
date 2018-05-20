package server.model;

import server.auxilary.AccessLevels;
import server.auxilary.IO;

import java.io.Serializable;

/**
 * Created by ghost on 2018/01/29.
 */
public class Notification extends MVGObject
{
    private String subject;
    private String message;
    private String client_id;
    public static final String TAG = "Metafile";

    public Notification()
    {
    }

    public Notification(String _id)
    {
        super(_id);
    }

    public Notification(String subject, String message, String client_id)
    {
        setSubject(subject);
        setMessage(message);
        setClient_id(client_id);
        setStatus(0);
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

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getClient_id()
    {
        return client_id;
    }

    public void setClient_id(String client_id)
    {
        this.client_id = client_id;
    }

    @Override
    public void parse(String var, Object val)
    {
        super.parse(var, val);
        switch (var.toLowerCase())
        {
            case "subject":
                subject = (String)val;
                break;
            case "message":
                message=(String)val;
                break;
            case "client_id":
                client_id=(String)val;
                break;
            default:
                IO.log(TAG, IO.TAG_ERROR, "unknown "+TAG+" attribute '" + var + "'");
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
                case "subject":
                    return subject;
                case "message":
                    return message;
                case "client_id":
                    return client_id;
                default:
                    return null;
            }
        } else return val;
    }

    @Override
    public String apiEndpoint()
    {
        return "/notifications";
    }
}

