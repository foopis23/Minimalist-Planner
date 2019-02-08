package planner;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

public class AddButton implements Button{

	private boolean hovering;
	//private boolean pressed;
	private Rectangle rect;
	
	AddButton()
	{
		hovering = false;
		rect = new Rectangle();
	}
	
	@Override
	public void tick(Point mousePos, boolean mouseDown)
	{
		if(mousePos!=null)
		{
			hovering = rect.contains(mousePos);
			//pressed = hovering&&mouseDown;
		}
	}

	@Override
	public void render(Graphics g, int x, int y, int width, int height)
	{
		rect.setBounds(x, y, width, height);
		
		FontMetrics fm = g.getFontMetrics();
		int textHeight = fm.getMaxAscent();
		int textWidth = fm.stringWidth("+");
		int xOffset = (width-textWidth)/2;
		int yOffset = ((height-textHeight)/2)+textHeight;
		
		if(hovering)
		{
			g.setColor(Color.GRAY);
		}else {
			g.setColor(Color.LIGHT_GRAY);
		}
		
		g.drawRect(x, y, width, height);
		g.drawString("+", x+xOffset, y+yOffset);
	}

	@Override
	public boolean isHovering()
	{
		return hovering;
	}

}
