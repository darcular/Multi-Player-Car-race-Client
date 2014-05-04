package carRace;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * An RMI remote object interface
 * 
 * @author Yikai Gong
 */
public interface GameRoomInterface  extends Remote{
	
	//set
	public boolean setSize(int newSize) throws RemoteException;
	public void setName(String name) throws RemoteException;
	public String join(String playerName) throws RemoteException;
	public void quit(String playerName) throws RemoteException;
	public void setUserState(String username, int state) throws RemoteException;
	public void setUserPosition(String username, float x, float y)throws RemoteException;
	public void setUserDirection(String username, float direction)throws RemoteException;
	public void setUserMapInfo(String username, float x, float y, float direction)throws RemoteException;
	public void setUserSkinNum(String username, int i)throws RemoteException;
	public void setUserCurrentLap(String username, boolean isFinishedOne)throws RemoteException;
	public void setUserCurrentLap(String username, int i)throws RemoteException;
	public void setLap(int i)throws RemoteException;
	public void setUserConnectionTime(String username, long time)throws RemoteException;

	
	//get
	public int getSize() throws RemoteException;	
	public String getName() throws RemoteException;
	public ArrayList<String> getPlayerNameList()throws RemoteException;
	public ArrayList<String> getPlayerState()throws RemoteException;
	public int getUserState(String username) throws RemoteException ;
	public float[] getUserPosition(String username)throws RemoteException;
	public float getUserDirection(String username)throws RemoteException;
	public float[] getUserMapInfo(String username)throws RemoteException;
	public int getUserSkinNum(String username) throws RemoteException;
	public int getUserCurrentLap(String username) throws RemoteException;
	public int getLap() throws RemoteException;
	public int getUserRank(String username) throws RemoteException;
	public int getRoomState() throws RemoteException;
	public boolean getUserCollision(String username)throws RemoteException;
}
