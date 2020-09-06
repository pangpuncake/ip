package duke.command;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import duke.storage.Storage;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.TaskList;
import duke.task.Todo;

/**
 * Parser class which is used to process commands passed down by the user and returns
 * the result of each command as a String.
 */
public class Parser {

    private TaskList list;
    private boolean isExit;

    /**
     * Constructor for Parser object.
     *
     * @param list The TaskList that the Parser object interacts with.
     */
    public Parser(TaskList list) {
        this.list = list;
        this.isExit = false;
    }

    /**
     * Checks if the command to exit has been issued
     *
     * @return True if 'bye' command has been issued, False otherwise.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * The function that processes the incoming command and executes different
     * functions depending on the command.
     *
     * @param command String command provided by the user.
     * @return String result of the provided command.
     * @throws DukeException A custom Exception that carries a message for the user if thrown.
     */
    public String processCommand(String command) throws DukeException {
        assert (command != null) : "Parser - processCommand: command input is null!";
        Pattern pattern = Pattern.compile("^(.*?)\\s(.*?)(?:\\s/..\\s?(.*))?$");
        Matcher matcher = pattern.matcher(command);
        if (matcher.find()) {
            String com = matcher.group(1);
            String task = matcher.group(2);
            String date = matcher.group(3);
            String index = matcher.group(2);

            switch (com) {
            case ("undo"):
            case ("done"):
            case ("delete"):
            case("find"):
                return this.processList(com, index);
            case ("todo"):
            case ("deadline"):
            case ("event"):
                return this.processTask(com, task, date);
            default:
                throw new DukeException("Sorry, I did not understand: " + command
                        + "\nUse \"help\" to look at available commands.");
        } else {
            switch (command) {
            case (""):
                return "Please provide an input!";
            case ("list"):
                return this.list.toString();
            case ("hello"):
                return "Hi! I'm Duke! Pleasure to meet you :)";
            case ("bye"):
                this.isExit = true;
                Storage storage = new Storage();
                return storage.updateFile(this.list) + "\nBye! Hope to see you again soon!";
            case ("help"):
                return "Accepted commands:\n"
                        + "hello - hello!\n"
                        + "list - show current list\n"
                        + "bye - saves the current list and exits the program\n"
                        + "\n"
                        + "todo <description> - create a todo Task\n"
                        + "event <description> /at <dd/MM/yyyy> - create an event Task (date is optional)\n"
                        + "deadline <description> /by <dd/MM/yyyy> - create a deadline Task (date is optional)\n"
                        + "\n"
                        + "done <index> - mark the specified task as done\n"
                        + "undo <index> - mark the specified task as not done\n"
                        + "delete <index> - deletes the specified task from the list";
            default:
                throw new DukeException("Sorry, I did not understand: " + command
                        + "\nUse \"help\" to look at available commands.");
            }
        }
    }

    /**
     * Helper function to process Task related commands.
     *
     * @param com The type of Task.
     * @param task The description of the Task.
     * @param date The date of the Task to be done at or by (if applicable).
     * @return String result of the function executed.
     * @throws DukeException A custom Exception that carries a message for the user if thrown.
     */
    private String processTask(String com, String task, String date) throws DukeException {
        assert (com != null) : "Parser - processTask: com is null!";
        assert (task != null) : "Parser - processTask: task is null!";
        switch(com) {
        case("todo"):
            if (task.equals("")) {
                throw new DukeException("Please write a task to be done, with \"todo <task>\"");
            }
            return list.addItem(new Todo(task));
        case("deadline"):
            if (task.equals("")) {
                throw new DukeException("Please write a deadline, with \"deadline <task> /by <date>\"");
            }
            try {
                return list.addItem(new Deadline(task, Parser.convertDate(date)));
            } catch (DateTimeParseException e) {
                throw new DukeException("Please write your date in the format \"dd/MM/yyyy\"");
            }
        case("event"):
            if (task.equals("")) {
                throw new DukeException("Please write an event, with \"event <task> /at <date>\"");
            }
            try {
                return list.addItem(new Event(task, Parser.convertDate(date)));
            } catch (DateTimeParseException e) {
                throw new DukeException("Please write your date in the format \"dd/MM/yyyy\"");
            }
        default:
            throw new DukeException("Something went wrong with processing the command! Please try again!");
        }
    }

    /**
     * Helper function to process list related commands.
     *
     * @param com The command keyword that interacts with the list.
     * @param index The index of which the command wishes to act on.
     * @return String result of the function executed.
     * @throws DukeException A custom Exception that carries a message for the user if thrown.
     */
    private String processList(String com, String index) throws DukeException {
        assert (com != null) : "Parser - processList: com is null!";
        assert (index != null) : "Parser - processList: task is null!";
        switch(com) {
        case("done"):
            if (index.equals("")) {
                throw new DukeException("Please choose a task to mark as done, with \"done <task number>\"");
            }
            return list.markDone(Integer.parseInt(index) - 1);
        case("undo"):
            if (index.equals("")) {
                throw new DukeException("Please choose a task to undo, with \"undo <task number>\"");
            }
            return list.revertDone(Integer.parseInt(index) - 1);
        case("delete"):
            if (index.equals("")) {
                throw new DukeException("Please choose a task to delete, with \"delete <task number>\"");
            }
            return list.deleteItem(Integer.parseInt(index) - 1);
        case("find"):
            if (index.equals("")) {
                throw new DukeException("Please input a word to find tasks with, using \"find <word>\"");
            }
            return list.findWord(index);
        default:
            throw new DukeException("Something went wrong with processing the command! Please try again!");
        }
    }

    /**
     * Helper function to convert a date String into a LocalDate object.
     *
     * @param date The date String to be converted.
     * @return LocalDate object that follows the input date String.
     * @throws DateTimeParseException thrown if the inputted date is invalid.
     */
    private static LocalDate convertDate(String date) throws DateTimeParseException {
        if (date == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(date, formatter);
    }
}
