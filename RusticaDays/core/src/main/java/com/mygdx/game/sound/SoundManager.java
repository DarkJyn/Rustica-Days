package com.mygdx.game.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Timer;

public class SoundManager {
    private Music bgm;
    private Sound healingSound;
    private Sound inventoryOpenSound;
    private Sound pickingUpItemsSound;
    private Sound plantSound;
    private Sound waterSound;
    private Sound shoppingSound;
    private Sound levelUpSound;

    public SoundManager() {
        bgm = Gdx.audio.newMusic(Gdx.files.internal("audio/Background_music.mp3"));
        bgm.setLooping(true); // lặp vô hạn
        bgm.setVolume(0.5f); // điều chỉnh nếu muốn nhỏ hơn
        bgm.play(); // bắt đầu nhạc nền

        healingSound = Gdx.audio.newSound(Gdx.files.internal("audio/Healing_mana.mp3"));
        inventoryOpenSound = Gdx.audio.newSound(Gdx.files.internal("audio/Inventory_open.mp3"));
        pickingUpItemsSound = Gdx.audio.newSound(Gdx.files.internal("audio/Picking_up_items.mp3"));
        plantSound = Gdx.audio.newSound(Gdx.files.internal("audio/Planting_tree.mp3"));
        waterSound = Gdx.audio.newSound(Gdx.files.internal("audio/watering.mp3"));
        shoppingSound = Gdx.audio.newSound(Gdx.files.internal("audio/Shopping.mp3"));
        levelUpSound = Gdx.audio.newSound(Gdx.files.internal("audio/Level_up.mp3"));
    }

    public void playHealingSound() {
        bgm.setVolume(0.25f); // giảm âm lượng
        healingSound.play(1.0f);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                bgm.setVolume(0.5f); // tăng lại sau 1s
            }
        }, 1.5f);

    }

    public void playInventoryOpenSound() {
        bgm.setVolume(0.25f); // giảm âm lượng
        inventoryOpenSound.play(1.0f);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                bgm.setVolume(0.5f); // tăng lại sau 1s
            }
        }, 1.5f);
    }

    public void playPickingUpItemsSound() {
        bgm.setVolume(0.25f); // giảm âm lượng
        pickingUpItemsSound.play(1.0f);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                bgm.setVolume(0.5f); // tăng lại sau 1s
            }
        }, 1.5f);

    }

    public void playPlantSound() {
        bgm.setVolume(0.25f); // giảm âm lượng
        plantSound.play(1.0f);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                bgm.setVolume(0.5f); // tăng lại sau 1s
            }
        }, 1.5f);

    }

    public void playWaterSound() {
        bgm.setVolume(0.25f); // giảm âm lượng
        waterSound.play(1.0f);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                bgm.setVolume(0.5f); // tăng lại sau 1s
            }
        }, 1.5f);

    }

    public void playShoppingSound() {
        bgm.setVolume(0.25f); // giảm âm lượng
        shoppingSound.play(1.0f);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                bgm.setVolume(0.5f); // tăng lại sau 1s
            }
        }, 1.5f);

    }
    public void playLevelUpSound() {
        bgm.setVolume(0.25f); // giảm âm lượng
        levelUpSound.play(1.0f); // phát hiệu ứng

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                bgm.setVolume(0.5f); // tăng lại sau 1s
            }
        }, 1.5f);
    }

    public void dispose() {
        bgm.dispose();
        healingSound.dispose();
        inventoryOpenSound.dispose();
        pickingUpItemsSound.dispose();
        plantSound.dispose();
        waterSound.dispose();
        shoppingSound.dispose();
        levelUpSound.dispose();
        //fishSound.dispose();
    }
}
