/* 
 * Copyright (c) 2016 RobotsByTheC. All rights reserved.
 *
 * Open Source Software - may be modified and shared by FRC teams. The code must
 * be accompanied by the BSD license file in the root directory of the project.
 */
package org.usfirst.frc2084.CMonster2016.vision;

import java.io.File;
import java.util.Arrays;

import javax.activation.MimetypesFileTypeMap;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.usfirst.frc.team2084.CMonster2016.vision.CameraCalibration;

/**
 * Window that is used for calibrating the camera. Use the right to navigate to
 * a folder full of calibration images, then press the "Calibrate" button. View
 * the calibration and copy it to your vision code.
 * 
 * @author Ben Wolsieffer
 */
@SuppressWarnings("serial")
public class CalibrationFrame extends TestingFrame {

    private CameraCalibration calibration;

    public CalibrationFrame() {
        super(new CameraCalibration(new Size(9, 6), 0.080216535, 1));
        setTitle("Camera Calibration");

        calibration = (CameraCalibration) processor;

        add(new ControlPanel(), "pushx, growx");

    }

    private static class CalibrationViewerDialog extends JDialog {

        private static String getJava2DArray(String name, Mat matrix) {
            if (matrix.type() != CvType.CV_32F && matrix.type() != CvType.CV_64F) {
                throw new IllegalArgumentException(
                        "Must be a single channel double matrix, instead was: " + CvType.typeToString(matrix.type()));
            }
            StringBuilder arrayString = new StringBuilder();

            arrayString.append("public static final double[][] ");
            arrayString.append(name);
            arrayString.append(" = {\n");

            for (int r = 0; r < matrix.rows(); r++) {
                arrayString.append("    {");
                for (int c = 0; c < matrix.cols(); c++) {
                    double[] v = matrix.get(r, c);
                    arrayString.append(v[0]);
                    if (c < matrix.cols() - 1) {
                        arrayString.append(", ");
                    }
                }
                arrayString.append('}');
                if (r < matrix.rows() - 1) {
                    arrayString.append(',');
                }
                arrayString.append('\n');
            }
            arrayString.append("};");

            return arrayString.toString();
        }

        private static String getJavaArray(String name, MatOfDouble matrix) {
            StringBuilder arrayString = new StringBuilder();

            arrayString.append("public static final double[] ");
            arrayString.append(name);
            arrayString.append(" = ");
            arrayString.append(Arrays.toString(matrix.toArray()).replace('[', '{').replace(']', '}'));

            arrayString.append(';');

            return arrayString.toString();
        }

        private JTextArea textArea = new JTextArea();
        private JButton okButton = new JButton("OK");

        /**
         * 
         */
        public CalibrationViewerDialog(Mat cameraMatrix, MatOfDouble distortionMatrix) {
            setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

            okButton.addActionListener((e) -> dispose());
            textArea.setText(getJava2DArray("CAMERA_MATRIX", cameraMatrix) + "\n\n"
                    + getJavaArray("DISTORTION_MATRIX", distortionMatrix));
            textArea.setEditable(false);
            add(textArea);
            add(okButton);

            setModal(true);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            pack();
        }
    }

    private class CalibrationWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            synchronized (calibration) {
                calibration.clearCalibrationImages();
            }
            for (File f : directoryChooser.getCurrentDirectory().listFiles()) {
                if (f.isFile()
                        && new MimetypesFileTypeMap().getContentType(f).substring(0, 5).equalsIgnoreCase("image")) {
                    Mat image = Imgcodecs.imread(f.getAbsolutePath());
                    synchronized (calibration) {
                        calibration.process(image, true);
                    }
                }
            }
            synchronized (calibration) {
                calibration.calibrate();
            }
            return null;
        }

    }

    private class ControlPanel extends JPanel {

        private final JButton calibrateButton = new JButton("Calibrate");
        private final JButton viewCalibrationButton = new JButton("View Calibration");

        public ControlPanel() {
            calibrateButton.addActionListener((e) -> new CalibrationWorker().execute());
            viewCalibrationButton.addActionListener((e) -> {
                CalibrationViewerDialog dialog =
                        new CalibrationViewerDialog(calibration.getCameraMatrix(), calibration.getDistCoeffs());
                dialog.setLocationRelativeTo(CalibrationFrame.this);
                dialog.setVisible(true);
            });

            add(calibrateButton);
            add(viewCalibrationButton);
        }
    }

}
