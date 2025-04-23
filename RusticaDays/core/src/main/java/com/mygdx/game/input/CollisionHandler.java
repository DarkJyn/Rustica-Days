package com.mygdx.game.input;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;

public class CollisionHandler {
    private TiledMap map;
    private MapObjects collisionObjects;
    private boolean debugRender;

    public CollisionHandler(TiledMap map, String objectLayerName) {
        this.map = map;
        this.debugRender = false;

        // Lấy objects từ layer có tên được chỉ định
        MapLayer layer = map.getLayers().get(objectLayerName);
        if (layer != null) {
            this.collisionObjects = layer.getObjects();
            System.out.println("Loaded collision layer: " + objectLayerName + " with " + this.collisionObjects.getCount() + " objects");
        } else {
            System.out.println("Warning: Layer " + objectLayerName + " not found in map");
            this.collisionObjects = null;
        }
    }

    public void setDebugRender(boolean debugRender) {
        this.debugRender = debugRender;
    }

    public boolean isDebugRender() {
        return debugRender;
    }

    // Kiểm tra xem nhân vật có va chạm với bất kỳ object nào không
    public boolean checkCollision(Rectangle playerBounds) {
        if (collisionObjects == null) return false;

        for (MapObject object : collisionObjects) {
            if (object instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
                if (Intersector.overlaps(rectangle, playerBounds)) {
                    return true;
                }
            } else if (object instanceof EllipseMapObject) {
                Ellipse ellipse = ((EllipseMapObject) object).getEllipse();
                // Chuyển đổi ellipse thành rectangle để kiểm tra va chạm đơn giản
                Rectangle ellipseBounds = new Rectangle(
                    ellipse.x, ellipse.y, ellipse.width, ellipse.height
                );
                if (Intersector.overlaps(ellipseBounds, playerBounds)) {
                    return true;
                }
            } else if (object instanceof PolygonMapObject) {
                Polygon polygon = ((PolygonMapObject) object).getPolygon();
                // Kiểm tra va chạm giữa polygon và rectangle
                if (checkPolygonRectangleCollision(polygon, playerBounds)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Phương thức kiểm tra va chạm giữa polygon và rectangle
    private boolean checkPolygonRectangleCollision(Polygon polygon, Rectangle rectangle) {
        // Chuyển rectangle thành polygon
        Polygon rectPoly = new Polygon(new float[] {
            rectangle.x, rectangle.y,
            rectangle.x + rectangle.width, rectangle.y,
            rectangle.x + rectangle.width, rectangle.y + rectangle.height,
            rectangle.x, rectangle.y + rectangle.height
        });

        // Kiểm tra va chạm giữa hai polygon
        return Intersector.overlapConvexPolygons(polygon, rectPoly);
    }

    // Phương thức để di chuyển nhân vật với kiểm tra va chạm
    // Đã đơn giản hóa để chỉ xử lý một hướng di chuyển mỗi lần
    public Vector2 moveWithCollisionCheck(Rectangle playerBounds, float deltaX, float deltaY) {
        Vector2 newPosition = new Vector2(playerBounds.x, playerBounds.y);

        // Nếu không có di chuyển, trả về vị trí hiện tại
        if (deltaX == 0 && deltaY == 0) {
            return newPosition;
        }

        // Tạo một bản sao của playerBounds để kiểm tra va chạm
        Rectangle tempBounds = new Rectangle(playerBounds);

        // Di chuyển theo trục X hoặc Y (chỉ một trong hai)
        if (deltaX != 0) {
            tempBounds.x += deltaX;
            if (!checkCollision(tempBounds)) {
                newPosition.x += deltaX;
            }
        } else if (deltaY != 0) {
            tempBounds.y += deltaY;
            if (!checkCollision(tempBounds)) {
                newPosition.y += deltaY;
            }
        }

        return newPosition;
    }

    // Phương thức vẽ tất cả các vùng va chạm
    public void renderCollisions(ShapeRenderer shapeRenderer) {
        if (!debugRender || collisionObjects == null) return;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);

        for (MapObject object : collisionObjects) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
            } else if (object instanceof EllipseMapObject) {
                Ellipse ellipse = ((EllipseMapObject) object).getEllipse();
                shapeRenderer.ellipse(ellipse.x, ellipse.y, ellipse.width, ellipse.height);
            } else if (object instanceof PolygonMapObject) {
                Polygon polygon = ((PolygonMapObject) object).getPolygon();
                float[] vertices = polygon.getTransformedVertices();

                for (int i = 0; i < vertices.length; i += 2) {
                    int nextI = (i + 2) % vertices.length;
                    shapeRenderer.line(
                        vertices[i], vertices[i+1],
                        vertices[nextI], vertices[nextI+1]
                    );
                }
            }
        }

        shapeRenderer.end();
    }
}
