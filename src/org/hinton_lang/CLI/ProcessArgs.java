package org.hinton_lang.CLI;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

import org.hinton_lang.Hinton;

public class ProcessArgs {
    String[] args;
    int currentArg = 0;

    public ProcessArgs(String... args) {
        this.args = args;
    }

    /**
     * Run the provided arguments.
     * 
     * @throws IOException
     */
    public void run() throws IOException {
        // If no arguments are provided, run the REPL
        if (args.length == 0) {
            Hinton.runPrompt();
        }

        // Otherwise run the command
        String command = this.args[0];
        currentArg = 1;

        switch (command) {
            case "run":
                doRunCommand();
                break;
            case "help":
            case "--h":
                doHelpCommand();
                break;
            default:
                System.out.println("==============================");
                System.out.println("'" + command + "' is not a valid command.");
                System.out.println("==============================");
                doHelpCommand();
        }
    }

    /**
     * Executes the 'run' command.
     * 
     * @throws IOException
     */
    public void doRunCommand() throws IOException {
        ArrayList<String> permissions = new ArrayList<>();

        // Gets the permission flags
        while (currentArg < this.args.length) {
            String arg = this.args[currentArg];

            if (arg.startsWith("--")) {
                permissions.add(arg);
            } else {
                break;
            }

            currentArg++;
        }

        // Gets the filepath
        Path base = Path.of(System.getProperty("user.dir"));
        Path file = Path.of(args[currentArg]);
        String filePath = base.resolve(file).normalize().toString();

        // Gets the programs arguments
        String[] programArgs = Arrays.copyOfRange(this.args, currentArg + 1, this.args.length);

        // // Debug only
        // System.out.println(Arrays.deepToString(permissions.toArray()));
        // System.out.println(filePath);
        // System.out.println(Arrays.deepToString(programFlags));

        // Run the file
        Hinton.setPermissions(permissions);
        Hinton.setProgramArgs(programArgs);
        Hinton.runFile(filePath);
    }

    /**
     * Executes the 'help' command
     */
    public void doHelpCommand() {
        System.out.println("Available Commands:");
        System.out.println("    run [program permissions]? [program path] [program args]?");
        System.out.println("        Executes the provided program with the provided permissions");
        System.out.println("    help");
        System.out.println("        Prints details about the available commands");
    }

}
