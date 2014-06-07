package detector;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;

import detector.antipattern.Antipattern;

public class ApDetectorService extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String DIRFILES = "/home/panther/tomcat/apache-tomcat-7.0.52/webapps/ServiceClusterer";

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		out.println("capo total!");
		
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
		HashMap<String, Object> map = new HashMap<>();
		AntipatternDetector s = new AntipatternDetector();
		ArrayList<HashMap<String, Object>> antiPatterns = new ArrayList<HashMap<String, Object>>();
		try {
			for(String url : files){
			s.setWsdlUrl(new URL("file:" + DIRFILES + "/"+ url));
			
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
}
