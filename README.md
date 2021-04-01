# JokeServer
This project is a pair of multi-threaded servers that accept input from multiple clients, and return appropriate output. In addition to the client-server model, there is an implementation of a secondary administration channel to the servers, and manually maintain the state of all conversations within the distributed application.
  
With some minor changes, it can serve as the basis for a real, viable, client-server application handling thousands of client conversations simultaneously. Note that these servers are not thread-safe as of now.
