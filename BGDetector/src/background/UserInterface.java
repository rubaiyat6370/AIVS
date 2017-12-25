
package background;

/**
 *
 * Created on 11/11/2017 12:20:16 AM
 * All rights reserved by Rubaiyat Jahan Mumu
 *
 **/

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

class UserInterface {
	private JFrame j;
	private JMenu jmenu;
	private JMenuBar jbar;
	private JMenuItem jmi, jexit;
	private JPanel jpanel;
	JLabel image;
	ImageIcon ic;
	Image img;

	UserInterface() {
		j = new JFrame("Image Viewer");
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// j.setExtendedState(Frame.MAXIMIZED_BOTH);
		// j.setLocationRelativeTo(null);
		j.setLocationByPlatform(true);
		j.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		jpanel = new JPanel();
		jpanel.setLayout(new BorderLayout());
		jpanel.setPreferredSize(new Dimension(300, 350));
		image = new JLabel(" ");
		jpanel.add(image, BorderLayout.CENTER);

		c.anchor = GridBagConstraints.CENTER;
		// c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = c.gridy = 0;
		c.gridwidth = 100;
		c.weightx = 0.1;
		c.weighty = .8;
		c.ipady = 1;
		// c.insets = new Insets(5, 5, 10, 5);
		jpanel.setBackground(Color.BLACK);
		j.add(jpanel, c);

		// Creating Menu
		jbar = new JMenuBar();
		jmenu = new JMenu("File");
		jmi = new JMenuItem("Open");
		Run r = new Run();

		jmi.addActionListener(new ActionListener() {
			private BufferedImage bufferedImage;

			public void actionPerformed(ActionEvent ae) {
				JFileChooser fc = new JFileChooser();
				int result = fc.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					r.startImageProcessing(file);
					IrisDetection id = new IrisDetection(file);
					// bufferedImage = new BufferedImage(0, 0, 0);
					bufferedImage = r.getImage();

					if (bufferedImage != null) {
						boolean flag = true;
						image.setIcon(
								new ImageIcon(bufferedImage.getScaledInstance(300, 350, Image.SCALE_AREA_AVERAGING)));
						if (id.getEyeNumber() > 0)
						{
							setWarningMsg("Closed eye detected!!!");
							flag = false;
						}
						if (r.getAvgBackground() > 15) {
							setWarningMsg("Non Uniform Background Detected!!!");
							flag = false;
						}
						if (r.getDifference() < 60) {
							setWarningMsg("Dress color contrast with background is not significant!!!");
							// System.exit(0);
							flag = false;
						}
						if(flag) {
							successMsg("Correct Format!!!");
						}
					}

					else {
						image.setIcon(null);
						setWarningMsg("Incorrect file format!!!");

					}

				}
			}
		});

		jexit = new JMenuItem("Exit");
		jexit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				System.exit(0);
			}
		});
		jmenu.add(jmi);
		jmenu.add(jexit);
		jbar.add(jmenu);
		j.setJMenuBar(jbar);

		// j.setSize(800, 600);
		j.pack();
		j.setResizable(true);
		j.setVisible(true);
	}

	public BufferedImage startImageProcessing(File f) {
		BufferedImage img = null;

		try {
			img = ImageIO.read(f);

			// to display in your UI
			System.out.println("image: " + f.getName());
			System.out.println(" width : " + img.getWidth());
			System.out.println(" height: " + img.getHeight());
			System.out.println(" size  : " + f.length());

			System.out.println("\n\n\n");
		} catch (final IOException e) {
			// handle errors here
		}
		return img;
	}

	public void setWarningMsg(String text) {
		Toolkit.getDefaultToolkit().beep();
		JOptionPane optionPane = new JOptionPane(text, JOptionPane.WARNING_MESSAGE);
		JDialog dialog = optionPane.createDialog("Warning!");
		dialog.setAlwaysOnTop(true);
		dialog.setVisible(true);
	}

	public void successMsg(String text) {
		// Toolkit.getDefaultToolkit().beep();
		JOptionPane optionPane = new JOptionPane(text, JOptionPane.DEFAULT_OPTION);
		JDialog dialog = optionPane.createDialog("Success!!");
		dialog.setAlwaysOnTop(true);
		dialog.setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new UserInterface();
			}
		});
	}
}
