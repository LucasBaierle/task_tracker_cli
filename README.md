# Task Manager CLI

## Introduction

This is a simple command-line interface (CLI) application built in Java to manage tasks. It allows users to add, update, delete, and list tasks with different statuses. All tasks are saved locally in a `storage.txt` file, ensuring they persist between sessions.

## Features

- **Add a task:** Create a new task with a description.  
- **Update a task:** Change the description or status of an existing task.  
- **Delete a task:** Remove a task by its ID.  
- **List tasks:** View all tasks or filter them by status (`done`, `todo`, `in-progress`).  
- **Local Storage:** Tasks are saved in a `storage.txt` file in a JSON-like format.  

## Installation

Clone the repository:  
`git clone https://github.com/LucasBaierle/task_tracker_cli`

Compile the source code:  
`javac Main.java`

## Usage

Run the application:  
`java Main <command> [arguments]`

### Commands

- **help:** Displays the list of available commands and their usage.  
`java Main help`

- **add:** Adds a new task. The task description must be enclosed in quotes.  
`java Main add "Buy groceries"`

- **update:** Updates the description of an existing task using its ID.  
`java Main update 1 "Buy groceries and cook dinner"`

- **mark-in-progress:** Changes a task's status to "in progress".  
`java Main mark-in-progress 1`

- **mark-done:** Changes a task's status to "done".  
`java Main mark-done 1`

- **delete:** Deletes a task by its ID.  
`java Main delete 1`

- **list:** Lists all tasks. You can also filter the list by status.  
List all tasks: `java Main list`  
List tasks with a specific status:  
`java Main list done`  
`java Main list todo`  
`java Main list in-progress`
