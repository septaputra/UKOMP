<?php

use App\Http\Controllers\AdminController;
use App\Http\Controllers\LanguageController;
use Illuminate\Support\Facades\Route;

Route::post('/language', [LanguageController::class, 'update'])->name('language.update');

Route::get('/', function () {
    if (session()->get('dummy_admin_authenticated') || session()->get('is_logged')) {
        return redirect('/dashboard');
    }

    return view('auth.login');
})->name('home');

Route::middleware('guest')->group(function () {
    Route::get('/login', [AdminController::class, 'loginForm'])->name('login.form');
    Route::post('/login', [AdminController::class, 'login'])->name('login.submit');
});

Route::middleware('admin.auth')->group(function () {
    Route::get('/logout', [AdminController::class, 'logout'])->name('logout');
    Route::get('/dashboard', [AdminController::class, 'dashboard'])->name('dashboard');

    Route::get('/banners', [AdminController::class, 'banners'])->name('banners');
    Route::post('/banners/add', [AdminController::class, 'bannersAdd'])->name('banners.add');
    Route::post('/banners/edit', [AdminController::class, 'bannersEdit'])->name('banners.edit');
    Route::post('/banners/delete', [AdminController::class, 'bannersDelete'])->name('banners.delete');

    Route::get('/berita', [AdminController::class, 'berita'])->name('berita');
    Route::post('/berita/add', [AdminController::class, 'beritaAdd'])->name('berita.add');
    Route::post('/berita/edit', [AdminController::class, 'beritaEdit'])->name('berita.edit');
    Route::post('/berita/delete', [AdminController::class, 'beritaDelete'])->name('berita.delete');

    Route::get('/videos', [AdminController::class, 'videos'])->name('videos');
    Route::post('/videos/add', [AdminController::class, 'videosAdd'])->name('videos.add');
    Route::post('/videos/edit', [AdminController::class, 'videosEdit'])->name('videos.edit');
    Route::post('/videos/delete', [AdminController::class, 'videosDelete'])->name('videos.delete');

    Route::get('/bimbel', [AdminController::class, 'bimbel'])->name('bimbel');
    Route::post('/bimbel/add', [AdminController::class, 'bimbelAdd'])->name('bimbel.add');
    Route::post('/bimbel/edit', [AdminController::class, 'bimbelEdit'])->name('bimbel.edit');
    Route::post('/bimbel/delete', [AdminController::class, 'bimbelDelete'])->name('bimbel.delete');
    Route::get('/bimbel/regions/{provinceCode}', [AdminController::class, 'bimbelRegencies'])
        ->where('provinceCode', '[0-9]{2}')
        ->name('bimbel.regencies');

    Route::get('/users', [AdminController::class, 'users'])->name('users');

    Route::get('/settings', [AdminController::class, 'settings'])->name('settings');
    Route::post('/settings/toggle-dark', [AdminController::class, 'toggleDark'])->name('settings.toggleDark');
    Route::post('/settings/reset', [AdminController::class, 'resetData'])->name('settings.reset');
});
