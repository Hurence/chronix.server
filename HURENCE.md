this is the Hurence notes


# Migration to solr V8
    ./gradlew clean build
    
     cp */build/libs/*-0.6.0.jar ../solr-8.0.0/server/solr/chronix/lib/
 
 
 # The Chronix test data set
 Contains about one month of time series with monitoring data.
 
 - \# of time series: 5,836
 - \# of points: 76,439,668
 
 That's about one month of time series with monitoring data.
 They were collected from a linux server running a jenkins ci.
 
 Time range:
 
  - Start: 2016-02-26
  - End: 2016-03-28


13/3/2016 à 23:00:04	
14/3/2016 à 22:59:59	
 
 

import some data : 

    java -Xms2G  -Xmx10G -Dlog4j.configurationFile=log4j2.xml -jar lib/chronix-importer-0.5-beta.jar config.yml data2/