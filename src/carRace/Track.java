package carRace;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.*;

/**
 * This is a Class for the race track.
 * Including map pictures and some geometric shapes used for collision detection
 * Coordinate transform is implemented in the methods
 * 
 * @author Xiao Ma
 */
public class Track {
	private Image map;
	private Shape finishLine1;
	private Shape finishLine2;
	private Shape outShape;
	private Shape inShape;
	private Shape bound;
	
	public Track (String path) throws SlickException{
		
		map = new Image(path);
		finishLine1 = new Rectangle(100,100,1,700);
		finishLine2 = new Rectangle(100,100,1,700);
		float [] outPoints = new float[]{20,40,20,1380,210,1150,155,1065,144,980,144,715
				,160,650,144,550
				,144,400,155,320,200,250,290,175,390,158
				,1870,158,1975,182,2045,230,2100,315,2115,400
				,2115,560,2095,650,2115,725
				,2115,980,2090,1100,1990,1205,1870,1226
				,1600,1226,1550,1202,500,1202,440,1226
				,390,1226,305,1215,210,1150
				,20,1380,2168,1380,2168,40};
		float [] inPoints = new float[]{0,130,0,720,50,800
				,115,828,235,828
				,525,758,1000,758,1300,828,1620,828,1685,790
				,1730,720,1730,120,1690,40				
				,1620,0,115,0,30,50};
		outShape = new Polygon(outPoints);
		inShape = new Polygon(inPoints);
		bound = new Rectangle(100,100,2200,1400);
	}
	public Shape getBound(float direction, float x, float y)
	{
		Shape shape = bound;
		shape.setLocation(x-130,y-130);
		shape=shape.transform(Transform.createRotateTransform(direction,400,300));
		return shape;
	}
	public Shape getShape(float direction, float x, float y)
	{
		Shape shape = outShape;
		shape.setLocation(x-100,y-100);
		shape=shape.transform(Transform.createRotateTransform(direction,400,300));
		return shape;
	}
	
	public Shape getInnerBoundry(float direction, float x, float y)
	{
		Shape shape = inShape;		
		shape.setLocation(x+145,y+138);
		shape=shape.transform(Transform.createRotateTransform(direction,400,300));
		return shape;
	}
	
	public Shape getFinishLine1(float direction, float x, float y)
	{
		Shape shape = finishLine1;
		shape.setLocation(x,y);
		shape=shape.transform(Transform.createRotateTransform(direction,400,300));
		return shape;
	}
	public Shape getFinishLine2(float direction, float x, float y)
	{
		Shape shape = finishLine2;
		shape.setLocation(x,y);
		shape=shape.transform(Transform.createRotateTransform(direction,400,300));
		return shape;
	}
	
	public Image getMap() {
		return map;
	}
}
