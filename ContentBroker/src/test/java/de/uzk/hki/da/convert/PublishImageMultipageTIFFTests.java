package de.uzk.hki.da.convert;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.ConversionRoutine;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.utils.TESTHelper;

public class PublishImageMultipageTIFFTests {

	String basePath="src/test/resources/convert/PublishImageMultipageTiffTests/";
	
	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMultipage() throws FileNotFoundException {
		PublishImageConversionStrategy cs = new PublishImageConversionStrategy();
		Object o = TESTHelper.setUpObject("123",basePath);
		
		CLIConnector cli = mock ( CLIConnector.class );		
		when(cli.execute((String[]) anyObject())).thenReturn(true);
		
		cs.setCLIConnector(cli);
		DAFile sourceFile = new DAFile(o.getLatestPackage(),"source","ALVR_Nr_4557_Aufn_249.tif");
		
		ConversionInstruction ci = new ConversionInstruction();
		ci.setSource_file(sourceFile);
		ci.setTarget_folder("");
		
		ConversionRoutine cr = new ConversionRoutine();
		cr.setTarget_suffix("jpg");
		ci.setConversion_routine(cr);
		
		cs.setObject(o);
		List<Event> events = cs.convertFile(ci);
		assertTrue(events.isEmpty());
	}
	
}
