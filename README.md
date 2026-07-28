<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Login - Digital Payment Fraud Detection</title>
    <link rel="stylesheet" href="style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
    <div class="login-wrapper">
        <div class="login-card">
            <div class="login-header">
                <div class="brand-icon">
                    <i class="fa-solid fa-shield-halved"></i>
                </div>
                <h2>Digital Payment Fraud System</h2>
                <p>Enter your credentials to access the security portal</p>
            </div>

            <form id="loginForm">
                <div class="form-group">
                    <label for="username"><i class="fa-solid fa-user"></i> Username</label>
                    <input type="text" id="username" class="form-control" placeholder="Enter username (e.g. admin)" required autocomplete="username">
                </div>

                <div class="form-group">
                    <label for="password"><i class="fa-solid fa-lock"></i> Password</label>
                    <input type="password" id="password" class="form-control" placeholder="Enter password (e.g. password123)" required autocomplete="current-password">
                </div>

                <button type="submit" id="btnLogin" class="btn-primary">
                    <span>Sign In to Dashboard</span>
                    <i class="fa-solid fa-arrow-right"></i>
                </button>
            </form>
        </div>
    </div>

    <!-- Alert Toast Notification -->
    <div id="toast" class="alert-toast">
        <i id="toastIcon" class="fa-solid fa-circle-info"></i>
        <span id="toastMessage">Notification message</span>
    </div>

    <script src="script.js"></script>
    <script>
        // Check if already logged in
        if (localStorage.getItem('fraud_user')) {
            window.location.href = 'dashboard.html';
        }

        document.getElementById('loginForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const btn = document.getElementById('btnLogin');
            btn.disabled = true;
            btn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Authenticating...';

            const username = document.getElementById('username').value.trim();
            const password = document.getElementById('password').value.trim();

            try {
                const response = await fetch('/api/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ username, password })
                });

                const data = await response.json();

                if (response.ok && data.success) {
                    showToast('Login successful! Redirecting...', 'success');
                    localStorage.setItem('fraud_user', JSON.stringify({
                        username: data.username,
                        fullName: data.fullName
                    }));
                    setTimeout(() => {
                        window.location.href = 'dashboard.html';
                    }, 800);
                } else {
                    showToast(data.message || 'Invalid credentials. Try username: admin, password: password123', 'error');
                }
            } catch (err) {
                // Fallback for offline static browser preview
                if (username === 'admin' && password === 'password123') {
                    showToast('Offline Mode: Login Successful!', 'success');
                    localStorage.setItem('fraud_user', JSON.stringify({ username: 'admin', fullName: 'System Administrator' }));
                    setTimeout(() => { window.location.href = 'dashboard.html'; }, 800);
                } else {
                    showToast('Server error or database connection issue. Check backend logs.', 'error');
                }
            } finally {
                btn.disabled = false;
                btn.innerHTML = '<span>Sign In to Dashboard</span> <i class="fa-solid fa-arrow-right"></i>';
            }
        });
    </script>
</body>
</html>
