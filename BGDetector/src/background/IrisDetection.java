
package background;

import java.io.File;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

/**
 *
 * Created on 12/11/2017 1:56:27 PM All rights reserved by Rubaiyat Jahan Mumu
 *
 **/

public class IrisDetection {

	static CascadeClassifier detector;
	static int eyenumber = 0;
	public IrisDetection(File file) {

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		detector = new CascadeClassifier();

		// for eye detection
		detector.load("D:\\OpenCv\\opencv\\sources\\data\\haarcascades_cuda\\closed_eye_classifier.xml");
		// detector.load ("I:\\Haar Training\\cascade2xml\\myfacedetector.xml" );
		//closed_eye_classifier, haarcascade_lefteye_2splits.xml
		
		Mat image = Imgcodecs.imread ( file.getAbsolutePath() );
		Mat face = null;
		if(image!=null) {
			face = detectFace ( image );
		}
		 
		Mat eyes = trainEyeDetector(face);
		
		detectEye(eyes);
		
	}

	public Mat detectFace(Mat image) {
		System.out.println("face detection started");
		// Detecting faces
		Mat imageFace = null;

		MatOfRect faceDetections = new MatOfRect();
		detector.load ("D:\\OpenCv\\opencv\\sources\\data\\haarcascades_cuda\\haarcascade_frontalface_alt2.xml" );
		detector.detectMultiScale(image, faceDetections);
		System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));

		// Creating a rectangular box showing faces detected
		Rect faceCrop = null;
		if (faceDetections.toArray().length >= 1) {

			for (Rect rect : faceDetections.toArray()) {
				Imgproc.rectangle(image, new Point(rect.x, rect.y),
						new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
				faceCrop = new Rect(rect.x, rect.y, rect.width, rect.height);
				imageFace = new Mat(image, faceCrop);
			}
		} else {
			System.out.println("Incorrect format!!!");
			imageFace = image;
		}

		return imageFace;
	}

	public Mat trainEyeDetector(Mat image) {

		Rect eyeArea = new Rect(0, image.height() / 2 - image.height() / 3, image.width(), image.height() / 3);
		Mat eyes = new Mat(image, eyeArea);
		Imgcodecs.imwrite("D:\\Test Images\\eye" + ".jpg", eyes);
		return eyes;
	}

	public void detectEye(Mat image) {
		MatOfRect eyeDetections = new MatOfRect();
		detector.load("D:\\OpenCv\\opencv\\sources\\data\\haarcascades_cuda\\closed_eye_classifier.xml");
		//detector.load ("I:\\Haar Training\\cascade2xml\\myfacedetector.xml" );
		detector.detectMultiScale(image, eyeDetections);
		System.out.println(String.format("Detected %s eyes", eyeDetections.toArray().length));

		// Creating a rectangular box showing faces detected
		Rect eyeCrop = null;
		// detector.load
		// ("D:\\OpenCv\\opencv\\sources\\data\\haarcascades\\haarcascade_eye.xml");

		if (eyeDetections.toArray().length == 2) {

		} else {
			System.out.println("Incorrect format!!");
		}
		eyenumber = eyeDetections.toArray().length;
	}
	
	public int getEyeNumber()
	{
		return eyenumber;
	}
}
