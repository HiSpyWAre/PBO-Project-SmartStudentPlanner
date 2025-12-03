# 📚 Smart Study Planner

Aplikasi produktivitas berbasis GUI JavaFX yang komprehensif dirancang untuk membantu mahasiswa mengelola beban kerja akademik mereka secara efektif.

![Java](https://img.shields.io/badge/Java-17+-orange.svg)
![JavaFX](https://img.shields.io/badge/JavaFX-17.0.2-blue.svg)

## ✨ Features

### 🎯 Smart Task Management
- **Tiga Jenis Tugas**: Assignments (Tugas), Exams (Ujian), and Projects(Proyek)
- **Sistem Prioritas Cerdas**: Kalkulasi urgensi otomatis
- **Pelacakan Dependensi**: Hubungkan tugas yang terkait
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
- Durasi fokus dan istirahat yang dapat disesuaikan
- Pelacakan sesi
- Indikator progres melingkar
- Penjadwalan istirahat otomatis
- Istirahat panjang setelah 4 sesi
<img width="1919" height="1127" alt="image" src="https://github.com/user-attachments/assets/865e70d8-59c2-4688-b7d5-6846071689c5" />


### 📅 Calendar View
- Visualisasi tugas per bulannya
- Tenggat tugas overview (di bawah kalender)
- Tombol pemilihan yang interaktif
<img width="1919" height="1129" alt="image" src="https://github.com/user-attachments/assets/a44caadc-292c-4847-8d9a-f6666a376056" />


### 📈 Analytics
- Tingkat penyelesaian tugas
- Grafik distribusi prioritas
- Wawasan pola belajar
- Tren produktivitas
<img width="1919" height="1124" alt="image" src="https://github.com/user-attachments/assets/e1786f81-d7fc-4c3b-b65e-9f0cd23319d0" />


### 🎴 Flashcard System (Coming Soon)
<img width="1591" height="1057" alt="image" src="https://github.com/user-attachments/assets/3895d742-c98e-41c4-8838-595504c05b96" />
- Mendukung pemakaian multiple deck
- Pembelajaran dengan mode interaktif
- Tracking penguasaan pengguna
- 4-level rating system (Again, Hard, Good, Easy): dapat dipilih oleh pengguna setiap jawaban ditampilkan dan menilai secara sederhana level penguasaan materi 


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

## 📧 Contact

Nayyara - [@yourhandle](https://twitter.com/yourhandle)

Project Link: [https://github.com/HiSpyWare/smart-study-planner](https://github.com/HiSpyWare/smart-study-planner)

---
