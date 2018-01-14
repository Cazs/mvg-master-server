package server.model;

/**
 * Created by ghost on 2017/01/21.
 */
public class PurchaseOrderResource extends PurchaseOrderItem
{
    public static final String TAG = "PurchaseOrderResource";

    @Override
    public String apiEndpoint()
    {
        return "/purchaseorders/resources";
    }
}