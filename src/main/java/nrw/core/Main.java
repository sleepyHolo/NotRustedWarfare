package nrw.core;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import nrw.helpers.processor.GameInputProcessor;
import nrw.helpers.localization.LocalizedStrings;
import nrw.helpers.masters.FontMaster;
import nrw.helpers.masters.ImageMaster;
import nrw.helpers.masters.MapMaster;
import nrw.screens.AbstractScreen;
import nrw.screens.LoadingScreen;
import nrw.screens.MainMenuScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ApplicationListener {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public ScreenViewport uiView;
    public Stage stage;
    public Table table;

    private SpriteBatch sb;
    private AbstractScreen screen = null;

    @Override
    public void create() {
        logger.info("NRW created.");
        sb = new SpriteBatch();
        uiView = new ScreenViewport();

        stage = new Stage(uiView);
        Gdx.input.setInputProcessor(stage);
        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        table.columnDefaults(0);

        // 启用的话会导致有些字显示不了,包括ascii字符.不清楚原因.
//        table.setDebug(true);

        this.setScreen(new LoadingScreen());
        this.initialize();
    }

    private void initialize() {
        new FontMaster();
        new ImageMaster();
        new MapMaster();
        NRWGame.stringLibrary = new LocalizedStrings();
        NRWGame.menuScreen = new MainMenuScreen();

        NRWGame.multiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(NRWGame.multiplexer);
        NRWGame.multiplexer.addProcessor(stage);
    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.BLACK);
        sb.begin();
        if (this.screen != null) {
            this.screen.renderGround(sb);
        }
        uiView.apply();
        sb.setProjectionMatrix(uiView.getCamera().combined);
        if (this.screen != null) {
            this.screen.renderUI(sb);
        }
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        sb.end();

    }

    @Override
    public void resize(int width, int height) {
        uiView.update(width, height, true);
        if (this.screen != null) {
            this.screen.resize(width, height);
        }

    }

    @Override
    public void dispose() {
        logger.info("Disposing...");
        try {
            stage.dispose();
            if (this.screen != null) {
                this.screen.hide();
            }
            if (sb != null) {
                sb.dispose();
            }
            NRWGame.dispose();
        } catch (Exception e) {
            logger.error("Disposing error.", e);
        }

    }

    public void setScreen(AbstractScreen screen) {
        table.clear();
        if (this.screen != null) {
            this.screen.hide();
        }

        this.screen = screen;
        if (this.screen != null) {
            this.screen.show();
            this.screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

    }

    @Override
    public void pause() {
        if (this.screen != null) {
            this.screen.pause();
        }

    }

    @Override
    public void resume() {
        if (this.screen != null) {
            this.screen.resume();
        }

    }

}
