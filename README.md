# 📚 Smart Study Planner

Aplikasi produktivitas JavaFX yang komprehensif dirancang untuk membantu mahasiswa mengelola beban kerja akademik mereka secara efektif.

(Screenshoot tmpilan)

![Java](https://img.shields.io/badge/Java-17+-orange.svg)
![JavaFX](https://img.shields.io/badge/JavaFX-17.0.2-blue.svg)
![License](https://img.shields.io/badge/license-MIT-green.svg)

## ✨ Features

### 🎯 Smart Task Management
- **Tiga Jenis Tugas**: Assignments (Tugas), Exams (Ujian), and Projects(Proyek)
- **Sistem Prioritas Cerdas**: Kalkulasi urgensi otomatis
- **Pelacakan Dependensi**: Hubungkan tugas yang terkait
- **Monitoring Status**: Lacak progres dari TODO hingga selesai

### 📊 Dashboard
- Tampilan statistik real-time
- Pelacakan streak untuk produktivitas harian
- Sistem XP dan leveling
- Tampilan tugas paling mendesak
- Grafik produktivitas mingguan

### 🍅 Pomodoro Timer
- Durasi fokus dan istirahat yang dapat disesuaikan
- Pelacakan sesi
- Indikator progres melingkar
- Penjadwalan istirahat otomatis
- Istirahat panjang setelah 4 sesi

### 📅 Calendar View
- Monthly task visualization
- Due date overview
- Interactive date selection

### 📈 Analytics
- Tingkat penyelesaian tugas
- Grafik distribusi prioritas
- Wawasan pola belajar
- Tren produktivitas

### 🎴 Flashcard System (Coming Soon)
- **SM-2 Spaced Repetition Algorithm**
- Dukungan banyak deck
- Mode belajar interaktif
- Pelacakan penguasaan materi
- Sistem rating 4 level (Again, Hard, Good, Easy)

### 🏆 Gamification
- Sistem perolehan XP
- Progres level
- Pencapaian yang dapat di-unlock
- Reward streak
  
## 🏗️ Architecture

### Design Patterns
- **MVC (Model-View-Controller)**: Pemisahan concern yang jelas
- **Observer Pattern**: Update UI real-time
- **Factory Pattern**: Pembuatan tugas
- **Strategy Pattern**: Kalkulasi urgensi yang berbeda per jenis tugas

### Project Structure
```
SmartStudyPlanner/
├── src/
│   ├── model/              # Model data and logika
│   │   ├── Task.java
│   │   ├── TaskManager.java
│   │   ├── UserProfile.java
│   │   ├── Flashcard.java
│   │   └── ...
│   ├── ui/                 # Tampilan User interface 
│   │   ├── DashboardView.java
│   │   ├── TasksView.java
│   │   ├── PomodoroView.java
│   │   └── ...
│   ├── controller/         # Controllers aplikasi
│   │   ├── MainController.java
│   │   └── SmartScheduler.java
│   ├── resources/          # CSS dan assets
│   │   └── styles.css
│   └── StudyPlannerApp.java
└── README.md
```

## 🚀 Getting Started

### Persyaratan stack
- Java JDK 17 atau lebih tinggi
- JavaFX SDK 17 atau lebih tinggi
- Git (untuk cloning)

### Installation

1. **Clone repository**
```bash
git clone https://github.com/YOUR_USERNAME/smart-study-planner.git
cd smart-study-planner
```

2. **Download JavaFX SDK**
   - Download melalui: https://gluonhq.com/products/javafx/
   - Extract ke lokasi di komputer Anda

3. **Compile project**
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

4. **Run/jalankan project**
```bash
java --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.fxml \
     -cp out \
     StudyPlannerApp
```

### Menggunakan Maven (Direkomendasikan)

1. **Install Maven** 
2. **Run applikasi**
```bash
mvn clean javafx:run
```

## 💡 Panduan Penggunaan

### Membuat Tugas
1. Navigasi ke tab "Tasks"
2. Klik "+ Add Task"
3. Isi detail (judul, deskripsi, tanggal jatuh tempo, prioritas)
4. Pilih jenis tugas (Assignment/Exam/Project)
5. Klik "Add"

### Memulai Sesi Pomodoro
1. Pergi ke tab "Pomodoro"
2. Sesuaikan durasi fokus/istirahat jika diperlukan
3. Klik "Start"
4. Fokus pada pekerjaan Anda hingga timer selesai
5. Beri rating kualitas sesi Anda

### Menggunakan Flashcard (Cooming Soon)
(rencana)
Klik tab "Flashcards"
Buat deck baru atau gunakan sample deck
Tambahkan kartu ke deck Anda
Klik "Study Now" untuk memulai
Beri rating setiap kartu: Again, Hard, Good, atau Easy

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
Setiap jenis tugas menghitung urgensi secara berbeda:
- **Assignments**: Tekanan waktu + prioritas + usaha
- **Exams**: Urgensi dasar lebih tinggi + kesiapan
- **Projects**: Persentase penyelesaian + waktu + prioritas

### Spaced Repetition (SM-2)
- Interval: 1 hari → 6 hari → dikalikan dengan ease factor
- Ease factor menyesuaikan berdasarkan performa
- Kartu yang gagal direset ke interval 1 hari


## 🙏 Acknowledgments

- Color scheme inspired by [Catppuccin](https://github.com/catppuccin/catppuccin)
- SM-2 algorithm by Piotr Wozniak
- JavaFX community for excellent documentation

## 📧 Contact

Nayyara - [@yourhandle](https://twitter.com/yourhandle)

Project Link: [https://github.com/HiSpyWare/smart-study-planner](https://github.com/HiSpyWare/smart-study-planner)

---
