# PhoneCenter &nbsp;&nbsp; <img src="./images/phone_center_icon.jpg"  width="80px" height="100px"/>
Phone Center Architecture using Play, Akka, Scala, Kafka, Kamon &amp; Datadog
<p>Created during the third year at <strong><em>Ariel University</em></strong> in the 
Department of Computer Science, 2020 <br /> 
<strong>Project site:</strong>&nbsp;<a href="https://github.com/ZviMints/PhoneCenter">https://github.com/ZviMints/PhoneCenter</a><br /> 
<strong>Made by: </strong> <a href="https://github.com/ZviMints">Zvi Mints</a>, and <a href="https://github.com/eilon26">Eilon Tsadok</a></p>

# Run The Project:
1. Open the terminal in this path: `PhoneCenter/CallProducer/` and run `sbt clean runProd -Dhttp.port=8080`
2. Open the terminal in this path: `PhoneCenter/CallConsumer/` and run `sbt clean runProd`
3. Start the MongoDB server.
4. run the Kafka server by the following commends:

   `bin/zookeeper-server-start.sh config/zookeeper.properties` 
   
   `bin/kafka-server-start.sh config/server.properties`
5. Start Redis with Kitematic on `localhost:6379`
6. Open https://apm.kamon.io/demo/demo/dashboards/ to get analytics.
7. **Open the browser and go to the url https://localhost:8080/ and start to answer calls.**

# About The Project:
We have developed an **asynchronous system** that aims to simulate the architecture of a phone call center:

**Flow: (CallProducer Service)**
1. When user press `set` button with new number of waiting calls, The number is sent to route named `/totalWaitingCalls` which aims to send a new message to Kafka with that number on topic `monitorTopic` which is env variable.
2. The user enters a call by clicking the `סיום` button.
3. The call is sent to route named `/send` which aims to enter the call with the `Ready` status into MongoDB.
4. There is Akka's Actor who from time to time gets all the calls with the Ready status and locks them (in order to avoid Race Conditions).
5. The Actor sends the message to Kafka on topic `callsTopic` which is env variable.

**Flow: (CallConsumer Service)**
1. As soon as the app goes up there is an Actor who wakes up and listens to Kafka.
2. Once there is a new message, the call is being saved into the cache (when the cache is implemented by Redis) for 12 hours.
3. There is an update of new metrics by Kamon.
4. There is a send from Kamon to APM Kamon Dashboard which graphically displays the information.

**Below are pictures of the Dashboard:**
<img src="./images/callsView.jpeg" width="750px" height="300px" />
<img src="./images/dashboard1.jpeg" width="750px" height="300px" />
<img src="./images/dashboard2.jpeg" width="750px" height="300px" />
<img src="./images/dashboard3.jpeg" width="750px" height="300px" />
<img src="./images/dashboard4.jpeg" width="750px" height="300px" />
<img src="./images/dashboard5.jpeg" width="750px" height="300px" />

   You can select `start time` and `end time` for each metric
<img src="./images/dashboard6.jpeg" width="750px" height="300px" />
<img src="./images/dashboard7.jpeg" width="750px" height="300px" />


