package com.swssm.waveloop;

import java.io.*;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;


public class GlobalOptions {
	public static int playbackSpeed		= 1000;		// 1.0배속
	
	public static int repeatCount		= 0;		// 0은 무제한 반복.
	public static int repeatDelayTime	= 100;		// 구간반복시 1회 재생후 대기시간.
	
	private final static String fileName = "waveloop_options.xml";
	public static void save( Context context )
	{
		BufferedWriter writer = null;
        try {
        	File xmlFile = context.getFileStreamPath(fileName);
        	if( xmlFile.exists() == false )
        		xmlFile.createNewFile();
        	if( xmlFile.canWrite() )
        	{
        		writer = new BufferedWriter( new FileWriter( xmlFile ) );
            	writer.write( writeXml() );
        	}
        	
        	
        }
        catch ( IOException e ) {
        }
        finally {
            try {
            	if ( writer != null )
            		writer.close( );
            }
            catch ( IOException e)
            {
            }
        }
        
	}
	
	private static String tag_options = "options";
	
	private static String tag_playback = "playback";
	private static String attr_speed = "speed";
	
	private static String tag_repeat = "repeat";
	private static String attr_count = "count";
	private static String attr_delay_time = "delay_time";
	
	
	private static String writeXml(){
	    XmlSerializer serializer = Xml.newSerializer();
	    StringWriter writer = new StringWriter();
	    try {
	        serializer.setOutput(writer);
	        serializer.startDocument("UTF-8", true);

	        serializer.startTag("", tag_options);
	        {
		        serializer.startTag("", tag_playback);
		        serializer.attribute("", attr_speed, String.valueOf(playbackSpeed) );
		        serializer.endTag("", tag_playback);
		        
		        serializer.startTag("", tag_repeat);
		        serializer.attribute("", attr_count, String.valueOf(repeatCount) );
		        serializer.attribute("", attr_delay_time, String.valueOf(repeatDelayTime) );
		        serializer.endTag("", tag_repeat);
	        }
		    serializer.endTag("", tag_options);
	        

	        serializer.endDocument();
	        
	    } catch (Exception e) {
	        //throw new RuntimeException(e);
	    }
	    return writer.toString();
	}
	
	
	
	public static void load( Context context )
	{
		
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser parser = factory.newPullParser();
			
			//context.openFileInput(name);
			File xmlFile = context.getFileStreamPath(fileName);
			if( xmlFile.exists() && xmlFile.canRead() )
			{
				FileInputStream stream = new FileInputStream(xmlFile);
				parser.setInput(stream, "UTF-8");
				
				int eventType = parser.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch(eventType) {
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.END_DOCUMENT:
						break;
						
					case XmlPullParser.START_TAG:
						if( parser.getName().toLowerCase().equals(tag_playback) )
						{
							String speed = parser.getAttributeValue("", attr_speed);
							playbackSpeed = Integer.parseInt(speed);
						}
						else if( parser.getName().toLowerCase().equals(tag_repeat) )
						{
							String count = parser.getAttributeValue("", attr_count);
							String delay_time = parser.getAttributeValue("", attr_delay_time);
							
							repeatCount = Integer.parseInt(count);
							repeatDelayTime = Integer.parseInt(delay_time);
						}
						break;
					case XmlPullParser.END_TAG:
						break;
						
					case XmlPullParser.TEXT:
						break;
						
					}
					eventType = parser.next();
				}
				
			}
			else
			{
				save(context);
				return;
			}

		}catch(Exception e) {
			
		}

		
	}
	
	
}
