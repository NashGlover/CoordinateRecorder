/**
*
* @author Administrator
*/

public class Coordinate {
    private double x;
    private double y;
    private double z;
    private double timestamp;
    private Boolean anchor;
    
    public Coordinate (double _x, double _y, double _z) {
    	x = _x;
    	y = _y;
    	z = _z;
    	anchor = false;
    }
    public Coordinate (double _x, double _y, double _z, Boolean _anchor) {
    	x = _x;
    	y = _y;
    	z = _z;
    	anchor = _anchor;
    }
    
    public Coordinate (double _x, double _y, double _z, double _timestamp) {
        x = _x;
        y = _y;
        z = _z;
        timestamp = _timestamp;
        anchor = false;
    }
    
    public Coordinate (double _x, double _y, double _z, double _timestamp, Boolean _anchor) {
        x = _x;
        y = _y;
        z = _z;
        timestamp = _timestamp;
        anchor = _anchor;
    }
    
    public void setTimestamp(double _timestamp) {
    	timestamp = _timestamp;
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public double getZ() {
        return z;
    }
    
    public double getTimestamp() {
        return timestamp;
    }
    
    public Boolean getAnchor() {
    	return anchor;
    }
}