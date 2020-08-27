package duke.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TaskList {

    private List<Task> list;
    private int activeTasks = 0;
    private int completedTasks = 0;

    public TaskList() {
        this.list = new ArrayList<>();
    }

    private TaskList(List<Task> list) {
        this.list = list;
        for (Task t : this.list) {
            if (t.done) {
                this.completedTasks += 1;
            } else {
                this.activeTasks += 1;
            }
        }
    }

    public List<Task> getList() {
        return this.list;
    }

    public String addItem(Task item) {
        if (item == null) {
            return "Task is null! Nothing was added.";
        }
        this.list.add(item);
        if (item.done) {
            this.completedTasks += 1;
        } else {
            this.activeTasks += 1;
        }
        return "added: " + item + "\nActive Tasks: " +
                this.activeTasks + "\nCompleted Tasks: " + this.completedTasks;
    }

    public String deleteItem(int index) {
        if (index >= this.list.size() || index < 0) {
            return "Please choose a valid task to delete";
        }
        Task deletedTask = this.list.remove(index);
        if (deletedTask.done) this.completedTasks -= 1; else this.activeTasks -= 1;
        return "Noted. I have deleted the following task: \n"
                + deletedTask + "\nActive Tasks: " +
                this.activeTasks + "\nCompleted Tasks: " + this.completedTasks;
    }

    public String markDone(int index) {
        if (index >= this.list.size() || index < 0) {
            return "Please choose a valid task to mark as done";
        }
        if (!this.list.get(index).done) {
            this.activeTasks -= 1;
            this.completedTasks += 1;
            return this.list.get(index).markDone() + "\nActive Tasks: " +
                    this.activeTasks + "\nCompleted Tasks: " + this.completedTasks;
        }
        return "The task is already done!";
    }

    public String revertDone(int index) {
        if (index >= this.list.size() || index < 0) {
            return "Please choose a valid task to mark as not done";
        }
        if (this.list.get(index).done) {
            this.activeTasks += 1;
            this.completedTasks -= 1;
            return this.list.get(index).revertDone() + "\nActive Tasks: " +
                    this.activeTasks + "\nCompleted Tasks: " + this.completedTasks;
        }
        return "The task is not yet done!";
    }

    public String findWord(String word) {
        List<Task> filteredList = new ArrayList<>(this.list);
        filteredList = filteredList.stream()
                .filter(task -> task.task.contains(word))
                .collect(Collectors.toList());
        return "Using keyword: " + word + "\n" + new TaskList(filteredList).toString();
    }

    @Override
    public String toString() {
        if (this.list.size() == 0) {
            return "There are currently no tasks.";
        }
        String result = "Current tasks:\n";
        for (int i = 1; i <= this.list.size(); i++) {
            result += i + ". " + this.list.get(i - 1) + "\n";
        }
        result += "\nActive Tasks: " +
                this.activeTasks + "\nCompleted Tasks: " + this.completedTasks;
        return result;
    }
}
