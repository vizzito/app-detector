package org.detector.services;

import detector.AntipatternDetector;
import detector.antipattern.Antipattern;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.Gson;


/**
 * 
 */
@Controller
@EnableAutoConfiguration
@RequestMapping("/detector")
public class ApDetectorService
{
	@RequestMapping(value = "check", method = RequestMethod.GET)
	protected void checkService(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
			IOException
	{
		final PrintWriter out = response.getWriter();
		out.println("Service Running!");
	}

	@RequestMapping(value = "ap-detector", method = RequestMethod.POST)
	protected void detectorService(final HttpServletRequest request, final HttpServletResponse response) throws IOException
	{
		//		request.getParameter("files[]");
		final String[] paramValues = request.getParameterValues("files[]");


		//	final MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
		//		String str_request = IOUtils.toString(request.getInputStream());
		//		str_request = str_request + "&";
		//		final String regex = "\\=([-0-9a-zA-Z.+_]*)\\&";
		//		final Pattern p = Pattern.compile(regex);
		//		final Matcher m = p.matcher(str_request);
		//		final ArrayList<String> files = new ArrayList<String>();
		//		while (m.find())
		//		{
		//			files.add(m.group(1));
		//		}

		final AntipatternDetector s = new AntipatternDetector();
		final ArrayList<HashMap<String, Object>> antiPatterns = new ArrayList<HashMap<String, Object>>();
		try
		{
			for (final String url : paramValues)
			{
				s.setWsdlUrl(new URL("file:/tmp/" + url));
				final HashMap<String, Object> map = new HashMap<>();
				final Antipattern[] result = s.analyze();
				map.put("fileName", url);
				map.put("antiPatterns", result);
				antiPatterns.add(map);
			}

		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		response.setStatus(200);
		final Gson g = new Gson();
		final String jsonResult = g.toJson(antiPatterns);
		final PrintWriter out = response.getWriter();
		out.println(jsonResult);
	}
}
