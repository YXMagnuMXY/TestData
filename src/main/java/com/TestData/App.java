package com.TestData;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.TestData.dto.Rate;
import com.TestData.dto.ValCurs;
import com.TestData.dto.Valute;
import com.thoughtworks.xstream.XStream;





public class App 
{
	   static Logger log = Logger.getLogger(App.class.getName());

    @Test
	public void test() throws IOException
	{
    	 //Format of the date - Today and Yesterday
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String today= dateFormat.format(cal.getTime());
        cal.add(Calendar.DATE, -1);
        String yesterday= dateFormat.format(cal.getTime());

				URL url = new URL("https://www.bnm.md/en/export-official-exchange-rates?date=" + today + "&xls=1");
			    URLConnection urlc = url.openConnection();
			    InputStream is = urlc.getInputStream();
			    HSSFWorkbook wb = new HSSFWorkbook(is);
			   	HSSFSheet sh = wb.getSheetAt(0);
				

			   	
				//EXCEL TO XML to import data with XStream
				String ExtoXML="<ValCurs Date=\"" + dateFormat.format(sh.getRow(0).getCell(1).getDateCellValue()) + "\" name=\"" +sh.getRow(0).getCell(0).toString() + "\">\n";
				for(int i=3;i<45;i++)
				{
						ExtoXML=ExtoXML+"<Valute ID=\"" + null + 
								"\">\n<NumCode>"+sh.getRow(i).getCell(1).toString()+"</NumCode>\n" +
								"><CharCode>"+sh.getRow(i).getCell(2).toString()+"</CharCode>\n" +
								"><Nominal>"+sh.getRow(i).getCell(3).toString()+"</Nominal>\n" +
								"><Name>"+sh.getRow(i).getCell(0).toString()+"</Name>\n" +
								"><Value>"+sh.getRow(i).getCell(4).toString()+"</Value>\n" +
								"</Valute>\n";
				}
				ExtoXML=ExtoXML + "</ValCurs>";
				wb.close();

			XStream xstream = new XStream();
		    xstream.processAnnotations(ValCurs.class);
		    xstream.processAnnotations(Valute.class);
				
			
			//XML for today to object
			log.info("Importing DATA from today's XML file");
			ValCurs xmlT = (ValCurs)xstream.fromXML(new URL("https://www.bnm.md/en/official_exchange_rates?get_xml=1&date=" + today));
			
			//XML for yesterday to object
			log.info("Importing DATA from yesterday's XML file");
			ValCurs xmlY = (ValCurs)xstream.fromXML(new URL("https://www.bnm.md/en/official_exchange_rates?get_xml=1&date=" + yesterday));
			
			//EXCEL to object	
			log.info("Importing DATA from today's EXCEL file");
			ValCurs ExcelT = (ValCurs)xstream.fromXML(ExtoXML);

			List<Rate> RateList = new ArrayList<Rate>();
			

		//	System.out.println("\n\nObject loaded from today's XML " + xmlT);
		//	System.out.println("\n\nObject loaded from yesterday's XML " + xmlY);
		//	System.out.println("\n\nObject loaded from today's Excel File. " + ExcelT);

			//Comparing XML from yesterday and XML from today, and creating new object with Rates to compare with data from site
			log.info("Comparing the today's XML file and yesterday's XML file to create RateList for further comparision");
			int EqCount = 0;
			for(int i = 0; i<xmlT.getValute().size(); i++)
			{
				for(int j = 0; j<xmlY.getValute().size(); j++)
				{
					if (xmlT.getValute().get(i).getCharCode()==xmlY.getValute().get(j).getCharCode())
					{
						if(xmlT.getValute().get(i).getValue()<xmlY.getValute().get(j).getValue())
						{
							//System.out.println(xmlT.getValute().get(i).getValue() + " < "+ xmlY.getValute().get(j).getValue());
							Rate mItem = new Rate(); 
							mItem.setStatus("rate down");
							mItem.setCharCode(xmlT.getValute().get(i).getCharCode());
							RateList.add(mItem);
						}
						else if(xmlT.getValute().get(i).getValue()>xmlY.getValute().get(j).getValue())
						{
							//System.out.println(xmlT.getValute().get(i).getValue() + " > "+ xmlY.getValute().get(j).getValue());
							Rate mItem = new Rate(); 
							mItem.setStatus("rate up");
							mItem.setCharCode(xmlT.getValute().get(i).getCharCode());
							RateList.add(mItem);
						}
						else if(xmlT.getValute().get(i).getValue()==xmlY.getValute().get(j).getValue())
						{
							//System.out.println(xmlT.getValute().get(i).getValue() + " = "+ xmlY.getValute().get(j).getValue());
							Rate mItem = new Rate(); 
							mItem.setStatus("rate none");
							mItem.setCharCode(xmlT.getValute().get(i).getCharCode());
							RateList.add(mItem);
							EqCount++;
						}
					}
				}
			}
			log.info("RateList created acting on previous comparision(today's XML and yesterday's XML)");

			//Comparing the today's XML file and today's EXCEL file
			log.info("Comparing the today's XML file and today's EXCEL file");
			EqCount = 0;
			for(int i = 0; i<xmlT.getValute().size(); i++)
			{
				for(int j = 0; j<ExcelT.getValute().size(); j++)
				{
					if (xmlT.getValute().get(i).getCharCode()==ExcelT.getValute().get(j).getCharCode())
					{
						if(xmlT.getValute().get(i).getValue()<ExcelT.getValute().get(j).getValue())
						{
							//System.out.println(xmlT.getValute().get(i).getValue() + " < "+ ExcelT.getValute().get(j).getValue());
						}
						else if(xmlT.getValute().get(i).getValue()>ExcelT.getValute().get(j).getValue())
						{
							//System.out.println(xmlT.getValute().get(i).getValue() + " > "+ ExcelT.getValute().get(j).getValue());
						}
						else if(xmlT.getValute().get(i).getValue()==ExcelT.getValute().get(j).getValue())
						{
							//System.out.println(xmlT.getValute().get(i).getValue() + " = "+ ExcelT.getValute().get(j).getValue());
							EqCount++;
						}
					}
				}
			}
			
			//System.out.println("\n" + RateList);

			if(EqCount==xmlT.getValute().size())
			{
				log.info("DATA from XML file for today IS EQUAL to DATA from EXCEL file for today");
			}
			else
			{
				log.info("DATA from XML file for today IS NOT EQUAL to DATA from EXCEL file for today");

			}
		//	Assert.assertEquals(EqCount, xmlT.getValute().size());
			
    	System.setProperty("webdriver.chrome.driver", "src\\main\\java\\com\\TestData\\chromedriver\\chromedriver.exe");
		WebDriver driver = new ChromeDriver();
    	driver.get("https://www.bnm.md/en/content/official-exchange-rates");
		driver.findElement(By.className("less")).click();
		WebElement table1 = driver.findElement(By.xpath("//*[@id=\"ajax-wrapper-table\"]/div[1]/div[1]/table[2]/tbody"));
		WebElement table2 = driver.findElement(By.xpath("//*[@id=\"ajax-wrapper-table\"]/div[1]/div[2]/table[2]/tbody"));

		List<WebElement>  Rows1 = table1.findElements(By.tagName("tr"));
		List<WebElement>  Rows2 = table2.findElements(By.tagName("tr"));


		
				
		//Importing data from first table from SITE
		log.info("Importing Value data from first table from SITE");
		List<Valute> siteTableList = new ArrayList<Valute>();
		for(int i = 1; i<Rows1.size()+1; i++)
		{
						
						Valute mItem = new Valute();
						mItem.setCharCode(driver.findElement(By.xpath("//*[@id=\"ajax-wrapper-table\"]/div[1]/div[1]/table[2]/tbody/tr["+ i +"]/td[3]")).getText());
						mItem.setId("null");
						mItem.setName(driver.findElement(By.xpath("//*[@id=\"ajax-wrapper-table\"]/div[1]/div[1]/table[2]/tbody/tr["+ i +"]/td[1]")).getText());
						mItem.setNominal(driver.findElement(By.xpath("//*[@id=\"ajax-wrapper-table\"]/div[1]/div[1]/table[2]/tbody/tr["+ i +"]/td[4]")).getText());						
						mItem.setNumCode(driver.findElement(By.xpath("//*[@id=\"ajax-wrapper-table\"]/div[1]/div[1]/table[2]/tbody/tr["+ i +"]/td[2]")).getText());
						mItem.setValue(Float.parseFloat(driver.findElement(By.xpath("//*[@id=\"ajax-wrapper-table\"]/div[1]/div[1]/table[2]/tbody/tr["+ i +"]/td[5]")).getText()));
						siteTableList.add(mItem);
			
		}
		
		//Importing data from second table from SITE
		log.info("Importing Value data from second table from SITE");
		for(int i = 1; i<Rows2.size()+1; i++)
		{
						Valute mItem = new Valute();
						mItem.setCharCode(driver.findElement(By.xpath("//*[@id=\"ajax-wrapper-table\"]/div[1]/div[2]/table[2]/tbody/tr["+ i +"]/td[3]")).getText());
						mItem.setId("null");
						mItem.setName(driver.findElement(By.xpath("//*[@id=\"ajax-wrapper-table\"]/div[1]/div[2]/table[2]/tbody/tr["+ i +"]/td[1]")).getText());
						mItem.setNominal(driver.findElement(By.xpath("//*[@id=\"ajax-wrapper-table\"]/div[1]/div[2]/table[2]/tbody/tr["+ i +"]/td[4]")).getText());						
						mItem.setNumCode(driver.findElement(By.xpath("//*[@id=\"ajax-wrapper-table\"]/div[1]/div[2]/table[2]/tbody/tr["+ i +"]/td[2]")).getText());
						mItem.setValue(Float.parseFloat(driver.findElement(By.xpath("//*[@id=\"ajax-wrapper-table\"]/div[1]/div[2]/table[2]/tbody/tr["+ i +"]/td[5]")).getText()));
						siteTableList.add(mItem);
			
		}
		
		//Importing Rates data from first table from SITE
		log.info("Importing Rates data from first table from SITE");
		List<Rate> siteRates = new ArrayList<Rate>();
		for(int i = 1; i<Rows1.size()+1; i++)
		{
						
						Rate mItem = new Rate();
						mItem.setCharCode(driver.findElement(By.xpath("//*[@id=\"ajax-wrapper-table\"]/div[1]/div[1]/table[2]/tbody/tr["+ i +"]/td[3]")).getText());
						mItem.setStatus(driver.findElement(By.xpath("//*[@id=\"ajax-wrapper-table\"]/div[1]/div[1]/table[2]/tbody/tr["+i+"]/td[5]")).findElement(By.tagName("span")).getAttribute("class"));
						
						siteRates.add(mItem);
			
		}
		
		//Importing Rates data from second table from SITE
		log.info("Importing Rates data from second table from SITE");
		for(int i = 1; i<Rows2.size()+1; i++)
		{
						Rate mItem = new Rate();
						mItem.setCharCode(driver.findElement(By.xpath("//*[@id=\"ajax-wrapper-table\"]/div[1]/div[1]/table[2]/tbody/tr["+ i +"]/td[3]")).getText());
						mItem.setStatus(driver.findElement(By.xpath("//*[@id=\"ajax-wrapper-table\"]/div[1]/div[1]/table[2]/tbody/tr["+i+"]/td[5]")).findElement(By.tagName("span")).getAttribute("class"));
			
						siteRates.add(mItem);
			
		}
		
		
	//	System.out.println(siteTableList);
	//	System.out.println(siteRates);
		
		//Comparing DATA from SITE and today's XML
		EqCount = 0;
		log.info("Comparing today's XML and data from SITE");
		for(int i=0;i<xmlT.getValute().size();i++)
		{
			for (int j=0;j<siteTableList.size();j++)
			{
				if (xmlT.getValute().get(i).getCharCode().equals(siteTableList.get(j).getCharCode()))
				{
					//System.out.println(xmlT.getValute().get(i).getCharCode()+"="+siteTableList.get(j).getCharCode());

					if(xmlT.getValute().get(i).getValue()==siteTableList.get(j).getValue())
					{
						//System.out.println(xmlT.getValute().get(i).getValue()+" = "+siteTableList.get(j).getValue());
						EqCount++;
					}
					else if (xmlT.getValute().get(i).getValue()>siteTableList.get(j).getValue())
					{
						//System.out.println(xmlT.getValute().get(i).getValue()+" > "+siteTableList.get(j).getValue());
					}
					else if (xmlT.getValute().get(i).getValue()<siteTableList.get(j).getValue())
					{
						//System.out.println(xmlT.getValute().get(i).getValue()+" < "+siteTableList.get(j).getValue());
					}
				}
			}
		}
	
		if (EqCount == xmlT.getValute().size())
		{
			log.info("Correct DATA on SITE!");
		}
		else
		{
			log.info("Incorrect DATA on SITE!");
		}
		
		//Checking if Rates arrows from site are correct paced acting on XML files comparison(from today and yesterday)
		log.info("Checking Rate arrows from SITE if are indicated correctly");
		EqCount = 0;
		for (int i=0; i<RateList.size();i++)
		{
			for (int j=0;j<siteRates.size();j++)
			{
				if (RateList.get(i).getCharCode().equals(siteRates.get(j).getCharCode()))
				{
					if (RateList.get(i).getStatus().equals(siteRates.get(j).getStatus()))
					{
						//System.out.println(RateList.get(i).getStatus()+" = "+siteRates.get(j).getStatus());
						EqCount++;
					}
					else
					{
						//System.out.println(RateList.get(i).getStatus()+" <> "+siteRates.get(j).getStatus());
					}
				}
			}
		}
		
		if (EqCount == RateList.size())
		{
			log.info("Correct Rates on SITE!");
		}
		else
		{
			log.info("Incorrect Rates on SITE!");
		}
		
		xmlT = null;
		xmlY = null;
		ExcelT = null;
		RateList = null;
		siteRates = null;
		siteTableList = null;
	    System.gc();

	}
		
	

    
}
