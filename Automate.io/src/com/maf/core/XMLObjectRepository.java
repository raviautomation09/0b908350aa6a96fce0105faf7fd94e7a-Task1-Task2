package com.maf.core;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy.ByAndroidUIAutomator;
import io.appium.java_client.MobileElement;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@SuppressWarnings("unused")
public class XMLObjectRepository {
	public AppiumDriver<?> driver;
	static Logger logger = Logger.getLogger("devpinoyLogger");
	
	
	public XMLObjectRepository(AppiumDriver<?> driver) {
		this.driver = driver;
	}
	
	synchronized public static HashMap<String, String> getXMLNodeTextByXpath(Document xmlData, String xPath) throws Exception {
		HashMap<String, String> orRepo= new HashMap<String, String>();
		String NodeText=null;	 
		try {
					 
			 XPathFactory xfactory = XPathFactory.newInstance();
			 XPath xpath = xfactory.newXPath();				 
		     XPathExpression expr = xpath.compile(xPath);
		     
		     Object result = expr.evaluate(xmlData, XPathConstants.NODE);
		     Node xmlNode = (Node) result;
		     if (result!=null) {
		    	 //NodeText=xmlNode.getTextContent();
		    	 //logger.info(xmlNode.hasChildNodes());
		    	 if (xmlNode.hasChildNodes()) {
		    		 NodeList x = xmlNode.getChildNodes();
		    		 //logger.info(x.getLength());
					for (int k = 0; k < x.getLength(); k++) {
						String childNodeName = x.item(k).getNodeName();
						String childNodeText = x.item(k).getTextContent();
						if (!childNodeName.equalsIgnoreCase("#text")) {
							//logger.info(childNodeName + ":" + childNodeText);
							orRepo.put(childNodeName, childNodeText);
							//logger.info(orRepo);
						}
						
					}
				}else {
					throw new Exception("Please specify the locator for element");
				}
		    	 
			}else{
				NodeText="";
			}
  
		} catch (XPathExpressionException e) {
			logger.info(e.getMessage());
		}
		return orRepo;
	}
	
	synchronized public static Document loadXMLFromString(String xml)  throws ParserConfigurationException, IOException, SAXException, TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError
	{
		//logger.info("Before Parsing:"+xml);
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();		
		InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		Document xmlString = db.parse(is);
		
	    //logger.info("After Parsing:"+printRequest(xmlString));
	    return xmlString;
	    		
	}
	
	synchronized public MobileElement getLocators(Document dc , String LocatorName) throws Exception {
		
		HashMap<String, String> orObject= getXMLNodeTextByXpath(dc, "//Element[@name='"+LocatorName.trim()+"']");
		
		MobileElement locator = null;
		Set<String> x = orObject.keySet();
		
		for (Iterator<String> iterator = x.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			
			switch (string) {
			case "xpath":
				try {
					locator= (MobileElement) driver.findElement(ByAndroidUIAutomator.xpath(orObject.get(string)));
					logger.info("Locator Value :"+locator);
				} catch (Exception e) {
					//System.out.println(e);
				}
				
				break;
			case "id":
				try {
					locator= (MobileElement) driver.findElement(ByAndroidUIAutomator.id(orObject.get(string)));
					logger.info("Locator Value :"+locator);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				break;
			
			case "name":
				try {
					locator= (MobileElement) driver.findElement(ByAndroidUIAutomator.name(orObject.get(string)));
					logger.info("Locator Value :"+locator);
				} catch (Exception e) {
					//System.out.println(e);
				}
				
				break;
			
			case "linktext":
				try {
					locator= (MobileElement) driver.findElement(ByAndroidUIAutomator.linkText(orObject.get(string)));
					logger.info("Locator Value :"+locator);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				break;
			
			case "css":
				try {
					locator= (MobileElement) driver.findElement(ByAndroidUIAutomator.cssSelector(orObject.get(string)));
					logger.info("Locator Value :"+locator);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				break;
			case "classname":
				try {
					locator= (MobileElement) driver.findElement(ByAndroidUIAutomator.className(orObject.get(string)));
					logger.info("Locator Value :"+locator);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				break;
			case "partiallinktext":
				try {
					locator= (MobileElement) driver.findElement(ByAndroidUIAutomator.partialLinkText(orObject.get(string)));
					logger.info("Locator Value :"+locator);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				break;
			case "tagname":
				try {
					locator= (MobileElement) driver.findElement(ByAndroidUIAutomator.tagName(orObject.get(string)));
					logger.info("Locator Value :"+locator);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				break;
				
			default:
				break;
			}
			
		}
		return locator;
	}
	
/*	public static void main(String[] args) throws TransformerFactoryConfigurationError, Exception {
		
		File in = new File("C:\\Users\\H124795\\Desktop\\WaveCrest_ConsumerPortal_MyChoice_OR.xml");
		InputStream is= new FileInputStream(in);
		String ObjRepoObj=IOUtils.toString(is);
		//logger.info(ObjRepoObj);
		Document dc= loadXMLFromString(ObjRepoObj);
		
		//logger.info(getXMLNodeTextByXpath(dc, "//Element[@name='btnLoginViaUser']"));
		//logger.info(getLocators(dc, "btnLoginViaUser"));
		is.close();
		
		
		
	}*/
	
}
