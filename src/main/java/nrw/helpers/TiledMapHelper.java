package nrw.helpers;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class TiledMapHelper {
    public static final int TILED_SIZE;
    private static int mapHeight = 0;

    public static void setMap(MapProperties properties) {
        mapHeight = properties.get("height", Integer.class) * TILED_SIZE;
    }

    public static Vector3 getRectPosition(RectangleMapObject object) {
        Rectangle rect = object.getRectangle();
        return new Vector3(rect.getX(), rect.getY(), 0.0F);
    }

    static {
        TILED_SIZE = 20;
    }

}
