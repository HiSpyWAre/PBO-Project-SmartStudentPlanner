# 📚 Smart Study Planner

A comprehensive JavaFX productivity application designed to help students manage their academic workload effectively.

![Java](https://img.shields.io/badge/Java-17+-orange.svg)
![JavaFX](https://img.shields.io/badge/JavaFX-17.0.2-blue.svg)
![License](https://img.shields.io/badge/license-MIT-green.svg)

## ✨ Features

### 🎯 Smart Task Management
- **Three Task Types**: Assignments, Exams, and Projects
- **Intelligent Priority System**: Automatic urgency calculation
- **Dependency Tracking**: Link related tasks
- **Status Monitoring**: Track progress from TODO to completion

### 📊 Dashboard
- Real-time statistics display
- Streak tracking for daily productivity
- XP and leveling system
- Most urgent tasks view
- Weekly productivity chart

### 🍅 Pomodoro Timer
- Customizable focus and break durations
- Session tracking
- Circular progress indicator
- Automatic break scheduling
- Long break after 4 sessions

### 📅 Calendar View
- Monthly task visualization
- Due date overview
- Interactive date selection

### 📈 Analytics
- Task completion rates
- Priority distribution charts
- Study pattern insights
- Productivity trends

### 🎴 Flashcard System
- **SM-2 Spaced Repetition Algorithm**
- Multiple deck support
- Interactive study mode
- Mastery tracking
- 4-level rating system (Again, Hard, Good, Easy)

### 🏆 Gamification
- XP earning system
- Level progression
- Achievement unlocking
- Streak rewards

## 🏗️ Architecture

### Design Patterns
- **MVC (Model-View-Controller)**: Clear separation of concerns
- **Observer Pattern**: Real-time UI updates
- **Factory Pattern**: Task creation
- **Strategy Pattern**: Different urgency calculations per task type

### Project Structure
```
SmartStudyPlanner/
├── src/
│   ├── model/              # Data models and business logic
│   │   ├── Task.java
│   │   ├── TaskManager.java
│   │   ├── UserProfile.java
│   │   ├── Flashcard.java
│   │   └── ...
│   ├── ui/                 # User interface views
│   │   ├── DashboardView.java
│   │   ├── TasksView.java
│   │   ├── PomodoroView.java
│   │   └── ...
│   ├── controller/         # Application controllers
│   │   ├── MainController.java
│   │   └── SmartScheduler.java
│   ├── resources/          # CSS and assets
│   │   └── styles.css
│   └── StudyPlannerApp.java
└── README.md
```

## 🚀 Getting Started

### Prerequisites
- Java JDK 17 or higher
- JavaFX SDK 17 or higher
- Git (for cloning)

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/YOUR_USERNAME/smart-study-planner.git
cd smart-study-planner
```

2. **Download JavaFX SDK**
   - Download from: https://gluonhq.com/products/javafx/
   - Extract to a location on your computer

3. **Compile the project**
```bash
javac -encoding UTF-8 \
      --module-path /path/to/javafx-sdk/lib \
      --add-modules javafx.controls,javafx.fxml \
      -d out \
      src/model/*.java \
      src/ui/*.java \
      src/controller/*.java \
      src/StudyPlannerApp.java
```

4. **Run the application**
```bash
java --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.fxml \
     -cp out \
     StudyPlannerApp
```

### Using Maven (Recommended)

1. **Install Maven** (if not already installed)
2. **Run the application**
```bash
mvn clean javafx:run
```

## 💡 Usage Guide

### Creating Tasks
1. Navigate to "Tasks" tab
2. Click "+ Add Task"
3. Fill in details (title, description, due date, priority)
4. Select task type (Assignment/Exam/Project)
5. Click "Add"

### Starting a Pomodoro Session
1. Go to "Pomodoro" tab
2. Adjust focus/break duration if needed
3. Click "Start"
4. Focus on your work until the timer ends
5. Rate your session quality

### Using Flashcards
1. Click "Flashcards" tab
2. Create a new deck or use sample decks
3. Add cards to your deck
4. Click "Study Now" to begin
5. Rate each card: Again, Hard, Good, or Easy

## 🎨 Customization

### Changing Theme Colors
Edit `src/resources/styles.css` to customize the color scheme.

Current theme: **Catppuccin Mocha**

### Adding New Task Types
1. Create a new class extending `Task`
2. Implement `calculateUrgencyScore()` method
3. Add to the factory in `TasksView`

## 🧪 Key Algorithms

### Urgency Scoring
Each task type calculates urgency differently:
- **Assignments**: Time pressure + priority + effort
- **Exams**: Higher base urgency + preparedness
- **Projects**: Completion percentage + time + priority

### Spaced Repetition (SM-2)
Flashcards use the SuperMemo-2 algorithm:
- Intervals: 1 day → 6 days → multiplied by ease factor
- Ease factor adjusts based on performance
- Failed cards reset to 1-day interval

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see below for details.

## 🙏 Acknowledgments

- Color scheme inspired by [Catppuccin](https://github.com/catppuccin/catppuccin)
- SM-2 algorithm by Piotr Wozniak
- JavaFX community for excellent documentation

## 📧 Contact

Your Name - [@yourhandle](https://twitter.com/yourhandle)

Project Link: [https://github.com/YOUR_USERNAME/smart-study-planner](https://github.com/YOUR_USERNAME/smart-study-planner)

---

**Made with ❤️ for students everywhere**