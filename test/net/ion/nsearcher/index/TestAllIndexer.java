package net.ion.nsearcher.index;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.ion.nsearcher.common.TestCaseInsensitiveInReadDocument;
import net.ion.nsearcher.common.TestMyDocument;
import net.ion.nsearcher.common.TestWriteDocument;
import net.ion.nsearcher.config.TestFieldIndexingStrategy;
import net.ion.nsearcher.index.channel.TestDocument;
import net.ion.nsearcher.index.channel.TestStackFile;

public class TestAllIndexer {

	public static Test suite(){
		TestSuite ts = new TestSuite("All Indexer") ;
		
		ts.addTestSuite(TestPolicy.class) ;
		ts.addTestSuite(TestRollback.class) ;
		ts.addTestSuite(TestDocument.class) ;
		ts.addTestSuite(TestMyDocument.class) ;
		ts.addTestSuite(TestCaseInsensitiveInReadDocument.class) ;
		ts.addTestSuite(TestWriteDocument.class) ;
		ts.addTestSuite(TestStackFile.class) ;
		ts.addTestSuite(TestIndexer.class) ;
		ts.addTestSuite(TestIndexSession.class) ;
		
		ts.addTestSuite(TestFieldIndexingStrategy.class) ;
		
		return ts ;
	} 
}
