package com.etrust.stategrid.utils;

public class EquipTemplate {
	public static String htmlTemplate=null;
	public static String initHtmlTemplate(){
		StringBuilder sb=new StringBuilder();
		sb.append("<!DOCTYPE html>\n");
		sb.append("<html>\n");
		sb.append("  <head>\n");
		sb.append("    <title>设备详情</title>\n");
		sb.append("    <style>\n");
		sb.append("   	table{\n");
		sb.append("		border:1px solid #ccc;\n");
		sb.append("		font-family:微软雅黑, 宋体, Arial, Helvetica, sans-serif, simsun;\n");
		sb.append("		font-size:16px;\n");
		sb.append("		border-collapse:collapse;\n");
		sb.append("	 }\n");
		sb.append("	td{\n");
		sb.append("      border:1px solid #ccc;\n");
		sb.append("       padding:5px;\n");
		sb.append("      height:30px;\n");
		sb.append("	}\n");
		sb.append("	tr{\n");
		sb.append("	    border:1px solid #ccc;\n");
		sb.append("	}\n");
		sb.append("   </style>\n");
		sb.append("</head>\n");
		sb.append("<body>\n");
		sb.append("	     <table>\n");
		sb.append("	         <tr><td align='right' width='100'>变电站：</td><td width='350'>device_ts_name</td></tr>\n");
		sb.append("	         <tr><td  align='right'>设备名称：</td><td>device_equipName</td></tr>\n");
		sb.append("	         <tr><td  align='right'>设备型号：</td><td>device_equipNo</td></tr>\n");
		sb.append("	         <tr><td  align='right'>设备编号：</td><td>device_equipBh</td></tr>\n");
		sb.append("	         <tr><td align='right'>设备类别：</td><td>device_name</td></tr>\n");
		sb.append("       </table>\n");
		sb.append("  </body>\n");
		sb.append("</html>");
		return sb.toString();
	}
	public static String getHtmlTemplate(){
		if(htmlTemplate==null){
			htmlTemplate=initHtmlTemplate();
		}
		return htmlTemplate;
	}
}
