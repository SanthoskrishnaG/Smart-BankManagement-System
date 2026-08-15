document.addEventListener('DOMContentLoaded', async () => {
    // Check Auth
    let user;
    try {
        user = await API.get('/api/user/profile');
    } catch (e) {
        return; // Redirected by app.js
    }

    // UI Elements
    document.getElementById('welcome-msg').textContent = `Welcome, ${user.name}`;
    document.getElementById('user-avatar').textContent = user.name.charAt(0);
    const logoutBtn = document.getElementById('logout-btn');
    if(logoutBtn) logoutBtn.addEventListener('click', handleLogout);

    const accountSelect = document.getElementById('account-select');
    let currentAccount = null;

    // Initialize Accounts
    function initAccounts() {
        accountSelect.innerHTML = '';
        if (user.accounts && user.accounts.length > 0) {
            user.accounts.forEach(acc => {
                const opt = document.createElement('option');
                opt.value = acc.accountNumber;
                opt.textContent = `${acc.accountNumber} - ${acc.type}`;
                accountSelect.appendChild(opt);
            });
            currentAccount = user.accounts[0];
            updateDashboard();
        } else {
            accountSelect.innerHTML = '<option value="">No Accounts Found</option>';
        }
    }

    accountSelect.addEventListener('change', (e) => {
        currentAccount = user.accounts.find(a => a.accountNumber === e.target.value);
        updateDashboard();
    });

    // Chart Instance
    let balanceChart = null;

    function updateDashboard() {
        if (!currentAccount) return;
        
        document.getElementById('current-balance').textContent = formatCurrency(currentAccount.balance);
        document.getElementById('account-type').textContent = currentAccount.type;

        // Sort transactions by date descending
        const txs = [...(currentAccount.transactions || [])].reverse();
        
        // Update Table (recent 5)
        const tbody = document.getElementById('recent-tx-body');
        tbody.innerHTML = '';
        txs.slice(0, 5).forEach(tx => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${formatDate(tx.date)}</td>
                <td>${tx.description}</td>
                <td><span class="badge ${tx.type === 'Deposit' ? 'badge-success' : 'badge-warning'}">${tx.type}</span></td>
                <td>${tx.type === 'Withdrawal' ? '-' : '+'}${formatCurrency(tx.amount)}</td>
            `;
            tbody.appendChild(tr);
        });

        // Update Chart
        updateChart(txs);
    }

    function updateChart(transactions) {
        const ctx = document.getElementById('balanceChart').getContext('2d');
        
        // Calculate running balance history (simple approx based on current balance and traversing back)
        let balance = currentAccount.balance;
        const dataReversed = [];
        const labelsReversed = [];

        // Chart shows up to 10 points
        const recentTxs = transactions.slice(0, 10);
        
        // Insert current state first (end of chart)
        dataReversed.push(balance);
        labelsReversed.push('Current');

        for (let tx of recentTxs) {
            if (tx.type === 'Deposit') {
                balance -= tx.amount;
            } else {
                balance += tx.amount;
            }
            dataReversed.push(balance);
            labelsReversed.push(formatDate(tx.date));
        }

        const labels = labelsReversed.reverse();
        const data = dataReversed.reverse();

        if (balanceChart) {
            balanceChart.destroy();
        }

        balanceChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Balance History',
                    data: data,
                    borderColor: '#d4a853',
                    backgroundColor: 'rgba(212, 168, 83, 0.1)',
                    borderWidth: 2,
                    fill: true,
                    tension: 0.4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false }
                },
                scales: {
                    y: {
                        grid: { color: 'rgba(255, 255, 255, 0.05)' },
                        ticks: { color: '#94a3b8' }
                    },
                    x: {
                        grid: { color: 'rgba(255, 255, 255, 0.05)' },
                        ticks: { color: '#94a3b8' }
                    }
                }
            }
        });
    }

    // Modals
    const depositModal = document.getElementById('deposit-modal');
    const withdrawModal = document.getElementById('withdraw-modal');
    
    document.getElementById('open-deposit').addEventListener('click', () => depositModal.classList.add('active'));
    document.getElementById('close-deposit').addEventListener('click', () => depositModal.classList.remove('active'));
    
    document.getElementById('open-withdraw').addEventListener('click', () => withdrawModal.classList.add('active'));
    document.getElementById('close-withdraw').addEventListener('click', () => withdrawModal.classList.remove('active'));

    // Forms
    document.getElementById('deposit-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const amount = document.getElementById('deposit-amount').value;
        try {
            await API.post('/api/user/deposit', {
                accountNumber: currentAccount.accountNumber,
                amount: parseFloat(amount)
            });
            Toast.show('Deposit successful');
            depositModal.classList.remove('active');
            e.target.reset();
            // Refresh User Data
            user = await API.get('/api/user/profile');
            initAccounts();
        } catch (err) {
            Toast.show(err.message, 'error');
        }
    });

    document.getElementById('withdraw-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const amount = document.getElementById('withdraw-amount').value;
        try {
            await API.post('/api/user/withdraw', {
                accountNumber: currentAccount.accountNumber,
                amount: parseFloat(amount)
            });
            Toast.show('Withdrawal successful');
            withdrawModal.classList.remove('active');
            e.target.reset();
            // Refresh User Data
            user = await API.get('/api/user/profile');
            initAccounts();
        } catch (err) {
            Toast.show(err.message, 'error');
        }
    });

    initAccounts();
});
