package nrw.worlds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import nrw.core.NRWGame;
import nrw.helpers.FontHelper;
import nrw.helpers.InputHelper;
import nrw.helpers.Settings;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AbstractWorld extends InputAdapter {

    private static final float CAMERA_SPEED = 1000.0F;
    private static final float MAP_GRID = 20.0F;

    private final TiledMap map;
    private final int[] mapGround;
    private final OrthogonalTiledMapRenderer renderer;
    private final float map_width;
    private final float map_height;

    // camera
    private final OrthographicCamera camera;
    private final Vector3 camera_velocity;
    private final Vector3 camera_mark;
    private final Vector3 camera_move;
    private float log_zoom;

    private float zoomTimer = 0.0F;

    public AbstractWorld(TiledMap map) {
        this.map = map;
        map_width = map.getProperties().get("width", Integer.class) * MAP_GRID;
        map_height = map.getProperties().get("height", Integer.class) * MAP_GRID;
        mapGround = new int[]{this.map.getLayers().getIndex("Ground")};

        camera = new OrthographicCamera();
        camera.setToOrtho(false, NRWGame.game.uiView.getScreenWidth(), NRWGame.game.uiView.getScreenHeight());
        camera_velocity = new Vector3(0.0F, 0.0F, 0.0F);
        camera_mark = new Vector3(0.0F, 0.0F, 0.0F);
        camera_move = new Vector3(0.0F, 0.0F, 0.0F);
        log_zoom = 0.0F;

        renderer = new OrthogonalTiledMapRenderer(this.map, 1.0F);
        renderer.setView(camera);

        NRWGame.multiplexer.addProcessor(this);

    }

    public void dispose() {
        NRWGame.multiplexer.removeProcessor(this);
        renderer.dispose();
    }

    public void update() {
        float dt = Gdx.graphics.getDeltaTime();
        update_camera(dt);
    }

    private void update_camera(float dt) {
        if (zoomTimer > 0.0F) {
            zoomTimer -= dt;
        }
        Vector3 tmp = new Vector3(camera_velocity.x * dt, camera_velocity.y * dt, 0.0F);
        if (!camera_move.isZero()) {
            tmp.add(camera_move.x, camera_move.y, 0.0F);
            camera_move.set(0.0F, 0.0F, 0.0F);
        }
        tmp.scl(camera.zoom);
        camera.position.add(tmp);
        camera.update();
    }

    public void renderGround(SpriteBatch sb) {
        if (renderer == null) {
            return;
        }
        sb.setProjectionMatrix(camera.combined);
        renderer.setView(camera);
        renderer.render(mapGround);

    }

    public void renderUI(SpriteBatch sb) {
        if (zoomTimer > 0.0F) {
            FontHelper.LOADING_TEXT1.draw(sb, "ZOOM: " + camera.zoom,
                    10.0F * Settings.scale, NRWGame.game.uiView.getScreenHeight() - 10.0F * Settings.scale);
        }
    }

    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override
    public boolean keyDown (int keycode) {
        if (keycode == InputHelper.LEFT) {
            camera_velocity.add(-CAMERA_SPEED, 0.0F, 0.0F);
        }
        if (keycode == InputHelper.RIGHT) {
            camera_velocity.add(CAMERA_SPEED, 0.0F, 0.0F);
        }
        if (keycode == InputHelper.UP) {
            camera_velocity.add(0.0F, CAMERA_SPEED, 0.0F);
        }
        if (keycode == InputHelper.DOWN) {
            camera_velocity.add(0.0F, -CAMERA_SPEED, 0.0F);
        }
        return false;
    }

    @Override
    public boolean keyUp (int keycode) {
        if (keycode == InputHelper.LEFT) {
            camera_velocity.add(CAMERA_SPEED, 0.0F, 0.0F);
        }
        if (keycode == InputHelper.RIGHT) {
            camera_velocity.add(-CAMERA_SPEED, 0.0F, 0.0F);
        }
        if (keycode == InputHelper.UP) {
            camera_velocity.add(0.0F, -CAMERA_SPEED, 0.0F);
        }
        if (keycode == InputHelper.DOWN) {
            camera_velocity.add(0.0F, CAMERA_SPEED, 0.0F);
        }
        return false;
    }

    @Override
    public boolean keyTyped (char character) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (amountY == 0.0F) {
            return false;
        }
        log_zoom += amountY * Gdx.graphics.getDeltaTime() * 16.0F;
        log_zoom = Math.max(log_zoom, -3.321928095F);   // log0.1
        BigDecimal bd = BigDecimal.valueOf(Math.pow(2.0, log_zoom));
        camera.zoom = bd.setScale(4, RoundingMode.HALF_UP).floatValue();
        zoomTimer = 1.0F;
        return false;
    }

    @Override
    public boolean mouseMoved (int x, int y) {
        return false;
    }

    @Override
    public boolean touchDown (int x, int y, int pointer, int button) {
        if (button == Input.Buttons.RIGHT) {
            camera_mark.set(x, y, 0.0F);
        }
        return false;
    }

    @Override
    public boolean touchUp (int x, int y, int pointer, int button) {
        if (button == Input.Buttons.RIGHT) {
            camera_mark.set(0.0F, 0.0F, 0.0F);
        }
        return false;
    }

    @Override
    public boolean touchDragged (int x, int y, int pointer) {
        if (!camera_mark.isZero()) {
            camera_move.set(x, y, 0.0F).sub(camera_mark);
            camera_move.x *= -1.0F;     // but why?
            camera_mark.set(x, y, 0.0F);
        }
        return false;
    }

}
