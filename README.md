# Mobile Automation Framework - ATID Bug Tracker Application

A mobile automation framework for testing the ATID Bug Tracker Android application using Appium, Java 11, Spring DI, and Page Object Model.

---

## Setup and Run

### Prerequisites

1. **Install JDK 11** - 
2. **Install Android Studio**
3. **Install Node.js and Appium**
   ```bash
   npm install -g appium
   appium driver install uiautomator2
   ```
4. **Configure Environment Variables**
   - JAVA_HOME → JDK path
   - ANDROID_HOME → Android SDK path
   - Add `%ANDROID_HOME%\platform-tools` to PATH

### Setup Steps

1. **Install the APK**
   ```bash
   # Download from: https://atidcollege.co.il/downloads/APKs/atid-bug-tracker.apk
   adb install atid-bug-tracker.apk
   ```

2. **Configure the framework**

   Copy the template configuration file and update it with your device details:
   ```bash
   cd automation-core/src/test/resources/config/
   cp config.example.json config.json
   ```

   Edit `config.json` and update these fields:
   - `deviceName`: Your device name (e.g., "Samsung Galaxy S21")
   - `udid`: Your device UDID (get it by running `adb devices`)

   **Important:** The `config.json` file is in `.gitignore` and should NOT be committed to Git.
   Each developer maintains their own local configuration.

3. **Start Appium server**
   ```bash
   appium
   ```

4. **Run tests**
   ```bash
   # All tests
   .\gradlew :automation-core:test

   # Example for specific test class
   .\gradlew :automation-core:test --tests "CreateBugTest"

   # Specific test method
   ./gradlew :automation-core:test --tests "CreateBugTest.testCreateBugBasic"                                                                                             

   ./gradlew :automation-core:test --tests "com.automation.tests.bugtracker.CreateBugTest.testCreateBug_DataDriven"
   
   ./gradlew :automation-core:test --tests "com.automation.tests.bugtracker.ListBugsTest.testListAllBugTitles"   
   
   ./gradlew test --tests "EditBugTest.testEditBugStatus_OpenToClosed"                                                                                         

   ```

---

## Framework Structure and Design Decisions

### Project Structure

```
automation-core/
├── src/main/java/com/automation/
│   ├── config/
│   │   └── AutomationConfig.java         # Spring DI configuration
│   ├── core/
│   │   ├── config/
│   │   │   ├── ConfigReader.java         # Configuration interface
│   │   │   └── JsonConfigReader.java     # JSON config implementation
│   │   └── driver/
│   │       ├── DriverManager.java        # Driver management interface
│   │       └── AppiumDriverManager.java  # Appium driver implementation
│   ├── pages/
│   │   ├── base/
│   │   │   ├── BasePage.java            # Base class for all pages
│   │   │   └── BaseFormPage.java        # Base class for form pages (extends BasePage)
│   │   ├── components/
│   │   │   └── NavigationBar.java       # Reusable navigation component
│   │   ├── bugtracker/
│   │   │   ├── HomePage.java            # Home page object
│   │   │   ├── CreateBugPage.java       # Create bug form page
│   │   │   ├── BugsListPage.java        # View bugs list page
│   │   │   └── EditBugPage.java         # Edit bug form page
│   │   └── locators/
│   │       └── BugTrackerLocators.java  # Centralized element locators
│   ├── models/
│   │   └── Bug.java                     # Bug model (DTO)
│   ├── enums/
│   │   ├── BugStatus.java               # Bug status enum
│   │   ├── BugSeverity.java             # Bug severity enum
│   │   └── BugPriority.java             # Bug priority enum
│   ├── utils/
│   │   ├── WaitHelper.java              # Explicit wait utilities
│   │   ├── GestureHelper.java           # Mobile gesture utilities
│   │   └── DataProvider.java            # Test data loader
│   ├── exceptions/
│   │   ├── AutomationException.java     # Base exception
│   │   ├── ConfigurationException.java  # Config-related exceptions
│   │   ├── DriverInitializationException.java  # Driver exceptions
│   │   └── ElementNotFoundException.java       # Element exceptions
│   └── reporting/
│       └── ExtentReportManager.java     # ExtentReports manager
│
└── src/test/
    ├── java/com/automation/
    │   ├── tests/
    │   │   ├── base/
    │   │   │   └── BaseTest.java        # Base class for all tests
    │   │   └── bugtracker/
    │   │       ├── CreateBugTest.java   # Create bug tests
    │   │       ├── EditBugTest.java     # Edit bug tests
    │   │       └── ListBugsTest.java    # List bugs tests
    │   └── reporting/
    │       └── TestListener.java        # JUnit test listener
    └── resources/
        ├── config/
        │   ├── config.json              # Test configuration (not in Git - created from template)
        │   └── config.example.json      # Configuration template (committed to Git)
        └── testdata/
            ├── bugs.json                # Test data for bugs
            └── files/
                └── sample.jpg           # Test file for attachments
```

### Key Design Decisions

#### 1. Page Object Model (POM)
Separates page structure from test logic for maintainability.

#### 2. NavigationBar Component Pattern
Reusable navigation component using composition.
The app has navigation tabs on all screens, so instead of duplicating navigation logic, I use a shared NavigationBar component.

#### 3. getNavigationBar()  in BaseTest
Tests can navigate from any page without needing specific page instances.

#### 4. Spring Dependency Injection
Automatic dependency management through Spring.

#### 5. OOP Principles Implementation
The framework demonstrates core object-oriented programming principles:

- **Abstraction**: BasePage provides abstract interface for all pages. ConfigReader and DriverManager are interfaces with subclasses implementations.
- **Encapsulation**: Page objects hide internal element locators and implementation details. Tests interact through public methods only.
- **Inheritance Hierarchy**: The framework uses a well-structured inheritance tree:
  - `BasePage` → Common functionality for ALL pages (HomePage, BugsListPage, form pages)
  - `BaseFormPage extends BasePage` → Form-specific functionality (dropdowns, date pickers, file attachment)
  - `CreateBugPage extends BaseFormPage` → Create bug form
  - `EditBugPage extends BaseFormPage` → Edit bug form
- **Interface Segregation Principle**: Pages only inherit methods they actually need. HomePage and BugsListPage extend BasePage directly (no form methods), while CreateBugPage and EditBugPage extend BaseFormPage (with form methods). This prevents "fat" base classes with unused methods.
- **Polymorphism**: Different implementations of ConfigReader (JsonConfigReader) and DriverManager (AppiumDriverManager) can be used.
- **Composition**: BasePage has a NavigationBar component rather than inheritance - composition over inheritance when it's better.
- **Single Responsibility**: Each class has one clear purpose - WaitHelper handles waits, GestureHelper handles gestures, CreateBugPage manages only bug creation...
- **Don't Repeat Yourself**: Common functionality is extracted to base classes as reusable protected methods. Methods like `isLoaded()`, `clearAndType()`, `attachFile()` are shared across page objects, eliminating code duplication.
 
#### 6. Data-Driven Testing
External test data in JSON format, separated from test logic.

---


## What Each Test Does and Validates

### CreateBugTest

#### testCreateBugBasic
**What it does:**
1. Navigates to Create Bug form (works from any page)
2. Fills all required fields (Bug ID, Date, Title, Steps to Recreate, Expected Result, Actual Result, Status, Severity, Priority, Detected By, Fixed By, Date Closed)
3. Attaches a file
4. Submits the form
5. Navigates to View Bugs tab and verifies the bug appears in the list by Bug ID

**Validation:** Waits for bug with the specified Bug ID to appear in the bugs list, confirming successful creation.


#### testCreateBug_DataDriven
**What it does:**
1. Loads bug data from JSON file (testdata/bugs.json)
2. Navigates to Create Bug form
3. Fills form using Bug model object (data-driven approach)
4. Submits the form
5. Navigates to View Bugs tab and verifies bug appears in list by Bug ID

**Validation:** Waits for bug with the specified Bug ID to appear in the bugs list, confirming successful creation.

---

### EditBugTest

#### testEditBugStatus_OpenToClosed
**What it does:**
1. Creates a new bug with status "Open" (Bug ID: 905)
2. Navigates to View Bugs tab and waits for the bug to appear
3. Clicks Edit button for that specific bug (using Bug ID to identify it)
4. Changes status from "Open" to "Closed"
5. Updates Date Closed field
6. Attaches a file
7. Saves the changes
8. Clicks "Closed" filter button
9. Verifies bug appears in the "Closed" filtered results

**Validation:** Confirms the bug is found in the Closed filter view, proving the status update was successful.

---

### ListBugsTest

#### testListAllBugTitles
**What it does:**
1. Navigates to View Bugs tab
2. Retrieves all bug titles from the list (scrolls through entire list)
3. Prints all bug titles to console with numbering
4. Handles empty list case gracefully

**Output:**
- If bugs exist: Prints numbered list of all bug titles
- If list is empty: Prints "No bugs in the list" and passes the test

**Note:** This test extracts only the bug titles (without Bug IDs) and scrolls through the entire list to capture all bugs.

---
