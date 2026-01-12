# 📚 Smart Study Planner

Aplikasi desktop komprehensif berbasis GUI JavaFX untuk mengelola tugas akademik, melacak produktivitas, dan membuat pengalaman belajar lebih menyenangkan dengan XP dan pencapaian.

![Java](https://img.shields.io/badge/Java-17+-orange.svg)
![JavaFX](https://img.shields.io/badge/JavaFX-17.0.2-blue.svg)
![SQLite](https://img.shields.io/badge/SQLite-3.45-green.svg)
<!-- ![Stars](https://img.shields.io/github/stars/nayyaraazra/PBO-Project-SmartStudentPlanner)
![Issues](https://img.shields.io/github/issues/nayyaraazra/PBO-Project-SmartStudentPlanner) -->

## ✨ Fitur

### 🎯 Smart Task Management (Manajemen Tugas)
- **Tiga Jenis Tugas**: Assignments (Tugas), Exams (Ujian), and Projects(Proyek)
- **Sistem Prioritas Cerdas**: Secara otomatis menghitung urgensi tugas berdasarkan tenggat waktu, prioritas, dan upaya.
- **Pelacakan Dependensi**: Hubungkan tugas-tugas yang harus diselesaikan secara berurutan.
- **Monitoring Status**: Lacak progres dari TODO hingga selesai
<img width="1919" height="1124" alt="image" src="https://github.com/user-attachments/assets/a8f58426-cb80-4320-a474-07667878f1db" />


### 📊 Dashboard
- Tampilan statistik real-time
- Pelacakan streak untuk produktivitas harian
- Sistem XP dan leveling
- Tampilan tugas paling mendesak dan skor urgensi-nya
- Grafik produktivitas mingguan
<img width="1919" height="1129" alt="image" src="https://github.com/user-attachments/assets/575930fa-5a29-4d1e-a077-3e236fea2850" />


### 🍅 Pomodoro Timer
- Durasi fokus(15-60 menit) dan istirahat(3-15 menit) yang dapat disesuaikan
- Pelacakan sesi: Hitung sesi Pomodoro yang telah selesai
- Indikator progres melingkar
- Jeda Otomatis : Beralih antara fokus dan jeda secara otomatis.
- Istirahat panjang setelah 4 sesi
<img width="1919" height="1127" alt="image" src="https://github.com/user-attachments/assets/865e70d8-59c2-4688-b7d5-6846071689c5" />


### 📅 Calendar View
- Visualisasi tugas per bulannya
- Tenggat tugas overview (di bawah kalender)
- Tombol pemilihan yang interaktif
<img width="1919" height="1129" alt="image" src="https://github.com/user-attachments/assets/a44caadc-292c-4847-8d9a-f6666a376056" />

<!--
### 📈 Analytics
- Tingkat penyelesaian tugas
- Grafik distribusi prioritas
- Wawasan pola belajar
- Tren produktivitas
<img width="1919" height="1124" alt="image" src="https://github.com/user-attachments/assets/e1786f81-d7fc-4c3b-b65e-9f0cd23319d0" />-->


### 🎴 Flashcard System 
- Mendukung pemakaian multiple deck
- Pembelajaran dengan mode interaktif
- Tracking penguasaan pengguna: Indikator kemajuan untuk setiap kartu dan kartu akan muncul saat waktunya untuk ditinjau.
- 4-level rating system (Again, Hard, Good, Easy): dapat dipilih oleh pengguna setiap jawaban ditampilkan dan menilai secara sederhana level penguasaan materi 
<img width="1591" height="1057" alt="image" src="https://github.com/user-attachments/assets/3895d742-c98e-41c4-8838-595504c05b96" />

## 🎯 Sistem Gamifikasi
- **XP & Naik Level** : Dapatkan poin pengalaman dengan menyelesaikan tugas.
- **Perhitungan XP Dinamis** :
  Rumus perhitungan detail:
  ```
  Total XP = Base (50) 
         + Priority Bonus (0-75)
         + Type Bonus (Assignment:50, Project:75, Exam:100)
         + Time Bonus (50 if on time)
         + Difficulty Bonus (Estimated Hours × 5)
  ```
    XP Dasar (50)
    Bonus prioritas (0-75 XP)
    Bonus berdasarkan jenis tugas (Ujian: 100, Proyek: 75, Tugas: 50)
    Bonus waktu (50 XP untuk penyelesaian tepat waktu)
    Bonus kesulitan (5 XP per perkiraan jam)
- **Sistem Pencapaian** : Buka pencapaian untuk setiap tonggak penting<br>
  Langkah Pertama: Selesaikan tugas pertama Anda<br>
  🔥 Berdedikasi: Pertahankan rentetan 7 hari<br>
  💪 Pelari Maraton: Selesaikan sesi belajar selama 10 jam<br>
  ⏰ Bangun Pagi: Belajar sebelum jam 7 pagi<br>
  🌙 Burung Hantu Malam: Belajar hingga lewat tengah malam<br>
  🎯 Perfeksionis: Selesaikan 10 tugas tepat waktu<br>
  👑 Master: Capai level 10<br>
- **Pelacakan Rentetan Belajar** : Penghitung Rentetan Belajar Harian
  
## 🏗️ Architecture (Pola Desain yang Digunakan)

### 1. Design Patterns
- **MVC (Model-View-Controller)**: Model(Task, UserProfile, Achievement, Flashcard, dll), View(DashboardView, TasksView, PomodoroView, dll), Controller(MainController, dll).
- **Observer Pattern**: Pembaruan UI secara real-time 
- **Factory Pattern**: Pembuatan tugas
- **Strategy Pattern**: Kalkulasi urgensi yang berbeda per jenis tugas, penerapan polymorphism melalui inheritance
- **Singleton Pattern**: Memastikan koneksi basis data tunggal (DatabaseManager.java)
- **Data Access Object (DAO)**: UserDAO(Operasi pada profil pengguna), TaskDAO(Operasi CRUD tugas), FlashcardDAO(Operasi kartu flash dan deck)

### 2. Prinsip inti PBO
- **Encapsulation**: Private fields with public getters/setters
- **Inheritance**: Task → Assignment/Exam/Project
- **Polymorphism**: Abstract calculateUrgencyScore() method
- **Abstraction**: Abstract Task class with concrete implementations

## 💾 Penyimpanan Permanen

- **Basis Data SQLite** : Semua data tersimpan secara otomatis.
- **Ketahanan Data Lintas Sesi** : Data tetap ada meskipun aplikasi dimulai ulang.
- **Pencadangan Otomatis** : Fitur pencadangan basis data yang mudah.
- **Integritas Data** : Kepatuhan ACID memastikan tidak ada kehilangan data.
### Skema
```
- users (id, username, xp, level, streak, last_activity_date)
- tasks (id, user_id, title, description, task_type, due_date, status, priority, estimated_hours, actual_hours)
- achievements (id, user_id, name, description, xp_reward, unlocked, unlocked_date)
- productivity_history (id, user_id, date, minutes)
- pomodoro_sessions (id, user_id, start_time, end_time, duration, completed)
- decks (id, user_id, name, description, category, created_date)
- flashcards (id, deck_id, question, answer, ease_factor, repetitions, interval, next_review)
```
## 🎨 Antarmuka Pengguna

- **Tema Gelap Modern** : Skema warna Catppuccin Mocha
- **Desain Responsif** : Beradaptasi dengan berbagai ukuran jendela.
- **Navigasi Intuitif** : Sidebar dengan bagian yang jelas
- **Pembaruan Waktu Nyata** : Antarmuka pengguna diperbarui secara otomatis saat terjadi perubahan data.
- **Animasi Halus** : Pengalaman pengguna yang lebih baik.

### Project Structure
```
smart-study-planner/
├── src/
│   ├── StudyPlannerApp.java          # Main application entry point
│   ├── controller/
│   │   ├── MainController.java       # Business logic controller
│   │   └── SmartScheduler.java       # Task scheduling algorithm
│   ├── model/
│   │   ├── Task.java                 # Abstract task class
│   │   ├── Assignment.java           # Assignment implementation
│   │   ├── Exam.java                 # Exam implementation
│   │   ├── Project.java              # Project implementation
│   │   ├── TaskManager.java          # Task collection manager
│   │   ├── TaskObserver.java         # Observer interface
│   │   ├── TaskStatus.java           # Task status enum
│   │   ├── TaskPriority.java         # Priority enum
│   │   ├── UserProfile.java          # User data model
│   │   ├── ProfileObserver.java      # Profile observer interface
│   │   ├── Achievement.java          # Achievement model
│   │   ├── Flashcard.java            # Flashcard model
│   │   ├── Deck.java                 # Flashcard deck
│   │   ├── DeckManager.java          # Deck collection manager
│   │   ├── PomodoroSession.java      # Pomodoro session model
│   │   └── StudyStatistics.java      # Statistics calculator
│   ├── database/
│   │   ├── DatabaseManager.java      # SQLite connection manager
│   │   ├── UserDAO.java              # User database operations
│   │   ├── TaskDAO.java              # Task database operations
│   │   └── FlashcardDAO.java         # Flashcard database operations
│   ├── ui/
│   │   ├── DashboardView.java        # Main dashboard
│   │   ├── TasksView.java            # Task management view
│   │   ├── CalendarView.java         # Calendar view
│   │   ├── PomodoroView.java         # Pomodoro timer
│   │   ├── FlashcardsView.java       # Flashcard study mode
│   │   └── AnalyticsView.java        # Statistics and charts
└── lib/
│   └── sqlite-jdbc-3.45.0.0.jar      # SQLite JDBC driver
│   └── javafx lib
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
1. Klik tab "Flashcards"
2. Buat deck baru atau gunakan sample deck
3. Tambahkan kartu ke deck Anda
4. Klik "Study Now" untuk memulai
5. Beri rating setiap kartu: Again, Hard, Good, atau Easy 

## 🎨 Customization

### Changing Theme Colors
Edit `src/resources/styles.css` to customize the color scheme.

Current theme: **Catppuccin Mocha**

### (Jika ingin menambah tipe tugas baru)
1. Buat class baru dan extend `Task`
2. Implement `calculateUrgencyScore()` method
3. Tambahkan logika di `TasksView`

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

<!--
## 📧 Contact

Nayyara - [@yourhandle](https://twitter.com/yourhandle)

Project Link: [https://github.com/HiSpyWare/smart-study-planner](https://github.com/HiSpyWare/smart-study-planner)-->

---
