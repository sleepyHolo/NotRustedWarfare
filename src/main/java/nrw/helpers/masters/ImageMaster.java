package nrw.helpers.masters;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageMaster implements AssetMaster {

    public static Texture GAME_TITLE;

    private static final HashMap<String, String> textureMap = new HashMap<>();

    public ImageMaster() {
        AssetLoader.registerMaster(this, Texture.class);
    }

    protected static void registerTexture(String key, String path) {
        textureMap.put(key, path);
    }

    @Override
    public List<String> getAssetPath() {
        return new ArrayList<>(textureMap.values());
    }

    @Override
    public void postLoad(AssetManager manager) {
        try {
            for (Map.Entry<String, String> entry: textureMap.entrySet()) {
                Field tmp = this.getClass().getDeclaredField(entry.getKey());
                tmp.set(null, manager.get(entry.getValue(), Texture.class));
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        registerTexture("GAME_TITLE", "image/ui/title.png");
    }

}
