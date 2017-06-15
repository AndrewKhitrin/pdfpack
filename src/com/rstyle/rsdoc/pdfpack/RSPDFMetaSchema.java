package com.rstyle.rsdoc.pdfpack;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.apache.jempbox.xmp.XMPMetadata;
import org.apache.jempbox.xmp.XMPSchema;
import org.w3c.dom.Element;

public class RSPDFMetaSchema extends XMPSchema{
	
	public static final String NAMESPACE = "http://www.redsys.ru/rsdoc/pdf/elements/1.0/";
	
	public RSPDFMetaSchema(XMPMetadata parent) {
	    super(parent, "rsdocpdf", NAMESPACE);
	}

	public RSPDFMetaSchema(Element element, String prefix) {
	    super(element, prefix);
	}
	
	public String getMetaDataType() {
	    return getTextProperty(prefix + ":metaDataType");
	}

	public void setMetaDataType(String metaDataType) {
	    setTextProperty(prefix + ":metaDataType", metaDataType);
	}
	
	public void setScaleProc(Boolean proc) {
		setBooleanProperty(prefix + ":scale", proc);
	}
	
	public boolean getScaleProc() {
		Boolean res = getBooleanProperty(prefix + ":scale");
		return res == null ? false : res;
	}

	public void removeStream(String stream) {
	    removeBagValue(prefix + ":stream", stream);
	}

	public void addStream(String stream) {
	    addBagValue(prefix + ":stream", stream);
	}

	public List<String> getStreams() {
	    return getBagList(prefix + ":stream");
	}
	
	public void setProcDate(Calendar date) {
		setDateProperty(prefix + ":scaledate", date);
	}
	
	public Calendar getProcDate() {
		Calendar res;
		try {
			res = getDateProperty(prefix + ":scaledate");
		} catch (IOException e) {
			return  Calendar.getInstance();
		}
		return res == null ? Calendar.getInstance() : res;
	}


	public void setDuration(Integer duration) {
		setIntegerProperty(prefix + ":duration", duration);
	}
	
	public Integer getDuration() {
		Integer res = getIntegerProperty(prefix + ":duration");
		return res == null ? 0 : res;
	}


	public void setVersion(Integer version) {
		setIntegerProperty(prefix + ":version", version);
	}
	
	public Integer getVersion() {
		Integer res = getIntegerProperty(prefix + ":version");
		return res == null ? 0 : res;
	}


	public void setAlgo(String algo) {
		setTextProperty(prefix + ":algo", algo);
	}
	
	public String getAlgo() {
		String res = getTextProperty(prefix + ":algo");
		return res == null ? "" : res;
	}


	public void setDPI(Integer dpi) {
		setIntegerProperty(prefix + ":dpi", dpi);
	}
	
	public Integer getDPI() {
		Integer res = getIntegerProperty(prefix + ":dpi");
		return res == null ? 0 : res;
	}


	public void setOldSize(Integer oldSize) {
		setIntegerProperty(prefix + ":oldsize", oldSize);
	}
	
	public Integer getOldSize() {
		Integer res = getIntegerProperty(prefix + ":oldsize");
		return res == null ? 0 : res;
	}


	public void setNewSize(Integer newSize) {
		setIntegerProperty(prefix + ":newsize", newSize);
	}
	
	public Integer getNewSize() {
		Integer res = getIntegerProperty(prefix + ":newsize");
		return res == null ? 0 : res;
	}

	
	public void setIgnoreScale(Integer ignoreScale) {
		setIntegerProperty(prefix + ":ignorescale", ignoreScale);
	}
	
	public Integer getIgnoreScale() {
		Integer res = getIntegerProperty(prefix + ":ignorescale");
		return res == null ? 0 : res;
	}

	public void setMinFileSize(Integer minFileSize) {
		setIntegerProperty(prefix + ":minfilesize", minFileSize);
	}
	
	public Integer getMinFileSize() {
		Integer res = getIntegerProperty(prefix + ":minfilesize");
		return res == null ? 0 : res;
	}

	public void setMinPicSize(Integer minPicSize) {
		setIntegerProperty(prefix + ":minpicsize", minPicSize);
	}
	
	public Integer getMinPicSize() {
		Integer res = getIntegerProperty(prefix + ":minpicsize");
		return res == null ? 0 : res;
	}

}
