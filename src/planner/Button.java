package planner;

import java.awt.Graphics;
import java.awt.Point;

public interface Button
{
	void tick(Point mousePos, boolean mouseDown);
	void render(Graphics g, int x, int y, int width, int height);
	boolean isHovering();
}
