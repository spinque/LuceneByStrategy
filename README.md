# LuceneByStrategy

LuceneByStrategy is an open-source implementation of the *Search by Strategy* paradigm developed by [Spinque](http://www.spinque.com). 

A web-base *Strategy Editor* allows to design search strategies by visually connecting operational building blocks into a graph. Search strategies can then be saved and executed on top of Lucene indices. This allows to implement search scenarios that involve multiple data sources, multiple operations, and any combination thereof, without writing a single line of code.

Note that this open-source release and the commercial one offered by Spinque share the Search by Strategy paradigm, but may differ significantly in terms of limitations and performance.  

*This project is partly supported by the Netherlands Organization for Scientific Research (NWO, CATCH WebART project, \# 640.005.001).*

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


### StrategyCenter
Run the following to deploy the web service to Tomcat on localhost:8080, with default credentials.
Edit `build.gradle` to customize deployment.

```bash
$ cd StrategyEditor
$ ./gradlew clean reDeploy
```

## Usage

### Indexing data

[TBD]

### Editing a search strategy

After deployment of StrategyEditor, point your browser to: `localhost:8080/LuceneByStrategyEditor/` (or to the url you defined in `build.gradle`. 

### Executing a search strategy

[TBD]

