package org.jboss.ddoyle.drools.cep.demo.model;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Event fired when a bag is scanned.
 * 
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 */
public class SensorFact extends AbstractFact {

	/**
	 * SerialVersionUID. 
	 */
	private static final long serialVersionUID = 1L;
	private float co2, humidity, irradiance, light, rain, temperature, wind;
	private Date timestamp;
	
	public SensorFact(float temperature, float co2, float humidity, float light, float rain, float wind, float irradiance) {
		this(temperature, co2, humidity, light, rain, wind, irradiance, new Date());
	}
	
	public SensorFact(float temperature, float co2, float humidity, float light, float rain, float wind, float irradiance, Date eventTimestamp) {
		this(UUID.randomUUID().toString(), temperature, co2, humidity, light, rain, wind, irradiance, eventTimestamp);
	}
	
	public SensorFact(String id, float temperature, float co2, float humidity, float light, float rain, float wind, float irradiance, Date eventTimestamp) {
		super(id);
		this.co2 = co2;
		this.humidity = humidity;
		this.irradiance = irradiance;
		this.light = light;
		this.rain = rain;
		this.temperature = temperature;
		this.wind = wind;
		this.timestamp = eventTimestamp;
	}

	public float getTemperature() {
		return temperature;
	}
	
	public float getCO2() {
		return co2;
	}
	
	public float getHumidity() {
		return humidity;
	}
	
	public float getlight() {
		return light;
	}
	
	public float getRain() {
		return rain;
	}
	
	public float getWind() {
		return wind;
	}
	
	public float getIrradiance() {
		return irradiance;
	}

	public Date getTimestamp(){
		return timestamp;
	}
	
	public void setTimestamp(Date timestamp){
		this.timestamp = timestamp;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("co2", co2).append("humidity", humidity).append("irradiance",irradiance).append("light",light).append("rain",rain).append("temperature",temperature).append("wind",wind).append("timestamp", timestamp).toString();
	}
	
}
