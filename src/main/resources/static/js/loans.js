document.addEventListener('DOMContentLoaded', async () => {
    let user;
    try {
        user = await API.get('/api/user/profile');
    } catch (e) {
        return;
    }

    const logoutBtn = document.getElementById('logout-btn');
    if(logoutBtn) logoutBtn.addEventListener('click', handleLogout);

    const loansBody = document.getElementById('loans-body');

    function renderLoans() {
        loansBody.innerHTML = '';
        if (!user.loans || user.loans.length === 0) {
            loansBody.innerHTML = '<tr><td colspan="5" class="text-center">No loans applied yet</td></tr>';
            return;
        }

        user.loans.forEach(loan => {
            let badgeClass = 'badge-warning'; // Pending
            if (loan.status === 'Approved') badgeClass = 'badge-success';
            if (loan.status === 'Rejected') badgeClass = 'badge-danger';

            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${loan.loanId}</td>
                <td>${formatCurrency(loan.amount)}</td>
                <td>${loan.duration}</td>
                <td>${loan.reason}</td>
                <td><span class="badge ${badgeClass}">${loan.status}</span></td>
            `;
            loansBody.appendChild(tr);
        });
    }

    renderLoans();

    // Modal
    const loanModal = document.getElementById('loan-modal');
    document.getElementById('open-loan-modal').addEventListener('click', () => loanModal.classList.add('active'));
    document.getElementById('close-loan-modal').addEventListener('click', () => loanModal.classList.remove('active'));

    document.getElementById('loan-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const amount = document.getElementById('loan-amount').value;
        const duration = document.getElementById('loan-duration').value;
        const reason = document.getElementById('loan-reason').value;

        try {
            await API.post('/api/user/loan', { amount, duration, reason });
            Toast.show('Loan application submitted successfully');
            loanModal.classList.remove('active');
            e.target.reset();
            
            user = await API.get('/api/user/profile');
            renderLoans();
        } catch (error) {
            Toast.show(error.message, 'error');
        }
    });
});
