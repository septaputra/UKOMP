<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Carbon\Carbon;

class AdminController extends Controller
{
    protected function seedIfNeeded(Request $request)
    {
        if (!$request->session()->has('notes')) {
            $notes = [
                ['id'=>1,'judul'=>'Belajar Algebra','isi'=>'Ringkasan konsep dasar algebra','tanggal'=>'2026-01-10'],
                ['id'=>2,'judul'=>'Catatan Sejarah','isi'=>'Perang dunia ringkasan','tanggal'=>'2026-01-20'],
                ['id'=>3,'judul'=>'Fisika - Gerak','isi'=>'Hukum Newton dan contoh soal','tanggal'=>'2026-01-25'],
            ];
            $request->session()->put('notes', $notes);
        }

        if (!$request->session()->has('schedule')) {
            $schedule = [
                ['id'=>1,'mata_pelajaran'=>'Matematika','hari'=>'Senin','tanggal'=>null,'jam'=>'08:00 - 09:30','catatan'=>'Bab 1-2'],
                ['id'=>2,'mata_pelajaran'=>'Fisika','hari'=>'Selasa','tanggal'=>null,'jam'=>'10:00 - 11:30','catatan'=>'Praktikum'],
                ['id'=>3,'mata_pelajaran'=>'Bahasa Inggris','hari'=>'Rabu','tanggal'=>null,'jam'=>'13:00 - 14:30','catatan'=>'Reading'],
                ['id'=>4,'mata_pelajaran'=>'kimia','hari'=>Carbon::parse('2026-02-24')->locale('id')->isoFormat('dddd'),'tanggal'=>'2026-02-24','jam'=>'09:10','catatan'=>'belajar perubahan suhu'],
            ];
            $request->session()->put('schedule', $schedule);
        }

        if (!$request->session()->has('users')) {
            $users = [
                ['id'=>1,'nama'=>'Admin','email'=>'admin@example.com','role'=>'admin'],
                ['id'=>2,'nama'=>'Siswa A','email'=>'siswa.a@example.com','role'=>'student'],
                ['id'=>3,'nama'=>'Guru B','email'=>'guru.b@example.com','role'=>'teacher'],
            ];
            $request->session()->put('users', $users);
        }

        if (!$request->session()->has('app')) {
            $request->session()->put('app', ['name'=>'StudyMate','version'=>'0.1.0','dark'=>false]);
        }
    }

    public function loginForm()
    {
        return view('auth.login');
    }

    public function login(Request $request)
    {
        $request->validate([
            'email'=>'required',
            'password'=>'required',
        ]);

        // dummy login
        $request->session()->put('is_logged', true);
        $this->seedIfNeeded($request);
        return redirect('/dashboard');
    }

    public function logout(Request $request)
    {
        $request->session()->flush();
        return redirect('/login');
    }

    public function dashboard(Request $request)
    {
        $this->seedIfNeeded($request);
        $notes = $request->session()->get('notes', []);
        $schedule = $request->session()->get('schedule', []);
        $users = $request->session()->get('users', []);

        $progress = 65; // dummy %
        $activities = [
            'Menambahkan catatan baru',
            'Mengedit jadwal Matematika',
            'User baru terdaftar (dummy)'
        ];

        return view('dashboard.index', compact('notes','schedule','users','progress','activities'));
    }

    // Notes
    public function notes(Request $request)
    {
        $this->seedIfNeeded($request);
        $notes = $request->session()->get('notes', []);
        return view('notes.index', compact('notes'));
    }

    public function notesAdd(Request $request)
    {
        $this->seedIfNeeded($request);
        $data = $request->validate(['judul'=>'required','isi'=>'required','tanggal'=>'required']);
        $notes = $request->session()->get('notes', []);
        $next = collect($notes)->max('id') + 1;
        if (!$next) $next = 1;
        $data['id'] = $next;
        $notes[] = $data;
        $request->session()->put('notes', $notes);
        return redirect()->back();
    }

    public function notesEdit(Request $request)
    {
        $this->seedIfNeeded($request);
        $data = $request->validate(['id'=>'required','judul'=>'required','isi'=>'required','tanggal'=>'required']);
        $notes = $request->session()->get('notes', []);
        foreach ($notes as &$n) {
            if ($n['id'] == $data['id']) {
                $n['judul'] = $data['judul'];
                $n['isi'] = $data['isi'];
                $n['tanggal'] = $data['tanggal'];
                break;
            }
        }
        $request->session()->put('notes', $notes);
        return redirect()->back();
    }

    public function notesDelete(Request $request)
    {
        $this->seedIfNeeded($request);
        $id = $request->input('id');
        $notes = collect($request->session()->get('notes', []))->reject(fn($n)=>$n['id']==$id)->values()->all();
        $request->session()->put('notes', $notes);
        return redirect()->back();
    }

    // Schedule
    public function schedule(Request $request)
    {
        $this->seedIfNeeded($request);
        $schedule = $request->session()->get('schedule', []);
        return view('schedule.index', compact('schedule'));
    }

    public function scheduleAdd(Request $request)
    {
        $this->seedIfNeeded($request);
        $data = $request->validate(['mata_pelajaran'=>'required','tanggal'=>'nullable|date','hari_select'=>'nullable','jam'=>'required','catatan'=>'nullable']);
        $schedule = $request->session()->get('schedule', []);
        $next = collect($schedule)->max('id') + 1;
        if (!$next) $next = 1;

        // determine hari (weekday name) and tanggal
        if (!empty($data['tanggal'])) {
            $hariName = Carbon::parse($data['tanggal'])->locale('id')->isoFormat('dddd');
            $tanggal = $data['tanggal'];
        } else {
            $hariName = $data['hari_select'] ?? null;
            $tanggal = null;
        }

        $entry = [
            'id'=>$next,
            'mata_pelajaran'=>$data['mata_pelajaran'],
            'hari'=>$hariName,
            'tanggal'=>$tanggal,
            'jam'=>$data['jam'],
            'catatan'=>$data['catatan'] ?? null,
        ];

        $schedule[] = $entry;
        $request->session()->put('schedule', $schedule);
        return redirect()->back();
    }

    public function scheduleEdit(Request $request)
    {
        $this->seedIfNeeded($request);
        $data = $request->validate(['id'=>'required','mata_pelajaran'=>'required','tanggal'=>'nullable|date','hari_select'=>'nullable','jam'=>'required','catatan'=>'nullable']);
        $schedule = $request->session()->get('schedule', []);
        foreach ($schedule as &$s) {
            if ($s['id'] == $data['id']) {
                if (!empty($data['tanggal'])) {
                    $hariName = Carbon::parse($data['tanggal'])->locale('id')->isoFormat('dddd');
                    $tanggal = $data['tanggal'];
                } else {
                    $hariName = $data['hari_select'] ?? null;
                    $tanggal = null;
                }

                $s['mata_pelajaran'] = $data['mata_pelajaran'];
                $s['hari'] = $hariName;
                $s['tanggal'] = $tanggal;
                $s['jam'] = $data['jam'];
                $s['catatan'] = $data['catatan'] ?? null;
                break;
            }
        }
        $request->session()->put('schedule', $schedule);
        return redirect()->back();
    }

    public function scheduleDelete(Request $request)
    {
        $id = $request->input('id');
        $schedule = collect($request->session()->get('schedule', []))->reject(fn($s)=>$s['id']==$id)->values()->all();
        $request->session()->put('schedule', $schedule);
        return redirect()->back();
    }

    // Users
    public function users(Request $request)
    {
        $this->seedIfNeeded($request);
        $users = $request->session()->get('users', []);
        return view('users.index', compact('users'));
    }

    public function usersAdd(Request $request)
    {
        $data = $request->validate(['nama'=>'required','email'=>'required|email','role'=>'required']);
        $users = $request->session()->get('users', []);
        $next = collect($users)->max('id') + 1;
        if (!$next) $next = 1;
        $data['id'] = $next;
        $users[] = $data;
        $request->session()->put('users', $users);
        return redirect()->back();
    }

    public function usersEdit(Request $request)
    {
        $data = $request->validate(['id'=>'required','nama'=>'required','email'=>'required|email','role'=>'required']);
        $users = $request->session()->get('users', []);
        foreach ($users as &$u) {
            if ($u['id'] == $data['id']) {
                $u = array_merge($u, $data);
                break;
            }
        }
        $request->session()->put('users', $users);
        return redirect()->back();
    }

    public function usersDelete(Request $request)
    {
        $id = $request->input('id');
        $users = collect($request->session()->get('users', []))->reject(fn($u)=>$u['id']==$id)->values()->all();
        $request->session()->put('users', $users);
        return redirect()->back();
    }

    // Settings
    public function settings(Request $request)
    {
        $this->seedIfNeeded($request);
        $app = $request->session()->get('app', []);
        return view('settings.index', compact('app'));
    }

    public function toggleDark(Request $request)
    {
        $app = $request->session()->get('app', ['dark'=>false]);
        $app['dark'] = !$app['dark'];
        $request->session()->put('app', $app);
        return redirect()->back();
    }

    public function resetData(Request $request)
    {
        $request->session()->forget(['notes','schedule','users','app']);
        $this->seedIfNeeded($request);
        return redirect()->back();
    }
}
