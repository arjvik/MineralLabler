import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class LocalMineralLoader {
	
	private JFrame frame;
	private JFileChooser directoryChooser;
	private JButton loadButton, nextButton;
	private JButton inDirButton;
	private JLabel inDirLabel;
	private PicturePanel picturePanel;
	private JDialog pictureDialog;
	
	private File inDirectory;
	private File[] pictures;
	
	private int picNumber = -1;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(LocalMineralLoader::new);
	}

	/**
	 * Create the application.
	 */
	public LocalMineralLoader() {
		frame = new JFrame();
		frame.setBounds(100, 100, 340, 240);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));

		directoryChooser = new JFileChooser();
		directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		directoryChooser.setDialogTitle("Select Directory");

		JLabel title = new JLabel("Mineral Loader");
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
			loadButton.setEnabled(true);
		}
	}

	private void loadPictures() {
		inDirButton.setEnabled(false);
		loadButton.setEnabled(false);
		nextButton.setEnabled(true);
		pictures = inDirectory.listFiles((f, s) -> s.endsWith(".txt"));
		picNumber = 0;
		pictureDialog = new JDialog(frame);
		pictureDialog.setBounds(500,100,680,520);
		
		picturePanel = new PicturePanel();
		pictureDialog.add(picturePanel);
		
		pictureDialog.setVisible(true);
		drawPicture();
	}

	private void drawPicture() {
		Picture picture = getPicture();
		picturePanel.setImage(picture.getImage());
		picturePanel.setCoordinates(picture.getCoordinates());
		picturePanel.repaint();
	}

	private Picture getPicture() {
		try (FileReader fileReader = new FileReader(pictures[picNumber]);
			 BufferedReader reader = new BufferedReader(fileReader)) {
			String[] coordLine = reader.readLine().split(",");
			Pair coordinates = new Pair(Integer.parseInt(coordLine[0]), Integer.parseInt(coordLine[1]));
			BufferedImage image = new BufferedImage(320, 240, BufferedImage.TYPE_INT_ARGB);
			for (int r = 0; r < 240; r++) {
				String[] line = reader.readLine().split(",");
				for (int c = 0; c < 320; c++) {
					int red = Integer.parseInt(line[3*c]);
					int green = Integer.parseInt(line[3*c+1]);
					int blue = Integer.parseInt(line[3*c+2]);
					int pixel = (0xff << 24) | (red << 16) | (green << 8) | (blue);
					image.setRGB(c, r, pixel);
				}
			}
			return new Picture(image, coordinates);
		} catch (IOException e) {
			errorAndExit(e);
			return null;
		}
	}
	
	private void errorAndExit(Exception e) {
		JOptionPane.showMessageDialog(frame, "An error occured. Please try again later.");
		e.printStackTrace();
		System.exit(1);
	}

	private void nextPicture() {
		picNumber++;
		if(picNumber == pictures.length)
			JOptionPane.showMessageDialog(frame, "No more pictures available");
		else
			drawPicture();
	}
	
}
