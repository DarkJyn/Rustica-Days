package com.mygdx.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.GameLaucher;
import com.mygdx.game.MainMenuScreen;
import com.mygdx.game.MainApplication;

public class PauseScreen implements Screen {
    private final GameLaucher game;
    private final Stage stage;
    private final SpriteBatch batch;
    private final Texture backgroundTexture;
    private final Texture titleTexture;
    private final Texture exitButtonTexture;
    private final Texture resumeButtonTexture;
    private final Texture volumeButtonTexture;
    private final Texture volumeMuteButtonTexture;
    private final BitmapFont font;
    private boolean isVisible = false;
    private boolean isMuted = false;
    private Image volumeButton;
    private TextureRegion[] volumeRegions;
    private TextureRegion[] volumeMuteRegions;

    // Button states
    private static final int NORMAL = 0;
    private static final int HOVER = 1;
    private static final int PRESSED = 2;

    public PauseScreen(GameLaucher game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport());
        this.backgroundTexture = new Texture(Gdx.files.internal("TutorialBG.png"));
        this.titleTexture = new Texture(Gdx.files.internal("RusticaDays.png"));
        this.exitButtonTexture = new Texture(Gdx.files.internal("exitButton.png"));
        this.resumeButtonTexture = new Texture(Gdx.files.internal("ResumeButton.png"));
        this.volumeButtonTexture = new Texture(Gdx.files.internal("VolumeButton.png"));
        this.volumeMuteButtonTexture = new Texture(Gdx.files.internal("VolumeMuteButton.png"));
        this.font = new BitmapFont();

        createUI();
    }

    private void createUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);

        // Background
        Image background = new Image(new TextureRegion(backgroundTexture));
        float scale = 0.8f;
        float scaledWidth = Gdx.graphics.getWidth() * scale;
        float scaledHeight = (backgroundTexture.getHeight() * scaledWidth) / backgroundTexture.getWidth();
        background.setSize(scaledWidth, scaledHeight);
        background.setPosition(
            (Gdx.graphics.getWidth() - scaledWidth) / 2,
            (Gdx.graphics.getHeight() - scaledHeight) / 2
        );
        stage.addActor(background);

        // Title Image
        Image titleImage = new Image(new TextureRegion(titleTexture));
        float titleScale = 0.9f;
        float titleWidth = titleTexture.getWidth() * titleScale;
        float titleHeight = titleTexture.getHeight() * titleScale;
        titleImage.setSize(titleWidth, titleHeight);
        titleImage.setPosition(
            (Gdx.graphics.getWidth() - titleWidth) / 2,
            (Gdx.graphics.getHeight() - titleHeight) / 2 + 130
        );
        stage.addActor(titleImage);

        // Create button regions
        float buttonScale = 7.2f;

        // Resume button regions
        float resumeButtonWidth = resumeButtonTexture.getWidth() / 3f;
        float resumeButtonHeight = resumeButtonTexture.getHeight();
        TextureRegion[] resumeRegions = new TextureRegion[3];
        for (int i = 0; i < 3; i++) {
            resumeRegions[i] = new TextureRegion(resumeButtonTexture,
                (int)(i * resumeButtonWidth), 0,
                (int)resumeButtonWidth, (int)resumeButtonHeight);
        }

        // Exit button regions
        float exitButtonWidth = exitButtonTexture.getWidth() / 3f;
        float exitButtonHeight = exitButtonTexture.getHeight();
        TextureRegion[] exitRegions = new TextureRegion[3];
        for (int i = 0; i < 3; i++) {
            exitRegions[i] = new TextureRegion(exitButtonTexture,
                (int)(i * exitButtonWidth), 0,
                (int)exitButtonWidth, (int)exitButtonHeight);
        }

        // Volume button regions
        float volumeButtonWidth = volumeButtonTexture.getWidth() / 3f;
        float volumeButtonHeight = volumeButtonTexture.getHeight();
        volumeRegions = new TextureRegion[3];
        for (int i = 0; i < 3; i++) {
            volumeRegions[i] = new TextureRegion(volumeButtonTexture, 
                (int)(i * volumeButtonWidth), 0, 
                (int)volumeButtonWidth, (int)volumeButtonHeight);
        }

        // Volume mute button regions
        float volumeMuteButtonWidth = volumeMuteButtonTexture.getWidth() / 3f;
        float volumeMuteButtonHeight = volumeMuteButtonTexture.getHeight();
        volumeMuteRegions = new TextureRegion[3];
        for (int i = 0; i < 3; i++) {
            volumeMuteRegions[i] = new TextureRegion(volumeMuteButtonTexture, 
                (int)(i * volumeMuteButtonWidth), 0, 
                (int)volumeMuteButtonWidth, (int)volumeMuteButtonHeight);
        }

        // Create buttons with animation
        Image resumeButton = new Image(resumeRegions[NORMAL]);
        Image exitButton = new Image(exitRegions[NORMAL]);
        volumeButton = new Image(volumeRegions[NORMAL]);

        // Set button sizes
        resumeButton.setSize(resumeButtonWidth * buttonScale, resumeButtonHeight * buttonScale);
        exitButton.setSize(exitButtonWidth * buttonScale, exitButtonHeight * buttonScale);
        volumeButton.setSize(volumeButtonWidth * buttonScale, volumeButtonHeight * buttonScale);

        // Add hover and pressed effects for Resume button
        resumeButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                resumeButton.setDrawable(new TextureRegionDrawable(resumeRegions[HOVER]));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                resumeButton.setDrawable(new TextureRegionDrawable(resumeRegions[NORMAL]));
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                resumeButton.setDrawable(new TextureRegionDrawable(resumeRegions[PRESSED]));
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (x >= 0 && x <= resumeButton.getWidth() && y >= 0 && y <= resumeButton.getHeight()) {
                    resumeButton.setDrawable(new TextureRegionDrawable(resumeRegions[HOVER]));
                    hide();
                } else {
                    resumeButton.setDrawable(new TextureRegionDrawable(resumeRegions[NORMAL]));
                }
            }
        });

        // Add hover and pressed effects for Exit button
        exitButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                exitButton.setDrawable(new TextureRegionDrawable(exitRegions[HOVER]));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                exitButton.setDrawable(new TextureRegionDrawable(exitRegions[NORMAL]));
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                exitButton.setDrawable(new TextureRegionDrawable(exitRegions[PRESSED]));
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (x >= 0 && x <= exitButton.getWidth() && y >= 0 && y <= exitButton.getHeight()) {
                    exitButton.setDrawable(new TextureRegionDrawable(exitRegions[HOVER]));
                    game.getMainApplication().returnToMainMenu();
                } else {
                    exitButton.setDrawable(new TextureRegionDrawable(exitRegions[NORMAL]));
                }
            }
        });

        // Add hover and pressed effects for Volume button
        volumeButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                if (isMuted) {
                    volumeButton.setDrawable(new TextureRegionDrawable(volumeMuteRegions[HOVER]));
                } else {
                    volumeButton.setDrawable(new TextureRegionDrawable(volumeRegions[HOVER]));
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                if (isMuted) {
                    volumeButton.setDrawable(new TextureRegionDrawable(volumeMuteRegions[NORMAL]));
                } else {
                    volumeButton.setDrawable(new TextureRegionDrawable(volumeRegions[NORMAL]));
                }
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (isMuted) {
                    volumeButton.setDrawable(new TextureRegionDrawable(volumeMuteRegions[PRESSED]));
                } else {
                    volumeButton.setDrawable(new TextureRegionDrawable(volumeRegions[PRESSED]));
                }
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (x >= 0 && x <= volumeButton.getWidth() && y >= 0 && y <= volumeButton.getHeight()) {
                    isMuted = !isMuted;
                    if (isMuted) {
                        volumeButton.setDrawable(new TextureRegionDrawable(volumeMuteRegions[HOVER]));
                        game.getSoundManager().setMuted();
                    } else {
                        volumeButton.setDrawable(new TextureRegionDrawable(volumeRegions[HOVER]));
                        game.getSoundManager().setUnMuted();
                    }
                } else {
                    if (isMuted) {
                        volumeButton.setDrawable(new TextureRegionDrawable(volumeMuteRegions[NORMAL]));
                    } else {
                        volumeButton.setDrawable(new TextureRegionDrawable(volumeRegions[NORMAL]));
                    }
                }
            }
        });

        // Layout
        mainTable.add().height(titleHeight + 130).row();

        Table buttonTable = new Table();
        buttonTable.add(resumeButton).width(resumeButtonWidth * buttonScale).height(resumeButtonHeight * buttonScale).padRight(50);
        buttonTable.add(exitButton).width(exitButtonWidth * buttonScale).height(exitButtonHeight * buttonScale).padRight(50);
        buttonTable.add(volumeButton).width(volumeButtonWidth * buttonScale).height(volumeButtonHeight * buttonScale);

        mainTable.add(buttonTable).padTop(-230);

        stage.addActor(mainTable);
    }

    public void show() {
        isVisible = true;
        Gdx.input.setInputProcessor(stage);
    }

    public void hide() {
        isVisible = false;
        Gdx.input.setInputProcessor(null);
    }

    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public void render(float delta) {
        if (!isVisible) return;

        batch.begin();
        float scale = 0.8f;
        float scaledWidth = Gdx.graphics.getWidth() * scale;
        float scaledHeight = (backgroundTexture.getHeight() * scaledWidth) / backgroundTexture.getWidth();
        float x = (Gdx.graphics.getWidth() - scaledWidth) / 2;
        float y = (Gdx.graphics.getHeight() - scaledHeight) / 2;
        batch.draw(backgroundTexture, x, y, scaledWidth, scaledHeight);
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        backgroundTexture.dispose();
        titleTexture.dispose();
        exitButtonTexture.dispose();
        resumeButtonTexture.dispose();
        volumeButtonTexture.dispose();
        volumeMuteButtonTexture.dispose();
        font.dispose();
    }
}
