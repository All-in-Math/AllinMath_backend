# AllinMath Backend

This is the backend service for the All-in-Math application, built with Spring Boot.

## Prerequisites

- **Java 17**: Ensure you have Java 17 installed. The run script assumes it is located at `C:\Program Files\Java\jdk-17`. If your installation is different, you may need to update the `run_backend.bat` file and set your `JAVA_HOME` environment variable accordingly.

## Configuration

This application uses a `.env` file for configuration. You need to set up the Firebase configuration path correctly for the app to function.

### Setting up `FIREBASE_CONFIG_PATH`

1.  Locate the `.env` file in the root directory of the project.
2.  Locate the Firebase service account JSON file (e.g., `all-in-math-532d1cf24946.json`) in the project root.
3.  Open `.env` and find the `FIREBASE_CONFIG_PATH` variable.
4.  Update the value to the **Full Absolute Path** of the JSON file on your machine.

**Example:**

If your project is located at `C:\Users\username\Documents\OOP\Projects\AllinMath_backend`, your `.env` entry should look like this:

```properties
FIREBASE_CONFIG_PATH=C:\Users\username\Documents\OOP\Projects\AllinMath_backend\all-in-math-532d1cf24946.json
```

**Note:** Ensure you use backslashes (`\`) for Windows paths.

## How to Run

### Windows

To run the application on Windows, simply double-click or run the included batch script from a command prompt:

```cmd
.\run_backend.bat
```

Alternatively, if you want to run it manually using the Maven wrapper:

```cmd
.\mvnw.cmd spring-boot:run
```

The application will start and be accessible at `http://localhost:8080`
