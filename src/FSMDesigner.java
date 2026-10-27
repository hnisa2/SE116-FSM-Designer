import java.util.Scanner;
public class FSMDesigner {

    public static void main(String[] args) {
        System.out.println("FSM DESIGNER 0.1 " + java.time.LocalDateTime.now());
        FSM fsm = new FSM();

        if (args.length > 1) {
            System.out.println("Usage: java FSMDesigner [commands.txt]");
            return;
        }
        if (args.length == 1) {
            fsm.loadFSMText("LOAD " + args[0], null);
            return;
        }

        Scanner scanner = new Scanner(System.in);
        StringBuilder command = new StringBuilder();

        while (true) {
            System.out.print("? ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String line = scanner.nextLine().trim();

            // Eğer satır sadece yorum ise tamamen atla
            if (line.startsWith(";") || line.isBlank()) {
                continue;
            }

            // Satır içinde noktalı virgül varsa yorum kısmını kes
            int semicolon = line.indexOf(';');
            if (semicolon != -1) {
                line = line.substring(0, semicolon);
            }

            command.append(line).append(" ");

            // Noktalı virgül ile bitmişse, komut hazır demektir
            if (semicolon != -1) {
                String fullCommand = command.toString().trim();
                command.setLength(0); // Temizle

                String upperCommand = fullCommand.toUpperCase(java.util.Locale.ROOT);

                if (fullCommand.equalsIgnoreCase("exit")) {
                    System.out.println("TERMINATED BY USER");
                    break;
                } else if (upperCommand.startsWith("SYMBOLS")) {
                    fsm.processSymbols(fullCommand);
                } else if (upperCommand.startsWith("STATES")) {
                    fsm.processStates(fullCommand);
                } else if (upperCommand.startsWith("INITIAL-STATE")) {
                    fsm.processInitialState(fullCommand);
                } else if (upperCommand.startsWith("FINAL-STATES")) {
                    fsm.processFinalStates(fullCommand);
                } else if (upperCommand.startsWith("TRANSITIONS")) {
                    fsm.processTransitions(fullCommand);
                } else if (upperCommand.startsWith("EXECUTE")) {
                    fsm.executeInput(fullCommand);
                } else if (upperCommand.startsWith("PRINT")) {
                    fsm.printFSM();
                } else if (upperCommand.startsWith("CLEAR")) {
                    fsm.clearFSM();
                }
                else if (upperCommand.startsWith("COMPILE")) {
                    fsm.compileFSMText(fullCommand);
                } else if (upperCommand.startsWith("LOAD")) {
                    fsm.loadFSMText(fullCommand, cmd -> {
                        // satırdan önce noktalı virgül ve yorum kısmı silinir
                        int semi = cmd.indexOf(';');
                        if (semi != -1) {
                            cmd = cmd.substring(0, semi).trim();
                        }

                        if (!cmd.isEmpty()) {
                            String upper = cmd.toUpperCase();

                            if (upper.startsWith("SYMBOLS")) fsm.processSymbols(cmd);
                            else if (upper.startsWith("STATES")) fsm.processStates(cmd);
                            else if (upper.startsWith("INITIAL-STATE")) fsm.processInitialState(cmd);
                            else if (upper.startsWith("FINAL-STATES")) fsm.processFinalStates(cmd);
                            else if (upper.startsWith("TRANSITIONS")) fsm.processTransitions(cmd);
                            else if (upper.startsWith("EXECUTE")) fsm.executeInput(cmd);
                            else if (upper.startsWith("PRINT")) fsm.printFSM();
                            else if (upper.startsWith("CLEAR")) fsm.clearFSM();
                            else if (upper.startsWith("LOG")) fsm.handleLogCommand(cmd);
                            else if (upper.startsWith("COMPILE")) fsm.compileFSMText(cmd);
                            else fsm.println("Unknown command in file: " + cmd);
                        }
                    });
                }
                else if (upperCommand.startsWith("LOG")) {
                    fsm.handleLogCommand(fullCommand);
                }
                else {
                    System.out.println("Unknown command: " + fullCommand);
                }
            }
        }
        scanner.close();
    }
}