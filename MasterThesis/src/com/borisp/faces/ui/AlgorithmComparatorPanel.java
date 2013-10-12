package com.borisp.faces.ui;

import javax.swing.JFrame;

import org.hibernate.SessionFactory;

import com.borisp.faces.beans.Classification;
import com.borisp.faces.beans.Transformation;
import com.borisp.faces.classifiers.AlgorithmComparator;
import com.borisp.faces.classifiers.ClassifierExperimenter;

/** A panel for comparing algorithm precision prior and after the PCA transformation. */
public class AlgorithmComparatorPanel extends BasicClassifierRunnerPanel {
    private static final long serialVersionUID = -6897755312359561486L;

    private static final String PANEL_TITLE = "Algorithm comparator";
    private static final String BUTTON_LABEL = "Compare algorithms";

    public AlgorithmComparatorPanel(Transformation transformation, Classification classification,
            JFrame parentFrame, SessionFactory sessionFactory) {
        super(transformation, classification, parentFrame, sessionFactory);
    }

    @Override
    protected ClassifierExperimenter getClassifierExperimenter() {
        return new AlgorithmComparator() {
            @Override
            protected void appendToOutput(String string) {
                textArea.append(string);
                textArea.setCaretPosition(textArea.getDocument().getLength());
                textArea.update(textArea.getGraphics());
            }
        };
    }

    @Override
    protected String getPanelTitle() {
        return PANEL_TITLE;
    }

    @Override
    protected String getButtonLabel() {
        return BUTTON_LABEL;
    }
}
