package com.borisp.faces.ui;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.borisp.faces.beans.EigenFaceEntity;
import com.borisp.faces.beans.Transformation;

/**
 * A panel designed to show the scree plot for a transformation.
 *
 * @author Boris
 */
public class ScreePlotVisualizer {
    private static final String SCREE_PLOT_LABEL = "Scree plot";
    private static final String X_AXIS_LABEL = "PCA components";
    private static final String Y_AXIS_LABEL = "Associated variation";
    private static final String LINE_LABEL = "Eigen values";
    private static final int PCA_COMPONENTS_LIMIT = 102;

    /** Creates a frame containing the scree plot of the transformation. */
    public static void createScreePlot(Transformation transformation) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries xy = new XYSeries(LINE_LABEL);

        List<EigenFaceEntity> eigenFaces = transformation.getEigenFaces();
        // sorting the eigen faces in descending order of the eigen values
        Collections.sort(eigenFaces, Collections.reverseOrder());

        for (int i = 0; i < eigenFaces.size(); i++) {
            xy.add(i, eigenFaces.get(i).getEigenValue());
        }
        dataset.addSeries(xy);

        JFreeChart chart = ChartFactory.createXYLineChart(SCREE_PLOT_LABEL, X_AXIS_LABEL,
                Y_AXIS_LABEL, dataset, PlotOrientation.VERTICAL, true, true, false);
        ChartFrame chartFrame = new ChartFrame(SCREE_PLOT_LABEL, chart);
        chartFrame.pack();
        chartFrame.setBackground(Color.WHITE);
        chartFrame.setVisible(true);
    }
}
