package com.TestData.dto;

public class Rate
{
private String Status;
private String CharCode;
public String getStatus()
{
	return Status;
}
public void setStatus(String status)
{
	Status = status;
}
public String getCharCode()
{
	return CharCode;
}
public void setCharCode(String charCode)
{
	CharCode = charCode;
}
public String toString()
{
	return "\nCharCode=" + CharCode + " Status=" + Status;
}
}
