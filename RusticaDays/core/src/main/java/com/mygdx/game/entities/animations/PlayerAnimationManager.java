package com.mygdx.game.entities.animations;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PlayerAnimationManager {
    // Chia ô Asset Player
    private static final int FRAME_COLS = 6;
    private static final int FRAME_ROWS = 6;
    private static final float FRAME_DURATION = 0.15f;

    // Animations
    private Animation<TextureRegion> standUpAnimation;
    private Animation<TextureRegion> standRightAnimation;
    private Animation<TextureRegion> standDownAnimation;
    private Animation<TextureRegion> walkUpAnimation;
    private Animation<TextureRegion> walkRightAnimation;
    private Animation<TextureRegion> walkDownAnimation;

    // Current animation state
    private TextureRegion currentFrame;
    private float stateTime;

    // Player texture
    private Texture playerSpriteSheet;

    public PlayerAnimationManager(String spritesheetPath) {
        playerSpriteSheet = new Texture(spritesheetPath);
        createAnimations();
        stateTime = 0f;

        // Khởi tạo currentFrame với frame đầu tiên để tránh null pointer
        currentFrame = standDownAnimation.getKeyFrame(0);
    }

    private void createAnimations() {
        // Chia sprite sheet thành frames
        TextureRegion[][] tmp = TextureRegion.split(
            playerSpriteSheet,
            playerSpriteSheet.getWidth() / FRAME_COLS,
            playerSpriteSheet.getHeight() / FRAME_ROWS);

        // Tạo mảng chạy animation
        TextureRegion[] standUpFrames = new TextureRegion[FRAME_COLS];
        TextureRegion[] standRightFrames = new TextureRegion[FRAME_COLS];
        TextureRegion[] standDownFrames = new TextureRegion[FRAME_COLS];
        TextureRegion[] walkUpFrames = new TextureRegion[FRAME_COLS];
        TextureRegion[] walkRightFrames = new TextureRegion[FRAME_COLS];
        TextureRegion[] walkDownFrames = new TextureRegion[FRAME_COLS];

        // Nạp arrays chứa Frame từ sprite sheet
        // Row 0: Stand Down
        for (int i = 0; i < FRAME_COLS; i++) {
            standDownFrames[i] = tmp[0][i];
        }

        // Row 1: Stand Right
        for (int i = 0; i < FRAME_COLS; i++) {
            standRightFrames[i] = tmp[1][i];
        }

        // Row 2: Stand Up
        for (int i = 0; i < FRAME_COLS; i++) {
            standUpFrames[i] = tmp[2][i];
        }

        // Row 3: Walk Down
        for (int i = 0; i < FRAME_COLS; i++) {
            walkDownFrames[i] = tmp[3][i];
        }

        // Row 4: Walk Right
        for (int i = 0; i < FRAME_COLS; i++) {
            walkRightFrames[i] = tmp[4][i];
        }

        // Row 5: Walk Up
        for (int i = 0; i < FRAME_COLS; i++) {
            walkUpFrames[i] = tmp[5][i];
        }

        // Tạo Animations
        standUpAnimation = new Animation<>(FRAME_DURATION, standUpFrames);
        standRightAnimation = new Animation<>(FRAME_DURATION, standRightFrames);
        standDownAnimation = new Animation<>(FRAME_DURATION, standDownFrames);
        walkUpAnimation = new Animation<>(FRAME_DURATION, walkUpFrames);
        walkRightAnimation = new Animation<>(FRAME_DURATION, walkRightFrames);
        walkDownAnimation = new Animation<>(FRAME_DURATION, walkDownFrames);
    }
// Update trạng thái dựa trên state hiện tại
    public void update(float deltaTime, PlayerState state) {
        stateTime += deltaTime;

        switch (state) {
            case STAND_UP:
                currentFrame = standUpAnimation.getKeyFrame(stateTime, true);
                break;
            case STAND_RIGHT:
                currentFrame = standRightAnimation.getKeyFrame(stateTime, true);
                break;
            case STAND_DOWN:
                currentFrame = standDownAnimation.getKeyFrame(stateTime, true);
                break;
            case WALK_UP:
                currentFrame = walkUpAnimation.getKeyFrame(stateTime, true);
                break;
            case WALK_RIGHT:
                currentFrame = walkRightAnimation.getKeyFrame(stateTime, true);
                break;
            case WALK_DOWN:
                currentFrame = walkDownAnimation.getKeyFrame(stateTime, true);
                break;
            default:
                currentFrame = standDownAnimation.getKeyFrame(stateTime, true);
        }
    }

    public TextureRegion getCurrentFrame() {
        return currentFrame;
    }

    public int getFrameWidth() {
        if (currentFrame == null) {
            // Nếu vì lý do nào đó currentFrame vẫn null, trả về giá trị mặc định
            return playerSpriteSheet.getWidth() / FRAME_COLS;
        }
        return currentFrame.getRegionWidth();
    }

    public int getFrameHeight() {
        if (currentFrame == null) {
            // Nếu vì lý do nào đó currentFrame vẫn null, trả về giá trị mặc định
            return playerSpriteSheet.getHeight() / FRAME_ROWS;
        }
        return currentFrame.getRegionHeight();
    }

    public void dispose() {
        playerSpriteSheet.dispose();
    }

    public enum PlayerState {
        STAND_UP, STAND_RIGHT, STAND_DOWN, WALK_UP, WALK_RIGHT, WALK_DOWN
    }
}
