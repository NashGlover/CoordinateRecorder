
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
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
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
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

public class GraphPlot {
	
//	private static final int N = 25;
//    private static final double K = 273.15;
    private static final Random random = new Random();
	private static XYSeries series = new XYSeries("Test");
	private static XYPlot plot;
	
	private static NumberAxis range;
	private static NumberAxis domain;
	
	private static XYDataset xyDataset = null;
	private static int i = 0;
	private static Coordinate lastCoordinate;

	private Stack<Double> zoomInStack;
	private Stack<Double> zoomOutStack;
	
	private JFrame f;
	
	public JFrame getJFrame() {
		return this.f;
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
	}
	
	public void updateAxis() {
		range = (NumberAxis) plot.getRangeAxis();
	}
	
    public static void addPoint(Coordinate coordinate) {
    	if (xyDataset == null) {
    		series.add(coordinate.getX(), coordinate.getY());
        	System.out.println("In addPoint. X: " + coordinate.getX() + " Y: " + coordinate.getY());
        	System.out.println("Size of series: " + series.getItemCount());
        	xyDataset = new XYSeriesCollection(series);
        	plot.setDataset(xyDataset);
    	}
    	else {
    		final XYSeries newSeries = new XYSeries(i);
    		newSeries.add(lastCoordinate.getX(), lastCoordinate.getY());
    		newSeries.add(coordinate.getX(), coordinate.getY());
    		plot.getRenderer().setSeriesPaint(i, Color.RED);
    		plot.getRenderer().setSeriesPaint(i+1, Color.RED);	
    		XYSeriesCollection newDataset = (XYSeriesCollection) xyDataset;
    		newDataset.addSeries(newSeries);
    		plot.setDataset(newDataset);
    		i++;
    	}
    	/*series.add(coordinate.getX(), coordinate.getY());
    	System.out.println("In addPoint. X: " + coordinate.getX() + " Y: " + coordinate.getY());
    	System.out.println("Size of series: " + series.getItemCount());
    	xyDataset = new XYSeriesCollection(series);
    	plot.setDataset(xyDataset);*/
    	lastCoordinate = new Coordinate(coordinate.getX(), coordinate.getY(), coordinate.getZ());
    }
    
    public static void addAnchorPoint(Coordinate coordinate) {
    	System.out.println("In add anchor point.");
    	if (xyDataset == null) {
    		series.add(coordinate.getX(), coordinate.getY());
    		series.add(0, 0);
        	System.out.println("In addPoint. X: " + coordinate.getX() + " Y: " + coordinate.getY());
        	System.out.println("Size of series: " + series.getItemCount());
        	xyDataset = new XYSeriesCollection(series);
        	plot.setDataset(xyDataset);
        	i++;
    	} else {
	    	final XYSeries newSeries = new XYSeries(i);
	    	newSeries.add(coordinate.getX(), coordinate.getY());
	    	XYSeriesCollection newDataset = (XYSeriesCollection) xyDataset;
	    	newDataset.addSeries(newSeries);
	    	plot.setDataset(newDataset);
	    	i++;
    	}
    	//XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
    	XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
    	renderer.setSeriesLinesVisible(i-1, false);
    	renderer.setSeriesShapesVisible(i-1, true);
    	plot.setRenderer(renderer);
    }
    
    public static void atAnchorPoint(Coordinate coordinate) {
    	lastCoordinate = coordinate;
    }
    
	private static XYDataset getDataset(int n) {
		System.out.println("Get data set");
        //final XYSeries series = new XYSeries("Temp (K�)");
        double temperature;
       /* for (int length = 0; length < N; length++) {
            temperature = K + n * random.nextGaussian();
            series.add(length + 1, temperature);
        }*/
        return new XYSeriesCollection(series);
    }
	
    private static JFreeChart createChart(final XYDataset dataset) {
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
        JFreeChart chart = createChart(firstXY);
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
        plot.setRangeGridlinePaint(new Color(240, 240, 240));
        plot.setDomainGridlinePaint(new Color(240, 240, 240));
        //plot.setDomainValue(0, 50);
        ChartPanel chartPanel = new ChartPanel(chart) {
            
        	
        	@Override
            public Dimension getPreferredSize() {
                return new Dimension(800, 400);
            }
        };
        
        chartPanel.setPopupMenu(null);
        chartPanel.setMouseZoomable(false);
        plot.addChangeListener(new PlotChangeListener() {

			@Override
			public void plotChanged(PlotChangeEvent arg0) {
				System.out.println("The plot has changed.");
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
	
	/*private class TrendZoomListener implements ChartChangeListener {
		
	}*/
	
}