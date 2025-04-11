package com.selzerj;


import com.selzerj.geopattern.PatternGenerator;
import com.selzerj.geopattern.pattern.Pattern;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Main {

	GeoPatternPane geoPatternPane;

	public static void main(String[] args) {
		new Main();
	}

	public Main() {
		JFrame frame = new JFrame("Hello World Java Swing");

		// set frame site
		frame.setMinimumSize(new Dimension(800, 600));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// center the JLabel
		JLabel lblText = new JLabel("Type something!", SwingConstants.CENTER);
		JTextField seedField = new JTextField("Jason");
		seedField.getDocument().addDocumentListener(new MyDocumentListener());
		geoPatternPane = new GeoPatternPane(generateSvg(seedField.getText()));
		frame.getContentPane().add(geoPatternPane);

		geoPatternPane.add(lblText);
		geoPatternPane.add(seedField);

		// display it
		frame.pack();
		frame.setVisible(true);

	}

	private String generateSvg(String seed) {
		Pattern result = new PatternGenerator(seed).generate();
		return result.toSvg();
	}


	public class GeoPatternPane extends JPanel {

		private BufferedImage tile;

		public GeoPatternPane(String svgContent) {
			convertToImg(svgContent);
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(100, 100);
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


		private void convertToImg(String svgContent) {

			try (InputStream in = new ByteArrayInputStream(svgContent.getBytes())) {
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


	class MyDocumentListener implements DocumentListener {

		public void insertUpdate(DocumentEvent e) {
			updatePattern(e);
		}
		public void removeUpdate(DocumentEvent e) {
			updatePattern(e);
		}
		public void changedUpdate(DocumentEvent e) {
			//Plain text components do not fire these events
		}

		public void updatePattern(DocumentEvent e) {
			Document doc = e.getDocument();
			String value = getValue(doc);

			System.out.println(value);

			geoPatternPane.convertToImg(generateSvg(value));
			geoPatternPane.repaint();
		}

		private String getValue(Document doc) {
			try {
				return doc.getText(0, doc.getLength());
			} catch (BadLocationException e) {
				return "";
			}
		}
	}
}