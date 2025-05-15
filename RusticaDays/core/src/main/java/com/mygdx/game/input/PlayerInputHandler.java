package com.mygdx.game.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.entities.Player;
import com.mygdx.game.entities.NPC;
import java.util.ArrayList;
import java.util.List;

public class PlayerInputHandler {
    private Player player;
    private TiledMap map;
    private CollisionHandler collisionHandler;
    private Rectangle playerBounds;
    private float playerSpeed;

    // Thêm danh sách NPCs và NPC đang được tương tác
    private List<NPC> npcs;
    private NPC interactingNPC;
    private boolean canInteract;

    public PlayerInputHandler(Player player, TiledMap map) {
        this.player = player;
        this.map = map;

        // Khởi tạo collision handler với tên của object layer là "Collision"
        collisionHandler = new CollisionHandler(map, "Collisions");

        // Lấy bounds từ player
        playerBounds = new Rectangle(player.getBounds());
        playerSpeed = player.getSpeed();

        // Khởi tạo danh sách NPCs trống
        npcs = new ArrayList<>();
        interactingNPC = null;
        canInteract = false;
    }

    public CollisionHandler getCollisionHandler() {
        return collisionHandler;
    }

    // Thêm phương thức để đăng ký NPC
    public void registerNPC(NPC npc) {
        if (!npcs.contains(npc)) {
            npcs.add(npc);
        }
    }

    // Thêm phương thức để gỡ bỏ đăng ký NPC
    public void unregisterNPC(NPC npc) {
        npcs.remove(npc);
        if (interactingNPC == npc) {
            interactingNPC = null;
        }
    }

    // Thêm phương thức để kiểm tra tương tác với các NPC
    private void checkNPCInteractions() {
        canInteract = false;
        interactingNPC = null;

        // Lấy tọa độ giữa của người chơi
        float playerCenterX = player.getX() + player.getWidth() / 2;
        float playerCenterY = player.getY() + player.getHeight() / 2;

        // Kiểm tra từng NPC
        for (NPC npc : npcs) {
            // Cập nhật trạng thái của NPC dựa trên vị trí người chơi
            npc.update(player);

            // Kiểm tra xem player có đang ở trong vùng tương tác với NPC không
            Rectangle interactionZone = npc.getInteractionZone();
            if (interactionZone != null &&
                interactionZone.contains(playerCenterX, playerCenterY)) {
                canInteract = true;
                interactingNPC = npc;
                break;
            }
        }
    }

    // Thêm phương thức xử lý tương tác với NPC
    public void handleNPCInteraction() {
        if (canInteract && interactingNPC != null) {
            // Xử lý phím F để tương tác với NPC
            if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
                interactingNPC.interact();
            }

            // Xử lý phím SPACE để mở cửa hàng nếu đang hiển thị hộp thoại
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                interactingNPC.openShop();
            }

            // Xử lý phím ESC để đóng cửa hàng
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) &&
                interactingNPC.isShopOpen()) {
                interactingNPC.closeShop();
            }
        }
    }

    public void processInput(float delta) {
        // Cập nhật playerBounds theo vị trí hiện tại của player
        playerBounds.setPosition(player.getX(), player.getY());

        // Xử lý input - chỉ di chuyển một hướng duy nhất
        float deltaX = 0, deltaY = 0;
        boolean hasMoved = false;

        if(!isInteractingWithNPC()) {
            // Kiểm tra phím theo thứ tự ưu tiên
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
                deltaX = -playerSpeed * delta;
                hasMoved = true;
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
                deltaX = playerSpeed * delta;
                hasMoved = true;
            } else if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
                deltaY = playerSpeed * delta;
                hasMoved = true;
            } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
                deltaY = -playerSpeed * delta;
                hasMoved = true;
            }
        }
        // Nếu không có phím nào được nhấn
        if (!hasMoved) {
            player.standStill();
            // Không return ở đây để vẫn có thể xử lý tương tác với NPC
        }
        else {
            // Cập nhật vị trí nhân vật với kiểm tra va chạm
            Vector2 newPosition = collisionHandler.moveWithCollisionCheck(playerBounds, deltaX, deltaY);

            // Cập nhật animation dựa trên hướng di chuyển
            if (deltaX < 0) {
                player.moveLeft(delta);
            }
            else if (deltaX > 0) {
                player.moveRight(delta);
            }
            else if (deltaY > 0) {
                player.moveUp(delta);
            }
            else if (deltaY < 0) {
                player.moveDown(delta);
            }

            // Cập nhật vị trí cho player
            player.setX(newPosition.x);
            player.setY(newPosition.y);
        }

        // Kiểm tra tương tác với các NPC
        checkNPCInteractions();

        // Xử lý tương tác với NPC
        handleNPCInteraction();
    }

    // Phương thức để kiểm tra xem có đang tương tác với NPC không
    public boolean isInteractingWithNPC() {
        return interactingNPC != null &&
            (interactingNPC.isShopOpen() || interactingNPC.isShowingDialog() || interactingNPC.isMenuOpen());
    }

    // Phương thức để lấy NPC đang tương tác
    public NPC getInteractingNPC() {
        return interactingNPC;
    }
}
