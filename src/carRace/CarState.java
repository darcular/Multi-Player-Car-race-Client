package carRace;

import java.rmi.RemoteException;
import java.util.ArrayList;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * This is an Playing state in game container; ID:2.
 * Allow player to control their car to race.
 * An 'update' Thread is used to communicate with the server to update essential information
 * For rendering, the client will do coordinate transform to draw the picture around the player's car
 * 
 * @author Yikai Gong
 */
public class CarState extends BasicGameState{
	private String username;
	private int roomSize;
	private Car myCar;
	private ArrayList<String> otherPlayerNames;
	private ArrayList<Car> cars;
	private Track track;
	private ArrayList<Image> skins;
	private Image map,mySkin;
	private boolean isTurnLeft=false;
	private boolean isTurnRight=false;
	private boolean isRun=false;
	private boolean isBrake=false;
	private boolean isLost;
	private float[] shiftPosition;
	private Shape carShape;
	private Shape finishLine1;
	private Shape finishLine2;
	private Shape exteriorBoundary;
	private Shape innerBoundary;
	private Shape bound;
	private ArrayList<Shape> otherCarShapes;
	private double[] onFinishTime;
	private boolean[] isOnFinishLine;
	private int isCrossLine;      //0: no, 1:forward, 2:reverse
	private int lap;
	private int currentLap;
	private String gameResult;
	private boolean isMenuOpen;
	private float[] startPosition;
	private Thread update;
	private boolean isCollide;
	private SpriteSheet explosionpic;
	private Animation explosion;

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		if(Console.isStart==false)
			return;
		otherCarShapes=new ArrayList<Shape>();
		shiftPosition = new float[]{0,0};
		skins= new ArrayList<Image>();
		cars = new ArrayList<Car>();
		otherPlayerNames = new ArrayList<String>();
		onFinishTime = new double[] {0.0,0.0};
		isOnFinishLine =new boolean[]{false,false};
		username=Console.getUsername();
		currentLap=0;
		gameResult=null;
		isCrossLine=0;
		isMenuOpen=false;
		isCollide=false;
		isLost=false;
		explosionpic =new SpriteSheet("data/emap.png",60,60);
		explosion =new Animation(explosionpic,100);
		try {
			roomSize=Console.room.getSize();
			lap = Console.room.getLap();
			int skinNum = Console.room.getUserSkinNum(username);
			String path;
			if(skinNum==0)
				path="./data/car/cb1.png";
			else
				path="./data/car/cr1.png";
			myCar = new Car(path,Console.room.getUserPosition(username),Console.room.getUserDirection(username));
			startPosition=new float[]{myCar.getPosition()[0],myCar.getPosition()[1],myCar.getDirection()};
			ArrayList<String> allNames =Console.room.getPlayerNameList();
			for(String name:allNames)
				if(!name.equals(username))
					otherPlayerNames.add(name);
			for(int i=0;i<otherPlayerNames.size();i++)
			{
				skinNum = Console.room.getUserSkinNum(otherPlayerNames.get(i));
				if(skinNum==0)
					path="./data/car/cb1.png";
				else
					path="./data/car/cr1.png";
				cars.add(new Car(path,Console.room.getUserPosition(otherPlayerNames.get(i)),Console.room.getUserDirection(otherPlayerNames.get(i))));
			}		
			shiftPosition[0]=myCar.getPosition()[0];
			shiftPosition[1]=-myCar.getPosition()[1];
		} catch (RemoteException e) {
			Console.foundServer=false;
		} catch (Exception e) {
			e.getStackTrace();
		}	
		mySkin = myCar.getSkin();
		for(int i =0;i<otherPlayerNames.size();i++)
			skins.add(cars.get(i).getSkin2());
		track = new Track("./data/track/track.png");
		map = track.getMap();
		carShape = new Rectangle(50,50,20,40);
		carShape.setLocation(390, 280);
		exteriorBoundary = track.getShape((float) Math.toRadians(-myCar.getDirection()),shiftPosition[0],shiftPosition[1]);
		innerBoundary= track.getInnerBoundry((float) Math.toRadians(-myCar.getDirection()),shiftPosition[0],shiftPosition[1]);
		finishLine1 = track.getFinishLine1((float) Math.toRadians(-myCar.getDirection()),860+shiftPosition[0],820+shiftPosition[1]);
		finishLine2 = track.getFinishLine2((float) Math.toRadians(-myCar.getDirection()),845+shiftPosition[0],820+shiftPosition[1]);
		bound = track.getBound((float) Math.toRadians(-myCar.getDirection()),shiftPosition[0],shiftPosition[1]);
		for(int i =0;i<otherPlayerNames.size();i++)
			otherCarShapes.add(cars.get(i).getShape((float) Math.toRadians(myCar.getDirection()), shiftPosition[0], shiftPosition[1]));
		
		//update Thread
		update = new Thread(){
			public synchronized void run(){
				while(Console.threadPlaying){
					try {
						Console.room.setUserMapInfo(username, myCar.getPosition()[0], myCar.getPosition()[1], myCar.getDirection());
						isCollide=Console.room.getUserCollision(username);
						//if no one has exited, just download their position information for rendering
						if(Console.room.getPlayerNameList().size()==(otherPlayerNames.size()+1))
						{
							for(int i=0; i<otherPlayerNames.size(); i++){
								float[] mapInfo = Console.room.getUserMapInfo(otherPlayerNames.get(i));
								cars.get(i).setPosition(mapInfo[0], mapInfo[1]);
								cars.get(i).setDirection(mapInfo[2]);
							}
						}
						//if some one has exited, update the players list
						else
						{
							System.out.println("start update players");//
							ArrayList<String> allNames = Console.room.getPlayerNameList();
							allNames.remove(username);
							otherPlayerNames=allNames;
							int skinNum;
							String path;
							cars.clear();
							skins.clear();
							otherCarShapes.clear();
							for(int i=0;i<otherPlayerNames.size();i++)
							{
								skinNum = Console.room.getUserSkinNum(otherPlayerNames.get(i));
								if(skinNum==0)
									path="data/car/cb1.png";
								else
									path="data/car/cr1.png";
								try {
									cars.add(new Car(path,Console.room.getUserPosition(otherPlayerNames.get(i)),Console.room.getUserDirection(otherPlayerNames.get(i))));
								} catch (SlickException e) {
									e.printStackTrace();
								}
								try {
									skins.add(cars.get(i).getSkin2());
								} catch (SlickException e) {
									e.printStackTrace();
								}
								otherCarShapes.add(cars.get(i).getShape((float) Math.toRadians(myCar.getDirection()), shiftPosition[0], shiftPosition[1]));
							}
							System.out.println("update players");//
						}
							
					} catch (RemoteException e) {
						Console.foundServer=false;
					} catch (Exception e) {
						e.getStackTrace();
					}
				}
			}
		};
		update.start();

		//tell the server is ready
		try {
			Console.room.setUserState(username, 3);
		} catch (RemoteException e) {
			Console.foundServer=false;
		}
	}
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		Input input=container.getInput();
		
		//handle the disconnection
		if(Console.foundServer==false)
		{
			isLost=true;
			Console.setUsername(null);
		}
			
		if(isLost)
		{
			if(input.isKeyPressed(Keyboard.KEY_B))
			{
				try {
					Console.threadPlaying=false;
					update.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				game.getState(0).init(container, game);
				game.enterState(0);
			}
			return;
		}
		
		//car control
		myCar.setAcceleration((float) 0.03);
		if(input.isKeyDown(203))
			isTurnLeft=true;
		else
			isTurnLeft=false;
		if(input.isKeyDown(205))
			isTurnRight=true;
		else
			isTurnRight=false;
		if(input.isKeyDown(200))
			isRun=true;
		else
			isRun=false;
		if(input.isKeyDown(208))
			isBrake=true;
		else
			isBrake=false;
		
		if(input.isKeyPressed(Keyboard.KEY_ESCAPE))
			isMenuOpen = !isMenuOpen;
		
		if(isMenuOpen)
		{
			if(input.isKeyDown(Keyboard.KEY_R))
			{
				reset();
				isMenuOpen=false;
			}
			else if(input.isKeyDown(Keyboard.KEY_E))
			{
				try {
					exit();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				isMenuOpen=false;
				game.getState(0).init(container, game);
				game.enterState(0);
			}		
		}
				
		if(isRun)
			myCar.acclerate();
		else if(!isBrake)
			myCar.slowDown((float) 0.03);

		if(isTurnLeft&&(myCar.getSpeed()!=0))
			myCar.turn((float) -1.9);
		if(isTurnRight&&(myCar.getSpeed()!=0))
			myCar.turn((float) +1.9);
		if(isBrake)
			myCar.brake((float) 0.09);
		
		float position[] = myCar.getPosition();
		float dx=(float) Math.sin(Math.toRadians(-myCar.getDirection()))*myCar.getSpeed()*(float)3.2;
		float dy=(float) Math.cos(Math.toRadians(-myCar.getDirection()))*myCar.getSpeed()*(float)3.2;
		myCar.setPosition(position[0]+dx, position[1]-dy); //!
		
		shiftPosition[0]+=dx;
		shiftPosition[1]+=dy;
		
        //skin update
		if(otherPlayerNames.size()==skins.size())  //prevent from under updating someone's leave
			for(int i=0; i<skins.size();i++)
				skins.set(i, cars.get(i).getSkin2()); 
		
		//shape update
		finishLine1 = track.getFinishLine1((float) Math.toRadians(-myCar.getDirection()),800+shiftPosition[0],620+shiftPosition[1]);
		finishLine2 = track.getFinishLine2((float) Math.toRadians(-myCar.getDirection()),785+shiftPosition[0],620+shiftPosition[1]);
		exteriorBoundary = track.getShape((float) Math.toRadians(-myCar.getDirection()),shiftPosition[0],shiftPosition[1]);
		innerBoundary= track.getInnerBoundry((float) Math.toRadians(-myCar.getDirection()),shiftPosition[0],shiftPosition[1]);
		bound = track.getBound((float) Math.toRadians(-myCar.getDirection()),shiftPosition[0],shiftPosition[1]);
		if(otherPlayerNames.size()==otherCarShapes.size()) //prevent from under updating someone's leave
			for(int i=0; i<otherCarShapes.size();i++)
				otherCarShapes.set(i, cars.get(i).getShape((float) Math.toRadians(-myCar.getDirection()), shiftPosition[0]+375, shiftPosition[1]+275));
		
		//for judging lap++ or lap--
		if(carShape.intersects(finishLine1)&&isOnFinishLine[0]==false)
		{
			onFinishTime[0]=System.currentTimeMillis();
			isOnFinishLine[0]=true;
		}
		else if(!carShape.intersects(finishLine1))
			isOnFinishLine[0]=false;

		if(carShape.intersects(finishLine2)&&isOnFinishLine[1]==false)
		{
			onFinishTime[1]=System.currentTimeMillis();
			isOnFinishLine[1]=true;
		}
		else if(!carShape.intersects(finishLine2))
			isOnFinishLine[1]=false;
		
		if(carShape.intersects(finishLine1)&&carShape.intersects(finishLine2)&&isCrossLine==0)
		{
			if(onFinishTime[0]>onFinishTime[1])
				isCrossLine=2;
			else
				isCrossLine=1;
		}	
		
		if((!carShape.intersects(finishLine1))&&(!carShape.intersects(finishLine2))&&isCrossLine!=0)
		{
			if(isCrossLine==2)
			{
				try {
					Console.room.setUserCurrentLap(username, false);
					currentLap--;
				} catch (RemoteException e) {
					Console.foundServer=false;
				} //--	
			}
			else if(isCrossLine==1)
			{
				try {
					Console.room.setUserCurrentLap(username, true);
					currentLap++;
				} catch (RemoteException e) {
					Console.foundServer=false;
				} //++
			}
			isCrossLine=0;
		}
		
		if(currentLap>=(lap+1)&&gameResult==null)  //if finished ask for result
		{
			int rank=0;
			try {
				rank = Console.room.getUserRank(username);
			} catch (RemoteException e) {
				Console.foundServer=false;
			}
			if(rank!=0)  //means this player's game has finished
			{
				gameResult="Your rank is: "+rank;
			}		
		}
		
		//detect collision between player's car and the track boundary
		if(carShape.intersects(innerBoundary)||innerBoundary.contains(carShape)||carShape.intersects(exteriorBoundary)||exteriorBoundary.contains(carShape))
			if(myCar.getSpeed()>1.2)
				myCar.setSpeed((float) 1.2);
		
		//if player drive out of the track too far, than reset it's position
		if(carShape.intersects(bound))
			reset();
		
		//collision between cars is judged by the server, client will get the result and restart if collision occurs
		if(isCollide)
		{
			explosion.start();
			reset();
		}
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		g.setBackground(Color.gray);
        //handle disconnection
		if(isLost)
		{
			g.drawString("Lost connection to the server",290,280);
			g.drawString("B: Back to Entrance", 610, 550);
			return;
		}
		
		//draw the player's car in the middle of window statically.
		//Do coordinate transform and draw the other elements dynamically around the car
	    float position[] = myCar.getPosition();
	    float direction = -myCar.getDirection();
		map.setCenterOfRotation(-position[0]+400, position[1]+300);
		map.setRotation(direction);
		map.draw(shiftPosition[0],shiftPosition[1],(float)1);
        for(int i=0; i<skins.size(); i++)
        {
        	skins.get(i).setCenterOfRotation(25-position[0]+cars.get(i).getPosition()[0], 25+position[1]-cars.get(i).getPosition()[1]);
    		skins.get(i).setRotation(direction);
    		skins.get(i).draw(375+shiftPosition[0]-cars.get(i).getPosition()[0],275+shiftPosition[1]+cars.get(i).getPosition()[1]);
        }	
		mySkin.drawCentered(400, 300);
		
		//draw the results if finished
		if(gameResult!=null)
		{
			g.drawString("Finished", 350, 200);
			g.drawString("Rank: "+gameResult, 300, 230);
    	}			

		if(isCollide)
		{
			g.setColor(Color.orange);
			g.drawString("Collision!!!!",300,300);
			g.setColor(Color.white);
			explosion.draw(370, 270);
		}
		
		//draw some player's current information
		g.setColor(Color.white);
		g.draw(bound);
		g.drawString("Lap: "+currentLap+"/"+lap, 15, 30);
		g.drawString("Speed: "+new Integer ((int) (myCar.getSpeed()*140/4))+"KM/H", 15, 45);
		g.drawString("Your name: "+username,15,60);
		g.drawString("Other players: ",15,75);
		g.drawString("Delay:"+Console.delay+"ms", 680, 0);
		try{
			for (int i=0; i<otherPlayerNames.size();i++ )
				g.drawString(otherPlayerNames.get(i), 15, 90+i*15);
		}catch (Exception e)
		{}
		
		//draw the menu
		if(isMenuOpen)
		{
			g.drawString("R: Reset", 300, 300);
			g.drawString("E: Exit", 300, 330);
		}
	}

	@Override
	public int getID() {
		return 2;
	}
	
	//reset car position, also tell the server
	public void reset()
	{
		myCar.setSpeed(0);
		myCar.setAcceleration(0);
		myCar.setPosition(startPosition[0], startPosition[1]);
		shiftPosition[0]=myCar.getPosition()[0];
		shiftPosition[1]=-myCar.getPosition()[1];
		myCar.setDirection(startPosition[2]);
		if(gameResult==null)
		{
			currentLap=0;
			try {
				Console.room.setUserCurrentLap(username, 0);
			} catch (RemoteException e) {
				Console.foundServer=false;
			}
		}
	}
	
	//exit the game and back to Entrance
	public void exit() throws InterruptedException
	{
		try {
			Console.threadPlaying=false;   //close the update thread
			update.join();   //wait for the update thread finished
			Console.setUsername(null);
			Console.room.setUserState(username, 4);   //tell the server I am leaving
		} catch (RemoteException e) {
			Console.foundServer=false;
		}
	}
}
