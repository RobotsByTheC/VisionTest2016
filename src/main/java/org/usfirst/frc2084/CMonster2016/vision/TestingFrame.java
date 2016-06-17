/* 
 * Copyright (c) 2016 RobotsByTheC. All rights reserved.
 *
 * Open Source Software - may be modified and shared by FRC teams. The code must
 * be accompanied by the BSD license file in the root directory of the project.
 */
package org.usfirst.frc2084.CMonster2016.vision;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.usfirst.frc.team2084.CMonster2016.vision.VisionParameters;
import org.usfirst.frc.team2084.CMonster2016.vision.VisionProcessor;
import org.usfirst.frc.team2084.CMonster2016.vision.VisionProcessor.DebugHandler;

import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;
import net.miginfocom.swing.MigLayout;

/**
 * Window for testing the vision algorithm. Allows for the selection of images
 * in the file system and then processes them, displaying the results and
 * intermediate steps.
 * 
 * @author Ben Wolsieffer
 */
@SuppressWarnings("serial")
public class TestingFrame extends JFrame {

    private final JSplitPane splitPane = new JSplitPane();

    private final JTabbedPane imageTabs = new JTabbedPane();

    protected final JFileChooser directoryChooser = new JFileChooser(new File(System.getProperty("user.dir")));

    private final HashMap<String, ImageTab> tabs = new HashMap<>();

    protected final VisionProcessor processor;

    private Mat fileImage;
    private Mat image = new Mat();

    public TestingFrame(VisionProcessor processor) {
        this.processor = processor;
        setLocationByPlatform(true);
        directoryChooser.setControlButtonsAreShown(false);
        directoryChooser.setFileFilter(new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes()));
        directoryChooser.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
                    File file = directoryChooser.getSelectedFile();
                    if (file != null) {
                        fileImage = Imgcodecs.imread(directoryChooser.getSelectedFile().getAbsolutePath());
                        fileImage.copyTo(image);
                        synchronized (processor) {
                            processor.process(image);
                        }
                        updateTab("Output", image);
                    }
                }
            }
        });

        VisionParameters.VISION_PARAMETERS.addTableListener(new ITableListener() {

            @Override
            public void valueChanged(ITable source, String key, Object value, boolean isNew) {
                SwingUtilities.invokeLater(() -> {
                    if (fileImage != null) {
                        fileImage.copyTo(image);
                        synchronized (processor) {
                            processor.process(image);
                        }
                        tabs.get("Output").setImage(image);
                    }
                });
            }
        }, false);

        processor.addDebugHandler(new DebugHandler() {

            @Override
            public void debugImage(String name, Mat image) {
                updateTab(name, image);
            }
        });

        setLayout(new MigLayout("wrap 1, fill"));

        splitPane.setDividerLocation(800);
        splitPane.setLeftComponent(imageTabs);
        splitPane.setRightComponent(directoryChooser);
        add(splitPane, "push, grow");

        setPreferredSize(new Dimension(1400, 600));
    }

    public void updateTab(String name, Mat image) {
        SwingUtilities.invokeLater(() -> {
            ImageTab tab;
            if ((tab = tabs.get(name)) != null) {
                tab.setImage(image);
            } else {
                tab = new ImageTab(image);
                imageTabs.add(name, tab);
                tabs.put(name, tab);
            }
        });
    }

    /**
     * @return the processor
     */
    public VisionProcessor getProcessor() {
        return processor;
    }
}
