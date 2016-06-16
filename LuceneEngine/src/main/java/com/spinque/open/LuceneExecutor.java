package com.spinque.open;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import com.spinque.open.blocks.LuceneBlock;

public class LuceneExecutor {

	public static void showResults(LuceneBlock b, int topN) throws Exception {
		final IndexSearcher s = b.getSearcher();
		TopDocs tds = b.getResults();
		
		/*
		 * We explicitely sort the results after the final search, as internal custom sort doesn't seem
		 * to work properly (see LuceneBlock.getResults())
		 */
        Arrays.sort(tds.scoreDocs, new Comparator<ScoreDoc>() {
			@Override
			public int compare(ScoreDoc sd1, ScoreDoc sd2) {
				try {
					return Double.compare(LuceneExecutor.getProbFromDocument(s.doc(sd2.doc)), 
						LuceneExecutor.getProbFromDocument(s.doc(sd1.doc)));
				} catch (IOException e) {
					// Something went wrong while retrieving the documents
					return 0;
				}
			}
        });

		System.out.println("Block [" + b.getName() + "]");
		System.out.println("Found " + tds.totalHits + " hits.");
		int i=0;
		for (ScoreDoc sd : tds.scoreDocs) {
			if (i >= topN)
				break;
		    int docId = sd.doc;
		    Document d = s.doc(docId);
		    String uid = d.get("_uid");
		    double prob = LuceneExecutor.getProbFromDocument(d);
		    System.out.println((i + 1) + ". ( " + prob + " | " + uid + " )");
		    int j = 0;
		    for (IndexableField f : d.getFields()) {
		    	if (j++ >= 10)
		    		break;
		    	if (!f.name().equals("_uid") && !f.name().equals("prob"))
		    		System.out.println(fieldAsString(f));
		    }
		    i++;
			System.out.println();
		}		
	}

	static String fieldAsString(IndexableField f) {
		Number num = f.numericValue();
		if (num != null) {
			return f.name() + ": " + num.toString();
		}
		String value = f.stringValue().length() > 128 ? f.stringValue().substring(0,128) + "..." : f.stringValue();
		return f.name() + ": " + value;
	}

	static public double getProbFromDocument(Document d) {
		IndexableField f = d.getField("prob");
		try {
			return f.numericValue().doubleValue();
		} catch (Exception e) {
			return 1d;
		}
	}

	static public void setDocumentProb(Document d, double prob) {
		d.removeFields("prob");
		addDocumentProb(d, prob);
	}

	static public void addDocumentProb(Document d, double prob) {
		d.add(new DoubleField("prob", prob, Field.Store.YES));
	}

}
