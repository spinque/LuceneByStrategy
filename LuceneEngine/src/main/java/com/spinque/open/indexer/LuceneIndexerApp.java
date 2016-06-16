package com.spinque.open.indexer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class LuceneIndexerApp {

	public static void main(String[] args) throws IOException {
		
		if (args.length != 3) {
			System.err.println("Usage: lucene-engine-indexer {<indexStore>}|dryrun <mappingFile> <documentCollection>");
			System.exit(1);
		}
		
		Path indexStore = args[0].equals("dryrun") ? null : Paths.get(args[0]);
		Path mappingFile = Paths.get(args[1]);
		Path documentsLocation = Paths.get(args[2]);
		LuceneIndexMapping mapping = LuceneIndexMapping.loadFromFile(mappingFile.toFile());
		final LuceneIndexer indexer = new LuceneIndexer(mapping, indexStore);
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		
		final long startTime = - System.currentTimeMillis();
		try {
			final AtomicLong numFiles = new AtomicLong(0);
			System.out.println("Starting indexing " + documentsLocation.toString() 
						     + " using mapping " +  mappingFile.toString() 
						     + " and writing to " + indexStore.toString());
			Files.walk(documentsLocation).forEach(new Consumer<Path>() {
				@Override
				public void accept(Path t) {
					File f= t.toFile();
					if (!(f.isFile() && f.canRead() && f.getName().endsWith(".xml")))
						return;
					
					try {
						DocumentBuilder db = dbf.newDocumentBuilder();
						Document d =  db.parse(f);
						indexer.indexDocument(d.getDocumentElement());
						if (numFiles.incrementAndGet() % 1000 == 0)
							System.out.println(((startTime + System.currentTimeMillis())/1000) + "s\tAt " + numFiles.get() + " documents");
					} catch (ParserConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SAXException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}
			});
			System.out.println("Done. Indexed " + numFiles.get() + " files, took " + ((startTime + System.currentTimeMillis())/1000) + "s");
		} finally {
			indexer.close();
		}
	}
}
