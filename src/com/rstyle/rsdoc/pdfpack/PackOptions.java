package com.rstyle.rsdoc.pdfpack;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.rstyle.rsdoc.document.process.ScaleAlgo;

@XmlRootElement(namespace="http://www.redsys.ru/rsdoc/pdf/config/1.0/")
public class PackOptions {

    private static final String CONFIG_FILE = "pdfpack.cfg";    
    
    private static final String QUERY_FILE = "files.sql";

    @XmlElement(name="ignore",nillable=true,required=false,defaultValue="10",namespace="http://www.redsys.ru/rsdoc/pdf/config/1.0/")
	private int ignoreScale;
	
	@XmlElement(name="dpi",nillable=true,required=false,defaultValue="150",namespace="http://www.redsys.ru/rsdoc/pdf/config/1.0/")
	private int scaleDPI;
	
	@XmlElement(name="algo",nillable=true,required=false,defaultValue="SMOOTH",namespace="http://www.redsys.ru/rsdoc/pdf/config/1.0/")
	private ScaleAlgo scaleAlgo;
	
	@XmlElement(name="minfile",nillable=true,required=false,defaultValue="4096",namespace="http://www.redsys.ru/rsdoc/pdf/config/1.0/")
	private int minFileSize;
	
	@XmlElement(name="minpic",nillable=true,required=false,defaultValue="14",namespace="http://www.redsys.ru/rsdoc/pdf/config/1.0/")
	private int minPicSize;
	
	@XmlElement(name="tmp",nillable=false,required=true,defaultValue="./",namespace="http://www.redsys.ru/rsdoc/pdf/config/1.0/")
	private String tmpDir;
	
	@XmlElement(name="db",nillable=false,required=true,namespace="http://www.redsys.ru/rsdoc/pdf/config/1.0/")
	private String DBURL;
	
	@XmlElement(name="user",nillable=false,required=true,namespace="http://www.redsys.ru/rsdoc/pdf/config/1.0/")
	private String dbUser;
	
	@XmlElement(name="pass",nillable=false,required=true,namespace="http://www.redsys.ru/rsdoc/pdf/config/1.0/")
	private String dbPass;
	
	@XmlElement(name="docfile",nillable=false,required=true,defaultValue="documents.txt",namespace="http://www.redsys.ru/rsdoc/pdf/config/1.0/")
	private String docFile;

	@XmlElement(name="docsrc",nillable=false,required=true,defaultValue="db",namespace="http://www.redsys.ru/rsdoc/pdf/config/1.0/")
	private String docSrc;

	@XmlElement(name="backup",nillable=false,required=true,defaultValue="./backup",namespace="http://www.redsys.ru/rsdoc/pdf/config/1.0/")
	private String backupDir;

	@XmlElement(name="stoponerror",nillable=false,required=true,defaultValue="true",namespace="http://www.redsys.ru/rsdoc/pdf/config/1.0/")
	private String stopOnError;

	@XmlElement(name="changedb",nillable=false,required=true,defaultValue="false",namespace="http://www.redsys.ru/rsdoc/pdf/config/1.0/")
	private String changeDB;


	@XmlTransient()
	public int getIgnoreScale() {
		return ignoreScale;
	}

	public void setIgnoreScale(int ignoreScale) {
		this.ignoreScale = ignoreScale;
	}
	
    @XmlTransient()
	public int getScaleDPI() {
		return scaleDPI;
	}

	public void setScaleDPI(int scaleDPI) {
		this.scaleDPI = scaleDPI;
	}

    @XmlTransient()
	public ScaleAlgo getScaleAlgo() {
		return scaleAlgo;
	}

	public void setScaleAlgo(ScaleAlgo scaleAlgo) {
		this.scaleAlgo = scaleAlgo;
	}
	
    @XmlTransient()
	public int getMinFileSize() {
		return minFileSize;
	}

	public void setMinFileSize(int minFileSize) {
		this.minFileSize = minFileSize;
	}

    @XmlTransient()
	public int getMinPicSize() {
		return minPicSize;
	}

	public void setMinPicSize(int minPicSize) {
		this.minPicSize = minPicSize;
	}
	
	@XmlTransient()
	public String getTmpDir() {
		return tmpDir;
	}

	public void setTmpDir(String tmpDir) {
		this.tmpDir = tmpDir;
	}

    @XmlTransient()
	public String getDBURL() {
		return DBURL;
	}

	public void setDBURL(String dBURL) {
		DBURL = dBURL;
	}

    @XmlTransient()
	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

    @XmlTransient()
	public String getDbPass() {
		return dbPass;
	}

	public void setDbPass(String dbPass) {
		this.dbPass = dbPass;
	}

	@XmlTransient()
	public String getDocFile() {
		return docFile;
	}

	public void setDocFile(String docFile) {
		this.docFile = docFile;
	}
	
	@XmlTransient()
	public String getDocSrc() {
		return docSrc;
	}

	public void setDocSrc(String docSrc) {
		this.docSrc = docSrc;
	}
	
	
	@XmlTransient()
	public String getBackupDir() {
		return backupDir;
	}
	
	
	@XmlTransient()
	public boolean getStopOnError() {
		return stopOnError == null ? false : stopOnError.trim().equalsIgnoreCase("true");
	}

	
	@XmlTransient()
	public boolean getChangeDB() {
		return changeDB == null ? false : changeDB.trim().equalsIgnoreCase("true");
	}

	@Override
	public String toString() {
		return "PackOptions [\n ignoreScale=" + ignoreScale + "\n scaleDPI=" + scaleDPI + "\n scaleAlgo=" + scaleAlgo
				+ "\n minFileSize=" + minFileSize + "\n minPicSize=" + minPicSize + "\n tmp=" + tmpDir  
				+  "\n DB=" + DBURL +  "\n user=" + dbUser +  "\n docFile=" + docFile +	"\n docSrc= "+docSrc+"\n"
				+ "\n stopOnError=" + stopOnError + "\n changeDB=" + changeDB + "\n backupDIR=" + backupDir + "]";
	}

	public static void save(){
		
	 try {
		    PackOptions po = new PackOptions();		
		    po.setIgnoreScale(10);		
		    po.setScaleDPI(150);		
		    po.setScaleAlgo(ScaleAlgo.SMOOTH);
		    po.setMinFileSize(4096);		
		    po.setMinPicSize(14);
		    po.setTmpDir("./data/imgs/");
		    po.setDBURL("DB");
		    po.setDbUser("user");
		    po.setDbPass("pass");
		    po.setDocFile("documents.txt");
		    po.setDocSrc("db");
			File file = new File(CONFIG_FILE+".template");
			JAXBContext jaxbContext = JAXBContext.newInstance(po.getClass());
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller(); 
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(po, file);			
		} catch (Exception e) {
			System.out.println("Error creating config template - "+e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	public static PackOptions load() throws JAXBException {
		
		File file = new File(CONFIG_FILE);
		JAXBContext jaxbContext = JAXBContext.newInstance(PackOptions.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		return (PackOptions) jaxbUnmarshaller.unmarshal(file);
		
	}
	
	public List<String> docs() throws FileNotFoundException{
		
		List<String> res = new ArrayList<String>();
		
		Scanner s = null;
		
		try {

			s = new Scanner(new File(docFile));
			
			while (s.hasNext()){
			    res.add(s.next());
			}
			
		} finally {
			
			if (s != null) s.close();
			
		}
				
		return res;
		

	}
	
	public String query() throws FileNotFoundException{
		
		Scanner s = null;
		
		try {

			s = new Scanner(new File(QUERY_FILE));
			
			return s.useDelimiter("\\A").next();
			
		} finally {
			
			if (s != null) s.close();
			
		}
				
	}
}
