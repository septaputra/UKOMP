@extends('layouts.auth')

@section('content')
<div x-data="{show:false}" class="min-h-[70vh] flex items-center justify-center">
    <div class="w-full max-w-md px-6 py-8 bg-white/95 dark:bg-gray-800 rounded-2xl shadow-xl transform transition hover:-translate-y-1">
        <div class="flex items-center gap-3 mb-4">
            <div class="w-12 h-12 rounded-xl bg-gradient-to-br from-orange-300 to-orange-600 flex items-center justify-center text-white text-lg font-bold">SM</div>
            <div>
                <h2 class="text-2xl font-semibold">Admin Login</h2>
                <p class="text-sm text-gray-500">Masuk ke panel manajemen</p>
            </div>
        </div>

        @if($errors->any())
            <div class="text-red-600 text-sm mb-3">{{ $errors->first() }}</div>
        @endif

        <form method="POST" action="/login" class="space-y-4">
            @csrf

            <div class="relative">
                <label class="block text-sm text-gray-600 mb-1">Email</label>
                <span class="absolute left-3 top-10 text-gray-400"><i class="fa fa-envelope"></i></span>
                <input type="email" name="email" required class="pl-10 pr-3 py-3 w-full rounded-xl border border-gray-200 focus:ring-2 focus:ring-orange-200 focus:outline-none" placeholder="you@example.com">
            </div>

            <div class="relative">
                <label class="block text-sm text-gray-600 mb-1">Password</label>
                <span class="absolute left-3 top-10 text-gray-400"><i class="fa fa-lock"></i></span>
                <input :type="show ? 'text' : 'password'" name="password" required class="pl-10 pr-10 py-3 w-full rounded-xl border border-gray-200 focus:ring-2 focus:ring-orange-200 focus:outline-none" placeholder="••••••••">
                <button type="button" @click="show = !show" class="absolute right-3 top-9 text-gray-500">
                    <template x-if="!show"><i class="fa fa-eye"></i></template>
                    <template x-if="show"><i class="fa fa-eye-slash"></i></template>
                </button>
            </div>

            <div>
                <button type="submit" class="w-full py-3 rounded-xl text-white font-semibold bg-gradient-to-r from-[#FF7A18] to-[#FF5A00] shadow-md hover:shadow-lg transform hover:-translate-y-0.5 transition">Login</button>
            </div>
        </form>

        <div class="mt-6 text-center text-sm text-gray-400">
            © 2026 StudyMate Admin
        </div>
    </div>
</div>

<style>
    /* Background soft gradient for auth layout wrapper */
    body { background: linear-gradient(180deg,#f3f6fb 0%, #eef2f7 50%, #ffffff 100%); }
    @media (prefers-color-scheme: dark) {
        body { background: linear-gradient(180deg,#0f1724 0%, #0b1220 100%); }
    }
</style>

@endsection
