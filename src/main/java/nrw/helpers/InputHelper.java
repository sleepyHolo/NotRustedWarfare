package nrw.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;

public class InputHelper {

    // camera
    public static int LEFT = Input.Keys.LEFT;
    public static int RIGHT = Input.Keys.RIGHT;
    public static int UP = Input.Keys.UP;
    public static int DOWN = Input.Keys.DOWN;

    public static boolean pressedLeft() {
        return Gdx.input.isKeyPressed(LEFT);
    }

    public static boolean pressedRight() {
        return Gdx.input.isKeyPressed(RIGHT);
    }

    public static boolean pressedUp() {
        return Gdx.input.isKeyPressed(UP);
    }

    public static boolean pressedDown() {
        return Gdx.input.isKeyPressed(DOWN);
    }

    public static boolean pressedZoomPlus() {
        return Gdx.input.isKeyPressed(Input.Keys.EQUALS) || Gdx.input.isKeyPressed(Input.Keys.NUMPAD_ADD);
    }

    public static boolean pressedZoomMinus() {
        return Gdx.input.isKeyPressed(Input.Keys.MINUS) || Gdx.input.isKeyPressed(Input.Keys.NUMPAD_SUBTRACT);
    }

    public static Vector3 buildSpeed(float speed) {
        float dL = Gdx.graphics.getDeltaTime() * speed;
        return new Vector3(dL * (b2f(pressedRight()) - b2f(pressedLeft())), dL * (b2f(pressedUp()) - b2f(pressedDown())), 0.0F);
    }

    private static float b2f(boolean b) {
        return b ? 1.0F : 0.0F;
    }

}
