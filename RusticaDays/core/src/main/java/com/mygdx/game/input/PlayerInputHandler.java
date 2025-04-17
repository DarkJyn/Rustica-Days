package com.mygdx.game.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.mygdx.game.entities.Player;

public class PlayerInputHandler {
    private Player player;
    private TiledMap map;
    private TiledMapTileLayer walkableLayer; // Layer chứa đường màu nâu
    public PlayerInputHandler(Player player,TiledMap map) {
        this.player = player;
            this.map = map;

            // Lấy các layer có thể đi được
            this.walkableLayer = (TiledMapTileLayer) map.getLayers().get("Walkable");
    }

    public void processInput(float delta) {
        boolean isMoving = false;
        float originalX = player.getX();
        float originalY = player.getY();
        // Process input and determine state
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            float newX = originalX - player.getSpeed() * delta;
//            float newX = originalX - delta;
            if (canMoveTo(newX, originalY)) {
                player.moveLeft(delta);
                isMoving = true;
            }
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            float newX = originalX + player.getSpeed() * delta;
//            float newX = originalX + delta;
            if (canMoveTo(newX, originalY)) {
                player.moveRight(delta);
                isMoving = true;
            }
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            float newY = originalY + player.getSpeed() * delta;
//            float newY = originalY +  delta;
            if (canMoveTo(originalX, newY)) {
                player.moveUp(delta);
                isMoving = true;
            }
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            float newY = originalY - player.getSpeed() * delta;
//            float newY = originalY - delta;
            if (canMoveTo(originalX, newY)){
                player.moveDown(delta);
                isMoving = true;
            }
        }

        // If not moving, set to standing animation
        if (!isMoving) {
            player.standStill();
        }
    }
    private boolean canMoveTo(float x, float y) {
        int tileX = (int) (x / walkableLayer.getTileWidth());
        int tileY = (int) (y / walkableLayer.getTileHeight());

        // Kiểm tra nếu vị trí nằm trên BẤT KỲ layer nào được phép đi
        boolean canWalk = false;

//         Kiểm tra trên đường màu nâu
        if (walkableLayer != null && walkableLayer.getCell(tileX, tileY) != null) {
            canWalk = true;
        }

        // Kiểm tra trên cỏ (nếu cho phép đi trên cỏ)
//        if (!canWalk && grassLayer != null && grassLayer.getCell(tileX, tileY) != null) {
//            canWalk = true;
//        }

        return canWalk;
    }
}
