package com.mygdx.game.entities.animals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class Cow {
    private static final float ANIMATION_SPEED = 0.1f;
    private static final float MOVEMENT_SPEED = 30f;
    private static final float STATE_CHANGE_TIME = 3f;
    
    private static final float BARN_LEFT_X = 60f;
    private static final float BARN_RIGHT_X = 218f;
    private static final float BARN_BOTTOM_Y = 70f;
    private static final float BARN_TOP_Y = 208f;
    private static final float BARN_MIDDLE_X = 153f;
    private static final float BARN_MIDDLE_BOTTOM_Y = 86f;
    private static final float BARN_MIDDLE_TOP_Y = 155f;
    
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
    
    private CowState currentState;
    private boolean isFacingRight;
    
    private enum CowState {
        WALKING, IDLE, EATING, STANDING_UP, SITTING_DOWN, SLEEPING
    }
    
    public Cow(float x, float y) {
        position = new Vector2(x, y);
        targetPosition = new Vector2(x, y);
        stateTime = 0f;
        stateChangeTimer = 0f;
        random = new Random();
        currentState = CowState.IDLE;
        isFacingRight = true;
        
        loadAnimations();
    }
    
    private void loadAnimations() {
        // Load walking animation (8 frames)
        walkingAnimation = loadAnimation("Cow/Cow_Walk", 8);
        
        // Load idle animation (8 frames)
        idleAnimation = loadAnimation("Cow/Cow_Idle", 8);
        
        // Load eating animation (8 frames)
        eatingAnimation = loadAnimation("Cow/Cow_Eat", 8);
        
        // Load standing up animation (6 frames)
        standingUpAnimation = loadAnimation("Cow/Cow_StandUp", 6);
        
        // Load sitting down animation (6 frames)
        sittingDownAnimation = loadAnimation("Cow/Cow_SitDown", 6);
        
        // Load sleeping animation (4 frames)
        sleepingAnimation = loadAnimation("Cow/Cow_Sleep", 4);
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
        
        if (currentState == CowState.WALKING) {
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
            currentState = CowState.IDLE;
        }
    }
    
    private void changeState() {
        CowState[] states = CowState.values();
        CowState newState;
        do {
            newState = states[random.nextInt(states.length)];
        } while (newState == currentState);
        
        currentState = newState;
        
        if (currentState == CowState.WALKING) {
            generateNewTargetPosition();
        }
    }
    
    private void generateNewTargetPosition() {
        float x, y;
        do {
            if (random.nextBoolean()) {
                x = random.nextFloat() * (BARN_RIGHT_X - BARN_MIDDLE_X) + BARN_MIDDLE_X;
                y = random.nextFloat() * (BARN_TOP_Y - BARN_BOTTOM_Y) + BARN_BOTTOM_Y;
            } else {
                x = random.nextFloat() * (BARN_MIDDLE_X - BARN_LEFT_X) + BARN_LEFT_X;
                y = random.nextFloat() * (BARN_MIDDLE_TOP_Y - BARN_MIDDLE_BOTTOM_Y) + BARN_MIDDLE_BOTTOM_Y;
            }
        } while (!isInBarn(x, y));
        
        targetPosition.set(x, y);
    }
    
    private boolean isInBarn(float x, float y) {
        return (x > BARN_LEFT_X && x < BARN_RIGHT_X && y > BARN_BOTTOM_Y && y < BARN_TOP_Y) || 
               (x > BARN_LEFT_X && x < BARN_MIDDLE_X && y > BARN_MIDDLE_BOTTOM_Y && y < BARN_MIDDLE_TOP_Y);
    }
    
    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = getCurrentFrame();
        if (currentFrame != null) {
            boolean wasFlipped = currentFrame.isFlipX();
            
            if (!isFacingRight) {
                currentFrame.flip(true, false);
            }
            
            batch.draw(currentFrame, position.x, position.y);
            
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