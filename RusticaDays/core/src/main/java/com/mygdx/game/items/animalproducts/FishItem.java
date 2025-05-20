package com.mygdx.game.items.animalproducts;

import com.mygdx.game.entities.animals.FishType;
import com.mygdx.game.items.base.Item;

public class FishItem extends Item {
    private FishType fishType;

    private static final String[] NAMES = {"Blue Fish", "Green Fish", "Yellow Fish", "Gray Fish", "Red Fish"};
    private static final String[] TEXTURES = {
        "[Rustica] Asset/Cute_Fantasy/Cute_Fantasy/Animals/Fish/Fish01.png",
        "[Rustica] Asset/Cute_Fantasy/Cute_Fantasy/Animals/Fish/Fish02.png",
        "[Rustica] Asset/Cute_Fantasy/Cute_Fantasy/Animals/Fish/Fish03.png",
        "[Rustica] Asset/Cute_Fantasy/Cute_Fantasy/Animals/Fish/Fish04.png",
        "[Rustica] Asset/Cute_Fantasy/Cute_Fantasy/Animals/Fish/Fish05.png"
    };
    private static final String[] DESCRIPTIONS = {
        "Một con cá tươi ngon.", "Một con cá tươi ngon.", "Một con cá tươi ngon.", "Một con cá tươi ngon.", "Một con cá tươi ngon."
    };
    private static final int[] PRICES = {100, 120, 150, 90, 200};

    public FishItem(FishType fishType) {
        super(
            NAMES[fishType.ordinal()],
            DESCRIPTIONS[fishType.ordinal()],
            PRICES[fishType.ordinal()],
            ItemType.ANIMAL_PRODUCT,
            TEXTURES[fishType.ordinal()]
        );
        this.fishType = fishType;
    }

    public FishType getFishType() {
        return fishType;
    }

    public static String getFishName(FishType type) {
        return NAMES[type.ordinal()];
    }
}
