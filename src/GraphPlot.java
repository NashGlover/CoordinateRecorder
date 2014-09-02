
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.PlotEntity;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

public class GraphPlot {
	
//	private  final int N = 25;
//    private  final double K = 273.15;
    private  final Random random = new Random();
	private  XYSeries series = new XYSeries("Test");
	private  XYPlot plot;
	
	private  NumberAxis range;
	private  NumberAxis domain;
	
	private  XYDataset xyDataset = null;
	private XYDataset anchorlessXYDataset = null;
	private int i = 0;
	private  Coordinate lastCoordinate;
	private Coordinate anchorlessLastCoordinate;

	private Stack<Double> zoomInStack;
	private Stack<Double> zoomOutStack;
	private  ArrayList<Color> colorList;
	private JFrame f;
	
	private int numPoints = 0;
	private int numAnchorPoints = 0;
	private int numAnchorlessPoints = 0;
	
	private  double labelWidth;
	private  double labelHeight;
	
	private  ChartPanel chartPanel;
	
	private  char anchorChar = 65;
	private  JFreeChart chart;
	
	private XYTextAnnotation anchorlessTextAnnotation;
	private XYTextAnnotation anchoredTextAnnotation;
	
	public JFrame getJFrame() {
		return this.f;
	}
	
	public void colorListInit() {
		colorList = new ArrayList<Color>(10);
		colorList.add(Color.BLUE);
		colorList.add(Color.GREEN);
		colorList.add(Color.BLACK);
		colorList.add(Color.CYAN);
		colorList.add(Color.YELLOW);
		colorList.add(Color.MAGENTA);
		colorList.add(Color.ORANGE);
	}
	
	public void checkTickMarks() {
		Double axisLength = plot.getDomainAxis().getRange().getLength();
		if (axisLength > 48) {
			if (axisLength < 120) {
				range.setTickUnit(new NumberTickUnit(5));
				domain.setTickUnit(new NumberTickUnit(5));
			} else {
				range.setTickUnit(new NumberTickUnit(10));
				domain.setTickUnit(new NumberTickUnit(10));
			}
		}
		else {
			range.setTickUnit(new NumberTickUnit(1));
			domain.setTickUnit(new NumberTickUnit(1));
		}
	}
	
	public void zoomIn() {
		
		Double domainLength;
		Double domainChangeLength;
		Double rangeLength;
		Double rangeChangeLength;
		
		domainLength = plot.getDomainAxis().getRange().getLength();
		rangeLength = range.getRange().getLength();
		
		if (zoomOutStack.empty()) {
			System.out.println("It's empty!");
			domainChangeLength = domainLength/8;
			rangeChangeLength = rangeLength/8;
			zoomInStack.push(domainChangeLength);
		} else {
			domainChangeLength = zoomOutStack.pop();
			rangeChangeLength = domainChangeLength;
		}
		
		domain.setRange(domain.getLowerBound()+domainChangeLength, domain.getUpperBound()-domainChangeLength);
		range.setRange(range.getLowerBound()+rangeChangeLength, range.getUpperBound()-rangeChangeLength);
		checkTickMarks();
	}
	
	public void zoomOut() {
		Double domainLength = plot.getDomainAxis().getRange().getLength();
		Double domainChangeLength;
		Double rangeLength = range.getRange().getLength();
		Double rangeChangeLength;
		
		if (zoomInStack.empty()) {
			domainChangeLength = domainLength/8;
			rangeChangeLength = rangeLength/8;
			zoomOutStack.push(domainChangeLength);
		} else {
			domainChangeLength = zoomInStack.pop();
			System.out.println("Distance from zoomInStack: " + domainChangeLength);
			rangeChangeLength = domainChangeLength;
		}
		
		domain.setRange(domain.getLowerBound()-domainChangeLength, domain.getUpperBound()+domainChangeLength);
		range.setRange(range.getLowerBound()-rangeChangeLength, range.getUpperBound()+rangeChangeLength);
		checkTickMarks();
	}
	
	public void updateAxis() {
		range = (NumberAxis) plot.getRangeAxis();
		domain = (NumberAxis) plot.getDomainAxis();
	}
	
    public  void addPoint(Coordinate coordinate) {
    	// Origin point
    	System.out.println("Coordinate x in addPoint: " + coordinate.getX());
    	System.out.println("In add point");
    	System.out.println(numPoints);
    	if (numPoints == 0) {
    		System.out.println("Origin point");
    		final XYSeries newSeries = new XYSeries(i);
    		newSeries.add(coordinate.getX(), coordinate.getY());
        	System.out.println("In addPoint. X: " + coordinate.getX() + " Y: " + coordinate.getY());
        	System.out.println("Size of series: " + series.getItemCount());
        	if (xyDataset == null) {
        		xyDataset = new XYSeriesCollection(newSeries);
        		plot.setDataset(0, xyDataset);
        	}
        	else{
        		XYSeriesCollection newDataset = (XYSeriesCollection) xyDataset;
        		newDataset.addSeries(newSeries);
        		plot.setDataset(0, newDataset);
        	}
        	XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer(0);
        	plot.getRenderer(0).setSeriesPaint(i, Color.BLUE);
        	plot.getRenderer(0).setSeriesShape(i, new Ellipse2D.Double(-3.5, -3.5, 7, 7));
        	renderer.setSeriesShapesVisible(i, true);
        	//plot.getRenderer().setSeriesShape(i, ShapeUtilities.createDiagonalCross(1, 1));
    	}
    	else {
    		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer(0);
    		if (numPoints >= 2) {
    			renderer.removeAnnotation(anchoredTextAnnotation);
    		}
    		anchoredTextAnnotation = new XYTextAnnotation("(" + (double)Math.round(coordinate.getX() * 1000) / 1000 + ", " + (double)Math.round(coordinate.getY() * 1000) / 1000 + ")" , coordinate.getX() + .1, coordinate.getY()+ .3);
    		renderer.addAnnotation(anchoredTextAnnotation);
    		final XYSeries newSeries = new XYSeries(i);
    		newSeries.add(lastCoordinate.getX(), lastCoordinate.getY());
    		newSeries.add(coordinate.getX(), coordinate.getY());
    		plot.getRenderer(0).setSeriesPaint(i, Color.RED);
    		//plot.getRenderer().setSeriesPaint(i+1, Color.RED);	
    		XYSeriesCollection newDataset = (XYSeriesCollection) xyDataset;
    		newDataset.addSeries(newSeries);
    		plot.setDataset(0, newDataset);
    	}
    	/*series.add(coordinate.getX(), coordinate.getY());
    	System.out.println("In addPoint. X: " + coordinate.getX() + " Y: " + coordinate.getY());
    	System.out.println("Size of series: " + series.getItemCount());
    	xyDataset = new XYSeriesCollection(series);
    	plot.setDataset(xyDataset);*/
    	lastCoordinate = new Coordinate(coordinate.getX(), coordinate.getY(), coordinate.getZ());
    	numPoints++;
    	i++;
    }
    
    public void addAnchorlessPoint(Coordinate coordinate) {
    	System.out.println("In addAnchorlessPoint");
    	if (numAnchorlessPoints == 0) {
    		System.out.println("Origin point");
    		final XYSeries newSeries = new XYSeries(numAnchorlessPoints);
    		newSeries.add(coordinate.getX(), coordinate.getY());
        	System.out.println("In addPoint. X: " + coordinate.getX() + " Y: " + coordinate.getY());
        	System.out.println("Size of series: " + series.getItemCount());
        	if (anchorlessXYDataset == null) {
        		System.out.println("AnchorlessXYDataset == null");
        		anchorlessXYDataset = new XYSeriesCollection(newSeries);
        		XYLineAndShapeRenderer anchorlessRenderer = new XYLineAndShapeRenderer();
        		anchorlessRenderer.setBaseShapesVisible(false);
        		anchorlessRenderer.setBaseLinesVisible(true);
        		plot.setRenderer(1, anchorlessRenderer);
        		plot.setDataset(1, anchorlessXYDataset);
        	}
        	else{
        		XYSeriesCollection newDataset = (XYSeriesCollection) anchorlessXYDataset;
        		newDataset.addSeries(newSeries);
        		plot.setDataset(1, newDataset);
        	}
        	System.out.println("Anchorless dataset series number " + anchorlessXYDataset.getSeriesCount());
    	}
    	else {
    		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer(1);
    		if (numAnchorlessPoints >= 2) {
    			renderer.removeAnnotation(anchorlessTextAnnotation);
    		}
    		anchorlessTextAnnotation = new XYTextAnnotation("(" + (double)Math.round(coordinate.getX() * 1000) / 1000 + ", " + (double)Math.round(coordinate.getY() * 1000) / 1000 + ")" , coordinate.getX() + .1, coordinate.getY()+ .3);
    		renderer.addAnnotation(anchorlessTextAnnotation);
    		final XYSeries newSeries = new XYSeries(numAnchorlessPoints);
    		newSeries.add(anchorlessLastCoordinate.getX(), anchorlessLastCoordinate.getY());
    		newSeries.add(coordinate.getX(), coordinate.getY());
    		//plot.getRenderer().setSeriesPaint(numAnchorlessPoints, Color.RED);	
    		XYSeriesCollection newDataset = (XYSeriesCollection) anchorlessXYDataset;
    		newDataset.addSeries(newSeries);
    		plot.setDataset(1, newDataset);
    	}
    	
    	XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer(1);
    	System.out.println("After the renderer");
    	plot.getRenderer(1).setSeriesPaint(numAnchorlessPoints, Color.BLUE);
    	//plot.getRenderer().setSeriesShape(numAnchorlessPoints, new Ellipse2D.Double(-3.5, -3.5, 7, 7));
    	System.out.println("Num Anchorless Points: " + numAnchorlessPoints);
    	renderer.setSeriesShapesVisible(numAnchorlessPoints, false);
    	System.out.println("After setSeriesShapesVisible");
    	//plot.getRenderer().setSeriesShape(i, ShapeUtilities.createDiagonalCross(1, 1));
    	
    	anchorlessLastCoordinate = new Coordinate(coordinate.getX(), coordinate.getY(), coordinate.getZ());
    	numAnchorlessPoints++;
    }
    
    public  void addAnchorPoint(Coordinate coordinate) {
    	System.out.println("In add anchor point.");
    	if (xyDataset == null) {
    		final XYSeries newSeries = new XYSeries(i);
    		newSeries.add(coordinate.getX(), coordinate.getY());
        	System.out.println("In addPoint. X: " + coordinate.getX() + " Y: " + coordinate.getY());
        	System.out.println("Size of series: " + series.getItemCount());
        	xyDataset = new XYSeriesCollection(newSeries);
	    	//newDataset.addSeries(newSeries);
        	plot.setDataset(0, xyDataset);
    	} else {
	    	final XYSeries newSeries = new XYSeries(i);
	    	newSeries.add(coordinate.getX(), coordinate.getY());
	    	XYSeriesCollection newDataset = (XYSeriesCollection) xyDataset;
	    	newDataset.addSeries(newSeries);
	    	plot.setDataset(0, newDataset);
    	}
    	i++;
    	//XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
    	XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer(0);
    	renderer.setSeriesLinesVisible(i-1, false);
    	renderer.setSeriesShapesVisible(i-1, true);
    	System.out.println("Just before series paint");
    	System.out.println("numAnchorPoints: " + numAnchorPoints);
    	renderer.setSeriesPaint(i-1, colorList.get(numAnchorPoints));
    	System.out.println("After series paint");
    	//renderer.setSeriesShape(i-1, new Ellipse2D.Double(-4, -4, 8, 8));
    	//renderer.setSeriesShape(i-1, ShapeUtilities.rotateShape(ShapeUtilities.createDiagonalCross(100, .1f), 40.055, 0, 0));
    	renderer.setSeriesShape(i-1, ShapeUtilities.createRegularCross(5, .5f));
    	plot.setRenderer(0, renderer);
    	//plot.addAnnotation(new XYTextAnnotation(new Character((char)(anchorChar+(char)numAnchorPoints)).toString(), coordinate.getX()+.5, coordinate.getY()+.5));
    	plot.addAnnotation(new XYTextAnnotation(new Character((char)(anchorChar+(char)numAnchorPoints)).toString(), coordinate.getX()+computePixelWidth(10), coordinate.getY()+computePixelHeight(10)));
    	numAnchorPoints++;
    }
    
    public  void atAnchorPoint(Coordinate coordinate) {
    	lastCoordinate = coordinate;
    }
    
	private  XYDataset getDataset(int n) {
		System.out.println("Get data set");
        //final XYSeries series = new XYSeries("Temp (K�)");
        double temperature;
       /* for (int length = 0; length < N; length++) {
            temperature = K + n * random.nextGaussian();
            series.add(length + 1, temperature);
        }*/
        return new XYSeriesCollection(series);
    }
	
    private  JFreeChart createChart(final XYDataset dataset) {
        JFreeChart chart = ChartFactory.createXYLineChart(
            "Map", "X", "Y", dataset,
            PlotOrientation.VERTICAL, false, false, false);
        ChartUtilities.applyCurrentTheme(chart);
        return chart;
    }
	
	public GraphPlot() {
		initComponents();
	}
	
	private void initComponents(){
		MouseEvent evt;
		colorListInit();
		f = new JFrame();
		zoomInStack = new Stack<Double>();
		zoomOutStack = new Stack<Double>();
		//f.setUndecorated(true);
		
		JButton getInfoButton = new JButton("Get Info");
		JButton zoomInButton = new JButton("Zoom In +");
		JButton zoomOutButton = new JButton("Zoom Out -");
		
		f.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				System.out.println("The frame has been resized");
			}
		});
		
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  /*      final List<XYDataset> list = new ArrayList<XYDataset>();
        for (int i = 0; i <= 10; i++) {
            list.add(getDataset(i));
        }*/
        //series.add(0, 0);
        XYDataset firstXY = new XYSeriesCollection(series);
        chart = createChart(firstXY);
        plot = (XYPlot) chart.getPlot();
        final XYPlot finalPlot = plot;
       /* plot.addChangeListener(new PlotChangeListener() {
        	
        	@Override
        	public void plotChanged(PlotChangeEvent evt) {
        		System.out.println("The chart has changed");
        	}        	
        	
        });*/
        plot.getDomainAxis().addChangeListener(new AxisChangeListener() {

			@Override
			public void axisChanged(AxisChangeEvent arg0) {
				System.out.println("Domain changed");
				
			}
        	
        });
        //Image im = new ImageIcon("C:\\Users\\Public\\Pictures\\Sample Pictures\\Desert.jpg").getImage(); 
        plot.setBackgroundPaint(null);
       // plot.setBackgroundImage(im);
       // plot.getRangeAxis().setRangeAboutValue(K, K / 5);
        range = (NumberAxis) plot.getRangeAxis();
        range.setTickUnit(new NumberTickUnit(1));
        range.setRange(-10, 10);
        domain = (NumberAxis) plot.getDomainAxis();
        domain.setRange(-10, 10);
        domain.setTickUnit(new NumberTickUnit(1));
        
        
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(new Color(150, 150, 150));
        plot.setDomainGridlinePaint(new Color(150, 150, 150));
        //plot.setDomainValue(0, 50);
        chartPanel = new ChartPanel(chart) {
            
        	
        	@Override
            public Dimension getPreferredSize() {
                return new Dimension(500, 500);
            }
        };
        
        chartPanel.setPopupMenu(null);
        chartPanel.setMouseZoomable(false);
        GraphMouseListeners listeners = new GraphMouseListeners();
        chartPanel.addMouseMotionListener(listeners);
        chartPanel.addMouseListener(listeners);
        chartPanel.addMouseWheelListener(new MouseWheelListener() {
        	@Override
        	public void mouseWheelMoved(MouseWheelEvent e) {
        		if (e.getWheelRotation() < 0) {
        			zoomIn();
        			System.out.println("Mouse wheel X: " + e.getX());
        		}
        		else {
        			zoomOut();
        		}
        	}
        	
        });
        
        /*chartPanel.addMouseMotionListener(new MouseAdapter() {

        	int startX;
        	int startY;
        	
        	int lastX;
        	int lastY;
        	
        	Boolean dragging = false;
        	
        	@Override
        	public void mousePressed(MouseEvent e) {
        		System.out.println("Clicked");
        		startX = e.getX();
        		startY = e.getY();
        	}
        	
            @Override
            public void mouseDragged(MouseEvent e) {
            	System.out.println("Mouse dragged");
            	int currX = e.getX();
            	int currY = e.getY();
            	
            	System.out.println("Start X: " + startX + "CurrX: " + e.getX());
            	
            	System.out.println("startX - currX = " + (startX-e.getX()));
            	
            	if (!dragging && Math.abs(startX-e.getX()) > 10)
            	{
            		dragging = true;
            		lastX = startX;
            		lastY = startY;
            	}
            	if (dragging) {
	            	super.mouseDragged(e);
	            	int mouseX = e.getX();
	            	int mouseY = e.getY();
	                Rectangle2D plotRectangle = chartPanel.getChartRenderingInfo().getPlotInfo().getDataArea();
	                if (mouseX >= plotRectangle.getX() && mouseX <= (plotRectangle.getX() + plotRectangle.getWidth())) {
	                	System.out.println("Dragging on plot area");
	                }
	                
	                changeXAxis(lastX-currX, plotRectangle.getWidth());
                }
            	
            	lastX = currX;
            	lastY = currY;
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
            	System.out.println("Released");
            	dragging = false;
            }
        });*/
        
        //setUpMouseListeners(chartPanel);
        plot.addChangeListener(new PlotChangeListener() {

			@Override
			public void plotChanged(PlotChangeEvent arg0) {
				Rectangle2D plotRectangle = chartPanel.getChartRenderingInfo().getPlotInfo().getDataArea();
				updateAxis();
			}
        	
        });
        
        final ChartPanel finalPanel = chartPanel;
        
        
        /* 
         * 
         * BUTTON LISTENERS
         * 
        */
        
        /* Get info button listener*/
        getInfoButton.addActionListener(new ActionListener() {
			
        	@Override
        	public void actionPerformed(ActionEvent evt) {
				Rectangle2D plotArea = finalPanel.getChartRenderingInfo().getPlotInfo().getDataArea();
				System.out.println("Plot area width: " + plotArea.getWidth());
			}
		});
        
        final NumberAxis finalRange = range;
        
        /* Zoom in button listener */
        zoomInButton.addActionListener(new ActionListener() {
        	
        	@Override
        	public void actionPerformed(ActionEvent evt) {
        	/*	System.out.println
        		ValueAxis domainAxis = finalPlot.getDomainAxis();
        		
        		Double domainLength = domainAxis.getRange().getLength();
        		Double changeLength = domainLength/4;
        		
        		finalRange.setRange(domainAxis.getLowerBound()+changeLength, domainAxis.getUpperBound()-changeLength);*/
        		System.out.println("Zoom in clicked");
        		zoomIn();
        	}
        });
        
        zoomOutButton.addActionListener(new ActionListener() {
        	
        	@Override
        	public void actionPerformed(ActionEvent evt) {
        		zoomOut();
        	}
        });
        
        /* Listen for changes in the domain axis */
       /* plot.getDomainAxis().addChangeListener(new AxisChangeListener() {
        	
        	@Override
        	public void axisChanged(AxisChangeEvent event) {
        		
        	}
        });*/
        
        
        f.add(chartPanel);
        JPanel p = new JPanel();
        p.add(getInfoButton);
        p.add(zoomInButton);
        p.add(zoomOutButton);
        f.add(p, BorderLayout.SOUTH);
       /* final JSlider slider = new JSlider(0, 10);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                plot.setDataset(list.get(slider.getValue()));
            }
        });*/
        f.pack();
        Double domainLength = plot.getDomainAxis().getRange().getLength();
        System.out.println("Domain length: " + domainLength);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        Rectangle2D plotArea = chartPanel.getChartRenderingInfo().getPlotInfo().getDataArea();
	}
	
	/*void setUpMouseListeners(ChartPanel panel) {
		
		panel.addChartMouseListener(new ChartMouseListener() {

			@Override
			public void chartMouseClicked(ChartMouseEvent event) {
				ChartEntity entity = event.getEntity();
				if (entity != null && (entity instanceof PlotEntity)) {
					System.out.println("On the plot");
				}
				
			}

			@Override
			public void chartMouseMoved(ChartMouseEvent arg0) {
				
			}
			
		});
	}*/
	
	public XYPlot getPlot() {return plot;}
	
	private  double computePixelWidth(double width) {
		Rectangle2D plotRectangle = chartPanel.getChartRenderingInfo().getPlotInfo().getDataArea();
		
		double plotWidth = plotRectangle.getWidth();
		double domainRange = domain.getRange().getLength();
		return (width*domainRange/plotWidth);
	}
	
	private  double computePixelHeight(double height) {
		Rectangle2D plotRectangle = chartPanel.getChartRenderingInfo().getPlotInfo().getDataArea();
		
		double plotHeight = plotRectangle.getHeight();
		double rangeRange = range.getRange().getLength();
		return (height*rangeRange/plotHeight);
	}
	
	public void changeXAxis (int delta, double width) {
		System.out.println("Width: " + width);
		Double length = domain.getRange().getLength();
		Double ratio = delta/width;
		Double lengthDelta = ratio*length;
		
		domain.setRange(domain.getLowerBound()+lengthDelta, domain.getUpperBound()+lengthDelta);
	}
	
	public void changeYAxis(int delta, double height) {
		Double length = range.getRange().getLength();
		Double ratio = delta/height;
		Double lengthDelta = ratio*length;
		
		range.setRange(range.getLowerBound()-lengthDelta, range.getUpperBound()-lengthDelta);
	}
	
	/*private class TrendZoomListener implements ChartChangeListener {
		
	}*/
	
	//public void 
	
	public void saveChart(String fileName) {
		try {
			System.out.println("In save chart");
			ChartUtilities.saveChartAsPNG(new File(fileName), chart, 1500, 1500);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	private class GraphMouseListeners extends MouseAdapter {
		
		int startX;
    	int startY;
    	
    	int lastX;
    	int lastY;
    	
    	Boolean dragging = false;
    	
    	@Override
    	public void mousePressed(MouseEvent e) {
    		System.out.println("Clicked");
    		startX = e.getX();
    		startY = e.getY();
    	}
    	
        @Override
        public void mouseDragged(MouseEvent e) {
        	System.out.println("Mouse dragged");
        	int currX = e.getX();
        	int currY = e.getY();
        	
        	System.out.println("Start X: " + startX + "CurrX: " + e.getX());
        	
        	System.out.println("startX - currX = " + (startX-e.getX()));
        	
        	//if (!dragging && Math.abs(startX-e.getX()) > 10)
        	if (!dragging && (Math.sqrt(Math.pow(currX-startX, 2) + Math.pow(currY-startY, 2)) > 10))
        	{
        		dragging = true;
        		lastX = startX;
        		lastY = startY;
        	}
        	if (dragging) {
            	super.mouseDragged(e);
            	int mouseX = e.getX();
            	int mouseY = e.getY();
            	System.out.println("New X: " + mouseX + " New Y: " + mouseY);
                Rectangle2D plotRectangle = chartPanel.getChartRenderingInfo().getPlotInfo().getDataArea();
                if (mouseX >= plotRectangle.getX() && mouseX <= (plotRectangle.getX() + plotRectangle.getWidth())) {
                	System.out.println("Dragging on plot area");
                }
                
                changeXAxis(lastX-currX, plotRectangle.getWidth());
                changeYAxis(lastY-currY, plotRectangle.getHeight());
            }
        	
        	lastX = currX;
        	lastY = currY;
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
        	System.out.println("Released");
        	dragging = false;
        }
	}
}