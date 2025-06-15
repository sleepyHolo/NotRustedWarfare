package nrw.starters;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import nrw.core.Main;
import nrw.core.NRWGame;
import nrw.helpers.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Lwjgl3Launcher {
    private static final Logger logger = LoggerFactory.getLogger(Lwjgl3Launcher.class);

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        try {
            NRWGame.game = new Main();
            new Lwjgl3Application(NRWGame.game, getDefaultConfig());
        } catch (Exception e) {
            logger.error("Game crashed.", e);
            NRWGame.game.dispose();
        }
        logger.info("==== ==== ==== ==== ====");
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfig() {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Not Rusted Warfare");
        config.useVsync(true);
        config.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate);
        config.setWindowedMode(Settings.width, Settings.height);
        config.setWindowIcon(
                "image/icon/window128.png", "image/icon/window32.png",
                "image/icon/window24.png", "image/icon/window16.png"
                );
        return config;
    }

}
