#!/bin/bash

for file in ../data/*.ttl
do
	rapper -o rdfxml -i turtle $file > "${file%.*}.rdf"
done
