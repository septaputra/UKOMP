<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>{{ config('app.name','StudyMate') }} - Admin</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.tailwindcss.com"></script>
    <script defer src="https://unpkg.com/alpinejs@3.x.x/dist/cdn.min.js"></script>
    <style>
        .brand{color:var(--primary)}

        /* Theme variables */
        .theme-light { --bg: #F8FAFC; --sidebar:#FFFFFF; --card:#FFFFFF; --text:#0F172A; --muted:#6B7280; --border:#E5E7EB; --primary:#FF7A18; }
        .theme-dark { --bg: #0B1220; --sidebar:#0F172A; --card:#111827; --text:#E5E7EB; --muted:#9CA3AF; --border:#1F2933; --primary:#FF7A18; }

        /* Base */
        body { background: var(--bg); color: var(--text); transition: background .25s ease, color .25s ease; }

        /* Sidebar */
        .theme-light aside { background: var(--sidebar); color: var(--text); }
        .theme-dark aside { background: var(--sidebar); color: var(--text); }
        .theme-dark aside a, .theme-dark aside .text-xs { color: var(--muted); }

        /* Cards / containers that use bg-white should follow theme */
        .theme-dark .bg-white { background-color: var(--card) !important; color: var(--text) !important; }
        .theme-dark .text-gray-500, .theme-dark .text-muted { color: var(--muted) !important; }
        .theme-dark .rounded-2xl, .theme-dark .rounded-xl, .theme-dark .rounded-3, .theme-dark .rounded-4 { background: var(--card) !important; }

        /* Tables */
        .theme-dark table { color: var(--text); }
        .theme-dark .table-borderless tbody tr { border-color: var(--border); }
        .theme-dark .table, .theme-dark .table > thead > tr > th, .theme-dark .table > tbody > tr > td {
            background: var(--card) !important;
            color: var(--text) !important;
            border-color: var(--border) !important;
        }
        .theme-dark .table thead th { background: transparent; color: var(--text); }
        .theme-dark .table tbody tr { border-bottom: 1px solid var(--border); }
        .theme-dark .table-responsive { background: transparent; }

        /* Forms */
        .form-control { background: var(--card); color: var(--text); border: 1px solid var(--border); transition: background .2s, border-color .2s, color .2s; }
        .form-control:focus { box-shadow: 0 0 0 0.2rem rgba(255,122,24,0.12); border-color: var(--primary); }

        /* Inputs, selects and placeholders in dark mode (and general dark theme support) */
        .theme-dark input,
        .theme-dark textarea,
        .theme-dark select {
            background: var(--card) !important;
            color: var(--text) !important;
            border: 1px solid var(--border) !important;
        }
        .theme-dark input::placeholder,
        .theme-dark textarea::placeholder {
            color: rgba(255,255,255,0.55) !important;
        }
        .theme-dark .input-group .form-control,
        .theme-dark .form-control,
        .theme-dark .p-2.border {
            background: var(--card) !important;
            color: var(--text) !important;
            border-color: var(--border) !important;
        }
        .theme-dark .input-group-text {
            background: transparent !important;
            color: var(--muted) !important;
            border: 1px solid var(--border);
        }
        .theme-dark .rounded-pill.border-0 { border: 1px solid rgba(255,255,255,0.04); }

        /* Progress bar styling (track + bar) for light + dark themes */
        .progress-track { background: #e9eef2; }
        .progress-bar { background: linear-gradient(90deg,var(--primary), #FF5A00); }

        .theme-dark .progress-track { background: rgba(255,255,255,0.03) !important; }
        .theme-dark .progress-bar { background: linear-gradient(90deg,var(--primary), #FF5A00) !important; box-shadow: 0 2px 6px rgba(255,122,24,0.14); }

        /* Modal */
        .modal-content { background: var(--card); color: var(--text); border: 1px solid var(--border); }

        /* Buttons - keep primary orange */
        .btn-warning { background: linear-gradient(90deg,var(--primary), #FF5A00); border: none; }
        .btn-warning:hover { filter: brightness(0.95); }
        .theme-dark .btn-outline-primary { color: #0ea5a3; border-color: rgba(14,165,163,0.12); }
        .theme-dark .btn-outline-danger { color: #f87171; border-color: rgba(248,113,113,0.12); }

        /* Helpers */
        .text-muted { color: var(--muted) !important; }
        .shadow-sm { box-shadow: 0 6px 18px rgba(2,6,23,0.06); }
    </style>
</head>
<body class="{{ session('app.dark') ? 'theme-dark' : 'theme-light' }}">
    <div class="min-h-screen flex">
        <aside class="w-64 bg-white dark:bg-gray-800 shadow-md p-4" x-data="{open:true}">
            <div class="flex items-center gap-3 mb-6">
                <div class="w-10 h-10 rounded-xl bg-orange-200 flex items-center justify-center brand text-white font-bold">SM</div>
                <div>
                    <div class="font-bold">StudyMate</div>
                    <div class="text-xs text-gray-500">Admin Panel</div>
                </div>
            </div>

            <nav class="space-y-1">
                <a href="{{ url('/dashboard') }}" class="flex items-center gap-3 p-2 rounded-lg hover:bg-gray-100 {{ request()->is('dashboard') ? 'bg-gray-100 font-semibold' : '' }}"><i class="fa fa-house"></i> <span>Dashboard</span></a>
                <a href="{{ url('/notes') }}" class="flex items-center gap-3 p-2 rounded-lg hover:bg-gray-100 {{ request()->is('notes') ? 'bg-gray-100 font-semibold' : '' }}"><i class="fa fa-sticky-note"></i> <span>Notes</span></a>
                <a href="{{ url('/schedule') }}" class="flex items-center gap-3 p-2 rounded-lg hover:bg-gray-100 {{ request()->is('schedule') ? 'bg-gray-100 font-semibold' : '' }}"><i class="fa fa-calendar-days"></i> <span>Schedule</span></a>
                <a href="{{ url('/users') }}" class="flex items-center gap-3 p-2 rounded-lg hover:bg-gray-100 {{ request()->is('users') ? 'bg-gray-100 font-semibold' : '' }}"><i class="fa fa-users"></i> <span>Users</span></a>
                <a href="{{ url('/settings') }}" class="flex items-center gap-3 p-2 rounded-lg hover:bg-gray-100 {{ request()->is('settings') ? 'bg-gray-100 font-semibold' : '' }}"><i class="fa fa-gear"></i> <span>Settings</span></a>
                <a href="{{ url('/logout') }}" class="flex items-center gap-3 p-2 rounded-lg hover:bg-gray-100"><i class="fa fa-sign-out-alt"></i> <span>Logout</span></a>
            </nav>
        </aside>

        <section class="flex-1 p-6">
            <div class="mb-6 flex items-center justify-between">
                <h1 class="text-2xl font-semibold">@yield('title', $pageTitle ?? 'Dashboard')</h1>
                <div class="flex items-center gap-3">
                    <button @click="$dispatch('toggle-dark')" class="px-3 py-2 rounded-lg bg-white/60">Toggle</button>
                </div>
            </div>

            <div>
                @yield('content')
            </div>
        </section>
    </div>

    <script>
        window.addEventListener('toggle-dark', ()=>{
            fetch('/settings/toggle-dark', {method:'POST', headers:{'X-CSRF-TOKEN':'{{ csrf_token() }}'}}).then(()=> location.reload());
        });
    </script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
