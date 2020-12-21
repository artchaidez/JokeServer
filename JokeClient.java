/*--------------------------------------------------------

1. Name / Date: Arturo Chaidez III September 24, 2020

2. Java version used, if not the official version for the class:

openjdk version "15" 2020-09-15
OpenJDK Runtime Environment (build 15+36-1562)
OpenJDK 64-Bit Server VM (build 15+36-1562, mixed mode, sharing)

3. Precise command-line compilation examples / instructions:

Run in 3 separate terminals:
> javac JokeServer.java
> javac JokeClient.java
> javac JokeClientAdmin.java

4. Precise examples / instructions to run this program:

In separate shell windows:

> java JokeServer
> java JokeClient
> java JokeClientAdmin

5. List of files needed for running the program.

1. JokeServer.java
2. JokeClient.java
3. JokeClientAdmin.java
4. JokeLog.txt
5. checklist-joke.html.

5. Notes:

Do not have a secondary server.  When switching between jokes/proverbs, it does
save and returns to the proper list order. None of the MultipleServers checklist has been done.
It does not randomize correctly. My fourth joke and proverbs stay fourth sometimes, a bug.
I have also seen two of the same in one list before randomizing.

----------------------------------------------------------*/

import java.io.*;
import java.net.*;

//Similar to InetClient. A lot less work than JokeServer
public class JokeClient {

  public static void main(String[] args) {

    String serverName;
    //Same as InetClient
    if (args.length < 1) {
      serverName = "localhost";
      System.out.println("Connected to server one: localhost, port 4545");
    } else {
      serverName = args[0];
      System.out.println("Connected to server one: localhost, port 4545");
    }

    //prof wants primary port to be 4545
    System.out.println("Welcome to Arturo Chaidez's Joke server, port 4545.");
    //prof wants admin port to be 5050
    System.out.println(("ClientAdmin port 5050"));
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    try {
      //need to get client name and ID
      String name;
      String input;
      int id = 0;

      System.out.println("You must be pretty bored to connect here..give me your name...");
      name = in.readLine();
      //IDK how to use UUID, come back to it later. Random is the same concept
      //This is acceptable according to prof.
      //followed format suggested online
      id = 0 + (int) (Math.random() * ((10000000 - 0) + 1));
      System.out.println("Click enter for a joke. Once " +
              "you realize this place isn't very funny, (quit) to get out.");
      do {
        //get info
        input = in.readLine();
        //make sure client did not quit right away
        //.equals() wont work. stick to indexOf
        if (input.indexOf("quit") <0 ) {
          /*Send name, unique id, and serverName to Server */
          printLine(name, id, serverName);
            }
      }
      //exit out when told
      while (input.indexOf("quit") <0);
      System.out.println("I see you got bored. Program ended");
    } catch (IOException x) {
      x.printStackTrace();
    }
  }

//since we are not using IP addresses, do not need the toText Prof gave us for Inet
//seems to be working without it

//Similar as printRemoteAddress in Inet. Need to get jokes/proverbs
  static void printLine(String name, int id, String serverName) {
    Socket sock;
    BufferedReader fromServer;
    PrintStream toServer;
    String textFromServer;

    try {
      sock = new Socket(serverName, 4545); //it wont let me change setting here
      //I am dumb, changing between jokes and proverbs happen in the AdminClient
      //error was probably unrelated too

      fromServer = new BufferedReader((new InputStreamReader(sock.getInputStream())));
      toServer = new PrintStream(sock.getOutputStream());

      //Send info to Server
      toServer.println(name);
      toServer.println(id);

      //get lines from Server. Probably only need 1, but do 5 to be sure
      //some jokes proverbs kind of long.
      for(int i = 1; i <= 5; i++) {
        textFromServer = fromServer.readLine();
        if (textFromServer != null) {
          System.out.println(textFromServer);
        }
      }
      sock.close(); //done, can close socket

    } catch (IOException x) { //usual error
      System.out.println("Error: Socket error.");
      x.printStackTrace();
    }
  }

}