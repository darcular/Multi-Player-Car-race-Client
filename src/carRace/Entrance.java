package carRace;


import java.rmi.RemoteException;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * This is an Entrance state in game container; ID:0.
 * Allow player to choose create a game room or join a game room
 * 
 * @author Yikai Gong
 */
public class Entrance extends BasicGameState{
	private Image entranceBackGround;
	private int state=0;
	private boolean wait = true;
	private String inputusername;
	private TextField nameInput;
	private Font font;

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		entranceBackGround = new Image("data/background/entrance.jpg");
		state = 0;
		inputusername="player";
		font = new AngelCodeFont("testdata/demo2.fnt","testdata/demo2_00.tga");
		nameInput = new TextField(container,font, 610,150,100,35, new ComponentListener() {
			public void componentActivated(AbstractComponent source) {
				inputusername = nameInput.getText();
		        nameInput.setFocus(false);
			}
		});
		nameInput.setText(inputusername);
		nameInput.setCursorPos(nameInput.getText().length());
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		entranceBackGround.draw(0,0,(float) 1.3);
		g.drawString(Console.ip, 650, 0);
		if(state==0)
			g.drawString("Press 'Enter' to start", 500, 500);
		if(state==1)
		{
			g.drawString("Welcome! Choose the option via keyboard", 400, 100);
			g.drawString("Type in your user name: ",400,150);
		    nameInput.render(container, g);
			g.drawString("1: Create a Game Room", 550, 200);
			g.drawString("2: Join a Game Room", 550, 250);
			g.drawString("Q: Exit",550,300);
		}
		if(state==2)
		{
			g.drawString("Please choose a room size (Press the key)", 400, 100);
			g.drawString("1: TWO PLAYERS", 550, 200);
			g.drawString("2: THREE PLAYERS", 550, 250);
			g.drawString("3: FOUR PLAYERS",550,300);
			g.drawString("B: Back",550,350);		
		}
		if(state==3)
		{
			if(wait)
				g.drawString("Loading from the server...Please wait",400,500);			
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		Input input=container.getInput();
    	if(state==0)
    		if(input.isKeyPressed(28))
    			state=1;
    	if(state==1)    
    	{
    		if(input.isKeyPressed(Keyboard.KEY_1)&&!nameInput.hasFocus()) 
    		{
    			state=2;
    			inputusername=nameInput.getText();
    		}
    		else if(input.isKeyPressed(Keyboard.KEY_2)&&!nameInput.hasFocus())
    		{
    			state=3;
    		    inputusername=nameInput.getText();
    		}
    		else if(input.isKeyDown(Keyboard.KEY_Q)&&!nameInput.hasFocus())
    			System.exit(0);
    	}
    	
    	if(state==2)   //create game room
    	{
    		if(input.isKeyPressed(Keyboard.KEY_1))
    		{
    			try {
    				Console.setCreatedSuccessful(Console.room.setSize(2));
					if(!Console.isCreatedSuccessful())
						Console.setJoinSuccessful(false);
					else
					{
						String response = Console.room.join(inputusername);
						if(response==null)
							Console.setJoinSuccessful(false);
						else
						{
							Console.setUsername(response);
							Console.setJoinSuccessful(true);
						}
					}
				} catch (RemoteException e) {
					Console.foundServer=false;
				}catch (Exception e)
				{
					e.getStackTrace();
				}
    			Console.setPlayerNum(2);
    			Console.select=0;
    			Console.threadWaiting=true;
    			game.getState(1).init(container, game);
    			game.enterState(1);
    		}
    		if(input.isKeyPressed(Keyboard.KEY_2))
    		{
    			try {
    				Console.setCreatedSuccessful(Console.room.setSize(3));
					if(!Console.isCreatedSuccessful())
						Console.setJoinSuccessful(false);
					else
					{
						String response = Console.room.join(inputusername);
						if(response==null)
							Console.setJoinSuccessful(false);
						else
						{
							Console.setUsername(response);
							Console.setJoinSuccessful(true);
						}
					}
				} catch (RemoteException e) {
					Console.foundServer=false;
				}catch (Exception e)
				{
					e.getStackTrace();
				}
    			Console.setPlayerNum(3);
    			Console.select=0;
    			Console.threadWaiting=true;
    			game.getState(1).init(container, game);
    			game.enterState(1);
    		}
    		if(input.isKeyPressed(Keyboard.KEY_3))
    		{
    			try {
    				Console.setCreatedSuccessful(Console.room.setSize(4));
					if(!Console.isCreatedSuccessful())
						Console.setJoinSuccessful(false);
					else
					{
						String response = Console.room.join(inputusername);
						if(response==null)
							Console.setJoinSuccessful(false);
						else
						{
							Console.setUsername(response);
							Console.setJoinSuccessful(true);
						}
					}
				} catch (RemoteException e) {
					Console.foundServer=false;
				}catch (Exception e)
				{
					e.getStackTrace();
				}
    			Console.setPlayerNum(4);
    			Console.select=0;
    			Console.threadWaiting=true;
    			game.getState(1).init(container, game);
    			game.enterState(1);
    		}
    		if(input.isKeyPressed(Keyboard.KEY_B))
    			state=1;
    	}
    	
    	if(state==3)   //join 
    	{
			try {
				Console.setPlayerNum(Console.room.getSize());
				String response = Console.room.join(inputusername);
				if(response == null)
					Console.setJoinSuccessful(false);
				else
				{
					Console.setUsername(response);
					Console.setJoinSuccessful(true);
				}
			} catch (RemoteException e) {
				Console.foundServer=false;
			}catch (Exception e)
			{
				e.getStackTrace();
			}
			state=1;
			Console.select=1;
			Console.threadWaiting=true;
			game.getState(1).init(container, game);
			game.enterState(1);
    	}
	}

	@Override
	public int getID() {
		return 0;
	}
	
	public void setState(int state)
	{
		this.state=state;
	}

}
