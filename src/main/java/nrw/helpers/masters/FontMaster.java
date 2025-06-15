package nrw.helpers.masters;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import nrw.helpers.FontHelper;
import nrw.helpers.Settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FontMaster implements AssetMaster {

    public static HashMap<String, String> fontPathMap;
    public static HashMap<String, FreeTypeFontGenerator> fontMap;
    static {
        fontPathMap = new HashMap<>();
        fontMap = new HashMap<>();
    }

    public FontMaster() {
        initMap();
        AssetLoader.registerMaster(this, FreeTypeFontGenerator.class);
    }

    protected static void initMap() {
        switch (Settings.usingLang) {
            case zhs: {
                fontPathMap.put("NotoSans-Bold", "font/NotoSansSC-Bold.ttf");
                return;
            }
            default: {
                // eng
                fontPathMap.put("NotoSans-Bold", "font/NotoSans-Bold.ttf");
            }
        }
    }

    @Override
    public List<String> getAssetPath() {
        return new ArrayList<>(fontPathMap.values());
    }

    @Override
    public void postLoad(AssetManager manager) {
        for (Map.Entry<String, String> entry: fontPathMap.entrySet()) {
            fontMap.put(entry.getKey(), manager.get(entry.getValue(), FreeTypeFontGenerator.class));
        }
        FontHelper.initialize();
    }

}
