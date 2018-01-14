package server.model;

import server.auxilary.IO;

/**
 * Created by ghost on 2017/01/21.
 */
public class Expense extends MVGObject
{
    private String expense_title;
    private String expense_description;
    private double expense_value;
    private String supplier;
    private String account;
    public static final String TAG = "Expense";

    public String getSupplier()
    {
        return supplier;
    }

    public void setSupplier(String supplier)
    {
        this.supplier = supplier;
    }

    public String getExpense_title()
    {
        return expense_title;
    }

    public void setExpense_title(String expense_title)
    {
        this.expense_title = expense_title;
    }

    public String getExpense_description()
    {
        return expense_description;
    }

    public void setExpense_description(String expense_description)
    {
        this.expense_description = expense_description;
    }

    public double getExpense_value()
    {
        return expense_value;
    }

    public void setExpense_value(double expense_value)
    {
        this.expense_value = expense_value;
    }

    public String getAccount()
    {
        return account;
    }

    public void setAccount(String account)
    {
        this.account = account;
    }

    @Override
    public String[] isValid()
    {
        if(getExpense_title()==null)
            return new String[]{"false", "invalid title value."};
        if(getExpense_description()==null)
            return new String[]{"false", "invalid expense_description value."};
        if(getExpense_value()<=0)
            return new String[]{"false", "invalid expense_value value."};
        if(getAccount()==null)
            return new String[]{"false", "invalid account value."};
        if(getSupplier()==null)
            return new String[]{"false", "invalid supplier value."};

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
                case "expense_title":
                    expense_title = (String)val;
                    break;
                case "expense_description":
                    expense_description = (String)val;
                    break;
                case "expense_value":
                    expense_value = Double.parseDouble(String.valueOf(val));
                    break;
                case "supplier":
                    supplier = (String)val;
                    break;
                case "account":
                    account = String.valueOf(val);
                    break;
                default:
                    IO.log(getClass().getName(), IO.TAG_ERROR,"unknown "+getClass().getName()+" attribute '" + var + "'.");
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
                case "expense_title":
                    return expense_title;
                case "expense_description":
                    return expense_description;
                case "expense_value":
                    return expense_value;
                case "supplier":
                    return supplier;
                case "account":
                    return account;
                default:
                    IO.log(getClass().getName(), IO.TAG_ERROR,"unknown "+getClass().getName()+" attribute '" + var + "'.");
                    return null;
            }
        } else return val;
    }

    @Override
    public String toString()
    {
        return this.expense_title;
    }

    @Override
    public String apiEndpoint()
    {
        return "/expenses";
    }
}
