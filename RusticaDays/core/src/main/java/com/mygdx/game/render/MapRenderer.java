package com.mygdx.game.render;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import java.util.ArrayList;

public class MapRenderer {
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private MapProperties props;
    private ArrayList<Integer> belowPlayerLayers;
    private ArrayList<Integer> abovePlayerLayers;

    public MapRenderer(String mapPath) {
        // Load the map
        map = new TmxMapLoader().load(mapPath);
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        props = map.getProperties();
        categorizeLayers();
    }

    // Phân loại layer thành below và above
    private void categorizeLayers() {
        belowPlayerLayers = new ArrayList<>();
        abovePlayerLayers = new ArrayList<>();

        for (int i = 0; i < map.getLayers().getCount(); i++) {
            MapLayer layer = map.getLayers().get(i);
            String name = layer.getName().toLowerCase(); // so sánh không phân biệt hoa thường

            if (name.contains("below")) {
                belowPlayerLayers.add(i);
            } else if (name.contains("above")) {
                abovePlayerLayers.add(i);
            }
        }
    }

    public void render(OrthographicCamera camera, int[] layers) {
        mapRenderer.setView(camera);
        mapRenderer.render(layers);
    }

    public void renderBelowPlayer(OrthographicCamera camera) {
        mapRenderer.setView(camera);
        mapRenderer.render(belowPlayerLayers.stream().mapToInt(i -> i).toArray());
    }

    public void renderAbovePlayer(OrthographicCamera camera) {
        mapRenderer.setView(camera);
        mapRenderer.render(abovePlayerLayers.stream().mapToInt(i -> i).toArray());
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
