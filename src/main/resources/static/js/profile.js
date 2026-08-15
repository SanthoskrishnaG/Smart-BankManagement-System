document.addEventListener('DOMContentLoaded', async () => {
    let user;
    try {
        user = await API.get('/api/user/profile');
    } catch (e) {
        return;
    }

    const logoutBtn = document.getElementById('logout-btn');
    if(logoutBtn) logoutBtn.addEventListener('click', handleLogout);

    document.getElementById('profile-avatar').textContent = user.name.charAt(0);
    document.getElementById('profile-name').textContent = user.name;
    document.getElementById('profile-id').textContent = `ID: ${user.id}`;
    document.getElementById('profile-username').textContent = user.username;
    
    document.getElementById('profile-accounts-count').textContent = user.accounts ? user.accounts.length : 0;
    document.getElementById('profile-loans-count').textContent = user.loans ? user.loans.length : 0;
});
