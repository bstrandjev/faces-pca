package com.borisp.faces.ui;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.borisp.faces.database.ManipulationCreator.ManipulationIterator;

public class ManipulationEvaluatorUI extends JFrame {
    private static final long serialVersionUID = 1L;


    private ManipulationIterator manipulationIterator;

    private class ManipulationEvaluatorPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        private static final String GOOD_LABEL = "good";
        private static final String BAD_LABEL = "bad";
        private static final int IMAGE_BEG_X = 30;
        private static final int IMAGE_BEG_Y = 30;

        private JRadioButton isGood;
        private JRadioButton isNotGood;

        public ManipulationEvaluatorPanel() {
            this.isGood = new JRadioButton(GOOD_LABEL);
            this.isNotGood = new JRadioButton(BAD_LABEL);
            this.isGood.addActionListener(radioButtonListener);
            this.isNotGood.addActionListener(radioButtonListener);
            add(isGood);
            add(isNotGood);
        }

        @Override
        public void paintComponent(Graphics g) {
            ManipulationEvaluatorUI.this.setTitle(manipulationIterator.getCurrentImageLabel());
            BufferedImage image = manipulationIterator.getCurrentImage();
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();
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
                if (e.getActionCommand().equals(GOOD_LABEL)) {
                    System.out.println("Good selected");
                    manipulationIterator.recordGoodState(true);
                } else {
                    System.out.println("Bad selected");
                    manipulationIterator.recordGoodState(false);
                }
                isGood.setSelected(false);
                isNotGood.setSelected(false);
                if (manipulationIterator.moveIterator()) {
                    ManipulationEvaluatorUI.this.repaint();
                } else {
                    ManipulationEvaluatorUI.this.setVisible(false);
                    ManipulationEvaluatorUI.this.dispose();
                }
            }
        };
    }

    public ManipulationEvaluatorUI(ManipulationIterator manipulationIterator) {
        this.manipulationIterator = manipulationIterator;
        JPanel panel = new ManipulationEvaluatorPanel();
        getContentPane().add(panel);
        setSize(180, 230);
        setVisible(true);
    }

}
