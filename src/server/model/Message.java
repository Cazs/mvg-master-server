package server.model;

import server.auxilary.AccessLevels;
import server.auxilary.IO;

/**
 * Created by ghost on 2018/01/29.
 */
public class Message extends MVGObject
{
    private String subject;
    private String message;
    private String receiver;
    public static final String TAG = "Metafile";

    public Message()
    {
    }

    public Message(String _id)
    {
        super(_id);
    }

    public Message(String subject, String message, String receiver)
    {
        setSubject(subject);
        setMessage(message);
        setReceiver(receiver);
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

    public String getReceiver()
    {
        return receiver;
    }

    public void setReceiver(String receiver)
    {
        this.receiver = receiver;
    }

    public String getTo()
    {
        return getReceiver();
    }

    public String getFrom()
    {
        return super.getCreator();
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
            case "receiver":
                receiver =(String)val;
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
                case "receiver":
                    return receiver;
                default:
                    return null;
            }
        } else return val;
    }

    @Override
    public String apiEndpoint()
    {
        return "/message";
    }
}

