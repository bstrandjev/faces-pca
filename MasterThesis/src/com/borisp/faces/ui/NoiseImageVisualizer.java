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

import com.borisp.faces.beans.ManipulatedImage;
import com.borisp.faces.beans.Manipulation;
import com.borisp.faces.initial_manipulation.ImageScaler;
import com.borisp.faces.recognizer.NoiseGenerator;
import com.borisp.faces.util.ImageConstructor;

/**
 * A panel for visualizing the images with noise introduced.
 *
 * @author Boris
 */
public class NoiseImageVisualizer extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;

    private static final String TITLE_FORMAT = "Image number %03d";
    private static final String NEXT_LABEL = "next";
    private static final String PREV_LABEL = "previous";
    private static final int IMAGE_BEG_X = 40;
    private static final int IMAGE_BEG_Y = 78;

    private static final String NOISE_LABEL = "Noise";
    private static final String NOISE_DEFAULT = " 0.2";
    private static final String RECALCULATE_BUTTON_LABEL = "Recalculate";

    private JButton nextButton;
    private JButton previousButton;
    private JTextField noiseField;
    private JFrame parentFrame;

    private int currentIdx;
    private List<ManipulatedImage> manipulatedImages;

    public NoiseImageVisualizer(Manipulation manipulation, JFrame parentFrame) {
        this.manipulatedImages = manipulation.getManipulatedImages();
        this.parentFrame = parentFrame;
        this.nextButton = new JButton(NEXT_LABEL);
        this.nextButton.addActionListener(this);
        this.previousButton = new JButton(PREV_LABEL);
        this.previousButton.addActionListener(this);
        this.noiseField = new JTextField(NOISE_DEFAULT);

        JButton recalculateButton = new JButton(RECALCULATE_BUTTON_LABEL);
        recalculateButton.addActionListener(this);

        add(new JLabel(NOISE_LABEL));
        add(noiseField);
        add(recalculateButton);
        add(nextButton);
        add(previousButton);

        this.currentIdx = 0;
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

        double noise  = Double.valueOf(noiseField.getText().trim());
        int imageHeight = ImageScaler.TARGET_HEIGHT;
        int imageWidth = ImageScaler.TARGET_WIDTH;
        int[][] imageGrayscale = NoiseGenerator.getImageWithNoise(
                manipulatedImages.get(currentIdx), noise);

        BufferedImage image = ImageConstructor.createImage(imageGrayscale);

        g.drawImage(image, IMAGE_BEG_X, IMAGE_BEG_Y, imageWidth, imageHeight, null);
        g.drawLine(IMAGE_BEG_X - 1, IMAGE_BEG_Y - 1, IMAGE_BEG_X + imageWidth, IMAGE_BEG_Y - 1);
        g.drawLine(IMAGE_BEG_X - 1, IMAGE_BEG_Y - 1, IMAGE_BEG_X - 1, IMAGE_BEG_Y + imageHeight);
        g.drawLine(IMAGE_BEG_X + imageWidth, IMAGE_BEG_Y - 1, IMAGE_BEG_X + imageWidth, IMAGE_BEG_Y
                + imageHeight);
        g.drawLine(IMAGE_BEG_X - 1, IMAGE_BEG_Y + imageHeight, IMAGE_BEG_X + imageWidth,
                IMAGE_BEG_Y + imageHeight);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(NEXT_LABEL)) {
            currentIdx++;
        } else if (e.getActionCommand().equals(PREV_LABEL)){
            currentIdx--;
        }
        parentFrame.repaint();
    }
}
