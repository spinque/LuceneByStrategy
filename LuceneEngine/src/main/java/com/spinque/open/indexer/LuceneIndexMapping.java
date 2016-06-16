package com.spinque.open.indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LuceneIndexMapping {

	enum Destination { ID, TextField, StringField, IntegerField, GeoPoint, 
						DateField, RelationSplitByComma, Ignore }
	
	/* fieldName -> destination */
	private final Map<String, Destination> _mapping;
	/* fieldName (relation) -> type of destination object */
	private final Map<String, String> _types;
	private final Map<String, String> _geoPoints;
	private final List<String> _idFields;
	private String _rootType;
	
	public LuceneIndexMapping(Map<String, Destination> mapping, List<String> idFields, String rootType, Map<String, String> types, Map<String, String> geoPoints) {
		_mapping = mapping;
		_idFields = idFields;
		_rootType = rootType;
		_types = types;
		_geoPoints = geoPoints;
	}

	static LuceneIndexMapping loadFromFile(File mappingFile) throws IOException {
		Map<String, Destination> mapping = new HashMap<String, LuceneIndexMapping.Destination>();
		List<String> idFields = new ArrayList<String>();
		Map<String, String> types = new HashMap<String, String>();
		Map<String, String> geoPoints = new HashMap<String, String>();
		String rootType = "document";
		BufferedReader reader = new BufferedReader(new FileReader(mappingFile));
		boolean firstLine = true;
		try {
			while (true) {
				String line = reader.readLine();
				if (line == null)
					break;
				
				/* the first line may contain type info */
				if (firstLine && line.startsWith("[")) {
					rootType = line.substring(1, line.indexOf(']'));
					firstLine=false;
					continue;
				}
				firstLine=false;
				
				String[] keyValue = line.split("=",2);
				Destination d = Destination.valueOf(keyValue[0]);
				switch (d) {
				case RelationSplitByComma: {
					String[] items = keyValue[1].split("\\s*,\\s*");
					for (int i = 0; i < items.length; i++) {
						String item = items[i];
						int pos = item.indexOf('[');
						if (pos != -1) { /* if type info is available, strip it */
							int endPos = item.indexOf(']');
							items[i] = item.substring(0, pos);
							String type = item.substring(pos+1, endPos);
							types.put(items[i], type);
						}
					} 
					for (String item : items) {
						mapping.put(item, d);
					}
					break;
				}
				case ID: {
					String[] items = keyValue[1].split("\\s*,\\s*");
					idFields.addAll(Arrays.asList(items));
					break;
				}
				case GeoPoint: {
					String[] items = keyValue[1].split("\\s*,\\s*");
					for (int i = 0; i < items.length; i++) {
						String item = items[i];
						int startPos = item.indexOf('{');
						int delimPos = item.indexOf(' ');
						int endPos = item.indexOf('}');
						if (startPos == -1 || delimPos == -1 || endPos ==-1 ||
								delimPos > endPos || delimPos < startPos) continue;
						items[i] = item.substring(0, startPos).trim();
						
						String point = item.substring(startPos+1, endPos);
						geoPoints.put(items[i], point);
					} 
					for (String item : items) {
						mapping.put(item, d);
					}
				}
				default: {
					String[] items = keyValue[1].split("\\s*,\\s*");
					for (String item : items) {
						mapping.put(item, d);
					}
				}
				}
			}
			if (idFields.isEmpty())
				throw new IOException("need at least one ID-field");
			return new LuceneIndexMapping(mapping, idFields, rootType, types, geoPoints); 
		} finally {
			reader.close();
		}
	}

	public List<String> getIDFields() {
		return _idFields;
	}
	
	public Map<String, Destination> getMapping() {
		return _mapping;
	}

	public String getType(String key) {
		return _types.containsKey(key) ? _types.get(key) : "unknown";
	}

	public String getGeoPoint(String key) {
		return _geoPoints.containsKey(key) ? _geoPoints.get(key) : "";
	}

	public String getRootType() {
		return _rootType;
	}
}
