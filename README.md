# Multithreaded TCP Producer-Consumer Servers – Java Project

## Project Description
This Java project simulates a system of servers with producer and consumer processes communicating over TCP sockets. Each server maintains a shared storage variable representing product inventory. Producers and consumers connect randomly to servers, modifying the storage value by adding or subtracting quantities, respectively, under capacity constraints. The servers handle multiple producers and consumers concurrently via multithreading.

## Features
- Multiple servers each running on independent ports for producers and consumers.
- Each server has a storage variable initialized to a random value [1,1000].
- Producers add random amounts [10,100] to storage; if exceeding 1000, addition is rejected.
- Consumers subtract random amounts [10,100] from storage; if resulting value <1, subtraction is rejected.
- Servers support concurrent producers and consumers managing storage with thread safety.
- Producers and consumers connect randomly to different servers.
- Random wait times between operations simulate asynchronous activity.

## How to Run
1. Run the `Main` program to start multiple servers, consumers, and producers.
2. The servers listen on respective ports for producer and consumer connections.
3. Producers and consumers connect randomly per operation cycle.
4. Servers log storage values, rejections, and updates on the console.
5. The system runs until producers and consumers complete their designated repetitions.

## Requirements
- Java 8 or later.
- No external dependencies.
- All components run locally (localhost).

## File Structure
- `Main.java` – Starts servers, producers, and consumers.
- `Server.java` – Server implementation with thread-safe storage and connection handling.
- `Producer.java` – Producer process sending additions to server storage.
- `Consumer.java` – Consumer process sending subtractions to server storage.
- `README.md` – This documentation file.
