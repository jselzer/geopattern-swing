package com.selzerj;


import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Main {

	public static void main(String[] args) {
		new Main();
	}

	public Main() {
		JFrame frame = new JFrame("Hello World Java Swing");

		// set frame site
		frame.setMinimumSize(new Dimension(800, 600));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// center the JLabel
		JLabel lblText = new JLabel("Hello World!", SwingConstants.CENTER);
		GeoPatternPane geoPatternPane = new GeoPatternPane();
		frame.getContentPane().add(geoPatternPane);

		geoPatternPane.add(lblText);


		// add JLabel to JFrame
//		frame.getContentPane().add(lblText);

		// display it
		frame.pack();
		frame.setVisible(true);

	}



	public class GeoPatternPane extends JPanel {

		private BufferedImage tile;

		public GeoPatternPane() {
			convertToImg("/plus_signs.svg");
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(200, 200);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g.create();
			int tileWidth = tile.getWidth();
			int tileHeight = tile.getHeight();
			for (int y = 0; y < getHeight(); y += tileHeight) {
				for (int x = 0; x < getWidth(); x += tileWidth) {
					g2d.drawImage(tile, x, y, this);
				}
			}
			g2d.dispose();
		}


		private void convertToImg(String svgResource) {

			try (InputStream in = getClass().getResourceAsStream(svgResource);
				 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				TranscoderInput input = new TranscoderInput(in);

				ImageTranscoder t = new ImageTranscoder() {

					@Override
					public BufferedImage createImage(int w, int h) {
						return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
					}

					@Override
					public void writeImage(BufferedImage image, TranscoderOutput out)
							throws TranscoderException {
						tile = image;
					}
				};
				t.transcode(input, null);

			} catch (TranscoderException e) {
				throw new RuntimeException("Failed to convert SVG to TIFF", e);
			} catch (IOException e) {
				throw new RuntimeException("IOException converting SVG to TIFF", e);
			}
		}
	}
}