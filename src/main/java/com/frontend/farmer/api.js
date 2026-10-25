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

/* ===================================================================
   Farmer Notification System (In-App notifications, badge & polling)
   =================================================================== */

let lastKnownUnreadCount = 0;
let isFirstNotificationPoll = true;

async function ffGetNotifications() {
    return await ffGet("/farmer/notifications");
}

async function ffGetUnreadNotificationCount() {
    return await ffGet("/farmer/notifications/unread-count");
}

async function ffMarkNotificationRead(id) {
    return await ffPut("/farmer/notifications/" + id + "/read", {});
}

async function ffMarkAllNotificationsRead() {
    return await ffPut("/farmer/notifications/mark-all-read", {});
}

function formatNotificationTime(isoString) {
    if (!isoString) return "";
    const date = new Date(isoString);
    const now = new Date();
    const diffSec = Math.floor((now - date) / 1000);

    if (diffSec < 60) return "Just now";
    if (diffSec < 3600) return Math.floor(diffSec / 60) + "m ago";
    if (diffSec < 86400) return Math.floor(diffSec / 3600) + "h ago";
    return date.toLocaleDateString();
}

function showFarmerToast(title, message, orderId) {
    let container = document.getElementById("ffToastContainer");
    if (!container) {
        container = document.createElement("div");
        container.id = "ffToastContainer";
        container.className = "toast-container position-fixed bottom-0 end-0 p-3";
        container.style.zIndex = "1100";
        document.body.appendChild(container);
    }

    const toastEl = document.createElement("div");
    toastEl.className = "toast show shadow-lg border-0 mb-2";
    toastEl.style.borderRadius = "12px";
    toastEl.style.overflow = "hidden";
    toastEl.setAttribute("role", "alert");
    toastEl.setAttribute("aria-live", "assertive");
    toastEl.setAttribute("aria-atomic", "true");

    const ordersPageUrl = window.location.pathname.endsWith("orders.html") ? "#" : "orders.html";
    const actionUrl = orderId ? (ordersPageUrl + (orderId ? "?orderId=" + orderId : "")) : ordersPageUrl;

    toastEl.innerHTML = `
        <div class="toast-header bg-success text-white py-2 px-3">
            <i class="bi bi-bell-fill me-2"></i>
            <strong class="me-auto">${title}</strong>
            <small class="text-white-50">Just now</small>
            <button type="button" class="btn-close btn-close-white ms-2" onclick="this.closest('.toast').remove()"></button>
        </div>
        <div class="toast-body bg-white text-dark p-3">
            <p class="mb-2" style="font-size: 13.5px;">${message}</p>
            <div class="d-flex justify-content-end gap-2 mt-2">
                <a href="${actionUrl}" class="btn btn-sm btn-success px-3" style="font-size:12px;border-radius:6px;">
                    <i class="bi bi-check-circle me-1"></i> Accept Order
                </a>
            </div>
        </div>
    `;

    container.appendChild(toastEl);
    setTimeout(() => {
        if (toastEl.parentNode) toastEl.remove();
    }, 10000);
}

async function renderFarmerNotificationsMenu() {
    const listEl = document.getElementById("farmerNotificationItemsList");
    if (!listEl) return;

    try {
        const notifs = await ffGetNotifications();
        if (!notifs || notifs.length === 0) {
            listEl.innerHTML = `
                <li class="p-4 text-center text-muted" style="font-size: 13.5px;">
                    <i class="bi bi-bell-slash d-block fs-3 mb-2 text-muted"></i>
                    No notifications yet.
                </li>
            `;
            return;
        }

        listEl.innerHTML = notifs.slice(0, 15).map(n => {
            const bgClass = n.isRead ? "bg-white" : "bg-light border-start border-4 border-success";
            const unreadDot = n.isRead ? "" : '<span class="badge bg-success rounded-pill me-1" style="font-size: 9px;">NEW</span>';
            const timeStr = formatNotificationTime(n.createdAt);
            const isOrder = n.orderId != null;

            return `
                <li class="p-2 border-bottom ${bgClass}" style="transition: background .2s; font-size: 13px;">
                    <div class="d-flex justify-content-between align-items-start mb-1">
                        <strong class="text-dark">${unreadDot}${n.title}</strong>
                        <small class="text-muted ms-2">${timeStr}</small>
                    </div>
                    <div class="text-secondary mb-2" style="font-size: 12.5px; line-height: 1.4;">${n.message}</div>
                    <div class="d-flex justify-content-between align-items-center">
                        ${isOrder ? `
                            <button class="btn btn-sm btn-outline-success py-0 px-2" style="font-size: 11.5px; border-radius: 4px;" onclick="ffHandleNotificationOrderClick(${n.id}, ${n.orderId})">
                                <i class="bi bi-arrow-right-circle me-1"></i>Review & Accept
                            </button>
                        ` : '<span></span>'}
                        ${!n.isRead ? `
                            <button class="btn btn-link btn-sm text-muted p-0 text-decoration-none" style="font-size: 11px;" onclick="ffHandleMarkRead(${n.id}, event)">
                                Mark read
                            </button>
                        ` : ''}
                    </div>
                </li>
            `;
        }).join('');
    } catch (err) {
        listEl.innerHTML = `
            <li class="p-3 text-center text-danger" style="font-size: 12.5px;">
                Could not load notifications.
            </li>
        `;
    }
}

async function ffHandleMarkRead(id, event) {
    if (event) event.stopPropagation();
    try {
        await ffMarkNotificationRead(id);
        await updateFarmerNotificationBadge();
        await renderFarmerNotificationsMenu();
    } catch (err) {
        console.error("Failed to mark notification read", err);
    }
}

async function ffHandleNotificationOrderClick(notifId, orderId) {
    try {
        await ffMarkNotificationRead(notifId);
    } catch (e) {}

    const isOrdersPage = window.location.pathname.endsWith("orders.html");
    if (isOrdersPage) {
        if (typeof viewOrderById === "function") {
            viewOrderById(orderId);
        } else {
            window.location.hash = "#order-" + orderId;
            window.location.reload();
        }
    } else {
        window.location.href = "orders.html?orderId=" + orderId;
    }
}

async function updateFarmerNotificationBadge() {
    const badge = document.getElementById("farmerNotificationBadge");
    if (!badge) return;

    try {
        const res = await ffGetUnreadNotificationCount();
        const count = res ? (res.unreadCount || 0) : 0;

        if (count > 0) {
            badge.textContent = count > 99 ? "99+" : count;
            badge.style.display = "inline-block";
        } else {
            badge.style.display = "none";
        }

        // Detect new order arrival in real time
        if (!isFirstNotificationPoll && count > lastKnownUnreadCount) {
            // New notification arrived! Fetch top notification to show toast
            try {
                const notifs = await ffGetNotifications();
                if (notifs && notifs.length > 0) {
                    const top = notifs[0];
                    showFarmerToast(top.title || "New Notification", top.message || "You have a new update.", top.orderId);
                }
            } catch (e) {}
        }

        lastKnownUnreadCount = count;
        isFirstNotificationPoll = false;
    } catch (err) {
        // Silently ignore network poll errors
    }
}

function initFarmerNotifications() {
    const session = ffGetSession();
    if (!session || session.role !== "farmer") return;

    const navBarList = document.querySelector(".navbar-nav");
    if (!navBarList || document.getElementById("farmerNotificationItem")) return;

    // Create Notification Bell Dropdown Item
    const li = document.createElement("li");
    li.className = "nav-item dropdown";
    li.id = "farmerNotificationItem";
    li.innerHTML = `
        <a class="nav-link position-relative px-2 d-flex align-items-center" href="#" id="farmerNotificationDropdownBtn" role="button" data-bs-toggle="dropdown" aria-expanded="false" title="Notifications">
            <i class="bi bi-bell-fill fs-5 text-warning"></i>
            <span class="position-absolute top-1 start-100 translate-middle badge rounded-pill bg-danger" id="farmerNotificationBadge" style="display:none; font-size: 10px; padding: 3px 6px;">
                0
            </span>
        </a>
        <div class="dropdown-menu dropdown-menu-end shadow p-0" id="farmerNotificationMenu" style="width: 350px; max-height: 480px; border-radius: 12px; overflow: hidden; border: 1px solid #e2e8f0;">
            <div class="p-3 bg-light border-bottom d-flex justify-content-between align-items-center">
                <span class="fw-bold text-dark"><i class="bi bi-bell me-1 text-success"></i> Notifications</span>
                <button type="button" class="btn btn-link btn-sm p-0 text-success text-decoration-none fw-semibold" style="font-size: 12px;" id="markAllNotifsReadBtn">
                    Mark all read
                </button>
            </div>
            <ul class="list-unstyled mb-0" id="farmerNotificationItemsList" style="max-height: 380px; overflow-y: auto;">
                <li class="p-3 text-center text-muted" style="font-size: 13px;">Loading...</li>
            </ul>
            <div class="p-2 text-center bg-light border-top">
                <a href="orders.html" class="text-decoration-none small fw-semibold text-success">
                    View All Orders <i class="bi bi-arrow-right"></i>
                </a>
            </div>
        </div>
    `;

    const badgeWrap = document.getElementById("farmerNameBadgeWrap");
    if (badgeWrap) {
        navBarList.insertBefore(li, badgeWrap);
    } else {
        navBarList.appendChild(li);
    }

    // Dropdown open handler
    const dropdownBtn = document.getElementById("farmerNotificationDropdownBtn");
    if (dropdownBtn) {
        dropdownBtn.addEventListener("click", () => {
            renderFarmerNotificationsMenu();
        });
    }

    // Mark all read button
    const markAllBtn = document.getElementById("markAllNotifsReadBtn");
    if (markAllBtn) {
        markAllBtn.addEventListener("click", async (e) => {
            e.preventDefault();
            try {
                await ffMarkAllNotificationsRead();
                await updateFarmerNotificationBadge();
                await renderFarmerNotificationsMenu();
            } catch (err) {
                console.error("Failed to mark all notifications read", err);
            }
        });
    }

    // Initial badge check and start periodic polling (every 12 seconds)
    updateFarmerNotificationBadge();
    setInterval(updateFarmerNotificationBadge, 12000);
}

document.addEventListener("DOMContentLoaded", () => {
    initFarmerNotifications();
});
