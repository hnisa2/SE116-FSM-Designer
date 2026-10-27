# FSM Designer

This is a group project we developed for SE116 - Introduction to Programming II in Spring 2025. It is a command-line program for defining and running deterministic finite state machines in Java.

We can declare symbols and states, add transitions, and test an input string. The program shows the states it visits and ends with YES or NO depending on whether the input is accepted.

## How to run

You need JDK 11 or newer. From the project folder:

```sh
javac -encoding UTF-8 -d build src/FSM.java src/FSMDesigner.java src/FSMTransition.java
java -cp build FSMDesigner
```

Enter one command at a time and end it with a semicolon. Anything after the first semicolon on a line is treated as a comment. A command can also continue onto the next line.

To run the example file:

```sh
java -cp build FSMDesigner examples/even-ones.txt
```

You can also build a JAR:

```sh
jar cfe fsm.jar FSMDesigner -C build .
java -jar fsm.jar
```

## Example

This machine accepts binary strings containing an even number of 1s:

```text
SYMBOLS 0 1;
STATES EVEN ODD;
INITIAL-STATE EVEN;
FINAL-STATES EVEN;
TRANSITIONS 0 EVEN EVEN, 1 EVEN ODD,
0 ODD ODD, 1 ODD EVEN;
EXECUTE 101;
EXECUTE 1;
EXIT;
```

The two execution results are:

```text
EVEN ODD ODD EVEN YES
EVEN ODD NO
```

## Commands

| Command | Purpose |
| --- | --- |
| `SYMBOLS 0 1;` | Add symbols. Use `SYMBOLS;` to list them. |
| `STATES Q0 Q1;` | Add states. The first state is the default initial state. |
| `INITIAL-STATE Q0;` | Set the initial state. |
| `FINAL-STATES Q1;` | Mark accepting states. |
| `TRANSITIONS 0 Q0 Q1;` | Add a transition: symbol, current state, next state. |
| `EXECUTE 001;` | Run an input string and print the state sequence. |
| `PRINT;` | Display the machine. |
| `COMPILE saved.txt;` | Save the machine as text commands. |
| `LOAD saved.txt;` | Read a text command file. |
| `CLEAR;` | Clear the current machine. |
| `LOG session.log;` | Start logging supported responses. Use `LOG;` to stop. |
| `EXIT;` | Exit the interactive program. |

Symbols are single letters or digits. State names are alphanumeric. Commands, symbols, and state names are case-insensitive.

## Files

- `FSM.java` stores the machine and handles its operations.
- `FSMDesigner.java` reads user commands and calls the methods in FSM.
- `FSMTransition.java` is the transition data class from our original project. The current FSM implementation stores transitions in nested maps.
- `examples/` contains a working machine and a short invalid-input example.

## Notes about this version

The original three-class structure is kept. Small fixes were made later for case handling, file arguments, the default initial state, and reaching the end of input.

Some features described in our course report are not complete in these source files. `COMPILE` writes text, not a serialized binary file. `PRINT filename;` is not implemented; use `COMPILE filename.txt;` to export instead. Logging does not capture the whole session. File paths containing spaces and nested LOAD commands are not supported.

While working on this project, we practised Java collections, command parsing, file operations, and working together with Git. Parsing transitions and handling semicolons were parts that needed particular care.
