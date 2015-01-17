package org.detector.services;


import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;

import detector.AntipatternDetector;
import detector.antipattern.Antipattern;

public class ApDetectorService extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static String DIRFILES;
	public ApDetectorService() {
		loadPropertyFile();
		
	}
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
	}
	
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
 		String str_request = IOUtils.toString(request.getInputStream());
		str_request = str_request+"&";
		String regex  = "\\=([-0-9a-zA-Z.+_]*)\\&";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str_request);
		ArrayList<String> files = new ArrayList<String>();
		 while (m.find()) {
			 files.add(m.group(1));
			 }
		
		AntipatternDetector s = new AntipatternDetector();
		ArrayList<HashMap<String, Object>> antiPatterns = new ArrayList<HashMap<String, Object>>();
		try {
			for(String url : files){
			s.setWsdlUrl(new URL("file:" + DIRFILES + "/"+ url));
			HashMap<String, Object> map = new HashMap<>();
			Antipattern[] result = s.analyze();
			map.put("fileName", url);		
			map.put("antiPatterns", result);
			antiPatterns.add(map);
			}	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		Gson g = new Gson();
		String jsonResult = g.toJson(antiPatterns);
		PrintWriter out = response.getWriter();
		out.println(jsonResult);
	}
	
	private void loadPropertyFile() {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			String filename = "config.properties";
    		input = ApDetectorService.class.getClassLoader().getResourceAsStream(filename);
    		if(input==null){
    	            System.out.println("Sorry, unable to find " + filename);
    		    return;
    		}
    		prop.load(input);
			DIRFILES = prop.getProperty("tomcat.dir");
			System.out.println("DIRFILES:DETECTOR::"+DIRFILES);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
