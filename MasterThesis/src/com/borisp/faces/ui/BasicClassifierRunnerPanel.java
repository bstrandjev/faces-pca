package com.borisp.faces.ui;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import com.borisp.faces.beans.Classification;
import com.borisp.faces.beans.Transformation;
import com.borisp.faces.classifiers.ClassifierExperimenter;
import com.borisp.faces.classifiers.ClassifierExperimenter.Classifiers;

/**
 * Defines a UI for displaying the results of classification experiments.
 *
 * @author Boris
 */
public abstract class BasicClassifierRunnerPanel extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;

    // Text area constants
    private static final int TEXT_AREA_COLUMNS = 20;
    private static final int TEXT_AREA_ROWS = 16;
    // Text field lables
    private static final String NUMBER_OF_FACES_LABEL = "Used faces";
    private static final String NUMBER_OF_EXPERIMENTS_LABEL = "Experiments";
    private static final String NUMBER_OF_FACES_DEFAULT = "  20";
    private static final String NUMBER_OF_EXPERIMENTS_DEFAULT = " 20";

    private JFrame parentFrame;
    protected JTextArea textArea;
    private JCheckBox []checkboxes;
    private JTextField numberOfFacesField;
    private JTextField numberOfExperimentsField;

    private String classificationKey;
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
    public BasicClassifierRunnerPanel(Transformation transformation, Classification classification,
            JFrame parentFrame, SessionFactory sessionFactory) {
        this.classificationKey = classification.getClassificationKey();
        this.transformationId = transformation.getTransformationId();
        this.sessionFactory = sessionFactory;

        this.parentFrame = parentFrame;
        this.parentFrame.setTitle(getPanelTitle());

        this.checkboxes = new JCheckBox[Classifiers.values().length];
        for (int i = 0; i < checkboxes.length; i++) {
            this.checkboxes[i] = new JCheckBox(Classifiers.values()[i].getLabel());
            this.checkboxes[i].setSelected(true);
        }

        this.numberOfFacesField = new JTextField(NUMBER_OF_FACES_DEFAULT);
        this.numberOfExperimentsField = new JTextField(NUMBER_OF_EXPERIMENTS_DEFAULT);

        JButton runExperimentsButton = new JButton(getButtonLabel());
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
        for (JCheckBox checkbox : checkboxes) {
            add(checkbox);
        }
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
        for (int i = 0; i < checkboxes.length; i++) {
            if (checkboxes[i].isSelected()) {
                classifiers.add(Classifiers.values()[i]);
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                ClassifierExperimenter experimenter = getClassifierExperimenter();
                Classifiers[] classifierArray = new Classifiers[classifiers.size()];
                for (int i = 0; i < classifierArray.length; i++) {
                    classifierArray[i] = classifiers.get(i);
                }
                int numberOfExperiments = Integer.valueOf(numberOfExperimentsField.getText().trim());
                int numberOfFaces  = Integer.valueOf(numberOfFacesField.getText().trim());
                experimenter.evaluateClassifier(classificationKey, transformationId,
                        sessionFactory, classifierArray, numberOfExperiments, numberOfFaces);
            }
        }).start();
    }

    protected abstract ClassifierExperimenter getClassifierExperimenter();
    protected abstract String getPanelTitle();
    protected abstract String getButtonLabel();
}
