package com.borisp.faces.ui;

import javax.swing.JFrame;

import org.hibernate.SessionFactory;

import com.borisp.faces.beans.Transformation;
import com.borisp.faces.beans.User;
import com.borisp.faces.classifiers.ClassifierExperimenter;

/** A panel for classifier experiments. */
public class ClassifierExperimenterRunnerPanel extends BasicClassifierRunnerPanel {
    private static final long serialVersionUID = -7817829025285376335L;

    private static final String PANEL_TITLE = "Classification experiments";
    private static final String RUN_EXPERIMENTS_BUTTON_LABEL = "Run experiments";

    public ClassifierExperimenterRunnerPanel(Transformation transformation, User user,
            JFrame parentFrame, SessionFactory sessionFactory) {
        super(transformation, user, parentFrame, sessionFactory);
    }

    @Override
    protected ClassifierExperimenter getClassifierExperimenter() {
        return new ClassifierExperimenter() {
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
        return RUN_EXPERIMENTS_BUTTON_LABEL;
    }

}
