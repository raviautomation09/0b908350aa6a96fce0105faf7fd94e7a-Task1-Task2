package com.maf.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.testng.TestNG;
import org.testng.collections.Lists;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.maf.core.Constants;
import com.maf.core.Uitils;
import com.maf.services.ServicesUtility;

import atu.testrecorder.ATUTestRecorder;
import atu.testrecorder.exceptions.ATUTestRecorderException;



public class Executor { 
	
	static Logger logger = Logger.getLogger("devpinoyLogger");
	public static Document doc ;
	public static Properties CONFIG;
	public static String strWkgDir = System.getProperty("user.dir");
	public static HashMap<String, String> AppiumURLS;
	public static Queue<String> AppiumURLQueue;
	public static ATUTestRecorder recorder;
	//static SynchronousQueue<String> AppiumURLQueue;// = new SynchronousQueue<String>();
	//public static StringBuilder asserstionDesc = new StringBuilder();
	
	public static List<String> SuiteNameArray = new ArrayList<String>();
    
	public static void main(String[] args) {
        	String strSuiteConfigPath = strWkgDir	+ "//Configuration/config.properties";
        	
        	try {
				
				// config property Initialization
				FileInputStream fs = new FileInputStream(strSuiteConfigPath);
				CONFIG = new Properties();
				CONFIG.load(fs);
				
				/**
				 * Load all request files for api when Service testing is true
				 */
				if (CONFIG.getProperty("isService").equalsIgnoreCase("true")) {
					ServicesUtility.loadAllRequests();					
				}
				
				/**
				 * Create recording object and start recording when test is web and isRecording is true
				 */
				if (CONFIG.getProperty("WebTestRecording").equalsIgnoreCase("true")) {
					DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH-mm-ss");
					Date date = new Date();
					try {
						recorder = new ATUTestRecorder(strWkgDir+ "//ScriptVideos/","TestVideo-"+dateFormat.format(date),false);
						recorder.start(); 
					} catch (ATUTestRecorderException e) {
						e.printStackTrace();
					}
					  
				}
				
				final String ESCAPE_PROPERTY = "org.uncommons.reportng.escape-output";
				System.setProperty(ESCAPE_PROPERTY, "false");
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        	
        	StopWatch stpw = new StopWatch();			
			stpw.start();		
        	
        	
        	if (args.length == 0) {
        		try {
        			System.out.println("************ Pre-Cleanup Activity ***********");
					FileUtils.cleanDirectory(new File(System.getProperty("user.dir")+"\\"+"temp"));
					System.out.println("Temp folder is cleaned.");
					
					FileUtils.cleanDirectory(new File(System.getProperty("user.dir")+"\\"+"test-output"));
					System.out.println("Reports folder is cleaned.");
					
					FileUtils.cleanDirectory(new File(System.getProperty("user.dir")+"\\"+"ScreenShot"));
					System.out.println("Reports folder is cleaned.");
					
					System.out.println("*********************************************");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
        		Uitils.Xls_Reader(System.getProperty("user.dir")+"\\"+"Configuration\\"+"Config.xlsx");        	
        		int testSuiteRowCount = Uitils.getRowCount("TestSuite"); 
        		fetchAppiumUrlsUDID();
                for(int currentSuiteID=2;currentSuiteID<=testSuiteRowCount;currentSuiteID++){
                	
                	String SuiteName= Uitils.getCellData("TestSuite", "TestSuiteName", currentSuiteID);
                	String verbose= Uitils.getCellData("TestSuite", "Verbose", currentSuiteID);
                	String configPolicy= Uitils.getCellData("TestSuite", "ConfigFailurePolicy", currentSuiteID);
                	String ExecutionFlag= Uitils.getCellData("TestSuite", "Execution", currentSuiteID);
                	
                	String parallelType= Uitils.getCellData("TestSuite", "Parallel", currentSuiteID);
                	String threadCount= Uitils.getCellData("TestSuite", "thread-count", currentSuiteID);
                	
                	if (ExecutionFlag.toLowerCase().trim().equalsIgnoreCase("run")) {            		
                		
                		SuiteNameArray.add(SuiteName);
                		
                		/*System.out.println(SuiteNameArray);
                		System.out.println(fetchSuiteParameters(SuiteName));
                		System.out.println(fetchSuiteListenerPackage(SuiteName));                		
                		System.out.println(fetchTestNameAndClass(SuiteName));
                		System.out.println(fetchTestMethods("BOD_TransfersTest"));
                		System.out.println(fetchTestParameters("BOD_TransfersTest"));*/
                		
                		
                		try {
                			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                    		DocumentBuilder docBuilder = null;
                    		
                    		docBuilder = docFactory.newDocumentBuilder();
                    		// root elements
                    		doc = docBuilder.newDocument();
                    		// staff elements
                    		Element suiteRoot = doc.createElement("suite");
                    		Attr attrSuiteName = addAttribute(doc, "name", SuiteName);
                    		Attr attrSuiteVerbose = addAttribute(doc, "verbose", verbose);
                    		Attr attrSuiteConfigPolicy = addAttribute(doc, "configfailurepolicy", configPolicy);
                    		Attr attrSuitePreserverOrder = addAttribute(doc, "preserve-order", "true");
                    		
                    		Attr attrSuiteParallel = null; 
                    		Attr attrSuiteThreadCnt = null;
                    		boolean isParallel=Boolean.valueOf(CONFIG.getProperty("isParallel"));
                    		
                    		if (isParallel==true) {
                    			attrSuiteParallel = addAttribute(doc, "parallel", parallelType);
                        		attrSuiteThreadCnt = addAttribute(doc, "thread-count", threadCount);
							}
                    		
                    		
                    		suiteRoot.setAttributeNode(attrSuiteName);
                    		suiteRoot.setAttributeNode(attrSuiteVerbose);
                    		suiteRoot.setAttributeNode(attrSuiteConfigPolicy);
                    		suiteRoot.setAttributeNode(attrSuitePreserverOrder);
                    		
                    		if (isParallel==true) {
                    			suiteRoot.setAttributeNode(attrSuiteParallel);
                        		suiteRoot.setAttributeNode(attrSuiteThreadCnt);
							}
                    		
                    		
            		//****************************** prepare Listeners *****************************************************
                    		List<String> Listeners = fetchSuiteListenerPackage(SuiteName);                		
                    		Element listenersRoot = doc.createElement("listeners");
                    		for (String string : Listeners) {
                    			Element tempEle = doc.createElement("listener");
                    			Attr attr = addAttribute(doc, "class-name", string);
                    			tempEle.setAttributeNode(attr);
                    			listenersRoot.appendChild(tempEle);
                			}
                    		suiteRoot.appendChild(listenersRoot);
                    		
                    		
                    		
                   //****************************** Prepare Parameters *****************************************************
                    		HashMap<String, String> parameters = fetchSuiteParameters(SuiteName);
                    		for (String key : parameters.keySet()) {
            	    			Element parameter = doc.createElement("parameter");
            	    			
            	    			Attr attr1 = addAttribute(doc,"name", key);
            	    			parameter.setAttributeNode(attr1);        	    			
            	    			
            	    			Attr attr2 = addAttribute(doc, "value", parameters.get(key));
            	    			parameter.setAttributeNode(attr2);
            	    			
            	    			
            	    			suiteRoot.appendChild(parameter);
            				}
                    		//doc.appendChild(suiteRoot);
                  
                   //****************************** Prepare Test *****************************************************
                    		//Prepare test
                    		LinkedHashMap<String, String> tests = fetchTestNameAndClass(SuiteName);
                    		
                    		
                    		for (String key : tests.keySet()) {
                    			Element testRoot = doc.createElement("test");
                    			Attr attr1 = addAttribute(doc,"name", key);
                    			testRoot.setAttributeNode(attr1);
                    			
                    			//test parameters
                    			HashMap<String, String> parametersTest = fetchTestParameters(key);
                    			
                        		for (String testkey : parametersTest.keySet()) {
                	    			Element parameter = doc.createElement("parameter");
                	    			
                	    			Attr attr3 = addAttribute(doc,"name", testkey);
                	    			parameter.setAttributeNode(attr3);        	    			
                	    			
                	    			Attr attr4 = addAttribute(doc, "value", parametersTest.get(testkey));
                	    			parameter.setAttributeNode(attr4);
                	    			
                	    			testRoot.appendChild(parameter);
                				}
                        		
                        		//classes 
                        		Element classesRoot = doc.createElement("classes");
                        		Element classRoot = doc.createElement("class");
                        		Attr attr5 = addAttribute(doc,"name", tests.get(key));
                        		classRoot.setAttributeNode(attr5);
                        		
                        		
                        		//methods
                        		Element methodsRoot = doc.createElement("methods");
                        		List<String> methods = fetchTestMethods(key);   		
                        		
                 	    		for (String string : methods) {
                 	    			Element ele = doc.createElement("include");
                 	    			Attr attr = addAttribute(doc, "name", string);
                 	    			ele.setAttributeNode(attr);
                 	    			methodsRoot.appendChild(ele);
                 	    			
                 				}
                        		if (!methods.isEmpty()) {
                        			classRoot.appendChild(methodsRoot);
                     	    		classesRoot.appendChild(classRoot);
                     	    		testRoot.appendChild(classesRoot);             	    		
                     	    		suiteRoot.appendChild(testRoot);
								}
                 	    		
                    			
            	    			
        					}
                    		
                    		
                    		doc.appendChild(suiteRoot);
                    		
    					} catch (Exception e) {
    						e.printStackTrace();
    					}
                		
                		
                		try {
                			
                    		// write the content into xml file
                    		TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    		Transformer transformer = transformerFactory.newTransformer();
                    		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                    		transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://testng.org/testng-1.0.dtd");
                    		DOMSource source = new DOMSource(doc);
                    		StreamResult result = new StreamResult(new File(System.getProperty("user.dir")+File.separator+"temp"+File.separator+SuiteName+".xml"));

                    		// Output to console for testing
                    		StreamResult result1 = new StreamResult(System.out);

                    		transformer.transform(source, result);
                    		System.out.println(SuiteName+".xml:File saved!");
    					} catch (Exception e) {
    						System.out.println("Error while preparing Suites TestNG XML's.....");
    						e.printStackTrace();
    					}
                	

                		
                		
    				}
                	
                }
                
                
                try {
    				
                	//prepare run all suite:
                    
                    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            		DocumentBuilder docBuilder = null;
            		
            		docBuilder = docFactory.newDocumentBuilder();
            		// root elements
            		Document doc2 = docBuilder.newDocument();
            		// staff elements
            		Element suiteRoot = doc2.createElement("suite");
            		Attr attrSuiteName = addAttribute(doc2, "name", "ExecuteAll");
            		suiteRoot.setAttributeNode(attrSuiteName);
            		
            		
            		
            		Element listenersRoot = doc2.createElement("listeners");
            		
            		Element tempEle = doc2.createElement("listener");
            		Attr attr = addAttribute(doc2, "class-name", "com.maf.core.TestListener");
            		tempEle.setAttributeNode(attr);
            		listenersRoot.appendChild(tempEle);
            		
            		Element tempEle2 = doc2.createElement("listener");
            		Attr attr2 = addAttribute(doc2, "class-name", "org.uncommons.reportng.HTMLReporter");
            		tempEle2.setAttributeNode(attr2);
            		listenersRoot.appendChild(tempEle2);
            		
            		
            		Element tempEle3 = doc2.createElement("listener");
            		Attr attr3 = addAttribute(doc2, "class-name", "org.uncommons.reportng.JUnitXMLReporter");
            		tempEle3.setAttributeNode(attr3);
            		listenersRoot.appendChild(tempEle3);        		
            			
            		suiteRoot.appendChild(listenersRoot);
            		
            		Element suitesRoot = doc2.createElement("suite-files");
            		//System.out.println(SuiteNameArray);
            		for (String string : SuiteNameArray) {
            			Element suiteFile = doc2.createElement("suite-file");
                		Attr attr4 = addAttribute(doc2, "path", "./"+string+".xml");
                		suiteFile.setAttributeNode(attr4);
                		suitesRoot.appendChild(suiteFile);
    				}
            		
            		
            		suiteRoot.appendChild(suitesRoot);
            		doc2.appendChild(suiteRoot);
                	
            		
            		
            		try {
            			
                		// write the content into xml file
                		TransformerFactory transformerFactory = TransformerFactory.newInstance();
                		Transformer transformer = transformerFactory.newTransformer();
                		//to avoid declaration of xml
                		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                		transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://testng.org/testng-1.0.dtd");
                		DOMSource source = new DOMSource(doc2);
                		StreamResult result = new StreamResult(new File(System.getProperty("user.dir")+File.separator+"temp"+File.separator+"RunAll.xml"));

                		transformer.transform(source, result);
                		System.out.println("Run.xml:Saved!");
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
            		
            		
            		try {
            			/*//append Doc type
            			File folder = new File(System.getProperty("user.dir")+File.separator+"temp");
            			File[] listOfFiles = folder.listFiles();

            			for (File file : listOfFiles) {
            			    if (file.isFile()) {
            			        System.out.println(file.getAbsolutePath());            			        
            			        File mFile = new File(file.getAbsolutePath());
            			        FileInputStream fis = new FileInputStream(mFile);
            			        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            			        String result = "";
            			        String line = "";
            			        while( (line = br.readLine()) != null){
            			         result = result + line; 
            			        }
            			        
            			        result = "<!DOCTYPE suite SYSTEM \"http://testng.org/testng-1.0.dtd\" >" + result;

            			        mFile.delete();
            			        FileOutputStream fos = new FileOutputStream(mFile);
            			        fos.write(result.getBytes());
            			        fos.flush();
            			        fos.close();
            			    }
            			}*/
            			
            			
            			//run all test NG suites using RunAll
    					TestNG testng = new TestNG();
    					java.util.List<String> suites = Lists.newArrayList();
    					suites.add(System.getProperty("user.dir") + File.separator + "temp"+File.separator+"RunAll.xml");
    					testng.setTestSuites(suites);
    					System.out.println("Starting the execution....");
    					testng.run();
    					
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
            		
            		
    			} catch (Exception e) {
    				System.out.println("Error while preparing RunAll TestNG XML's.....");
					e.printStackTrace();
    			}
                
                    	
                Uitils.closeWorkBook();
			} else {
				Uitils.Xls_Reader(System.getProperty("user.dir")+"\\"+"Configuration\\"+"Config.xlsx");
        		fetchAppiumUrlsUDID();
				
				TestNG testng = new TestNG();
				java.util.List<String> suites = Lists.newArrayList();
				if (args[0].trim().toLowerCase().equals("fail")) {
					/*suites.add(System.getProperty("user.dir") + File.separator + "test-output"+File.separator+"testng-failed.xml");
					Uitils.fetchFailedTestNGXML("testng-failed.xml");*/
					System.out.println(Uitils.fetchFailedTestNGXML("testng-failed.xml"));
					testng.setTestSuites(Uitils.fetchFailedTestNGXML("testng-failed.xml"));
					System.out.println("Starting the execution of FAILED tests ....");
					testng.run();
					
				}else{
					System.out.println("Starting the execution of "+args[0]+" tests ....");
					
					System.out.println(args[0]);
					suites.add(args[0].trim());	
					testng.setTestSuites(suites);
					System.out.println("Starting the execution....");
					testng.run();
				}
				
				
			}
        	
        				
			stpw.stop();
			
			long totalTime = stpw.getTime();
			
			int h = (int) ((totalTime / 1000) / 3600);
			int m = (int) (((totalTime / 1000) / 60) % 60);
			int s = (int) ((totalTime / 1000) % 60);
			
			logger.info("************ [Total Execution Time-"+h+":"+m+":"+s+"] *******************]");
        	try {
	        	// Lets append the Overvie table to html report
	        	File mFile = new File(System.getProperty("user.dir") + File.separator + "test-output"+File.separator+"testng-results.xml");
		        FileInputStream fis;
				
		        fis = new FileInputStream(mFile);
				
		        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		        String result = "";
		        String line = "";
		        while( (line = br.readLine()) != null){
		         result = result + line; 
		        }
		        br.close();
		        
		        String Passed = Uitils.getAttributesXML(Uitils.loadXMLFromString(result), "testng-results", "0", "passed");
		        String failed = Uitils.getAttributesXML(Uitils.loadXMLFromString(result), "testng-results", "0", "failed");
		        String skipped = Uitils.getAttributesXML(Uitils.loadXMLFromString(result), "testng-results", "0", "skipped");
		        
		        String table = "<table align='center' border='1' width='50%' class='main-page'>"
					        		+ "<tr>"
					        			+ "<th>Passed</th>"
					        			+ "<th>Failed</th>"
					        			+ "<th>Skipped</th>"
					        			+ "<th>TotalExecutionTime</th>"
				        			+ "</tr>"
					        		+ "<tr align='center' class='invocation-failed'>"
					        			+ "<td>"
					        				+ "<h3 class=\"passed\">"+Passed+"</h3>"
		        						+ "</td> "
			        					+ "<td>"
			        						+ "<h3 class=\"failed\">"+failed+"</h3>"
        								+ "</td>"
										+ "<td>"
											+ "<h3>"+skipped+"</h3>"
										+ "</td> "
										+ "<td>"
											+ "<h3>"+h+" Hrs:"+m+" Mins:"+s+" Secs</h3>"
										+ "</td> "
									+ "</tr> "
								+ "</table>";
		        
		        
		        // Lets append the Overvie table to html report
	        	File mFileIndex = new File(System.getProperty("user.dir") + File.separator + "test-output"+File.separator+"html"+File.separator+"overview.html");
		        FileInputStream fis2;
				
		        fis2 = new FileInputStream(mFileIndex);
				
		        BufferedReader br2 = new BufferedReader(new InputStreamReader(fis2));
		        String result2 = "";
		        String line2 = "";
		        while( (line2 = br2.readLine()) != null){
		         result2 = result2 + line2; 
		        }
		        br2.close();
		        result2 = result2.replace("<h1>Test Results Report</h1>", "<h1>"+CONFIG.getProperty("ReportTitle")+"</h1>"+table);
		        
		        mFile.delete();
		        FileOutputStream fos = new FileOutputStream(mFileIndex);
		        fos.write(result2.getBytes());
		        fos.flush();
		        fos.close();
	        
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerFactoryConfigurationError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
	}
        
        
        
        
		
		 public static void fetchAppiumUrlsUDID(){
			 AppiumURLS = new HashMap<String, String>();
			 AppiumURLQueue = new ConcurrentLinkedQueue<String>();
			 
			 //AppiumURLQueue = new SynchronousQueue<String>();
	     	int testSuiteRowCount = Uitils.getRowCount("AppiumURLS");    		
	        for(int currentSuiteID=2;currentSuiteID<=testSuiteRowCount;currentSuiteID++){
	        	
	        	if (!Uitils.getCellData("AppiumURLS", "AppiumUrls", currentSuiteID).trim().equalsIgnoreCase("")) {
	        		AppiumURLS.put(Uitils.getCellData("AppiumURLS", "AppiumUrls", currentSuiteID).trim(), Uitils.getCellData("AppiumURLS", "UDID", currentSuiteID).trim()); 
		        	AppiumURLQueue.add(Uitils.getCellData("AppiumURLS", "AppiumUrls", currentSuiteID).trim());
				}
	        	
	        	System.out.println(AppiumURLQueue);
	        }
	     }
        	
        
        public static List<String> fetchSuiteListenerPackage(String TestSuiteName){
        	List<String> Listeners = new ArrayList<>();
        	int rowNo = Uitils.getCellRowNum("SuiteListeners", "TestSuiteName", TestSuiteName);
        	while (TestSuiteName.equalsIgnoreCase(Uitils.getCellData("SuiteListeners", "TestSuiteName", rowNo).trim())) {				
        		Listeners.add(Uitils.getCellData("SuiteListeners", "ListenerPackage", rowNo).trim());        		
        		rowNo++;
			}        	
        	return Listeners;
        }
        
        
        public static List<String> fetchTestMethods(String TestName){
        	List<String> tests = new ArrayList<>();
        	int rowNo = Uitils.getCellRowNum("TestMethods", "TestName", TestName);
        	while (TestName.equalsIgnoreCase(Uitils.getCellData("TestMethods", "TestName", rowNo).trim())) {
        		
        		if (Uitils.getCellData("TestMethods", "Execution", rowNo).trim().equalsIgnoreCase("run")) {
        			tests.add(Uitils.getCellData("TestMethods", "TestMethods", rowNo).trim());
				}	
        		rowNo++;
			}        	
        	return tests;
        }
        
        
        public static LinkedHashMap<String, String> fetchSuiteParameters(String TestSuiteName){
        	LinkedHashMap<String, String> parameter = new LinkedHashMap<String, String>();
        	int rowNo = Uitils.getCellRowNum("SuiteParameters", "TestSuiteName", TestSuiteName);
        	while (TestSuiteName.equalsIgnoreCase(Uitils.getCellData("SuiteParameters", "TestSuiteName", rowNo).trim())) {
        		String Name = Uitils.getCellData("SuiteParameters", "ParameterName", rowNo);
        		String Value =Uitils.getCellData("SuiteParameters", "ParameterValue", rowNo);
        		
        		if (Value.toLowerCase().startsWith(Constants.CONFIG)) {
					// read actual data value from config.properties
					String splittedConst = Value.split(Constants.DATA_SPLIT)[1];
					try {
						Value = CONFIG.getProperty(splittedConst);
					} catch (Exception e) {
						System.out.println(e.toString());
					}
        		}
        		parameter.put(Name, Value);        		
        		rowNo++;
			}        	
        	return parameter;
        }
        
        
        public static LinkedHashMap<String, String> fetchTestNameAndClass(String SuiteName){
        	LinkedHashMap<String, String> TestClass = new LinkedHashMap<String, String>();
        	int rowNo = Uitils.getCellRowNum("TestClass", "TestSuiteName", SuiteName);
        	while (SuiteName.equalsIgnoreCase(Uitils.getCellData("TestClass", "TestSuiteName", rowNo))) {
        		if (Uitils.getCellData("TestClass", "Execution", rowNo).trim().equalsIgnoreCase("RUN")) {
        			TestClass.put(Uitils.getCellData("TestClass", "TestName", rowNo).trim(), Uitils.getCellData("TestClass", "TestClass", rowNo).trim());
				}        		
        		rowNo++;
			}        	
        	return TestClass;
        }
        
        
        
        public static LinkedHashMap<String, String> fetchTestParameters(String TestName){
        	LinkedHashMap<String, String> TestClass = new LinkedHashMap<String, String>();
        	int rowNo = Uitils.getCellRowNum("TestParameters", "TestName", TestName);
        	while (TestName.equalsIgnoreCase(Uitils.getCellData("TestParameters", "TestName", rowNo))) {
        		TestClass.put(Uitils.getCellData("TestParameters", "ParameterName", rowNo).trim(), Uitils.getCellData("TestParameters", "ParameterValue", rowNo).trim());  		
        		rowNo++;
			}        	
        	return TestClass;
        }
        
        
        public static Element prepareListenerDoc(List<String> Listeners){
        	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    		DocumentBuilder docBuilder = null;
    		Element listenersRoot=null;
    		
			try {
				docBuilder = docFactory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	// root elements
    		Document doc = docBuilder.newDocument();
    		// staff elements
    		listenersRoot = doc.createElement("listeners");
    		for (String string : Listeners) {
    			Element tempEle = doc.createElement("listener");
    			Attr attr = addAttribute(doc, "class-name", string);
    			tempEle.setAttributeNode(attr);
    			listenersRoot.appendChild(tempEle);
    			//listenersRoot.appendChild(doc.createElement("listener").setAttributeNode(addAttribute(doc, "class-name", string)));
			}
        
        	return listenersRoot;
        }
        
        public static Element prepareParameterDoc(HashMap<String, String> parameters){
        	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    		DocumentBuilder docBuilder = null;
    		Document doc = null;
    		Element parameter=null;
			try {
				docBuilder = docFactory.newDocumentBuilder();
	    		doc = docBuilder.newDocument();   		
	    		for (String key : parameters.keySet()) {
	    			parameter = doc.createElement("parameter");
	    			Attr attr = addAttribute(doc, key, parameters.get(key));
	    			parameter.setAttributeNode(attr);
	    			
	    			//parameter.setAttributeNode(addAttribute(doc, key, parameters.get(key)));
				}
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        
        	return parameter;
        }
        
        
        public static Element prepareMethodsDoc(List<String> methods){
        	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    		DocumentBuilder docBuilder = null;
    		Document doc = null;
    		Element methodsList = null;
			try {
				docBuilder = docFactory.newDocumentBuilder();
				// root elements
	    		doc = docBuilder.newDocument();
	    		// staff elements
	    		 methodsList= doc.createElement("methods");
	    		for (String string : methods) {
	    			Element ele = doc.createElement("include");
	    			Attr attr = addAttribute(doc, "name", string);
	    			ele.setAttributeNode(attr);
	    			methodsList.appendChild(ele);
	    			
	    			//methodsList.appendChild(doc.createElement("include").setAttributeNode(addAttribute(doc, "name", string)));
				}
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        
        	return methodsList;
        }
        
        public static Element prepareTestDoc(String TestName, String ClassPackage, Element Parameters, Element MethodsDoc){
        	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    		DocumentBuilder docBuilder = null;
    		Document doc = null;
    		Element testElement=null;
			try {
				docBuilder = docFactory.newDocumentBuilder();
				// root elements
	    		doc = docBuilder.newDocument();
	    		// staff elements
	    		Element classElement= doc.createElement("class");
	    		classElement.setAttributeNode(addAttribute(doc, "name", ClassPackage));	    		
	    		classElement.appendChild(doc.importNode(MethodsDoc, true));
	    		
	    		Element classList= doc.createElement("classes");
	    		classList.appendChild(classElement);
	    		
	    		testElement= doc.createElement("test");
	    		testElement.setAttributeNode(addAttribute(doc, "name", TestName));
	    		testElement.appendChild(doc.importNode(Parameters, true));
	    		//testElement.appendChild(Parameters);
	    		testElement.appendChild(classList);  
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	  		
        
        	return testElement;
        }
        
        
        
        public static Document prepareTestSuiteDoc(String TestSuiteName, Element parameters, Element suiteListeners, List<Element> Tests){
        	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    		DocumentBuilder docBuilder = null;
    		Document doc = null;
    		Element suiteElement=null;
    		
			try {
				docBuilder = docFactory.newDocumentBuilder();
				// root elements
	    		doc = docBuilder.newDocument();
	    		// staff elements
	    		suiteElement= doc.createElement("suite");    		
	    		suiteElement.setAttributeNode(addAttribute(doc, "name", TestSuiteName));   		
	    		
	    		suiteElement.appendChild(doc.importNode(parameters, true));
	    		suiteElement.appendChild(doc.importNode(suiteListeners, true));
	    		//suiteElement.appendChild(parameters);
	    		//suiteElement.appendChild(suiteListeners);
	    		for (Element document : Tests) {
	    			suiteElement.appendChild(doc.importNode(document, true));
	    			//suiteElement.appendChild(document);     
				}
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
    		   
        	return doc;
        }
        
        
        public static Attr addAttribute(Document doc, String attributeName, String attributeValue){
    		Attr attrSuite = doc.createAttribute(attributeName);
    		attrSuite.setValue(attributeValue);		
    		return attrSuite;		
    	}
        
        
        
}
