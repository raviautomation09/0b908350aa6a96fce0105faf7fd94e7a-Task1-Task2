package com.maf.web;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
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

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLObjectRepository_Web {
	public static Logger ORLogger = Logger.getLogger("devpinoyLogger");
	
	
	public static HashMap<String, String> getXMLNodeTextByXpath(Document xmlData, String xPath) throws Exception {
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
		    	 //System.out.println(xmlNode.hasChildNodes());
		    	 if (xmlNode.hasChildNodes()) {
		    		 NodeList x = xmlNode.getChildNodes();
		    		 //System.out.println(x.getLength());
					for (int k = 0; k < x.getLength(); k++) {
						String childNodeName = x.item(k).getNodeName();
						String childNodeText = x.item(k).getTextContent();
						if (!childNodeName.equalsIgnoreCase("#text")) {
							//System.out.println(childNodeName + ":" + childNodeText);
							orRepo.put(childNodeName, childNodeText);
							//System.out.println(orRepo);
						}
						
					}
				}else {
					throw new Exception("Please specify the locator for element");
				}
		    	 
			}else{
				NodeText="";
			}
  
		} catch (XPathExpressionException e) {
			System.out.println(e.getMessage());
		}
		return orRepo;
	}
	
	public static Document loadXMLFromString(String xml)  throws ParserConfigurationException, IOException, SAXException, TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError
	{
		//System.out.println("Before Parsing:"+xml);
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();		
		InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		Document xmlString = db.parse(is);
		
	    //System.out.println("After Parsing:"+printRequest(xmlString));
	    return xmlString;
	    		
	}
	
	public static By getLocators(Document dc , String LocatorName) throws Exception {
		
		HashMap<String, String> orObject= getXMLNodeTextByXpath(dc, "//Element[@name='"+LocatorName.trim()+"']");
		
		By locator = null;
		Set<String> x = orObject.keySet();
		
		for (Iterator<String> iterator = x.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			
			switch (string) {
			case "xpath":
				locator= By.xpath(orObject.get(string));
				ORLogger.info("Locator Value :"+locator);
				break;
			case "id":
				locator= By.id(orObject.get(string));
				ORLogger.info("Locator Value :"+locator);
				break;
			
			case "name":
				locator= By.name(orObject.get(string));
				ORLogger.info("Locator Value :"+locator);
				break;
			
			case "linktext":
				locator= By.linkText(orObject.get(string));
				ORLogger.info("Locator Value :"+locator);
				break;
			
			case "css":
				locator= By.cssSelector(orObject.get(string));
				ORLogger.info("Locator Value :"+locator);
				break;
			case "classname":
				locator= By.className(orObject.get(string));
				ORLogger.info("Locator Value :"+locator);
				break;
			case "partiallinktext":
				locator= By.partialLinkText(orObject.get(string));
				ORLogger.info("Locator Value :"+locator);
				break;
			case "tagname":
				locator= By.tagName(orObject.get(string));
				ORLogger.info("Locator Value :"+locator);
				break;
				
			default:
				break;
			}
			
		}
		return locator;
	}
	
	public static void main(String[] args) throws TransformerFactoryConfigurationError, Exception {
		
		File in = new File("C:\\Users\\H124795\\Desktop\\WaveCrest_ConsumerPortal_MyChoice_OR.xml");
		InputStream is= new FileInputStream(in);
		String ObjRepoObj=IOUtils.toString(is);
		//System.out.println(ObjRepoObj);
		Document dc= loadXMLFromString(ObjRepoObj);
		
		//System.out.println(getXMLNodeTextByXpath(dc, "//Element[@name='btnLoginViaUser']"));
		System.out.println(getLocators(dc, "btnLoginViaUser"));
		is.close();
		
		
		
	}
	
}
