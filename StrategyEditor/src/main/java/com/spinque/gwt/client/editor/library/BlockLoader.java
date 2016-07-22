package com.spinque.gwt.client.editor.library;

import com.google.gwt.core.client.JsArray;
import com.spinque.rest.client.Utils.ResponseTask;
import com.spinque.rest.client.overlay.BuildingBlockCategoryOverlay;
import com.spinque.rest.client.overlay.BuildingBlockOverlay;
import com.spinque.rest.client.overlay.BuildingBlockOverlay.ConnectionPointOverlay;
import com.spinque.rest.client.overlay.BuildingBlockOverlay.ControlOverlay;

public class BlockLoader {

	public static BlockLoader INSTANCE = new BlockLoader();

	public JsArray<BuildingBlockOverlay> blocks;
	
	BlockLoader() {
		blocks = JsArray.createArray().cast();
		blocks.push(makeMixBlock());
		blocks.push(makeSearchBlock());
		blocks.push(makeSourceBlock());
		blocks.push(makeTraverseBlock());
		blocks.push(makeGeoBlock());
	}
	
	public void fetchBlocks(String id, ResponseTask<JsArray<BuildingBlockOverlay>> responseTask, String message) {
		if (id.equals("lucene")) {
			responseTask.run(blocks);
		}
	}
	
	private BuildingBlockOverlay makeMixBlock() {
		JsArray<ConnectionPointOverlay> inputs = makeInputs(2);
		JsArray<ConnectionPointOverlay> outputs = makeOutput();
		
		JsArray<ControlOverlay> controls = JsArray.createArray().cast();
		controls.push(ControlOverlay.create("coefficients", "STRING", null, "0.5 0.5"));
		controls.push(ControlOverlay.create("resultAggregationType", "STRING", null, "MAX"));
		
		return BuildingBlockOverlay.create("lucene", "mix", "Combine results (mix)", 
				inputs, outputs, controls);
	}
	
	private BuildingBlockOverlay makeTraverseBlock() {
		JsArray<ConnectionPointOverlay> inputs = makeInputs(2);
		JsArray<ConnectionPointOverlay> outputs = makeOutput();
		
		JsArray<ControlOverlay> controls = JsArray.createArray().cast();
		controls.push(ControlOverlay.create("toField", "STRING", null, ""));
		controls.push(ControlOverlay.create("fromField", "STRING", null, "_uid"));
		controls.push(ControlOverlay.create("reverse", "BOOLEAN", null, "false"));
		controls.push(ControlOverlay.create("resultAggregationType", "STRING", null, "MAX"));

		return BuildingBlockOverlay.create("lucene", "traverse", "Traverse relation", 
				inputs, outputs, controls);
	}
	
	private BuildingBlockOverlay makeSearchBlock() {
		JsArray<ConnectionPointOverlay> inputs = makeInputs(1);
		JsArray<ConnectionPointOverlay> outputs = makeOutput();
		
		JsArray<ControlOverlay> controls = JsArray.createArray().cast();
		controls.push(ControlOverlay.create("queryString", "STRING", null, "title:\"%TOPIC%\""));

		return BuildingBlockOverlay.create("lucene", "search", "Text Search", 
				inputs, outputs, controls);
	}
	
	private BuildingBlockOverlay makeSourceBlock() {
		JsArray<ConnectionPointOverlay> inputs = makeInputs(0);
		JsArray<ConnectionPointOverlay> outputs = makeOutput();
		
		JsArray<ControlOverlay> controls = JsArray.createArray().cast();
		controls.push(ControlOverlay.create("source", "STRING", null, "/path/to/lucene/index"));
		
		return BuildingBlockOverlay.create("lucene", "source", "Source Index", 
				inputs, outputs, controls);
	}
	
	private BuildingBlockOverlay makeGeoBlock() {
		JsArray<ConnectionPointOverlay> inputs = makeInputs(2);
		JsArray<ConnectionPointOverlay> outputs = makeOutput();
		
		JsArray<ControlOverlay> controls = JsArray.createArray().cast();
		controls.push(ControlOverlay.create("sourceGeoLocationField", "STRING", null, ""));
		controls.push(ControlOverlay.create("refGeoLocationField", "STRING", null, ""));
		controls.push(ControlOverlay.create("resultAggregationType", "STRING", null, "MAX"));

		return BuildingBlockOverlay.create("lucene", "geo", "Find geographical nearby", 
				inputs, outputs, controls);
	}


	private JsArray<ConnectionPointOverlay> makeOutput() {
		JsArray<ConnectionPointOverlay> outputs = JsArray.createArray().cast();
		outputs.push(ConnectionPointOverlay.create("output", "ANY"));
		return outputs;
	}

	private JsArray<ConnectionPointOverlay> makeInputs(int n) {
		JsArray<ConnectionPointOverlay> inputs = JsArray.createArray().cast();
		for (int i = 0; i < n; i++) {
			inputs.push(ConnectionPointOverlay.create("input" + (i+1), "ANY"));
		}
		return inputs;
	}

	public BuildingBlockCategoryOverlay[] getCategories() {
		return new BuildingBlockCategoryOverlay[] {
				BuildingBlockCategoryOverlay.create("lucene", "Lucene Blocks",
						"Various blocks for building strategies on top of Lucene index", 10)
		};
	}

	public BuildingBlockOverlay getBlock(String buildingBlock) {
		for (int i = 0;i < blocks.length(); i++) {
			BuildingBlockOverlay bb = blocks.get(i);
			if (bb.getName().equals(buildingBlock))
				return bb;
		}
		return null;
	}

}
