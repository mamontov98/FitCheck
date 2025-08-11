FitCheck – Fitness Tracking App
📌 Overview
FitCheck is a personal fitness tracking Android application that allows users to set nutrition and fitness goals, track daily progress, and receive real-time feedback.
The app stores data in the cloud (Firebase) and provides a smooth user experience with live updates for progress bars and calorie calculations.

🚀 Features
Personal Goal Setup – Set daily targets for carbs, protein, fat, steps, sleep hours, and weight.

Automatic Calorie Calculation – Calculates calories based on the entered macronutrients.

Daily Progress Tracking – Save daily values and compare them with your set goals.

Real-Time Updates – Progress bars and calorie counts update live while typing.

Motivational Feedback – Toast messages when a goal is reached.

Bottom Navigation Bar – Quick navigation between Home, Workouts, and Weekly Metrics screens.

Cloud Storage – Uses Firebase to save and retrieve user data.

📂 Main Components
SetUpGoals ActivitySetUpGoals
Purpose: Screen for setting user goals.

Key Methods:

loadGoalsToFields() – Loads saved goals from Firebase into input fields.

setupButton() – Saves the entered goals to Firebase.

setupCaloriesCalculator() – Adds listeners to macro fields to update calories live.

Database Usage: Reads and writes the user's goals document in Firebase.

DailyTrackerActivityDailyTrackerActivity
Purpose: Screen for tracking daily progress.

Key Methods:

setGoalsLimits() – Sets progress bar max values based on goals.

loadDailyEntry() – Loads today’s entry from Firebase.

saveEntryFromUI() – Saves entered daily values to Firebase.

setupAutoUpdateProgress() – Listens for input changes and updates progress live.

giveFeedback() – Displays feedback when a goal is reached.

Database Usage: Reads and writes the "DailyTrackerEntry" document in Firebase.

🛠️ Tech Stack
Language: Kotlin

Framework: Android SDK

Database: Firebase Firestore

UI Components: RelativeLayout, EditText, ProgressBar, Toast

Architecture: Activity-based UI + Manager classes for business logic and DB communication

📦 Installation
Open the project in Android Studio.

Make sure you have a valid google-services.json file in the app/ directory.

Build the project (Build > Make Project).

Run it on an emulator or a physical device.

▶️ Usage
In the main menu, go to Set Goals to define your personal targets.

Enter macronutrients (Carbs, Protein, Fat) – the app will auto-calculate calories.

In Daily Tracker, fill in your daily values and track your progress via progress bars.

Save the data and receive motivational feedback.

📌 Future Improvements
Add graphs for progress analysis.

Support for WearOS devices.

Option to share data with a personal coach.
