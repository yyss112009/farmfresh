/* ===================================================================
   FarmFresh - shared frontend <-> backend helper
   Include this file on every page BEFORE any page-specific <script>.
   Talks to the Spring Boot backend running at API_BASE.
   =================================================================== */

const API_BASE = "http://localhost:8080/api";

/* ---- session (token/role/id) stored in localStorage ---- */
const FF_SESSION_KEY = "ffSession";

function ffSaveSession(auth) {
    localStorage.setItem(FF_SESSION_KEY, JSON.stringify(auth));
}

function ffGetSession() {
    try { return JSON.parse(localStorage.getItem(FF_SESSION_KEY)) || null; }
    catch (e) { return null; }
}

function ffClearSession() {
    localStorage.removeItem(FF_SESSION_KEY);
}

function ffLogout(redirectUrl) {
    ffClearSession();
    if (redirectUrl) window.location.href = redirectUrl;
}

/**
 * Redirects the browser to the correct home page for the given role.
 * Paths are relative to the /frontend root (user/farmer/admin folders sit next to each other).
 */
function ffRedirectForRole(role) {
    if (role === "farmer") window.location.href = "../farmer/farmerdash.html";
    else if (role === "admin") window.location.href = "../admin/dash.html";
    else window.location.href = "frounted.html";
}

/**
 * Guards a dashboard page: if nobody is logged in (or the wrong role is),
 * bounce back to the login page. Call this at the top of every protected page.
 */
function ffRequireRole(role, loginUrl) {
    const session = ffGetSession();
    if (!session || session.role !== role) {
        window.location.href = loginUrl;
        return null;
    }
    return session;
}

/**
 * Core fetch wrapper: adds the JSON headers + auth token, and turns a
 * non-2xx response into a thrown Error with the backend's error message.
 */
async function ffApi(path, options) {
    options = options || {};
    const headers = Object.assign({ "Content-Type": "application/json" }, options.headers || {});

    const session = ffGetSession();
    if (session && session.token) {
        headers["Authorization"] = "Bearer " + session.token;
    }

    let response;
    try {
        response = await fetch(API_BASE + path, Object.assign({}, options, { headers }));
    } catch (networkError) {
        throw new Error("Could not reach the server. Is the Spring Boot backend running on port 8080?");
    }

    let data = null;
    const text = await response.text();
    if (text) {
        try { data = JSON.parse(text); } catch (e) { data = null; }
    }

    if (!response.ok) {
        const message = (data && data.message) ? data.message : ("Request failed (" + response.status + ")");
        throw new Error(message);
    }

    return data;
}

function ffGet(path) { return ffApi(path, { method: "GET" }); }
function ffPost(path, body) { return ffApi(path, { method: "POST", body: JSON.stringify(body) }); }
function ffPut(path, body) { return ffApi(path, { method: "PUT", body: JSON.stringify(body) }); }
function ffDelete(path) { return ffApi(path, { method: "DELETE" }); }

/* ---- Toggle Login/Logout in navbar based on session ---- */
function ffInitNavbarAuth() {
    const session = ffGetSession();
    const loginBtns = document.querySelectorAll('a.ff-nav-btn[href*="loginpage.html"], a[href*="loginpage.html"].ff-nav-link');
    
    loginBtns.forEach(function (loginBtn) {
        if (session) {
            const roleName = session.fullName ? session.fullName.split(' ')[0] : (session.role || 'User');
            // Logout icon (door exit)
            const logoutSvg = '<svg viewBox="0 0 24 24" style="width:15px;height:15px;fill:none;stroke:currentColor;stroke-width:2;"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path><polyline points="16 17 21 12 16 7"></polyline><line x1="21" y1="12" x2="9" y2="12"></line></svg>';
            
            loginBtn.innerHTML = logoutSvg + ' ' + (session.fullName ? 'Logout (' + roleName + ')' : 'Logout');
            loginBtn.title = 'Logged in as ' + (session.fullName || session.email || session.role) + ' - Click to Logout';
            loginBtn.href = '#';
            loginBtn.classList.remove('active');
            
            // Remove previous cloned listeners
            const newBtn = loginBtn.cloneNode(true);
            loginBtn.parentNode.replaceChild(newBtn, loginBtn);
            
            newBtn.addEventListener('click', function (e) {
                e.preventDefault();
                if (confirm('Are you sure you want to logout?')) {
                    const isUserFolder = window.location.pathname.includes('/user/');
                    const redirectPath = isUserFolder ? 'loginpage.html' : '../user/loginpage.html';
                    ffLogout(redirectPath);
                }
            });
        }
    });
}

window.ffInitNavbarAuth = ffInitNavbarAuth;

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', ffInitNavbarAuth);
} else {
    ffInitNavbarAuth();
}
window.addEventListener('load', ffInitNavbarAuth);

