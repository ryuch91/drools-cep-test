package org.jboss.ddoyle.drools.cep.demo;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.drools.core.ClockType;
import org.drools.core.time.SessionPseudoClock;
import org.jboss.ddoyle.drools.cep.demo.model.Event;
import org.jboss.ddoyle.drools.cep.demo.model.Fact;
import org.jboss.ddoyle.drools.cep.demo.model.SensorFact;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.time.SessionClock;
import org.kie.internal.builder.conf.RuleEngineOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 */
public class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	private static final String EVENTS_CSV_FILE = "SensorData.csv";

	//private static final String CEP_STREAM = "AirportStream";
	//private static final String CO2_STREAM = "CO2DataStream";
	//private static final String HUMIDITY_STREAM = "HumidityDataStream";
	//private static final String IRRADIANCE_STREAM = "IrradianceDataStream";
	//private static final String LIGHT_STREAM = "LightDataStream";
	//private static final String RAIN_STREAM = "RainDataStream";
	//private static final String TEMP_STREAM = "TemperatureDataStream";
	//private static final String WIND_STREAM = "WindDataStream";
	private static final String CEP_STREAM = "SensorStream";
	
	public static void main(String[] args) {
		LOGGER.info("Initialize KIE.");

		KieServices kieServices = KieServices.Factory.get();
		
		// Select RETE algorithm rather than PHREAK
		KieBaseConfiguration kbConf = kieServices.newKieBaseConfiguration();
		kbConf.setOption( RuleEngineOption.RETEOO );
		
		// Load KieContainer from resources on class-path (i.e. kmodule.xml and rules).
		KieContainer kieContainer = kieServices.getKieClasspathContainer();
		KieBase kieBase = kieContainer.newKieBase(kbConf);
		
		// Initializing KieSession.
		LOGGER.info("Creating KieSession.");
		//KieSession kieSession = kieContainer.newKieSession();
		//KieSessionConfiguration ksConf = kieServices.newKieSessionConfiguration();
		//ksConf.setOption( ClockTypeOption.get(ClockType.PSEUDO_CLOCK.getId()));
		
		KieSessionConfiguration ksConf = kieServices.newKieSessionConfiguration();
		ksConf.setOption( ClockTypeOption.get(ClockType.PSEUDO_CLOCK.getId()));
		KieSession kieSession = kieBase.newKieSession(ksConf, null);
		
		KieRuntimeLogger logger = kieServices.getLoggers().newThreadedFileLogger(kieSession, "./log", 10);
		
		//LOGGER.info();
		
		try {
			//Load the facts/events from our CSV file.
			InputStream factsInputStream = Main.class.getClassLoader().getResourceAsStream(EVENTS_CSV_FILE);
			List<Fact> facts = FactsLoader.loadFacts(factsInputStream);
			
			for (Fact nextFact: facts) {
				//Insert the event into the session
				insert(kieSession, CEP_STREAM, nextFact);
				//And now, fire the rules. In a real application, you probably want to batch the inserts instead of firing rules after every insert.
				kieSession.fireAllRules();
			}
		
		} finally {
			/*
			 * Disposing session here as this is just a demo. If we would run a real CEP application, we would leave the session open and
			 * continuously insert new incoming events into the session.
			 */
			LOGGER.info("Disposing session.");
			kieSession.dispose();
		}
	}

	/**
	 * Inserts the {@link Fact} into a given <code>Drools Fusion Stream</code> of the {@link KieSession} and advances the PseudoClock to the
	 * time of the event.
	 * 
	 * @param kieSession
	 * @param stream
	 * @param fact
	 * @return
	 */
	private static FactHandle insert(KieSession kieSession, String stream, Fact fact) {
		//LOGGER.info("Inserting fact with id: '" + fact.getId() + "' into stream: " + stream);
		SessionClock clock = kieSession.getSessionClock();
		if (!(clock instanceof SessionPseudoClock)) {
			String errorMessage = "This fact inserter can only be used with KieSessions that use a SessionPseudoClock";
			LOGGER.error(errorMessage);
			throw new IllegalStateException(errorMessage);
		}
		SessionPseudoClock pseudoClock = (SessionPseudoClock) clock;
		EntryPoint ep = kieSession.getEntryPoint(stream);

		// First insert the fact
		//LOGGER.debug("Inserting fact: " + fact.toString());
		FactHandle factHandle = ep.insert(fact);

		// And then advance the clock
		// We only need to advance the time when dealing with Events. Our facts don't have timestamps.
		if (fact instanceof SensorFact){
			long advanceTime = fact.getTimestamp().getTime() - pseudoClock.getCurrentTime();
			if (advanceTime > 0) {
				//LOGGER.info("Advancing the PseudoClock with " + advanceTime + " milliseconds.");
				pseudoClock.advanceTime(advanceTime, TimeUnit.MILLISECONDS);
			} else {
				LOGGER.info("Not advancing time. Fact timestamp is '" + fact.getTimestamp().getTime()
						+ "', PseudoClock timestamp is '" + pseudoClock.getCurrentTime() + "'.");
			}
		}
		return factHandle;
	}

}
