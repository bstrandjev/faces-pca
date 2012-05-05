package com.borisp.faces.ui;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.hibernate.SessionFactory;

import com.borisp.faces.beans.Transformation;
import com.borisp.faces.beans.User;
import com.borisp.faces.classifiers.ClassifierExperimenter;
import com.borisp.faces.classifiers.ClassifierExperimenter.Classifiers;

/**
 * Defines a UI for displaying the results of classification experiments.
 *
 * @author Boris
 */
public class ClassifierExperimentRunnerPanel extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;

    private static final String PANEL_TITLE = "Classification experiments";
    private static final String RUN_EXPERIMENTS_BUTTON_LABEL = "Run experiments";
    // Checkbox labels
    private static final String NEAREST_NEIGHBOR_LABEL = "Nearest neighbor";
    private static final String NEURAL_NETWORK_LABEL = "Neural network";
    private static final String PERFECT_CLASSIFIER_LABEL = "Perfect classifier";
    // Text area constants
    private static final int TEXT_AREA_COLUMNS = 20;
    private static final int TEXT_AREA_ROWS = 16;
    // Text field lables
    private static final String NUMBER_OF_FACES_LABEL = "Used faces";
    private static final String NUMBER_OF_EXPERIMENTS_LABEL = "Experiments";
    private static final String NUMBER_OF_FACES_DEFAULT = "  20";
    private static final String NUMBER_OF_EXPERIMENTS_DEFAULT = " 20";

    private JFrame parentFrame;
    private JTextArea textArea;
    private JCheckBox nearestNeighbourCheckbox;
    private JCheckBox neuralNetworkCheckbox;
    private JCheckBox perfectClassifierCheckbox;
    private JTextField numberOfFacesField;
    private JTextField numberOfExperimentsField;

    private String username;
    private int transformationId;
    private SessionFactory sessionFactory;

    /**
     * Constructs a panel for the visualization of experiment running.
     *
     * @param transformation The transformation for which to run experiments.
     * @param user The user whose classifications to be used during the experiments.
     * @param parentFrame The frame that will contain he panel. Used for callback functions.
     * @param sessionFactory The session factory to use for the database calls.
     */
    public ClassifierExperimentRunnerPanel(Transformation transformation, User user, JFrame parentFrame,
            SessionFactory sessionFactory) {
        this.username = user.getName();
        this.transformationId = transformation.getTransformationId();
        this.sessionFactory = sessionFactory;

        this.parentFrame = parentFrame;
        this.parentFrame.setTitle(PANEL_TITLE);

        this.nearestNeighbourCheckbox = new JCheckBox(NEAREST_NEIGHBOR_LABEL);
        this.nearestNeighbourCheckbox.setMnemonic(KeyEvent.VK_G);
        this.nearestNeighbourCheckbox.setSelected(true);

        this.neuralNetworkCheckbox = new JCheckBox(NEURAL_NETWORK_LABEL);
        this.neuralNetworkCheckbox.setMnemonic(KeyEvent.VK_W);
        this.neuralNetworkCheckbox.setSelected(true);

        this.perfectClassifierCheckbox = new JCheckBox(PERFECT_CLASSIFIER_LABEL);
        this.perfectClassifierCheckbox.setMnemonic(KeyEvent.VK_P);
        this.perfectClassifierCheckbox.setSelected(true);

        this.numberOfFacesField = new JTextField(NUMBER_OF_FACES_DEFAULT);
        this.numberOfExperimentsField = new JTextField(NUMBER_OF_EXPERIMENTS_DEFAULT);

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
        add(new JLabel(NUMBER_OF_EXPERIMENTS_LABEL));
        add(numberOfExperimentsField);
        add(nearestNeighbourCheckbox);
        add(neuralNetworkCheckbox);
        add(perfectClassifierCheckbox);
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
        //textArea.setText("");
        final List<Classifiers> classifiers = new ArrayList<Classifiers>();
        if (nearestNeighbourCheckbox.isSelected()) {
            classifiers.add(Classifiers.NEAREST_NEIGHBOURS);
        }
        if (neuralNetworkCheckbox.isSelected()) {
            classifiers.add(Classifiers.NEURAL_NETWORK);
        }
        if (perfectClassifierCheckbox.isSelected()) {
            classifiers.add(Classifiers.IDENTITY);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                ClassifierExperimenter experimenter = new ClassifierExperimenter() {
                    @Override
                    protected void appendToOutput(String string) {
                        textArea.append(string);
                        textArea.setCaretPosition(textArea.getDocument().getLength());
                        textArea.update(textArea.getGraphics());
                    }
                };
                Classifiers[] classifierArray = new Classifiers[classifiers.size()];
                for (int i = 0; i < classifierArray.length; i++) {
                    classifierArray[i] = classifiers.get(i);
                }
                int numberOfExperiments = Integer.valueOf(numberOfExperimentsField.getText().trim());
                int numberOfFaces  = Integer.valueOf(numberOfFacesField.getText().trim());
                experimenter.evaluateClassifier(username, transformationId, sessionFactory,
                        classifierArray, numberOfExperiments, numberOfFaces);
            }
        }).start();

    }
}
