package planner;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Application extends JPanel implements MouseListener, MouseWheelListener, KeyListener
{
	private JFrame frame;
	private boolean running;
	
	private int margin;
	private int preferredButtonWidth;
	private int preferredButtonHeight;
	
	private Assignment editButton;
	private boolean editing;
	private boolean mouseDown;
	private int editIndex;
	private int scroll;
	private int scrollMin;
	private int scrollMax;
	
	private LinkedList<Button> buttons;
	
	private Application()
	{
		super();
		frame = new JFrame("Planner");
		buttons = new LinkedList<Button>();
		editButton = null;
		editing = false;
		editIndex = 0;
		scroll = 0;
		scrollMax = 0;
		scrollMin = 0;
	}
	
	private void init()
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int panelHeight = (int)(screenSize.getHeight()*.5);
		int panelWidth = (int)(panelHeight*.6);
		margin = 10 * (panelWidth/300);
		preferredButtonWidth = panelWidth - margin;
		preferredButtonHeight = 25 * (panelHeight/500);
		
		loadAssignments();
		buttons.add(new AddButton());
		
		this.setPreferredSize(new Dimension(panelWidth,panelHeight));
		this.setRequestFocusEnabled(true);
		this.addMouseWheelListener(this);
		this.addMouseListener(this);
		this.addKeyListener(this);
		this.setBackground(Color.WHITE);
		
		frame.getContentPane().add(this);
		frame.pack();
		frame.setResizable(true);
		frame.addWindowListener(new WindowAdapter()
		{
			@Override
		    public void windowClosing(WindowEvent e)
			{
				saveAssignments();
				System.exit(0);
		    }
		});
		frame.setVisible(true);
		running = true;
	}
	
	private void tick()
	{

		if(scroll>scrollMax)
		{
			scroll=scrollMax;
		}
		
		if(scroll<scrollMin)
		{
			scroll = scrollMin;
		}
		Point pos = this.getMousePosition();
		
		if(pos!=null)
		{
			pos.translate(0,-scroll);
		}
		
		for(Button b: buttons)
		{
			b.tick(pos,mouseDown);
		}
	}
	
	private void render(Graphics g)
	{	
		int maxCols = (int)(this.getSize().getWidth()/(preferredButtonWidth+margin));
		if(maxCols<1) {maxCols=1;}
		int rowsAmount = 1;
		if(buttons.size()>0) {rowsAmount = (int) (Math.ceil((double)buttons.size()/(double)maxCols));}
		int buttonWidth = (int) ((getSize().getWidth()-((maxCols+1)*margin))/maxCols);
		int buttonHeight = preferredButtonHeight;
		int totalHeight = (rowsAmount * (buttonHeight + margin))+margin;
		g.setFont(new Font("Courier New",Font.PLAIN,(int) (buttonHeight/1.13)));
		if(totalHeight>this.getHeight())
		{
			scrollMin = this.getHeight()-totalHeight;
			int scrollY = (int)(((double)this.getHeight()/(double)totalHeight)*(double)scroll);
			g.drawRect(this.getWidth()-10,-scrollY,10,(int)(((double)this.getHeight()/(double)totalHeight)*(double)this.getHeight()));
		}else{
			scrollMin = 0;
		}
		
		g.translate(0,scroll);
		
		int i = 0;
		for(int row = 0; row<rowsAmount;row++)
		{
			for(int col =0; col<maxCols;col++)
			{
				if(i>=buttons.size()) {break;}
				
				int x = (margin*(col+1))+(col*buttonWidth);
				int y = (margin*(row+1))+(row*buttonHeight);
				
				buttons.get(i).render(g, x, y, buttonWidth, buttonHeight);
				
				i++;
			}
		}
	}
	
	public static void main(String[] args)
	{
		Application app = new Application();
		app.init();
		app.loop();
	}
	
	private void loop()
	{
		long target = 1000000000/15;
		long last = System.nanoTime();
		while(running)
		{
			long current = System.nanoTime();
			long delta = (current - last)/target;
			
			if(delta>=1)
			{
				this.requestFocus();
				tick();
				repaint();
				last = current;
			}
		}
	}
	
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		if(running)
		{
			render(g);
			g.finalize();
		}
	}
	
	private void saveAssignments()
	{
		int i=0;
		for(Button b: buttons)
		{
			try {
				Assignment a = (Assignment)b;
				FileUtilities.writeFile(a,"assignments/"+i+".planner");
			}catch(Exception e) {}
			i++;
		}
	}
	
	private void loadAssignments()
	{
		File[] files = FileUtilities.getAllFiles("assignments");
		if(files!=null)
		{
			for(File f: files)
			{
				buttons.add((Assignment)FileUtilities.readFile(f));
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{

	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if(e.getButton()==MouseEvent.BUTTON1)
		{
			mouseDown=true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		if(e.getButton()==MouseEvent.BUTTON1)
		{
			mouseDown=false;
			for(Button a: buttons)
			{
				if(a.isHovering())
				{
					try {
						if(editButton!=null)
							editButton.setEditing(false);
						
						editButton = (Assignment)a;
						editButton.setEditing(true);
						editIndex = editButton.length()-1;
						editButton.setEditIndex(editIndex);
						editing = true;
						return;
					}catch(Exception er)
					{
						Button temp = buttons.removeLast();
						buttons.add(new Assignment(""));
						editButton = (Assignment)buttons.getLast();
						editIndex = editButton.length()-1;
						editButton.setEditIndex(editIndex);
						editButton.setEditing(true);
						editing = true;
						buttons.add(temp);
						return;
					}
				}
			}
			
			if(editing)
			{
				editing = false;
				editButton.setEditing(false);
				editButton = null;
				saveAssignments();
			}
		}
		
		if(e.getButton()==MouseEvent.BUTTON3)
		{
			for(int i=0;i<buttons.size();i++)
			{
				Button a = buttons.get(i);
				if(a.isHovering())
				{
					FileUtilities.deleteFile("assignments/"+i+".planner");
					if(a==editButton)
					{
						editButton = null;
						editing = false;
					}
					buttons.remove(a);
					break;
				}
			}
			saveAssignments();
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		scroll -= e.getPreciseWheelRotation()*e.getScrollAmount()*3;
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if(editing)
		{
			if(e.getKeyCode()==KeyEvent.VK_BACK_SPACE)
			{
				if(editIndex-1>-2)
				{
					editButton.deleteChar(editIndex);
					editIndex--;
					editButton.setEditIndex(editIndex);
				}
			}
			
			if(e.getKeyCode()==KeyEvent.VK_RIGHT)
			{
				if(editIndex+1<editButton.length())
				{
					editIndex++;
					editButton.setEditIndex(editIndex);
				}
			}
			
			if(e.getKeyCode()==KeyEvent.VK_LEFT)
			{
				if(editIndex-1>-2)
				{
					editIndex--;
					editButton.setEditIndex(editIndex);
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if(editing)
		{
			if(e.getKeyCode()==KeyEvent.VK_ENTER||e.getKeyCode()==KeyEvent.VK_ESCAPE)
			{
				editing = false;
				editButton.setEditing(false);
				editButton = null;
				saveAssignments();
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		if(editing==true)
		{
			if(((int)e.getKeyChar())!=8&&((int)e.getKeyChar())!=27)
			{
				editButton.insert(editIndex,e.getKeyChar());
				editIndex++;
				editButton.setEditIndex(editIndex);
			}
		}
	}
}