import java.awt.Image;

public class Picture {
	private Image image;
	private Pair coordinates;
	
	public Picture(Image image, Pair coordinates) {
		this.image = image;
		this.coordinates = coordinates;
	}

	public Image getImage() {
		return image;
	}

	public Pair getCoordinates() {
		return coordinates;
	}
	
	
	
}
