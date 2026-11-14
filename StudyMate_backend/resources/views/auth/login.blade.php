@extends('layouts.auth')

@section('content')
<div x-data="{ show: false }" class="auth-card app-surface shadow-sm">
    <div class="auth-brand mb-4">
        <div class="auth-logo">SM</div>
        <div>
            <h2 class="auth-title">{{ __('auth.title') }}</h2>
            <p class="auth-subtitle">{{ __('auth.subtitle') }}</p>
        </div>
    </div>

    @if($errors->any())
        <div class="auth-error">{{ $errors->first() }}</div>
    @endif
    @if(session('error'))
        <div class="auth-error">{{ session('error') }}</div>
    @endif

    <form method="POST" action="/login" class="auth-form">
        @csrf

        <div class="auth-field">
            <label class="auth-label">{{ __('auth.email') }}</label>
            <span class="auth-icon"><i class="fa fa-envelope"></i></span>
            <input type="email" name="email" required class="auth-input" placeholder="{{ __('auth.email_placeholder') }}" value="{{ old('email') }}">
        </div>

        <div class="auth-field">
            <label class="auth-label">{{ __('auth.password') }}</label>
            <span class="auth-icon"><i class="fa fa-lock"></i></span>
            <input :type="show ? 'text' : 'password'" name="password" required class="auth-input auth-input-password" placeholder="{{ __('auth.password_placeholder') }}">
            <button type="button" @click="show = !show" :aria-label="show ? @js(__('auth.hide_password')) : @js(__('auth.show_password'))" class="auth-visibility">
                <template x-if="!show"><i class="fa fa-eye"></i></template>
                <template x-if="show"><i class="fa fa-eye-slash"></i></template>
            </button>
        </div>

        <div>
            <button type="submit" class="auth-button btn btn-warning shadow-md">{{ __('auth.login') }}</button>
        </div>
    </form>

    <div class="auth-footer">
        {{ __('auth.footer') }}
    </div>
</div>

@endsection
