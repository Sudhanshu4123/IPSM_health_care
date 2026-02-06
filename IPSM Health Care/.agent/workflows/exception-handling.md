---
description: How to handle exceptions in IPSM Health Care project
---

To ensure all types of exceptions are handled consistently across the application:

1. **Use the `ErrorHandler` Class**:
   Always use `ErrorHandler.showError(parentComponent, "User friendly message", exception)` inside catch blocks in Swing Frames.
   ```java
   try {
       // logic
   } catch (Exception e) {
       ErrorHandler.showError(this, "Failed to perform operation", e);
   }
   ```

2. **Database Operations**:
   Database calls in `DatabaseManager` should log specific messages with `System.err.println` and `e.printStackTrace()` for debugging, and return meaningful values (null/false) or throw exceptions for the UI to handle.

3. **Backend Global Exception Handling**:
   The backend uses `GlobalExceptionHandler.java` (under `exception` package) to intercept all controller errors and return them as JSON. If you add new custom exceptions, register them there.

4. **Global Swing Handler**:
   A global uncaught exception handler is set in `Main.java` to catch any runtime errors in the UI thread that weren't caught locally.

5. **Input Validation**:
   Always validate user input (like `NumberFormatException`) before processing to prevent technical errors from reaching the database layer.
