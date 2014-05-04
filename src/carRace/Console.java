package carRace;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
/**
 * This is a Game Client console
 * 
 * @author Yikai Gong
 */
public class Console extends StateBasedGame {
	private static final int TFR=30;
	private static int numberOfPlayers =0;
	private static String username=null;
	private static boolean isJoinSuccessful = false;
	private static boolean isCreatedSuccessful=false;
	private static Thread keepConnection;
	private static Properties prop;
	public static String ip;
	public static boolean isStart= false;
	public static int select =0;
	public static boolean threadWaiting=false;
	public static boolean threadPlaying=false;
	public static boolean foundServer =true;
	public static GameRoomInterface room;
	public static int delay;
	
	public Console(String name) {
		super(name);
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		addState(new Entrance());
		addState(new WaitingState());
		addState(new CarState());
	}
	
	//main
	public static void main(String[] args) {
		AppGameContainer app;
		try {
			app = new AppGameContainer(new Console("Distributed Race Car"));
			app.setDisplayMode(800, 600, false);
			app.setTargetFrameRate(TFR);
			app.setAlwaysRender(true);
			InputStream is = new FileInputStream("./Config.properties");
			prop = new Properties();
			prop.load(is);
			ip = prop.getProperty("ip");
			System.out.println(ip);
			keepConnection = new KeepConnection();
			//start a 'help' thread first
			keepConnection.start();
			System.out.println("Connection thread start");
			//start game container
			app.start();			
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	
	//methods used to get and modify the variables in game console 
    public static void setPlayerNum(int newNum){
    	numberOfPlayers = newNum;
    }
    public static int getPlayeNum(){
    	return numberOfPlayers;
    }
    public static void setUsername(String name){
    	username = name;
    }
    public static String getUsername(){
    	return username;
    }
    public static void setJoinSuccessful(boolean tf){
    	isJoinSuccessful=tf;
    }
    public static boolean isJoinedSuccessful(){
    	return isJoinSuccessful;
    }
    public static void setCreatedSuccessful(boolean tf){
    	isCreatedSuccessful=tf;
    }
    public static boolean isCreatedSuccessful(){
    	return isCreatedSuccessful;
    }
}
