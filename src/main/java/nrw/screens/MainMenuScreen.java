package nrw.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import nrw.core.NRWGame;
import nrw.helpers.Settings;
import nrw.helpers.TiledMapHelper;
import nrw.helpers.localization.UIStrings;
import nrw.helpers.masters.ImageMaster;
import nrw.helpers.masters.MapMaster;
import nrw.ui.MainMenuButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainMenuScreen extends AbstractScreen {
    private static final Logger logger = LoggerFactory.getLogger(MainMenuScreen.class);

    private static UIStrings uiStrings;
    private TiledMap menuMap = null;
    private int mapHeight;
    private MapLayer triggerLayer;
    private int[] menuMapGround;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera = null;
    private final List<Vector3> cameraPan;
    private int cameraTarget;
    private final Vector3 cameraVelocity;

    private Image title = null;
    private MainMenuButton singleGame;

    private static final float PADDING_Y = 100.0F * Settings.scale;

    public MainMenuScreen() {
        cameraPan = new ArrayList<>();
        cameraVelocity = new Vector3();
    }

    @Override
    public void show() {
        if (title == null) {
            title = new Image(ImageMaster.GAME_TITLE);
        }
        if (uiStrings == null) {
            uiStrings = NRWGame.stringLibrary.getUIString("menu");
            initializeButton();
        }
        padLayout();
        if (camera == null) {
            camera = new OrthographicCamera();
        }
        camera.setToOrtho(false, NRWGame.game.uiView.getScreenWidth(), NRWGame.game.uiView.getScreenHeight());
        if (menuMap == null) {
            // todo 随机化选取菜单地图
            menuMap = MapMaster.getMap("menu1");
        }
        initRenderMap();

    }

    @Override
    public void hide() {
        menuMap = null;
        renderer = null;
    }

    private void padLayout() {
        NRWGame.game.table.add(title).width(title.getWidth()).height(title.getHeight());
        NRWGame.game.table.row();
        NRWGame.game.table.add(singleGame).height(PADDING_Y);
    }

    private void initializeButton() {
        singleGame = new MainMenuButton(uiStrings.TEXT[0], () -> {
            NRWGame.game.setScreen(new SingleGameScreen(MapMaster.getMap("menu1")));
            return false;
        });
    }

    private void updateGround() {
        float delta = Gdx.graphics.getDeltaTime();
        // move camera
        if (cameraPan.isEmpty()) {
            return;
        }
        camera.translate(new Vector3(cameraVelocity).scl(delta));
        if (camera.position.dst2(cameraPan.get(cameraTarget)) < 25.0F) {
            camera.position.set(cameraPan.get(cameraTarget));
            // to next position
            cameraTarget += 1;
            if (cameraTarget >= cameraPan.size()) {
                cameraTarget = 0;
            }
            updateCameraVelocity();
        }

    }

    @Override
    public void renderGround(SpriteBatch sb) {
        if (renderer == null) {
            return;
        }
        updateGround();
        camera.update();
        sb.setProjectionMatrix(camera.combined);
        renderer.setView(camera);
        renderer.render(menuMapGround);
    }

    @Override
    public void resize(int width, int height) {
        // camera.setToOrtho 会改坐标, 坑啊
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
        logger.debug("Camera center: x: {}, y: {}", camera.position.x, camera.position.y);
    }

    private void initRenderMap() {
        if (menuMap == null) {
            logger.error("WTF? Menu Map is NULL!");
            renderer = null;
            return;
        }
        menuMapGround = new int[]{menuMap.getLayers().getIndex("Ground")};
        triggerLayer = menuMap.getLayers().get("Triggers");
        initCamaraLoop();
        // 需要为每个地图新建,因为 "The renderer will only ever be able to render the map you pass to it in the constructor."
        renderer = new OrthogonalTiledMapRenderer(menuMap, 1.0F);
        renderer.setView(camera);

    }

    private void initCamaraLoop() {
        HashMap<Integer, MapObject> tmpMap = new HashMap<>();
        for (MapObject object: triggerLayer.getObjects()) {
            // Fk, 开始瞎写了, 算法见鬼去吧
            MapProperties properties = object.getProperties();
            if (properties.containsKey("type") && ((String) properties.get("type")).equals("camera_pan")) {
                if (!properties.containsKey("index")) {
                    logger.warn("Invalid map object: No index for type 'camera_pan', object id: {}", properties.get("id"));
                    continue;
                }
                tmpMap.put(Integer.parseInt((String) properties.get("index")), object);
            }
        }
        int i = 0;
        cameraPan.clear();
        while (tmpMap.containsKey(i)) {
            if (!(tmpMap.get(i) instanceof RectangleMapObject)) {
                logger.warn("'camera_pan' object should be RectangleMapObject.");
                break;
            }
            Vector3 tmp = TiledMapHelper.getRectPosition((RectangleMapObject) tmpMap.get(i));
            cameraPan.add(tmp);
            logger.debug("Add camera pan {}: x: {}, y: {}", i, tmp.x, tmp.y);
            i++;
        }
        // set camera -> camera_start
        cameraTarget = 0;
        MapObject camera_start = triggerLayer.getObjects().get("camera_start");
        if (!(camera_start instanceof RectangleMapObject)) {
            logger.warn("'camera_start' object should be RectangleMapObject.");
            camera.position.set(0.0F, 0.0F, 0.0F);
        } else {
            Vector3 pos = TiledMapHelper.getRectPosition((RectangleMapObject) camera_start);
            logger.debug("Set camera center: x: {}, y: {}", pos.x, pos.y);
            camera.position.set(pos);
        }
        camera.update();
        updateCameraVelocity();

    }

    private void updateCameraVelocity() {
        if (cameraPan.isEmpty()) {
            return;
        }
        cameraVelocity.set(cameraPan.get(cameraTarget)).sub(camera.position).setLength2(1000.0F);
//        cameraVelocity.y *= -1.0F;  // yDown is positive
        logger.debug("Set camera velocity: {}, {}, {}", cameraVelocity.x, cameraVelocity.y, cameraVelocity.z);
    }

}
