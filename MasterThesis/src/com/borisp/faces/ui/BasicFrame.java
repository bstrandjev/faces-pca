package com.borisp.faces.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.classic.Session;

import com.borisp.faces.beans.Classification;
import com.borisp.faces.beans.EigenFaceEntity;
import com.borisp.faces.beans.Image;
import com.borisp.faces.beans.ManipulatedImage;
import com.borisp.faces.beans.Manipulation;
import com.borisp.faces.beans.PcaCoeficient;
import com.borisp.faces.beans.Transformation;
import com.borisp.faces.beans.User;

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
    private static final int FRAME_HEIGHT = 300;

    // Menu constants
    private static final String VISUALIZATION_MENU_LABEL = "Visualize";
    private static final String VISUALIZATION_MENU_DESCRIPTION =
            "All operations connected to visualization";
    private static final String SHOW_MANIPULATION_LABEL = "Show manipulation";
    private static final String SHOW_TRANSFORMATION_LABEL = "Show eigen faces";
    private static final String SHOW_PROJECTIONS_LABEL = "Show projections";

    // Dialog constants
    private static final String SELECT_MANIPULATION_TEXT =
            "Please select the appropriate manipulation";
    private static final String SELECT_MANIPULATION_HEADER = "Select manipulation";
    private static final String SELECT_TRANSFORMATION_TEXT =
            "Please select the appropriate transformation";
    private static final String SELECT_TRANSFORMATION_HEADER = "Select transformation";

    // Sql constants
    /** A string for selecting all the manipulations from the database. */
    private static final String SELECT_ALL_MANIPULATIONS_SQL_QUERY = "from Manipulation m";
    /** A string for selecting all the transformations from the database. */
    private static final String SELECT_ALL_TRANSFORMATIONS_SQL_QUERY = "from Transformation t";

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
        setJMenuBar(menuBar);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
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
                Manipulation manipulation = chooseManipulation();
                if (currentPanel != null) {
                    BasicFrame.this.remove(currentPanel);
                }
                currentPanel = new ManipulationVisualizerPanel(manipulation, BasicFrame.this);
                BasicFrame.this.getContentPane().add(currentPanel);

                BasicFrame.this.validate();
                BasicFrame.this.repaint();
            }
        });

        JMenuItem transformationDisplayItem = new JMenuItem(SHOW_TRANSFORMATION_LABEL);
        transformationDisplayItem.setMnemonic(KeyEvent.VK_T);
        transformationDisplayItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                Transformation transformation = chooseTransformation();
                if (currentPanel != null) {
                    BasicFrame.this.remove(currentPanel);
                }
                currentPanel = new EigenFaceVisualizerPanel(transformation, BasicFrame.this);
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
                BasicFrame.this.getContentPane().add(currentPanel);

                BasicFrame.this.validate();
                BasicFrame.this.repaint();
            }
        });

        visualizationMenu.add(manipulationDisplayItem);
        visualizationMenu.add(transformationDisplayItem);
        visualizationMenu.add(projectionDisplayItem);
        return visualizationMenu;
    }

    @SuppressWarnings("deprecation")
    private SessionFactory initializeSessionFactory() {
        AnnotationConfiguration configuration = new AnnotationConfiguration();
        configuration.addAnnotatedClass(Classification.class);
        configuration.addAnnotatedClass(EigenFaceEntity.class);
        configuration.addAnnotatedClass(Image.class);
        configuration.addAnnotatedClass(ManipulatedImage.class);
        configuration.addAnnotatedClass(Manipulation.class);
        configuration.addAnnotatedClass(PcaCoeficient.class);
        configuration.addAnnotatedClass(Transformation.class);
        configuration.addAnnotatedClass(User.class);

        configuration.configure("hibernate.cfg.xml");

        return configuration.buildSessionFactory();
    }

    /** Creates a dialog for selecting manipulation. */
    private Manipulation chooseManipulation() {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery(SELECT_ALL_MANIPULATIONS_SQL_QUERY);
        List<Manipulation> manipulations = new ArrayList<Manipulation>();
        for (Iterator<?> it = query.iterate(); it.hasNext();) {
            manipulations.add((Manipulation) it.next());
        }

        return (Manipulation) objectChooserHelper(manipulations, SELECT_MANIPULATION_TEXT,
                SELECT_MANIPULATION_HEADER);
    }

    /** Creates a dialog for selecting manipulation. */
    private Transformation chooseTransformation() {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery(SELECT_ALL_TRANSFORMATIONS_SQL_QUERY);
        List<Transformation> manipulations = new ArrayList<Transformation>();
        for (Iterator<?> it = query.iterate(); it.hasNext();) {
            manipulations.add((Transformation) it.next());
        }

        return (Transformation) objectChooserHelper(manipulations, SELECT_TRANSFORMATION_TEXT,
                SELECT_TRANSFORMATION_HEADER);
    }

    private Object objectChooserHelper(List<? extends Object> objects, String text, String header) {
        Object [] objectArray = objects.toArray();

        return JOptionPane.showInputDialog(this, text, header, JOptionPane.PLAIN_MESSAGE, null,
                objectArray, objectArray[0]);
    }

}
