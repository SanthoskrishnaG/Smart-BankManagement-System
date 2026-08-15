document.addEventListener('DOMContentLoaded', () => {
    
    // Check if already logged in
    fetch('/api/auth/session')
        .then(res => {
            if (res.ok) return res.json();
            throw new Error();
        })
        .then(data => {
            if (data.role === 'admin') window.location.href = '/admin-dashboard.html';
            else if (data.role === 'user') window.location.href = '/user-dashboard.html';
        })
        .catch(() => { /* not logged in, show login page */ });

    // Login logic
    const loginForm = document.getElementById('login-form');
    const roleToggle = document.getElementById('role-toggle');

    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const username = loginForm.username.value;
            const password = loginForm.password.value;
            const role = roleToggle.checked ? 'admin' : 'user';

            try {
                const res = await API.post('/api/auth/login', { username, password, role });
                Toast.show('Login successful');
                setTimeout(() => {
                    if (res.role === 'admin') {
                        window.location.href = '/admin-dashboard.html';
                    } else {
                        window.location.href = '/user-dashboard.html';
                    }
                }, 500);
            } catch (error) {
                Toast.show(error.message, 'error');
            }
        });
    }

    // Modal toggling
    const registerModal = document.getElementById('register-modal');
    const openRegisterBtn = document.getElementById('open-register');
    const closeRegisterBtn = document.getElementById('close-register');

    if (openRegisterBtn && registerModal) {
        openRegisterBtn.addEventListener('click', (e) => {
            e.preventDefault();
            registerModal.classList.add('active');
        });
    }

    if (closeRegisterBtn && registerModal) {
        closeRegisterBtn.addEventListener('click', () => {
            registerModal.classList.remove('active');
        });
    }

    // Registration logic
    const registerForm = document.getElementById('register-form');
    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const name = registerForm.name.value;
            const username = registerForm.reg_username.value;
            const password = registerForm.reg_password.value;

            try {
                await API.post('/api/auth/register', { name, username, password });
                Toast.show('Registration successful! Please login.');
                registerModal.classList.remove('active');
                registerForm.reset();
            } catch (error) {
                Toast.show(error.message, 'error');
            }
        });
    }
});
