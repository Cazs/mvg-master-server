package server.model;

import server.auxilary.IO;

/**
 * Created by ghost on 2017/01/21.
 */
public class QuoteItem extends MVGObject
{
    private int item_number;
    private int quantity;
    private double unit_cost;
    private double markup;
    private String additional_costs;
    private String quote_id;
    private String resource_id;
    public static final String TAG = "QuoteItem";

    public int getItem_number()
    {
        return item_number;
    }

    public void setItem_number(int item_number)
    {
        this.item_number = item_number;
    }

    public String getQuote_id()
    {
        return quote_id;
    }

    public void setQuote_id(String quote_id)
    {
        this.quote_id = quote_id;
    }

    public String getResource_id()
    {
        return resource_id;
    }

    public void setResource_id(String resource_id)
    {
        this.resource_id = resource_id;
    }

    public String getAdditional_costs()
    {
        return additional_costs;
    }

    public void setAdditional_costs(String additional_costs)
    {
        this.additional_costs = additional_costs;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public String getUnit_cost()
    {
        return String.valueOf(getUnitCost());
    }

    public double getUnitCost()
    {
        return unit_cost;
    }

    public void setUnit_cost(double unit_cost)
    {
        this.unit_cost = unit_cost;
    }

    public double getMarkup(){return this.markup;}

    public void setMarkup(double markup){this.markup=markup;}

    public double getRate()
    {
        //double marked_up = getUnitCost() + getUnitCost()*(markup/100);
        double total = 0;//getUnitCost();

        //check additional costs
        if (getAdditional_costs() != null)
        {
            if (!getAdditional_costs().isEmpty())
            {
                //compute additional costs for each Quote Item
                if(getAdditional_costs().contains(";"))//check cost delimiter
                {
                    String[] costs = getAdditional_costs().split(";");
                    for (String str_cost : costs)
                    {
                        if (str_cost.contains("="))
                        {
                            //retrieve cost and markup
                            String add_cost = str_cost.split("=")[1];//the cost value is [1] (the cost name is [0])

                            double cost,add_cost_markup=0;
                            if(add_cost.contains("*"))//if(in the form cost*markup)
                            {
                                cost = Double.parseDouble(add_cost.split("\\*")[0]);
                                add_cost_markup = Double.parseDouble(add_cost.split("\\*")[1]);
                            }else cost = Double.parseDouble(add_cost);

                            //add marked up additional cost to total
                            total += cost + cost*(add_cost_markup/100);
                        } else IO.log(getClass().getName(), IO.TAG_ERROR, "invalid Quote Item additional cost.");
                    }
                } else if (getAdditional_costs().contains("="))//if only one additional cost
                {
                    double cost,add_cost_markup=0;
                    //get cost and markup
                    if(getAdditional_costs().split("=")[1].contains("*"))
                    {
                        cost = Double.parseDouble(getAdditional_costs().split("=")[1].split("\\*")[0]);
                        add_cost_markup = Double.parseDouble(getAdditional_costs().split("=")[1].split("\\*")[1]);
                    }else cost = Double.parseDouble(getAdditional_costs().split("=")[1]);
                    //add marked up additional cost to total
                    total += cost + cost*(add_cost_markup/100);
                } else IO.log(getClass().getName(), IO.TAG_WARN, getClass().getName()+" has no additional costs.");
            } else IO.log(getClass().getName(), IO.TAG_WARN, getClass().getName()+" has no additional costs.");
        }
        return total;
    }

    public double getTotal()
    {
        return getRate()*getQuantity();
    }

    @Override
    public String[] isValid()
    {
        if(getResource_id()==null)
            return new String[]{"false", "invalid resource_id value."};
        if(getItem_number()<0)
            return new String[]{"false", "invalid item_number value."};
        if(getQuote_id()==null)
            return new String[]{"false", "invalid quote_id value."};
        if(getUnitCost()<0)
            return new String[]{"false", "invalid unit_cost value."};
        if(getQuantity()<=0)
            return new String[]{"false", "invalid quantity value."};
        if(getMarkup()<0)
            return new String[]{"false", "invalid markup value."};

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
                case "quote_id":
                    setQuote_id(String.valueOf(val));
                    break;
                case "resource_id":
                    setResource_id(String.valueOf(val));
                    break;
                case "item_number":
                    setItem_number(Integer.valueOf((String)val));
                    break;
                case "additional_costs":
                    setAdditional_costs((String)val);
                    break;
                case "quantity":
                    setQuantity(Integer.valueOf((String)val));
                    break;
                case "unit_cost":
                    setUnit_cost(Double.valueOf((String)val));
                    break;
                case "markup":
                    setMarkup(Double.parseDouble((String) val));
                    break;
                default:
                    IO.log(getClass().getName(), IO.TAG_ERROR, "Unknown "+getClass().getName()+" attribute '" + var + "'.");
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
        switch (var.toLowerCase())
        {
            case "_id":
                return get_id();
            case "quote_id":
                return getQuote_id();
            case "resource_id":
                return getResource_id();
            case "item_number":
                return getItem_number();
            case "additional_costs":
                return getAdditional_costs();
            case "unit_cost":
                return getUnitCost();
            case "markup":
                return getMarkup();
        }
        return super.get(var);
    }

    @Override
    public String apiEndpoint()
    {
        return "/quotes/resources";
    }
}