import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

class PicturePanel extends JPanel {

		private static final long serialVersionUID = 1L;
		private Image image;
		private Pair coordinates;
		
		public void setImage(Image image) {
			this.image = image;
			this.coordinates = null;
		}
		
		public void setCoordinates(Pair coordinates) {
			this.coordinates = coordinates;
		}
		
		public void setPicture(Picture picture) {
			image = picture.getImage();
			coordinates = picture.getCoordinates();
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(image, 0, 0, 640, 480, null);
			if(coordinates != null) {
				g.setColor(Color.RED);
				g.fillOval(coordinates.getX(), coordinates.getY(), 10, 10);
			}
		}
		
	}