package nrw.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import nrw.helpers.FontHelper;
import nrw.helpers.processor.GameInputProcessor;
import nrw.helpers.localization.LocalizedStrings;
import nrw.helpers.masters.AssetLoader;
import nrw.screens.MainMenuScreen;

public class NRWGame {

    public static Main game;
    public static boolean loading_done;
    public static AssetLoader loader;
    public static LocalizedStrings stringLibrary;

    public static MainMenuScreen menuScreen;
    public static InputMultiplexer multiplexer;

    public static void dispose() {
        loader.dispose();
        if (menuScreen != null) {
            menuScreen.dispose();
        }
        FontHelper.dispose();
    }

}
