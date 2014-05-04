package carRace;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.*;

/**
 * This is a Class or player's car
 * Including its geometric shape and skin picture
 * 
 * @author Yikai Gong
 */
public class Car{
	private Image skin;
	private float speed, acceleration,direction;
	private float position[];
	private Shape shape;
	private Image skinD;
	private String skinName;
	
	//constructor
	public Car(String filePath) throws SlickException{
		skin = new Image(filePath);
		skinName=filePath.split("/")[3].substring(0,2);
		speed = 0;
		acceleration=0;
		position=new float[]{0,0};
		direction = 0;
		shape= new Rectangle(0,0,20,40);
	}
	
	public Car(String filePath, float[] position, float direction) throws SlickException{
		skin = new Image(filePath);
		skinName=filePath.split("/")[3].substring(0,2);
		speed = 0;
		acceleration=0;
		this.position=position;
		this.direction=direction;
		shape= new Rectangle(0,0,20,40);
	}
	
	//action
	public void acclerate(){
		if(speed<=4)
			speed+=acceleration;			
	}
	
	public void brake (float deceleration){
		if(speed>0)
			this.speed-=deceleration;
		else if(speed>-0.6)
			this.speed-=deceleration/4;
	}
	
	public void slowDown(float deceleration){
		if(speed>deceleration)
			this.speed-=deceleration;
		else if(speed<-deceleration)
			this.speed+=deceleration;
		else
			this.speed=0;
	}
	
	public void turn(float velocity){   //right:+ left:-
		direction+=velocity;
	}
	
	//set
	public synchronized void setAcceleration(float acceleration){
		this.acceleration = acceleration;
	}
	
	public synchronized void setPosition(float x, float y){
		position[0]=x;
		position[1]=y;
	}
	
	public synchronized void setDirection(float direction){
		this.direction=direction; 
	}
	
	public synchronized void setSpeed(float speed){
		this.speed=speed;
	}
	
	//get
	public float getSpeed(){
		return this.speed;
	}
	
	public Image getSkin(){
		return this.skin;
	}

	//get skins in different rotation
	public Image getSkin2() throws SlickException{
		float directionR = (float) Math.toRadians(direction);
		
		while(directionR<-Math.PI/16)
			directionR+=2*Math.PI;
		while(directionR>=31*Math.PI/16)
			directionR-=2*Math.PI;
		if(directionR>=-Math.PI/16&&directionR<Math.PI/16)
			skinD= new Image("./data/car/carR/"+skinName+"1.png");
		else if(directionR>=Math.PI/16&&directionR<3*Math.PI/16)
			skinD= new Image("./data/car/carR/"+skinName+"2.png");
		else if(directionR>=3*Math.PI/16&&directionR<5*Math.PI/16)
			skinD= new Image("./data/car/carR/"+skinName+"3.png");
		else if(directionR>=5*Math.PI/16&&directionR<7*Math.PI/16)
			skinD= new Image("./data/car/carR/"+skinName+"4.png");
		else if(directionR>=7*Math.PI/16&&directionR<9*Math.PI/16)
			skinD= new Image("./data/car/carR/"+skinName+"5.png");
		else if(directionR>=9*Math.PI/16&&directionR<11*Math.PI/16)
			skinD= new Image("./data/car/carR/"+skinName+"6.png");
		else if(directionR>=11*Math.PI/16&&directionR<13*Math.PI/16)
			skinD= new Image("./data/car/carR/"+skinName+"7.png");
		else if(directionR>=13*Math.PI/16&&directionR<15*Math.PI/16)
			skinD= new Image("./data/car/carR/"+skinName+"8.png");
		else if(directionR>=15*Math.PI/16&&directionR<17*Math.PI/16)
			skinD= new Image("./data/car/carR/"+skinName+"9.png");
		else if(directionR>=17*Math.PI/16&&directionR<19*Math.PI/16)
			skinD= new Image("./data/car/carR/"+skinName+"10.png");
		else if(directionR>=3*Math.PI/19&&directionR<21*Math.PI/16)
			skinD= new Image("./data/car/carR/"+skinName+"11.png");
		else if(directionR>=21*Math.PI/16&&directionR<23*Math.PI/16)
			skinD= new Image("./data/car/carR/"+skinName+"12.png");
		else if(directionR>=23*Math.PI/16&&directionR<25*Math.PI/16)
			skinD= new Image("./data/car/carR/"+skinName+"13.png");
		else if(directionR>=25*Math.PI/16&&directionR<27*Math.PI/16)
			skinD= new Image("./data/car/carR/"+skinName+"14.png");
		else if(directionR>=27*Math.PI/16&&directionR<29*Math.PI/16)
			skinD= new Image("./data/car/carR/"+skinName+"15.png");
		else //(directionR>=29*Math.PI/16&&directionR<31*Math.PI/16)
			skinD= new Image("./data/car/carR/"+skinName+"16.png");


		skinD.drawCentered(0,0); //Slick 2D
		return skinD;
	}
    
	public float[] getPosition(){
		return position;
	}
	
    public float getDirection(){
    	return direction;
    }
    
    public Shape getShape(float d, float x, float y){
    	Shape t = shape;
    	t= t.transform(Transform.createRotateTransform((float) Math.toRadians(direction),t.getCenterX(),t.getCenterY()));
    	t.setLocation(15+x-position[0], 5+y+position[1]);
    	t= t.transform(Transform.createRotateTransform(d,400,300));
    	return t;
    }
}
