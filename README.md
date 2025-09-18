# PuTTY AI Assistant

An AI-powered assistant that monitors PuTTY sessions in real-time, analyzes terminal output, and provides intelligent solutions to common issues.

## Features

- Real-time monitoring of PuTTY sessions
- Automatic detection of common SSH and terminal errors
- AI-powered analysis of session data
- Question-answering capability based on terminal content
- Simple console-based interface

## Requirements

- Java 8 (JDK 1.8)
- Apache Ant for building
- Windows operating system

## Installation

1. Ensure Java 8 is installed on your system
2. Install Apache Ant if not already installed
3. Clone or download this repository
4. Run the build script to compile the application

```
.\build.bat
```

## Usage

1. Start the application using the run script:

```
.\run.bat
```

2. The application will automatically detect running PuTTY sessions
3. Select a session from the dropdown to view its output
4. Ask questions about the session or get help with issues

## How It Works

The PuTTY AI Assistant monitors running PuTTY processes on your system and captures their output. It uses pattern matching and AI analysis to identify common issues and provide solutions. You can also ask specific questions about your terminal session, and the assistant will analyze the data to provide helpful answers.

## Troubleshooting

- If the application doesn't detect PuTTY sessions, ensure PuTTY is running
- If you encounter build errors, verify that Java 8 and Ant are properly installed
- For any other issues, check the console output for error messages

## License

This project is open source and available under the MIT License.