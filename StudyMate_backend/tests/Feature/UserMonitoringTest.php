<?php

namespace Tests\Feature;

use Tests\TestCase;

class UserMonitoringTest extends TestCase
{
    public function test_dashboard_uses_localized_home_label_and_has_six_statistics(): void
    {
        $this->withSession([
            'dummy_admin_authenticated' => true,
            'locale' => 'id',
        ])->get('/dashboard')
            ->assertOk()
            ->assertSee('<h1 class="text-2xl font-semibold mb-0">Beranda</h1>', false)
            ->assertSee('Pantau Pengguna')
            ->assertSee('Lihat dan pantau data pengguna terdaftar.')
            ->assertSee('href="'.route('users').'"', false)
            ->assertDontSee('Pengguna Diblokir')
            ->assertDontSee('Akun pengguna diaktifkan kembali');

        $dashboard = $this->withSession([
            'dummy_admin_authenticated' => true,
            'locale' => 'en',
        ])->get('/dashboard');

        $dashboard->assertOk()
            ->assertSee('<h1 class="text-2xl font-semibold mb-0">Dashboard</h1>', false)
            ->assertSee('Monitor Users')
            ->assertSee('View and monitor registered users.')
            ->assertDontSee('Banned Users')
            ->assertDontSee('reactivated');

        $this->assertSame(6, substr_count($dashboard->getContent(), 'class="col-12 col-sm-6 col-xl-4"'));
    }

    public function test_users_page_is_read_only_and_shows_only_the_four_monitoring_columns(): void
    {
        $this->withSession([
            'dummy_admin_authenticated' => true,
            'locale' => 'id',
        ])->get('/users')
            ->assertOk()
            ->assertSeeInOrder(['Profil</th>', 'Status</th>', 'Metode Masuk</th>', 'Bergabung</th>'])
            ->assertSee('Admin')
            ->assertSee('admin@example.com')
            ->assertSee('10 Desember 2025')
            ->assertSee('Google')
            ->assertDontSee('Statistik')
            ->assertDontSee('Pemblokiran')
            ->assertDontSee('Ditangguhkan')
            ->assertDontSee('Edit')
            ->assertDontSee('Delete')
            ->assertDontSee('jadwal')
            ->assertDontSee('catatan')
            ->assertDontSee('banLabel');

        $english = $this->withSession([
            'dummy_admin_authenticated' => true,
            'locale' => 'en',
        ])->get('/users');

        $english->assertOk()
            ->assertSeeInOrder(['Profile</th>', 'Status</th>', 'Login Method</th>', 'Joined</th>'])
            ->assertSee('Monitor user profiles, account status, login methods, and registration dates.')
            ->assertSee('December 10, 2025');

        $this->assertSame(4, substr_count($english->getContent(), '<th '));
    }

    public function test_user_modification_routes_are_not_available(): void
    {
        $this->withSession(['dummy_admin_authenticated' => true])
            ->post('/users/add')
            ->assertNotFound();

        $this->post('/users/edit')->assertNotFound();
        $this->post('/users/delete')->assertNotFound();
    }

    public function test_existing_dummy_user_records_are_cleaned_for_monitoring(): void
    {
        $response = $this->withSession([
            'dummy_admin_authenticated' => true,
            'locale' => 'en',
            'users' => [[
                'id' => 3,
                'nama' => 'Legacy User',
                'email' => 'legacy@example.com',
                'foto' => null,
                'status' => 'suspend',
                'provider' => 'email',
                'joined' => '2026-01-20',
                'jadwal' => 2,
                'catatan' => 3,
                'tugas' => 1,
                'ban' => 'Suspend 3 hari',
            ]],
        ])->get('/users');

        $response->assertOk()
            ->assertSee('Active')
            ->assertSessionHas('users.0.status', 'aktif')
            ->assertSessionMissing('users.0.jadwal')
            ->assertSessionMissing('users.0.catatan')
            ->assertSessionMissing('users.0.tugas')
            ->assertSessionMissing('users.0.ban');
    }
}
