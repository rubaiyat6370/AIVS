package background;

import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

public class Run {

	static String imageName;
	static String ext;
	static Pixel[][] input;
	static int[] red;
	static int[] green;
	static int[] blue;
	static int[][] output;
	static int height;
	static int width;
    private BufferedImage img = null;
    private double avgBackground = 0.0;
    private double difference = 0.0;
    
	// List of pixel from background and body
	static List<Pixel> background;
	static List<Pixel> dress;

	// To generate random pixel value
	static Random random = new Random();

	// File representing the folder that you select using a FileChooser
	// static final File dir = new File("D:\\Dropbox\\8th semester
	// Java\\BGDetector\\src");

	static final File dir = new File("C:\\Users\\RubaiyatJahan\\Documents\\input");

	// array of supported extensions (use a List if you prefer)
	static final String[] EXTENSIONS = new String[] { "gif", "png", "bmp", "jpg", "jpeg" // and other formats you need
	};
	// filter to identify images based on their extensions
	static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

		@Override
		public boolean accept(final File dir, final String name) {
			for (final String ext : EXTENSIONS) {
				if (name.endsWith("." + ext)) {
					return (true);
				}
			}
			return (false);
		}
	};
	
	public double getDifference() {
		return this.difference;
	}
	
	public double getAvgBackground() {
		return this.avgBackground;
	}
	
	public BufferedImage getImage() {
		return this.img;
	}
	
	
	
	public void startImageProcessing(File f)
	{
		BufferedImage img = null;
		
		background = new ArrayList<>();
		try {
			img = ImageIO.read(f);
			
			this.img = img;
			transformToGrayScale(img);
			checkDress();
			// to display in your UI
			System.out.println("image: " + f.getName());
			System.out.println(" width : " + img.getWidth());
			System.out.println(" height: " + img.getHeight());
			System.out.println(" size  : " + f.length());

			System.out.println("\n\n\n");
		} catch (IOException e) {
			this.img = null;
		}
	}

	public static void run(String[] args) {
		
		Run r = new Run();

		dress = new ArrayList<>();

		if (dir.isDirectory()) { // make sure it's a directory
			for (final File f : dir.listFiles(IMAGE_FILTER)) {  //dir.listFiles(IMAGE_FILTER)
				BufferedImage img = null;
				background = new ArrayList<>();
				try {
					img = ImageIO.read(f);

					r.transformToGrayScale(img);
					r.checkDress();
					// to display in your UI
					System.out.println("image: " + f.getName());
					System.out.println(" width : " + img.getWidth());
					System.out.println(" height: " + img.getHeight());
					System.out.println(" size  : " + f.length());

					System.out.println("\n\n\n");
				} catch (final IOException e) {
					// handle errors here
				}
			}
		}
	}

	

	private void transformToGrayScale(BufferedImage image) {
		width = image.getWidth();
		height = image.getHeight();
		input = new Pixel[height][width];
		output = new int[height][width];
		int red = 0;
		int green = 0;
		int blue = 0;

		// System.out.println("width :" + width + " Height :" + height);

		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				int value = image.getRGB(col, row);

				red = (value & 0x00ff0000) >> 16;
				green = (value & 0x0000ff00) >> 8;
				blue = value & 0x000000ff;

				input[row][col] = new Pixel(red, green, blue);
				// System.out.println(input[row][col]);
			}
		}

	}

	public double checkBackground() {

		System.out.println(" \n\nCalculating background pixels :");
		Pixel p = input[30][30];
		int mismatched = 0;
		int total = 0;
		double totalDist = 0;
		double avgDist = 0;

		int midX = width / 2;
		int midY = height / 2;

		Pixel topLeft = input[30][30];
		Pixel topRight = input[30][width - 30];
		// Pixel topMid = input[30][midX];
		Pixel midLeft = input[midY][30];
		Pixel midRight = input[midY][width - 30];

		background.add(topLeft);
		background.add(topRight);
		// background.add(topMid);
		background.add(midLeft);
		background.add(midRight);

		double dis = 0.0;
		System.out.println("\nLeft sided pixels:\n");
		for (int row = 30; row < midY; row++) {

			total++;
			int r = random.nextInt(10) - 5;
			Pixel np = input[row][30 + r];
			dis = calculateDistance(p, np);
			totalDist = totalDist + dis; // Calculate total distance
			if (dis < 10) {
				// System.out.println("matched " + dis+ "point: ("+ row+","+(30+r)+")");
			} else {
				// System.out.println("mismatched! " + dis+ "point: ("+ row+","+(30+r)+")");
				mismatched++;
			}

		}

		System.out.println("\nRight sided pixels:\n");
		for (int row = 30; row < midY; row++) {

			total++;
			int r = random.nextInt(10) - 5;
			Pixel np = input[row][width - 30 + r];
			dis = calculateDistance(p, np);
			totalDist = totalDist + dis; // Calculate total distance
			if (dis < 10) {
				// System.out.println("matched " + dis + "point: ("+ row+","+(width-30+r)+")");
			} else {
				// System.out.println("mismatched! " + dis + "point: ("+
				// row+","+(width-30+r)+")");
				mismatched++;
			}

		}

		System.out.println("\nKey points: " + background.size() + "\n");
		for (int i = 0; i < background.size(); i++) {

			total++;
			dis = calculateDistance(p, background.get(i));
			totalDist = totalDist + dis;
			if (dis < 10) {
				// System.out.println("matched " + dis);
			} else {
				// System.out.println("mismatched! " + dis);
				mismatched++;
			}
		}

		Double percent = (double) (mismatched / (total)) * 100;

		if (percent > 5) {
			System.out.println("Incorrect format!!!" + "    \nPercent : " + percent);
		}

		avgDist = (double) (totalDist / total);
		this.avgBackground = avgDist;
		System.out.println("Average distance of background pixels :" + avgDist);
		return avgDist;
	}

	public void checkDress() {
		// Add other pixel from dress

		System.out.println("\n\nCalculating Dress pixels: ");

		double totalDist = 0;
		double avgDist = 0;
		double dis = 0;
		int mismatched = 0;
		int total = 0;
		
		Pixel p = input[30][30];

		for (int i = 0; i < 200; i++) {
			int x = random.nextInt(30);
			int y = random.nextInt((2*width) / 3) - width / 3;

			Pixel randomPixel = input[height - 20 - x][width / 2 + y];
			dis = calculateDistance(p, randomPixel);
			totalDist = totalDist + dis;
			if (dis < 30) {
				// System.out.println("matched " + dis);
			} else {
				// System.out.println("mismatched! " + dis);

			}
			//dress.add(randomPixel);

		}
		
		avgDist = totalDist / (200);
		System.out.println("Avarage distance of dress pixels: " + avgDist);
		this.difference = avgDist - checkBackground();
		System.out.println(this.difference);
	}

	public double calculateDistance(Pixel p, Pixel np) {
	
		ColorUtil cu = new ColorUtil();
		double dis = cu.colorDifference(p, np);

		
		return dis;
	}
	
	public double calculateDistance1(Pixel p, Pixel np)
	{
		double distance = Math.abs((p.red - np.red)*(p.red - np.red)) + Math.abs((p.green-np.green)*(p.green-np.green)) + Math.abs((p.blue-np.blue)*(p.blue-np.blue)); 
		return distance;
	}
}