# JOLT — Java On-Line Topics

A TCP publish-subscribe message broker built from scratch in Java using sockets, threads, and file I/O.

JOLT was built over four weeks as a hands-on learning project, starting with two programs sending a single message over a socket and ending with a multi-threaded broker that routes messages by topic, persists them to disk, and displays a live dashboard.

---

## How It Works

Publishers send messages tagged with a topic. Subscribers register for topics they care about. The broker sits in the middle, routing every published message to the right subscribers in real time. If a subscriber joins after messages have already been published, it receives the backlog automatically. Messages survive broker restarts because they are saved to disk.

```
 ┌──────────────┐                                    ┌──────────────────┐
 │  Publisher    │───PUBLISH | sports | goal!───────▶│                  │──────▶ Subscriber A
 └──────────────┘          TCP :1602                 │   BrokerServer   │──────▶ Subscriber B
                                                     │                  │──────▶ Subscriber C
 ┌──────────────┐                                    │   ┌────────────┐ │
 │  Subscriber   │◀──────────────────────────────────│   │ Dashboard  │ │
 │  (new join)   │◀── queued messages replayed ──────│   └────────────┘ │
 └──────────────┘                                    └──────────────────┘
```

---

## The Protocol

JOLT uses a simple text-based protocol over TCP. Messages are single lines with fields separated by `|`.

**Subscribe to a topic:**
```
SUBSCRIBE | sports
```

**Publish a message to a topic:**
```
PUBLISH | sports | Mbappe scores a hat trick
```

**Broker delivers to subscribers:**
```
Mbappe scores a hat trick
```

---

## Project Structure

```
jolt/
└── src/
    ├── jolt/broker/
    │   ├── BrokerServer.java       — Entry point, accepts connections on port 1602
    │   ├── ClientHandler.java      — Handles SUBSCRIBE and PUBLISH per connection
    │   ├── MessageStore.java       — Reads/writes messages to .txt files on disk
    │   └── BrokerDashboard.java    — Live stats thread (topics + subscriber counts)
    │
    └── jolt/publisher/
        ├── PublisherClient.java    — Connects and publishes a message
        └── SuscriberClient.java    — Connects, subscribes, and prints incoming messages
```

---

## How to Run

**1. Compile everything:**
```bash
javac -d out src/jolt/broker/*.java src/jolt/publisher/*.java
```

**2. Start the broker:**
```bash
java -cp out jolt.broker.BrokerServer
```
You should see:
```
Loaded topic from disk: sports
=== JOLT BROKER ===
===================
```

**3. In a second terminal, start a subscriber:**
```bash
java -cp out jolt.publisher.SuscriberClient
```
Output:
```
Connected to broker!
```

**4. In a third terminal, publish a message:**
```bash
java -cp out jolt.publisher.PublisherClient
```

The subscriber terminal will print:
```
Received: Mbappe scores a hat trick
```
'''If sports.txt exists from a previous run, the subscriber will receive queued messages immediately on connecting 
— this demonstrates the persistence and replay feature'''

And the broker dashboard will update:
```
=== JOLT BROKER ===
 - sports : 1 subscribers
==================
```

---

## What Each Class Does

**BrokerServer** opens a `ServerSocket` on port 1602. On startup, it scans the working directory for `.txt` files and loads any previously saved messages back into memory using `MessageStore`. It starts the `BrokerDashboard` as a background thread, then loops forever accepting new client connections, handing each one to its own `ClientHandler` thread.

**ClientHandler** implements `Runnable`. It reads the first line from the client socket, splits it on `|`, and checks the command. For `SUBSCRIBE`, it adds the socket to a shared `HashMap<String, ArrayList<Socket>>` under the requested topic — and if there are queued messages from before the subscriber joined, it replays them immediately. For `PUBLISH`, it looks up all sockets subscribed to that topic and writes the message to each one, then calls `MessageStore.saveMessage()` to persist it.

**MessageStore** handles disk I/O. `saveMessage()` appends each message as a new line in `<topic>.txt`. `loadMessages()` reads the file back into a `Queue<String>` so the broker can replay history to late-joining subscribers.

**BrokerDashboard** implements `Runnable` and runs as a daemon thread. Every 5 seconds it prints a banner showing each registered topic and how many subscribers are connected to it.

**PublisherClient** and **SuscriberClient** are minimal test clients. Each opens a socket to `localhost:1602`, sends a single protocol line, and either exits (publisher) or waits for incoming messages (subscriber).

---

## Week-by-Week Build Log

### Week 1 — Two Programs Talking
Started from zero. Built basic TCP socket communication: `BrokerServer` opens a port, `SuscriberClient` connects. Designed the pipe-delimited text protocol. Learned `ServerSocket`, `Socket`, `BufferedReader`, `PrintWriter`, how ports work, and how data flows through streams.

### Week 2 — The Real Broker
Introduced multi-threading with `ClientHandler implements Runnable`. The broker can now handle multiple clients at the same time, each on its own thread. Built the subscription registry — a `HashMap<String, ArrayList<Socket>>` that maps topic names to lists of subscriber sockets. Achieved end-to-end delivery: publisher sends a message, broker routes it to every subscriber registered for that topic.

### Week 3 — Durability
Built `MessageStore` with `saveMessage()` and `loadMessages()` using `FileWriter` and `BufferedReader`. Every published message gets appended to a `.txt` file named after its topic. On broker startup, the server scans for these files and rebuilds the in-memory message queues. Subscribers that connect after messages were published automatically receive the backlog.

### Week 4 — Dashboard + Polish
Built `BrokerDashboard implements Runnable`, a background thread that prints a live status display every 5 seconds with active topics and subscriber counts. Added the dashboard thread launch to `BrokerServer` (one line before the accept loop). Ran the final integration test — publisher to broker to subscriber, with persistence and dashboard all working. Wrote this README.

---

## Key Concepts Used

- **TCP Sockets** — `ServerSocket` and `Socket` for network communication
- **Threads** — `Runnable` interface and `new Thread().start()` for concurrency
- **Streams** — `InputStream`/`OutputStream` wrapped in readers/writers for text I/O
- **Collections** — `HashMap`, `ArrayList`, `Queue`, `LinkedList` for data management
- **File I/O** — `FileWriter`, `FileReader`, `BufferedWriter`, `BufferedReader` for persistence
- **Protocol Design** — Simple pipe-delimited text protocol over raw TCP

---

## Port

JOLT runs on port **1602**.

---

*Built by Abdoul — a four-week learning project, from first socket to working message broker.*
