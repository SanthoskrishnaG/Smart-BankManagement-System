// Global utilities for GlobalTrust Bank

// Toast Notification System
const Toast = {
    container: null,
    
    init() {
        if (!document.getElementById('toast-container')) {
            this.container = document.createElement('div');
            this.container.id = 'toast-container';
            document.body.appendChild(this.container);
        } else {
            this.container = document.getElementById('toast-container');
        }
    },
    
    show(message, type = 'success') {
        this.init();
        
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.textContent = message;
        
        this.container.appendChild(toast);
        
        setTimeout(() => {
            toast.style.animation = 'fadeOut 0.3s ease forwards';
            setTimeout(() => {
                if (toast.parentNode) {
                    toast.parentNode.removeChild(toast);
                }
            }, 300);
        }, 3000);
    }
};

// API Utility Wrapper
const API = {
    async request(endpoint, options = {}) {
        const defaultOptions = {
            headers: {
                'Content-Type': 'application/json'
            }
        };
        
        if (options.body && typeof options.body !== 'string') {
            options.body = JSON.stringify(options.body);
        }
        
        const finalOptions = { ...defaultOptions, ...options };
        
        try {
            const response = await fetch(endpoint, finalOptions);
            const data = await response.json().catch(() => ({}));
            
            if (!response.ok) {
                if (response.status === 401 && !endpoint.includes('/auth/login')) {
                    window.location.href = '/index.html';
                }
                throw new Error(data.error || 'API Request Failed');
            }
            
            return data;
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    },
    
    async get(endpoint) {
        return this.request(endpoint, { method: 'GET' });
    },
    
    async post(endpoint, body) {
        return this.request(endpoint, { method: 'POST', body });
    },

    async put(endpoint, body) {
        return this.request(endpoint, { method: 'PUT', body });
    },

    async delete(endpoint) {
        return this.request(endpoint, { method: 'DELETE' });
    }
};

// Format Currency
function formatCurrency(amount) {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
    }).format(amount);
}

// Format Date
function formatDate(dateString) {
    const options = { year: 'numeric', month: 'short', day: 'numeric' };
    return new Date(dateString).toLocaleDateString('en-US', options);
}

// Logout handler
async function handleLogout() {
    try {
        await API.post('/api/auth/logout', {});
        window.location.href = '/index.html';
    } catch (e) {
        Toast.show('Logout failed', 'error');
    }
}
