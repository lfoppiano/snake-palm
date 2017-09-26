# Snake-PALM #

[![Build Status](https://travis-ci.org/lfoppiano/snake-palm.svg?branch=master)](https://travis-ci.org/lfoppiano/snake-palm)
[![Coverage Status](https://coveralls.io/repos/lfoppiano/snake-palm/badge.svg)](https://coveralls.io/r/lfoppiano/snake-palm)

Work in progress... 

### Dependencies ###

 - GROBID, branch [dropwizard-service](https://github.com/kermitt2/grobid/tree/dropwizard-service)
 - GROBID-NER, branch [newModelUsingXml](https://github.com/kermitt2/grobid-ner/tree/newModelUsingXml)
 
### Getting started ###

1. Install GROBID (branch dropwizard-service)
```bash
> git clone https://github.com/kermitt2/grobid.git
> git checkout dropwizard-service
> mvn clean install
``` 

2. Install GROBID-NER (branch newModelUsingXML)
```bash
> git clone https://github.com/kermitt2/grobid-ner.git
> git checkout newModelUsingXML
> mvn clean install 
``` 

3. Train the multiDate model 

TBC