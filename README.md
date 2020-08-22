# PhoneCenter <img src="./images/phone_center_icon.jpg"  width="80px" height="100px"/>
Phone Center Architecture using Play, Akka, Scala, Kafka, Kamon &amp; Datadog
<p>Created during the third year at <em>Ariel University</em> in the 
Department of Computer Science, 2019 <br /> 
<strong>Project site:</strong>&nbsp;<a href="https://github.com/ZviMints/PhoneCenter">https://github.com/ZviMints/PhoneCenter</a><br /> 
<strong>Made by: </strong> <a href="https://github.com/ZviMints">Zvi Mints</a>, and <a href="https://github.com/eilon26">Eilon Tsadok</a></p>
<h1>About The Project:</h1>
<h2> We have developed an asynchronous system that aims to simulate the architecture of a telephone call center:</h2>
<strong>Flow: (CallProducer Service)</strong>
<p>1. The user enters a call by clicking the Send button.</br>
2. The call is sent to Route named `/ send` which aims to enter the call with the Ready status into the database.</br>
3. There is Akka's Actor who from time to time gets all the calls with the Ready status and locks them (in order to avoid RaceConditions).</br>
4. The Actor sends the message to Kafka.</br></p>
</br>
<strong>Flow: (CallConsumer Service)</strong>
<p>1. As soon as the app goes up there is an Actor who wakes up and listens to Kafka.</br>
2. Once there is a new message, the call is cached (when the Cache is managed by Redis) for 12 hours.</br>
3. There is an update of new metrics by Kamon.</br>
4. There is a send from Kamon to APM Kamon which graphically displays the information.</br><p>
</br>
<p style="text-align: center;"><strong>Below are pictures of the dashboard:</strong></p>
<p><img src="./images/callsView.jpeg" width="750px" height="300px" /></p></br>
<p><img src="./images/dashboard1.jpeg" width="750px" height="300px" /></p></br>
<p><img src="./images/dashboard2.jpeg" width="750px" height="300px" /></p></br>
<h1>Run The Project:</h1>
1. open the terminal in this path: "PhoneCenter/CallProducer/" and enter the commend "ssbt runProd -Dhttp.port=8080" </br>
2. open the terminal in this path: "PhoneCenter/CallConsumer/" and enter the commend "sbt runProd" </br>
3. start the mongodb process.</br>
4. run the kafka server by those commends:</br>
       bin/zookeeper-server-start.sh config/zookeeper.properties</br>
       bin/kafka-server-start.sh config/server.properties</br>
5. Open https://apm.kamon.io/demo/demo/dashboards/ to get analytics.</br>
6. Open the browser and go to the url "localhost:8080" and start to answer calls.</br>






