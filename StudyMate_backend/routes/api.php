<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. This file uses only closures
| and `response()->json()` so no database or Eloquent models are required.
|
*/

// GET /api/schedules
// This endpoint returns an array of schedule items as JSON.
// Requirements satisfied: no DB, no auth, accessible at /api/schedules
Route::get('/schedules', function (Request $request) {
    // Static (dummy) data for schedules. Keep this simple and explicit
    // so the Android app can parse the JSON without any DB dependency.
    $schedules = [
        [
            'subject' => 'Matematika',
            'day' => 'Senin',
            'time' => '08:00 - 09:30',
            'note' => 'Bab 1-2',
        ],
        // You can add more static entries here for testing purposes.
    ];

    // Return a JSON response with the schedules array and 200 HTTP status.
    return response()->json($schedules, 200);
});

// GET /api/notes
// This endpoint returns an array of note items as JSON.
// It uses static dummy data (no database, no models) so the Android
// app can fetch notes without any backend persistence setup.
Route::get('/notes', function (Request $request) {
    // Static (dummy) notes array. Fields must match the required JSON
    // structure exactly so the Android `Note` data class can map fields.
    $notes = [
        [
            'id' => 1,
            'title' => 'Rumus Trigonometri',
            'content' => 'Sin, Cos, Tan dasar',
            'subject' => 'Matematika',
            'created_at' => '2026-02-01',
        ],
        [
            'id' => 2,
            'title' => 'Hukum Newton',
            'content' => 'Hukum I, II, III',
            'subject' => 'Fisika',
            'created_at' => '2026-02-02',
        ],
    ];

    // Return the notes array as JSON with HTTP 200 status.
    return response()->json($notes, 200);
});
