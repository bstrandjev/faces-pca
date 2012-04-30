package com.borisp.faces.ui;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.borisp.faces.beans.EigenFaceEntity;
import com.borisp.faces.beans.ManipulatedImage;
import com.borisp.faces.beans.Transformation;
import com.borisp.faces.initial_manipulation.ImageScaler;
import com.borisp.faces.pca.PcaTransformer;
import com.borisp.faces.util.ImageConstructor;

/**
 * A panel that cna be used to visualize an image projection on eigen face space.
 *
 * @author Boris
 */
public class ProjectionVisualizerPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final String RECALCULATE_LABEL = "Recalculate";
    private static final String PANEL_TITLE = "Image restore";
    private static final String NUMBER_OF_FACES_DEFAULT = "  10";
    private static final String NUMBER_OF_FACES_LABEL = "Num faces";
    private static final String MANIPULATED_IMAGE_IDX_DEFAULT = "    1";
    private static final String MANIPULATED_IMAGE_IDX_LABEL = "Image Idx";
    private static final int IMAGE_BEG_X = 70;
    private static final int IMAGE_BEG_Y = 70;

    private JButton calculateButton;
    private JTextField numberOfFacesField;
    private JTextField manipulatedImageIndexField;
    private JFrame parentFrame;

    private List<EigenFaceEntity> eigenFaces;
    private List<ManipulatedImage> manipulatedImages;
    private int currentIdx;
    private int facesConsidered;

    public ProjectionVisualizerPanel(Transformation transformation, JFrame parentFrame) {
        this.eigenFaces = transformation.getEigenFaces();
        this.manipulatedImages = transformation.getManipulation().getManipulatedImages();

        this.parentFrame = parentFrame;
        this.parentFrame.setTitle(PANEL_TITLE);
        this.calculateButton = new JButton(RECALCULATE_LABEL);
        this.numberOfFacesField = new JTextField(NUMBER_OF_FACES_DEFAULT);
        this.manipulatedImageIndexField = new JTextField(MANIPULATED_IMAGE_IDX_DEFAULT);
        this.calculateButton.addActionListener(recalculateButtonListener);
        add(new JLabel(NUMBER_OF_FACES_LABEL));
        add(numberOfFacesField);
        add(new JLabel(MANIPULATED_IMAGE_IDX_LABEL));
        add(manipulatedImageIndexField);
        add(calculateButton);
        fetchNewSettings();
    }

    @Override
    public void paintComponent(Graphics g) {
        double[] projectedImage = PcaTransformer.getProjectedImage(
                manipulatedImages.get(currentIdx), eigenFaces, facesConsidered);
        int imageHeight = ImageScaler.TARGET_HEIGHT;
        int imageWidth = ImageScaler.TARGET_WIDTH;
        BufferedImage image = ImageConstructor.createImage(projectedImage, imageHeight,
                imageWidth);
        g.drawImage(image, IMAGE_BEG_X, IMAGE_BEG_Y, imageWidth, imageHeight, null);
        g.drawLine(IMAGE_BEG_X - 1, IMAGE_BEG_Y - 1, IMAGE_BEG_X + imageWidth + 1,
                IMAGE_BEG_Y - 1);
        g.drawLine(IMAGE_BEG_X - 1, IMAGE_BEG_Y - 1, IMAGE_BEG_X - 1, IMAGE_BEG_Y + imageHeight
                + 1);
        g.drawLine(IMAGE_BEG_X + imageWidth + 1, IMAGE_BEG_Y - 1, IMAGE_BEG_X + imageWidth + 1,
                IMAGE_BEG_Y + imageHeight + 1);
        g.drawLine(IMAGE_BEG_X - 1, IMAGE_BEG_Y + imageHeight + 1,
                IMAGE_BEG_X + imageWidth + 1, IMAGE_BEG_Y + imageHeight + 1);
    }

    private ActionListener recalculateButtonListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (fetchNewSettings()) {
                parentFrame.repaint();
            }
        }
    };

    private boolean fetchNewSettings() {
        int newCurentIdx;
        int newFacesConsidered;
        try {
            newCurentIdx = Integer.valueOf(manipulatedImageIndexField.getText().trim());
            newFacesConsidered  = Integer.valueOf(numberOfFacesField.getText().trim());
            currentIdx = newCurentIdx - 1;
            facesConsidered = newFacesConsidered;
            return true;
        } catch (NumberFormatException error) {
            numberOfFacesField.setText(String.valueOf(facesConsidered));
            manipulatedImageIndexField.setText(String.valueOf(currentIdx));
            error.printStackTrace();
            return false;
        }
    }
}
