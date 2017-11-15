package com.TestData.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class Valute
{
	@XStreamAsAttribute
	@XStreamAlias("ID") // 
	private String ID;
	
	@XStreamAlias("NumCode") // 
	private String NumCode;

	@XStreamAlias("CharCode") // 
	private String CharCode;

	@XStreamAlias("Nominal") // 
	private String Nominal;

	@XStreamAlias("Name") // 
	private String Name;

	@XStreamAlias("Value") // 
	private float Value;
	

	public String getID()
	{
		return ID;
	}

	public void setId(String id)
	{
		ID = id;
	}

	public String getNumCode()
	{
		return NumCode;
	}

	public void setNumCode(String numCode)
	{
		NumCode = numCode;
	}

	public String getCharCode()
	{
		return CharCode;
	}

	public void setCharCode(String charCode)
	{
		CharCode = charCode;
	}

	public String getNominal()
	{
		return Nominal;
	}

	public void setNominal(String nominal)
	{
		Nominal = nominal;
	}

	public String getName()
	{
		return Name;
	}

	public void setName(String name)
	{
		Name = name;
	}

	public float getValue()
	{
		return Value;
	}

	public void setValue(float value)
	{
		Value = value;
	}

	@Override
	public String toString() 
	{
		return "\nValute ID=" + ID + "; NumCode=" + NumCode + "; CharCode=" + CharCode + "; Nominal=" + Nominal + "; Name=" + Name + "; Value=" + Value + "";
	}

	
}
