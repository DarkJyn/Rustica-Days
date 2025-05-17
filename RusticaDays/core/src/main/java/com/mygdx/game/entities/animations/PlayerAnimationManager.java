package com.mygdx.game.entities.animations;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PlayerAnimationManager {
    // Chia ô Asset Player
    private static final int FRAME_COLS = 6;
    private static final int FRAME_ROWS = 6;
    private static final float FRAME_DURATION = 0.15f;

    // Số cột cho từng loại hoạt ảnh câu cá
    private static final int FISH_CAST_COLS = 9;
    private static final int FISH_WAIT_COLS = 5;
    private static final int FISH_HOOK_COLS = 4;
    private static final int FISH_PULL_COLS = 8;

    // Thời gian frame cho từng loại animation câu cá (tùy chỉnh tốc độ)
    private static final float FISH_CAST_FRAME_DURATION = 0.3f;
    private static final float FISH_WAIT_FRAME_DURATION = 0.4f;
    private static final float FISH_HOOK_FRAME_DURATION = 0.3f;
    private static final float FISH_PULL_FRAME_DURATION = 0.3f;

    // Animations
    private Animation<TextureRegion> standUpAnimation;
    private Animation<TextureRegion> standRightAnimation;
    private Animation<TextureRegion> standDownAnimation;
    private Animation<TextureRegion> walkUpAnimation;
    private Animation<TextureRegion> walkRightAnimation;
    private Animation<TextureRegion> walkDownAnimation;
    private Animation<TextureRegion> fishCastAnimation;
    private Animation<TextureRegion> fishWaitAnimation;
    private Animation<TextureRegion> fishHookAnimation;
    private Animation<TextureRegion> fishPullAnimation;

    // Current animation state
    private TextureRegion currentFrame;
    private float stateTime;

    // Player texture
    private Texture playerSpriteSheet;
    private Texture fishCastTexture;
    private Texture fishWaitTexture;
    private Texture fishHookTexture;
    private Texture fishPullTexture;

    // Lưu lại trạng thái cuối cùng để biết đang ở hoạt ảnh nào
    private PlayerState lastState = PlayerState.STAND_DOWN;

    public PlayerAnimationManager(String spritesheetPath) {
        playerSpriteSheet = new Texture(spritesheetPath);

        // Tải các texture riêng biệt cho hoạt ảnh câu cá
        try {
            fishCastTexture = new Texture("Player_FishCast.png");
            fishWaitTexture = new Texture("Player_FishWait.png");
            fishHookTexture = new Texture("Player_FishHook.png");
            fishPullTexture = new Texture("Player_FishPull.png");
        } catch (Exception e) {
            System.err.println("Không thể tải file hoạt ảnh câu cá: " + e.getMessage());
            // Nếu không tải được file hoạt ảnh câu cá, các biến sẽ giữ giá trị null
        }

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

        // Tạo hoạt ảnh câu cá từ các texture riêng biệt
        if (fishCastTexture != null && fishWaitTexture != null && fishHookTexture != null && fishPullTexture != null) {
            TextureRegion[] fishCastFrames = new TextureRegion[FISH_CAST_COLS];
            TextureRegion[][] castTmp = TextureRegion.split(
                fishCastTexture,
                fishCastTexture.getWidth() / FISH_CAST_COLS,
                fishCastTexture.getHeight());
            for (int i = 0; i < FISH_CAST_COLS; i++) {
                fishCastFrames[i] = castTmp[0][i];
            }

            TextureRegion[] fishWaitFrames = new TextureRegion[FISH_WAIT_COLS];
            TextureRegion[][] waitTmp = TextureRegion.split(
                fishWaitTexture,
                fishWaitTexture.getWidth() / FISH_WAIT_COLS,
                fishWaitTexture.getHeight());
            for (int i = 0; i < FISH_WAIT_COLS; i++) {
                fishWaitFrames[i] = waitTmp[0][i];
            }

            TextureRegion[] fishHookFrames = new TextureRegion[FISH_HOOK_COLS];
            TextureRegion[][] hookTmp = TextureRegion.split(
                fishHookTexture,
                fishHookTexture.getWidth() / FISH_HOOK_COLS,
                fishHookTexture.getHeight());
            for (int i = 0; i < FISH_HOOK_COLS; i++) {
                fishHookFrames[i] = hookTmp[0][i];
            }

            TextureRegion[] fishPullFrames = new TextureRegion[FISH_PULL_COLS];
            TextureRegion[][] pullTmp = TextureRegion.split(
                fishPullTexture,
                fishPullTexture.getWidth() / FISH_PULL_COLS,
                fishPullTexture.getHeight());
            for (int i = 0; i < FISH_PULL_COLS; i++) {
                fishPullFrames[i] = pullTmp[0][i];
            }

            // Tạo Animations
            standUpAnimation = new Animation<>(FRAME_DURATION, standUpFrames);
            standRightAnimation = new Animation<>(FRAME_DURATION, standRightFrames);
            standDownAnimation = new Animation<>(FRAME_DURATION, standDownFrames);
            walkUpAnimation = new Animation<>(FRAME_DURATION, walkUpFrames);
            walkRightAnimation = new Animation<>(FRAME_DURATION, walkRightFrames);
            walkDownAnimation = new Animation<>(FRAME_DURATION, walkDownFrames);
            fishCastAnimation = new Animation<>(FISH_CAST_FRAME_DURATION, fishCastFrames);
            fishWaitAnimation = new Animation<>(FISH_WAIT_FRAME_DURATION, fishWaitFrames);
            fishHookAnimation = new Animation<>(FISH_HOOK_FRAME_DURATION, fishHookFrames);
            fishPullAnimation = new Animation<>(FISH_PULL_FRAME_DURATION, fishPullFrames);
        }
    }

    // Update trạng thái dựa trên state hiện tại
    public void update(float deltaTime, PlayerState state) {
        stateTime += deltaTime;
        lastState = state;

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
            case FISH_CAST:
                if (fishCastAnimation != null)
                    currentFrame = fishCastAnimation.getKeyFrame(stateTime, false);
                break;
            case FISH_WAIT:
                if (fishWaitAnimation != null)
                    currentFrame = fishWaitAnimation.getKeyFrame(stateTime, true);
                break;
            case FISH_HOOK:
                if (fishHookAnimation != null)
                    currentFrame = fishHookAnimation.getKeyFrame(stateTime, true);
                break;
            case FISH_PULL:
                if (fishPullAnimation != null)
                    currentFrame = fishPullAnimation.getKeyFrame(stateTime, false);
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
        if (fishCastTexture != null) fishCastTexture.dispose();
        if (fishWaitTexture != null) fishWaitTexture.dispose();
        if (fishHookTexture != null) fishHookTexture.dispose();
        if (fishPullTexture != null) fishPullTexture.dispose();
    }

    // Trả về index frame hiện tại của hoạt ảnh
    public int getCurrentAnimationFrameIndex() {
        if (currentFrame == null) return 0;
        Animation<TextureRegion> anim = null;
        switch (lastState) {
            case STAND_UP: anim = standUpAnimation; break;
            case STAND_RIGHT: anim = standRightAnimation; break;
            case STAND_DOWN: anim = standDownAnimation; break;
            case WALK_UP: anim = walkUpAnimation; break;
            case WALK_RIGHT: anim = walkRightAnimation; break;
            case WALK_DOWN: anim = walkDownAnimation; break;
            case FISH_CAST: anim = fishCastAnimation; break;
            case FISH_WAIT: anim = fishWaitAnimation; break;
            case FISH_HOOK: anim = fishHookAnimation; break;
            case FISH_PULL: anim = fishPullAnimation; break;
            default: anim = standDownAnimation;
        }
        if (anim == null) return 0;
        return anim.getKeyFrameIndex(stateTime);
    }

    // Trả về tổng số frame của hoạt ảnh hiện tại
    public int getTotalFrames() {
        Animation<TextureRegion> anim = null;
        switch (lastState) {
            case STAND_UP: anim = standUpAnimation; break;
            case STAND_RIGHT: anim = standRightAnimation; break;
            case STAND_DOWN: anim = standDownAnimation; break;
            case WALK_UP: anim = walkUpAnimation; break;
            case WALK_RIGHT: anim = walkRightAnimation; break;
            case WALK_DOWN: anim = walkDownAnimation; break;
            case FISH_CAST: anim = fishCastAnimation; break;
            case FISH_WAIT: anim = fishWaitAnimation; break;
            case FISH_HOOK: anim = fishHookAnimation; break;
            case FISH_PULL: anim = fishPullAnimation; break;
            default: anim = standDownAnimation;
        }
        if (anim == null) return 1;
        return anim.getKeyFrames().length;
    }

    public void resetStateTime() {
        stateTime = 0f;
    }

    public enum PlayerState {
        STAND_UP, STAND_RIGHT, STAND_DOWN, WALK_UP, WALK_RIGHT, WALK_DOWN,
        FISH_CAST, FISH_WAIT, FISH_HOOK, FISH_PULL
    }
}
