package faceDetection;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.Feature2D;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class FaceDetection {
	static CascadeClassifier detector;

	static int face = 0;
	static int eye = 0;

	static final File dir = new File("D:\\Openeyes"); // D:\Open eyes

	// array of supported extensions (use a List if you prefer)
	static final String[] EXTENSIONS = new String[] { "png", "bmp", "jpg", "jpeg" // and other formats you need
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

	public static void main(String[] args) {

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		detector = new CascadeClassifier();

		// for eye detection
		detector.load("D:\\OpenCv\\opencv\\sources\\data\\haarcascades_cuda\\closed_eye_classifier.xml");

		// detector.load ("I:\\Haar Training\\cascade2xml\\myfacedetector.xml" );
		// //closed_eye_classifier, haarcascade_lefteye_2splits.xml
		// D:\Haar Training\training\Backup
		// for face detection
		// detector.load
		// ("D:\\OpenCv\\opencv\\sources\\data\\haarcascades_cuda\\haarcascade_frontalface_alt2.xml"
		// );
		System.out.println("Xml loaded");

		System.out.println("Working");

		FaceDetection fd = new FaceDetection();

		if (dir.isDirectory()) { // make sure it's a directory
			for (final File f : dir.listFiles(IMAGE_FILTER)) {

				// img = ImageIO.read(f);
				// System.out.println(f.getName());
				Mat image = Imgcodecs.imread(f.getAbsolutePath());
				// Imgcodecs.imwrite ( "D:\\BMPImage\\face"+ face + ".bmp" , image );
				Mat src_gray = new Mat();
				// Imgproc.cvtColor(image, src_gray, Imgproc.COLOR_BGR2GRAY);
				// Imgcodecs.imwrite ( "D:\\Negative\\eye"+ face + ".jpg" , src_gray );
				// face++;
				// image = fd.detectFace ( image );
				// fd.trainEyeDetector(image);
				//fd.detectEye(image);
			}
		}

		// Input image
		// Mat image = Imgcodecs.imread (
		// "D:\\dataset_B_FacialImages_highResolution\\closedeye1.jpg" );
		// Mat image = Imgcodecs.imread (
		// "D:\\input.jpg" );

		// fd.detectEye(image);

		 fd.findPupilIris();
		 textCircle();

	}

	public Mat detectFace(Mat image) {
		System.out.println("face detection started");
		// Detecting faces
		Mat imageFace = null;

		MatOfRect faceDetections = new MatOfRect();
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
				Imgcodecs.imwrite("D:\\OpenEyeFaces\\face" + face + ".jpg", imageFace);

				// return imageFace;
			}
		} else {
			System.out.println("Incorrect format!!!");
			imageFace = image;
		}
		face++;
		return imageFace;
	}

	public static void textCircle() {

		Mat src = Imgcodecs.imread("D:\\Openeyes\\eye5.bmp");
		//Mat src = img;
		Mat srcH = new Mat();
		src.convertTo(srcH, -1, 0.7, 0);
		Imgcodecs.imwrite("D:\\contrast.jpg", srcH);
		Mat src_gray = new Mat();
		Imgproc.cvtColor(srcH, src_gray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.equalizeHist(src_gray, src_gray);
		Imgcodecs.imwrite("outgray.jpg", src_gray);
		Mat smooth = new Mat();
		Imgproc.GaussianBlur(src_gray, smooth, new Size(11, 11), 4, 4);
		Imgcodecs.imwrite("blur.jpg", smooth);
		Mat circles = new Mat();
		int erosion_size = 5;
		int dilation_size = 5;

		Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
				new Size(2 * erosion_size + 1, 2 * erosion_size + 1));
		Imgproc.erode(smooth, smooth, element);
		Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
				new Size(2 * dilation_size + 1, 2 * dilation_size + 1));
		Imgproc.dilate(smooth, smooth, element1);
		Imgproc.HoughCircles(smooth, circles, Imgproc.CV_HOUGH_GRADIENT, 2, 20, 81, 29, 8, 13);

		System.out.println("Found " + circles.cols() + " circles.");
		for (int i = 0; i < circles.cols(); i++) {
			double[] circle = circles.get(0, i);
			if (src.get((int) circle[1], (int) circle[0])[2] > 100) {
				// list.add(new Ball((int)circle[0],(int)circle[1]));
				Point center = new Point((int) circle[0], (int) circle[1]);

				int radius = (int) circle[2];
				// circle center
				Imgproc.circle(src, center, 3, new Scalar(0, 255, 0), -1, 8, 0);
				// circle outline
				Imgproc.circle(src, center, radius, new Scalar(0, 0, 255), 3, 8, 0);

			}
			Imgcodecs.imwrite("D:\\Houghpic" + i + ".jpg", src);
		}

	}

	public void trainEyeDetector(Mat image) {
		MatOfRect eyeDetections = new MatOfRect();
		detector.detectMultiScale(image, eyeDetections);
		System.out.println(String.format("Detected %s eyes", eyeDetections.toArray().length));

		// Creating a rectangular box showing faces detected
		Rect eyeCrop = null;

		if (eyeDetections.toArray().length >= 2) {
			int i = 0;
			int x = 0;
			int y = 0;
			int height = 0;
			int width = 0;

			Rect eye1 = eyeDetections.toArray()[0];
			Rect eye2 = eyeDetections.toArray()[1];

			if (eye1.x < eye2.x) {
				x = eye1.x;
				width = Math.min(eye2.width * 2 + eye2.width / 2, image.width() - x);
			} else {
				x = eye2.x;
				width = Math.min(eye1.width * 2 + eye1.width / 2, image.width() - x);
			}
			System.out.println(width);

			if (eye1.y < eye2.y)
				y = eye1.y;
			else
				y = eye2.y;

			// width = Math.abs(eye2.width+eye2.x - eye1.x);

			height = eye1.height;

			// Rect eyeArea = new Rect(x,y,width,height);
			// Imgproc.cvtColor(image, src_gray, Imgproc.COLOR_BGR2GRAY);
			Rect eyeArea = new Rect(0, image.height() / 2 - image.height() / 3, image.width(), image.height() / 3);
			Mat eyes = new Mat(image, eyeArea);
			Imgcodecs.imwrite("D:\\Test Images\\eye" + eye + ".bmp", eyes);

			eye++;
		} else {
			Rect eyeArea = new Rect(0, image.height() / 2 - image.height() / 3, image.width(), image.height() / 3);
			Mat eyes = new Mat(image, eyeArea);
			Imgcodecs.imwrite("D:\\Test Images\\eye" + eye + ".bmp", eyes);
			eye++;
		}
	}

	public void detectEye(Mat image) {
		MatOfRect eyeDetections = new MatOfRect();
		detector.detectMultiScale(image, eyeDetections);
		System.out.println(String.format("Detected %s eyes", eyeDetections.toArray().length));

		// Creating a rectangular box showing faces detected
		Rect eyeCrop = null;
		// detector.load
		// ("D:\\OpenCv\\opencv\\sources\\data\\haarcascades\\haarcascade_eye.xml");

		if (eyeDetections.toArray().length == 2) {

			for (Rect eye : eyeDetections.toArray()) {
				Imgproc.rectangle(image, new Point(eye.x, eye.y), new Point(eye.x + eye.width, eye.y + eye.height),
						new Scalar(0, 255, 0));
				eyeCrop = new Rect(eye.x, eye.y, eye.width, eye.height);

				// find the pupil inside the eye rect
				Mat imageEye = new Mat(image, eyeCrop);
				// textCircle(imageEye);
				// FeatureDetectionUsingMSER(imageEye);
				// findPupilIris(imageEye,i++);

				Imgcodecs.imwrite("D:\\Dropbox\\8th semester Java\\IrisDetection\\output\\eye" + eye.x + ".jpg",
						imageEye);
			}
		} else {
			System.out.println("Incorrect format!!");
		}

	}

	private void findPupilIris() {

		int i = 0;
		Mat newimg = Imgcodecs.imread("D:\\Openeyes\\eye5.bmp");
		Mat v = new Mat(newimg.rows(), newimg.cols(), newimg.type());

		Imgproc.cvtColor(newimg, v, Imgproc.COLOR_BGR2GRAY);
		Imgproc.equalizeHist(v, v);
		// Smoothen the image
		Imgproc.GaussianBlur(v, v, new Size(9, 9), 2,2);
		//Core.addWeighted(v, 1.5, v, -0.5, 0, v);

		if (v != null) {
			Mat circles = new Mat();

			Imgproc.HoughCircles(v, circles, Imgproc.CV_HOUGH_GRADIENT, 1, newimg.width()/3, 30, 20, 1, 10);
			// Imgproc.HoughCircles( v, circles, Imgproc.CV_HOUGH_GRADIENT, 2, v.rows()/1,
			// 30, 20, 1, 8 );

			System.out.println("circles.cols() " + circles.cols());
			if (circles.cols() > 0) {
				System.out.println("1");
				for (int x = 0; x < circles.cols(); x++) {
					System.out.println("2");
					double vCircle[] = circles.get(0, x);

					if (vCircle == null) {
						break;
					}

					Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
					int radius = (int) Math.round(vCircle[2]);

					// draw the found circle
					Imgproc.circle(v, pt, radius, new Scalar(255, 0, 0), 2); // newimg
					// Imgproc.circle(des, pt, radius/3, new Scalar(225,0,0),2); //pupil
					Imgcodecs.imwrite("D:\\Dropbox\\8th semester Java\\IrisDetection\\output" + i + ".jpg", v); // newimg
				}
			}
		}
	}

	public void FeatureDetectionUsingMSER(Mat image) {
		Mat src_gray = new Mat();
		Imgproc.cvtColor(image, src_gray, Imgproc.COLOR_BGR2GRAY);
		Mat smooth = new Mat();
		Imgproc.GaussianBlur(src_gray, smooth, new Size(11, 11), 10, 10);
		FeatureDetector fd = FeatureDetector.create(FeatureDetector.MSER);
		MatOfKeyPoint regions = new MatOfKeyPoint();

		fd.detect(smooth, regions);
		KeyPoint[] array = regions.toArray();
		Mat outputImage = new Mat();
		Scalar color = new Scalar(0, 0, 255); // BGR
		int flags = Features2d.DRAW_RICH_KEYPOINTS; // For each keypoint, the circle around keypoint with keypoint size
													// and orientation will be drawn.
		Features2d.drawKeypoints(image, regions, outputImage, color, flags);
		Imgcodecs.imwrite("D:\\Dropbox\\8th semester Java\\IrisDetection\\output\\" + Math.random() + ".jpg",
				outputImage);
		for (KeyPoint keyPoint : array) {
			System.out.println(keyPoint);
		}
		System.out.println(array.length);
	}

	private Mat clahe(Mat image, int ClipLimit, Size size) {
		CLAHE clahe = Imgproc.createCLAHE();
		clahe.setClipLimit(ClipLimit);
		clahe.setTilesGridSize(size);
		Mat dest_image = new Mat();
		clahe.apply(image, image);
		return image;
	}

	private static Mat unsharpMask(Mat input_image, Size size, double sigma) {

		// Make sure the {input_image} is gray.

		Mat sharpend = new Mat(input_image.rows(), input_image.cols(), input_image.type());
		Mat Blurred_image = new Mat(input_image.rows(), input_image.cols(), input_image.type());
		Imgproc.GaussianBlur(input_image, Blurred_image, size, sigma);

		Core.addWeighted(input_image, 2.0D, Blurred_image, -1.0D, 0.0D, sharpend);
		return sharpend;
	}
}
