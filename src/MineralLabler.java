import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class MineralLabler {
	
	private JFrame frame;
	private JFileChooser directoryChooser;
	private JButton loadButton, nextButton, delButton;
	private JButton inDirButton, outDirButton;
	private JLabel inDirLabel, outDirLabel;
	private JCheckBox fastMode;
	private PicturePanel picturePanel;
	private JDialog pictureDialog;
	
	private File inDirectory;
	private File outDirectory;
	private List<File> inputPictures;
	
	private int picNumber = -1;
	private Image resizedPicture;
	private Pair output;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(MineralLabler::new);
	}

	/**
	 * Create the application.
	 */
	public MineralLabler() {
		frame = new JFrame();
		frame.setBounds(100, 100, 340, 340);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));

		directoryChooser = new JFileChooser();
		directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		directoryChooser.setDialogTitle("Select Directory");

		JLabel title = new JLabel("Mineral Labler");
		title.setFont(title.getFont().deriveFont(40f)); //must be a float
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(title);
		
		inDirButton = new JButton("Select Input Directory");
		add(inDirButton);
		inDirButton.addActionListener(e -> chooseInDirectory());
		inDirButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		inDirLabel = new JLabel(" ");
		inDirLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(inDirLabel);

		add(Box.createVerticalGlue());
		
		outDirButton = new JButton("Select Output Directory");
		add(outDirButton);
		outDirButton.addActionListener(e -> chooseOutDirectory());
		outDirButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		outDirLabel = new JLabel(" ");
		outDirLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(outDirLabel);

		add(Box.createVerticalGlue());
		
		loadButton = new JButton("Load Images");
		loadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		loadButton.setEnabled(false);
		loadButton.addActionListener(e -> loadPictures());
		add(loadButton);

		add(Box.createVerticalGlue());
		add(new JLabel(" ")); //padding
		
		nextButton = new JButton("Next Image");
		nextButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		nextButton.setEnabled(false);
		nextButton.addActionListener(e -> nextPicture());
		add(nextButton);
		
		add(Box.createVerticalGlue());
		add(new JLabel(" ")); //padding
		
		delButton = new JButton("Delete Image");
		delButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		delButton.setEnabled(false);
		delButton.addActionListener(e -> deletePicture());
		add(delButton);
		
		add(Box.createVerticalGlue());
		add(new JLabel(" ")); //padding
		
		fastMode = new JCheckBox("Fast classifying - use with caution");
		fastMode.setSelected(false);
		fastMode.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(fastMode);
		
		add(Box.createVerticalGlue());
		
		frame.setVisible(true);
	}
	
	private void add(Component comp) {
		frame.getContentPane().add(comp);
	}

	private void chooseInDirectory() {
		directoryChooser.setDialogTitle("Select Input Directory");
		directoryChooser.setCurrentDirectory(inDirectory == null ? new File(".") : inDirectory);
		directoryChooser.showOpenDialog(frame);
		File tmpInDirectory = directoryChooser.getSelectedFile();
		if(tmpInDirectory != null)
			inDirectory = tmpInDirectory;
		if(inDirectory != null) {
			inDirLabel.setText(inDirectory.getAbsolutePath());
			if(outDirectory != null) 
				loadButton.setEnabled(true);
		}
	}

	private void chooseOutDirectory() {
		directoryChooser.setDialogTitle("Select Output Directory");
		directoryChooser.setCurrentDirectory(outDirectory == null ? new File(".") : outDirectory);
		directoryChooser.showOpenDialog(frame);
		File tmpOutDirectory = directoryChooser.getSelectedFile();
		if(tmpOutDirectory != null)
			outDirectory = tmpOutDirectory;
		if(outDirectory != null) {
			outDirLabel.setText(outDirectory.getAbsolutePath());
			if(inDirectory != null)
				loadButton.setEnabled(true);
		}
	}

	private void loadPictures() {
		inDirButton.setEnabled(false);
		outDirButton.setEnabled(false);
		loadButton.setEnabled(false);
		delButton.setEnabled(true);
		inputPictures = new ArrayList<>(Arrays.asList(
										inDirectory.listFiles((f, s) -> s.endsWith(".png") ||
																		s.endsWith(".jpg") ||
																		s.endsWith(".bmp") ||
																		s.endsWith(".gif"))));
		picNumber = 0;
		pictureDialog = new JDialog(frame, "Select Gold Mineral", false);
		pictureDialog.setBounds(500,100,680,520);
		
		picturePanel = new PicturePanel();
		picturePanel.addMouseListener(new PictureClickListener());
		pictureDialog.add(picturePanel);
		
		pictureDialog.setVisible(true);
		drawPicture();
	}

	private void drawPicture() {
		try {
			Image originalPicture =  ImageIO.read(inputPictures.get(picNumber));
	    	resizedPicture = originalPicture.getScaledInstance(320, 240, Image.SCALE_FAST);
	    	output = null;
	    	picturePanel.setImage(resizedPicture);
			picturePanel.repaint();
		} catch (IOException e) {
			errorAndExit(e);
		}
	}

	private void nextPicture() {
		nextButton.setEnabled(false);
		savePicture();
		picNumber++;
		if(picNumber == inputPictures.size()) {
			JOptionPane.showMessageDialog(frame, "Successfully labled all pictures");
			System.exit(0);
		} else {
			drawPicture();
		}
	}
	
	private void deletePicture() {
		nextButton.setEnabled(false);
		inputPictures.remove(picNumber);
		output = null;
		drawPicture();
	}
	
	private void savePicture() {
		try {
			BufferedImage image = toBufferedImage(resizedPicture);
			String fileName = "LabledMineral-" + UUID.randomUUID().toString() + ".txt";
			try (PrintWriter writer = new PrintWriter(new FileWriter(new File(outDirectory, fileName)))) {
				writer.print(output.getX());
				writer.print(',');
				writer.print(output.getY());
				writer.println(',');
				for (int r = 0; r < 240; r++) {
					for (int c = 0; c < 320; c++) {
						int pixel = image.getRGB(c, r);
					    int red = (pixel >> 16) & 0xff;
					    int green = (pixel >> 8) & 0xff;
					    int blue = (pixel) & 0xff;
					    writer.print(red);
					    writer.print(',');
					    writer.print(green);
					    writer.print(',');
					    writer.print(blue);
					    writer.print(',');
					}
					writer.println();
				}
			}
		} catch (IOException e) {
			errorAndExit(e);
		}
	}
	
	// https://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage
	private static BufferedImage toBufferedImage(Image img) {
	    if (img instanceof BufferedImage) {
	        return (BufferedImage) img;
	    }

	    // Create a buffered image with transparency
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();

	    // Return the buffered image
	    return bimage;
	}
	
	private void errorAndExit(Exception e) {
		JOptionPane.showMessageDialog(frame, "An error occured. Please try again later.");
		e.printStackTrace();
		System.exit(1);
	}

	public class PictureClickListener implements MouseListener {
	
		@Override
		public void mouseClicked(MouseEvent e) {
			output = new Pair(e.getX(), e.getY());
			picturePanel.setCoordinates(output);
			picturePanel.repaint();
			nextButton.setEnabled(true);
			if(fastMode.isSelected())
				nextPicture();
		}
		@Override public void mouseEntered(MouseEvent e) {}
		@Override public void mouseExited(MouseEvent e) {}
		@Override public void mousePressed(MouseEvent e) {}
		@Override public void mouseReleased(MouseEvent e) {}
	
	}
	
}
