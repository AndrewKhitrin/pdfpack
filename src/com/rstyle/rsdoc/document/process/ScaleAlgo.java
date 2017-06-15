package com.rstyle.rsdoc.document.process;

import java.awt.Image;

public enum ScaleAlgo {
	
	DEFAULT, SMOOTH, AVERAGING, FAST, REPLICATE;
	
	public int getInt() {
		
		switch (this) {
		
			case DEFAULT:
				
				return Image.SCALE_DEFAULT;
	
			case SMOOTH:
				
				return Image.SCALE_SMOOTH;
	
			case AVERAGING:
				
				return Image.SCALE_AREA_AVERAGING;
	
			case FAST:
				
				return Image.SCALE_FAST;
	
			case REPLICATE:
				
				return Image.SCALE_REPLICATE;
	
				
			default:
				
				return Image.SCALE_DEFAULT;
				
		}
	}
}	

