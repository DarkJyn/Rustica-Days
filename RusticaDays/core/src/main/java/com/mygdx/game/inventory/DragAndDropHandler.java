package com.mygdx.game.inventory;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class DragAndDropHandler {
    private final DragAndDrop dragAndDrop;

    public DragAndDropHandler(Stage stage) {
        dragAndDrop = new DragAndDrop();
    }

    public void addSource(final Button itemButton) {
        dragAndDrop.addSource(new Source(itemButton) {
            @Override
            public Payload dragStart(InputEvent event, float x, float y, int pointer) {
                Payload payload = new Payload();

                // Lấy background hiện tại (vật phẩm) từ button
                TextureRegionDrawable drawable = (TextureRegionDrawable) itemButton.getStyle().up;

                if (drawable != null && drawable.getRegion() != null) {
                    com.badlogic.gdx.scenes.scene2d.ui.Image dragImage =
                        new com.badlogic.gdx.scenes.scene2d.ui.Image(drawable.getRegion());
                    dragImage.setSize(32, 32);
                    payload.setDragActor(dragImage);
                }

                return payload;
            }
        });
    }


    public void addTarget(final Button targetSlot) {
        dragAndDrop.addTarget(new Target(targetSlot) {
            @Override
            public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
                return true;
            }

            @Override
            public void drop(Source source, Payload payload, float x, float y, int pointer) {
                // You can add logic to swap item positions or drop on map
            }
        });
    }

    public DragAndDrop getDragAndDrop() {
        return dragAndDrop;
    }

}
