package nrw.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import nrw.core.NRWGame;
import nrw.helpers.FontHelper;
import nrw.helpers.Settings;
import nrw.helpers.masters.AssetLoader;

public class LoadingScreen extends AbstractScreen {

    private final AssetLoader loader;

    public LoadingScreen() {
        loader = new AssetLoader();
        NRWGame.loading_done = false;
    }

    @Override
    public void renderUI(SpriteBatch sb) {
        update();
        float w = NRWGame.game.uiView.getScreenWidth();
        float h = NRWGame.game.uiView.getScreenHeight();
        sb.draw(AssetLoader.GAME_LOGO,
                0.5F * w - 0.5F * AssetLoader.GAME_LOGO.getWidth(), 0.5F * h - 0.5F * AssetLoader.GAME_LOGO.getHeight());
        FontHelper.LOADING_TEXT1.draw(sb, "Loading... " + loader.getProgress(), 0, 0.2F * h, w, Align.center, false);
        FontHelper.LOADING_TEXT2.draw(sb, loader.getPhase(), 0, 0.2F * h - 30.0F * Settings.scale, w, Align.center, false);

    }

    private void update() {
        if (!loader.done()) {
            loader.update();
        } else {
            NRWGame.game.setScreen(NRWGame.menuScreen);
        }
    }

    @Override
    public void hide() {
        NRWGame.loader = loader;
        NRWGame.loading_done = true;
    }

}
