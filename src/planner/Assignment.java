package planner;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

@SuppressWarnings("serial")
public class Assignment implements java.io.Serializable, Button
{
	private StringBuilder title;
	private boolean hovering;
	private boolean editing;
	private Rectangle rect;
	private int cursorTime;
	private boolean cursorVisible;
	private int editIndex;
	private int editStart;
	
	Assignment(String title)
	{
		this.title = new StringBuilder(title);
		rect = new Rectangle();
		hovering = false;
		cursorTime = 0;
		cursorVisible = false;
		editIndex = 0;
		editStart = -1;
	}
	
	Assignment()
	{
		this.title = new StringBuilder("");
		rect = new Rectangle();
		hovering = false;
		cursorTime = 0;
		cursorVisible = false;
		editIndex = 0;
		editStart = -1;
	}
	
	@Override
	public void tick(Point mousePos, boolean mouseDown)
	{
		if(mousePos!=null)
		{
			hovering = rect.contains(mousePos);
		}
	}
	
	@Override
	public void render(Graphics g, int x, int y, int width, int height)
	{
		rect.setBounds(x, y, width, height);
		
		FontMetrics fm = g.getFontMetrics();
		int charLimit = (int)(width/fm.getMaxAdvance());
		String displayText = title.toString();
		
		if(title.length()>charLimit)
		{
			if(editing)
			{
				if(editStart==-1)
				{
					editStart = title.length()-charLimit;
				}
				if(editIndex>(editStart+charLimit-1))
				{
					editStart += (editIndex-(editStart+charLimit-1));
				}
				if(editIndex<(editStart-1))
				{
					editStart -= ((editStart-1)-editIndex);
				}
				displayText = title.toString().substring(editStart,editStart+charLimit);
			}else{
				displayText = title.toString().substring(0,charLimit-3) + "...";
			}
		}else
		{
			editStart = -1;
		}
		
		int textHeight = fm.getAscent();
		int textWidth = fm.stringWidth(displayText);
		int yOffset = ((height - textHeight)/2) + textHeight;
		int xOffset = (width-textWidth)/2;
		
		if(hovering)
		{
			g.setColor(Color.DARK_GRAY);
		}else {
			g.setColor(Color.GRAY);
		}
		
		g.drawString(displayText,x+xOffset,y+yOffset);
		
		g.drawRect(x, y, width, height);
		
		if(cursorVisible&&editing)
		{
			int indexOffset;
			if(editStart<0)
			{
				indexOffset = fm.stringWidth(title.substring(0,editIndex+1));
			}else {
				indexOffset = fm.stringWidth(title.substring(editStart,editIndex+1));
			}
			g.drawRect(x+xOffset+indexOffset,y+yOffset-textHeight,0,textHeight);
		}
		
		cursorTime++;
		if(cursorTime>=7)
		{
			cursorVisible = !cursorVisible;
			cursorTime=0;
		}
	}
	
	public boolean isHovering()
	{
		return hovering;
	}
	
	void insert(int index,char c)
	{
		title.insert(index+1,c);
	}
	
	void deleteChar(int index)
	{
		title.deleteCharAt(index);
		editStart--;
	}
	
	int length()
	{
		return title.length();
	}
	
	void setEditing(boolean editing)
	{
		this.editing = editing;
	}
	
	void setEditIndex(int index)
	{
		editIndex = index;
	}

	String getTitle() {
		// TODO Auto-generated method stub
		return title.toString();
	}
}