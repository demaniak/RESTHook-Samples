# Description:

This is a sample web API subscribing to WhereIsMyTransport webhooks.

This example uses the Java Spark (http://sparkjava.com/) micro framework and is an api that creates a webhook. The Api runs on port 4567. If you run a default GET call, or alternatively open it in a browser (localhost:4567) you will see the result "Startup class loaded.". This means that the Api started up successfully.
 
This example API subscribes to the Messenges webhook from WhereIsMyTransport. It receives messages sent from WhereIsMyTransport Messenger and stores it in a list in memory. These messages are then viewable from the /alerts GET call.
 
The other calls available are:

| Call    | Description  |
| ------- | :----------- |
| <b>/alerts </b> | Shows all the alerts that the webhook has received. It shows an empty list if it hasnt received any. |
| <b>/logs </b>   | Shows the logs of the server. This is useful when you don't find any Alerts to see if anything went wrong. |
 
