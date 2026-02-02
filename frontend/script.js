const userId = localStorage.getItem("userId");
if (!userId) {
	alert("Please login first.");
	localStorage.setItem("openLoginPopup", "true");
	window.location.href = "home.html";
}

const API_URL = `http://localhost:8081/api/expenses?userId=${userId}`;

// Add Expense
let pendingExpense = null;

// Wait until DOM is loaded
document.addEventListener("DOMContentLoaded", () => {
	// Intercept form submit
	const form = document.getElementById("expense-form");
	if (form) {
		form.addEventListener("submit", (e) => {
			e.preventDefault();

			pendingExpense = {
				title: document.getElementById("title").value,
				amount: parseFloat(document.getElementById("amount").value),
				category: document.getElementById("category").value,
				date: document.getElementById("date").value,
			};

			// fill modal preview
			document.getElementById("preview-title").textContent = pendingExpense.title;
			document.getElementById("preview-amount").textContent = pendingExpense.amount;
			document.getElementById("preview-category").textContent = pendingExpense.category;
			document.getElementById("preview-date").textContent = pendingExpense.date;

			document.getElementById("preview-modal").style.display = "flex";
		});
	}

	// Cancel button
	document.getElementById("cancel-btn").addEventListener("click", () => {
		pendingExpense = null;
		document.getElementById("preview-modal").style.display = "none";
		document.getElementById("title").focus();
	});

	// Confirm button (do actual POST here)
	document.getElementById("confirm-btn").addEventListener("click", async () => {
		if (!pendingExpense) return;

		await fetch(`http://localhost:8081/api/expenses?userId=${userId}`, {
			method: "POST",
			headers: {
				"Content-Type": "application/json"
			},
			body: JSON.stringify(pendingExpense),
		});

		await loadExpenses();
		document.getElementById("expense-form").reset();
		pendingExpense = null;

		document.getElementById("preview-modal").style.display = "none";
		showNotification("Expense added successfully!");
	});

	// Cancel delete
	document.getElementById("delete-cancel-btn").addEventListener("click", () => {
		expenseToDelete = null;
		document.getElementById("delete-modal").style.display = "none";
	});

	// Confirm delete
	document.getElementById("delete-confirm-btn").addEventListener("click", async () => {
		if (!expenseToDelete) return;

		await fetch(`http://localhost:8081/api/expenses/${expenseToDelete}?userId=${userId}`, {
			method: "DELETE",
		});

		await loadExpenses();
		expenseToDelete = null;
		document.getElementById("delete-modal").style.display = "none";
		showNotification("Expense deleted successfully!");
	});

	// Cancel update
	document.getElementById("update-cancel-btn").addEventListener("click", () => {
		document.getElementById("update-modal").style.display = "none";
	});

	// Handle update
	document.getElementById("update-form").addEventListener("submit", async (e) => {
		e.preventDefault();

		const id = document.getElementById("update-id").value;
		const expense = {
			title: document.getElementById("update-title").value,
			amount: parseFloat(document.getElementById("update-amount").value),
			category: document.getElementById("update-category").value,
			date: document.getElementById("update-date").value,
		};

		await fetch(`http://localhost:8081/api/expenses/${id}?userId=${userId}`, {
			method: "PUT",
			headers: {
				"Content-Type": "application/json"
			},
			body: JSON.stringify(expense),
		});

		document.getElementById("update-modal").style.display = "none";
		await loadExpenses();
		showNotification("Expense updated successfully!");
	});

	// Initial load
	loadExpenses();
});

// Delete Expense
let expenseToDelete = null;

function confirmDelete(id) {
	expenseToDelete = id;
	document.getElementById("delete-text").textContent =
		`Are you sure you want to delete record ?`;
	document.getElementById("delete-modal").style.display = "flex";
}

// Show update modal
function showUpdateForm(id, title, amount, category, date) {
	document.getElementById("update-id").value = id;
	document.getElementById("update-title").value = title;
	document.getElementById("update-amount").value = amount;
	document.getElementById("update-category").value = category;

	updateDatePicker.setDate(date, true);

	document.getElementById("update-modal").style.display = "flex";
}

// --- SEARCH + FILTER ---

// --- SEARCH + FILTER (live search) ---
document.addEventListener("DOMContentLoaded", () => {
	const searchInput = document.getElementById("searchInput");
	const filterSelect = document.getElementById("filterSelect");

	if (searchInput) {
		searchInput.addEventListener("input", () => {
			applySearchFilter();
		});
	}

	if (filterSelect) {
		filterSelect.addEventListener("change", () => {
			applySearchFilter();
		});
	}
});

async function applySearchFilter() {
	const keyword = document.getElementById("searchInput").value.trim();
	const category = document.getElementById("filterSelect").value;

	let url = "http://localhost:8081/api/expenses";

	if (keyword && category) {
		url += `/search-filter?userId=${userId}&category=${encodeURIComponent(category)}&keyword=${encodeURIComponent(keyword)}`;
	} else if (keyword) {
		url += `/search?userId=${userId}&keyword=${encodeURIComponent(keyword)}`;
	} else if (category) {
		url += `/filter?userId=${userId}&category=${encodeURIComponent(category)}`;
	} else {
		url += `?userId=${userId}`;
	}

	try {
		const res = await fetch(url);
		const expenses = await res.json();
		renderExpenseList(expenses);
		updateSummaryAndCharts(expenses);
	} catch (err) {
		console.error("Error fetching search/filter results:", err);
	}
	const res = await fetch(url);
	currentExpenses = await res.json();
	renderExpenseList(currentExpenses);
	updateSummaryAndCharts(currentExpenses);
}

// Render list
function renderExpenseList(expenses) {
	const list = document.getElementById("expense-list");
	list.innerHTML = "";

	if (expenses.length === 0) {
		const li = document.createElement("li");
		li.textContent = "No records match your search.";
		li.style.color = "black"; // make it visible on dark bg
		li.style.fontSize = "16px";
		li.style.padding = "10px";
		list.appendChild(li);
		return;
	}

	expenses.forEach((exp) => {
		const li = document.createElement("li");
		li.innerHTML = `
      <div class="expense-item-wrapper">
        <div class="expense-item">
          <span>${exp.title}</span>
          <span>₹${exp.amount}</span>
          <span>${exp.category}</span>
          <span>${exp.date}</span>
        </div>
        <div class="expense-actions">
          <button id="edit" onclick="showUpdateForm(${exp.id}, '${exp.title}', ${exp.amount}, '${exp.category}', '${exp.date}')">✏️</button>
          <button onclick="confirmDelete(${exp.id})">Delete</button>
        </div>
      </div>`;
		list.appendChild(li);
	});
}

// Summary + charts
function updateSummaryAndCharts(expenses) {
	let totalIncome = 0;
	let totalExpense = 0;
	const trendData = {};

	expenses.forEach((exp) => {
		if (exp.category === "Income") {
			totalIncome += exp.amount;
		} else {
			totalExpense += exp.amount;
		}

		const date = exp.date;
		if (!trendData[date]) {
			trendData[date] = {
				income: 0,
				expense: 0
			};
		}
		if (exp.category === "Income") {
			trendData[date].income += exp.amount;
		} else {
			trendData[date].expense += exp.amount;
		}
	});

	const balance = totalIncome - totalExpense;

	// Update DOM
	document.getElementById("total-income").textContent = `₹${totalIncome}`;
	document.getElementById("total-expense").textContent = `₹${totalExpense}`;

	const balanceElement = document.getElementById("balance");
	balanceElement.textContent = `₹${balance}`;

	// Color logic
	if (balance < 0) {
		balanceElement.style.color = "red";
	} else if (balance > 0) {
		balanceElement.style.color = "green";
	} else {
		balanceElement.style.color = "black";
	}

	renderCharts(totalIncome, totalExpense, trendData);
}


// Initial load
async function loadExpenses() {
	const res = await fetch(API_URL);
	const expenses = await res.json();
	renderExpenseList(expenses);
	updateSummaryAndCharts(expenses);
}

// Render Charts with Chart.js
let pieChart;

function renderCharts(totalIncome, totalExpense) {
	const ctx1 = document.getElementById("incomeExpenseChart");
	if (!ctx1) return;

	if (pieChart) pieChart.destroy();

	pieChart = new Chart(ctx1, {
		type: "pie",
		data: {
			labels: ["Income", "Expense"],
			datasets: [{
				data: [totalIncome, totalExpense],
				backgroundColor: ["#4CAF50", "#F44336"]
			}]
		}
	});
}

// Graph Popup
const graphPopup = document.getElementById("graphPopup");
const closeGraph = document.querySelector(".close-graph");
const viewGraphTrigger = document.querySelector(".view-graph-trigger");

viewGraphTrigger.addEventListener("click", () => {
	graphPopup.style.display = "flex";
	setTimeout(() => graphPopup.classList.add("show"), 10);
});

closeGraph.addEventListener("click", () => {
	graphPopup.classList.remove("show");
	setTimeout(() => (graphPopup.style.display = "none"), 400);
});

window.addEventListener("click", (e) => {
	if (e.target === graphPopup) {
		graphPopup.classList.remove("show");
		setTimeout(() => (graphPopup.style.display = "none"), 400);
	}
});

// flatpickr config
flatpickr("#date", {
	maxDate: "today",
	dateFormat: "Y-m-d",
	altInput: true,
	altFormat: "F j, Y",
	allowInput: true,
	onChange: function(selectedDates, dateStr, instance) {
		instance.input.classList.add("valid");
	},
});

const updateDatePicker = flatpickr("#update-date", {
	dateFormat: "Y-m-d",
	altInput: true,
	altFormat: "F j, Y",
	allowInput: true,
	onReady: function(selectedDates, dateStr, instance) {
		instance.altInput.style.color = "#fff";
		instance.altInput.style.background = "rgba(255,255,255,0.15)";
	},
});

// Toastify notifications
function showNotification(message, type = "success") {
	Toastify({
		text: message + '<div class="toast-progress"></div>',
		duration: 3000,
		close: true,
		gravity: "top",
		position: "right",
		escapeMarkup: false,
		backgroundColor: type === "success" ? "green" : "red",
		stopOnFocus: true,
		offset: {
			x: 10,
			y: 60
		},
	}).showToast();
}

function exportToCSV(expenses) {
	if (!expenses || expenses.length === 0) {
		showNotification("No records to export.", "error");
		return;
	}

	const headers = ["Title", "Amount", "Category", "Date"];
	const rows = expenses.map(e => [e.title, e.amount, e.category, e.date]);

	let csvContent = "data:text/csv;charset=utf-8," +
		[headers, ...rows].map(r => r.join(",")).join("\n");

	const link = document.createElement("a");
	link.setAttribute("href", encodeURI(csvContent));
	link.setAttribute("download", "expenses.csv");
	document.body.appendChild(link);
	link.click();
	document.body.removeChild(link);
}
async function exportToPDF(expenses) {
	if (!expenses || expenses.length === 0) {
		showNotification("No records to export.", "error");
		return;
	}

	const {
		jsPDF
	} = window.jspdf;
	const doc = new jsPDF();

	doc.setFontSize(16);
	doc.text("Expense Records", 14, 20);

	const headers = ["Title", "Amount", "Category", "Date"];
	let rows = expenses.map(e => [e.title, e.amount.toString(), e.category, e.date]);

	// simple table
	let y = 30;
	doc.setFontSize(12);
	doc.text(headers.join(" | "), 14, y);
	y += 10;

	rows.forEach(r => {
		doc.text(r.join(" | "), 14, y);
		y += 10;
	});

	doc.save("expenses.pdf");
}

let currentExpenses = [];

async function loadExpenses() {
	const res = await fetch(API_URL);
	currentExpenses = await res.json();
	renderExpenseList(currentExpenses);
	updateSummaryAndCharts(currentExpenses);
}


document.getElementById("export-csv").addEventListener("click", () => {
	exportToCSV(currentExpenses);
});

document.getElementById("export-pdf").addEventListener("click", () => {
	exportToPDF(currentExpenses);
});


document.addEventListener("DOMContentLoaded", () => {
	const exportSection = document.querySelector(".export-section");
	if (!exportSection) return;

	const downloadBtn = exportSection.querySelector(".download-btn");
	const dropdown = exportSection.querySelector(".export-dropdown");

	// Toggle dropdown
	downloadBtn.addEventListener("click", (e) => {
		e.stopPropagation();
		dropdown.classList.toggle("show");
	});

	// Close dropdown when clicking outside
	window.addEventListener("click", (e) => {
		if (!e.target.closest(".export-section")) {
			dropdown.classList.remove("show");
		}
	});

	// CSV & PDF buttons
	document.getElementById("export-csv").addEventListener("click", (e) => {
		e.preventDefault();
		exportToCSV(currentExpenses);
		dropdown.classList.remove("show");
	});

	document.getElementById("export-pdf").addEventListener("click", (e) => {
		exportToPDF(currentExpenses);
	});
});


document.addEventListener("DOMContentLoaded", async () => {
  const userId = localStorage.getItem("userId");

  if (userId) {
    try {
      const res = await fetch(`http://localhost:8081/api/users/${userId}/status`);
      if (res.status === 403) {
        // account is deactivated
        alert("Your account has been deactivated. Please contact admin.");
        localStorage.removeItem("userId");
        localStorage.removeItem("username");
        window.location.href = "home.html"; // redirect to login page
      }
    } catch (err) {
      console.error("Error checking user status:", err);
    }
  }
});
