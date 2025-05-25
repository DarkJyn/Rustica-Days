package com.mygdx.game.entities.animations;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class WateringEffect {
    private Animation<TextureRegion> animation;
    private float x, y;
    private float stateTime = 0.15f;
    private boolean finished = false;

    public WateringEffect(float x, float y, Animation<TextureRegion> animation) {
        this.x = x;
        this.y = y;
        this.animation = animation;
    }

    public void update(float delta) {
        stateTime += delta;
        if (animation.isAnimationFinished(stateTime)) {
            finished = true;
        }
    }

    public void render(Batch batch) {
        if (!finished) {
            TextureRegion frame = animation.getKeyFrame(stateTime);
            int frameIndex = animation.getKeyFrameIndex(stateTime);
            int frameWidth = frame.getRegionWidth();
            int totalMove = frameWidth/3;
            float offsetX = -frameIndex * (totalMove / (animation.getKeyFrames().length - 1));
            batch.draw(frame, x + offsetX, y);
        }
    }

    public boolean isFinished() {
        return finished;
    }
}
