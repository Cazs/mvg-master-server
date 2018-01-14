package server.model;

/**
 * Created by ghost on 2017/02/01.
 */
public class AssetType extends Type
{
    @Override
    public String apiEndpoint()
    {
        return "/assets/types";
    }
}
