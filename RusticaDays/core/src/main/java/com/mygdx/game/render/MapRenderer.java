package com.mygdx.game.render;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class MapRenderer {
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private MapProperties props;
    public MapRenderer(String mapPath) {
        // Load the map
        map = new TmxMapLoader().load(mapPath);
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        props = map.getProperties();
    }

    public void render(OrthographicCamera camera) {
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
    }
    public float getMapWidth() {
        int mapWidth = props.get("width", Integer.class) * props.get("tilewidth", Integer.class);
        return mapWidth;
    }
    public float getMapHeight() {
        int mapHeight = props.get("height", Integer.class) * props.get("tileheight", Integer.class);
        return mapHeight;
    }
    public TiledMap getMap() {
        return map;
    }
}
