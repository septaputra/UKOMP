@extends('layouts.app')
@section('content')
<div class="grid grid-cols-1 md:grid-cols-3 gap-4">
    <div class="p-4 bg-white rounded-xl shadow-sm">
        <div class="text-sm text-gray-500">Total Catatan</div>
        <div class="text-2xl font-bold">{{ count($notes) }}</div>
    </div>
    <div class="p-4 bg-white rounded-xl shadow-sm">
        <div class="text-sm text-gray-500">Total Jadwal</div>
        <div class="text-2xl font-bold">{{ count($schedule) }}</div>
    </div>
    <div class="p-4 bg-white rounded-xl shadow-sm">
        <div class="text-sm text-gray-500">Total User</div>
        <div class="text-2xl font-bold">{{ count($users) }}</div>
    </div>
</div>

<div class="mt-6 grid grid-cols-1 md:grid-cols-2 gap-4">
    <div class="p-4 bg-white rounded-xl shadow-sm">
        <div class="flex justify-between items-center mb-2"><div class="text-sm text-gray-500">Progress Belajar</div><div class="text-sm">{{ $progress }}%</div></div>
        <div class="w-full progress-track h-3 rounded-xl overflow-hidden">
            <div class="progress-bar h-3 rounded-xl" style="width:{{ $progress }}%"></div>
        </div>
    </div>
    <div class="p-4 bg-white rounded-xl shadow-sm">
        <div class="text-sm text-gray-500 mb-2">Aktivitas Hari Ini</div>
        <ul class="space-y-2">
            @foreach($activities as $a)
                <li class="text-sm">• {{ $a }}</li>
            @endforeach
        </ul>
    </div>
</div>

@endsection
