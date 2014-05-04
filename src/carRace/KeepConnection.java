package carRace;

import java.rmi.Naming;
import java.rmi.RemoteException;
/**
 * This Thread is used to keep connection and rebuild connection with the server
 * 
 * @author Yikai Gong
 */
public class KeepConnection extends Thread{
	public synchronized void run(){
		try {
			Console.room = (GameRoomInterface)Naming.lookup("rmi://"+Console.ip+"/room");
		} catch (Exception e1) {
			e1.getStackTrace();
		}
		while(true){
			try
			{
				if(Console.foundServer==false)
				{
					Console.threadPlaying=false;
					Console.threadWaiting=false;
					Console.room = (GameRoomInterface)Naming.lookup("rmi://"+Console.ip+"/room");   //try to reconnect to the server
					Console.room.setName("null");
					Console.foundServer=true;
					System.out.println(Console.foundServer);
				}
				else if(Console.getUsername()!=null)
				{
					long temp = System.currentTimeMillis();
					Console.room.setUserConnectionTime(Console.getUsername(), System.currentTimeMillis());  //update current time to server after join a game room
					Console.delay=(int)(System.currentTimeMillis()-temp);
				}
				this.wait(1000);
			}catch(RemoteException e){
				Console.foundServer=false;
			}catch(Exception e){
				e.getStackTrace();
			}		
		}
	}
}
