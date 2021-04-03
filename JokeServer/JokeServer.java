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
It does not randomize correctly at times. My fourth joke and proverbs stay fourth
sometimes, a bug. I have also seen two of the same in one list before randomizing

----------------------------------------------------------*/

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

/*Suggestions from classmates on D2L, Store on a HashMap. */
public class JokeServer {

  static String setting = "Joke";
  static String name;
  static String id = "";
  static int[] toldJokes;
  static int[] toldProverbs;
  static ClientData client;

  static HashMap<String, String> jokes = new HashMap<>();
  static HashMap<String, String> proverbs = new HashMap<>();
  static HashMap<String, ClientData> clientFiles = new HashMap<>();

  public static String getJoke(String key) {
    //NBA Hall of Famer Charles Barkley
    jokes.put("JA", "You got to believe in yourself. I believe" +
            " I'm the best-looking guy in the world and I might be right.");
    //Seinfeld quote
    jokes.put("JB", "Did you know that the original title for War " +
            "and Peace was War, What Is It Good For?");
    //Quote from the show Parks and Rec
    jokes.put("JC", "Jogging is the worst. I know it keeps you healthy, but God, at what cost?");
    jokes.put("JD", "I googled your symptoms and it says here you could  " +
            "have network connectivity problems.");

    return key + " " + name + ": " + jokes.get(key);
  }

  public static String getProverb(String key) {
    /*From a classmate from CSC447. He was explaining
     * prof wants us to complete this method without builtin functions*/
    proverbs.put("PA", "Sometimes we need to push a marble " +
            "across the floor with our noses.");
    /*From former NY Yankees outfielder Oscar Gamble. Rumor is this was his response to
     * allegations that there is racism being a ballplayer, or that playing for the Yankees
     * feels like a circus. I think its insightful but funny.*/
    proverbs.put("PB", "They don't think it be like it is, but it do.");
    //Famous Ali quote
    proverbs.put("PC", "Float like a butterfly and sting like a bee.");
    //Baseball HOF Yogi Berra
    proverbs.put("PD", "Baseball is ninety percent mental. The other half is physical.");

    return key + " " + name + ": " + proverbs.get(key);
  }

  public static void main(String[] args) throws IOException {

    Socket sock;
    int queueLen = 6;
    //ports normally in 45750-55000 range, never below 1025
    int port = 4545;

    AdminLooper ad = new AdminLooper();
    Thread t = new Thread(ad);
    t.start();

    ServerSocket servSock = new ServerSocket(port, queueLen);

    System.out.println("Arturo Chaidez's Joke Server 1.0, using port " + port);
    while (true) {
      sock = servSock.accept();
      new JokeWorker(sock).run();
    }
  }
}

class ClientData {

  String name;
  String id;
  int[] toldJokes = {0, 0, 0, 0};
  int[] toldProverbs = {0, 0, 0, 0};

  public void setToldJokes(int[] toldJokes) {
    this.toldJokes = toldJokes;
  }

  public int[] getToldJokes() {
    return toldJokes;
  }

  public int[] getToldProverbs() {
    return toldProverbs;
  }

  public void newName(String name) {
    this.name = name;
  }

  public void newId(String id) {
    this.id = id;
  }

  public void setToldProverbs(int[] toldProverbs) {
    this.toldProverbs = toldProverbs;
  }
}

class AdminLooper implements Runnable {

  public static boolean adminSwitch = true;

  public void run() {
    System.out.println("In the admin looper thread.");

    int queueLen = 6;
    int port = 5050;
    Socket sock;

    try {
      ServerSocket servsock = new ServerSocket(port, queueLen);
      while (adminSwitch) {
        sock = servsock.accept();
        new AdminWorker(sock).start();
      }
    } catch (IOException ioe) {
      System.out.println(ioe);
    }

  }
}

class AdminWorker extends Thread {

  Socket adminSock;
  AdminWorker (Socket s) {adminSock = s;}

  public void run() {
    PrintStream adminOut = null;
    BufferedReader adminIn = null;

    try {
      adminIn = new BufferedReader(new InputStreamReader(adminSock.getInputStream()));
      adminOut = new PrintStream((adminSock.getOutputStream()));
      try {

        JokeServer.name = adminIn.readLine();

        if (JokeServer.setting.equals("Joke")) {
          JokeServer.setting = "Proverb";
          //System.out.println(JokeServer.name);
        }
        else {
          JokeServer.setting = "Joke";
          //System.out.println(JokeServer.name);
        }

        String settingChanged = "Changing to " + JokeServer.setting + " setting.";

        System.out.println(settingChanged);
        adminOut.println(settingChanged);

      } catch (IOException x) {
        System.out.println("Sever Error!");
        x.printStackTrace();
      }
      adminSock.close();
    } catch (IOException e) {
      System.out.println(e);
    }
  }
}

class JokeWorker extends Thread {
  Socket jokeSock;
  JokeWorker(Socket s) { jokeSock = s; }

  public void run() {

    PrintStream out = null;
    BufferedReader in = null;

    try {
      in = new BufferedReader(new InputStreamReader(jokeSock.getInputStream()));
      out = new PrintStream((jokeSock.getOutputStream()));
      try {
        JokeServer.name = in.readLine();
        System.out.println("Getting Client info");

        JokeServer.id = in.readLine();

        if (JokeServer.clientFiles.containsKey(JokeServer.id)) {
          JokeServer.client = JokeServer.clientFiles.get(JokeServer.id);
          JokeServer.id = JokeServer.clientFiles.get(JokeServer.id).id;
          JokeServer.name = JokeServer.clientFiles.get(JokeServer.id).name;
          JokeServer.toldJokes = JokeServer.clientFiles.get(JokeServer.id).toldJokes;
          JokeServer.toldProverbs = JokeServer.clientFiles.get(JokeServer.id).toldProverbs;
        } else {
          ClientData newClient = new ClientData();
          newClient.newName(JokeServer.name);
          newClient.newId(JokeServer.id);
          JokeServer.clientFiles.put(JokeServer.id, newClient);
          JokeServer.client = JokeServer.clientFiles.get(JokeServer.id);
        }

        sendToClient(out);

      } catch (IOException x) {
        System.out.println("Server read error");
        x.printStackTrace();
      }
      jokeSock.close();
    } catch (
        IOException ioe) {
      System.out.println(ioe);
    }

  }

  public Line newLine(ClientData client) {

    String nextLine = "";
    HashMap<String, String> jokeMap = new HashMap<>();
    HashMap<String, String> proverbMap = new HashMap<>();

    jokeMap.put("0", "JA");
    jokeMap.put("1", "JB");
    jokeMap.put("2", "JC");
    jokeMap.put("3", "JD");
    proverbMap.put("0", "PA");
    proverbMap.put("1", "PB");
    proverbMap.put("2", "PC");
    proverbMap.put("3", "PD");

    Line savedLine = new Line();
    Random randomNumber = new Random();
    //was doing .nextInt(4) but was not randomizing correctly, trying 5
    //5 gives me an error. Try ((3-0) +1 )
    //that did not work. just stick to 4
    int pickRandom= randomNumber.nextInt(4);

    /* if/else statement. If server is in joke setting, get next joke, make sure we
     * dont need to randomize again, reset jokes. Else, do the same for proverb*/
    if (JokeServer.setting.equals("Joke")) {
      //set bool to true. Can loop to find if all jokes have been used.
      boolean allJokesUsed = true;
      int[] toldJokes = client.getToldJokes();

      //loop if all Jokes have been used. If one has not been used, set it to false
      for (int x = 0; x < toldJokes.length; x++) {
        if (toldJokes[x] != 1) {
          allJokesUsed = false;
          break;
        }
      }
      //if all jokes have been seen, reset array to 0
      if (allJokesUsed) {
        Arrays.fill(toldJokes, 0);
        System.out.println("Seen all jokes, new random order.");
      }

      //pick a random joke if they have not been used. Use it as nextLine
      if (toldJokes[pickRandom] == 0 && allJokesUsed) {
        toldJokes[pickRandom] = 1;
        client.setToldJokes(toldJokes);
        nextLine = jokeMap.get(String.valueOf(pickRandom));
        savedLine.nextJoke = nextLine;
        return savedLine;
      }

      else {
        for (int x = 0; x < toldJokes.length; x++) {
          if (toldJokes[x] == 0) {
            toldJokes[x] = 1;
            client.setToldJokes(toldJokes);
            nextLine = jokeMap.get(String.valueOf(x));
            savedLine.nextJoke = nextLine;
            break;
          }

        }
        return savedLine;
      }
    }
    //else used for proverbs. Same steps
    else {
      boolean allProverbsUsed = true;
      int[] toldProverbs = client.getToldProverbs();

      for (int x = 0; x < toldProverbs.length; x++) {
        if (toldProverbs[x] != 1) {
          allProverbsUsed = false;
          break;
        }
      }
      if (allProverbsUsed) {
        Arrays.fill(toldProverbs, 0);
        System.out.println("Seen all proverbs, new random order");

      }

      if (toldProverbs[pickRandom] == 0 && allProverbsUsed) {
        toldProverbs[pickRandom] = 1;
        client.setToldJokes(toldProverbs);
        nextLine = proverbMap.get(String.valueOf(pickRandom));
        savedLine.nextJoke = nextLine;
        return savedLine;
      }
      else {
        for (int x = 0; x < toldProverbs.length; x++) {
          if (toldProverbs[x] == 0) {
            toldProverbs[x] = 1;
            client.setToldProverbs(toldProverbs);
            nextLine = proverbMap.get(String.valueOf(x));
            savedLine.nextJoke = nextLine;
            break;
          }
        }
      }
      return savedLine;
    }
  }

  class Line {

    String jokeList;
    String proverbList;
    String nextJoke;

    public String getJokeList() {
      return jokeList;
    }

    public String getProverbList() {
      return proverbList;
    }

  }

  public void sendToClient(PrintStream out) {
    Line savedLine = newLine(JokeServer.client);

    if (JokeServer.setting.equals("Joke")) {
      if (savedLine.getJokeList() != null) {
        out.println(savedLine.getJokeList());
      }
      out.println(JokeServer.getJoke(savedLine.nextJoke));
    }
    else {
      if (savedLine.getProverbList() != null) {
        out.println(savedLine.getProverbList());
      }
      out.println(JokeServer.getProverb(savedLine.nextJoke));

    }
  }

}








