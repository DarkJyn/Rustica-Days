package com.mygdx.game.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.entities.Player;

public class PlayerInputHandler {
    private Player player;
    private TiledMap map;
    private CollisionHandler collisionHandler;
    private Rectangle playerBounds;
    private float playerSpeed;

    public PlayerInputHandler(Player player, TiledMap map) {
        this.player = player;
        this.map = map;

        // Khởi tạo collision handler với tên của object layer là "Collision"
        collisionHandler = new CollisionHandler(map, "Collisions");

        // Lấy bounds từ player
        playerBounds = new Rectangle(player.getBounds());
        playerSpeed = player.getSpeed();
    }

    public CollisionHandler getCollisionHandler() {
        return collisionHandler;
    }

    public void processInput(float delta) {
        // Cập nhật playerBounds theo vị trí hiện tại của player
        playerBounds.setPosition(player.getX(), player.getY());

        // Xử lý input - chỉ di chuyển một hướng duy nhất
        float deltaX = 0, deltaY = 0;
        boolean hasMoved = false;

        // Kiểm tra phím theo thứ tự ưu tiên
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            deltaX = -playerSpeed * delta;
            hasMoved = true;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            deltaX = playerSpeed * delta;
            hasMoved = true;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            deltaY = playerSpeed * delta;
            hasMoved = true;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            deltaY = -playerSpeed * delta;
            hasMoved = true;
        }

        // Nếu không có phím nào được nhấn
        if (!hasMoved) {
            player.standStill();
            return;
        }

        // Cập nhật vị trí nhân vật với kiểm tra va chạm
        Vector2 newPosition = collisionHandler.moveWithCollisionCheck(playerBounds, deltaX, deltaY);

        // Cập nhật animation dựa trên hướng di chuyển
        if (deltaX < 0) {
            player.moveLeft(delta);
        }
        else if (deltaX > 0) {
            player.moveRight(delta);
        }
        else if (deltaY > 0) {
            player.moveUp(delta);
        }
        else if (deltaY < 0) {
            player.moveDown(delta);
        }

        // Cập nhật vị trí cho player
        player.setX(newPosition.x);
        player.setY(newPosition.y);
    }
}
