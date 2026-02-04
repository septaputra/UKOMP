<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\AdminController;

Route::get('/', function () {
    return redirect('/login');
});

Route::get('/login', [AdminController::class, 'loginForm'])->name('login.form');
Route::post('/login', [AdminController::class, 'login'])->name('login.submit');
Route::get('/logout', [AdminController::class, 'logout'])->name('logout');

Route::get('/dashboard', [AdminController::class, 'dashboard'])->name('dashboard');

Route::get('/notes', [AdminController::class, 'notes'])->name('notes');
Route::post('/notes/add', [AdminController::class, 'notesAdd'])->name('notes.add');
Route::post('/notes/edit', [AdminController::class, 'notesEdit'])->name('notes.edit');
Route::post('/notes/delete', [AdminController::class, 'notesDelete'])->name('notes.delete');

Route::get('/schedule', [AdminController::class, 'schedule'])->name('schedule');
Route::post('/schedule/add', [AdminController::class, 'scheduleAdd'])->name('schedule.add');
Route::post('/schedule/edit', [AdminController::class, 'scheduleEdit'])->name('schedule.edit');
Route::post('/schedule/delete', [AdminController::class, 'scheduleDelete'])->name('schedule.delete');

Route::get('/users', [AdminController::class, 'users'])->name('users');
Route::post('/users/add', [AdminController::class, 'usersAdd'])->name('users.add');
Route::post('/users/edit', [AdminController::class, 'usersEdit'])->name('users.edit');
Route::post('/users/delete', [AdminController::class, 'usersDelete'])->name('users.delete');

Route::get('/settings', [AdminController::class, 'settings'])->name('settings');
Route::post('/settings/toggle-dark', [AdminController::class, 'toggleDark'])->name('settings.toggleDark');
Route::post('/settings/reset', [AdminController::class, 'resetData'])->name('settings.reset');
