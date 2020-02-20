# VizAssist ReadMe

## PART 5

#### Web Service Overview

**![WeChat040fe9b3671c0744b8736482747c5dff](/Users/tommy/Documents/VizAssist/Pics/WeChat040fe9b3671c0744b8736482747c5dff.png)**

Make connections between client and server so that client can successfully send request to server, and the server can successfully send response to client. Deploy the app onto GCE (Google Compute Engine) server, the boxes in between the Android App and the Cloud Vision API functions as what interacts with the user (the web app), and also lessen the burden of applying all the functionalities on user phone end.

----



#### Setup Apache Tomcat Server

**RPC**(remote procedure call): A procedure call is a function call. RPC is a function call to a remote server (remote means caller and callee on different machines).

![WeChatf85dc2ff5e4c6f1a279396f301d50915](/Users/tommy/Documents/VizAssist/Pics/WeChatf85dc2ff5e4c6f1a279396f301d50915.png)

**Javs Servlet:** A java class to handle RPC on server side

**How to send the request to a proper servlet? ** <u>Tomcat</u> is what deference the request, then displatch to the corresponding servlet. 

**Connection Pool: ** a pool (usually of 500) that processes requests. If the pool is full, the later coming requests have to wait. Usually there's also a <u>deadline</u> by which a request needs to be responded, otherwise the user has to waitforever, which is impractical.

**Using Eclipse for Java EE IDE:**

1. Define a server Tomcat v9.0, using downloaded Tomcat installation directory (apache-tomcat-9.0.24)
2. Double tap "Tomcat v9.0 server at localhost..." under server tap to open overview. Then, change server location to:![WeChat73e6b061a08775986e9a73533b460b5a](/Users/tommy/Documents/VizAssist/Pics/WeChat73e6b061a08775986e9a73533b460b5a.png)
3. right click the same icon under servers tab, under properties, switch location to not to use the workspace one
4. test with localhost: 8080 (or change to another host by changing port number under server overview)

-----

#### HTTP Request and Response

**HTTP Request: **

![WeChat6e7b69a1d6175ac41b3839f66acb8e72](/Users/tommy/Documents/VizAssist/Pics/WeChat6e7b69a1d6175ac41b3839f66acb8e72.png)

The first two lines (GET... Host:...) are the "<u>resource path</u>", the object that the request is dealing with.

Eg. I'm trying to read a .html file that's deployed on a server

Eg. https://www.youtube.com/result?search_query=TommyTangAwesomeVideo, the 'result' here is a dynamic resource that receives a paremeter 'TommyTangAwesomeVideo'.

Request headers give the key-value pair of what is being requested. 



**HTTP Request and Response Body:**

![WeChatdf66edfc04502308f7b9aca5faf6420a](/Users/tommy/Documents/VizAssist/Pics/WeChatdf66edfc04502308f7b9aca5faf6420a.png)

Request Body is is usually for the GET request (GET: request data from server). What is being passed is usually written in the body.



![WeChat4bdebb41852334040913d54ca105250c](/Users/tommy/Documents/VizAssist/Pics/WeChat4bdebb41852334040913d54ca105250c.png)

The response body is usually in the body. Usually for nowadays complex responses, JSON format is used.

---



#### Build an HTTP Web Service



http://localhost:8080/VizAssist/annotate

