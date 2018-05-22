package org.jboss.ddoyle.drools.cep.demo.model;

import java.util.Date;

/**
 * 
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 */
public class AbstractFact implements Fact {

	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private final String id;
	
	public AbstractFact(String id) {
		this.id = id;
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public Date getTimestamp(){
		return null;
	}

}
