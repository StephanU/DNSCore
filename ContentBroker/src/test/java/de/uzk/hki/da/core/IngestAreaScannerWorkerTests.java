/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package de.uzk.hki.da.core;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

//import de.uzk.hki.da.db.BaseThreadDatabaseOperations;


/**
 * The Class IngestAreaScannerWorkerTests.
 */
public class IngestAreaScannerWorkerTests {

	/** The base path. */
	String basePath = "src/test/resources/core/IngestAreaScanner/";
	
	/** The worker. */
	IngestAreaScannerWorker worker = new IngestAreaScannerWorker();
	
	AbstractApplicationContext context;
	
	/**
	 * Sets the up.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Before
	public void setUp() throws IOException{
		
		context = new FileSystemXmlApplicationContext(basePath+"IngestAreaScanner.xml");
		
//		BaseThreadDatabaseOperations ops = mock(BaseThreadDatabaseOperations.class);
//		when(ops.getNumberOfInactiveJobs(anyString())).thenReturn(0);
		
		worker = context.getBean("ingestAreaScannerWorker", IngestAreaScannerWorker.class);
//		worker.setOps(ops);
	}
	
	/**
	 * Tear down.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@After
	public void tearDown() throws IOException{
		
		new File(basePath+"ingest/TEST/a.tgz").delete();
		new File(basePath+"work/TEST/a.tgz").delete();
		
		context.close();		
	}
	
	/**
	 * Replace.
	 */
	@Test 
	public void replace(){
		System.out.println(worker.convertMaskedSlashes("abcde%2Fslafj"));
	}	
	
}
