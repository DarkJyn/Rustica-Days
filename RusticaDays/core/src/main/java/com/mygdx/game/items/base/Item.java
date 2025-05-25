package com.mygdx.game.items.base;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;

public abstract class Item {
    protected String name;
    protected String description;
    protected int price;
    protected int quantity;
    protected TextureRegion texture;

    public enum ItemType {
        SEED, HARVEST, TOOL, ANIMAL_PRODUCT, ANIMAL_FEED
    }

    protected ItemType type;

    public Item(String name, String description, int price, ItemType type, String texturePath) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.type = type;
        this.quantity = 1;
        this.texture = null;
        if (texturePath != null && !texturePath.isEmpty()) {
            try {
                this.texture = new TextureRegion(new Texture(texturePath));
            } catch (Exception e) {
                System.err.println("[Item] Không thể load texture: " + texturePath + ", lỗi: " + e.getMessage());
            }
        }
    }

    public Item(String name, String description, int price, ItemType type, TextureRegion texture) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.type = type;
        this.quantity = 1;
        this.texture = texture;
    }

    public void render(SpriteBatch batch, float x, float y, float width, float height) {
        if (texture != null) {
            batch.draw(texture, x, y, width, height);
        }
    }

    public void increaseQuantity(int amount) {
        this.quantity += amount;
    }

    public boolean decreaseQuantity(int amount) {
        if (quantity >= amount) {
            quantity -= amount;
            return true;
        }
        return false;
    }

    public boolean isType(ItemType type) {
        return this.type == type;
    }

    public boolean canStackWith(Item other) {
        return other != null && other.getName().equals(this.name);
    }

    public int getTotalPrice() {
        return price * quantity;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPrice() { return price; }
    public int getSellPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public ItemType getType() { return type; }
    public TextureRegion getTexture() { return texture; }

    public void dispose() {
        // Không dispose texture ở đây vì có thể dùng chung
    }
}
