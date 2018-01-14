package server.model;

/**
 * Created by ghost on 2017/01/21.
 */
public class PurchaseOrderAsset extends PurchaseOrderItem
{
    public static final String TAG = "PurchaseOrderAsset";

    @Override
    public String apiEndpoint()
    {
        return "/purchaseorders/assets";
    }
}