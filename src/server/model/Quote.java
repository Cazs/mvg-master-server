package server.model;

import server.auxilary.IO;

/**
 * Created by ghost on 2017/01/21.
 */
public class Quote extends MVGObject
{
    private String enquiry_id;
    private String client_id;
    private String contact_person_id;
    private String parent_id;
    private String request;
    private double vat;
    private String account_name;
    private double revision;
    private int status;
    public static final String TAG = "Quote";

    public String getEnquiry_id()
    {
        return enquiry_id;
    }

    public void setEnquiry_id(String enquiry_id)
    {
        this.enquiry_id = enquiry_id;
    }

    public String getClient_id()
    {
        return client_id;
    }

    public void setClient_id(String client_id)
    {
        this.client_id = client_id;
    }

    public String getContact_person_id()
    {
        return contact_person_id;
    }

    public void setContact_person_id(String contact_person_id)
    {
        this.contact_person_id = contact_person_id;
    }

    public String getRequest()
    {
        return request;
    }

    public void setRequest(String request)
    {
        this.request = request;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public double getVat()
    {
        return vat;
    }

    public void setVat(double vat)
    {
        this.vat = vat;
    }

    public String getAccount_name()
    {
        return account_name;
    }

    public void setAccount_name(String account_name)
    {
        this.account_name = account_name;
    }

    public String getParent_id(){return this.parent_id;}

    public void setParent_id(String parent_id)
    {
        this.parent_id = parent_id;
    }

    public double getRevision()
    {
        return revision;
    }

    public void setRevision(double revision)
    {
        this.revision = revision;
    }

    @Override
    public String[] isValid()
    {
        if(getClient_id()==null)
            return new String[]{"false", "invalid client_id value."};
        /*if(getParent_id()==null)
        {
            IO.log(getClass().getName(), IO.TAG_ERROR, "invalid parent_id value.");
            return false;
        }*/
        /*if(getEnquiry_id()==null)
        {
            IO.log(getClass().getName(), IO.TAG_ERROR, "invalid enquiry_id value.");
            return false;
        }*/
        if(getRequest()==null)
            return new String[]{"false", "invalid request value."};
        if(getContact_person_id()==null)
            return new String[]{"false", "invalid contact_person_id value."};
        if(getCreator()==null)
            return new String[]{"false", "invalid creator value."};
        if(getAccount_name()==null)
            return new String[]{"false", "invalid account_name value."};
        if(getVat()<0)
            return new String[]{"false", "invalid VAT value."};
        if(getStatus()<0)
            return new String[]{"false", "invalid status value."};
        if(getRevision()<0)
            return new String[]{"false", "invalid revision value."};

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
                case "enquiry_id":
                    enquiry_id = (String)val;
                case "client_id":
                    client_id = (String)val;
                    break;
                case "contact_person_id":
                    contact_person_id = (String)val;
                    break;
                case "request":
                    request = String.valueOf(val);
                    break;
                case "status":
                    status = Integer.parseInt(String.valueOf(val));
                    break;
                case "parent_id":
                    parent_id = String.valueOf(val);
                    break;
                case "revision":
                    revision = Integer.parseInt(String.valueOf(val));
                    break;
                case "vat":
                    vat = Double.parseDouble(String.valueOf(val));
                    break;
                case "account_name":
                    account_name = String.valueOf(val);
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
            case "enquiry_id":
                return enquiry_id;
            case "client_id":
                return client_id;
            case "contact_person_id":
                return contact_person_id;
            case "request":
                return request;
            case "status":
                return status;
            case "parent_id":
                return parent_id;
            case "vat":
                return vat;
            case "account_name":
                return account_name;
            case "revision":
                return revision;
        }
        return super.get(var);
    }

    @Override
    public String toString()
    {
        return get_id();
    }

    @Override
    public String apiEndpoint()
    {
        return "/quotes";
    }
}
