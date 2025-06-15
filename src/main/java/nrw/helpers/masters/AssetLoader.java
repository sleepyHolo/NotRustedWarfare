package nrw.helpers.masters;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.StringBuilder;
import com.google.gson.reflect.TypeToken;
import nrw.core.NRWGame;
import nrw.helpers.FontHelper;
import nrw.helpers.Settings;
import nrw.helpers.localization.UIStrings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AssetLoader implements Disposable {
    private static final Logger logger = LoggerFactory.getLogger(AssetLoader.class);

    private static final List<AssetMaster> fontMasters;
    private static final List<AssetMaster> uiMasters;
    private static final List<AssetMaster> textureMasters;
    private static final List<AssetMaster> mapMasters;
    static {
        fontMasters = new ArrayList<>();
        uiMasters = new ArrayList<>();
        textureMasters = new ArrayList<>();
        mapMasters = new ArrayList<>();
    }

    private final AssetManager manager;
    private final InternalFileHandleResolver resolver;
    public static Texture GAME_LOGO;
    public boolean isInitialized;
    private boolean isInitializedFont;
    private final StringBuilder sb;
    private int loaded;
    private final ArrayList<Integer> flags;
    private boolean isDone;
    private boolean postLocalization = false;
    private boolean postFont = false;
    private boolean postTexture = false;
    private boolean postMap = false;
    private long time;

    public AssetLoader() {
        manager = new AssetManager();
        resolver = new InternalFileHandleResolver();
        manager.load("image/ui/logo.png", Texture.class);
        manager.finishLoading();
        GAME_LOGO = manager.get("image/ui/logo.png", Texture.class);
        // Here assert manager.getLoadedAssets() == 1

        isInitialized = false;
        isInitializedFont = false;
        isDone = false;
        sb = new StringBuilder();
        flags = new ArrayList<>();
        time = System.currentTimeMillis();
    }

    private void setLoaders() {
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(UIStrings.class, new GsonLoader<>(resolver,
                new TypeToken<Map<String, UIStrings>>() {}.getType()));
        manager.setLoader(TiledMap.class, new TmxMapLoader(resolver));
    }

    private void initialize() {
        // FreeTypeFonts
        for (AssetMaster m: fontMasters) {
            for (String path: m.getAssetPath()) {
                manager.load(path, FreeTypeFontGenerator.class);
            }
        }
        flags.add(manager.getQueuedAssets());
        logger.info("Font Files: {}", flags.get(0));
        // Localization
        for (AssetMaster m: uiMasters) {
            for (String path: m.getAssetPath()) {
                manager.load(path, UIStrings.class);
            }
        }
        flags.add(manager.getQueuedAssets());
        logger.info("Localization Files: {}", flags.get(1) - flags.get(0));
        // Texture
        for (AssetMaster m: textureMasters) {
            for (String path: m.getAssetPath()) {
                manager.load(path, Texture.class);
            }
        }
        flags.add(manager.getQueuedAssets());
        logger.info("Texture Files: {}", flags.get(2) - flags.get(1));
        // Map
        initialize_map();

        isInitialized = true;
    }

    private void initialize_map() {
        TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
        TmxMapLoader loader = (TmxMapLoader) manager.getLoader(TiledMap.class);
        // 依赖项(.tsx/.png)
        for (AssetMaster m: mapMasters) {
            for (String path: m.getAssetPath()) {
                Array<AssetDescriptor> dependencies = loader.getDependencies(path, resolver.resolve(path), params);
                for (AssetDescriptor dep : dependencies) {
                    if (!manager.contains(dep.fileName)) {
                        manager.load(dep);
                    }
                }
            }

        }
        flags.add(manager.getQueuedAssets());
        logger.info("Map Dependency Files: {}", flags.get(3) - flags.get(2));
        // .tmx 需要保证所有的依赖都完成
        for (AssetMaster m: mapMasters) {
            for (String path: m.getAssetPath()) {
                manager.load(path, TiledMap.class);
            }
        }
        flags.add(manager.getQueuedAssets());
        logger.info("Tiled Map Files: {}", flags.get(4) - flags.get(3));

    }

    public void update() {
        if (!isInitializedFont) {
            setLoaders();
            manager.load("font/VictorMono-Regular.ttf", FreeTypeFontGenerator.class);
            manager.finishLoading();
            FontHelper.initLoadingText(manager.get("font/VictorMono-Regular.ttf", FreeTypeFontGenerator.class));
            isInitializedFont = true;
            return;
        }
        if (!isInitialized) {
            initialize();
        }
        if (manager.isFinished()) {
            postSetup();
        } else {
            manager.update(16);
        }
    }

    private void postSetup() {
        if (!postLocalization) {
            for (AssetMaster m: uiMasters) {
                m.postLoad(manager);
            }
            postLocalization = true;
            return;
        }
        if (Settings.usingLang == Settings.lang.zhs && FontHelper.usingUnicode == null) {
            // 需要根据文本设计需要使用的字符
            NRWGame.stringLibrary.buildUnicodeSet();
            return;
        }
        if (!postFont) {
            for (AssetMaster m : fontMasters) {
                m.postLoad(manager);
            }
            postFont = true;
            return;
        }
        if (!postTexture) {
            for (AssetMaster m : textureMasters) {
                m.postLoad(manager);
            }
            postTexture = true;
            return;
        }
        if (!postMap) {
            for (AssetMaster m : mapMasters) {
                m.postLoad(manager);
            }
            postMap = true;
            return;
        }
        isDone = true;
        logger.info("Loading Time: {} ms.", System.currentTimeMillis() - time);
    }

    public boolean done() {
        return isDone;
    }

    public String getPhase() {
        if (!isInitialized) {
            return "Initializing Game";
        } else if (loaded < flags.get(0)) {
            return "Loading TrueTypeFont - " + loaded;
        } else if (loaded < flags.get(1)) {
            return "Loading Localization - " + (loaded - flags.get(0));
        } else if (loaded < flags.get(2)) {
            return "Loading Texture - " + (loaded - flags.get(1));
        } else if (loaded < flags.get(3)) {
            return "Loading Map Dependencies - " + (loaded - flags.get(2));
        } else if (loaded < flags.get(4)) {
            return "Loading Map - " + (loaded - flags.get(3));
        }
        if (Settings.usingLang == Settings.lang.zhs && FontHelper.usingUnicode == null) {
            return "Building Unicode Set For Chinese...";
        }
        return "Last Setup";
    }

    public String getProgress() {
        loaded = manager.getLoadedAssets() - 2;
        int queued = manager.getQueuedAssets();
        if (loaded + queued == 0) {
            return "";
        }
        sb.append("(").append(loaded).append("/").append(loaded + queued).append(")");
        return sb.toStringAndClear();
    }

    public static <T> void registerMaster(AssetMaster master, Class<T> type) {
        if (type == FreeTypeFontGenerator.class) {
            fontMasters.add(master);
            return;
        }
        if (type == UIStrings.class) {
            uiMasters.add(master);
            return;
        }
        if (type == Texture.class) {
            textureMasters.add(master);
            return;
        }
        if (type == TiledMap.class) {
            mapMasters.add(master);
            return;
        }
        throw new RuntimeException("Invalid AssetMaster type: " + type.toString());
    }

    @Override
    public void dispose() {
        try {
            manager.dispose();
        } catch (Exception e) {
            logger.error("Disposing failed: AssetManager", e);
        }
    }

}
