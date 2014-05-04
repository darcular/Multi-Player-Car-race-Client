package carRace;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
/**
 * This is an Waiting state in game container; ID:1.
 * Allow player to choose their car color, lap to race(Room admin only), and to confirm ready
 * A 'update' Thread is used to download current players' information in the game room
 * 
 * @author Yikai Gong
 */
public class WaitingState extends BasicGameState{
	private Image background;
	private int roomSize;
	private int roomState;
	private ArrayList<String> playerstates;
	private int skinNum;
	private Thread update;
	private int lap;
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		background = new Image("data/background/waitingState.jpg");
		lap =1;
		try{
	//		Console.room = (GameRoomInterface) Naming.lookup ("rmi://"+Console.ip+"/room");
			roomSize = new Integer(Console.room.getSize());
			roomState = Console.room.getRoomState();
			playerstates = Console.room.getPlayerState();
		}catch (RemoteException e){
			Console.foundServer=false;
		}catch (Exception e){
			e.getStackTrace();
		}
		update = new Thread(){
			public synchronized void run(){
				while(Console.threadWaiting){
					try {
						playerstates = Console.room.getPlayerState();
						if(playerstates!=null)
							if(!playerstates.get(0).split(",")[0].equals(Console.getUsername()))
								lap = Console.room.getLap();
					} catch (RemoteException e) {
						Console.foundServer=false;
					} catch(Exception e){
						e.getStackTrace();
					}
					try {
						wait(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		update.start();
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		background.draw(-200,50,(float) 1);
		g.setFont(new AngelCodeFont("testdata/demo2.fnt","testdata/demo2_00.tga"));		
		g.drawString("B:Back",700,550);
		if(Console.foundServer==false)
			g.drawString("Cannot find the server.",100,180);
		else if(roomState!=0) //
			g.drawString("Someone is playing in the room",50,180);
		else if(Console.select==0)//create
		{
			if(Console.isCreatedSuccessful())
			{
				g.drawString("Room size is: "+roomSize, 300, 10);
				g.drawString("Choose your car color: ", 150, 140);
				if(skinNum==0)
				{
					g.setColor(Color.blue);
					g.drawString("BLUE", 440, 140);
					g.setColor(Color.white);
				}
					
				else if(skinNum==1)
				{
					g.setColor(Color.red);
					g.drawString("RED", 440, 140);
					g.setColor(Color.white);
				}
				if(playerstates.get(0).split(",")[0].equals(Console.getUsername()))
				{
					g.drawString("Set the number of laps: ", 150, 175);
					g.setColor(Color.orange);
					g.drawString(""+lap, 440, 175);
					g.setColor(Color.white);
				}	
				else
				{
					g.drawString("Number of laps: ", 150, 175);
					g.setColor(Color.orange);
					g.drawString(""+lap, 350, 175);
					g.setColor(Color.white);
				}
				for(int i=0;i<roomSize;i++)
				{
					if(playerstates.size()>i)
					{
						String[] part =playerstates.get(i).split(",");
						if(part[0].equals(Console.getUsername()))
						{
							g.setColor(Color.yellow);
							g.drawString("(You)",70,210+i*35);
							g.setColor(Color.white);
							g.drawString("delay:"+Console.delay+"ms", 590, 210+i*35);
						}		
						g.drawString(part[0],150,210+i*35);
						if(part[1].equals("0"))
							g.drawString("Waiting..",440,210+i*35);
						if(part[1].equals("1"))
						{
							g.setColor(Color.red);
							g.drawString("Ready!",440,210+i*35);
							g.setColor(Color.white);
						}
					}
					else
						g.drawString("Waiting for player...",150,210+i*35);	
				}
			}
			else
			{
				g.drawString("Game Room is beeing used...please try to join it",100,180);
			}
				
		}
		else if(Console.select==1) //join
		{
			if(roomSize==0)
				g.drawString("No game room online avaliable, Please creat a game room first.",10,180);
			else if (!Console.isJoinedSuccessful())
				g.drawString("Sorry the game room is full.",100,180);
			else
			{
				g.drawString("Room size is: "+roomSize, 300, 10);
				g.drawString("Choose your car color: ", 150, 140);
				if(skinNum==0)
				{
					g.setColor(Color.blue);
					g.drawString("BLUE", 440, 140);
					g.setColor(Color.white);
				}	
				else if(skinNum==1)
				{
					g.setColor(Color.red);
					g.drawString("RED", 440, 140);
					g.setColor(Color.white);
				}

				if(playerstates.get(0).split(",")[0].equals(Console.getUsername()))
				{
					g.drawString("Set the number of laps: ", 150, 175);
					g.setColor(Color.orange);
					g.drawString(""+lap, 440, 175);
					g.setColor(Color.white);
				}	
				else
				{
					g.drawString("Number of laps: ", 150, 175);
					g.setColor(Color.orange);
					g.drawString(""+lap, 350, 175);
					g.setColor(Color.white);
				}
				
				for(int i=0;i<roomSize;i++)
				{
					if(playerstates.size()>i)
					{
						String[] part =playerstates.get(i).split(",");
						if(part[0].equals(Console.getUsername()))
						{
							g.setColor(Color.yellow);
							g.drawString("(You)",70,210+i*35);
							g.setColor(Color.white);
							g.drawString("delay:"+Console.delay+"ms", 590, 210+i*35);
						}	
						g.drawString(part[0],150,210+i*35);
						if(part[1].equals("0"))
							g.drawString("Waiting..",440,210+i*35);
						if(part[1].equals("1"))
						{
							g.setColor(Color.red);
							g.drawString("Ready!",440,210+i*35);
							g.setColor(Color.white);
						}
					}
					else
						g.drawString("Waiting for player...",150,210+i*35);	
				}
			}
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)throws SlickException {
		Input input = container.getInput();
	
		if(Console.isJoinedSuccessful())  //allow input when have joined successfully
		{	
			if(input.isKeyPressed(Keyboard.KEY_RIGHT))
			{
				if(skinNum==1)
					skinNum=0;
				else
					skinNum++;
			}
			if(input.isKeyPressed(Keyboard.KEY_LEFT))
			{
				if(skinNum==0)
					skinNum=1;
				else
					skinNum--;
			}
			if(playerstates.get(0).split(",")[0].equals(Console.getUsername()))
			{
				if(input.isKeyPressed(Keyboard.KEY_UP))
				{
					if(lap<8)
						lap++;
				}
				if(input.isKeyPressed(Keyboard.KEY_DOWN))
				{
					if(lap>1)
						lap--;
				}
				try {
					Console.room.setLap(lap);
				} catch (RemoteException e) {
					Console.foundServer=false;
				}
			}
			
			if(input.isKeyPressed(28))
			{
				try {
					Console.room.setUserSkinNum(Console.getUsername(), skinNum);
					Console.room.setUserState(Console.getUsername(), 1);
				} catch (RemoteException e) {
					Console.foundServer=false;
				}
			}
			if(input.isKeyPressed(Keyboard.KEY_BACK))
			{
				try {
					Console.room.setUserState(Console.getUsername(), 0);
				} catch (RemoteException e) {
					Console.foundServer=false;
				}
			}
			try {
				if(Console.room.getUserState(Console.getUsername())==2)
				{
					exit();
					Console.isStart=true;
					Console.threadPlaying=true;
					game.getState(2).init(container, game);
					game.enterState(2);
				}
			} catch (RemoteException e) {
				Console.foundServer=false;
			}		
		}
	
		if(input.isKeyPressed(Keyboard.KEY_B))
		{
			if(Console.isJoinedSuccessful())   //quit from server only when have joined successfully
			{
				try {
					Console.room.quit(Console.getUsername());
				} catch (RemoteException e) {
					Console.foundServer=false;
				}
			}
			Console.setUsername(null);
			exit();
			game.enterState(0);				
		}
	}

	@Override
	public int getID() {
		return 1;
	}
	
	public void exit(){
		Console.threadWaiting=false;
		try {
			update.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
