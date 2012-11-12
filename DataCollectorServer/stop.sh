cd run
for i in Downlink Uplink
do
	echo "stopping $i"	
	ps aux | grep "$i.jar" | awk '{system("sudo kill -9 " $2);}'
done
cd ..
