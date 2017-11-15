package com.TestData.dto;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;


@XStreamAlias("ValCurs") // 
public class ValCurs
{
	@XStreamAsAttribute
	@XStreamAlias("Date") // 
	private String Date ;

	@XStreamAsAttribute
	@XStreamAlias("Name") // 
	private String Name ;
	
	//@XStreamAlias("Valute") 
	@XStreamImplicit(itemFieldName="Valute")
	private List<Valute> Valute ;
	

	public String getDate()
	{
		return Date;
	}

	public void setDate(String date)
	{
		Date = date;
	}

	public String getName()
	{
		return Name;
	}

	public void setName(String name)
	{
		Name = name;
	}

	public List<Valute> getValute()
	{
		return Valute;
	}

	public void setValute(List<Valute> Valute)

	{
		this.Valute = Valute;
	}

	@Override
	public String toString() 
	{
		return "\nValCurs Name=" + Name + "; Date=" + Date + Valute;
	}
}
