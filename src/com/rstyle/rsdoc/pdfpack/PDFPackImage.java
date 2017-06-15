package com.rstyle.rsdoc.pdfpack;

import java.util.UUID;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.util.Matrix;

public class PDFPackImage implements Comparable<PDFPackImage>{
	
	private final String name;

	private final int page;
	
	private boolean isSaved;
	
	private boolean scaled;
	
	private final String id;
	
	private int pdfWidth = 0;
	
	private int pdfHeight = 0;
	
	private int awtWidth = 0;
	
	private int awtHeight = 0;

	private int newWidth = 0;
	
	private int newHeight = 0;
	
	private int awtType = 0;
	
	private String error;
	
	PDRectangle cropBox;
	
	Matrix matrix;
	
	private long oldSize;
	
	private long newSize;
	
	private String fmt;
	
	public static String hrSize(long bytes) {
	    int unit =1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    char pre = "KMGTPE".charAt(exp-1) ;
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	
	public PDFPackImage(String name, int page) {
		this.name = name;
		this.page = page;
		this.isSaved = false;
		this.id = UUID.randomUUID().toString();
	}

	
	public String info(){
		
		StringBuilder sb = new StringBuilder();
		
		if (!scaled) {
			
			sb.append("Not scaled, REASON - ").append(error);
			
		} else {
			
			//String pct = ;
			
		   sb.append("Scaled ").append(String.format(java.util.Locale.US,"%.2f ", (float) oldSize / (float) newSize))
			.append("%, ").append(fmt).append(" ").append(pdfWidth).append("x").append(pdfHeight)
			.append(" DPI ").append(getDPIX()).append("x").append(getDPIY()).append(" ").append(hrSize(oldSize))
			.append("; AFTER ").append(newWidth).append("x").append(newHeight).append(" ").append(hrSize(newSize))
			.append("; Page ").append(getPage()).append(" Name `").append(name).append("`.");
		}
		
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return "PDFPackImage [name=" + name + ", page=" + page + ", isSaved=" + isSaved + ", id=" + id + ", pdfWidth="
				+ pdfWidth + ", pdfHeight=" + pdfHeight + ", awtWidth=" + awtWidth + ", awtHeight=" + awtHeight
				+ ", awtType=" + awtType + ", cropBox=" + cropBox + ", matrix=" + matrix + ", dpi (X) = " + getDPIX() 
				+ ", dpi (Y) = " + getDPIY() + "]";
	}



	public boolean isSaved() {
		return isSaved;
	}




	public void setSaved(boolean isSaved) {
		this.isSaved = isSaved;
	}

	public String getName() {
		return name;
	}

	public int getPage() {
		return page;
	}

	public String getId() {
		return id;
	}

	public int getPdfWidth() {
		return pdfWidth;
	}

	public void setPdfWidth(int pdfWidth) {
		this.pdfWidth = pdfWidth;
	}

	public int getPdfHeight() {
		return pdfHeight;
	}

	public void setPdfHeight(int pdfHeight) {
		this.pdfHeight = pdfHeight;
	}

	public int getAwtWidth() {
		return awtWidth;
	}

	public void setAwtWidth(int awtWidth) {
		this.awtWidth = awtWidth;
	}

	public int getAwtHeight() {
		return awtHeight;
	}

	public void setAwtHeight(int awtHeight) {
		this.awtHeight = awtHeight;
	}

	public int getAwtType() {
		return awtType;
	}

	public void setAwtType(int awtType) {
		this.awtType = awtType;
	}



	@Override
	public int compareTo(PDFPackImage o) {
		int pageCmp = this.page - o.page;
		if (pageCmp == 0) {
			return this.name.compareTo(o.getName());	
		}
		return pageCmp;
	}



	@Override
	public int hashCode() {
		return (String.valueOf(page) + name).hashCode();
	}



	public PDRectangle getCropBox() {
		return cropBox;
	}



	public void setCropBox(PDRectangle cropBox) {
		this.cropBox = cropBox;
	}



	public Matrix getMatrix() {
		return matrix;
	}



	public void setMatrix(Matrix matrix) {
		this.matrix = matrix;
	}
	
	public long getDPIX(){
		return Math.round(pdfWidth / (matrix.getXScale() / 72));
	}

	public long getDPIY(){
		return Math.round(pdfHeight / (matrix.getYScale() / 72));
	}



	public String getError() {
		return error;
	}



	public void setError(String error) {
		this.error = error;
	}



	public boolean isScaled() {
		return scaled;
	}



	public void setScaled(boolean scaled) {
		this.scaled = scaled;
	}



	public long getOldSize() {
		return oldSize;
	}



	public void setOldSize(long oldSize) {
		this.oldSize = oldSize;
	}



	public long getNewSize() {
		return newSize;
	}



	public void setNewSize(long newSize) {
		this.newSize = newSize;
	}



	public String getFmt() {
		return fmt;
	}



	public void setFmt(String fmt) {
		this.fmt = fmt;
	}



	public int getNewWidth() {
		return newWidth;
	}



	public void setNewWidth(int newWidth) {
		this.newWidth = newWidth;
	}



	public int getNewHeight() {
		return newHeight;
	}



	public void setNewHeight(int newHeight) {
		this.newHeight = newHeight;
	}
    
	
        
}
