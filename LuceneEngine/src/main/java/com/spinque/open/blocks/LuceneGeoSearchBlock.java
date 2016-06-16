package com.spinque.open.blocks;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.spatial.geopoint.document.GeoPointField;
import org.apache.lucene.spatial.geopoint.search.GeoPointDistanceQuery;
import org.apache.lucene.util.SloppyMath;
import org.w3c.dom.Element;

import com.spinque.open.LuceneExecutor;


public class LuceneGeoSearchBlock extends LuceneBlock {

	private String _sourceGeoLocationField = null;
	private String _refGeoLocationField = null;
	private double _radiusMeters;
	private double _slope; 

	public LuceneGeoSearchBlock() {
		setResultAggregationType(ResultAggregationType.MAX);
		setRadiusMeters(200 * 1000d);
		setSlope(1d);
	}
	
	public double getSlope() {
		return _slope;
	}

	public void setSlope(double slope) {
		_slope = slope;
	}

	public double getRadiusMeters() {
		return _radiusMeters;
	}
	
	public void setRadiusMeters(double radiusMeters) {
		_radiusMeters = radiusMeters;
	}
	
	public String getSourceGeoLocationField() {
		return _sourceGeoLocationField;
	}
	
	public String getRefGeoLocationField() {
		return _refGeoLocationField;
	}

	public void setSourceGeoLocationField(String sourceGeoLocationField) {
		if (!sourceGeoLocationField.equals(_sourceGeoLocationField)) {
			_sourceGeoLocationField = sourceGeoLocationField;
			_resultStore.setIsUpToDate(false);
		}
	}

	public void setRefGeoLocationField(String refGeoLocationField) {
		if (!refGeoLocationField.equals(_refGeoLocationField)) {
			_refGeoLocationField = refGeoLocationField;
			_resultStore.setIsUpToDate(false);
		}
	}



	@Override
	public void computeAndStoreResults() throws Exception {
		IndexSearcher sourceSearcher = getSourceBlock().getSearcher();
    	IndexSearcher refSearcher = getRefBlock().getSearcher();
		
		// Nested-loop Join
		// Rank every point Source with its distance to every point in Ref 
		// NB: GeoPointDistanceQuery can only filter on max distance, it does not rank.
		// So after the filter we recompute the distance to assign probabilities
		TopDocs refResults = getRefBlock().getResults();
		for (ScoreDoc rsd : refResults.scoreDocs) {
		    Document rd = refSearcher.doc(rsd.doc);

		    try {
			    GeoPointField refGeoPoint = new GeoPointField("_tmp",0,0, Field.Store.NO);
		    	Long refV = rd.getField(_refGeoLocationField).numericValue().longValue();
			    refGeoPoint.setLongValue(refV);
			     
			    GeoPointDistanceQuery query = new GeoPointDistanceQuery(_sourceGeoLocationField, 
			    		refGeoPoint.getLon(), refGeoPoint.getLat(), _radiusMeters);
			     
			    TopDocs sourceResults = sourceSearcher.search(query, MAX_RESULTS);
			    GeoPointField sourceGeoPoint = new GeoPointField("_tmp",0,0, Field.Store.NO);
			    double refProb = LuceneExecutor.getProbFromDocument(rd);
		    	for (ScoreDoc ssd : sourceResults.scoreDocs) {
				    Document sd = sourceSearcher.doc(ssd.doc);
				    try {
				    	Long sourceV = sd.getField(_sourceGeoLocationField).numericValue().longValue();
					    sourceGeoPoint.setLongValue(sourceV);
					    double sourceProb = LuceneExecutor.getProbFromDocument(sd);
					    double distScore = distanceScore(refGeoPoint, sourceGeoPoint, _slope);
					    double prob = sourceProb * refProb * distScore;
					    LuceneExecutor.setDocumentProb(sd, prob);
					    addDocumentToResultAggregation(sd);
				    } catch (Exception e) {
				    	// no valid geo Field found
				    }
				}	
			    
		    } catch (Exception e) {
		    	// no valid geo Field found
		    }
		}
		aggregateAndStoreResults();
		_resultStore.commitAndFinalizeResultStore();
	}

	private double distanceScore(GeoPointField p1, GeoPointField p2, double slope) {
		double distance = SloppyMath.haversin(p1.getLat(), p1.getLon(), p2.getLat(), p2.getLon());
		return slope / (slope + distance);
	}
	
	private LuceneBlock getSourceBlock() {
		checkValid();
		return getInputBlocks().get(0);
	}

	private LuceneBlock getRefBlock() {
		checkValid();
		return getInputBlocks().get(1);
	}
	
	@Override
	protected void checkValid() {
		List<LuceneBlock> inputBlocks = getInputBlocks(); 
		
		if (inputBlocks == null || inputBlocks.size() != 2)
			throw new IllegalStateException("Two blocks (Source,Ref) need to be connected");
		
		if (_sourceGeoLocationField == null || _refGeoLocationField == null 
				|| _sourceGeoLocationField.isEmpty() || _refGeoLocationField.isEmpty())
			throw new IllegalStateException("sourceGeoLocationField and refGeoLocationField parameters need to be set");
	}
	
	public static LuceneGeoSearchBlock makeBlock(Element blockElem, Map<String, String> query) throws IOException {
		LuceneGeoSearchBlock result = new LuceneGeoSearchBlock();
		
		String sourceGeoLocationField = LuceneUtils.getTextContentOfElement(blockElem, "sourceGeoLocationField", true);
		String refGeoLocationField = LuceneUtils.getTextContentOfElement(blockElem, "refGeoLocationField", true);
		String maxDistKM = LuceneUtils.getTextContentOfElement(blockElem, "maxDistKM", false);
		String slope = LuceneUtils.getTextContentOfElement(blockElem, "slope", false);
		
		result.setSourceGeoLocationField(sourceGeoLocationField);
		result.setRefGeoLocationField(refGeoLocationField);
		result.setRadiusMeters(Double.parseDouble(maxDistKM) * 1000);
		result.setSlope(Double.parseDouble(slope));
		
		return result;
	}

}
