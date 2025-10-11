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

   Edit `automation-core/src/test/resources/config/config.json`:
   ```json
   {
     "appiumServerUrl": "http://127.0.0.1:4723",
     "udid": "YOUR_DEVICE_UDID",
     "appPackage": "com.atidcollege.bugtracker",
     "appActivity": "com.atidcollege.bugtracker.MainActivity"
   }
   ```

   Get your device UDID with the commend: `adb devices`

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
   
   ./gradlew test --tests "EditBugTest.testEditBugStatus_OpenToFixed"                                                                                         

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
        │   └── config.json              # Test configuration
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
2. Fills all required fields (Bug ID, Title, Steps, Expected/Actual Result, Status, Severity, Priority, Detected By)
3. Submits the form
4. Validates success message appears


#### testCreateBug_DataDriven
**What it does:**
1. Loads bug data from JSON file
2. Navigates to Create Bug form
3. Fills form using Bug model object
4. Submits the form
5. Navigates to View Bugs and verifies bug appears in list

---

### EditBugTest

#### testEditBugStatus_OpenToFixed
**What it does:**
1. Creates a new bug with status "Open"
2. Navigates to View Bugs tab
3. Finds the bug by Bug ID
4. Clicks Edit button for that bug
5. Changes status from "Open" to "Fixed"
6. Saves the changes
7. Verifies bug appears in "fixed" filtered results

---

### ListBugsTest

#### testListAllBugTitles
**What it does:**
1. Navigates to View Bugs tab
2. Retrieves all bug titles from the list
3. Prints all bug titles to console

---
