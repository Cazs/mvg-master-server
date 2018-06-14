package server.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import server.auxilary.AccessLevels;
import server.auxilary.IO;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.AbstractMap;

/**
 * Created by ghost on 2017/01/04.
 */
public abstract class MVGObject implements Serializable
{
    @Id
    private String _id;
    private long object_number;
    private long date_logged;
    private String creator;
    private String authoriser;
    private int status;
    private String other;
    private boolean marked;
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_AUTHORISED = 1;
    public static final int STATUS_ARCHIVED = 2;
    public static final int STATUS_INVISIBLE = 3;
    public static final int VAT = 15;

    public abstract AccessLevels getReadMinRequiredAccessLevel();

    public abstract AccessLevels getWriteMinRequiredAccessLevel();

    public MVGObject()
    {}

    public MVGObject(String _id)
    {
        set_id(_id);
    }

    public String get_id()
    {
        return _id;
    }

    public void set_id(String _id)
    {
        this._id = _id;
    }

    public long getObject_number()
    {
        return object_number;
    }

    public void setObject_number(long object_number)
    {
        this.object_number = object_number;
    }

    public boolean isMarked()
    {
        return this.marked;
    }

    public void setMarked(boolean marked){this.marked=marked;}

    public long getDate_logged()
    {
        return this.date_logged;
    }

    public void setDate_logged(long date_logged)
    {
        this.date_logged = date_logged;
    }

    public String getLogged_date()
    {
        // return date.format(DateTimeFormatter.BASIC_ISO_DATE);
        AbstractMap.SimpleEntry<Integer, LocalDateTime> date_map = IO.isEpochSecondOrMilli(getDate_logged());
        LocalDateTime date = date_map.getValue();

        return IO.getYyyyMMddFormmattedDate(date);
    }

    public String getCreator()
    {
        return this.creator;
    }

    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    public String getCreator_name()
    {
        User user = getCreator_user();
        if(user!=null)
            return user.getName();
        return getCreator();// fallback to username
    }

    public User getCreator_user()
    {
        return IO.getInstance().mongoOperations().findOne(new Query(Criteria.where("usr").is(getCreator())), User.class, "users");
    }

    public String getAuthoriser()
    {
        return this.authoriser;
    }

    public void setAuthoriser(String creator)
    {
        this.authoriser = authoriser;
    }

    public User getAuthoriser_user()
    {
        return IO.getInstance().mongoOperations().findOne(new Query(Criteria.where("usr").is(getAuthoriser())), User.class, "users");
    }

    public String getAuthoriser_name()
    {
        User user = getAuthoriser_user();
        if(user!=null)
            return user.getName();
        else if(getAuthoriser() != null)
            return getAuthoriser();
        else return "N/A";
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public String getStatus_description()
    {
        switch (getStatus())
        {
            case MVGObject.STATUS_PENDING:
                return "Pending";
            case MVGObject.STATUS_AUTHORISED:
                return "Authorized";
            case MVGObject.STATUS_ARCHIVED:
                return "Archived";
            default:
                return "Unknown";
        }
    }

    public String getOther()
    {
        return other;
    }

    public void setOther(String other)
    {
        this.other = other;
    }

    public void parse(String var, Object val)
    {
        switch (var.toLowerCase())
        {
            case "date_logged":
                date_logged = Long.parseLong(String.valueOf(val));
                break;
            case "creator":
                creator = String.valueOf(val);
                break;
            case "status":
                status = Integer.parseInt(String.valueOf(val));
                break;
            case "other":
                other = String.valueOf(val);
                break;
            case "marked":
                marked = Boolean.valueOf((String) val);
                break;
        }
    }

    public Object get(String var)
    {
        switch (var.toLowerCase())
        {
            case "_id":
            case "id":
                return get_id();
            case "date_logged":
                return getDate_logged();
            case "creator":
                return getCreator();
            case "status":
                return getStatus();
            case "marked":
                return isMarked();
            case "other":
                return getOther();
            default:
                IO.log(getClass().getName(), IO.TAG_ERROR, "unknown "+getClass().getName()+" attribute '" + var + "'.");
                return null;
        }
    }

    /**
     * Method to check if respective MVGObject's attributes are valid or not.
     * @return String Array of size 2, first element is a true/false value and second is a message
     */
    //TODO: throw InvalidBusinessObjectException
    public String[] isValid()
    {
        if(getDate_logged()<=0)
            return new String[]{"false", "invalid date_logged value."};
        if(getClass() != User.class) // all other models must have a creator except Users, which can, but don't have to
        {
            if (getCreator() == null)
                return new String[]{"false", "invalid creator value."};
            if (getCreator().isEmpty())
                return new String[]{"false", "invalid creator value."};
        }

        return new String[]{"true", "valid "+getClass().getName()+" object."};
    }
    //TODO: public abstract String asURLEncodedString();//TODO: check if models comply

    @Override
    public String toString()
    {
        return get_id() + "#" + getObject_number() + " created by " +getCreator()+" ";
    }

    public abstract String apiEndpoint();
}
