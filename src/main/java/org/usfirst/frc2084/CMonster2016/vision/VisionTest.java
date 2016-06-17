/* 
 * Copyright (c) 2014 RobotsByTheC. All rights reserved.
 *
 * Open Source Software - may be modified and shared by FRC teams. The code must
 * be accompanied by the BSD license file in the root directory of the project.
 */
package org.usfirst.frc2084.CMonster2016.vision;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.usfirst.frc.team2084.CMonster2016.vision.HighGoalProcessor;
import org.usfirst.frc.team2084.CMonster2016.vision.OpenCVLoader;

/**
 * Main class for the application.
 * 
 * @author Ben Wolsieffer
 */
public class VisionTest {

    private TestingFrame testingFrame;
    private CalibrationFrame calibrationFrame;

    /**
     * Runs the vision processing algorithm and displays the results in a
     * window.
     */
    public VisionTest() {
        initGUI();
    }

    public void initGUI() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                testingFrame = new TestingFrame(new HighGoalProcessor(null));

                testingFrame.setVisible(true);
                testingFrame.pack();
                testingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                calibrationFrame = new CalibrationFrame();

                calibrationFrame.setVisible(true);
                calibrationFrame.pack();
                calibrationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            });
        } catch (InvocationTargetException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        OpenCVLoader.loadOpenCV();
        new VisionTest();
    }
}
