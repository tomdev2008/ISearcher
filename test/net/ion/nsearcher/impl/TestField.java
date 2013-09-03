package net.ion.nsearcher.impl;

import java.io.IOException;
import java.util.Date;

import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJob;
import net.ion.nsearcher.index.IndexSession;
import net.ion.nsearcher.index.Indexer;
import net.ion.nsearcher.search.Searcher;

import org.apache.lucene.search.NumericRangeFilter;

public class TestField extends ISTestCase{

	public void testKeyword() throws Exception {
		Central cen = CentralConfig.newRam().build() ;
		Indexer indexer = cen.newIndexer();
		indexer.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				WriteDocument doc = isession.newDocument().number("num", 2000).keyword("knum", "2000").unknown("date", new Date());
				isession.insertDocument(doc) ;
				return null;
			}
		}) ;
		
		Searcher searcher = cen.newSearcher();
		
		assertEquals(1, searcher.search("num:2000").getDocument().size()) ;
		assertEquals(1, searcher.search("knum:2000").getDocument().size()) ;
		cen.close() ;
	}
	
	public void testUnknownNumber() throws Exception {
		Central cen = writeDocument() ;
		
		Indexer indexer = cen.newIndexer() ;
		indexer.index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws IOException {
				WriteDocument doc = isession.newDocument() ;
				doc.add(MyField.keyword("name", "test"));
				doc.add(MyField.unknown("intkey", 123));
				isession.updateDocument(doc) ;
				return null ;
			}
		}) ;

		Searcher searcher = cen.newSearcher() ;
		searcher.andFilter(NumericRangeFilter.newLongRange("intkey", 8, 0L, 10000L, true, true)) ;
		assertEquals(1, searcher.createRequest("test").find().size()) ;
	}

	public void testUnknownDate() throws Exception {
		Central cen = writeDocument() ;
		
		Indexer indexer = cen.newIndexer() ;
		indexer.index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws Exception {
				WriteDocument doc = isession.newDocument() ;
				doc.add(MyField.keyword("name", "test"));
				doc.add(MyField.unknown("datekey", new Date()));
				
				isession.updateDocument(doc) ;
				return null;
			}
		}) ;

		Searcher searcher = cen.newSearcher() ;
//		searcher.andFilter(NumericRangeFilter.newLongRange("datekey", 8, 20100101L, 20111231L, true, true)) ;
		assertEquals(2, searcher.search("test").size()) ;
		
		
		// "date", 20100725, 232010))
		searcher.search("date:[\"20100725 16\" TO \"20100726 17\"]").debugPrint() ;
		//searcher.searchTest("date:\"20110530 164134\"").debugPrint(Page.ALL) ;
		
//		Date d = DateFormatUtil.getDateIfMatchedType("20110530-164134");
//		Calendar c = DateUtil.dateToCalendar(d);
//		int yyyymmdd = c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH) + 1) * 100 + c.get(Calendar.DATE);
//		int hh24miss = c.get(Calendar.HOUR) * 10000 + c.get(Calendar.MINUTE) * 100 + c.get(Calendar.SECOND);
//		Debug.line(hh24miss);
	}

}
