
package background;

/**
 *
 * Created on 04/10/2017 4:20:59 PM
 * All rights reserved by Rubaiyat Jahan Mumu
 *
 **/

public class Pixel {

	int red;
	int green;
	int blue;
	
	public Pixel(int red, int green, int blue)
	{
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public int getRed()
	{
		return red;
	}
	
	public int getGreen()
	{
		return green;
	}
	
	public int getBlue()
	{
		return blue;
	}

	@Override
	public String toString() {
		return "Pixel [red=" + red + ", green=" + green + ", blue=" + blue + "]";
	}
	
	
}
