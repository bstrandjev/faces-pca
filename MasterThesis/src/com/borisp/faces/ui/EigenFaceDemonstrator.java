package com.borisp.faces.ui;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.borisp.faces.beans.EigenFaceEntity;
import com.borisp.faces.beans.Transformation;
import com.borisp.faces.initial_manipulation.ImageScaler;
import com.borisp.faces.util.ImageConstructor;

/**
 * This class visualizes the eigen faces in a single transformation.
 *
 * @author Boris
 */
public class EigenFaceDemonstrator extends JFrame {
    private static final long serialVersionUID = 1L;

    private EigenFaceEntity[] eigenFaces;

    private class EigenFaceDemonstratorPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        private static final String NEXT_LABEL = "next";
        private static final String PREV_LABEL = "previous";
        private static final int IMAGE_BEG_X = 30;
        private static final int IMAGE_BEG_Y = 48;

        private JButton nextButton;
        private JButton previousButton;
        private int currentIdx;

        public EigenFaceDemonstratorPanel() {
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
            EigenFaceDemonstrator.this.setTitle("Eigen face number " + (currentIdx + 1));

            previousButton.setEnabled(true);
            nextButton.setEnabled(true);
            if (currentIdx == 0) {
                previousButton.setEnabled(false);
            }
            if (currentIdx == eigenFaces.length - 1) {
                nextButton.setEnabled(false);
            }

            int imageHeight = ImageScaler.TARGET_HEIGHT;
            int imageWidth = ImageScaler.TARGET_WIDTH;
            BufferedImage image =
                    ImageConstructor.createImage(eigenFaces[currentIdx].normalizeForPrinting(),
                            imageHeight, imageWidth);

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

        private ActionListener radioButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals(NEXT_LABEL)) {
                    System.out.println("Next selected");
                    currentIdx++;
                } else {
                    System.out.println("Previous selected");
                    currentIdx--;
                }
                EigenFaceDemonstratorPanel.this.repaint();
            }
        };
    }


    public EigenFaceDemonstrator(Transformation transformation) {
        List<EigenFaceEntity> eigenFacesList = transformation.getEigenFaces();
        this.eigenFaces = new EigenFaceEntity[eigenFacesList.size()];
        transformation.getEigenFaces().toArray(this.eigenFaces);
        // sorting the eigen faces in descending order of the eigen values
        Arrays.sort(eigenFaces, Collections.reverseOrder());
        JPanel panel = new EigenFaceDemonstratorPanel();
        getContentPane().add(panel);
        setSize(180, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
