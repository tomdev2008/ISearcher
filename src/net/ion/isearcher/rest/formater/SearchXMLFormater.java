package net.ion.isearcher.rest.formater;

import java.io.IOException;
import java.util.List;

import net.ion.framework.rest.RopeRepresentation;
import net.ion.framework.rope.Rope;
import net.ion.framework.rope.RopeWriter;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.searcher.ISearchRequest;
import net.ion.isearcher.searcher.ISearchResponse;

import org.apache.ecs.xml.XML;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;

public class SearchXMLFormater extends AbstractDocumentFormater implements SearchResponseFormater {
	public final static String XML_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";

	public Representation toRepresentation(ISearchResponse iresponse) throws IOException {

		ISearchRequest irequest = iresponse.getRequest();
		RopeWriter rw = toXMLString(irequest, iresponse);

		Representation result = new RopeRepresentation(rw.getRope(), MediaType.APPLICATION_XML);
		result.setCharacterSet(CharacterSet.UTF_8);
		return result;
	}

	private RopeWriter toXMLString(ISearchRequest irequest, ISearchResponse iresponse) throws CorruptIndexException, IOException {

		XML result = new XML("result");

		result.addElement(irequest.toXML());
		result.addElement(iresponse.toXML());
		XML nodes = new XML("nodes");
		appendChild(nodes, iresponse.getDocument());
		result.addElement(nodes);

		RopeWriter rw = new RopeWriter();
		rw.write(XML_HEADER);
		result.output(rw);
		return rw;
	}

	private void appendChild(XML nodes, List<MyDocument> docs) throws IOException {

		for (MyDocument doc : docs) {
			XML node = new XML("node");
			List<Fieldable> fields = doc.getFields();
			for (Fieldable field : fields) {
				XML property = new XML("property");
				property.addAttribute("name", field.name());
				property.addAttribute("stored", field.isStored());
				property.addAttribute("tokenized", field.isTokenized());
				property.addAttribute("indexed", field.isIndexed());
				property.addAttribute("binary", field.isBinary());
				property.addElement("<![CDATA[" + field.stringValue() + "]]>");
				node.addElement(property);
			}
			nodes.addElement(node);
		}

	}

	public Rope toRope(List<MyDocument> docs) throws IOException {
		XML result = new XML("result");

		XML nodes = new XML("nodes");
		result.addElement(new XML("request"));
		result.addElement(new XML("response"));
		appendChild(nodes, docs);
		result.addElement("nodes", nodes);

		RopeWriter rw = new RopeWriter();
		rw.write(XML_HEADER);
		result.output(rw);
		return rw.getRope();

	}

	public MediaType getMediaType() {
		return MediaType.APPLICATION_XML;
	}
}