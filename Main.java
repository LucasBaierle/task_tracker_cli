import java.io.File;
import java.io.FileWriter;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner; 

public class Main {

    public static void main(String[] args) {
        
        Map<Integer, Task> tasks = loadTasks();

        switch (args[0].toLowerCase()) {
            case "help":
                printHelp();

                break;
            case "add":
                if(validateArgs(tasks, args))
                    addTask(tasks, args);

                break;
            case "update":
                if(validateArgs(tasks, args))
                    updateTaskDescription(tasks, args);

                break;
            case "mark-in-progress":
                if(validateArgs(tasks, args))
                    updateTaskStatus(tasks, args);

                break;
            case "mark-done":
                if(validateArgs(tasks, args))
                    updateTaskStatus(tasks, args);

                break; 
            case "delete":
                if(validateArgs(tasks, args))
                    deleteTask(tasks, args);

                break;
            case "list":
                if(validateArgs(tasks, args))
                    listTasks(tasks, args);

                break;
            default:
                System.out.println("Unrecognized command: '" + args[0] + "'. Use 'help' to see available commands.");
        }

        saveTasks(tasks);
    }

    public static Map<Integer, Task> loadTasks(){
        File storage = getFile();

        Map<Integer, Task> tasks = new HashMap<>();

        try {

            Scanner reader = new Scanner(storage);

            while (reader.hasNextLine()) {
                    
                Task task = fromJson(reader.nextLine()
                    .replace("[{", "{")
                    .replace("}]", "}")
                    .replace("},", "}")
                );

                tasks.put(task.id, task);
            }

            reader.close();

        } catch (Exception e) {}

        return tasks;
    }

    public static void saveTasks(Map<Integer, Task> tasks){
        File storage = getFile();

        try {
            FileWriter writer = new FileWriter(storage);

            int count = 1;

            for(Task task : tasks.values()){
                
                writer.write((count == 1 ? "[" : "") + task.toJson() + (count == tasks.size() ? "]" : ",\n"));

                count++;
            }

            writer.close();

        } catch (Exception e) {}
    }

    public static File getFile(){
        File storage = new File("storage.txt");
        
        try {
            if(!storage.exists())
                storage.createNewFile();

        } catch (Exception e) {}
        
        return storage;
    }

    public static void addTask(Map<Integer, Task> tasks, String[] args){
        String taskDescription = args[1];
        Integer taskId = getNewId(tasks);

        tasks.put(taskId, new Task(taskId, taskDescription, Status.NOTDONE));

        System.out.println("Task added successfully (ID: " + taskId + ").");
    }

    public static void updateTaskDescription(Map<Integer, Task> tasks, String[] args){
        Integer taskId = Integer.valueOf(args[1]);
        String taskDescription = args[2];

        Task task = tasks.get(taskId);
        task.description = taskDescription;
        task.updatedAt = LocalTime.now();

        System.out.println("Task successfully updated.");
    }

    public static void updateTaskStatus(Map<Integer, Task> tasks, String[] args){
        Integer taskId = Integer.valueOf(args[1]);
        Status status = args[0].equals("mark-in-progress") ? Status.INPROGRESS : Status.DONE;

        Task task = tasks.get(taskId);
        task.status = status;
        task.updatedAt = LocalTime.now();

        System.out.println("Task successfully updated.");
    }

    public static void deleteTask(Map<Integer, Task> tasks, String[] args){
        Integer taskId = Integer.valueOf(args[1]);

        tasks.remove(taskId);

        System.out.println("Task deleted.");
    }
    
    public static void listTasks(Map<Integer, Task> tasks, String[] args){
        if(List.of(args).size() == 1){
            listAllTasksOrByStatus(tasks, true, null);
            return;
        }

        switch (args[1].toLowerCase()) {
            case "done":
                listAllTasksOrByStatus(tasks, false, Status.DONE);
                break;
            case "todo":
                listAllTasksOrByStatus(tasks, false, Status.NOTDONE);
                break;
            case "in-progress":
                listAllTasksOrByStatus(tasks, false, Status.INPROGRESS);
                break;
            default:
                break;
        }
    }

    public static void listAllTasksOrByStatus(Map<Integer, Task> tasks, Boolean all, Status status){
       
        boolean foundSome = false;

        for(Task task : tasks.values()){
            if(all){
                System.out.println(task.toString());
                foundSome = true;
            }
            if(!all && task.status.equals(status)){
                foundSome = true;
                System.out.println(task.toString());
            }
        } 

        if(!foundSome)
            if(all)
                System.out.println("No tasks found.");
            else
                System.out.println("No tasks with status '" + status.getDescricao() + "' found.");
    }

    static class Task {

        Task(Integer id, String description, Status status){
            this.id = id;
            this.description = description;
            this.status = status;
            this.createdAt = LocalTime.now();
            this.updatedAt =  LocalTime.now();
        }

        Task(){}

        public Integer id;
        public String description;
        public Status status;
        public LocalTime createdAt;
        public LocalTime updatedAt;

        public String toJson(){
            return String.format(
                "{\"Id\":%d,\"Description\":\"%s\",\"Status\":\"%s\",\"CreatedAt\":\"%s\",\"UpdatedAt\":\"%s\"}",
                this.id,
                this.description,
                this.status,
                this.createdAt,
                this.updatedAt
            );
        }
        public String toString(){
            return String.format(
                "Task Id: %d, Description: %s, Status: %s, CreatedAt: %s, UpdatedAt: %s",
                this.id,
                this.description,
                this.status,
                this.createdAt,
                this.updatedAt
            );
        }
    }

    public static Task fromJson(String jString){
        jString = jString.trim().replace("{", "").replace("}", "");
        List<String> fields = new ArrayList<String>(List.of(jString.split(",")));

        Task task = new Task();

        for(String field : fields){

            String[] labelValue = field.split(":", 2);
            String label = labelValue[0].trim().replace("\"", "");
            String value = labelValue[1].trim().replace("\"", "");

            switch (label) {
                case "Id":
                    task.id = Integer.valueOf(value);
                    break;
                case "Description":
                    task.description = value;
                    break;
                case "Status":
                    task.status = Status.valueOf(value);
                    break;
                case "CreatedAt":
                    task.createdAt = LocalTime.parse(value);
                    break;
                case "UpdatedAt":
                    task.updatedAt = LocalTime.parse(value);
                    break;
                default:
                    break;
            }
        }

        return task;
    }


    public enum Status {
        DONE("done"), NOTDONE("not done"), INPROGRESS("in progress");

        private String description;

        Status(String description){
            this.description = description;
        }

        public String getDescricao() {
            return this.description;
        }
    }

    public static Integer getNewId(Map<Integer, Task> tasks){

        Integer initial = tasks.size() + 1;

        while(tasks.containsKey(initial))
            initial++;
        
        return initial;
    }

    public static Boolean validateArgs(Map<Integer, Task> tasks, String[] args){

        switch (args[0]) {
            case "add":
                if (args.length < 2){
                    System.out.println("Missing task description. Usage: add \"Task description\"");
                    return false;
                }
                break;
            case "update":
            case "mark-in-progress":
            case "mark-done":
            case "delete": 
            
                if(args.length < 2){
                    System.out.println("Missing task ID.");
                    return false;
                }
                
                if(!tasks.containsKey(Integer.valueOf(args[1]))){
                    System.out.println(String.format(
                        "Task with ID %s not found. Use 'list' to see available tasks.", args[1]
                    ));
                    return false;
                }

                break;
        
            default:
                break;
        }

        return true;
    } 

    public static void printHelp() {
        System.out.println("""
            Usage: <command> [options]

            Commands:
            add              Add a new task
            update           Update an existing task
            delete           Delete a task
            mark-in-progress Mark a task as in progress
            mark-done        Mark a task as done
            list             List all tasks or filter by status (done, todo, in-progress)

            Examples:
                add "Buy groceries"
                update 1 "Buy groceries and cook dinner"
                delete 1
                mark-in-progress 1
                mark-done 1
                list
                list done
                list todo
                list in-progress
        """);
    }
}
