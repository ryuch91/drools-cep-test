package org.jboss.ddoyle.drools.cep.demo.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Base interface for all our <code>facts</code>
 * 
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 */
public interface Fact extends Serializable {

	public abstract String getId();
	public abstract Date getTimestamp();
}
