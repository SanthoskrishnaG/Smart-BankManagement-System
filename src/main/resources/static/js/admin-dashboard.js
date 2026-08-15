document.addEventListener('DOMContentLoaded', async () => {
    // Check auth
    try {
        const session = await API.get('/api/auth/session');
        if (session.role !== 'admin') {
            window.location.href = '/index.html';
        }
    } catch (e) {
        window.location.href = '/index.html';
        return;
    }

    document.getElementById('logout-btn').addEventListener('click', handleLogout);

    const usersBody = document.getElementById('users-body');
    const loansBody = document.getElementById('admin-loans-body');

    async function loadAdminData() {
        try {
            const users = await API.get('/api/admin/users');
            
            // Calc stats
            document.getElementById('total-users').textContent = users.length;
            
            let totalBal = 0;
            let totalAccs = 0;
            let allLoans = [];

            users.forEach(u => {
                if (u.accounts) {
                    totalAccs += u.accounts.length;
                    u.accounts.forEach(a => totalBal += a.balance);
                }
                if (u.loans) {
                    u.loans.forEach(l => {
                        allLoans.push({ ...l, userId: u.id });
                    });
                }
            });

            document.getElementById('total-balance').textContent = formatCurrency(totalBal);
            document.getElementById('total-accounts').textContent = totalAccs;

            // Render Users
            usersBody.innerHTML = '';
            users.forEach(u => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${u.id}</td>
                    <td>${u.name}</td>
                    <td>${u.username}</td>
                    <td>${u.accounts ? u.accounts.length : 0}</td>
                    <td>
                        <button class="btn btn-outline delete-user" data-id="${u.id}" style="padding: 0.25rem 0.5rem; color: #ef4444; border-color: #ef4444;">Delete</button>
                    </td>
                `;
                usersBody.appendChild(tr);
            });

            // Bind Delete events
            document.querySelectorAll('.delete-user').forEach(btn => {
                btn.addEventListener('click', async (e) => {
                    const id = e.target.getAttribute('data-id');
                    if (confirm('Are you sure you want to delete this user?')) {
                        try {
                            await API.delete(`/api/admin/users/${id}`);
                            Toast.show('User deleted successfully');
                            loadAdminData();
                        } catch (err) {
                            Toast.show(err.message, 'error');
                        }
                    }
                });
            });

            // Render Loans (Pending only or all)
            loansBody.innerHTML = '';
            const pendingLoans = allLoans.filter(l => l.status === 'Pending');
            
            if (pendingLoans.length === 0) {
                loansBody.innerHTML = '<tr><td colspan="6" class="text-center">No pending loans</td></tr>';
            } else {
                pendingLoans.forEach(l => {
                    const tr = document.createElement('tr');
                    tr.innerHTML = `
                        <td>${l.loanId}</td>
                        <td>${l.userId}</td>
                        <td>${formatCurrency(l.amount)}</td>
                        <td>${l.reason}</td>
                        <td><span class="badge badge-warning">${l.status}</span></td>
                        <td>
                            <button class="btn btn-primary approve-loan" data-id="${l.loanId}" style="padding: 0.25rem 0.5rem;">Approve</button>
                            <button class="btn btn-outline reject-loan" data-id="${l.loanId}" style="padding: 0.25rem 0.5rem; color: #ef4444;">Reject</button>
                        </td>
                    `;
                    loansBody.appendChild(tr);
                });

                // Bind Loan Actions
                document.querySelectorAll('.approve-loan').forEach(btn => {
                    btn.addEventListener('click', async (e) => {
                        const id = e.target.getAttribute('data-id');
                        try {
                            await API.put(`/api/admin/loans/${id}/approve`, {});
                            Toast.show('Loan approved');
                            loadAdminData();
                        } catch (err) {
                            Toast.show(err.message, 'error');
                        }
                    });
                });

                document.querySelectorAll('.reject-loan').forEach(btn => {
                    btn.addEventListener('click', async (e) => {
                        const id = e.target.getAttribute('data-id');
                        try {
                            await API.put(`/api/admin/loans/${id}/reject`, {});
                            Toast.show('Loan rejected');
                            loadAdminData();
                        } catch (err) {
                            Toast.show(err.message, 'error');
                        }
                    });
                });
            }
        } catch (error) {
            Toast.show('Failed to load admin data', 'error');
        }
    }

    // Modal
    const addUserModal = document.getElementById('add-user-modal');
    document.getElementById('open-add-user').addEventListener('click', () => addUserModal.classList.add('active'));
    document.getElementById('close-add-user').addEventListener('click', () => addUserModal.classList.remove('active'));

    document.getElementById('add-user-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const name = document.getElementById('add-name').value;
        const username = document.getElementById('add-username').value;
        const password = document.getElementById('add-password').value;

        try {
            await API.post('/api/admin/users', { name, username, password });
            Toast.show('User added successfully');
            addUserModal.classList.remove('active');
            e.target.reset();
            loadAdminData();
        } catch (err) {
            Toast.show(err.message, 'error');
        }
    });

    loadAdminData();
});
