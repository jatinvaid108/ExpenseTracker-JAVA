const ADMIN_SECRET = 'change-me-secret'; // must match admin.secret
const API_BASE = '/api/admin';

async function fetchJson(path, opts={}) {
  opts.headers = opts.headers || {};
  opts.headers['X-Admin-Auth'] = ADMIN_SECRET;
  if (opts.body && typeof opts.body === 'object') {
    opts.headers['Content-Type'] = 'application/json';
    opts.body = JSON.stringify(opts.body);
  }
  const res = await fetch(path, opts);
  if (!res.ok) {
    const t = await res.text();
    throw new Error('Server error: ' + res.status + ' - ' + t);
  }
  return res.json();
}

async function loadAll() {
  try {
    const users = await fetchJson(API_BASE + '/users');
    const container = document.getElementById('usersContainer');
    container.innerHTML = '';
    const tpl = document.getElementById('user-card-tpl');

    for (const u of users) {
      const node = tpl.content.cloneNode(true);
      const card = node.querySelector('.card');
      node.querySelector('.u-id').textContent = u.id;
      // show email if present else fall back to username
      node.querySelector('.u-email-display').textContent = u.email ? u.email : (u.username ? u.username : '');
      const emailInput = node.querySelector('.u-email-input');
      emailInput.value = u.email ? u.email : (u.username ? u.username : '');

      const statusBadge = node.querySelector('.u-status');
      const active = (u.isActive === null || u.isActive === undefined) ? true : u.isActive;
      statusBadge.textContent = active ? 'Active' : 'Deactivated';
      statusBadge.classList.add(active ? 'active' : 'inactive');

      // expenses: fetch per user and render immediately
      const expBody = node.querySelector('.expenses-body');
      try {
        const expenses = await fetchJson(API_BASE + `/users/${u.id}/expenses`);
        if (Array.isArray(expenses) && expenses.length) {
          expBody.innerHTML = expenses.map(e => `
            <tr data-exp-id="${e.id}">
              <td>${e.id}</td>
              <td>${e.title || ''}</td>
              <td>${e.category || ''}</td>
              <td>${e.amount || ''}</td>
              <td>${e.date || ''}</td>
              <td>
                <button class="btn btn-muted" onclick="editExpense(${e.id})">Edit</button>
                <button class="btn btn-danger" onclick="deleteExpense(${e.id})">Delete</button>
              </td>
            </tr>
          `).join('');
        } else {
          expBody.innerHTML = '<tr><td colspan="6">No expenses</td></tr>';
        }
      } catch (err) {
        expBody.innerHTML = '<tr><td colspan="6">Failed to load expenses</td></tr>';
      }

      // wire actions
      const editBtn = node.querySelector('.u-edit');
      const saveBtn = node.querySelector('.u-save');
      const cancelBtn = node.querySelector('.u-cancel');
      const deleteBtn = node.querySelector('.u-delete');
      const toggleActiveBtn = node.querySelector('.u-toggle-active');

      editBtn.addEventListener('click', () => {
        node.querySelector('.u-email-display').style.display = 'none';
        emailInput.style.display = 'inline-block';
        editBtn.style.display = 'none';
        saveBtn.style.display = 'inline-block';
        cancelBtn.style.display = 'inline-block';
      });

      cancelBtn.addEventListener('click', () => {
        emailInput.value = u.email ? u.email : (u.username ? u.username : '');
        node.querySelector('.u-email-display').style.display = '';
        emailInput.style.display = 'none';
        editBtn.style.display = '';
        saveBtn.style.display = 'none';
        cancelBtn.style.display = 'none';
      });

      saveBtn.addEventListener('click', async () => {
        const newEmail = emailInput.value.trim();
        // read badge state for active/deactivated toggle label
        const newIsActive = statusBadge.textContent === 'Active';
        try {
          await fetchJson(API_BASE + `/users/${u.id}`, {
            method: 'PUT',
            body: { email: newEmail, isActive: newIsActive }
          });
          // reflect
          node.querySelector('.u-email-display').textContent = newEmail;
          node.querySelector('.u-email-display').style.display = '';
          emailInput.style.display = 'none';
          editBtn.style.display = '';
          saveBtn.style.display = 'none';
          cancelBtn.style.display = 'none';
          // reload all to reflect
          await loadAll();
        } catch (err) {
          alert('Failed to save user: ' + err.message);
        }
      });

      deleteBtn.addEventListener('click', async () => {
        if (!confirm('Delete user ' + u.id + '?')) return;
        try {
          await fetchJson(API_BASE + `/users/${u.id}`, { method: 'DELETE' });
          await loadAll();
        } catch (err) {
          alert('Delete failed: ' + err.message);
        }
      });

      // Toggle active button label and behavior
      toggleActiveBtn.textContent = active ? 'Deactivate' : 'Activate';
      toggleActiveBtn.addEventListener('click', async () => {
        const newState = !active;
        try {
          await fetchJson(API_BASE + `/users/${u.id}/deactivate?active=${newState}`, { method: 'POST' });
          await loadAll();
        } catch (err) {
          alert('Failed to toggle active: ' + err.message);
        }
      });

      container.appendChild(node);
    } // end for users
  } catch (e) {
    document.getElementById('usersContainer').innerHTML = '<div style="color:red;">Failed to load users: '+e.message+'</div>';
  }
}

async function editExpense(expId) {
  alert('Edit expense UI not implemented yet for id: ' + expId);
}

async function deleteExpense(expId) {
  if (!confirm('Delete expense ' + expId + '?')) return;
  try {
    await fetchJson(API_BASE + `/expenses/${expId}`, { method: 'DELETE' });
    await loadAll();
  } catch (err) {
    alert('Failed to delete expense: ' + err.message);
  }
}

// initial load
loadAll();
