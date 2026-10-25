/* ===================================================================
   FarmFresh Admin - shared helpers, loaded on every admin page
   (dash.html, farmer.html, customer.html, product.html, order.html, msg.html)
   Include AFTER api.js.
   =================================================================== */

// Guards every admin page: bounce to login if not logged in as admin
const adminSession = ffRequireRole('admin', '../user/loginpage.html');

function fmtMoney(n) {
    n = Number(n) || 0;
    return "₹" + n.toLocaleString('en-IN');
}

// order status -> which CSS badge color class to reuse (Pending/Confirmed/Shipped/Delivered/Cancelled)
function orderBadgeClass(status) {
    const map = { Pending: 'pending', Confirmed: 'transit', Shipped: 'transit', Delivered: 'ok', Cancelled: 'rejected' };
    return map[status] || 'pending';
}

// generic badge for farmer/product status ("ok" / "pending" / "blocked") and message status
function badgeHtml(status, customLabel) {
    const labels = { ok: 'Approved', pending: 'Pending', rejected: 'Rejected', blocked: 'Blocked', transit: 'In Transit', UNREAD: 'Unread', READ: 'Read' };
    const cssClass = status === 'UNREAD' ? 'pending' : (status === 'READ' ? 'ok' : status);
    return '<span class="badge ' + cssClass + '">' + (customLabel || labels[status] || status) + '</span>';
}

function orderBadgeHtml(status) {
    return '<span class="badge ' + orderBadgeClass(status) + '">' + status + '</span>';
}

function showToast(msg) {
    const stack = document.getElementById('toastStack');
    if (!stack) { console.log(msg); return; }
    const t = document.createElement('div');
    t.className = 'toast';
    t.textContent = msg;
    stack.appendChild(t);
    setTimeout(function () { t.remove(); }, 2500);
}

function toggleMobileNav() {
    const app = document.getElementById('app');
    if (app) app.classList.toggle('mobile-open');
}

function adminLogout(e) {
    if (e) e.preventDefault();
    if (confirm('Are you sure you want to logout?')) {
        ffLogout('../user/loginpage.html');
    }
}

// Unread message count shown as a badge on the "Messages" sidebar link, on every page
async function renderMsgBadge() {
    const el = document.getElementById('msgBadge');
    if (!el) return;
    try {
        const messages = await ffGet('/admin/messages');
        const count = messages.filter(m => m.status === 'UNREAD').length;
        el.textContent = count;
        el.style.display = count > 0 ? 'inline-block' : 'none';
    } catch (err) {
        el.style.display = 'none';
    }
}
document.addEventListener('DOMContentLoaded', renderMsgBadge);
