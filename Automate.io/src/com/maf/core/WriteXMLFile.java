package com.maf.core;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class WriteXMLFile {

	public static void main(String argv[]) {

	  try {

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element suiteElement = doc.createElement("suite");
		doc.appendChild(suiteElement);
		
		suiteElement.setAttributeNode(addAttribute(doc, "name", "BODTestSuite"));
		suiteElement.setAttributeNode(addAttribute(doc, "verbose", "2"));
		suiteElement.setAttributeNode(addAttribute(doc, "configfailurepolicy", "continue"));

		// staff elements
		Element parameter = doc.createElement("parameter");
		suiteElement.appendChild(parameter);
		
		parameter.setAttributeNode(addAttribute(doc, "name", "platform"));
		parameter.setAttributeNode(addAttribute(doc, "value", "Android"));

		parameter.setAttributeNode(addAttribute(doc, "name", "MovPassword"));
		parameter.setAttributeNode(addAttribute(doc, "value", "1"));
		
		
		
		// staff elements
		Element listeners = doc.createElement("listeners");		
		Element listener1 = doc.createElement("listener");
		Element listener2 = doc.createElement("listener");
		listeners.appendChild(listener1);
		listeners.appendChild(listener2);
		suiteElement.appendChild(listeners);
		
		

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File("C:\\MyDrive\\file.xml"));

		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);

		transformer.transform(source, result);

		System.out.println("File saved!");

	  } catch (ParserConfigurationException pce) {
		pce.printStackTrace();
	  } catch (TransformerException tfe) {
		tfe.printStackTrace();
	  }
	}
	
	
	public static Attr addAttribute(Document doc, String attributeName, String attributeValue){
		Attr attrSuite = doc.createAttribute(attributeName);
		attrSuite.setValue(attributeValue);		
		return attrSuite;		
	}
	
}