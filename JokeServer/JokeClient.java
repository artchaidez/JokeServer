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

public class JokeClient {

  public static void main(String[] args) {

    String serverName;
    if (args.length < 1) {
      serverName = "localhost";
      System.out.println("Connected to server one: localhost, port 4545");
    } else {
      serverName = args[0];
      System.out.println("Connected to server one: localhost, port 4545");
    }

    System.out.println("Welcome to Arturo Chaidez's Joke server, port 4545.");
    System.out.println(("ClientAdmin port 5050"));
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    try {
      String name;
      String input;
      int id = 0;

      System.out.println("You must be pretty bored to connect here..give me your name...");
      name = in.readLine();

      id = 0 + (int) (Math.random() * ((10000000 - 0) + 1));
      System.out.println("Click enter for a joke. Once " +
              "you realize this place isn't very funny, (quit) to get out.");
      do {
        input = in.readLine();
        //.equals() wont work. Using indexOf
        if (input.indexOf("quit") <0 ) {
          printLine(name, id, serverName);
            }
      }
      while (input.indexOf("quit") <0);
      System.out.println("I see you got bored. Program ended");
    } catch (IOException x) {
      x.printStackTrace();
    }
  }

  static void printLine(String name, int id, String serverName) {
    Socket sock;
    BufferedReader fromServer;
    PrintStream toServer;
    String textFromServer;

    try {
      sock = new Socket(serverName, 4545);

      fromServer = new BufferedReader((new InputStreamReader(sock.getInputStream())));
      toServer = new PrintStream(sock.getOutputStream());

      toServer.println(name);
      toServer.println(id);

      for(int i = 1; i <= 5; i++) {
        textFromServer = fromServer.readLine();
        if (textFromServer != null) {
          System.out.println(textFromServer);
        }
      }
      sock.close();

    } catch (IOException x) {
      System.out.println("Error: Socket error.");
      x.printStackTrace();
    }
  }

}