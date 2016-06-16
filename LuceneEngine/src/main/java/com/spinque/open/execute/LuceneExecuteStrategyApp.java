package com.spinque.open.execute;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.spinque.open.LuceneExecutor;
import com.spinque.open.strategy.LuceneStrategy;

public class LuceneExecuteStrategyApp {

	public static void main(String[] args) throws Exception {
		
		if (args.length != 3) {
			System.err.println("Usage: lucene-engine <strategy-file> \"<param>=<value>,...\" <numResults>");
			System.exit(1);
		}
		
		File strategyFile = new File(args[0]); /* strategy to load */
		Map<String, String> query = parseQuery(args[1]); /* key -> value */
		int topN = Integer.parseInt(args[2]); /* num results to show */
		
		LuceneStrategy strategy = LuceneStrategy.load(strategyFile, query);
		try {
			final long startTime = - System.currentTimeMillis();
			LuceneExecutor.showResults(strategy.getResultBlock(), topN);
			System.out.println("Done. Took " + ((startTime + System.currentTimeMillis())/1000) + "s");
		} finally {
			strategy.close();
		}
	}

	private static Map<String, String> parseQuery(String queryString) {
		String[] params = queryString.split("\\s*,\\s*");
		Map<String, String> result = new HashMap<String, String>();
		for (String param: params) {
			String[] keyValue = param.split("=", 2);
			result.put(keyValue[0], keyValue[1]);
		}
		return result;
	}
}
