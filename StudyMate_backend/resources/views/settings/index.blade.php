@extends('layouts.app')
@section('title', __('settings.title'))
@section('content')

<div class="row g-4">
    <div class="col-12 col-xl-7">
        <div class="card border-0 rounded-4 shadow-sm h-100 app-surface">
            <div class="card-body p-4">
                <div class="d-flex justify-content-between align-items-start mb-4">
                    <div>
                        <div class="text-muted small">{{ __('settings.general') }}</div>
                        <h5 class="mb-1 fw-semibold">{{ __('settings.admin_settings') }}</h5>
                        <div class="text-muted small">{{ __('settings.description') }}</div>
                    </div>
                    <span class="badge text-bg-warning">{{ __('settings.system') }}</span>
                </div>

                <div class="row g-3">
                    <div class="col-md-6">
                        <div class="p-3 rounded-4 app-surface-soft h-100">
                            <div class="text-muted small mb-1">{{ __('settings.app_name') }}</div>
                            <div class="fw-semibold">{{ $app['name'] ?? 'StudyMate' }}</div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="p-3 rounded-4 app-surface-soft h-100">
                            <div class="text-muted small mb-1">{{ __('settings.version') }}</div>
                            <div class="fw-semibold">{{ $app['version'] ?? '0.1.0' }}</div>
                        </div>
                    </div>
                    <div class="col-12">
                        <div class="p-3 rounded-4 app-surface-soft d-flex flex-column flex-md-row align-items-md-center justify-content-between gap-3">
                            <div>
                                <div class="text-muted small mb-1">{{ __('settings.theme') }}</div>
                                <div class="fw-semibold">{{ __('settings.dark_mode') }}</div>
                                <div class="text-muted small">{{ __('settings.theme_description') }}</div>
                            </div>
                            <div class="d-flex align-items-center gap-3">
                                <x-switch
                                    name="theme_mode"
                                    :checked="false"
                                    label-on="{{ app()->getLocale() === 'id' ? 'MODE TERANG' : 'LIGHT MODE' }}"
                                    label-off="{{ app()->getLocale() === 'id' ? 'MODE GELAP' : 'DARK MODE' }}"
                                    description=""
                                    class="mb-0 theme-switch-settings"
                                />
                            </div>
                        </div>
                    </div>
                    <div class="col-12">
                        <div class="p-3 rounded-4 app-surface-soft d-flex align-items-center justify-content-between gap-3">
                            <div>
                                <div class="text-muted small">{{ __('settings.theme_status') }}</div>
                                <div class="fw-semibold" data-theme-status>{{ __('settings.light_mode_enabled') }}</div>
                            </div>
                            <div class="rounded-circle d-flex align-items-center justify-content-center accent-circle" style="width:44px;height:44px;"><i class="fa fa-moon"></i></div>
                        </div>
                    </div>
                    <div class="col-12">
                        <div class="p-3 rounded-4 app-surface-soft d-flex flex-column flex-md-row align-items-md-center justify-content-between gap-3">
                            <div>
                                <div class="text-muted small mb-1">{{ __('settings.language') }}</div>
                                <div class="text-muted small">{{ __('settings.language_description') }}</div>
                            </div>
                            <x-language-switcher style="full" />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="col-12 col-xl-5">
        <div class="card border-0 rounded-4 shadow-sm h-100 app-surface">
            <div class="card-body p-4">
                <div class="text-muted small mb-1">{{ __('settings.about_system') }}</div>
                <h5 class="fw-semibold mb-3">{{ __('settings.environment') }}</h5>

                <div class="row g-3">
                    <div class="col-6">
                        <div class="p-3 rounded-4 app-surface-soft h-100">
                            <div class="text-muted small">{{ __('settings.laravel_version') }}</div>
                            <div class="fw-semibold">{{ app()->version() }}</div>
                        </div>
                    </div>
                    <div class="col-6">
                        <div class="p-3 rounded-4 app-surface-soft h-100">
                            <div class="text-muted small">{{ __('settings.php_version') }}</div>
                            <div class="fw-semibold">{{ PHP_VERSION }}</div>
                        </div>
                    </div>
                    <div class="col-6">
                        <div class="p-3 rounded-4 app-surface-soft h-100">
                            <div class="text-muted small">{{ __('settings.server_status') }}</div>
                            <div class="fw-semibold text-success">{{ __('settings.online') }}</div>
                        </div>
                    </div>
                    <div class="col-6">
                        <div class="p-3 rounded-4 app-surface-soft h-100">
                            <div class="text-muted small">{{ __('settings.storage') }}</div>
                            <div class="fw-semibold">{{ __('settings.storage_value') }}</div>
                        </div>
                    </div>
                    <div class="col-12">
                        <div class="p-3 rounded-4 app-surface-soft d-flex align-items-center justify-content-between">
                            <div>
                                <div class="text-muted small">{{ __('settings.database') }}</div>
                                <div class="fw-semibold">{{ __('settings.database_value') }}</div>
                            </div>
                            <i class="fa fa-database text-muted"></i>
                        </div>
                    </div>
                </div>

                <form method="POST" action="/settings/reset" class="mt-4">
                    @csrf
                    <button class="btn btn-outline-danger w-100 rounded-3">{{ __('settings.reset_dummy') }}</button>
                </form>
            </div>
        </div>
    </div>
</div>

@endsection
