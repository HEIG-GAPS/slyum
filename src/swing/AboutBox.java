package swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import utility.Utility;

/**
 * Show the about box for Slyum.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
@SuppressWarnings("serial")
public class AboutBox extends JDialog
{

	/**
	 * Create a new AboutBox with informations on Slyum.
	 * 
	 * @param parent
	 *            the parent JFrame
	 */
	public AboutBox(JFrame parent)
	{
		super(parent);

		setTitle("About Slyum");
		setModalityType(ModalityType.APPLICATION_MODAL);
		setSize(new Dimension(700, 300));
		setLocationRelativeTo(parent);
		setResizable(false);

		setContentPane(new JPanel() {

			@Override
			public void paintComponent(Graphics g)
			{
				final Graphics2D g2 = (Graphics2D) g;
				Utility.setRenderQuality(g2);

				final Rectangle bounds = getBounds();
				final Rectangle whiteRectangle = new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height / 2);
				final Rectangle controlRectangle = new Rectangle(bounds.x, bounds.y + bounds.height / 2, bounds.width, bounds.height / 2);
				final Point[] separator = new Point[] { new Point(bounds.x, bounds.y + bounds.height / 2), new Point(bounds.x + bounds.width, bounds.y + bounds.height / 2) };

				final URL imageLogoURL = Slyum.class.getResource("resources/icon/logo148.png");
				final URL imageHeigvdURL = Slyum.class.getResource("resources/heigvd.gif");

				BufferedImage imgLogo = null;
				BufferedImage imgHeigvd = null;

				try
				{
					imgLogo = ImageIO.read(imageLogoURL);
					imgHeigvd = ImageIO.read(imageHeigvdURL);
				} catch (final IOException e)
				{
					e.printStackTrace();
				}

				final Point imgLocation = new Point(bounds.x + bounds.width - imgLogo.getWidth() + 20, bounds.y + 20);

				g2.setColor(Color.WHITE);
				g2.fillRect(whiteRectangle.x, whiteRectangle.y, whiteRectangle.width, whiteRectangle.height);

				g2.drawImage(imgLogo, imgLocation.x, imgLocation.y, this);

				g2.setColor(SystemColor.control);
				g2.fillRect(controlRectangle.x, controlRectangle.y, controlRectangle.width, controlRectangle.height);

				g2.setColor(Color.LIGHT_GRAY);
				g2.drawLine(separator[0].x, separator[0].y, separator[1].x, separator[1].y);

				g2.setColor(Color.WHITE);
				g2.drawLine(separator[0].x, separator[0].y + 1, separator[1].x, separator[1].y + 1);

				g2.setColor(Color.ORANGE);
				g2.fillOval(17, 15, 20, 20);

				g2.setColor(Color.DARK_GRAY);
				g2.drawOval(17, 15, 20, 20);

				final Font baseFont = new Font("Ubuntu", Font.PLAIN, 100);
				g2.setFont(baseFont);
				g2.setColor(Color.DARK_GRAY);
				g2.drawString("Slyum", 40, 100);

				g2.setFont(baseFont.deriveFont(20.5f));
				g2.drawString("ver. " + Slyum.version, 350, 110);

				g2.setFont(baseFont.deriveFont(12.5f));
				g2.drawString("UML class diagram editor developped at heig-vd.", 20, bounds.height / 2 + 20);
				g2.drawString("Works with Java and Swing, use UML 1.4 and XML.", 20, bounds.height / 2 + 40);
				g2.drawString("Developer : Miserez David, Responsable : Donini Pier", 20, bounds.height / 2 + 70);
				g2.drawString("Copyrigth � 2011 Miserez David", 20, bounds.height / 2 + 110);

				g2.drawImage(imgHeigvd, bounds.x + bounds.width - imgHeigvd.getWidth() - 20, bounds.y + bounds.height / 2 + 20, this);
			}
		});

		getContentPane().setLayout(null);

		final Rectangle bounds = getBounds();
		final Dimension buttonSize = new Dimension(100, 25);
		final JButton buttonOK = new JButton("Ok");
		buttonOK.setBounds(bounds.width - buttonSize.width - 25, bounds.height - buttonSize.height - 40, buttonSize.width, buttonSize.height);
		getContentPane().add(buttonOK);
		buttonOK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
			}
		});

		setVisible(true);
	}
}
