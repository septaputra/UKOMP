<!doctype html>
<html lang="{{ app()->getLocale() }}" data-theme="light">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>{{ config('app.name','StudyMate') }} - Admin</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <script>
        (function () {
            const theme = localStorage.getItem('studymate-theme') || 'light';
            document.documentElement.setAttribute('data-theme', theme);
            document.addEventListener('DOMContentLoaded', function () {
                document.body && document.body.setAttribute('data-theme', theme);
            });
        })();
    </script>
    @vite(['resources/css/app.css', 'resources/js/app.js'])
    <script defer src="https://unpkg.com/alpinejs@3.x.x/dist/cdn.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.3/dist/chart.umd.min.js"></script>
</head>
<body
    x-data="{ sidebarOpen: false }"
    :class="sidebarOpen ? 'sidebar-open' : ''"
    data-theme-status-dark="{{ __('settings.dark_mode_enabled') }}"
    data-theme-status-light="{{ __('settings.light_mode_enabled') }}"
    data-switch-status-enabled="{{ __('settings.enabled') }}"
    data-switch-status-disabled="{{ __('settings.disabled') }}"
>
    <div class="app-shell">
        <aside class="app-sidebar shadow-sm p-4">
            <div class="flex items-center gap-3 mb-6">
                <div class="w-10 h-10 rounded-xl brand-badge flex items-center justify-center brand text-white font-bold">SM</div>
                <div>
                    <div class="font-bold">StudyMate</div>
                    <div class="text-xs text-gray-500">{{ __('navigation.admin_panel') }}</div>
                </div>
            </div>

            <nav class="space-y-1">
                <a href="{{ url('/dashboard') }}" class="nav-item-link {{ request()->is('dashboard') ? 'active' : '' }}"><i class="fa fa-house"></i> <span>{{ __('navigation.dashboard') }}</span></a>
                <a href="{{ url('/users') }}" class="nav-item-link {{ request()->is('users') ? 'active' : '' }}"><i class="fa fa-users"></i> <span>{{ __('navigation.users') }}</span></a>
                <a href="{{ url('/banners') }}" class="nav-item-link {{ request()->is('banners') ? 'active' : '' }}"><i class="fa fa-image"></i> <span>{{ __('navigation.banners') }}</span></a>
                <a href="{{ url('/berita') }}" class="nav-item-link {{ request()->is('berita') ? 'active' : '' }}"><i class="fa fa-newspaper"></i> <span>{{ __('navigation.news') }}</span></a>
                <a href="{{ url('/videos') }}" class="nav-item-link {{ request()->is('videos') ? 'active' : '' }}"><i class="fa fa-circle-play"></i> <span>{{ __('navigation.videos') }}</span></a>
                <a href="{{ url('/bimbel') }}" class="nav-item-link {{ request()->is('bimbel') ? 'active' : '' }}"><i class="fa fa-location-dot"></i> <span>{{ __('navigation.tutoring') }}</span></a>
                <a href="{{ url('/settings') }}" class="nav-item-link {{ request()->is('settings') ? 'active' : '' }}"><i class="fa fa-gear"></i> <span>{{ __('navigation.settings') }}</span></a>
                <a href="{{ url('/logout') }}" class="nav-item-link"><i class="fa fa-sign-out-alt"></i> <span>{{ __('common.logout') }}</span></a>
            </nav>
        </aside>

        <div class="app-main">
            <header class="app-header px-3 px-md-4 py-3">
                <div class="d-flex align-items-center justify-content-between gap-3">
                    <div class="d-flex align-items-center gap-3">
                        <button class="btn btn-outline-secondary d-lg-none" @click="sidebarOpen = !sidebarOpen"><i class="fa fa-bars"></i></button>
                        <h1 class="text-2xl font-semibold mb-0">@yield('title', $pageTitle ?? __('dashboard.title'))</h1>
                    </div>
                    <div class="d-flex align-items-center gap-2 header-theme-control">
                        <span class="text-muted small d-none d-sm-inline">{{ __('navigation.theme') }}</span>
                        <label class="theme-switch-pill theme-switch-pill-header">
                            <input id="theme-toggle-header" type="checkbox" class="theme-switch-pill-input" data-theme-toggle>
                            <span class="theme-switch-pill-track">
                                <span class="theme-switch-pill-option theme-switch-light">
                                    <i class="fa fa-sun"></i>
                                    <span>{{ __('navigation.light') }}</span>
                                </span>
                                <span class="theme-switch-pill-option theme-switch-dark">
                                    <i class="fa fa-moon"></i>
                                    <span>{{ __('navigation.dark') }}</span>
                                </span>
                            </span>
                        </label>
                    </div>
                </div>
            </header>

            <main class="p-3 p-md-4">
                @yield('content')
            </main>
        </div>

        <div x-show="sidebarOpen" x-cloak class="sidebar-overlay d-lg-none" @click="sidebarOpen = false"></div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
