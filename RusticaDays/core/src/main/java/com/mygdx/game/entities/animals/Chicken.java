package com.mygdx.game.entities.animals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class Chicken {
    private static final float ANIMATION_SPEED = 0.15f;
    private static final float MOVEMENT_SPEED = 20f; // Gà di chuyển chậm hơn bò
    private static final float STATE_CHANGE_TIME = 3f;
    private static final float CHICKEN_SCALE = 0.85f; // Thêm hệ số scale cho gà

    // Giới hạn chuồng gà
    private static final float BARN_LEFT_X = 179f;
    private static final float BARN_RIGHT_X = 242f;
    private static final float BARN_BOTTOM_Y = 387f;
    private static final float BARN_TOP_Y = 416f;
    private static final float BARN_MIDDLE_LEFT_X = 192f;
    private static final float BARN_MIDDLE_RIGHT_X = 227f;

    private Vector2 position;
    private Vector2 targetPosition;
    private float stateTime;
    private float stateChangeTimer;
    private Random random;

    private Animation<TextureRegion> walkingAnimation;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> eatingAnimation;
    private Animation<TextureRegion> standingUpAnimation;
    private Animation<TextureRegion> sittingDownAnimation;
    private Animation<TextureRegion> sleepingAnimation;

    private ChickenState currentState;
    private boolean isFacingRight;

    private enum ChickenState {
        WALKING, IDLE, EATING, STANDING_UP, SITTING_DOWN, SLEEPING
    }

    public Chicken(float x, float y) {
        position = new Vector2(x, y);
        targetPosition = new Vector2(x, y);
        stateTime = 0f;
        stateChangeTimer = 0f;
        random = new Random();
        currentState = ChickenState.IDLE;
        isFacingRight = true;

        loadAnimations();
    }

    private void loadAnimations() {
        // Load walking animation (6 frames)
        walkingAnimation = loadAnimation("Chicken/Chicken_Walk", 6);

        // Load idle animation (2 frames)
        idleAnimation = loadAnimation("Chicken/Chicken_Idle", 2);

        // Load eating animation (8 frames)
        eatingAnimation = loadAnimation("Chicken/Chicken_Eat", 8);

        // Load standing up animation (3 frames)
        standingUpAnimation = loadAnimation("Chicken/Chicken_StandUp", 3);

        // Load sitting down animation (3 frames)
        sittingDownAnimation = loadAnimation("Chicken/Chicken_SitDown", 3);

        // Load sleeping animation (3 frames)
        sleepingAnimation = loadAnimation("Chicken/Chicken_Sleep", 3);
    }

    private Animation<TextureRegion> loadAnimation(String path, int frameCount) {
        Texture texture = new Texture(Gdx.files.internal("[Rustica] Asset/Cute_Fantasy/Cute_Fantasy/Animals/" + path + ".png"));
        int frameWidth = texture.getWidth() / frameCount;
        int frameHeight = texture.getHeight();

        TextureRegion[][] tmp = TextureRegion.split(texture, frameWidth, frameHeight);
        TextureRegion[] frames = new TextureRegion[frameCount];

        for (int i = 0; i < frameCount; i++) {
            frames[i] = tmp[0][i];
        }

        return new Animation<>(ANIMATION_SPEED, frames);
    }

    public void update(float delta) {
        stateTime += delta;
        stateChangeTimer += delta;

        if (stateChangeTimer >= STATE_CHANGE_TIME) {
            stateChangeTimer = 0f;
            changeState();
        }

        if (currentState == ChickenState.WALKING) {
            moveTowardsTarget(delta);
        }
    }

    private void moveTowardsTarget(float delta) {
        Vector2 direction = new Vector2(targetPosition).sub(position).nor();
        Vector2 movement = new Vector2(direction).scl(MOVEMENT_SPEED * delta);
        Vector2 newPosition = new Vector2(position).add(movement);

        if (isInBarn(newPosition.x, newPosition.y)) {
            position.set(newPosition);

            if (direction.x > 0) {
                isFacingRight = false;
            } else if (direction.x < 0) {
                isFacingRight = true;
            }
        } else {
            generateNewTargetPosition();
        }

        if (position.dst(targetPosition) < 5f) {
            currentState = ChickenState.IDLE;
        }
    }

    private void changeState() {
        ChickenState[] states = ChickenState.values();
        ChickenState newState;
        do {
            newState = states[random.nextInt(states.length)];
        } while (newState == currentState);

        currentState = newState;

        if (currentState == ChickenState.WALKING) {
            generateNewTargetPosition();
        }
    }

    private void generateNewTargetPosition() {
        float x, y;
        do {
            if (random.nextBoolean()) {
                // Khu vực chuồng gà phải
                x = random.nextFloat() * (BARN_MIDDLE_RIGHT_X - BARN_MIDDLE_LEFT_X) + BARN_MIDDLE_LEFT_X;
                y = random.nextFloat() * (BARN_TOP_Y - BARN_BOTTOM_Y) + BARN_BOTTOM_Y;
            } else {
                // Khu vực chuồng gà trái
                x = random.nextFloat() * (BARN_MIDDLE_LEFT_X - BARN_LEFT_X) + BARN_LEFT_X;
                y = random.nextFloat() * (BARN_TOP_Y - BARN_BOTTOM_Y) + BARN_BOTTOM_Y;
            }
        } while (!isInBarn(x, y));

        targetPosition.set(x, y);
    }

    private boolean isInBarn(float x, float y) {
        return (x > BARN_MIDDLE_LEFT_X && x < BARN_MIDDLE_RIGHT_X && y > BARN_BOTTOM_Y && y < BARN_TOP_Y) ||
               (x > BARN_LEFT_X && x < BARN_RIGHT_X && y > BARN_BOTTOM_Y && y < BARN_TOP_Y);
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = getCurrentFrame();
        if (currentFrame != null) {
            boolean wasFlipped = currentFrame.isFlipX();

            if (!isFacingRight) {
                currentFrame.flip(true, false);
            }

            // Tính toán kích thước mới dựa trên scale
            float width = currentFrame.getRegionWidth() * CHICKEN_SCALE;
            float height = currentFrame.getRegionHeight() * CHICKEN_SCALE;

            // Vẽ gà với kích thước mới
            batch.draw(currentFrame, position.x, position.y, width, height);

            if (currentFrame.isFlipX() != wasFlipped) {
                currentFrame.flip(true, false);
            }
        }
    }

    private TextureRegion getCurrentFrame() {
        switch (currentState) {
            case WALKING:
                return walkingAnimation.getKeyFrame(stateTime, true);
            case IDLE:
                return idleAnimation.getKeyFrame(stateTime, true);
            case EATING:
                return eatingAnimation.getKeyFrame(stateTime, true);
            case STANDING_UP:
                return standingUpAnimation.getKeyFrame(stateTime, false);
            case SITTING_DOWN:
                return sittingDownAnimation.getKeyFrame(stateTime, false);
            case SLEEPING:
                return sleepingAnimation.getKeyFrame(stateTime, true);
            default:
                return null;
        }
    }

    public void dispose() {
        // Dispose textures if needed
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }
}
