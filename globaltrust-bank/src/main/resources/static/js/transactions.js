document.addEventListener('DOMContentLoaded', async () => {
    let user;
    try {
        user = await API.get('/api/user/profile');
    } catch (e) {
        return;
    }

    const logoutBtn = document.getElementById('logout-btn');
    if(logoutBtn) logoutBtn.addEventListener('click', handleLogout);

    const accountFilter = document.getElementById('account-filter');
    const typeFilter = document.getElementById('type-filter');
    const txBody = document.getElementById('tx-body');

    let allTxs = [];

    function initFilters() {
        if (user.accounts) {
            user.accounts.forEach(acc => {
                const opt = document.createElement('option');
                opt.value = acc.accountNumber;
                opt.textContent = `${acc.accountNumber} - ${acc.type}`;
                accountFilter.appendChild(opt);
                
                if (acc.transactions) {
                    acc.transactions.forEach(tx => {
                        allTxs.push({ ...tx, accountNumber: acc.accountNumber });
                    });
                }
            });
            // Sort by date descending
            allTxs.sort((a, b) => new Date(b.date) - new Date(a.date));
            renderTransactions();
        }
    }

    function renderTransactions() {
        const accF = accountFilter.value;
        const typF = typeFilter.value;

        txBody.innerHTML = '';
        const filtered = allTxs.filter(tx => {
            const matchAcc = accF === 'all' || tx.accountNumber === accF;
            const matchType = typF === 'all' || tx.type === typF || (typF === 'Withdrawal' && tx.type.includes('Withdrawal')) || (typF === 'Deposit' && tx.type.includes('Deposit'));
            return matchAcc && matchType;
        });

        if (filtered.length === 0) {
            txBody.innerHTML = '<tr><td colspan="6" class="text-center">No transactions found</td></tr>';
            return;
        }

        filtered.forEach(tx => {
            const isDeposit = tx.type.includes('Deposit') || tx.type.includes('Disbursement');
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${tx.id}</td>
                <td>${tx.accountNumber}</td>
                <td>${formatDate(tx.date)}</td>
                <td>${tx.description}</td>
                <td><span class="badge ${isDeposit ? 'badge-success' : 'badge-warning'}">${tx.type}</span></td>
                <td>${isDeposit ? '+' : '-'}${formatCurrency(tx.amount)}</td>
            `;
            txBody.appendChild(tr);
        });
    }

    accountFilter.addEventListener('change', renderTransactions);
    typeFilter.addEventListener('change', renderTransactions);

    initFilters();
});
