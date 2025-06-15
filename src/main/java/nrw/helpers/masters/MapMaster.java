package nrw.helpers.masters;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.tiled.TiledMap;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapMaster implements AssetMaster {

    private static final HashMap<String, String> mapPathMap;
    private static final HashMap<String, TiledMap> mapMap;
    static {
        mapPathMap = new HashMap<>();
        mapMap = new HashMap<>();
        registerMap("menu1", "map/menu1.tmx");
    }

    public MapMaster() {
        AssetLoader.registerMaster(this, TiledMap.class);
    }

    @Nullable
    public static TiledMap getMap(String key) {
        return mapMap.getOrDefault(key, null);
    }

    public static void registerMap(String key, String path, boolean override) {
        if (override || !mapPathMap.containsKey(key)) {
            mapPathMap.put(key, path);
        }
    }

    public static void registerMap(String key, String path) {
        registerMap(key, path, false);
    }

    @Override
    public List<String> getAssetPath() {
        return new ArrayList<>(mapPathMap.values());
    }

    @Override
    public void postLoad(AssetManager manager) {
        for (Map.Entry<String, String> entry: mapPathMap.entrySet()) {
            mapMap.put(entry.getKey(), manager.get(entry.getValue(), TiledMap.class));
        }
    }
}
