package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public interface GameObject {
    void update(float deltaTime);

    void render(SpriteBatch batch);

    Vector2 getPosition();

    float getDepth();
}
