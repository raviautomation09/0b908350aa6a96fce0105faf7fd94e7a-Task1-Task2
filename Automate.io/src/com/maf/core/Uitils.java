package com.maf.core;

import io.appium.java_client.AppiumDriver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.Capabilities;
import org.testng.collections.Lists;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Uitils {
	static Logger logger = Logger.getLogger("devpinoyLogger");
	
	public static  FileInputStream fis = null;
	public  FileOutputStream fileOut =null;
    private static XSSFWorkbook workbook = null;
    private static XSSFSheet sheet = null;
    private static XSSFRow row   =null;
    private static XSSFCell cell = null;
	
    synchronized public static String getCurrentTimeStamp(String Format) {	
		String timeStamp = new SimpleDateFormat(Format).format(new java.util.Date());

		return timeStamp;
	}
	
	synchronized public static boolean compareString(String Exp, String Actual, boolean CaseSensitive) {
		logger.info("Comparing Text --> ActualText:["+Actual +"] Expected:["+Exp+"]");
		if (CaseSensitive) {
			if (Exp.trim().equals(Actual.trim())) {
				logger.info("Passed");
				return true;
			}else{
				logger.info("Fail");
				return false;
			}
		}else {
			if (Exp.trim().toLowerCase().equals(Actual.toLowerCase().trim())) {
				logger.info("Passed");
				return true;
			}else{
				logger.info("Fail");
				return false;
			}
		}
	}
	
	
	
	synchronized public static void Xls_Reader(String filename) {

		try {
			fis = new FileInputStream(filename);
			workbook = new XSSFWorkbook(fis);
			
			// sheet = workbook.getSheetAt(0);
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}   
	
	synchronized public static void closeWorkBook() {

		try {
			workbook.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	} 
	
	synchronized public static int getRowCount(String sheetName) {
		int index = workbook.getSheetIndex(sheetName);
		if (index == -1)
			return 0;
		else {
			sheet = workbook.getSheetAt(index);
			int number = sheet.getLastRowNum() + 1;
			// System.out.println(number);
			return number;
		}

	}
	
	synchronized public static String getCellData(String sheetName,String colName,int rowNum){
        try{
            if(rowNum <=0)
                return "";

            int index = workbook.getSheetIndex(sheetName);
            int col_Num=-1;
            if(index==-1)
                return "";

            sheet = workbook.getSheetAt(index);
            row=sheet.getRow(0);
            for(int i=0;i<row.getLastCellNum();i++){
                //System.out.println(row.getCell(i).getStringCellValue().trim());
                if(row.getCell(i).getStringCellValue().trim().equals(colName.trim()))
                    col_Num=i;
            }
            if(col_Num==-1)
                return "";

            sheet = workbook.getSheetAt(index);
            row = sheet.getRow(rowNum-1);
            if(row==null)
                return "";
            cell = row.getCell(col_Num);

            if(cell==null)
                return "";
            //System.out.println(cell.getCellType());
            if(cell.getCellType()==Cell.CELL_TYPE_STRING)
                return cell.getStringCellValue();
            else if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC || cell.getCellType()==Cell.CELL_TYPE_FORMULA ){

                String cellText  = String.valueOf(cell.getNumericCellValue());
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    // format in form of M/D/YY
                    double d = cell.getNumericCellValue();

                    Calendar cal =Calendar.getInstance();
                    cal.setTime(HSSFDateUtil.getJavaDate(d));
                    cellText =
                            (String.valueOf(cal.get(Calendar.YEAR))).substring(2);
                    cellText = cal.get(Calendar.DAY_OF_MONTH) + "/" +
                            cal.get(Calendar.MONTH)+1 + "/" +
                            cellText;

                    //System.out.println(cellText);

                }
                return cellText;
            }else if(cell.getCellType()==Cell.CELL_TYPE_BLANK)
                return "";
            else
                return String.valueOf(cell.getBooleanCellValue());

        }
        catch(Exception e){

            e.printStackTrace();
            return "row "+rowNum+" or column "+colName +" does not exist in xls";
        }
    }
	
	synchronized public static int getCellRowNum(String sheetName,String colName,String cellValue){

        for(int i=2;i<=getRowCount(sheetName);i++){
            if(getCellData(sheetName,colName , i).equalsIgnoreCase(cellValue)){
                return i;
            }
        }
        return -1;

    }
	synchronized public static Document loadXMLFromString(String xml)  throws ParserConfigurationException, IOException, SAXException, TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError
	{
		//logger.info("Before Parsing:"+xml);
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();		
		InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		Document xmlString = db.parse(is);
		
		//SoapUtil_LOGS.info(xml);
		
	    /*DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    InputSource is = new InputSource(new StringReader(xml));
	    Document xmlString =builder.parse(is);*/
	    //logger.info("After Parsing:"+printRequest(xmlString));
	    return xmlString;
	    		
	}
	
	synchronized public static String getAttributesXML(Document xmlResponse, String NodeName,String nodeIndexValue, String NodeAttribute){
		String AttributeValue=null;
		int IndexValue=Integer.parseInt(nodeIndexValue);
		try {
			 
	         NodeList nodeList = xmlResponse.getElementsByTagName(NodeName);
	         AttributeValue=nodeList.item(IndexValue).getAttributes().getNamedItem(NodeAttribute).getNodeValue();
	         
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		//logger.info("["+NodeName+"]["+NodeAttribute+"]---->["+AttributeValue+"]");
		return AttributeValue;		
	}
	
	
	synchronized public static String printRequest(Document doc) throws TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError, IOException{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Source xmlSource = new DOMSource(doc);
		Result outputTarget = new StreamResult(outputStream);
		TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
		InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		String requestEntity = IOUtils.toString(inputStream, "UTF-8");
		logger.info(requestEntity);
		return requestEntity;
	}
	
	
	public static List<String> fetchFailedTestNGXML(String FileName) {
        File root = new File(System.getProperty("user.dir") + File.separator + "test-output");
        java.util.List<String> suites = Lists.newArrayList();
        try {
            boolean recursive = true;

            Collection<?> files = FileUtils.listFiles(root, null, recursive);

			for (Iterator<?> iterator = files.iterator(); iterator.hasNext();) {
				File file = (File) iterator.next();
				if (!file.getAbsolutePath().equalsIgnoreCase(System.getProperty("user.dir") + File.separator + "test-output"+ File.separator +FileName)) {
					if (file.getName().equals(FileName)) {
						System.out.println(file.getAbsolutePath());
						suites.add(file.getAbsolutePath());
					}
					
				}

			}
            System.out.println(suites);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return suites;
    }
	
	synchronized public static String fetchTestDataUsingUDID(AppiumDriver<?> driver, String ColumnName){
		Capabilities cap = driver.getCapabilities();		
		String UDID = cap.getCapability("deviceUDID").toString();
		System.out.println("UDID is:"+UDID);
     	int cellRowNum= 0;
		
		int testSuiteRowCount = Uitils.getRowCount("AppiumURLS");    		
        for(int currentSuiteID=2;currentSuiteID<=testSuiteRowCount;currentSuiteID++){
        	if (Uitils.getCellData("AppiumURLS", "UDID", currentSuiteID).trim().equalsIgnoreCase(UDID)) {
        		cellRowNum=currentSuiteID;
        		break;
			}
        }        
		
		return Uitils.getCellData("AppiumURLS", ColumnName, cellRowNum).trim();
	}
	
	
	synchronized public static String fetchTestDataUsingDeviceID(String UDID, String ColumnName){
		System.out.println("UDID is:"+UDID);
     	int cellRowNum= 0;
		
		int testSuiteRowCount = Uitils.getRowCount("AppiumURLS");    		
        for(int currentSuiteID=2;currentSuiteID<=testSuiteRowCount;currentSuiteID++){
        	if (Uitils.getCellData("AppiumURLS", "UDID", currentSuiteID).trim().equalsIgnoreCase(UDID)) {
        		cellRowNum=currentSuiteID;
        		break;
			}
        }        
		
		return Uitils.getCellData("AppiumURLS", ColumnName, cellRowNum).trim();
	}
	
}
