package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.entities.animations.PlayerAnimationManager;
import com.mygdx.game.entities.animations.PlayerAnimationManager.PlayerState;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.entities.animals.FishType;
import com.mygdx.game.items.animalproducts.FishItem;
import com.mygdx.game.inventory.InventoryManager;
import java.util.Random;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.mygdx.game.entities.plants.base.Plant;

public class Player implements GameObject {
    private float x, y;
    private float speed = 80f; // pixels/second
    private Rectangle bounds;

    private PlayerAnimationManager animationManager;
    private PlayerState currentState = PlayerState.STAND_DOWN;
    private PlayerState previousState = PlayerState.STAND_DOWN;
    private boolean facingLeft = false;

    private InventoryManager inventoryManager;
    // Notification trên đầu nhân vật
    private String notificationMessage = null;
    private float notificationTimer = 0f;
    private static final float NOTIFICATION_DURATION = 2.0f;

    public Player(float startX, float startY, String spritesheetPath, InventoryManager inventoryManager) {
        this.animationManager = new PlayerAnimationManager(spritesheetPath);

        // Khởi tạo bounds dựa trên kích thước của frame
        int frameWidth = animationManager.getFrameWidth();
        int frameHeight = animationManager.getFrameHeight();
        this.x = startX;
        this.y = startY;
        this.bounds = new Rectangle(x, y, 10, 5);
        this.inventoryManager = inventoryManager;
    }

    @Override
    public void update(float deltaTime) {
        // Update animations
        animationManager.update(deltaTime, currentState);

        // Cập nhật bounds theo vị trí hiện tại
        bounds.setPosition(x, y);

        // Save current state for next frame
        previousState = currentState;

        // Cập nhật notification
        if (notificationTimer > 0) {
            notificationTimer -= deltaTime;
            if (notificationTimer <= 0) {
                notificationMessage = null;
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        int frameWidth = animationManager.getFrameWidth();
        int frameHeight = animationManager.getFrameHeight();
        if(frameHeight == 32){
            float drawX = x - frameWidth / 3f - 1; // Căn giữa nhân vật theo X
            float drawY = y - 4;
            // Hàm điều kiện vẽ frame hiện tại( vì chỉ có asset quay sang phải nên phải if else ạ :)) )
            if (facingLeft) {
                // Lật texture sang trái
                batch.draw(animationManager.getCurrentFrame(), drawX + frameWidth, drawY, -frameWidth, frameHeight);
            } else {
                batch.draw(animationManager.getCurrentFrame(), drawX, drawY);
            }
        }

        else if(frameHeight == 64){
            float drawX = x - frameWidth / 3f - 5; // Căn giữa nhân vật theo X
            float drawY = y - 20;
            // Hàm điều kiện vẽ frame hiện tại( vì chỉ có asset quay sang phải nên phải if else ạ :)) )
            if (facingLeft) {
                // Lật texture sang trái
                batch.draw(animationManager.getCurrentFrame(), drawX + frameWidth, drawY, -frameWidth, frameHeight);
            } else {
                batch.draw(animationManager.getCurrentFrame(), drawX, drawY);
            }
        }
        // Vẽ notification trên đầu nhân vật
        if (notificationMessage != null) {
            if (Plant.countdownFont == null) {
                try {
                    Plant.class.getDeclaredMethod("initCountdownFont").setAccessible(true);
                    Plant.class.getDeclaredMethod("initCountdownFont").invoke(null);
                } catch (Exception e) {
                    // Nếu không gọi được, fallback font mặc định
                    Plant.countdownFont = new BitmapFont();
                    Plant.countdownFont.setColor(Color.WHITE);
                    Plant.countdownFont.getData().setScale(0.4f);
                }
            }
            Plant.countdownFont.setColor(Color.GOLD);
            Plant.initFont();
            float textX = x - 50; // căn giữa, tuỳ chỉnh lại nếu cần
            float textY = y + 50;    // phía trên đầu nhân vật
            Plant.countdownFont.draw(batch, notificationMessage, textX, textY);
            Plant.countdownFont.setColor(Color.WHITE);
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

    public void setState(PlayerAnimationManager.PlayerState state) {
        this.currentState = state;
    }

    public PlayerAnimationManager getAnimationManager() {
        return animationManager;
    }

    /**
     * Câu cá: 70% ra cá, random 1 trong 5 loại
     */
    public FishType tryCatchFish() {
        Random random = new Random();
        if (random.nextFloat() < 0.7f) {
            int fishIndex = random.nextInt(FishType.values().length);
            FishType caughtFish = FishType.values()[fishIndex];
            FishItem fishItem = new FishItem(caughtFish);
            inventoryManager.addItem(fishItem, 1);
            return caughtFish;
        } else {
            return null;
        }
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public void showNotification(String message) {
        this.notificationMessage = message;
        this.notificationTimer = NOTIFICATION_DURATION;
    }
}
