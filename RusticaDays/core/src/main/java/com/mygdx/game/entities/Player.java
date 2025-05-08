package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.entities.animations.PlayerAnimationManager;
import com.mygdx.game.entities.animations.PlayerAnimationManager.PlayerState;

public class Player implements GameObject {
    private float x, y;
    private float speed = 80f; // pixels/second
    private Rectangle bounds;

    private PlayerAnimationManager animationManager;
    private PlayerState currentState = PlayerState.STAND_DOWN;
    private PlayerState previousState = PlayerState.STAND_DOWN;
    private boolean facingLeft = false;

    public Player(float startX, float startY, String spritesheetPath) {
        this.animationManager = new PlayerAnimationManager(spritesheetPath);

        // Khởi tạo bounds dựa trên kích thước của frame
        int frameWidth = animationManager.getFrameWidth();
        int frameHeight = animationManager.getFrameHeight();
        this.x = startX;
        this.y = startY;
        this.bounds = new Rectangle(x, y, 10, 5);
    }

    @Override
    public void update(float deltaTime) {
        // Update animations
        animationManager.update(deltaTime, currentState);

        // Cập nhật bounds theo vị trí hiện tại
        bounds.setPosition(x, y);

        // Save current state for next frame
        previousState = currentState;
    }

    @Override
    public void render(SpriteBatch batch) {
        int frameWidth = animationManager.getFrameWidth();
        int frameHeight = animationManager.getFrameHeight();

        float drawX = x - frameWidth / 3f; // Căn giữa nhân vật theo X
        float drawY = y - 4;
        // Hàm điều kiện vẽ frame hiện tại( vì chỉ có asset quay sang phải nên phải if else ạ :)) )
        if (facingLeft) {
            // Lật texture sang trái
            batch.draw(animationManager.getCurrentFrame(), drawX + frameWidth, drawY, -frameWidth, frameHeight);
        } else {
            batch.draw(animationManager.getCurrentFrame(), drawX, drawY);
        }
    }

    @Override
    public Vector2 getPosition() {
        return new Vector2(x, y);
    }

    @Override
    public float getDepth() {
        return y;
    }

    public void moveLeft(float delta) {
        x -= speed * delta;
        currentState = PlayerState.WALK_RIGHT;
        facingLeft = true;
    }

    public void moveRight(float delta) {
        x += speed * delta;
        currentState = PlayerState.WALK_RIGHT;
        facingLeft = false;
    }

    public void moveUp(float delta) {
        y += speed * delta;
        currentState = PlayerState.WALK_UP;
    }

    public void moveDown(float delta) {
        y -= speed * delta;
        currentState = PlayerState.WALK_DOWN;
    }

    public void standStill() {
        // Set Animation đứng yên dựa trên hướng vừa di chuyển
        if (previousState == PlayerState.WALK_UP || previousState == PlayerState.STAND_UP) {
            currentState = PlayerState.STAND_UP;
        } else if (previousState == PlayerState.WALK_RIGHT || previousState == PlayerState.STAND_RIGHT) {
            currentState = PlayerState.STAND_RIGHT;
        } else {
            currentState = PlayerState.STAND_DOWN;
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getSpeed() {
        return speed;
    }

    public void dispose() {
        animationManager.dispose();
    }

    public float getWidth() {
        return animationManager.getFrameWidth();
    }

    public float getHeight() {
        return animationManager.getFrameHeight();
    }
}
