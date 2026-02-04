@extends('layouts.app')
@section('title','Settings')
@section('content')
<div class="grid grid-cols-1 md:grid-cols-2 gap-4">
    <div class="p-4 bg-white rounded-xl shadow-sm">
        <div class="text-sm text-gray-500">Nama Aplikasi</div>
        <div class="text-lg font-semibold">{{ $app['name'] ?? 'StudyMate' }}</div>
    </div>
    <div class="p-4 bg-white rounded-xl shadow-sm">
        <div class="text-sm text-gray-500">Versi Aplikasi</div>
        <div class="text-lg font-semibold">{{ $app['version'] ?? '0.1.0' }}</div>
    </div>
</div>

<div class="mt-6 bg-white rounded-xl shadow-sm p-4">
    <form method="POST" action="/settings/toggle-dark">
        @csrf
        <div class="flex items-center justify-between">
            <div>
                <div class="text-sm text-gray-500">Dark Mode (dummy)</div>
                <div class="font-semibold">{{ session('app.dark') ? 'Enabled' : 'Disabled' }}</div>
            </div>
            <button class="px-4 py-2 bg-orange-500 text-white rounded-lg">Toggle</button>
        </div>
    </form>

    <form method="POST" action="/settings/reset" class="mt-4">
        @csrf
        <div class="flex items-center justify-between">
            <div>
                <div class="text-sm text-gray-500">Reset Dummy Data</div>
            </div>
            <button class="px-4 py-2 bg-red-500 text-white rounded-lg">Reset</button>
        </div>
    </form>
</div>

@endsection
