for i in Downlink Uplink
do
	echo "running $i"
	cd run
	java -jar $i.jar &
	cd ../
done
