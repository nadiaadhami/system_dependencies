package system_dependencies;

import java.util.*;

public class SystemDependencies {

    public static Map<String, Set<String>> dependencies = new HashMap<>();
    public static Set<String> installed = new HashSet<>();

    public static void main(String[] args) {
        String commands;
        commands = "DEPEND TELNET TCPIP NETCARD\n" +
                "DEPEND TCPIP NETCARD\n" +
                "DEPEND DNS TCPIP NETCARD\n" +
                "DEPEND BROWSER TCPIP HTML\n" +
                "INSTALL NETCARD\n" +
                "INSTALL TELNET\n" +
                "INSTALL foo\n" +
                "REMOVE NETCARD\n" +
                "INSTALL BROWSER\n" +
                "INSTALL DNS\n" +
                "LIST\n" +
                "REMOVE TELNET\n" +
                "REMOVE NETCARD\n" +
                "REMOVE DNS\n" +
                "REMOVE NETCARD\n" +
                "INSTALL NETCARD\n" +
                "REMOVE TCPIP\n" +
                "REMOVE BROWSER\n" +
                "REMOVE TCPIP\n" +
                "LIST\n" +
                "END\n";

        String[] commandArray = commands.split("\n");
        for (String commandString: commandArray) {
            System.out.println(commandString);
            String[] tokens = commandString.split(" ");
//            StringTokenizer stringTokenizer = new StringTokenizer(commandString);
//            while (stringTokenizer.hasMoreTokens()) {
//                String element = (String) stringTokenizer.nextElement();
//            }
            String command = tokens[0];
            if (command.equals("DEPEND")) {
                String toInstall = tokens[1];
                Set<String> set = new HashSet<>();
                for (int i=2; i<tokens.length ; i++) {
                    set.add(tokens[i]);
                }
                dependencies.put(toInstall, set);
            } else if (command.equals("INSTALL")) {
                String toInstall = tokens[1];
                if (installed.contains(toInstall)) {
                    System.out.println("   \t"+toInstall+" is already installed.");
                } else {
                    install(tokens[1]);
                }
            } else if (command.equals("REMOVE")) {
                String toRemove = tokens[1];
                if (!installed.contains(toRemove)) {
                    System.out.println("\t"+toRemove+" is not installed");
                } else if (!isNeeded(toRemove)) {
                    installed.remove(tokens[1]);
                    System.out.println("\tRemoving "+toRemove);
                    if (dependencies.containsKey(tokens[1])) {
                        for (String dep : dependencies.get(toRemove)) {
                            if (!isNeeded(dep)) {
                                installed.remove(dep);
                                System.out.println("\tRemoving "+dep);
                            }
                        }
                    }
                } else {
                    System.out.println("\t"+toRemove + " is needed!");
                };
            } else if (command.equals("LIST")) {
                list();
            } else if (command.equals("END")) {
                System.out.println("goodbye!");
                break;
            }
        }
    }
    // recursive
    public static void install(String item) {
        // 1 - install item
        installed.add(item);
        System.out.println("\tInstalling "+item);
        // 2 - install dependencies
        if (dependencies.containsKey(item)) {
            Set<String> items = dependencies.get(item);
            for (String s : items) {
                if (!installed.contains(s)) {
                    install(s);
                }
            }
        }
    }
    public static boolean isNeeded(String item) {
        boolean needed = false;
        // go through installed items and make sure this is not one of the dependencies
        for (String installedItem: installed) {
            if (dependencies.containsKey(installedItem)) {
                Set<String> deps = dependencies.get(installedItem);
                if (deps.size() > 0 && deps.contains(item)) {
                    needed = true;
                    break;
                }
            }
        };
        return needed;
    }
    public static void list() {
        for (String item: installed) {
            System.out.println("  "+item);
        }
    }
}

/*
Command Syntax 	Interpretation/Response
DEPEND item1 item2 [item3 ...] 	item1 depends on item2 (and item3 ...)
INSTALL item1 	install item1 and those on which it depends
REMOVE item1 	remove item1, and those on whch it depends, if possible
LIST 	list the names of all currently-installed components

*/