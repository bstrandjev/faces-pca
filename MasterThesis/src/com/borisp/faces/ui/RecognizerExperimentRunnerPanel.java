package com.borisp.faces.ui;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.hibernate.SessionFactory;

import com.borisp.faces.beans.Transformation;
import com.borisp.faces.recognizer.RecognizerExperimenter;

/**
 * Defines a UI for displaying the results of recognition experiments.
 *
 * @author Boris
 */
public class RecognizerExperimentRunnerPanel  extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;

    private static final String PANEL_TITLE = "Recognition experiments";
    private static final String RUN_EXPERIMENTS_BUTTON_LABEL = "Run experiments";
    // Text area constants
    private static final int TEXT_AREA_COLUMNS = 20;
    private static final int TEXT_AREA_ROWS = 16;
    // Text field lables
    private static final String NUMBER_OF_FACES_LABEL = "Used faces";
    private static final String NUMBER_OF_FACES_DEFAULT = "  20";
    private static final String NOISE_LABEL = "Noise";
    private static final String NOISE_DEFAULT = " 0.2";

    private JFrame parentFrame;
    private JTextArea textArea;
    private JTextField numberOfFacesField;
    private JTextField noiseField;

    private int transformationId;
    private SessionFactory sessionFactory;

    /**
     * Constructs a panel for the visualization of experiment running.
     *
     * @param transformation The transformation for which to run experiments.
     * @param parentFrame The frame that will contain he panel. Used for callback functions.
     * @param sessionFactory The session factory to use for the database calls.
     */
    public RecognizerExperimentRunnerPanel(Transformation transformation, JFrame parentFrame,
            SessionFactory sessionFactory) {
        this.transformationId = transformation.getTransformationId();
        this.sessionFactory = sessionFactory;

        this.numberOfFacesField = new JTextField(NUMBER_OF_FACES_DEFAULT);
        this.noiseField = new JTextField(NOISE_DEFAULT);
        this.parentFrame = parentFrame;
        this.parentFrame.setTitle(PANEL_TITLE);

        JButton runExperimentsButton = new JButton(RUN_EXPERIMENTS_BUTTON_LABEL);
        runExperimentsButton.addActionListener(this);

        this.textArea = new JTextArea();
        this.textArea.setRows(TEXT_AREA_ROWS);
        this.textArea.setColumns(TEXT_AREA_COLUMNS);
        this.textArea.setLineWrap(true);
        this.textArea.setWrapStyleWord(true);
        this.textArea.setEditable(false);

        add(new JLabel(NUMBER_OF_FACES_LABEL));
        add(numberOfFacesField);
        add(new JLabel(NOISE_LABEL));
        add(noiseField);
        add(runExperimentsButton);

        // Adding the text area
        JScrollPane scrollPane = new JScrollPane(textArea);
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        add(scrollPane, c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RecognizerExperimenter experimenter = new RecognizerExperimenter() {
                    @Override
                    protected void appendToOutput(String string) {
                        textArea.append(string);
                        textArea.setCaretPosition(textArea.getDocument().getLength());
                        textArea.update(textArea.getGraphics());
                    }
                };
                int numberOfFaces  = Integer.valueOf(numberOfFacesField.getText().trim());
                double noise  = Double.valueOf(noiseField.getText().trim());
                experimenter.evaluateRecognizer(transformationId, sessionFactory, noise,
                        numberOfFaces);
            }
        }).start();
    }
}
