package com.mygdx.game.module;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.camera.GameCamera;
import com.mygdx.game.entities.Player;
import com.mygdx.game.entities.animations.PlayerAnimationManager.PlayerState;

public class SleepSystem {
    private Vector2 bedPosition;
    private Rectangle bedBounds;
    private Player player;
    private Vector2 playerOriginalPosition;
    private Texture fButton;
    private TextureRegion currentFrame;
    private GameCamera camera;
    private Animation<TextureRegion> fButtonAnimation;
    private float stateTime;
    private boolean nearBed = false;
    private boolean sleeping = false;
    private boolean sleepAnimationPlaying = false;
    private boolean sleepEnd = false;

    // Animation variables
    private float fadeAlpha = 0f;
    private float animationTimer = 0f;
    private final float FADE_DURATION = 3.0f;
    private final float SLEEP_DURATION = 1.0f;

    // UI components
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;
    private final float INTERACTION_DISTANCE = 20f;

    // Player sleeping position offset
    private final float BED_OFFSET_X = 0f;
    private final float BED_OFFSET_Y = 0f;

    public SleepSystem(Vector2 bedPosition, float bedWidth, float bedHeight, Player player, GameCamera gameCamera) {
        this.bedPosition = new Vector2(bedPosition);
        this.bedBounds = new Rectangle(bedPosition.x - bedWidth/2, bedPosition.y - bedHeight/2,
            bedWidth, bedHeight);
        this.player = player;
        this.playerOriginalPosition = new Vector2();
        this.camera = gameCamera;
        // Initialize UI components
        this.font = new BitmapFont();
        this.font.setColor(Color.WHITE);
        initFont();
        this.shapeRenderer = new ShapeRenderer();
        fButton = new Texture("FbuttonAni.png");
        createAnimations();
        stateTime = 0f;
        currentFrame = fButtonAnimation.getKeyFrame(0);
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;
        currentFrame = fButtonAnimation.getKeyFrame(stateTime * 2, true);
        // Check if player is near bed
        Vector2 playerPos = player.getPosition();
        float distance = playerPos.dst(bedPosition);
        nearBed = distance <= INTERACTION_DISTANCE && !sleeping;

        // Handle sleep input
        if (nearBed && Gdx.input.isKeyJustPressed(Input.Keys.F) && !sleepAnimationPlaying) {
            startSleep();
        }

        // Update sleep animation
        if (sleepAnimationPlaying) {
            updateSleepAnimation(deltaTime);
            sleepEnd = true;
        }
    }

    private void startSleep() {
        sleepAnimationPlaying = true;
        sleeping = true;
        playerOriginalPosition.set(player.getPosition());
        animationTimer = 0f;
        fadeAlpha = 0f;

        // Move player to bed position with offset
        player.setX(bedPosition.x + BED_OFFSET_X);
        player.setY(bedPosition.y + BED_OFFSET_Y);

        // Set player to appropriate sleeping state (stand down for sleeping)
        player.standStill();
    }

    private void updateSleepAnimation(float deltaTime) {
        animationTimer += deltaTime * 10;

        // Phase 1: Fade to black
        if (animationTimer <= FADE_DURATION) {
            fadeAlpha = animationTimer / FADE_DURATION;
        }
        // Phase 2: Stay black (sleeping)
        else if (animationTimer <= FADE_DURATION + SLEEP_DURATION) {
            fadeAlpha = 1f;
        }
        // Phase 3: Fade back to normal
        else if (animationTimer <= FADE_DURATION * 2 + SLEEP_DURATION) {
            float fadeOutProgress = (animationTimer - FADE_DURATION - SLEEP_DURATION) / FADE_DURATION;
            fadeAlpha = 1f - fadeOutProgress;
        }
        // Phase 4: Animation complete
        else {
            fadeAlpha = 0f;
            sleepAnimationPlaying = false;
            sleeping = false;
            // Move player away from bed (below the bed)
            player.setX(bedPosition.x + 10);
            player.setY(bedPosition.y - 10); // Move below bed
            player.standStill(); // Reset to standing state
        }
    }

    public void render(SpriteBatch batch) {
        // Render interaction prompt
        if (nearBed && !sleepAnimationPlaying) {
            // Calculate text position above the bed
            float textX = bedPosition.x; // Center the text
            float textY = bedPosition.y; // Above the bed

            batch.end();
            batch.setProjectionMatrix(camera.getCamera().combined);
            batch.begin();
            batch.draw(currentFrame, textX - 9, textY + 10,20, 20);
//            font.draw(batch, "Press   to go to bed", textX, textY);
        }
    }

    public void renderOverlay(SpriteBatch batch) {
        // Render sleep fade overlay
        if (sleepAnimationPlaying && fadeAlpha > 0) {
            batch.end(); // End sprite batch to use shape renderer

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, fadeAlpha);
            shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            shapeRenderer.end();

            batch.begin(); // Resume sprite batch
        }
    }

    // Check if player should be blocked from moving (during sleep animation)
    public boolean shouldBlockPlayerMovement() {
        return sleepAnimationPlaying;
    }

    // Getters
    public boolean isSleeping() {
        return sleeping;
    }

    public boolean isSleepAnimationPlaying() {
        return sleepAnimationPlaying;
    }

    public boolean isNearBed() {
        return nearBed;
    }

    // For debugging - render bed bounds and interaction area
    public void renderDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // Render bed bounds
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(bedBounds.x, bedBounds.y, bedBounds.width, bedBounds.height);

        // Render interaction circle
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.circle(bedPosition.x, bedPosition.y, INTERACTION_DISTANCE);

        // Render bed center point
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.circle(bedPosition.x, bedPosition.y, 3);

        shapeRenderer.end();
    }

    public void dispose() {
        font.dispose();
        shapeRenderer.dispose();
    }

    // Utility methods for customization
    public void setInteractionDistance(float distance) {
        // Note: This is a simple setter, you might want to validate the value
    }
    public boolean getSleepEnd(){
        return sleepEnd;
    }
    public void setSleepEnd(boolean status){
        sleepEnd = status;
    }
    public void setSleepDuration(float duration) {
        // Note: This would require modifying the final field, so it's just a placeholder
    }
    private void createAnimations() {
        TextureRegion[][] tmp = TextureRegion.split(
            fButton,
            fButton.getWidth() / 2,
            fButton.getHeight());

        TextureRegion[] fButtonFrames = new TextureRegion[2];

        for (int i = 0; i < 2; i++) {
            fButtonFrames[i] = tmp[0][i];
        }

        fButtonAnimation = new Animation<>(2, fButtonFrames);
    }
    private void initFont() {
        // Tạo generator từ file Pixellari.ttf
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
            Gdx.files.internal("fonts/PressStart2P.ttf"));

        // Thiết lập tham số font
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 6; // Kích thước font
        parameter.color = Color.WHITE;
//        parameter.borderWidth = 1;
//        parameter.borderColor = Color.BLACK;

        // Tạo font từ generator và parameter
        font = generator.generateFont(parameter);
    }
}
