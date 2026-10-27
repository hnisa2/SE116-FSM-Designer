import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.io.*;

public class FSM {
    private String logFileName = null;
    private Set<String> symbols = new HashSet<>();
    private Set<String> states = new LinkedHashSet<>();
    private String initialState = null;
    private Set<String> finalStates = new LinkedHashSet<>();
    private Map<String, Map<String, String>> transitions = new HashMap<>();// Tüm geçişleri tutmak için

    public void processSymbols(String input) {
        input = input.toUpperCase(Locale.ROOT);//Function to process the symbols
        String[] parts = input.split("\\s+");
        if (parts.length == 1) {
            System.out.println("SYMBOLS: " + symbols);
        } else {
            for (int i = 1; i < parts.length; i++) {
                String s = parts[i];
                if (!s.matches("[a-zA-Z0-9]")) {
                    System.out.println("ERROR: invalid symbol " + s);
                    continue;
                }
                if (!symbols.add(s)) {
                    System.out.println("ERROR: " + s + " was already declared as a symbol");
                }
            }
        }
    }

    public void processStates(String input) {
        input = input.toUpperCase(Locale.ROOT);//Function to process the States
        String[] parts = input.split("\\s+");
        if (parts.length == 1) {
            System.out.println("STATES: " + states);
        } else {
            for (int i = 1; i < parts.length; i++) {
                String s = parts[i];
                if (!s.matches("[a-zA-Z0-9]+")) {
                    System.out.println("ERROR: invalid state " + s);
                    continue;
                }
                if (!states.add(s)) {
                    System.out.println("ERROR: " + s + " was already declared as a state");
                }
                if (initialState == null) {
                    initialState = s;
                }
            }
        }
    }
    public void processInitialState(String input) {
        input = input.toUpperCase(Locale.ROOT);
        String[] parts = input.split("\\s+");
        if (parts.length < 2) {
            println("ERROR: No state specified for INITIAL-STATE");
            return;
        }

        String state = parts[1];

        if (!state.matches("[a-zA-Z0-9]+")) {
            println("ERROR: Invalid state " + state);
            return; //geçersizse hiç atama yapılmaz
        }

        if (!states.contains(state)) {
            println("WARNING: " + state + " was not previously declared as a state");
            states.add(state); // geçerli ama daha önce tanımlanmamışsa, eklenir
        }

        initialState = state;
        println("Initial state set to: " + initialState);
    }
    public void processFinalStates(String input) {
        input = input.toUpperCase(Locale.ROOT);
        String[] parts = input.split("\\s+");
        if (parts.length < 2) {
            println("ERROR: No final states specified");
            return;
        }

        for (int i = 1; i < parts.length; i++) {
            String state = parts[i];

            if (!state.matches("[a-zA-Z0-9]+")) {
                println("ERROR: Invalid state " + state);
                continue;
            }

            if (!states.contains(state)) {
                println("WARNING: " + state + " was not previously declared as a state");
                states.add(state); // Uyarı verip FSM'e ekliyoruz
            }

            if (!finalStates.add(state)) {
                println("WARNING: " + state + " was already declared as a final state");
            }
        }
    }
    public void processTransitions(String input) {
        String trimmed = input.substring("TRANSITIONS".length()).trim();
        String[] transitionItems = trimmed.split(",");
        for (int j = 0; j < transitionItems.length; j++) {
            String newStr="";
            for (int i = 0; i < transitionItems[j].length(); i++) {
                newStr += (transitionItems[j].substring(i)).toUpperCase(Locale.ROOT);
                break;
            }
            transitionItems[j]=newStr;
        }

        for (String item : transitionItems) {
            String[] parts = item.trim().split("\\s+");
            if (parts.length != 3) {
                println("ERROR: transition must be <symbol> <currentState> <nextState> — found: " + item.trim());
                continue;
            }

            String symbol = parts[0];
            String currentState = parts[1];
            String nextState = parts[2];

            boolean valid = true;

            if (!symbols.contains(symbol)) {
                println("ERROR: invalid symbol '" + symbol + "' in transition: " + item.trim());
                valid = false;
            }

            if (!states.contains(currentState)) {
                println("ERROR: invalid current state '" + currentState + "' in transition: " + item.trim());
                valid = false;
            }

            if (!states.contains(nextState)) {
                println("ERROR: invalid next state '" + nextState + "' in transition: " + item.trim());
                valid = false;
            }

            if (!valid) {
                continue; // geçersizse bu geçişi atla
            }

            // geçerli geçiş -> ekle veya override
            transitions.putIfAbsent(currentState, new HashMap<>());
            Map<String, String> inner = transitions.get(currentState);

            if (inner.containsKey(symbol)) {
                println("WARNING: transition for <" + symbol + "," + currentState + "> already exists. Overwriting.");
            }

            inner.put(symbol, nextState);
        }
    }
    public void executeInput(String input) {
        input = input.toUpperCase(Locale.ROOT);
        String[] parts = input.trim().split("\\s+");
        if (parts.length < 2) {
            println("ERROR: No input string provided.");
            return;
        }

        String inputString = parts[1].toUpperCase(Locale.ROOT);

        if (initialState == null) {
            println("ERROR: Initial state is not set.");
            return;
        }

        String currentState = initialState;
        List<String> stateSequence = new ArrayList<>();
        stateSequence.add(currentState);

        for (char ch : inputString.toCharArray()) {
            String symbol = String.valueOf(ch);

            if (!symbols.contains(symbol)) {
                println("ERROR: Invalid symbol '" + symbol + "' in input.");
                return;
            }

            if (!transitions.containsKey(currentState) || !transitions.get(currentState).containsKey(symbol)) {
                println("ERROR: No transition for <" + symbol + "," + currentState + ">");
                println(String.join(" ", stateSequence) + " NO");
                return;
            }

            currentState = transitions.get(currentState).get(symbol);
            stateSequence.add(currentState);
        }

        String result = String.join(" ", stateSequence) + (finalStates.contains(currentState) ? " YES" : " NO");
        println(result);
    }
    public void printFSM() {
        System.out.println("SYMBOLS " + symbols);
        System.out.println("STATES " + states);
        System.out.println("INITIAL STATE " + (initialState != null ? initialState : "(none)"));
        System.out.println("FINAL STATES " + finalStates);

        System.out.println("TRANSITIONS");
        List<String> sortedStates = new ArrayList<>(transitions.keySet());
        Collections.sort(sortedStates);

        for (String from : sortedStates) {
            Map<String, String> trans = transitions.get(from);
            List<String> sortedSymbols = new ArrayList<>(trans.keySet());
            Collections.sort(sortedSymbols);

            for (String symbol : sortedSymbols) {
                String to = trans.get(symbol);
                println(symbol + " " + from + " " + to);
            }
        }

    }
    public void clearFSM() {
        symbols.clear();
        states.clear();
        transitions.clear();
        finalStates.clear();
        initialState = null;

        System.out.println("FSM cleared.");
    }
    public void compileFSMText(String input) {
        String[] parts = input.split("\\s+");
        if (parts.length < 2) {
            println("ERROR: No filename specified.");
            return;
        }

        String filename = parts[1];
        if (!(filename.endsWith(".txt") || filename.endsWith(".fsm") || filename.endsWith(".fs"))) {
            println("ERROR: Filename must end with .txt, .fsm or .fs");
            return;
        }
        try (PrintWriter out = new PrintWriter(filename)) {
            out.print("SYMBOLS");
            for (String s : symbols) out.print(" " + s);
            out.println(";");

            out.print("STATES");
            for (String s : states) out.print(" " + s);
            out.println(";");

            if (initialState != null)
                out.println("INITIAL-STATE " + initialState + ";");

            if (!finalStates.isEmpty()) {
                out.print("FINAL-STATES");
                for (String s : finalStates) out.print(" " + s);
                out.println(";");
            }

            out.print("TRANSITIONS ");
            boolean first = true;
            for (String from : transitions.keySet()) {
                for (Map.Entry<String, String> entry : transitions.get(from).entrySet()) {
                    if (!first) out.print(", ");
                    out.print(entry.getKey() + " " + from + " " + entry.getValue());
                    first = false;
                }
            }
            out.println(";");
            println("FSM exported as text to " + filename);
        } catch (IOException e) {
            println("ERROR writing file: " + e.getMessage());
        }
    }
    public void loadFSMText(String input, Consumer<String> executor) {
        String filename = input.replaceFirst("(?i)^LOAD\\s+", "").trim();

        if (filename.endsWith(";")) {
            filename = filename.substring(0, filename.length() - 1).trim();
        }

        if (!(filename.toLowerCase().endsWith(".txt") || filename.toLowerCase().endsWith(".fsm") || filename.toLowerCase().endsWith(".fs"))) {
            println("ERROR: Filename must end with .txt, .fsm or .fs");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            StringBuilder multiLineCommand = new StringBuilder();
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                // Satır yorumsa veya boşsa geç
                String trimmedLine = line.trim();
                if (trimmedLine.isEmpty() || trimmedLine.startsWith(";")) continue;

                multiLineCommand.append(trimmedLine).append(" ");

                if (trimmedLine.contains(";")) {
                    String cmd = multiLineCommand.toString();
                    multiLineCommand.setLength(0); // temizle

                    int semi = cmd.indexOf(';');
                    if (semi != -1) {
                        cmd = cmd.substring(0, semi).trim();
                    }

                    if (!cmd.isEmpty()) {
                        String upper = cmd.toUpperCase(Locale.ROOT);

                        if (upper.startsWith("SYMBOLS")) processSymbols(cmd);
                        else if (upper.startsWith("STATES")) processStates(cmd);
                        else if (upper.startsWith("INITIAL-STATE")) processInitialState(cmd);
                        else if (upper.startsWith("FINAL-STATES")) processFinalStates(cmd);
                        else if (upper.startsWith("TRANSITIONS")) processTransitions(cmd);
                        else if (upper.startsWith("EXECUTE")) executeInput(cmd);
                        else if (upper.startsWith("PRINT")) printFSM();
                        else if (upper.startsWith("CLEAR")) clearFSM();
                        else if (upper.startsWith("LOG")) handleLogCommand(cmd);
                        else if (upper.startsWith("COMPILE")) compileFSMText(cmd);
                        else println("Unknown command in file (line " + lineNumber + "): " + cmd);
                    }
                }
                lineNumber++;
            }
            if (multiLineCommand.length() > 0) {
                println("ERROR: Missing semicolon at end of file.");
            }
            println("FSM loaded from text file: " + filename);
        } catch (IOException e) {
            println("ERROR reading file: " + e.getMessage());
        }
    }
    public void handleLogCommand(String input) {
        String[] parts = input.split("\\s+");

        if (parts.length == 1) {
            if (logFileName != null) {
                println("STOPPED LOGGING");
                logFileName = null;
            } else {
                println("LOGGING was not enabled");
            }
            return;
        }

        logFileName = parts[1];
        // Clear previous content (overwrite)
        try (FileWriter fw = new FileWriter(logFileName, false)) {
            fw.write(""); // empty it
        } catch (IOException e) {
            println("Error: " + e.getMessage());
            logFileName = null;
        }

        println("Logging started to " + logFileName);
    }
    public void println(String msg) {
        System.out.println(msg);
        if (logFileName != null) {
            try (FileWriter fw = new FileWriter(logFileName, true)) {
                fw.write(msg + System.lineSeparator());
            } catch (IOException e) {
                System.out.println("Log error: " + e.getMessage());
            }
        }
    }
}