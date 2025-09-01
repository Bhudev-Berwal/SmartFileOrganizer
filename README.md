Smart File Organizer
A modern, intuitive desktop application built with Java and JavaFX that automatically organizes files into clean, categorized subfolders. This project was built from the ground up, focusing on a user-friendly experience, a polished visual design, and robust functionality.

(Note: This is a sample screenshot. You should take a high-quality screenshot of your final running application, upload it to a site like Imgur, and replace the URL above with your own.)

✨ Features
Beautiful, Modern UI: A clean and professional user interface designed with JavaFX and styled with custom CSS to provide an excellent user experience.

In-Place Organization: Select any folder (like your "Downloads" folder), and the application will create categorized subfolders (PDFs, Images, Documents, etc.) directly within it.

Real-Time Feedback: A progress window appears during the organization process, giving you confidence that the application is working.

Automatic Navigation: Once the organization is complete, the application automatically opens the folder for you to immediately see the results.

Activity History: The "Recently Organized" list updates in real-time, providing a history of the folders you've organized and how many files were moved.

🛠️ Technologies & Tools Used
Core: Java 21

User Interface: JavaFX

Styling: CSS 3

Version Control: Git & GitHub

IDE: Visual Studio Code

🚀 How to Run from Source Code
To run this project, you'll need to have the following installed on your system:

Java Development Kit (JDK) 21 or higher

JavaFX SDK 21 or higher

Visual Studio Code with the Extension Pack for Java

Step-by-Step Instructions
Clone the Repository:

git clone [https://github.com/Bhudev-Berwal/SmartFileOrganizer.git](https://github.com/Bhudev-Berwal/SmartFileOrganizer.git)
cd SmartFileOrganizer


Configure VS Code:

Link JavaFX: Open the Command Palette (Ctrl+Shift+P), find Java: Configure Classpath, and under "Referenced Libraries," add all the .jar files from your JavaFX SDK's lib folder.

Configure the Runner: Create a .vscode/launch.json file and add the following configuration. Crucially, update the --module-path to point to the exact location of your JavaFX SDK's lib folder.

{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "Launch Smart File Organizer",
            "request": "launch",
            "mainClass": "GuiApp",
            "projectName": "SmartFileOrganizer",
            "vmArgs": "--module-path \"C:/Path/To/Your/javafx-sdk-21/lib\" --add-modules javafx.controls,javafx.fxml"
        }
    ]
}

Run the Application:

Open the Run and Debug view in VS Code.

Select "Launch Smart File Organizer" from the dropdown and click the green play button.

Future Enhancements
Persistent History: Save the "Recently Organized" list to a file so it persists between application launches.

Customizable Rules: Allow users to create, edit, and delete their own organization rules directly from the UI.

Create a Native Installer: Package the application using jpackage to create a distributable .exe for Windows, removing the need for users to have Java or JavaFX installed.