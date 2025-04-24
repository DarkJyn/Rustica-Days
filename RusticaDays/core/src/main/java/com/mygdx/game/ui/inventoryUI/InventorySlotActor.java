package com.mygdx.game.ui.inventoryUI;

import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.game.inventory.InventorySlot;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class InventorySlotActor extends Stack {
//    private final InventorySlot slot;
//    private final ImageButton iconButton;
//    private final Label quantityLabel;
//    private Texture iconTexture; // Manually manage icon texture
//
//    public InventorySlotActor(InventorySlot slot) {
//        this.slot = slot;
//
//        // Tạo BitmapFont và LabelStyle cho Label
//        BitmapFont font = new BitmapFont();  // Hoặc sử dụng font của bạn từ file .fnt
//        Label.LabelStyle labelStyle = new Label.LabelStyle();
//        labelStyle.font = font;
//
//        this.iconTexture = new Texture("[Rustica] Asset/Cute_Fantasy/Cute_Fantasy/Icons/Outline/empty_slot.png"); // Default icon texture
//        this.iconButton = new ImageButton(new TextureRegionDrawable(iconTexture));
//
//        // Khởi tạo Label với LabelStyle hợp lệ
//        this.quantityLabel = new Label("", labelStyle);
//
//        // Thêm iconButton và quantityLabel vào Stack
//        this.add(iconButton);
//        this.add(quantityLabel);
//        update();
//    }
//
//    public void update() {
//        if (slot.isEmpty()) {
//            iconButton.getStyle().up = new TextureRegionDrawable(new Texture("[Rustica] Asset/Cute_Fantasy/Cute_Fantasy/Icons/Outline/empty_slot.png"));
//            quantityLabel.setText("");
//        } else {
//            iconTexture = slot.getItem().getIconTexture();
//            iconButton.getStyle().up = new TextureRegionDrawable(iconTexture);
//            quantityLabel.setText(String.valueOf(slot.getQuantity()));
//        }
//    }
}
