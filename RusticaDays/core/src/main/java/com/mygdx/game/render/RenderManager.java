package com.mygdx.game.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.entities.GameObject;
import com.mygdx.game.entities.Player;
import com.mygdx.game.entities.plants.base.Plant;

import java.util.Comparator;

/**
 * Quản lý việc sắp xếp và render các đối tượng trong game
 * dựa theo độ sâu (depth) để tạo hiệu ứng 2.5D.
 */
public class RenderManager {
    private Array<GameObject> renderObjects;
    private Comparator<GameObject> depthComparator;

    public RenderManager() {
        renderObjects = new Array<>();
        depthComparator = (o1, o2) -> Float.compare(o2.getDepth(), o1.getDepth());
    }
    public void add(GameObject object) {
        if (!renderObjects.contains(object, true)) {
            renderObjects.add(object);
        }
    }
    public void remove(GameObject object) {
        renderObjects.removeValue(object, true);
    }

    public void clear() {
        renderObjects.clear();
    }
    public void render(SpriteBatch batch) {
        renderObjects.sort(depthComparator);

        for (GameObject object : renderObjects) {
            object.render(batch);
        }
    }
}
