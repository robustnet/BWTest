#!/bin/bash

mkdir run

cd bin
for i in Uplink Downlink
do
	echo "Main-Class: servers.$i" > manifest
	jar cvfm $i.jar manifest servers/$i*.class servers/Definition.class servers/Utilities.class
	mv $i.jar ../run
done

rm manifest

cd ..
