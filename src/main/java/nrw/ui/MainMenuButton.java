package nrw.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import nrw.helpers.FontHelper;

import java.util.function.Supplier;

public class MainMenuButton extends TextButton {

    public MainMenuButton(String text, Supplier<Boolean> onClick) {
        super(text, FontHelper.MENU_BUTTON);
        addListener(new Listener(onClick));
    }

    public MainMenuButton(String text) {
        this(text, () -> false);
    }

    private class Listener extends InputListener {

        Supplier<Boolean> onClick;

        public Listener(Supplier<Boolean> onClick) {
            this.onClick = onClick;
        }

        @Override
        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
            setStyle(FontHelper.MENU_BUTTON_HOVER);
        }
        @Override
        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
            setStyle(FontHelper.MENU_BUTTON);
        }
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            return onClick.get();
        }

    }
}
