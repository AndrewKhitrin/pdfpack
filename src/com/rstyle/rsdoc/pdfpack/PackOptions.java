package com.rstyle.rsdoc.pdfpack;

import com.rstyle.rsdoc.document.process.ScaleAlgo;

public class PackOptions {

	private int ignoreScale;
	
	private int scaleDPI;
	
	private ScaleAlgo scaleAlgo;
	
	private int minFileSize;
	
	private int minPicSize;
	
	public int getIgnoreScale() {
		return ignoreScale;
	}

	public void setIgnoreScale(int ignoreScale) {
		this.ignoreScale = ignoreScale;
	}

	public int getScaleDPI() {
		return scaleDPI;
	}

	public void setScaleDPI(int scaleDPI) {
		this.scaleDPI = scaleDPI;
	}

	public ScaleAlgo getScaleAlgo() {
		return scaleAlgo;
	}

	public void setScaleAlgo(ScaleAlgo scaleAlgo) {
		this.scaleAlgo = scaleAlgo;
	}

	public int getMinFileSize() {
		return minFileSize;
	}

	public void setMinFileSize(int minFileSize) {
		this.minFileSize = minFileSize;
	}

	public int getMinPicSize() {
		return minPicSize;
	}

	public void setMinPicSize(int minPicSize) {
		this.minPicSize = minPicSize;
	}

	
}
