<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;

class AdminController extends Controller
{
    protected function seedIfNeeded(Request $request)
    {
        if (!$request->session()->has('banners')) {
            $request->session()->put('banners', [
                ['id'=>1,'judul'=>'Promo Back to School','status'=>'aktif','urutan'=>1,'posisi'=>'Home Hero','image'=>'https://picsum.photos/seed/banner1/800/400'],
                ['id'=>2,'judul'=>'Kelas Intensif UTBK','status'=>'aktif','urutan'=>2,'posisi'=>'Sidebar','image'=>'https://picsum.photos/seed/banner2/800/400'],
                ['id'=>3,'judul'=>'Try Out Mingguan','status'=>'draft','urutan'=>3,'posisi'=>'Popup','image'=>'https://picsum.photos/seed/banner3/800/400'],
            ]);
        }

        if (!$request->session()->has('berita')) {
            $request->session()->put('berita', [
                ['id'=>1,'judul'=>'Strategi Belajar Efektif','thumbnail'=>'https://picsum.photos/seed/news1/480/320','kategori'=>'Tips Belajar','ringkasan'=>'Cara mengatur waktu belajar agar lebih fokus.','link'=>'https://example.com/berita/1','status'=>'publish'],
                ['id'=>2,'judul'=>'Info Kurikulum Baru','thumbnail'=>'https://picsum.photos/seed/news2/480/320','kategori'=>'Kurikulum','ringkasan'=>'Ringkasan perubahan kurikulum terbaru.','link'=>'https://example.com/berita/2','status'=>'draft'],
            ]);
        }

        if (!$request->session()->has('videos')) {
            $request->session()->put('videos', [
                ['id'=>1,'judul'=>'Matematika Dasar','jenjang'=>'SMP','mapel'=>'Matematika','thumbnail'=>'https://img.youtube.com/vi/dQw4w9WgXcQ/hqdefault.jpg','youtube'=>'https://youtube.com/watch?v=dQw4w9WgXcQ'],
                ['id'=>2,'judul'=>'Fisika Gerak Lurus','jenjang'=>'SMA','mapel'=>'Fisika','thumbnail'=>'https://img.youtube.com/vi/dQw4w9WgXcQ/hqdefault.jpg','youtube'=>'https://youtube.com/watch?v=dQw4w9WgXcQ'],
            ]);
        }

        if (!$request->session()->has('bimbels')) {
            $request->session()->put('bimbels', [
                ['id'=>1,'nama'=>'Bimbel Cerdas','logo'=>'https://picsum.photos/seed/logo1/120/120','kota'=>'Bandung','provinsi'=>'Jawa Barat','jenjang'=>'SMA','maps'=>'https://maps.google.com','website'=>'https://example.com','alamat'=>'Jl. Cihampelas No. 18 Bandung','deskripsi'=>'Bimbel persiapan masuk sekolah dan ujian nasional dengan tutor berpengalaman.','kontak'=>'0812-3456-7890'],
                ['id'=>2,'nama'=>'Prestasi Center','logo'=>'https://picsum.photos/seed/logo2/120/120','kota'=>'Surabaya','provinsi'=>'Jawa Timur','jenjang'=>'SD-SMA','maps'=>'https://maps.google.com','website'=>'https://example.com','alamat'=>'Jl. Darmo No. 7 Surabaya','deskripsi'=>'Program belajar intensif untuk SD, SMP, dan SMA dengan fokus akademik.','kontak'=>'0813-9876-5432'],
            ]);
        }

        if (!$request->session()->has('users')) {
            $users = [
                ['id'=>1,'nama'=>'Admin','email'=>'admin@example.com','role'=>'admin','foto'=>'https://i.pravatar.cc/120?img=12','status'=>'aktif','provider'=>'email','joined'=>'2025-12-10'],
                ['id'=>2,'nama'=>'Siswa A','email'=>'siswa.a@example.com','role'=>'student','foto'=>'https://i.pravatar.cc/120?img=32','status'=>'aktif','provider'=>'google','joined'=>'2026-01-03'],
                ['id'=>3,'nama'=>'Guru B','email'=>'guru.b@example.com','role'=>'teacher','foto'=>'https://i.pravatar.cc/120?img=45','status'=>'aktif','provider'=>'email','joined'=>'2026-01-20'],
            ];
            $request->session()->put('users', $users);
        }

        $users = $request->session()->get('users', []);
        foreach ($users as &$user) {
            $user = array_intersect_key($user, array_flip([
                'id',
                'nama',
                'email',
                'role',
                'foto',
                'status',
                'provider',
                'joined',
            ]));

            if (($user['status'] ?? null) !== 'aktif') {
                $user['status'] = 'aktif';
            }
        }
        unset($user);
        $request->session()->put('users', $users);

        if (!$request->session()->has('app')) {
            $request->session()->put('app', ['name'=>'StudyMate','version'=>'0.1.0','dark'=>false]);
        }
    }

    public function loginForm(Request $request)
    {
        $isLogged = $request->session()->get('dummy_admin_authenticated') || $request->session()->get('is_logged');

        if ($isLogged) {
            return redirect('/dashboard');
        }

        return view('auth.login');
    }

    public function login(Request $request)
    {
        $request->validate([
            'email' => 'required|email',
            'password' => 'required',
        ], [
            'email.required' => __('auth.email_required'),
            'email.email' => __('auth.email_invalid'),
            'password.required' => __('auth.password_required'),
        ]);

        $email = trim((string) $request->input('email'));
        $password = trim((string) $request->input('password'));

        if ($email === '' || $password === '') {
            return redirect('/login')->withErrors([
                'email' => __('auth.credentials_invalid'),
            ]);
        }

        $locale = $request->session()->get('locale', 'id');
        $request->session()->invalidate();
        $request->session()->regenerateToken();
        $request->session()->put('locale', $locale);
        $request->session()->put('dummy_admin_authenticated', true);
        $request->session()->put('dummy_admin_email', $email);
        $request->session()->put('admin_name', 'Admin');

        $this->seedIfNeeded($request);

        return redirect('/dashboard');
    }

    public function logout(Request $request)
    {
        $locale = $request->session()->get('locale', 'id');
        $request->session()->forget([
            'dummy_admin_authenticated',
            'dummy_admin_email',
            'admin_name',
            'is_logged',
            'admin_name',
            'admin_email',
        ]);
        $request->session()->invalidate();
        $request->session()->regenerateToken();
        $request->session()->put('locale', $locale);

        return redirect('/login');
    }

    public function dashboard(Request $request)
    {
        $this->seedIfNeeded($request);
        $users = $request->session()->get('users', []);
        $banners = $request->session()->get('banners', []);
        $berita = $request->session()->get('berita', []);
        $videos = $request->session()->get('videos', []);
        $bimbels = $request->session()->get('bimbels', []);

        $activities = [
            __('dashboard.activities.login'),
            __('dashboard.activities.banner_added'),
            __('dashboard.activities.news_edited'),
            __('dashboard.activities.video_deleted'),
            __('dashboard.activities.tutoring_added'),
            __('dashboard.activities.logout'),
        ];

        $stats = [
            'total_user' => count($users),
            'user_aktif' => collect($users)->where('status','aktif')->count(),
            'banner' => count($banners),
            'berita' => count($berita),
            'video' => count($videos),
            'tempat_bimbel' => count($bimbels),
        ];

        $chartSeries = [72, 58, 66, 84, 91, 63, 77];

        return view('dashboard.index', compact('users','activities','stats','chartSeries'));
    }

    // Users
    public function users(Request $request)
    {
        $this->seedIfNeeded($request);
        $users = collect($request->session()->get('users', []))
            ->map(function (array $user) {
                $user['joined_formatted'] = \Illuminate\Support\Carbon::parse($user['joined'])
                    ->locale(app()->getLocale())
                    ->translatedFormat(app()->getLocale() === 'id' ? 'd F Y' : 'F j, Y');

                return $user;
            })
            ->all();

        return view('users.index', compact('users'));
    }

    public function bannersAdd(Request $request)
    {
        $data = $request->validate([
            'judul' => 'required|string|max:255',
            'image' => 'required|string',
            'posisi' => 'required|string|max:100',
            'status' => 'required|in:aktif,draft',
            'urutan' => 'required|integer|min:1',
        ], [
            'judul.required' => __('validation.required', ['attribute' => __('banners.banner_title')]),
            'image.required' => __('validation.required', ['attribute' => __('banners.image')]),
            'posisi.required' => __('validation.required', ['attribute' => __('banners.position')]),
            'status.required' => __('validation.required', ['attribute' => __('common.status')]),
            'urutan.required' => __('validation.required', ['attribute' => __('banners.order')]),
        ]);

        $items = $request->session()->get('banners', []);
        $data['id'] = (collect($items)->max('id') ?? 0) + 1;
        $data['image'] = !empty($data['image']) ? $data['image'] : 'https://picsum.photos/seed/banner-' . $data['id'] . '/800/400';
        $items[] = $data;
        $request->session()->put('banners', $items);

        return redirect()->back()->with('success', __('banners.created'));
    }

    public function bannersEdit(Request $request)
    {
        $data = $request->validate([
            'id' => 'required|integer',
            'judul' => 'required|string|max:255',
            'image' => 'required|string',
            'posisi' => 'required|string|max:100',
            'status' => 'required|in:aktif,draft',
            'urutan' => 'required|integer|min:1',
        ]);

        $items = $request->session()->get('banners', []);
        foreach ($items as $index => $item) {
            if ($item['id'] == $data['id']) {
                $items[$index] = array_merge($item, [
                    'judul' => $data['judul'],
                    'image' => !empty($data['image']) ? $data['image'] : $item['image'],
                    'posisi' => $data['posisi'],
                    'status' => $data['status'],
                    'urutan' => $data['urutan'],
                ]);
                break;
            }
        }

        $request->session()->put('banners', $items);
        return redirect()->back()->with('success', __('banners.updated'));
    }

    public function bannersDelete(Request $request)
    {
        $id = $request->input('id');
        $items = collect($request->session()->get('banners', []))->reject(fn($item) => $item['id'] == $id)->values()->all();
        $request->session()->put('banners', $items);
        return redirect()->back()->with('success', __('banners.deleted'));
    }

    public function beritaAdd(Request $request)
    {
        $data = $request->validate([
            'judul' => 'required|string|max:255',
            'thumbnail' => 'required|url|max:2048',
            'status' => 'required|in:publish,draft',
            'link' => 'required|url|max:2048',
        ], [
            'judul.required' => __('validation.required', ['attribute' => __('news.headline')]),
            'status.required' => __('validation.required', ['attribute' => __('news.status')]),
        ]);

        $items = $request->session()->get('berita', []);
        $data['id'] = (collect($items)->max('id') ?? 0) + 1;
        $items[] = $data;
        $request->session()->put('berita', $items);

        return redirect()->back()->with('success', __('news.created'));
    }

    public function beritaEdit(Request $request)
    {
        $data = $request->validate([
            'id' => 'required|integer',
            'judul' => 'required|string|max:255',
            'thumbnail' => 'required|url|max:2048',
            'status' => 'required|in:publish,draft',
            'link' => 'required|url|max:2048',
        ]);

        $items = $request->session()->get('berita', []);
        foreach ($items as $index => $item) {
            if ($item['id'] == $data['id']) {
                $items[$index] = array_merge($item, [
                    'judul' => $data['judul'],
                    'thumbnail' => $data['thumbnail'],
                    'status' => $data['status'],
                    'link' => $data['link'],
                ]);
                break;
            }
        }

        $request->session()->put('berita', $items);
        return redirect()->back()->with('success', __('news.updated'));
    }

    public function beritaDelete(Request $request)
    {
        $id = $request->input('id');
        $items = collect($request->session()->get('berita', []))->reject(fn($item) => $item['id'] == $id)->values()->all();
        $request->session()->put('berita', $items);
        return redirect()->back()->with('success', __('news.deleted'));
    }

    public function videosAdd(Request $request)
    {
        $data = $request->validate([
            'judul' => 'required|string|max:255',
            'jenjang' => 'required|in:SD,SMP,SMA,SMK,Kuliah',
            'mapel' => 'required|string|max:100',
            'youtube' => 'required|url|max:2048',
        ], [
            'judul.required' => __('validation.required', ['attribute' => __('videos.headline')]),
            'jenjang.required' => __('validation.required', ['attribute' => __('videos.level')]),
            'mapel.required' => __('validation.required', ['attribute' => __('videos.subject')]),
        ]);

        $videoId = $this->youtubeVideoId($data['youtube']);
        if ($videoId === null) {
            return back()->withErrors(['youtube' => __('videos.url_error')])->withInput();
        }

        $items = $request->session()->get('videos', []);
        $data['id'] = (collect($items)->max('id') ?? 0) + 1;
        $data['thumbnail'] = 'https://img.youtube.com/vi/' . $videoId . '/hqdefault.jpg';
        $items[] = $data;
        $request->session()->put('videos', $items);

        return redirect()->back()->with('success', __('videos.created'));
    }

    public function videosEdit(Request $request)
    {
        $data = $request->validate([
            'id' => 'required|integer',
            'judul' => 'required|string|max:255',
            'jenjang' => 'required|in:SD,SMP,SMA,SMK,Kuliah',
            'mapel' => 'required|string|max:100',
            'youtube' => 'required|url|max:2048',
        ]);

        $videoId = $this->youtubeVideoId($data['youtube']);
        if ($videoId === null) {
            return back()->withErrors(['youtube' => __('videos.url_error')])->withInput();
        }

        $items = $request->session()->get('videos', []);
        foreach ($items as $index => $item) {
            if ($item['id'] == $data['id']) {
                $items[$index] = array_merge($item, [
                    'judul' => $data['judul'],
                    'jenjang' => $data['jenjang'],
                    'mapel' => $data['mapel'],
                    'thumbnail' => 'https://img.youtube.com/vi/' . $videoId . '/hqdefault.jpg',
                    'youtube' => $data['youtube'],
                ]);
                break;
            }
        }

        $request->session()->put('videos', $items);
        return redirect()->back()->with('success', __('videos.updated'));
    }

    private function youtubeVideoId(string $url): ?string
    {
        $host = strtolower(parse_url($url, PHP_URL_HOST) ?: '');
        $allowedHosts = ['youtube.com', 'www.youtube.com', 'm.youtube.com', 'music.youtube.com', 'youtube-nocookie.com', 'www.youtube-nocookie.com', 'youtu.be', 'www.youtu.be'];
        if (!in_array($host, $allowedHosts, true)) {
            return null;
        }

        $path = trim(parse_url($url, PHP_URL_PATH) ?: '', '/');
        $segments = $path === '' ? [] : explode('/', $path);
        parse_str(parse_url($url, PHP_URL_QUERY) ?: '', $query);
        if (in_array($host, ['youtu.be', 'www.youtu.be'], true)) {
            $videoId = $segments[0] ?? null;
        } elseif (($segments[0] ?? null) === 'watch') {
            $videoId = $query['v'] ?? null;
        } elseif (in_array($segments[0] ?? null, ['embed', 'shorts', 'live'], true)) {
            $videoId = $segments[1] ?? null;
        } else {
            return null;
        }

        return is_string($videoId) && preg_match('/^[A-Za-z0-9_-]{11}$/', $videoId) === 1 ? $videoId : null;
    }

    public function videosDelete(Request $request)
    {
        $id = $request->input('id');
        $items = collect($request->session()->get('videos', []))->reject(fn($item) => $item['id'] == $id)->values()->all();
        $request->session()->put('videos', $items);
        return redirect()->back()->with('success', __('videos.deleted'));
    }

    public function bimbelAdd(Request $request)
    {
        $data = $request->validate([
            'nama' => 'required|string|max:255',
            'alamat' => 'nullable|string|max:255',
            'kota' => 'required|string|max:100',
            'provinsi' => 'required|string|max:100',
            'deskripsi' => 'nullable|string',
            'kontak' => 'nullable|string|max:100',
            'maps' => 'nullable|url',
            'website' => 'nullable|url|max:2048',
            'logo' => 'required|string',
            'jenjang' => 'required|in:SD,SMP,SMA,SMK,Kuliah,SD-SMP,SMP-SMA,SD-SMA',
        ], [
            'nama.required' => __('validation.required', ['attribute' => __('tutoring.institution_name')]),
            'kota.required' => __('validation.required', ['attribute' => __('tutoring.city')]),
            'provinsi.required' => __('validation.required', ['attribute' => __('tutoring.province')]),
            'logo.required' => __('validation.required', ['attribute' => __('tutoring.logo')]),
            'jenjang.required' => __('validation.required', ['attribute' => __('tutoring.level')]),
        ]);

        $items = $request->session()->get('bimbels', []);
        $data['id'] = (collect($items)->max('id') ?? 0) + 1;
        $data['logo'] = !empty($data['logo']) ? $data['logo'] : 'https://picsum.photos/seed/bimbel-' . $data['id'] . '/120/120';
        $data['maps'] = !empty($data['maps']) ? $data['maps'] : 'https://maps.google.com/?q=' . urlencode($data['nama']);
        $data['website'] = $data['website'] ?? null;
        $items[] = $data;
        $request->session()->put('bimbels', $items);

        return redirect()->back()->with('success', __('tutoring.created'));
    }

    public function bimbelEdit(Request $request)
    {
        $data = $request->validate([
            'id' => 'required|integer',
            'nama' => 'required|string|max:255',
            'alamat' => 'nullable|string|max:255',
            'kota' => 'required|string|max:100',
            'provinsi' => 'required|string|max:100',
            'deskripsi' => 'nullable|string',
            'kontak' => 'nullable|string|max:100',
            'maps' => 'nullable|url',
            'website' => 'nullable|url|max:2048',
            'logo' => 'required|string',
            'jenjang' => 'required|in:SD,SMP,SMA,SMK,Kuliah,SD-SMP,SMP-SMA,SD-SMA',
        ]);

        $items = $request->session()->get('bimbels', []);
        foreach ($items as $index => $item) {
            if ($item['id'] == $data['id']) {
                $items[$index] = array_merge($item, [
                    'nama' => $data['nama'],
                    'alamat' => $data['alamat'] ?? $item['alamat'] ?? '',
                    'kota' => $data['kota'],
                    'provinsi' => $data['provinsi'],
                    'deskripsi' => $data['deskripsi'] ?? $item['deskripsi'] ?? '',
                    'kontak' => $data['kontak'] ?? $item['kontak'] ?? '',
                    'maps' => !empty($data['maps']) ? $data['maps'] : ($item['maps'] ?? 'https://maps.google.com/?q=' . urlencode($data['nama'])),
                    'website' => $data['website'] ?? null,
                    'logo' => !empty($data['logo']) ? $data['logo'] : $item['logo'],
                    'jenjang' => $data['jenjang'],
                ]);
                break;
            }
        }

        $request->session()->put('bimbels', $items);
        return redirect()->back()->with('success', __('tutoring.updated'));
    }

    public function bimbelDelete(Request $request)
    {
        $id = $request->input('id');
        $items = collect($request->session()->get('bimbels', []))->reject(fn($item) => $item['id'] == $id)->values()->all();
        $request->session()->put('bimbels', $items);
        return redirect()->back()->with('success', __('tutoring.deleted'));
    }

    public function bimbelRegencies(string $provinceCode)
    {
        $response = Http::acceptJson()
            ->timeout(10)
            ->get('https://wilayah.id/api/regencies/' . $provinceCode . '.json');

        if (!$response->successful() || !is_array($response->json('data'))) {
            return response()->json(['message' => __('tutoring.region_load_error')], 502);
        }

        return response()->json(['data' => $response->json('data')]);
    }

    public function banners(Request $request)
    {
        $this->seedIfNeeded($request);
        return view('banner.index', ['items' => $request->session()->get('banners', [])]);
    }

    public function berita(Request $request)
    {
        $this->seedIfNeeded($request);
        return view('berita.index', ['items' => $request->session()->get('berita', [])]);
    }

    public function videos(Request $request)
    {
        $this->seedIfNeeded($request);
        return view('video.index', ['items' => $request->session()->get('videos', [])]);
    }

    public function bimbel(Request $request)
    {
        $this->seedIfNeeded($request);
        return view('bimbel.index', ['items' => $request->session()->get('bimbels', [])]);
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
        $request->session()->forget(['users','app','banners','berita','videos','bimbels']);
        $this->seedIfNeeded($request);
        return redirect()->back()->with('success', __('settings.reset_success'));
    }
}
