# 📊 Expense Tracker

A full-stack **Expense Tracking Application** built with **Spring Boot (Java)** on the backend and **HTML, CSS, JavaScript** on the frontend. It allows users to register/login, add and manage expenses, and view their financial data in a clean interface.

I’ve tried to create a **smooth user experience**, using modern libraries like **Toastify** for elegant notifications and **Flatpickr** for a better date-picking experience.

---

## 🚀 Features
- User authentication (register & login).
- Add, update, and delete expenses.
- Categorize expenses by type.
- Date picker with **Flatpickr** for quick input.
- Toast notifications with **Toastify** for better feedback.
- Centralized exception handling in the backend.
- RESTful API with layered architecture (Controller → Service → Repository → Database).
- Clean and responsive UI with improved UX.

---

## 🛠️ Tech Stack

**Backend:**
- Java 17+
- Spring Boot
- Spring Data JPA (Hibernate)
- Maven

**Frontend:**
- HTML, CSS, JavaScript
- Toastify (notifications)
- Flatpickr (calendar/date picker)

**Database:**
- MySQL (configurable in `application.properties`)

---

## 📂 Project Structure
```
expense-tracker/
│── frontend/              # Static frontend files
│   ├── index.html         # Login/Register page
│   ├── home.html          # Dashboard page
│   ├── script.js          # Frontend logic
│   └── style.css          # Styling
│
│── src/main/java/com/example/expensetracker/
│   ├── controller/        # REST Controllers
│   ├── service/           # Business logic layer
│   ├── repository/        # JPA repositories
│   ├── models/            # Entities (User, Expense)
│   └── ExpenseTrackerApplication.java # Main Spring Boot entry point
│
│── src/main/resources/
│   └── application.properties  # DB and server configuration
│
│── pom.xml                # Maven dependencies
```

---

## ⚙️ Setup & Installation

1. **Clone the repository**
```bash
git clone https://github.com/your-username/expense-tracker.git
cd expense-tracker
```

2. **Configure the database**  
Update DB settings in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/expense_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

3. **Run the backend**
```bash
mvn spring-boot:run
```
Backend will start at: `http://localhost:8080`

4. **Open the frontend**  
Open `frontend/index.html` in your browser.

---

## 🔗 API Endpoints

### User APIs
- `POST /api/users/register` → Register a new user  
- `POST /api/users/login` → Login user  

### Expense APIs
- `POST /api/expenses` → Add new expense  
- `GET /api/expenses` → Get all expenses  
- `PUT /api/expenses/{id}` → Update expense  
- `DELETE /api/expenses/{id}` → Delete expense  

---

## 🎨 User Experience Enhancements
- ✅ **Toastify** → Smooth success/error notifications for actions like adding or deleting expenses.  
- ✅ **Flatpickr** → Clean and intuitive date selection for expense entries.  
- ✅ **Minimal & responsive design** for better usability on different devices.  

---

## 🤝 Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you’d like to change.

---

## 📜 License
This project is licensed under the MIT License.
