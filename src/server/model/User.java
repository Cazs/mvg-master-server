/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.model;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import server.auxilary.AccessLevels;
import server.auxilary.IO;

import java.util.List;

/**
 *
 * @author ghost
 */
public class User extends MVGObject
{
    private String usr;
    private String pwd;//hashed
    private String firstname;
    private String lastname;
    private String organisation_id;
    private String gender;
    private String email;
    private String tel;
    private String cell;
    private int access_level;
    public static final int STATUS_INACTIVE = 0;
    public static final int STATUS_ACTIVE = 1;
    public static final String TAG = "User";

    public User()
    {}

    public User(String _id)
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
        //if User to be created has access rights > standard then creator must have superuser access rights
        if(getAccess_level()>AccessLevels.STANDARD.getLevel())
            return AccessLevels.SUPERUSER;
        else return AccessLevels.STANDARD;
    }

    public String getUsr()
    {
        return usr;
    }

    public User setUsr(String usr)
    {
        this.usr = usr;
        return this;
    }

    public String getPwd()
    {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public int getAccess_level() {
        return access_level;
    }

    public void setAccess_level(int access_level)
    {
        this.access_level = access_level;
    }

    public boolean isActive()
    {
        return getStatus()==STATUS_ACTIVE;
    }

    public void setActive(boolean active)
    {
        setStatus(active ? STATUS_ACTIVE : STATUS_INACTIVE);
    }

    public String getFirstname()
    {
        return firstname;
    }

    public void setFirstname(String firstname)
    {
        this.firstname = firstname;
    }

    public String getLastname()
    {
        return lastname;
    }

    public void setLastname(String lastname)
    {
        this.lastname = lastname;
    }

    public String getName()
    {
        return getFirstname()+" "+getLastname();
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getOrganisation_id()
    {
        return organisation_id;
    }

    public void setOrganisation_id(String organisation_id)
    {
        this.organisation_id = organisation_id;
    }

    public String getTel()
    {
        return tel;
    }

    public void setTel(String tel)
    {
        this.tel = tel;
    }

    public String getCell()
    {
        return cell;
    }

    public void setCell(String cell)
    {
        this.cell = cell;
    }

    public String getGender()
    {
        return gender;
    }

    public void setGender(String gender)
    {
        this.gender = gender;
    }

    public User getUser()
    {
        //get User from this Session object
        List<User> users = IO.getInstance().mongoOperations().find(new Query(Criteria.where("usr").is(getUsr())), User.class, "users");
        if(users==null)
        {
            IO.log(getClass().getName(), IO.TAG_ERROR, "getUser()> could not find a user associated with the username ["+getUsr()+"]");
            return null;
        }
        if(users.size()!=1)//should never happen
        {
            IO.log(getClass().getName(), IO.TAG_ERROR, "getUser()> could not find a valid user associated with username ["+getUsr()+"]");
            return null;
        }

        return users.get(0);
    }

    public String getInitials(){return new String(firstname.substring(0,1) + lastname.substring(0,1));}

    @Override
    public String[] isValid()
    {
        if(getUsr()==null)
            return new String[]{"false", "invalid usr value."};
        if(getPwd()==null)
            return new String[]{"false", "invalid pwd value."};
        if(getFirstname()==null)
            return new String[]{"false", "invalid firstname value."};
        if(getLastname()==null)
            return new String[]{"false", "invalid lastname value."};
        if(getCell()==null)
            return new String[]{"false", "invalid cell value."};
        // if(getTel()==null)
        //    return new String[]{"false", "invalid tel value."};
        if(getEmail()==null)
            return new String[]{"false", "invalid email value."};
        // if(getOrganisation_id()==null)
        //    return new String[]{"false", "invalid organisation_id value."};
        if(getGender()==null)
            return new String[]{"false", "invalid gender value."};
        if(getAccess_level()<0)
            return new String[]{"false", "invalid access_level value."};

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
                case "firstname":
                    setFirstname((String)val);
                    break;
                case "lastname":
                    setLastname((String)val);
                    break;
                case "usr":
                    setUsr((String)val);
                    break;
                case "gender":
                    setGender((String)val);
                    break;
                case "email":
                    setEmail((String)val);
                    break;
                case "organisation_id":
                    setOrganisation_id((String)val);
                    break;
                case "access_level":
                    setAccess_level(Integer.parseInt((String)val));
                    break;
                case "tel":
                    setTel((String)val);
                    break;
                case "cell":
                    setCell((String)val);
                    break;
                case "active":
                    setActive(Boolean.parseBoolean((String)val));
                    break;
                default:
                    IO.log(TAG, IO.TAG_WARN, String.format("unknown "+getClass().getName()+" attribute '%s'", var));
                    break;
            }
        }catch (NumberFormatException e)
        {
            IO.log(getClass().getName(), IO.TAG_ERROR, e.getMessage());
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
                case "firstname":
                    return firstname;
                case "lastname":
                    return lastname;
                case "usr":
                    return usr;
                case "access_level":
                    return access_level;
                case "gender":
                    return gender;
                case "email":
                    return email;
                case "organisation_id":
                    return organisation_id;
                case "tel":
                    return tel;
                case "cell":
                    return cell;
                case "active":
                    return isActive();
                default:
                    IO.log(TAG, IO.TAG_WARN, String.format("unknown "+getClass().getName()+" attribute '%s'", var));
                    return null;
            }
        } else return val;
    }

    @Override
    public String toString()
    {
        return super.toString() + " = "  + getName();
    }

    @Override
    public String apiEndpoint()
    {
        return "/users";
    }
}
