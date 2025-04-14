package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.entities.animations.PlayerAnimationManager;
import com.mygdx.game.entities.animations.PlayerAnimationManager.PlayerState;

public class Player {
    private float x, y;
    private float speed = 75f; // pixels/second

    private PlayerAnimationManager animationManager;
    private PlayerState currentState = PlayerState.STAND_DOWN;
    private PlayerState previousState = PlayerState.STAND_DOWN;
    private boolean facingLeft = false;

    public Player(float startX, float startY, String spritesheetPath) {
        this.x = startX;
        this.y = startY;
        this.animationManager = new PlayerAnimationManager(spritesheetPath);
    }

    public void update(float deltaTime) {
        // Update animations
        animationManager.update(deltaTime, currentState);

        // Save current state for next frame
        previousState = currentState;
    }

    public void render(SpriteBatch batch) {
        int frameWidth = animationManager.getFrameWidth();
        int frameHeight = animationManager.getFrameHeight();

        // Draw player with animation
        if (facingLeft) {
            // Flip the texture region for left-facing animations
            batch.draw(animationManager.getCurrentFrame(), x + frameWidth, y, -frameWidth, frameHeight);
        } else {
            batch.draw(animationManager.getCurrentFrame(), x, y);
        }
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
        // Set standing animation based on previous movement
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

    public void dispose() {
        animationManager.dispose();
    }
}
