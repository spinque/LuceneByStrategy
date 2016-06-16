package com.spinque.open;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.RAMDirectory;

import com.spinque.open.blocks.LuceneUtils;

public class LuceneBlockResultStore {
	private static final boolean USE_MMAP = false;

	private Directory _directory;
	private IndexWriterConfig _storedResultsConfig = null;
	private IndexWriter _storedResultsWriter = null;
	private boolean _isUpToDate = false;
	private IndexSearcher _searcher = null;
	
	public LuceneBlockResultStore(Directory d) {
		_directory = d;
	}

	public LuceneBlockResultStore() {
		_directory = null;
	}

	public IndexSearcher getSearcher() throws IOException {
		if (_searcher == null)
			_searcher = new IndexSearcher(DirectoryReader.open(_directory));
		return _searcher;
	}
	
	public boolean getIsUpToDate() {
		return _isUpToDate;
	}
	public void setIsUpToDate(boolean isUpToDate) {
		_isUpToDate = isUpToDate;
	}

	/**
	 * Commit and allow more writes
	 * @throws IOException
	 */
	public void commitResultStore() throws IOException { 
		if (_storedResultsWriter == null)
			initResultStore();
		_storedResultsWriter.commit();
	}
	
	/**
	 * Commit and close the writer
	 * @throws IOException
	 */
	public void commitAndFinalizeResultStore() throws IOException { 
		if (_storedResultsWriter == null)
			initResultStore();
		_storedResultsWriter.commit(); 
		_storedResultsWriter.close();
		_storedResultsWriter = null;
		_isUpToDate = true;
	}
		
	public void storeResult(Document d) throws IOException {
		if (_storedResultsWriter == null)
			initResultStore();
		d = LuceneUtils.fixLUCENE7171(d);
		_storedResultsWriter.addDocument(d);
	}
	
	public void deleteResult(Document d) throws ParseException, IOException {
		Query query =  new TermQuery(new Term("_uid", d.get("_uid")));
		_storedResultsWriter.deleteDocuments(query);
	}
	
	public void updateResult(Document d) throws ParseException, IOException {
		deleteResult(d);
		storeResult(d);
	}

	private void initResultStore() throws IOException {
		if (_directory == null) {
			if (USE_MMAP) {
				Path path = Paths.get(System.getProperty("java.io.tmpdir") , UUID.randomUUID().toString());
				_directory = new MMapDirectory(path);
			} else
				_directory = new RAMDirectory();
			
			_storedResultsConfig = new IndexWriterConfig(new StandardAnalyzer());
			_storedResultsWriter = new IndexWriter(_directory, _storedResultsConfig);
		}
	}

	public void close() throws IOException {
		if (_directory != null) {
			_directory.close();
		}
	}
}
