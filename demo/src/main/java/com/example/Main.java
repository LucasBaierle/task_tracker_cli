package com.example;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner; 
//$ java -cp target/classes com.example.Main add 12

public class Main {

    public static void main(String[] args) {
        
        Map<Integer, Task> tasks = loadTasks();

        switch (args[0].toLowerCase()) {
            case "add":

                addTask(tasks, args);

                break;
            case "update":
                    
                updateTaskDescription(tasks, args);

                break;
            case "mark-in-progress":
                    
                updateTaskStatus(tasks, args);

                break;
            case "mark-done":
                    
                updateTaskStatus(tasks, args);

                break; 
            case "delete":
                    
                deleteTask(tasks, args);

                break;
            case "list":
                    
                listTasks(tasks, args);

                break;
            default:
                break;
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

        } catch (Exception e) {

        }
        
        return storage;
    }

    public static void addTask(Map<Integer, Task> tasks, String[] args){
        String taskDescription = args[1];
        Integer taskId = tasks.size()+1; //todo, verifar se é unico

        tasks.put(taskId, new Task(taskId, taskDescription, Status.NOTDONE));
    }

    public static void updateTaskDescription(Map<Integer, Task> tasks, String[] args){
        Integer taskId = Integer.valueOf(args[1]);
        String taskDescription = args[2];
 
        Task task = tasks.get(taskId); //todo, verificar se existe
        task.description = taskDescription;
        task.updatedAt = LocalTime.now();
    }

    public static void updateTaskStatus(Map<Integer, Task> tasks, String[] args){
        Integer taskId = Integer.valueOf(args[1]);
        Status status = args[0].equals("mark-in-progress") ? Status.INPROGRESS : args[0].equals("mark-done") ? Status.DONE : Status.NOTDONE;

        Task task = tasks.get(taskId); //todo, verificar se existe
        task.status = status;
        task.updatedAt = LocalTime.now();
    }

    public static void deleteTask(Map<Integer, Task> tasks, String[] args){
        Integer taskId = Integer.valueOf(args[1]);
 
        tasks.remove(taskId); //todo, verificar se existe
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
       
        for(Task task : tasks.values()){
            if(all)
                System.out.println(task.toString());
            if(!all && task.status.equals(status))
                System.out.println(task.toString());
        } //todo, avisar que nao há registros nesse status.

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
}
