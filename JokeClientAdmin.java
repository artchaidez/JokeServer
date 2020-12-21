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

Do not have a secondary server. When switching between jokes/proverbs, it does
save and returns to the proper list order. None of the MultipleServers checklist has been done.
It does not randomize correctly. My fourth joke and proverbs stay fourth sometimes, a bug.
I have also seen two of the same in one list before randomizing

----------------------------------------------------------*/

import java.io.*; //used to retrieve the I/O libraries
import java.net.*; //used to retrieve the Java networking libraries

//THIS is where we change between server
/*I was trying to do it all on Client, misread instructions. I thought this
* had to do with primary/secondary servers. */
public class JokeClientAdmin {

  //switch between jokes and proverbs
  private static String setting ;
  static boolean changeSetting = false;

  // This is the main method.
  public static void main(String[] args) {

    //this is the same as Inet
    String serverName;
    //Same as InetClient
    if (args.length < 1) {
      serverName = "localhost";
      System.out.println("Now connected server to one, port: 5050");
    } else {
      serverName = args[0];
      System.out.println("Now connected to server one, port: 5050");
    }

    System.out.println(("Arturo Chaidez's JokeClientAdmin"));
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    try {
      //get input
      String adminInput = "";
      System.out.println(
          "Click enter to switch settings, or type (quit) to end program.");
      do {
        //check input from client
        adminInput = in.readLine();

        //if they do not quit and clicked enter, toggle to other setting
        //when I use equal(), it quits after one click. not sure why
        if (adminInput.indexOf("quit") < 0) {
          talkToServer(adminInput, serverName);
          System.out.println("Changed setting");
        }
      } while (adminInput.indexOf("quit") < 0); //user quit
      System.out.println("Client has ended program.");
    } catch (IOException x) {
      x.printStackTrace();
    }
  }


  static void talkToServer(String name, String serverName) {
    //same as usual
    Socket sock;
    BufferedReader fromServer;
    PrintStream toServer;
    String textFromServer;

    try {

      //make socket
      sock = new Socket(serverName, 5050);
      fromServer = new BufferedReader((new InputStreamReader(sock.getInputStream())));
      toServer = new PrintStream(sock.getOutputStream());

      //double check what this does?
      //doesn't seen to do anything. It should only be "" anyway
      /*Classmates had it print something on JokeServer, will try to figure it out later */
      toServer.println(name);
      //switch to other setting
      if (changeSetting) {
        //writing name
        toServer.println(setting);
        // Read 3 lines, probably too many
        for(int i = 1; i <= 3; i++) {
          textFromServer = fromServer.readLine();
          if (textFromServer != null) {
            System.out.println(textFromServer);
          }
        }

        changeSetting = false;
      }
      //complete
      sock.close();
    } catch (IOException x) { //same error check
      System.out.println("Error: Socket error.");
      x.printStackTrace();
    }
  }

}