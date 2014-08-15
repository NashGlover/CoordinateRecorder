import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;

import javax.swing.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
*sock
* @author Administrator
*/
public class CoordinateRecorder extends Thread {
	
	int steps = 0;
    Map<Character, Coordinate> anchorPoints = new HashMap<Character, Coordinate>();
    String distanceStart = "Distance from beginning to end: ";
    ArrayList<Coordinate> aionavCoordinates = new ArrayList<Coordinate>(300);
    Boolean marked = false;
    Boolean correcting = false;
    Boolean mark = false;
    private double anchorDiffX = 0, anchorDiffY = 0;
    private Integer markCount = 0;
    private double x = 0, y = 0, z = 0;
    private double firstX = 0, firstY = 0, firstZ = 0;
    private double beginX = 0, beginY = 0, beginZ = 0;
    private double differenceX = 0, differenceY = 0, differenceZ = 0;
    private double anchorX = 0, anchorY = 0;
    private double anchorlessX = 0, anchorlessY = 0;
    
    double slope;
    double vector;
    
    Coordinate realOrigin;
    Coordinate firstStep;
    Coordinate lastStep;
    Coordinate lastReal;
    Coordinate realCoordinate;
    
    JTextArea workingText;
    public Boolean running;
    int correctCount = 0;
    Boolean first = true;
    Socket clientSocket;
    GraphPlot plot;
    GraphPlot anchorlessPlot = new GraphPlot();
    Boolean heading = false;
    
    double angleInDegrees;
    
    CoordinateRecorder(JTextArea inTextArea, GraphPlot _plot){
        System.out.println("In CoordinateRecorder");
        workingText = inTextArea;
        plot = _plot;
    }
    
    
    public void setAnchor (Coordinate _coordinate, char _character) {
    	System.out.println("In coordinate recorder");
        System.out.println("In set anchor");
        Coordinate coordinate = _coordinate;
        Character character = new Character(_character);
        anchorPoints.put(character, coordinate);
        System.out.println("New Anchor Size: " + anchorPoints.size());
        plot.addAnchorPoint(coordinate);
    }
    
    public void atAnchor(char character) {
        Coordinate coordinate = (Coordinate)anchorPoints.get(character);
        anchorDiffX = anchorDiffX + (coordinate.getX() - lastStep.getX());
        anchorDiffY = anchorDiffY + (coordinate.getY() - lastStep.getY());
        
        workingText.append("AnchorDiffX: " + anchorDiffX + "\n");
        workingText.append("AnchorDiffY: " + anchorDiffY + "\n");
        
        System.out.println("The difference between the current and anchor point: " + anchorDiffX + " " + anchorDiffY);
        plot.atAnchorPoint(coordinate);
       /* firstX = coordinate.getX();
        firstY = coordinate.getY();
        firstZ = coordinate.getZ();
        first = true;*/
    }
    
    void getSegInfo()
    {
        marked = true;
        if (markCount == 0) {
            beginX = x;
            beginY = y;
        }
        if (markCount%2 == 0)
        {
            workingText.append("STARTING NEW SEGMENT" + String.format("%n"));
            firstX = x;
            firstY = y;
            firstZ = z;
        }
        else {
            workingText.append("ENDING SEGMENT" + String.format("%n"));
            Double segmentDistance = Math.sqrt(Math.pow(firstX-x,2)+Math.pow(firstY-y,2));
            segmentDistance = Math.round(segmentDistance * 1000.00) / 1000.00;
            workingText.append("Segment distance: " + String.valueOf(segmentDistance) + " m\n");
        }
        markCount++;
    }
    
    void startCorrection() {
        
    	double startX;
        double startY;
        double startZ;
        
        if (correctCount == 0) {
            startX = x;
            startY = y;
            startZ = z;
        }
        
    }
    
    void startRecording() {
        System.out.println("started recording");
        double latitude, longitude, altitude;
        //double x = 0, y = 0, z = 0;
        running = true;
        int bytesRead = 0;
        byte[] messageByte = new byte[1000];
        int length;
        int packet_type;
        long device_id, device_id2;
        long timestamp = 0;
        //double latitude;
        long startTime = System.currentTimeMillis();
        long currTime;
        try {
            String currLine;
            String fileName = "log" + startTime + ".txt";
            clientSocket = new Socket("localhost", 2222);
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            int i = 0;
            
            while (running){
                currTime = System.currentTimeMillis();
             //   System.out.printf("CURRENT TIME: %d%n", currTime);
                bytesRead = in.read(messageByte);
                ByteBuffer buffer = ByteBuffer.wrap(messageByte);
                
                if (bytesRead == 56 || bytesRead == 32)
                {
                        length = buffer.getInt();
                        packet_type = buffer.getInt(4);
                        if (bytesRead == 32){
                              //  System.out.println("Heartbeat");
                                
                                length = buffer.getInt();
                                packet_type = buffer.getInt(4);
                                device_id = buffer.getLong(8);
                                device_id2 = buffer.getLong(16);
                                timestamp = buffer.getLong(24);
                              //  System.out.println(timestamp);
                                Date currentDate = new Date(timestamp);
                                DateFormat df = new SimpleDateFormat("dd:MM:yy:HH:mm:ss");
                               // System.out.println("Current Date: " + df.format(currentDate));
                        }
                        if (bytesRead == 56) {
                                System.out.println("Position Update");
                                
                                if (packet_type == 5){
                                        System.out.printf("Longitude Latitude update%n");
                                        timestamp = buffer.getLong(24);
                                        latitude = buffer.getDouble(32);
                                        longitude = buffer.getDouble(40);
                                        altitude = buffer.getDouble(48);
                                        currLine = String.format("Time: %d Latitude: %f Longitude: %f Altitude: %f%n", timestamp, latitude, longitude, altitude);
                                        System.out.println(currLine);
                                        workingText.append(currLine);
                                        //fw.flush();
                                }
                                if (packet_type == 1)
                                {
                                        double timePos;
                                        System.out.printf("x,y,z Update%n");
                                        timePos = buffer.getDouble(24);
                                        System.out.println("Timestamp as double: " + timestamp);
                                        
                                        Date currentDate = new Date(timestamp);
                                        DateFormat df = new SimpleDateFormat("MM-dd-yy HH:mm:ss ");
                                        String dateString = df.format(currentDate);
                                        
                                        x = buffer.getDouble(32);
                                        y = buffer.getDouble(40);
                                        z = buffer.getDouble(48);
                                        realCoordinate = new Coordinate(x, y, z);
                                        //workingText.append("Real x: " + x + "\n");
                                        //workingText.append("Real y: " + y + "\n");
                                        
                                        if (first)
                                        {
                                        	System.out.println("In first");
                                        	differenceX = x - firstX;
                                        	differenceY = y - firstY;
                                        	differenceZ = z = firstZ;
                                        	System.out.println("DifferenceX: " + differenceX + " DifferenceY: " + differenceY + "DifferenceZ: " + differenceZ);
                                        	first = false;
                                        }
                                        
                                        if (heading) {
	                                        if (steps == 0) {
	                                        	realOrigin = new Coordinate(x, y, z);
	                                        	//workingText.append("Real origin%n");
	                                        	x = x - differenceX;
	                                            y = y - differenceY;
	                                            z = z - differenceZ;
	                                            
	                                            anchorlessX = x;
	                                            anchorlessY = y;
	                                        }
	                                        else if (steps == 1) {
	                                        	x = x - differenceX;
	                                            y = y - differenceY;
	                                            z = z - differenceZ;
	                                            //workingText.append("x: " + x + "\n");
	                                            //workingText.append("y: " + y + "\n");
	                                            //workingText.append("lastStep X: " + lastStep.getX() + "\n");
	                                            //workingText.append("lastStep Y: " + lastStep.getY() + "\n");
	                                            double deltaX = x;
	                                            double deltaY = y;
	                                            
	                                            //workingText.append("deltaX: " + deltaX + "\n");
	                                            //workingText.append("deltaY: " + deltaY + "\n");
	                                            
	                                            angleInDegrees = Math.atan2(deltaY, deltaX) * 180/Math.PI;
	                                            //workingText.append("Angle in degrees: " + angleInDegrees + "/n");
	                                        	angleInDegrees = -angleInDegrees;
	                                        	double tempX, tempY;
	                                        	
	                                        	tempX = x*Math.cos(Math.toRadians(angleInDegrees)) - y*Math.sin(Math.toRadians(angleInDegrees));
	                                        	tempY = x*Math.sin(Math.toRadians(angleInDegrees)) + y*Math.cos(Math.toRadians(angleInDegrees));
	                                        	System.out.println("ThingX: " + tempX);
	                                        	System.out.println("ThingY: " + tempY);
	                                            x = x*Math.cos(Math.toRadians(angleInDegrees)) - y*Math.sin(Math.toRadians(angleInDegrees));
	                                    		y = tempY;
	                                            
	                                    		//workingText.append("New y: " + y + "/n");
	                                    		System.out.println("y: " + y);
	                                    		
	                                            //workingText.append("First step%n");
	                                        	firstStep = new Coordinate(x, y, z);
	                                        	
	                                        	anchorlessX = x;
	                                        	anchorlessY = y;
	                                        	//x = lastStep.getX() + distance(firstStep.getX(), lastStep.getX(), firstStep.getY(), lastStep.getY());
	                                        	//y = lastStep.getY();
	                                        }
	                                        else {
	                                        	System.out.println("In more than 1 step");
	                                        /*	double distance = distance(realCoordinate.getX(), lastReal.getX(), realCoordinate.getY(), lastReal.getY());
	                                        	workingText.append("Distance from this step and last: " + distance + "\n");
	                                        	double x1, y1;
	                                        	/*x = x - differenceX;
	                                            y = y - differenceY;
	                                            z = z - differenceZ;*/
	                                        	/*
	                                            if (x - lastReal.getX() >= 0) {
	                                            	x1 = lastReal.getX() + (distance/vector);
	                                            	y1 = lastReal.getY() + ((slope*distance)/vector);
	                                            	workingText.append("x1: " + x1);
	                                            	workingText.append("y1" + y1);
	                                            	
	                                            	double differenceX = x - x1;
	                                            	double differenceY = y- y1;
	                                            	
	                                            	x = lastStep.getX() + differenceX;
	                                            	y = lastStep.getY() + differenceY;
	                                            }
	                                            else {
	                                            	x1 = lastReal.getX() - (distance/vector);
	                                            	y1 = lastReal.getY() - ((slope*distance)/vector);
	                                            	
	                                            	workingText.append("x1: " + x1);
	                                            	workingText.append("y1" + y1);
	                                            	
	                                            	double differenceX = x - x1;
	                                            	double differenceY = y - y1;
	                                          
	                                            	workingText.append("differenceX: " + differenceX + "/n");
	                                            	
	                                            	workingText.append("differenceY: " + differenceY + "/n");
	                                            	
	                                            	x = (lastStep.getX() + distance) + differenceX;
	                                            	y = lastStep.getY() + differenceY;
	                                            }*/
	                                       
	                                        	
	                                        	double tempX, tempY;
	                                        	
	                                        	x = x - differenceX;
	                                        	y = y - differenceY;
	                                        	z = z - differenceZ;
	                                        	
	                                        	tempX = x*Math.cos(Math.toRadians(angleInDegrees)) - y*Math.sin(Math.toRadians(angleInDegrees));
	                                        	tempY = x*Math.sin(Math.toRadians(angleInDegrees)) + y*Math.cos(Math.toRadians(angleInDegrees));
	                                        	
	                                        	anchorlessX = tempX;
	                                        	anchorlessY = tempY;
	                                        	
	                                        	tempX = tempX + anchorDiffX;
	                                        	tempY = tempY + anchorDiffY;
	                                        	/*tempX = x - differenceX;
	                                            tempY = y - differenceY;*/
	                                           // tempZ = z - differenceZ;
	                                        	
	                                        	x = tempX;
	                                    		y = tempY;
	                                        }
                                        } else {
                                        	x = x - differenceX;
	                                        y = y - differenceY;
	                                        z = z - differenceZ;
	                                        
	                                        anchorlessX = x;
	                                        anchorlessY = y;
                                        }
                                        
                                        
                                        Iterator it = anchorPoints.entrySet().iterator();
                                        System.out.println("Size of anchorPoints " + anchorPoints.size());
                                        Character characterPair = null;
                                        Double oldDistance = null;
                                        while (it.hasNext()) {
                                            //System.out.println("In the while loop");
                                            Map.Entry pair = (Map.Entry)it.next();
                                            Double entryX = ((Coordinate)pair.getValue()).getX();
                                            Double entryY = ((Coordinate)pair.getValue()).getY();
                                            Double distance = Math.sqrt(Math.pow(entryX-x,2) + Math.pow(entryY-y,2));
                                            System.out.println("Distance: " + distance);
                                            if (distance < 2.0) {
                                            	if (oldDistance == null) {
                                            		oldDistance = null;
                                            	}
                                            	if (oldDistance == null || distance <= oldDistance) {
                                            		characterPair = ((Character)pair.getKey());
                                            	}
                                            	oldDistance = distance;
                                            }
                                        }
                                        System.out.println("After iterator");
                                        if (characterPair != null) {
                                        	workingText.append("At anchor point " + characterPair + "\n");
                                        }
                                        
                                        Coordinate coordinate = new Coordinate(x, y, z, timestamp);
                                        aionavCoordinates.add(coordinate);
                                        System.out.println("Anchorless X: " + anchorlessX);
                                        anchorlessPlot.addPoint(new Coordinate(anchorlessX, anchorlessY, z));
                                        plot.addPoint(coordinate);
                                        
                                        currLine = String.format("Step: " + steps + "%nTime: " + dateString + "%nx: %.3f%ny: %.3f%nz: %.3f%n%n", x, y, z);
                                        System.out.println(currLine);
                                        workingText.append(currLine);
                                        System.out.printf("FirstX: %f%n", firstX);
                                        if (marked) {
                                            System.out.println("New distance");
                                            Double distance = Math.sqrt(Math.pow((beginX-x),2)+Math.pow((beginY-y),2));
                                            distance = Math.round(distance * 1000.00) / 1000.00;
                                            Double feetDistance = distance * 3.28084;
                                            feetDistance = Math.round(feetDistance * 1000.00) / 1000.00;
                                            //System.out.printf("%f%n", distance);
                                        }
                                        //fw.flush();
                                        i++;
                                        steps++;
                                        lastStep = new Coordinate(x,y,z);
                                        lastReal = realCoordinate;
                                }
                        }
                }
            }
            Double totalDisplacement = Math.sqrt(Math.pow(beginX-x, 2)+Math.pow(beginY-y,2));
            String totalDisplacementText = "TOTAL DISPLACEMENT: " + String.valueOf(totalDisplacement) + String.format("%n");
            workingText.append(totalDisplacementText);
            clientSocket.shutdownInput();
            clientSocket.shutdownOutput();
            clientSocket.close();
            System.out.printf("Done");
        }
        catch (IOException e) {
        	System.out.println(e.getMessage());
            System.out.println("Disconnected!");
            try {
                clientSocket.shutdownInput();
                clientSocket.shutdownOutput();
                clientSocket.close();
            } catch (IOException ioException) {
                System.out.println("Error in closing socket");
            }
        }
    }
    
    public GraphPlot getAnchorlessPlot() {
    	return anchorlessPlot;
    }
    
    private double distance(double x1, double x2, double y1, double y2) {
    	return Math.sqrt(Math.pow((x2-x1), 2) + Math.pow((y2-y1), 2));
    }
    
    public void heading() {
    	System.out.println("In heading");
    	heading = true;
    }
    
    public void run()
    {
        System.out.println("Hello from a thread!");
        startRecording();
        return;
    }
    
}