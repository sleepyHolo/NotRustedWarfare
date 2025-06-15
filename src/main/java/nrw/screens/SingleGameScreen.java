package nrw.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import nrw.worlds.AbstractWorld;
import nrw.worlds.Normal;

public class SingleGameScreen extends AbstractScreen {

    private AbstractWorld world;

    public SingleGameScreen(TiledMap map) {
        world = new Normal(map);

    }

    @Override
    public void show() {

    }

    @Override
    public void renderGround(SpriteBatch sb) {
        world.update();
        world.renderGround(sb);
    }

    @Override
    public void renderUI(SpriteBatch sb) {
        world.renderUI(sb);
    }

    @Override
    public void resize(int width, int height) {
        world.resize(width, height);
    }

    @Override
    public void dispose() {
        world.dispose();
    }

}
