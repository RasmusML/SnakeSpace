package snake.mvc.game.view;

import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import snake.mvc.game.model.Vector2;

public class Renderer {

  private FitViewport viewport;

  public Renderer(FitViewport viewport) {
    this.viewport = viewport;
  }

  public void clear(Color color, Canvas canvas) {
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.setFill(color);
    gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
  }

  public void drawRectLine(double x, double y, double width, double height, Color color, double thickness, Canvas canvas) {
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.setStroke(color);

    double scale = viewport.scale * viewport.getCameraZoom();

    gc.setLineWidth(thickness * scale);
    Vector2 world = viewport.worldToScreen(x, y);

    gc.strokeRect(world.x, world.y, width * scale, height * scale);
  }

  public void drawText(double x, double y, String text, Color color, int size, Alignment alignment, Canvas canvas) {
    GraphicsContext gc = canvas.getGraphicsContext2D();

    switch (alignment) {
      case topLeft: {
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.TOP);
        break;
      }

      case topRight: {
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.setTextBaseline(VPos.TOP);
        break;
      }

      case center: {
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        break;
      }
    }

    double scale = viewport.scale; // text doesn't get affected by the camera zoom, because it is HUD.
    Font font = Font.font("monospace", FontWeight.BOLD, size * scale);
    gc.setFont(font);
    gc.setFill(color);
    gc.fillText(text, x * scale, y * scale);
  }

  public void drawLine(double x1, double y1, double x2, double y2, Color color, double thickness, Canvas canvas) {
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.setStroke(color);

    double scale = viewport.scale * viewport.getCameraZoom();
    gc.setLineWidth(thickness * scale);

    Vector2 pos1 = viewport.worldToScreen(x1, y1);
    Vector2 pos2 = viewport.worldToScreen(x2, y2);

    gc.strokeLine(pos1.x, pos1.y, pos2.x, pos2.y);
  }

  public void drawFillPolygon(double[][] polygon, Color color, Canvas canvas) {
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.setFill(color);

    int points = polygon.length;

    double[] polyX = new double[points];
    double[] polyY = new double[points];
    for (int i = 0; i < points; i++) { //             x              y
      Vector2 pos = viewport.worldToScreen(polygon[i][0], polygon[i][1]);
      polyX[i] = pos.x;
      polyY[i] = pos.y;
    }

    gc.fillPolygon(polyX, polyY, points);
  }

  public void drawCircleLine(double x, double y, double radius, Color color, double thickness, Canvas canvas) {
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.setStroke(color);

    double scale = viewport.scale * viewport.getCameraZoom();
    gc.setLineWidth(thickness * scale);

    Vector2 position = viewport.worldToScreen(x, y);
    gc.strokeOval(position.x, position.y, radius * 2.0 * scale, radius * 2.0 * scale);
  }
}
