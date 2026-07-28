// Digital Payment Fraud Detection System - Client JavaScript

document.addEventListener('DOMContentLoaded', () => {
    // 1. Authentication & Profile setup
    const userJson = localStorage.getItem('fraud_user');
    if (!userJson && !window.location.pathname.endsWith('login.html')) {
        window.location.href = 'login.html';
        return;
    }

    if (userJson) {
        try {
            const user = JSON.parse(userJson);
            const userFullNameEl = document.getElementById('userFullName');
            if (userFullNameEl && user.fullName) {
                userFullNameEl.textContent = user.fullName;
            }
        } catch (e) {
            console.error('Error parsing stored user:', e);
        }
    }

    // Logout Handler
    const btnLogout = document.getElementById('btnLogout');
    if (btnLogout) {
        btnLogout.addEventListener('click', () => {
            localStorage.removeItem('fraud_user');
            showToast('Logged out successfully', 'success');
            setTimeout(() => {
                window.location.href = 'login.html';
            }, 600);
        });
    }

    // 2. Initialize Dashboard Components
    if (document.getElementById('transactionTableBody')) {
        initDashboard();
    }
});

// Mock Initial Data (for browser fallback if backend server isn't running)
let localTransactions = [
    { id: 1, senderName: 'John Doe', receiverName: 'Alice Smith', amount: 12500.00, transactionDate: '2026-07-20', status: 'Safe' },
    { id: 2, senderName: 'Robert Vance', receiverName: 'TechCorp International', amount: 85000.00, transactionDate: '2026-07-21', status: 'Fraud' },
    { id: 3, senderName: 'Sarah Connor', receiverName: 'Cyberdyne Systems', amount: 45000.50, transactionDate: '2026-07-22', status: 'Safe' },
    { id: 4, senderName: 'Michael Scott', receiverName: 'Dunder Mifflin', amount: 62000.00, transactionDate: '2026-07-23', status: 'Fraud' }
];

function initDashboard() {
    // Set default date to Today
    const dateInput = document.getElementById('transactionDate');
    if (dateInput) {
        dateInput.value = new Date().toISOString().split('T')[0];
    }

    // Live Amount Preview for Fraud Indicator
    const amountInput = document.getElementById('amount');
    if (amountInput) {
        amountInput.addEventListener('input', updateFraudPreview);
    }

    // Form Submit Event
    const form = document.getElementById('transactionForm');
    if (form) {
        form.addEventListener('submit', handleAddTransaction);
    }

    // Search Input Event
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('input', debounce(handleSearch, 300));
    }

    // Status Filter Select Event
    const statusFilter = document.getElementById('statusFilter');
    if (statusFilter) {
        statusFilter.addEventListener('change', handleFilter);
    }

    // Refresh Button Event
    const btnRefresh = document.getElementById('btnRefresh');
    if (btnRefresh) {
        btnRefresh.addEventListener('click', () => {
            loadTransactions();
            loadStats();
            showToast('Refreshed transaction data', 'success');
        });
    }

    // Initial Load
    loadTransactions();
    loadStats();
}

// Live Fraud Indicator logic: Amount > 50000 -> Fraud
function updateFraudPreview() {
    const amountVal = parseFloat(document.getElementById('amount').value) || 0;
    const previewBadge = document.getElementById('previewBadge');

    if (amountVal > 50000) {
        previewBadge.className = 'badge badge-fraud';
        previewBadge.innerHTML = '<i class="fa-solid fa-triangle-exclamation"></i> FLAGGED AS FRAUD (> $50,000)';
    } else {
        previewBadge.className = 'badge badge-safe';
        previewBadge.innerHTML = '<i class="fa-solid fa-shield"></i> SAFE TRANSACTION (&le; $50,000)';
    }
}

// 3. API Calls & Data Fetching

// Load All Transactions
async function loadTransactions() {
    try {
        const response = await fetch('/api/transactions');
        if (!response.ok) throw new Error('API server unavailable');
        const transactions = await response.json();
        renderTable(transactions);
    } catch (err) {
        console.warn('Backend server offline or loading mock data:', err);
        renderTable(localTransactions);
    }
}

// Load Dashboard KPI Stats
async function loadStats() {
    try {
        const response = await fetch('/api/stats');
        if (!response.ok) throw new Error('API server unavailable');
        const stats = await response.json();
        updateKpiCards(stats.total, stats.safe, stats.fraud);
    } catch (err) {
        // Fallback computation
        const total = localTransactions.length;
        const fraud = localTransactions.filter(t => t.status === 'Fraud').length;
        const safe = localTransactions.filter(t => t.status === 'Safe').length;
        updateKpiCards(total, safe, fraud);
    }
}

function updateKpiCards(total, safe, fraud) {
    document.getElementById('statTotal').textContent = total;
    document.getElementById('statSafe').textContent = safe;
    document.getElementById('statFraud').textContent = fraud;
}

// 4. Form Submission Handler
async function handleAddTransaction(e) {
    e.preventDefault();

    const senderName = document.getElementById('senderName').value.trim();
    const receiverName = document.getElementById('receiverName').value.trim();
    const amount = parseFloat(document.getElementById('amount').value);
    const transactionDate = document.getElementById('transactionDate').value;

    if (!senderName || !receiverName || isNaN(amount) || !transactionDate) {
        showToast('Please fill out all transaction fields properly.', 'error');
        return;
    }

    const newTxPayload = { senderName, receiverName, amount, transactionDate };
    const btnSubmit = document.getElementById('btnSubmitTx');
    btnSubmit.disabled = true;
    btnSubmit.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Processing...';

    try {
        const response = await fetch('/api/transactions', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(newTxPayload)
        });

        const data = await response.json();

        if (response.ok && data.success) {
            const statusMsg = data.transaction.status === 'Fraud' ? '🚨 FLAGGED AS FRAUD' : '✅ Marked as SAFE';
            showToast(`Transaction added! ${statusMsg}`, data.transaction.status === 'Fraud' ? 'error' : 'success');
            document.getElementById('transactionForm').reset();
            document.getElementById('transactionDate').value = new Date().toISOString().split('T')[0];
            updateFraudPreview();
            loadTransactions();
            loadStats();
        } else {
            showToast(data.message || 'Failed to record transaction', 'error');
        }
    } catch (err) {
        // Local Fallback Mode
        const evaluatedStatus = amount > 50000 ? 'Fraud' : 'Safe';
        const newId = localTransactions.length ? Math.max(...localTransactions.map(t => t.id)) + 1 : 1;
        const fallbackTx = { id: newId, senderName, receiverName, amount, transactionDate, status: evaluatedStatus };
        localTransactions.unshift(fallbackTx);

        showToast(`Offline Mode: Transaction saved! (${evaluatedStatus})`, evaluatedStatus === 'Fraud' ? 'error' : 'success');
        document.getElementById('transactionForm').reset();
        document.getElementById('transactionDate').value = new Date().toISOString().split('T')[0];
        updateFraudPreview();
        renderTable(localTransactions);
        loadStats();
    } finally {
        btnSubmit.disabled = false;
        btnSubmit.innerHTML = '<i class="fa-solid fa-paper-plane"></i> Save & Detect Fraud';
    }
}

// 5. Search Handler
async function handleSearch() {
    const sender = document.getElementById('searchInput').value.trim();
    const statusFilter = document.getElementById('statusFilter').value;

    try {
        const response = await fetch(`/api/transactions/search?sender=${encodeURIComponent(sender)}`);
        if (!response.ok) throw new Error();
        let results = await response.json();
        
        if (statusFilter !== 'All') {
            results = results.filter(t => t.status === statusFilter);
        }
        renderTable(results);
    } catch (err) {
        let filtered = localTransactions.filter(t => t.senderName.toLowerCase().includes(sender.toLowerCase()));
        if (statusFilter !== 'All') {
            filtered = filtered.filter(t => t.status === statusFilter);
        }
        renderTable(filtered);
    }
}

// 6. Filter Handler
async function handleFilter() {
    const status = document.getElementById('statusFilter').value;
    const sender = document.getElementById('searchInput').value.trim();

    try {
        const response = await fetch(`/api/transactions/filter?status=${encodeURIComponent(status)}`);
        if (!response.ok) throw new Error();
        let results = await response.json();

        if (sender) {
            results = results.filter(t => t.senderName.toLowerCase().includes(sender.toLowerCase()));
        }
        renderTable(results);
    } catch (err) {
        let filtered = localTransactions;
        if (status !== 'All') {
            filtered = filtered.filter(t => t.status === status);
        }
        if (sender) {
            filtered = filtered.filter(t => t.senderName.toLowerCase().includes(sender.toLowerCase()));
        }
        renderTable(filtered);
    }
}

// 7. Delete Transaction
async function deleteTransaction(id) {
    if (!confirm(`Are you sure you want to delete transaction #${id}?`)) {
        return;
    }

    try {
        const response = await fetch(`/api/transactions/delete?id=${id}`, {
            method: 'DELETE'
        });

        const data = await response.json();
        if (response.ok && data.success) {
            showToast('Transaction deleted successfully', 'success');
            loadTransactions();
            loadStats();
        } else {
            showToast(data.message || 'Failed to delete transaction', 'error');
        }
    } catch (err) {
        localTransactions = localTransactions.filter(t => t.id !== id);
        showToast('Offline Mode: Transaction deleted', 'success');
        renderTable(localTransactions);
        loadStats();
    }
}

// 8. Render Table HTML
function renderTable(transactions) {
    const tbody = document.getElementById('transactionTableBody');
    if (!tbody) return;

    if (!transactions || transactions.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7">
                    <div class="empty-state">
                        <i class="fa-solid fa-folder-open empty-state-icon"></i>
                        <p>No transactions found matching criteria.</p>
                    </div>
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = transactions.map(t => {
        const isFraud = t.status === 'Fraud';
        const badgeClass = isFraud ? 'badge-fraud' : 'badge-safe';
        const badgeIcon = isFraud ? 'fa-triangle-exclamation' : 'fa-circle-check';
        const formattedAmount = new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(t.amount);

        return `
            <tr>
                <td><strong>#${t.id}</strong></td>
                <td>${escapeHtml(t.senderName)}</td>
                <td>${escapeHtml(t.receiverName)}</td>
                <td style="font-weight: 600; ${isFraud ? 'color: var(--fraud);' : ''}">${formattedAmount}</td>
                <td>${t.transactionDate || ''}</td>
                <td>
                    <span class="badge ${badgeClass}">
                        <i class="fa-solid ${badgeIcon}"></i> ${t.status}
                    </span>
                </td>
                <td>
                    <button class="btn-delete" onclick="deleteTransaction(${t.id})" title="Delete Transaction">
                        <i class="fa-solid fa-trash-can"></i> Delete
                    </button>
                </td>
            </tr>
        `;
    }).join('');
}

// Helper Utilities
function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>"']/g, function (m) {
        return {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#039;'
        }[m];
    });
}

function showToast(message, type = 'info') {
    const toast = document.getElementById('toast');
    const toastMsg = document.getElementById('toastMessage');
    const toastIcon = document.getElementById('toastIcon');

    if (!toast || !toastMsg) return;

    toastMsg.textContent = message;
    toast.className = `alert-toast show ${type}`;

    if (type === 'success') {
        toastIcon.className = 'fa-solid fa-circle-check';
    } else if (type === 'error') {
        toastIcon.className = 'fa-solid fa-triangle-exclamation';
    } else {
        toastIcon.className = 'fa-solid fa-circle-info';
    }

    setTimeout(() => {
        toast.className = 'alert-toast';
    }, 3500);
}

function debounce(func, wait) {
    let timeout;
    return function (...args) {
        clearTimeout(timeout);
        timeout = setTimeout(() => func.apply(this, args), wait);
    };
}
