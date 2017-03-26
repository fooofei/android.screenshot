package gdg.ninja.croplib;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class Crop{
	
	public static final int REQUEST_CROP = 6932;
	public static final int REQUEST_SHARE = 6933;

	static interface Extra{
		String MAX_X = "max_x";
		String MAX_Y = "max_y";
		String IMAGE_SOURCE = "image_source";
		String IMAGE_OUTPUT = "image_output";
		String OUTPUT_QUALITY = "output_quality";
	}
	
	private Intent cropIntent;
	
	/**
	 * Create a crop Intent builder with source image
	 * 
	 * @param source
	 *            Source image to crop
	 */
	public Crop(Uri source){
		cropIntent = new Intent();
		cropIntent.putExtra(Extra.IMAGE_SOURCE, source);
	}
	
	/**
	 * Set the output URI where the cropped image will be saved
	 * 
	 * @param output
	 *            Output image URI
	 * 
	 */
	public Crop output(Uri output){
		cropIntent.putExtra(Extra.IMAGE_OUTPUT, output);
		return this;
	}
	
	/**
	 * Set maximum crop size
	 * 
	 * @param width
	 *            Max width
	 * @param height
	 *            Max height
	 * 
	 */
	public Crop withMaxSize(int width, int height){
		cropIntent.putExtra(Extra.MAX_X, width);
		cropIntent.putExtra(Extra.MAX_Y, height);
		return this;
	}
	
	/**
	 * Set output quality
	 * 
	 * @param quality
	 *            Quality of the output image
	 * 
	 */
	public Crop quality(int quality){
		cropIntent.putExtra(Extra.OUTPUT_QUALITY, quality);
		return this;
	}
	
	/**
	 * Send crop Intent and start Crop Activity
	 * 
	 * @param acvitity
	 * 2014_07_21  hujianfei  change Activity to Context
	 */
	public void start(Context acvitity){
		cropIntent.setClass(acvitity, CropActivity.class);
		cropIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		acvitity.startActivity(cropIntent);//REQUEST_CROP
		
		
	}
}
