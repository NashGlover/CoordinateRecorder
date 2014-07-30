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
    
    Map<Character, Coordinate> anchorPoints = new HashMap<Character, Coordinate>();
    String distanceStart = "Distance from beginning to end: ";
    ArrayList<Coordinate> aionavCoordinates = new ArrayList<Coordinate>(300);
    Boolean marked = false;
    Boolean correcting = false;
    Boolean mark = false;
    private Integer markCount = 0;
    private double x = 0, y = 0, z = 0;
    private double firstX = 0, firstY = 0, firstZ = 0;
    private double beginX = 0, beginY = 0, beginZ = 0;
    private double differenceX = 0, differenceY = 0, differenceZ = 0;
    private double anchorX = 0, anchorY = 0;
    JTextArea workingText;
    public Boolean running;
    int correctCount = 0;
    Boolean first = true;
    Socket clientSocket;
    
    CoordinateRecorder(JTextArea inTextArea){
        System.out.println("In CoordinateRecorder");
        workingText = inTextArea;
    }
    
    public void setAnchor (Coordinate _coordinate, char _character) {
        System.out.println("In set anchor");
        Coordinate coordinate = _coordinate;
        Character character = new Character(_character);
        anchorPoints.put(character, coordinate);
        System.out.println("New Anchor Size: " + anchorPoints.size());
    }
    
    public void atAnchor(char character) {
        Coordinate coordinate = (Coordinate)anchorPoints.get(character);
        firstX = coordinate.getX();
        firstY = coordinate.getY();
        firstZ = coordinate.getZ();
        first = true;
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
                System.out.printf("CURRENT TIME: %d%n", currTime);
                bytesRead = in.read(messageByte);
                ByteBuffer buffer = ByteBuffer.wrap(messageByte);
                
                if (bytesRead == 56 || bytesRead == 32)
                {
                        length = buffer.getInt();
                        packet_type = buffer.getInt(4);
                        if (bytesRead == 32){
                                System.out.println("Heartbeat");
                                
                                length = buffer.getInt();
                                packet_type = buffer.getInt(4);
                                device_id = buffer.getLong(8);
                                device_id2 = buffer.getLong(16);
                                timestamp = buffer.getLong(24);
                                System.out.println(timestamp);
                                Date currentDate = new Date(timestamp);
                                DateFormat df = new SimpleDateFormat("dd:MM:yy:HH:mm:ss");
                                System.out.println("Current Date: " + df.format(currentDate));
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
                                        
                                        Coordinate coordinate = new Coordinate(x, y, z, timestamp);
                                        
                                        aionavCoordinates.add(coordinate);
                                        
                                        if (first)
                                        {
                                           differenceX = x - firstX;
                                           differenceY = y - firstY;
                                           differenceZ = z = firstZ;
                                           first = false;
                                        }
                                        
                                        x = x - differenceX;
                                        y = y - differenceY;
                                        z = z - differenceZ;
                                        
                                        Iterator it = anchorPoints.entrySet().iterator();
                                        System.out.println("Size of anchorPoints " + anchorPoints.size());
                                        while (it.hasNext()) {
                                            System.out.println("In the while loop");
                                            Map.Entry pair = (Map.Entry)it.next();
                                            Character characterPair = ((Character)pair.getKey());
                                            Double entryX = ((Coordinate)pair.getValue()).getX();
                                            Double entryY = ((Coordinate)pair.getValue()).getY();
                                            Double distance = Math.sqrt(Math.pow(entryX-x,2) + Math.pow(entryY-y,2));
                                            System.out.println("Distance: " + distance);
                                            if (distance < 2.0) {
                                                workingText.append("At anchor point " + characterPair + "\n");
                                            }
                                        }
                                        
                                        currLine = String.format("Time: " + dateString + "x: %.3f y: %.3f z: %.3f%n%n", x, y, z);
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
        catch (Exception e) {
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
    
    public void run()
    {
        System.out.println("Hello from a thread!");
        startRecording();
        return;
    }
    
}