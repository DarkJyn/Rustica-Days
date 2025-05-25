package com.mygdx.game.inventory;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.mygdx.game.items.base.Item;
import com.mygdx.game.ui.InventoryUI;

/**
 * Xử lý kéo thả cho hệ thống túi đồ
 */
public class DragAndDropHandler {
    private final DragAndDrop dragAndDrop;
    private final Stage stage;
    private final InventoryManager inventoryManager;
    private final InventoryUI inventoryUI;

    /**
     * Khởi tạo handler kéo thả
     */
    public DragAndDropHandler(Stage stage) {
        this.dragAndDrop = new DragAndDrop();
        this.stage = stage;
        this.inventoryManager = null;
        this.inventoryUI = null;
    }

    /**
     * Khởi tạo handler kéo thả với InventoryManager
     */
    public DragAndDropHandler(Stage stage, InventoryManager inventoryManager, InventoryUI inventoryUI) {
        this.dragAndDrop = new DragAndDrop();
        this.stage = stage;
        this.inventoryManager = inventoryManager;
        this.inventoryUI = inventoryUI;
    }

    /**
     * Đăng ký một Stack như một source có thể được kéo đi
     */
    public void addSource(final Stack sourceStack, final int sourceIndex) {
        dragAndDrop.addSource(new Source(sourceStack) {
            @Override
            public Payload dragStart(InputEvent event, float x, float y, int pointer) {
                // Kiểm tra xem slot có vật phẩm hay không
                InventorySlot slot = inventoryManager.getSlots().get(sourceIndex);
                if (slot.isEmpty()) {
                    return null; // Không có gì để kéo
                }

                Payload payload = new Payload();

                // Lưu thông tin nguồn vào payload
                payload.setObject(new int[]{sourceIndex});

                // Tạo stack mới để hiển thị khi kéo
                Stack dragActor = new Stack();
                dragActor.setSize(sourceStack.getWidth(), sourceStack.getHeight());

                // Lấy item từ slot và tạo image mới
                Item item = slot.getItem();
                if (item != null && item.getTexture() != null) {
                    Image itemImage = new Image(item.getTexture());
                    dragActor.add(itemImage);
                }

                payload.setDragActor(dragActor);
                payload.setValidDragActor(dragActor);
                payload.setInvalidDragActor(dragActor);

                return payload;
            }
        });
    }

    /**
     * Đăng ký một Stack như một target có thể nhận drop
     */
    public void addTarget(final Stack targetStack, final int targetIndex) {
        dragAndDrop.addTarget(new Target(targetStack) {
            @Override
            public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
                return true; // Cho phép drop vào bất kỳ slot nào
            }

            @Override
            public void drop(Source source, Payload payload, float x, float y, int pointer) {
                if (inventoryManager == null) return;

                // Lấy index của slot nguồn và đích
                int sourceIndex = ((int[])payload.getObject())[0];

                // Lấy slot nguồn và đích
                InventorySlot sourceSlot = inventoryManager.getSlots().get(sourceIndex);
                InventorySlot targetSlot = inventoryManager.getSlots().get(targetIndex);

                // Thực hiện việc swap items giữa hai slots
                if (!sourceSlot.isEmpty()) {
                    Item sourceItem = sourceSlot.getItem();
                    int sourceQuantity = sourceSlot.getQuantity();

                    if (targetSlot.isEmpty()) {
                        // Target trống - di chuyển item
                        targetSlot.setItem(sourceItem, sourceQuantity);
                        sourceSlot.clear();
                    } else {
                        // Target có item - thử stack nếu cùng loại
                        Item targetItem = targetSlot.getItem();
                        int targetQuantity = targetSlot.getQuantity();

                        if (sourceItem.canStackWith(targetItem)) {
                            // Stack items cùng loại
                            targetSlot.setItem(targetItem, targetQuantity + sourceQuantity);
                            sourceSlot.clear();
                        } else {
                            // Swap items khác loại
                            sourceSlot.setItem(targetItem, targetQuantity);
                            targetSlot.setItem(sourceItem, sourceQuantity);
                        }
                    }
                    // Sau khi drop, cập nhật lại UI
                    if (inventoryUI != null) {
                        inventoryUI.updateUI();
                    }
                }
            }
        });
    }

    public DragAndDrop getDragAndDrop() {
        return dragAndDrop;
    }
}
