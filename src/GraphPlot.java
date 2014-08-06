
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class GraphPlot {
	
//	private static final int N = 25;
//    private static final double K = 273.15;
    private static final Random random = new Random();
	private static XYSeries series = new XYSeries("Test");
	private static XYPlot plot;
	
	private static XYDataset xyDataset = null;
	private static int i = 0;
	private static Coordinate lastCoordinate;
	
    public static void addPoint(Coordinate coordinate) {
    	if (xyDataset == null) {
    		series.add(coordinate.getX(), coordinate.getY());
        	System.out.println("In addPoint. X: " + coordinate.getX() + " Y: " + coordinate.getY());
        	System.out.println("Size of series: " + series.getItemCount());
        	xyDataset = new XYSeriesCollection(series);
        	plot.setDataset(xyDataset);
        	lastCoordinate = new Coordinate(coordinate.getX(), coordinate.getY(), coordinate.getZ());
    	}
    	else {
    		final XYSeries newSeries = new XYSeries(i);
    		newSeries.add(lastCoordinate.getX(), lastCoordinate.getY());
    		newSeries.add(coordinate.getX(), lastCoordinate.getY());
    		plot.getRenderer().setSeriesPaint(0, Color.RED);
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
    }
    
	private static XYDataset getDataset(int n) {
		System.out.println("Get data set");
        //final XYSeries series = new XYSeries("Temp (K°)");
        double temperature;
       /* for (int length = 0; length < N; length++) {
            temperature = K + n * random.nextGaussian();
            series.add(length + 1, temperature);
        }*/
        return new XYSeriesCollection(series);
    }
	
    private static JFreeChart createChart(final XYDataset dataset) {
        JFreeChart chart = ChartFactory.createXYLineChart(
            "Map", "Length (m)", "Temp (K°)", dataset,
            PlotOrientation.VERTICAL, false, false, false);
        return chart;
    }
	
	public GraphPlot() {
		initComponents();
	}
	
	private void initComponents() {
		JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  /*      final List<XYDataset> list = new ArrayList<XYDataset>();
        for (int i = 0; i <= 10; i++) {
            list.add(getDataset(i));
        }*/
        series.add(0, 0);
        XYDataset firstXY = new XYSeriesCollection(series);
        JFreeChart chart = createChart(firstXY);
        plot = (XYPlot) chart.getPlot();
        //Image im = new ImageIcon("C:\\Users\\Public\\Pictures\\Sample Pictures\\Desert.jpg").getImage(); 
        plot.setBackgroundPaint(null);
       // plot.setBackgroundImage(im);
       // plot.getRangeAxis().setRangeAboutValue(K, K / 5);
        NumberAxis range = (NumberAxis) plot.getRangeAxis();
        range.setTickUnit(new NumberTickUnit(1));
        range.setRange(-5, 5);
        NumberAxis domain = (NumberAxis) plot.getDomainAxis();
        domain.setRange(-2, 10);
        domain.setTickUnit(new NumberTickUnit(1));
        
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.black);
        plot.setDomainGridlinePaint(Color.black);
        //plot.setDomainValue(0, 50);
        ChartPanel chartPanel = new ChartPanel(chart) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(800, 400);
            }
        };
        f.add(chartPanel);
       /* final JSlider slider = new JSlider(0, 10);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                plot.setDataset(list.get(slider.getValue()));
            }
        });*/
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
	}
}