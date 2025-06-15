package nrw.helpers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import nrw.helpers.masters.FontMaster;

public class FontHelper {

    public static boolean isInitialized = false;
    public static BitmapFont LOADING_TEXT1;
    public static BitmapFont LOADING_TEXT2;
    public static BitmapFont MENU_TEXT;
    public static BitmapFont MENU_TEXT_HOVER;
    public static String usingUnicode = null;

    // text button style
    public static TextButtonStyle MENU_BUTTON = new TextButtonStyle();
    public static TextButtonStyle MENU_BUTTON_HOVER = new TextButtonStyle();

    public static void initLoadingText(FreeTypeFontGenerator textGen) {
        FreeTypeFontParameter param = new FreeTypeFontParameter();
        param.characters = FreeTypeFontGenerator.DEFAULT_CHARS;
        param.size = (int) (18 * Settings.scale);
        param.color = Color.WHITE;
        LOADING_TEXT1 = textGen.generateFont(param);
        param.size = (int) (18 * Settings.scale);
        param.color = Color.GRAY;
        LOADING_TEXT2 = textGen.generateFont(param);

    }

    public static void initialize() {
        if (isInitialized) {
            return;
        }

        FreeTypeFontParameter param = new FreeTypeFontParameter();
        if (usingUnicode != null) {
            param.characters = usingUnicode;
        }
        // MENU_TEXT
        FreeTypeFontGenerator tmp = FontMaster.fontMap.get("NotoSans-Bold");
        param.size = (int) (32 * Settings.scale);
        param.color = Color.BLACK;
        param.borderWidth = 2.0F * Settings.scale;
        param.borderColor = Color.WHITE;
        param.shadowOffsetX = (int) (4 * Settings.scale);
        param.shadowOffsetY = (int) (6 * Settings.scale);
        param.shadowColor = Color.GRAY;
        MENU_TEXT = tmp.generateFont(param);
        param.color = Color.RED;
        MENU_TEXT_HOVER = tmp.generateFont(param);


        initializeStyle();
        isInitialized = true;
    }

    private static void initializeStyle() {
        // MENU_BUTTON
        MENU_BUTTON.font = MENU_TEXT;
        MENU_BUTTON_HOVER.font = MENU_TEXT_HOVER;

    }

    public static void dispose() {
        MENU_TEXT.dispose();
    }

}
