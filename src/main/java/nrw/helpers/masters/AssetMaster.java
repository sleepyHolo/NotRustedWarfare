package nrw.helpers.masters;

import com.badlogic.gdx.assets.AssetManager;

import java.util.List;

public interface AssetMaster {

    public List<String> getAssetPath();

    public void postLoad(AssetManager manager);

}
