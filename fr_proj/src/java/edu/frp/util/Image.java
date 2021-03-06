package edu.frp.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import edu.frp.exception.UnsupportedImageTypeException;

/**
 * @author Saulo (scsm@ecomp.poli.br)
 * 
 */
public class Image {
	
	private static Logger logger = Logger.getLogger(Image.class.getName());

	public static String JPEG = "jpg";
	public static String BMP = "bmp";
	public static String GIF = "gif";
	public static String PMG = "pgm";

	public static int RED_LAYER = 0;
	public static int BLUE_LAYER = 1;
	public static int GREEN_LAYER = 2;
	
	private double[][][] matrix;
	private int width = 0;
	private int height = 0;
	private int type = 0;
	
	
	/**
	 * Class constructor
	 * @param path of the image file
	 * @throws IOException
	 * @throws UnsupportedImageTypeException
	 */
	public Image (String path) throws IOException {
			try {
				validateImagePath(path);
				File imageFile = new File(path);
				BufferedImage bi = ImageIO.read(imageFile);
				
				this.width = bi.getWidth();
				this.height = bi.getHeight();
				this.type = bi.getType();
				this.fillImageMatrix(bi);
				
			} catch (UnsupportedImageTypeException e) {
				logger.severe(e.getMessage());
				e.printStackTrace();
			}
	}
	
	/**
	 * Image from BufferedImage
	 * @param bi
	 * @throws IOException
	 */
	public Image (BufferedImage bi) {
			this.width = bi.getWidth();
			this.height = bi.getHeight();
			this.type = bi.getType();
			this.fillImageMatrix(bi);
	}

	/**
	 * Checks if the given path points to a valid image file
	 * @param path to be validated
	 * @throws UnsupportedImageTypeException
	 */
	private void validateImagePath(String path) throws UnsupportedImageTypeException {
		boolean result = path.endsWith("." + BMP)
						|| path.endsWith("." + JPEG)
						|| path.endsWith("." + GIF);
		
		if (result) {
			return;
		} else if (path.endsWith("." + PMG)) {
			
		}
		
		if (!result) {
			String msg = "Unsupported image type/extension: " + path.substring(path.lastIndexOf("."), path.length());
			throw new UnsupportedImageTypeException(msg);
		}
	}

	/**
	 * Fill the pixels matrix with data from given BufferedImage
	 * @param bi BufferedImage that contains image data
	 */
	private void fillImageMatrix(BufferedImage bi) {
		int rgb = 0;
		
		this.matrix = new double[this.height][this.width][3];
		
		for (int w = 0; w < this.width; w++) {
			for(int h = 0; h < this.height; h++) {
				rgb = bi.getRGB(w, h);
				
				this.matrix[h][w][0] = (double)((rgb >> 16) & 0xff)/255;
				this.matrix[h][w][1] = (double)((rgb >>  8) & 0xff)/255;
				this.matrix[h][w][2] = (double)((rgb      ) & 0xff)/255;
			}
		}
	}
	
	/**
	 * Writes the image to specified path
	 * @param path of the file where the image will be stored
	 * @param ext extension of the image
	 * @throws UnsupportedImageTypeException
	 */
	public void write(String path, String ext) throws UnsupportedImageTypeException {
		BufferedImage bi = this.getBufferedImage();
		logger.info("Writing image file: "+ path);
		try {
			ImageIO.write(bi, ext, new File(path));
		} catch (IOException e) {
			logger.severe("Failed to write image file.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the BufferedImage correspondent
	 * @return BufferedImage
	 */
	public BufferedImage getBufferedImage() {
		BufferedImage bi = new BufferedImage(this.width, this.height, this.type);
		int rgb,red,green,blue = 0;
		for (int w = 0; w < this.width; w++) {
			for (int h = 0; h < this.height; h++) {
				red = (int)Math.floor((this.matrix[h][w][0])*255);
				green = (int)Math.floor((this.matrix[h][w][1])*255);
				blue = (int)Math.floor((this.matrix[h][w][2])*255);
				rgb =  (red << 16) | (green << 8) | blue;
				bi.setRGB(w, h, rgb);
			}
		}
		return bi;
	}
	
	/**
	 * Returns the matrix containing pixel values of the requested layer. Use:
	 * <i>
	 * <li>Image.RED_LAYER
	 * <li>Image.BLUE_LAYER
	 * <li>Image.GREEN_LAYER
	 * </i>
	 * @param layer code
	 * @return double[][] containing pixel values
	 */
	public double[][] getLayerMatrix(int layer) {
		double[][] resultMatrix = new double[this.height][this.width];
		
		for (int w = 0; w < this.width; w++) {
			for (int h = 0; h < this.height; h++) {
				resultMatrix[h][w] = this.matrix[h][w][layer];
			}
		}
		return resultMatrix;
	}
	
	/**
	 * Test if image is colored based in the BufferedImage.getType()
	 * @return true if colored, false if grayscale
	 */
	public boolean isColored() {
		return (this.type < 10)&&(this.type!=0);
	}
	
	/**
	 * Dimensions: [Height][Width][Layer]
	 * @return the matrix with pixel map
	 */
	public double[][][] getMatrix() {
		return matrix;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return the type
	 * @see BufferedImage
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param matrix the matrix to set
	 */
	public void setMatrix(double[][][] matrix) {
		this.matrix = matrix;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}
	
}
