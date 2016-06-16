package com.spinque.open.indexer;

import java.io.IOException;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.spatial.geopoint.document.GeoPointField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.spinque.open.blocks.LuceneUtils;
import com.spinque.open.indexer.LuceneIndexMapping.Destination;

public class LuceneIndexer {

	private static final DateFormat DATEFORMATTER = new SimpleDateFormat("yyyyMMdd");
	
	private final LuceneIndexMapping _mapping;
	private final Directory _index;
	private final IndexWriter _w;

	public LuceneIndexer(LuceneIndexMapping mapping, Path indexLocation) throws IOException {
		_mapping = mapping;
		StandardAnalyzer analyzer = new StandardAnalyzer();
		_index = indexLocation == null ? new RAMDirectory() : new SimpleFSDirectory(indexLocation);
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		_w = new IndexWriter(_index, config);
	}
	
	void indexDocument(Element documentElem) throws IOException {
		String id = constructID(documentElem);
		
		IndexReader r = DirectoryReader.open(_w);
		Set<String> createdDocs = new HashSet<String>();
		try {
			Document doc = createNewDocInstance(createdDocs, r, id);
			if (doc == null) {
//				System.err.println("already in index");
				return;
			}
			doc.add(new StringField("_type", _mapping.getRootType(), Field.Store.YES));
			
			for (Entry<String, Destination> entry : _mapping.getMapping().entrySet()) {
				try {
					switch (entry.getValue()) {
					case TextField: {
						String value = getElemTextContent(documentElem, entry.getKey());
						if (value != null && !value.isEmpty()) {
							doc.add(new TextField(entry.getKey(), value, Field.Store.YES));
						}
					} break;
					case StringField: {
						String value = getElemTextContent(documentElem, entry.getKey());
						if (value != null && !value.isEmpty() && value.length() < 4096) {
							doc.add(new StringField(entry.getKey(), value, Field.Store.YES));
						}
					} break;
					case IntegerField: {
						String value = getElemTextContent(documentElem, entry.getKey());
						if (value != null && !value.isEmpty() && !value.equals("null")) {
							doc.add(new LongField(entry.getKey(), Long.parseLong(value), Field.Store.YES));
						}
					} break;
					case DateField: {
						String value = getElemTextContent(documentElem, entry.getKey());
						if (value != null && !value.isEmpty() && !value.equals("null"))
							doc.add(new LongField(entry.getKey(), DATEFORMATTER.parse(value).getTime(), Field.Store.YES));
					} break;
					case RelationSplitByComma: {
						String valueList = getElemTextContent(documentElem, entry.getKey());
						if (valueList != null) {
							for (String value : valueList.split("\\s*,\\s*")) {
								String relID = generateID(value);
								doc.add(new StringField(entry.getKey(), relID, Field.Store.YES));	
								Document relDoc = getDocInstance(_w, relID);
								if (relDoc == null) {
									/* if document is not present, create it, and give it a label and type */
									relDoc = newDocInstance(relID);
									String type = _mapping.getType(entry.getKey());
									relDoc.add(new StringField("_type", type, Field.Store.YES));
									relDoc.add(new TextField("label", value, Field.Store.YES));
								}
								/* always insert the reverse relation */
								relDoc.add(new StringField("rev_" + entry.getKey(), id, Field.Store.YES));
								_w.addDocument(relDoc);
							}
						}
					} break;
					case GeoPoint: {
						String fieldName = entry.getKey();
						String[] latLonFieldNames = _mapping.getGeoPoint(fieldName).split("\\s* \\s*");
						double lat = Double.parseDouble(getElemTextContent(documentElem, latLonFieldNames[0]));
						double lon = Double.parseDouble(getElemTextContent(documentElem, latLonFieldNames[1]));
						doc.add(new GeoPointField(entry.getKey(), lon,lat, Field.Store.YES));
					} break;
					case ID:
						/* ignore */
					case Ignore:
						/* ignore */
					default:
						break;
					}
				} catch (NumberFormatException e) {
					System.out.println("Failed to parse: " + e.getMessage());
				} catch (ParseException e) {
					System.out.println("Failed to parse: " + e.getMessage());
				} catch (NullPointerException e) {
					/* ignore */
				}
			}
			_w.addDocument(doc);
		} finally {
			r.close();
		}
	}

	/**
	 * @return textContent of first child-element with given tagName  
	 */
	private String getElemTextContent(Element elem, String tagName) {
		NodeList nl = elem.getElementsByTagName(tagName);
		if (nl.getLength() == 0)
			return null;
		return nl.item(0).getTextContent();
	}

	private static Document newDocInstance(String uid) {
		Document doc = new Document();
		doc.add(new StringField("_uid", uid, Field.Store.YES));
		return doc;
	}
	
	private String generateID(String value) {
		return "uid." + value.toLowerCase();
	}

	private String constructID(Element documentElem) {
		String id = "uid";
		for (String item : _mapping.getIDFields()) {
			NodeList nl = documentElem.getElementsByTagName(item);
			for (int i = 0; i < nl.getLength(); i++) {
				id += "." + ((Element) nl.item(i)).getTextContent();
			}
		}
		return id.toLowerCase();
	}
	
	public static Document createNewDocInstance(Set<String> alreadyCreatedDocs, IndexReader r, String uid) throws IOException {
		if (!alreadyCreatedDocs.add(uid)) {
			return null;
		}
		IndexSearcher s = new IndexSearcher(r);
		TopDocs td = s.search(new TermQuery(new Term("_uid", uid)), 1);
		if (td.totalHits == 0) {
			return newDocInstance(uid);
		}
		return null;
	}

	public static Document getDocInstance(IndexWriter w, String uid) throws IOException {
		IndexReader r = DirectoryReader.open(w);
		try {
			return getDocInstance(r, w, uid);
		} finally {
			r.close();
		}
	}

	public static Document getDocInstance(IndexReader r, IndexWriter w, String uid) throws IOException {
		IndexSearcher s = new IndexSearcher(r);
		TopDocs td = s.search(new TermQuery(new Term("_uid", uid)), 1);
		
		// Not found, return a new document with the given uid
		if (td.totalHits == 0) {
			return null;
		}
		
		// Found, update it
		Document doc = s.doc(td.scoreDocs[0].doc);
		doc = LuceneUtils.fixLUCENE7171(doc);
		
		// remove old document
		w.deleteDocuments(new TermQuery(new Term("_uid",uid)));
		return doc;
	}
	
	void close() throws IOException {
		_w.close();
	}
}
