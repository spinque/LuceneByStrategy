# LuceneByStrategy

LuceneByStrategy is an open-source implementation of the *Search by Strategy* paradigm developed by [Spinque](http://www.spinque.com). 


A web-base **Strategy Editor** allows to design search strategies by visually connecting operational building blocks into a graph. Search strategies can then be saved and executed on top of Lucene indices. This allows to implement search scenarios that involve multiple data sources, multiple operations, and any combination thereof, without writing a single line of code.

Note that this open-source release and the commercial one offered by Spinque share the Search by Strategy paradigm, but may differ significantly in terms of limitations and performance.  

*This project is partly supported by the Netherlands Organization for Scientific Research (NWO, CATCH WebART project, \# 640.005.001).*


## Basic usage

The project consists of two parts:
- **LuceneEngine**: to index data into / execute search strategies onto Lucene indices 
- **StrategyEditor** : to visually construct search strategies

### Indexing data

To index a data source:
```bash
$ <LuceneEngine installDir>/bin/lucene-engine-indexer {<indexStore>|dryrun} <mappingFile> <documentCollection>
```
where:
- `<indexStore>` is the path where the index will be created. Use `dryrun` to go through data without storing any index.
- `<mappingFile>` is a file with a specification of how to map data xml elements onto (enriched) Lucene fields
- `<documentCollection>` is a path pointing at the folder containing data files. This folder is scanned recursively for any `xml` file.

Note that only indices created with this indexer can be used within LuceneByStrategy, as some required enrichment is performed on top of standard Lucene data indexing.  

#### Example
[TBD]

### Editing a search strategy

After deployment of StrategyEditor, point your browser to: `localhost:8080/LuceneByStrategyEditor/` (or to the url you defined in `build.gradle`. 

### Executing a search strategy

To execute a strategy created by StrategyEditor:
```bash
$ <LuceneEngine installDir>/bin/lucene-engine <strategy-file> "<param>=<value>,..." <numResults>
```
where
- `<strategy-file>` is a path pointing at the file created for the strategy to be executed
- `<param>=<value>` pairs allow to customize the strategy with user-provided inputs
- `<numResults>` is the desired number of results in output. Default is all results.
  
#### Example
[TBD]

## Installation

Requirements:
- Java 7
- Tomcat 7

### LuceneEngine

```bash
$ cd LuceneEngine
$ ./gradlew clean installDist
```

Installed files are to be found in `build/install/LuceneEngine`.


### StrategyEditor
Run the following to deploy the web service to Tomcat on localhost:8080, with default credentials.
Edit `build.gradle` to customize deployment.

```bash
$ cd StrategyEditor
$ ./gradlew clean reDeploy
```

