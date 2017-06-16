package com.rstyle.rsdoc.pdfpack;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.jempbox.xmp.XMPMetadata;
import org.apache.log4j.Logger;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.rstyle.rsdoc.document.process.ScaleAlgo;
import com.rstyle.rsdoc.pdfpack.ImageLocator.ImageLocation;

public class PDFPack {
	
	private static final int MAX_PAGES = 1000;
	
	private static final int MAX_THREADS = 4;
	
	private static final long MAX_TIMEOUT = 12 * 60 * 60;
	
	private static final String SCALED_POSTFIX = "_scaled";
	
	private static final int VERSION = 1;
	
	private static final Logger log = Logger.getLogger("pdfpack");
	
	private static PackOptions getDefaultPackOptions(){
		
		PackOptions po = new PackOptions();
		
		po.setIgnoreScale(10);
		
		po.setScaleDPI(150);
		
		po.setScaleAlgo(ScaleAlgo.SMOOTH);
		
		po.setMinFileSize(4096);
		
		po.setMinPicSize(14);
		
		return po;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException, COSVisitorException, ParserConfigurationException, SAXException, TransformerException {
		
		log.info("PDF pack Started.");
		
		final Path tmp = Paths.get("./data/","/imgs/");
		
		 PackOptions po = getDefaultPackOptions();
		 
		 log.info(po.toString());
		 
		 log.info("Temp path " + tmp.toString());
		
		 boolean packResult = pack(new FileInputStream("./data/test.pdf"),new FileOutputStream("./data/test_scaled1.pdf"),tmp,po);
		 
		 if (!packResult) {
			 log.info("SKIPPED");
		 }
		
		 log.info("PDF pack Finished.");
	}

	public static void clearTemp(final Path tmpPath,Map<Integer,Map<String,PDFPackImage>> imgs) {
	
        for(Map<String,PDFPackImage> iMap: imgs.values()) {
        	  
        	  for(PDFPackImage i : iMap.values()) {

        		Paths.get(tmpPath.toString(), i.getId()).toFile().delete();
                
                if (i.isScaled()) {
              	  
                	Paths.get(tmpPath.toString(), i.getId() + SCALED_POSTFIX).toFile().delete();
              	  
                }
                
        	  }
        	  
          }
	}
	
	public static boolean pack(InputStream inPDF,OutputStream outPDF,final Path tmpPath,final PackOptions po) throws IOException, InterruptedException, COSVisitorException, ParserConfigurationException, SAXException, TransformerException {
		
		  long startTime = System.nanoTime();
		  
          Files.createDirectories(tmpPath);
          
          PDDocument document = PDDocument.load(inPDF);
          
          RSPDFMetaSchema meta = readMetadata(document);
          
          if (meta != null) {
              if (meta.getScaleProc()) {
            	  log.info(String.format("File already packed to DPI %d at %s ratio -  %.2f processed stream %d",
            			  meta.getDPI(),
            			  meta.getProcDate().toString(),
            			  (float) meta.getOldSize() / (float) meta.getNewSize(),
            			  meta.getStreams().size()
            			  ));
            	  return false;
              }        	  
          }
          
          log.info("Extractiong images");
		
          Map<Integer,Map<String,PDFPackImage>> imgs =  extractImg(document,tmpPath);
          
          int pCount = 0;
          int iCount = 0;
          long imageSize = 0;
          
         for(Map<String,PDFPackImage> iMap: imgs.values()) {
        	 
        	  pCount++;  
        	 
        	  for(PDFPackImage i : iMap.values()) {
        		  
        		  iCount++;
        		  
        		  imageSize += i.getOldSize();
        		  
        	  }
         }
          
         log.info(String.format("%d pages proceed %d images found", pCount,iCount));
          
          ExecutorService taskExecutor = Executors.newFixedThreadPool(MAX_THREADS);
          
          log.info("Rescale images.");
          
          for(Map<String,PDFPackImage> iMap: imgs.values()) {
        	  
        	  for(PDFPackImage i : iMap.values()) {
        		  
            	  taskExecutor.submit(new Runnable() {
      				
      				@Override
      				public void run() {
      					try {      						
      						scale(i,po,tmpPath);
      						log.info(i.info());
      					} catch (Exception e) {
      						
      						i.setError(e.getMessage());
      						i.setScaled(false);
      						log.error(String.format("Error processing image %s on page %d",i.getName(),i.getPage()), e);
      					}
      					
      				}
      			});
            	  
        	  }

          }
          
          taskExecutor.shutdown();
          taskExecutor.awaitTermination(MAX_TIMEOUT, TimeUnit.SECONDS);
          log.info("Assembling PDF file.");
          create(document,imgs,tmpPath);
          
          int scaledImages = addMetadata(document, imgs, po, startTime);
          log.info(String.format("%d total images rescaled",scaledImages));
          if (scaledImages > 0) {
           document.save(outPDF);
           outPDF.flush();
          }
          
          log.info("Removing temporary file(s).");
          clearTemp(tmpPath, imgs);
          
          return scaledImages > 0;
	}
	
	public static void create(PDDocument document,Map<Integer,Map<String,PDFPackImage>> imgs,Path tmpPath) throws IOException{
		
		HashSet<Integer> procPages = new HashSet<Integer>(10); 
		
		List<PDPage> list = document.getDocumentCatalog().getAllPages();
			
	    for (PDPage page : list) {
	        	
	        	Map<String,PDFPackImage> imagePage = imgs.get(page.getStructParents());
	        	
	        	if (imagePage == null) continue;
	        	
	            ImageLocator locator = new ImageLocator();
	            
	            locator.processStream(page, page.findResources(), page.getContents().getStream());
	           
		   		    for (ImageLocation location : locator.getLocations())
		   		    {
		   		        
		   		    	PDFPackImage img = imagePage.get(location.getName());
		   		    	
		   		    	if (img == null) continue;
		   		    	
		   		    	if (! img.isScaled()) continue;
		   		    	
		   		        PDXObjectImage newImage = new PDJpeg(document, new FileInputStream(Paths.get(tmpPath.toString(), img.getId() + SCALED_POSTFIX).toFile()));
		   		        
	     			    try {
	     				
	     			    	location.getImage().getCOSStream().replaceWithStream(newImage.getCOSStream());
	     			    	log.error(String.format("Replace image %s on page %d size - %s new size - %s",
	     			    			img.getName(),
	     			    			img.getPage(),
	     			    			PDFPackImage.hrSize(img.getOldSize()),
	     			    			PDFPackImage.hrSize(img.getNewSize())
	     			    			));
	     				
						} catch (Exception e) {
							
							img.setScaled(false);
							img.setError(e.getMessage());
							log.error(String.format("Error replacing image %s on page %d",img.getName(),img.getPage()), e);
						}
	     			
		   		    }
	
	            }

	}

	public static RSPDFMetaSchema readMetadata(PDDocument document) {
		
		try {
			PDDocumentCatalog catalog = document.getDocumentCatalog();
			PDMetadata  metadata = catalog.getMetadata();
			InputStream xmpIn = metadata.createInputStream();
			DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = f.newDocumentBuilder();
	        Document xmpDoc = builder.parse(xmpIn);
	        RSPDFMetaMetadata metadataRS = new RSPDFMetaMetadata(xmpDoc);
			return metadataRS.getRSPDFSchema();			
		} catch (Exception e) {
			log.info("Unable to read metadata");
			return null;
		}
		
	}
	

	
	public static int addMetadata(PDDocument document,Map<Integer,Map<String,PDFPackImage>> imgs,final PackOptions po,long startTime) throws IOException, ParserConfigurationException, SAXException, TransformerException {
		
		PDDocumentCatalog catalog = document.getDocumentCatalog();
		
		PDMetadata metadataStream = catalog.getMetadata();
		if (metadataStream == null) {
			metadataStream = new PDMetadata(document);			
			metadataStream.importXMPMetadata(new XMPMetadata());
			catalog.setMetadata(metadataStream);
		}
		InputStream xmpIn = metadataStream.createInputStream();

        
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setExpandEntityReferences(true);
        f.setIgnoringComments(true);
        f.setIgnoringElementContentWhitespace(true);
        f.setValidating(false);
        f.setCoalescing(true);
        f.setNamespaceAware(true);
        DocumentBuilder builder = f.newDocumentBuilder();
        Document xmpDoc = builder.parse(xmpIn);

        
		RSPDFMetaMetadata metadata = new RSPDFMetaMetadata(xmpDoc); 


		RSPDFMetaSchema pdfSchema = metadata.addRSPDFMetaSchema();
		
		int oldSize = 0;
		int newSize = 0;
		int imgCount = 0;
		
        for(Map<String,PDFPackImage> iMap: imgs.values()) {
      	  
      	  for(PDFPackImage i : iMap.values()) {

              pdfSchema.addStream(i.info());
              
              if (i.isScaled()) {
            	  
            	  oldSize += i.getOldSize();
            	  newSize += i.getNewSize();
            	  imgCount++;
            	  
              }
              
      	  }
      	  
        }
		
		
		pdfSchema.setScaleProc(true);
		pdfSchema.setProcDate(Calendar.getInstance());
		pdfSchema.setDuration((int) (System.nanoTime() - startTime));
		pdfSchema.setVersion(VERSION);
		pdfSchema.setAlgo(po.getScaleAlgo().toString());
		pdfSchema.setDPI(po.getScaleDPI());
		pdfSchema.setOldSize(oldSize);
		pdfSchema.setNewSize(newSize);
		pdfSchema.setIgnoreScale(po.getIgnoreScale());
		pdfSchema.setMinFileSize(po.getMinFileSize());
		pdfSchema.setMinPicSize(po.getMinPicSize());
		
        metadataStream.importXMPMetadata(metadata);

        
        catalog.setMetadata(metadataStream);
        
        return imgCount;
		
	}
	
	private static void scale(PDFPackImage image,PackOptions po,Path tmp) throws IOException {
	
		if (!image.isSaved()) {
			image.setScaled(false);
			return;
		}
		
		if (image.getPdfWidth() < po.getMinPicSize() ) {
			 image.setError(String.format("No need to change size (width = %d)",image.getPdfWidth()));
			 image.setScaled(false);
			 log.error(String.format("Image %s on page %d No need to change size (width = %d)",
					 image.getName(),image.getPage(), image.getPdfWidth()));
			 return;
		}
		
		if (image.getPdfHeight() < po.getMinPicSize() ) {
			 image.setError(String.format("No need to change size (width = %d)",image.getPdfHeight()));
			 image.setScaled(false);
			 log.error(String.format("Image %s on page %d No need to change size (width = %d)",
					 image.getName(),image.getPage(),image.getPdfHeight()));
			 return;
		}
		
		float xAspect = (float) po.getScaleDPI() / (float) image.getDPIX();
		 
		float yAspect = (float) po.getScaleDPI() / (float) image.getDPIY();
		
		float ratio = xAspect > yAspect ? yAspect : xAspect;
		
		if (ratio >= 1) {
			
			 image.setError(String.format("No need to pack (ratio = %.2f)", ratio));
			 image.setScaled(false);
			 log.error(String.format("Image %s on page %d No need to pack (ratio = %.2f)",
					 image.getName(),image.getPage(),ratio));
			 return;
		}
		
		if (ratio < po.getIgnoreScale() / 1000) {
			
			 image.setError(String.format("No need to change size (ratio = %.2f)",ratio));
			 image.setScaled(false);
			 log.error(String.format("Image %s on page %d No need to change size (ratio = %.2f)",
					 image.getName(),image.getPage(),ratio));
			 return;
			 
		}
		
		File f = Paths.get(tmp.toString(), image.getId()).toFile();
		
		image.setOldSize(f.length());
		
		if (f.length() < po.getMinFileSize()) {
			
			 image.setError(String.format("No need to change size (size = %d)",f.length()));			 
			 image.setScaled(false);
			 log.error(String.format("Image %s on page %d No need to change size (size = %d)",
					 image.getName(),image.getPage(),f.length()));
			 return;
		}
		
		ImageInputStream input = ImageIO.createImageInputStream(f);
		
		Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
		
		BufferedImage img = null;
		
		while(readers.hasNext()) {
			
			 ImageReader reader = readers.next();
			 reader.setInput(input);
			 ImageReadParam param = reader.getDefaultReadParam();
			 reader.setInput(input, true, true);
	         img = reader.read(0,param);
	         if (img != null) {
	           image.setFmt(reader.getFormatName());
	           break;
	         }
		}
		
		
		 if (img == null) {			 
			 image.setError("Unknown image format.");
			 log.info(String.format("Image %s on page %d Unknown image format.",image.getName(),image.getPage()));
			 image.setScaled(false);
			 return;
		 }
		
		 
		int scaleX = (int) (img.getWidth() * ratio);
		int scaleY = (int) (img.getHeight() * ratio);
		
		image.setNewHeight(scaleX);
		image.setNewWidth(scaleY);
		
		java.awt.Image newImg = img.getScaledInstance(scaleX, scaleY, po.getScaleAlgo().getInt());
		
        BufferedImage bimage = new BufferedImage(newImg.getWidth(null), newImg.getHeight(null),img.getType() == BufferedImage.TYPE_CUSTOM ?  BufferedImage.TYPE_INT_RGB : img.getType());
        
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(newImg, 0, 0, null);
        bGr.dispose();
		
		File outFile = Paths.get(tmp.toString(), image.getId() + SCALED_POSTFIX).toFile();
		
		ImageIO.write(bimage, "jpeg", outFile);
		
		image.setNewSize(outFile.length());
		
		image.setScaled(true);
		
	}
	
	private static  Map<Integer,Map<String,PDFPackImage>> extractImg(PDDocument document,Path tmpPath) throws IOException {
		
		Map<Integer,Map<String,PDFPackImage>> imgRes = new HashMap<>(10);
		
		HashSet<Integer> procPages = new HashSet<Integer>(10); 
		
		long addedPages = 0;
		
		do {
			
			addedPages = 0;
			
			List<PDPage> list = document.getDocumentCatalog().getAllPages();
			
	        for (int pNo = 0;pNo < list.size();pNo++) {
	        	
	        	if (procPages.contains(pNo)) continue;
	        	
	        	if (addedPages > MAX_PAGES) {
	        		break;
	        	}
	        	
	        	PDPage page = list.get(pNo);
	        	
	        	Map<String,PDFPackImage> imagePage = null;
	        	
	        	if (!imgRes.containsKey(page.getStructParents())) {
	        		
	        		imagePage = new HashMap<String,PDFPackImage>(10);
	        		imgRes.put(page.getStructParents(), imagePage);
	        		
	        	} else {
	        		
	        		imagePage = imgRes.get(page.getStructParents());
	        	}
	        	
	            ImageLocator locator = new ImageLocator();
	            
	            locator.processStream(page, page.findResources(), page.getContents().getStream());
	           
		   		    for (ImageLocation location : locator.getLocations())
		   		    {
		   		        PDFPackImage img = new PDFPackImage(location.getName(),page.getStructParents());
		   		        
	   		        	img.setMatrix(location.getMatrix());
	   		        	img.setCropBox(location.getPage().findCropBox());
	
		   		        
		   		        Path imgFile = null;
	     			
	     			    try {
	     				
		     				imgFile = Paths.get(tmpPath.toString(), img.getId());
		     				
		     				location.getImage().write2file(imgFile.toFile());
		     				
		     				img.setPdfHeight(location.getImage().getHeight());
		     				
		     				img.setPdfWidth(location.getImage().getWidth());
		     				
		     				img.setSaved(true);
		     				
		     				log.info(String.format("Image %s on page %d save to %s, size %s", img.getName(),img.getPage(),img.getId(),PDFPackImage.hrSize(imgFile.toFile().length())));
		     				
	     				
						} catch (Exception e) {
							
							img.setSaved(false);
							img.setError(e.getMessage());
							log.error(String.format("Error saving %s on page %d SKIPPED", img.getName(),img.getPage(),e));
							
						}
	     			
	     			    imagePage.put(img.getName(),img);
	     			    
		   		    }
	
	
	           	  procPages.add(pNo);
	        	
	        	  addedPages++;
	
	            }
	            
	            System.gc();
        
		 }while(addedPages > 0);
		
		return imgRes;
		
	}
}
