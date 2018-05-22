package org.jboss.ddoyle.drools.cep.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.ddoyle.drools.cep.demo.model.BagScannedEvent;
import org.jboss.ddoyle.drools.cep.demo.model.BagTag;
import org.jboss.ddoyle.drools.cep.demo.model.Event;
import org.jboss.ddoyle.drools.cep.demo.model.Fact;
import org.jboss.ddoyle.drools.cep.demo.model.Location;
import org.jboss.ddoyle.drools.cep.demo.model.SensorFact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads {@link Fact objects from the given (CSV) file. 
 * 
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 */
public class FactsLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(FactsLoader.class);

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
	private static final DateFormat DATETIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy/HH:mm");
	
	public static List<Fact> loadFacts(File eventsFile) {

		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(eventsFile));
		} catch (FileNotFoundException fnfe) {
			String message = "File not found.";
			LOGGER.error(message, fnfe);
			throw new IllegalArgumentException(message, fnfe);
		}
		return loadFacts(br);
		
	}
	
	public static List<Fact> loadFacts(InputStream eventsInputStream) {
		BufferedReader br = new BufferedReader(new InputStreamReader(eventsInputStream));
		return loadFacts(br);
		
	}
	
	private static List<Fact> loadFacts(BufferedReader reader) {
		List<Fact> factList = new ArrayList<Fact>();
		try {
			String nextLine;
			while ((nextLine = reader.readLine()) != null) {
				if (!nextLine.startsWith("#")) {
					Fact SensorFact = readFact(nextLine);
					if (SensorFact != null) {
						factList.add(SensorFact);
					}
				}
			}
		} catch (IOException ioe) {
			throw new RuntimeException("Got an IO exception while reading events.", ioe);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ioe) {
					// Swallowing exception, not much we can do here.
					LOGGER.error("Unable to close reader.", ioe);
				}
			}
		}
		return factList;
	}

	/**
	 * Layout of a StockTick line has to be {uuid}, {timestamp}, {symbol}, {price}.
	 * 
	 * @param line
	 *            the line to parse.
	 * @return the {@link Fact}
	 */
	private static Fact readFact(String line) {
		String[] eventData = line.split(",");
		if (eventData.length != 9) {
			LOGGER.error("Unable to parse string: " + line);
		}
		SensorFact sensorFact = null;
		try {
			Date date = DATE_FORMAT.parse(eventData[0].trim());
			Date time = TIME_FORMAT.parse(eventData[1].trim());
			Date datetime = DATETIME_FORMAT.parse(eventData[0].trim() + "/" + eventData[1].trim());
			float temperature = Float.parseFloat(eventData[2].trim());
			float co2 = Float.parseFloat(eventData[3].trim());
			float humidity = Float.parseFloat(eventData[4].trim());
			float light = Float.parseFloat(eventData[5].trim());
			float rain = Float.parseFloat(eventData[6].trim());
			float wind = Float.parseFloat(eventData[7].trim());
			float irradiance = Float.parseFloat(eventData[8].trim());
			
			sensorFact = new SensorFact(temperature, co2, humidity, light, rain, wind, irradiance, datetime);
			//BagTag tag = new BagTag(eventData[1].trim());
			//event = new BagScannedEvent(eventData[0], tag, Location.valueOf(eventData[2].trim()), DATE_FORMAT.parse(eventData[3].trim()));
			
		} catch (NumberFormatException nfe) {
			LOGGER.error("Error parsing line: " + line, nfe);
		} catch (ParseException pe) {
			LOGGER.error("Error parsing line: " + line, pe);
		}
		return sensorFact;

	}

}
