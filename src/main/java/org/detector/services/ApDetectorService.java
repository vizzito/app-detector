package org.detector.services;

import detector.AntipatternDetector;
import detector.antipattern.Antipattern;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.gson.Gson;


/**
 * 
 */
@Controller
@EnableAutoConfiguration
@RequestMapping("/detector")
public class ApDetectorService
{
	private List<MultipartFile> listFiles;
	public AntipatternDetector antiPatternDetector = null;

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
		listFiles = new ArrayList<MultipartFile>();
		final MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
		final Iterator<String> itr = mRequest.getFileNames();
		final ArrayList<String> paramValues = new ArrayList<String>();
		while (itr.hasNext())
		{
			final MultipartFile mFile = mRequest.getFile(itr.next());
			final String fileName = mFile.getOriginalFilename();
			mFile.transferTo(new File("/tmp/" + fileName));
			paramValues.add(fileName);
		}


		antiPatternDetector = new AntipatternDetector();
		final ArrayList<HashMap<String, Object>> antiPatterns = new ArrayList<HashMap<String, Object>>();
		try
		{
			for (final String url : paramValues)
			{
				antiPatternDetector.setWsdlUrl(new URL("file:/tmp/" + url));
				final HashMap<String, Object> map = new HashMap<>();
				final Antipattern[] result = antiPatternDetector.analyze();
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
