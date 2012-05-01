package com.borisp.faces.ui;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.borisp.faces.beans.ManipulatedImage;
import com.borisp.faces.beans.Manipulation;
import com.borisp.faces.initial_manipulation.ImageScaler;
import com.borisp.faces.util.ImageConstructor;
import com.borisp.faces.util.ImageReader;

/**
 * A panel that will be used to visualize all the images after manipulation
 *
 * @author Boris
 */
public class ManipulationVisualizerPanel  extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final String TITLE_FORMAT = "Image number %03d";
    private static final String NEXT_LABEL = "next";
    private static final String PREV_LABEL = "previous";
    private static final int IMAGE_BEG_X = 25;
    private static final int IMAGE_BEG_Y = 48;

    private JButton nextButton;
    private JButton previousButton;
    private int currentIdx;
    private List<ManipulatedImage> manipulatedImages;
    private JFrame parentFrame;

    public ManipulationVisualizerPanel(Manipulation manipulation, JFrame parentFrame) {
        this.manipulatedImages = manipulation.getManipulatedImages();
        this.parentFrame = parentFrame;
        this.nextButton = new JButton(NEXT_LABEL);
        this.previousButton = new JButton(PREV_LABEL);
        this.nextButton.addActionListener(radioButtonListener);
        this.previousButton.addActionListener(radioButtonListener);
        this.currentIdx = 0;
        add(nextButton);
        add(previousButton);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        parentFrame.setTitle(String.format(TITLE_FORMAT, (currentIdx + 1)));

        previousButton.setEnabled(true);
        nextButton.setEnabled(true);
        if (currentIdx == 0) {
            previousButton.setEnabled(false);
        }
        if (currentIdx == manipulatedImages.size() - 1) {
            nextButton.setEnabled(false);
        }

        int imageHeight = ImageScaler.TARGET_HEIGHT;
        int imageWidth = ImageScaler.TARGET_WIDTH;
        File imagePath = new File(manipulatedImages.get(currentIdx).getManipulatedImagePath());
        BufferedImage image = ImageConstructor.createImage(ImageReader.getImagePixels(imagePath));

        g.drawImage(image, IMAGE_BEG_X, IMAGE_BEG_Y, imageWidth, imageHeight, null);
        g.drawLine(IMAGE_BEG_X - 1, IMAGE_BEG_Y - 1, IMAGE_BEG_X + imageWidth, IMAGE_BEG_Y - 1);
        g.drawLine(IMAGE_BEG_X - 1, IMAGE_BEG_Y - 1, IMAGE_BEG_X - 1, IMAGE_BEG_Y + imageHeight);
        g.drawLine(IMAGE_BEG_X + imageWidth, IMAGE_BEG_Y - 1, IMAGE_BEG_X + imageWidth, IMAGE_BEG_Y
                + imageHeight);
        g.drawLine(IMAGE_BEG_X - 1, IMAGE_BEG_Y + imageHeight, IMAGE_BEG_X + imageWidth,
                IMAGE_BEG_Y + imageHeight);
    }

    private ActionListener radioButtonListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals(NEXT_LABEL)) {
                currentIdx++;
            } else {
                currentIdx--;
            }
            parentFrame.repaint();
        }
    };
}
