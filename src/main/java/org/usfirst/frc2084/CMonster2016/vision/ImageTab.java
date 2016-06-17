/* 
 * Copyright (c) 2016 RobotsByTheC. All rights reserved.
 *
 * Open Source Software - may be modified and shared by FRC teams. The code must
 * be accompanied by the BSD license file in the root directory of the project.
 */
package org.usfirst.frc2084.CMonster2016.vision;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.opencv.core.Mat;
import org.usfirst.frc.team2084.CMonster2016.vision.ImageConvertor;

/**
 * A panel that displays an image.
 * 
 * @author Ben Wolsieffer
 */
@SuppressWarnings("serial")
public class ImageTab extends JPanel {

    private BufferedImage image;

    private final ImageConvertor convertor = new ImageConvertor();

    public ImageTab(Mat image) {
        setImage(image);
    }

    public void setImage(Mat image) {
        this.image = convertor.toBufferedImage(image);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        int width = getBounds().width;
        int height = getBounds().height;
        // Scale the image to fit in the component and draw it.
        double scale =
                Math.min((double) width / (double) image.getWidth(), (double) height / (double) image.getHeight());

        g.drawImage(image, (int) (width - (scale * image.getWidth())) / 2,
                (int) (height - (scale * image.getHeight())) / 2, (int) ((width + scale * image.getWidth()) / 2),
                (int) (height + scale * image.getHeight()) / 2, 0, 0, image.getWidth(), image.getHeight(), null);
    }
}
