package com.borisp.faces.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

import com.borisp.faces.beans.Classification;
import com.borisp.faces.beans.ClassificationValue;
import com.borisp.faces.beans.ClassifiedImage;
import com.borisp.faces.beans.EigenFaceEntity;
import com.borisp.faces.beans.Image;
import com.borisp.faces.beans.ImageGroup;
import com.borisp.faces.beans.ManipulatedImage;
import com.borisp.faces.beans.Manipulation;
import com.borisp.faces.beans.PcaCoeficient;
import com.borisp.faces.beans.Transformation;
import com.borisp.faces.beans.TransformedImage;
import com.borisp.faces.database.ClassificationDatabaseHelper;
import com.borisp.faces.database.DatabaseHelper;

/**
 * This is the basic frame to use for all the application's visualizations.
 * <p>
 * The basic frame defines only the menus and the size of the screen. From then on different panels
 * are loaded in the frame considering the menu selections.
 *
 * @author Boris
 */
public class BasicFrame extends JFrame{
    private static final long serialVersionUID = 1L;

    // Frame constants
    private static final int FRAME_WIDTH = 260;
    private static final int FRAME_HEIGHT = 500;

    // Face display panel coordinates:
    private static final int BEG_X = 40;
    private static final int BEG_Y = 80;
    private static final int PANEL_WIDTH = 160;
    private static final int PANEL_HEIGHT = 400;

    private static final int PROJECTION_BEG_X = 30;
    private static final int PROJECTION_BEG_Y = 80;
    private static final int PROJECTION_WIDTH = 190;
    private static final int PROJECTION_HEIGHT = 400;

    private static final int EXPERIMENTS_BEG_X = 0;
    private static final int EXPERIMENTS_BEG_Y = 20;
    private static final int EXPERIMENTS_WIDTH = 260;
    private static final int EXPERIMENTS_HEIGHT = 440;

    // Menu constants
    private static final String VISUALIZATION_MENU_LABEL = "Visualize";
    private static final String VISUALIZATION_MENU_DESCRIPTION =
            "All operations connected to visualization";
    private static final String SHOW_MANIPULATION_LABEL = "Show manipulation";
    private static final String SHOW_TRANSFORMATION_LABEL = "Show eigen faces";
    private static final String SHOW_PROJECTIONS_LABEL = "Show projections";
    private static final String SHOW_SCREE_PLOT_LABEL = "Show scree plot";
    private static final String NOISE_DEMONSTRATOR_LABEL = "Show images with noise";

    private static final String EXPERIMENTS_MENU_LABEL = "Experiments";
    private static final String CLASSIFICATION_EXPERIMENTS_LABEL = "Classification experiments";
    private static final String ALGORITM_COMPARISON_LABEL = "Algorithm comparison";
    private static final String RECOGNITION_EXPERIMENTS_LABEL = "Recognition experiments";
    private static final String EXPERIMENTS_MENU_DESCRIPTION =
            "All operations connected to experiment running";

    // Dialog constants
    private static final String SELECT_MANIPULATION_TEXT =
            "Please select the appropriate manipulation";
    private static final String SELECT_MANIPULATION_HEADER = "Select manipulation";
    private static final String SELECT_TRANSFORMATION_TEXT =
            "Please select the appropriate transformation";
    private static final String SELECT_TRANSFORMATION_HEADER = "Select transformation";
    private static final String SELECT_CLASSIFICATION_TEXT =
            "Please select the classification to use";
    private static final String SELECT_CLASSIFICATION_HEADER = "Select classification";

    /** The single instance of the session factory to use for all database calls. */
    private SessionFactory sessionFactory;
    private JPanel currentPanel;

    public BasicFrame() {
        this.sessionFactory = initializeSessionFactory();
        this.currentPanel = null;

        //Create the menu bar.
        JMenuBar menuBar;
        menuBar = new JMenuBar();

        //Build the visualization menu.

        menuBar.add(buildVisualizationMenu());
        menuBar.add(buildExperimentsMenu());
        setJMenuBar(menuBar);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLayout(null);
        displayManipulation();
    }

    /** Constructs the menu for the visualizations. */
    private JMenu buildVisualizationMenu() {
        JMenu visualizationMenu = new JMenu(VISUALIZATION_MENU_LABEL);
        visualizationMenu.setMnemonic(KeyEvent.VK_V);
        visualizationMenu.getAccessibleContext().setAccessibleDescription(
                VISUALIZATION_MENU_DESCRIPTION);

        JMenuItem manipulationDisplayItem = new JMenuItem(SHOW_MANIPULATION_LABEL);
        manipulationDisplayItem.setMnemonic(KeyEvent.VK_M);
        manipulationDisplayItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                displayManipulation();
            }
        });

        JMenuItem transformationDisplayItem = new JMenuItem(SHOW_TRANSFORMATION_LABEL);
        transformationDisplayItem.setMnemonic(KeyEvent.VK_E);
        transformationDisplayItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                Transformation transformation = chooseTransformation();
                if (currentPanel != null) {
                    BasicFrame.this.remove(currentPanel);
                }
                currentPanel = new EigenFaceVisualizerPanel(transformation, BasicFrame.this);
                currentPanel.setBounds(BEG_X, BEG_Y, PANEL_WIDTH, PANEL_HEIGHT);
                BasicFrame.this.getContentPane().add(currentPanel);

                BasicFrame.this.validate();
                BasicFrame.this.repaint();
            }
        });

        JMenuItem projectionDisplayItem = new JMenuItem(SHOW_PROJECTIONS_LABEL);
        projectionDisplayItem.setMnemonic(KeyEvent.VK_P);
        projectionDisplayItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                Transformation transformation = chooseTransformation();
                if (currentPanel != null) {
                    BasicFrame.this.remove(currentPanel);
                }
                currentPanel = new ProjectionVisualizerPanel(transformation, BasicFrame.this);
                currentPanel.setBounds(PROJECTION_BEG_X, PROJECTION_BEG_Y, PROJECTION_WIDTH,
                        PROJECTION_HEIGHT);
                BasicFrame.this.getContentPane().add(currentPanel);

                BasicFrame.this.validate();
                BasicFrame.this.repaint();
            }
        });

        JMenuItem noiseDisplayItem = new JMenuItem(NOISE_DEMONSTRATOR_LABEL);
        noiseDisplayItem.setMnemonic(KeyEvent.VK_N);
        noiseDisplayItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                Manipulation manipulation = chooseManipulation();
                if (currentPanel != null) {
                    BasicFrame.this.remove(currentPanel);
                }
                currentPanel = new NoiseImageVisualizer(manipulation, BasicFrame.this);
                currentPanel.setBounds(PROJECTION_BEG_X, PROJECTION_BEG_Y, PROJECTION_WIDTH,
                        PROJECTION_HEIGHT);
                BasicFrame.this.getContentPane().add(currentPanel);

                BasicFrame.this.validate();
                BasicFrame.this.repaint();
            }
        });

        JMenuItem screePlotItem = new JMenuItem(SHOW_SCREE_PLOT_LABEL);
        screePlotItem.setMnemonic(KeyEvent.VK_S);
        screePlotItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                Transformation transformation = chooseTransformation();
                ScreePlotVisualizer.createScreePlot(transformation);
            }
        });

        visualizationMenu.add(manipulationDisplayItem);
        visualizationMenu.add(transformationDisplayItem);
        visualizationMenu.add(projectionDisplayItem);
        visualizationMenu.add(noiseDisplayItem);
        visualizationMenu.add(screePlotItem);
        return visualizationMenu;
    }

    /** Constructs the menu for the experiments. */
    private JMenu buildExperimentsMenu() {
        JMenu experimentsMenu = new JMenu(EXPERIMENTS_MENU_LABEL);
        experimentsMenu.setMnemonic(KeyEvent.VK_E);
        experimentsMenu.getAccessibleContext().setAccessibleDescription(
                EXPERIMENTS_MENU_DESCRIPTION);

        JMenuItem classificationExperimentsItem = new JMenuItem(CLASSIFICATION_EXPERIMENTS_LABEL);
        classificationExperimentsItem.setMnemonic(KeyEvent.VK_C);
        classificationExperimentsItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                Classification classification = chooseClassification();
                Transformation transformation = DatabaseHelper.findAppropriateTransformation(
                        sessionFactory, classification);
                if (currentPanel != null) {
                    BasicFrame.this.remove(currentPanel);
                }
                currentPanel = new ClassifierExperimenterRunnerPanel(transformation, classification,
                        BasicFrame.this, sessionFactory);
                currentPanel.setBounds(EXPERIMENTS_BEG_X, EXPERIMENTS_BEG_Y, EXPERIMENTS_WIDTH,
                        EXPERIMENTS_HEIGHT);
                BasicFrame.this.getContentPane().add(currentPanel, BorderLayout.CENTER);

                BasicFrame.this.validate();
                BasicFrame.this.repaint();
            }
        });

        JMenuItem algoritmComparisonItem = new JMenuItem(ALGORITM_COMPARISON_LABEL);
        algoritmComparisonItem.setMnemonic(KeyEvent.VK_A);
        algoritmComparisonItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                Classification classification = chooseClassification();
                Transformation transformation = DatabaseHelper.findAppropriateTransformation(
                        sessionFactory, classification);
                if (currentPanel != null) {
                    BasicFrame.this.remove(currentPanel);
                }
                currentPanel = new AlgorithmComparatorPanel(transformation, classification,
                        BasicFrame.this, sessionFactory);
                currentPanel.setBounds(EXPERIMENTS_BEG_X, EXPERIMENTS_BEG_Y, EXPERIMENTS_WIDTH,
                        EXPERIMENTS_HEIGHT);
                BasicFrame.this.getContentPane().add(currentPanel, BorderLayout.CENTER);

                BasicFrame.this.validate();
                BasicFrame.this.repaint();
            }
        });

        JMenuItem recognitionExperimentsItem = new JMenuItem(RECOGNITION_EXPERIMENTS_LABEL);
        recognitionExperimentsItem.setMnemonic(KeyEvent.VK_G);
        recognitionExperimentsItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                Transformation transformation = chooseTransformation();
                if (currentPanel != null) {
                    BasicFrame.this.remove(currentPanel);
                }
                currentPanel = new RecognizerExperimentRunnerPanel(transformation, BasicFrame.this,
                        sessionFactory);
                currentPanel.setBounds(EXPERIMENTS_BEG_X, EXPERIMENTS_BEG_Y, EXPERIMENTS_WIDTH,
                        EXPERIMENTS_HEIGHT);
                BasicFrame.this.getContentPane().add(currentPanel, BorderLayout.CENTER);

                BasicFrame.this.validate();
                BasicFrame.this.repaint();
            }
        });

        experimentsMenu.add(recognitionExperimentsItem);
        experimentsMenu.add(classificationExperimentsItem);
        experimentsMenu.add(algoritmComparisonItem);
        return experimentsMenu;
    }

    private void displayManipulation() {
        Manipulation manipulation = chooseManipulation();
        if (currentPanel != null) {
            BasicFrame.this.remove(currentPanel);
        }
        currentPanel = new ManipulationVisualizerPanel(manipulation, BasicFrame.this);
        currentPanel.setBounds(BEG_X, BEG_Y, PANEL_WIDTH, PANEL_HEIGHT);
        BasicFrame.this.getContentPane().add(currentPanel);

        BasicFrame.this.validate();
        BasicFrame.this.repaint();
    }

    @SuppressWarnings("deprecation")
    public static SessionFactory initializeSessionFactory() {
        AnnotationConfiguration configuration = new AnnotationConfiguration();
        configuration.addAnnotatedClass(ClassifiedImage.class);
        configuration.addAnnotatedClass(ClassificationValue.class);
        configuration.addAnnotatedClass(Classification.class);
        configuration.addAnnotatedClass(EigenFaceEntity.class);
        configuration.addAnnotatedClass(Image.class);
        configuration.addAnnotatedClass(ImageGroup.class);
        configuration.addAnnotatedClass(ManipulatedImage.class);
        configuration.addAnnotatedClass(TransformedImage.class);
        configuration.addAnnotatedClass(Manipulation.class);
        configuration.addAnnotatedClass(PcaCoeficient.class);
        configuration.addAnnotatedClass(Transformation.class);

        configuration.configure("hibernate.cfg.xml");

        return configuration.buildSessionFactory();
    }

    /** Creates a dialog for selecting manipulation. */
    private Manipulation chooseManipulation() {
        return (Manipulation) objectChooserHelper(
                DatabaseHelper.getAllManipulations(sessionFactory), SELECT_MANIPULATION_TEXT,
                SELECT_MANIPULATION_HEADER);
    }

    /** Creates a dialog for selecting manipulation. */
    private Transformation chooseTransformation() {
        return (Transformation) objectChooserHelper(
                DatabaseHelper.getAllTransformations(sessionFactory), SELECT_TRANSFORMATION_TEXT,
                SELECT_TRANSFORMATION_HEADER);
    }

    /** Creates a dialog for selecting classification. */
    private Classification chooseClassification() {
        return (Classification) objectChooserHelper(
                ClassificationDatabaseHelper.getAllClassifications(sessionFactory),
                SELECT_CLASSIFICATION_TEXT, SELECT_CLASSIFICATION_HEADER);
    }

    private Object objectChooserHelper(List<? extends Object> objects, String text, String header) {
        if (objects.size() > 1) {
            Object [] objectArray = objects.toArray();

            return JOptionPane.showInputDialog(this, text, header, JOptionPane.PLAIN_MESSAGE, null,
                    objectArray, objectArray[0]);
        } else {
            return objects.get(0);
        }
    }
}
