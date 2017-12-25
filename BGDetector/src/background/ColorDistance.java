
package background;

/**
 *
 * Created on 21/11/2017 2:46:56 PM
 * @author Rubaiyat Jahan Mumu
 *
 **/

public class ColorDistance {
	
	public double calculateDistance(Pixel p, Pixel np)
	{
		double distance = Math.abs((p.red - np.red)*(p.red - np.red)) + Math.abs((p.green-np.green)*(p.green-np.green)) + Math.abs((p.blue-np.blue)*(p.blue-np.blue)); 
		return distance;
	}
}
