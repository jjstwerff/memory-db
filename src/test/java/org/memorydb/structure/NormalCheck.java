package org.memorydb.structure;

import org.junit.Before;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class NormalCheck {
	@Before
	public void init() {
		((Logger) LoggerFactory.getLogger("ROOT")).setLevel(Level.INFO);
		// ((Logger) LoggerFactory.getLogger(Store.class)).setLevel(Level.DEBUG);
	}
}
