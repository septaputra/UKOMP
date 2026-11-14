<?php

namespace Tests\Feature;

use Tests\TestCase;

class LanguageSwitcherTest extends TestCase
{
    public function test_login_uses_indonesian_by_default_and_can_switch_to_english(): void
    {
        $this->get('/login')
            ->assertOk()
            ->assertSee('Masuk ke panel manajemen')
            ->assertSee('Kata Sandi');

        $this->withHeader('referer', 'http://localhost/login')
            ->post('/language', ['locale' => 'en'])
            ->assertRedirect('/login')
            ->assertSessionHas('locale', 'en');

        $this->get('/login')
            ->assertOk()
            ->assertSee('Sign in to management panel')
            ->assertSee('Password')
            ->assertDontSee('Masuk ke panel manajemen');
    }

    public function test_locale_is_preserved_through_login_and_logout(): void
    {
        $this->withHeader('referer', 'http://localhost/login')
            ->post('/language', ['locale' => 'en'])
            ->assertRedirect('/login');

        $this->post('/login', [
            'email' => 'admin@example.com',
            'password' => 'dummy-password',
        ])->assertRedirect('/dashboard')
            ->assertSessionHas('locale', 'en');

        $this->get('/dashboard')
            ->assertOk()
            ->assertSee('Quick Actions')
            ->assertSee('Learning Videos');

        $this->get('/logout')->assertRedirect('/login');

        $this->get('/login')
            ->assertOk()
            ->assertSee('Sign in to management panel');
    }

    public function test_settings_language_switch_changes_the_locale_for_admin_pages(): void
    {
        $this->withSession([
            'dummy_admin_authenticated' => true,
            'locale' => 'en',
        ])->get('/settings')
            ->assertOk()
            ->assertSee('Choose the language used throughout the StudyMate administration panel.')
            ->assertSee('🇮🇩')
            ->assertSee('English');

        $this->withHeader('referer', 'http://localhost/settings')
            ->post('/language', ['locale' => 'id'])
            ->assertRedirect('/settings')
            ->assertSessionHas('locale', 'id');

        $this->get('/users')
            ->assertOk()
            ->assertSee('Pengguna')
            ->assertSee('Pantau profil, status akun, metode masuk, dan tanggal bergabung pengguna.');
    }

    public function test_each_admin_page_renders_in_both_supported_locales(): void
    {
        $pages = [
            'id' => [
                '/dashboard' => 'Beranda',
                '/users' => 'Pengguna',
                '/banners' => 'Tambah Banner',
                '/berita' => 'Tambah Berita',
                '/videos' => 'Tambah Video',
                '/bimbel' => 'Tambah Tempat Bimbel',
                '/settings' => 'Pengaturan Umum',
            ],
            'en' => [
                '/dashboard' => 'Dashboard',
                '/users' => 'Users',
                '/banners' => 'Add Banner',
                '/berita' => 'Add News',
                '/videos' => 'Add Video',
                '/bimbel' => 'Add Tutoring Center',
                '/settings' => 'General',
            ],
        ];

        foreach ($pages as $locale => $routes) {
            $this->withSession([
                'dummy_admin_authenticated' => true,
                'locale' => $locale,
            ]);

            foreach ($routes as $uri => $translatedText) {
                $this->get($uri)
                    ->assertOk()
                    ->assertSee($translatedText);
            }
        }
    }

    public function test_language_route_rejects_unsupported_locales(): void
    {
        $this->from('/login')
            ->post('/language', ['locale' => 'fr'])
            ->assertRedirect('/login')
            ->assertSessionHasErrors('locale');

        $this->withHeader('referer', 'https://example.com/redirect')
            ->post('/language', ['locale' => 'en'])
            ->assertRedirect('/');
    }

    public function test_login_validation_messages_follow_the_selected_locale(): void
    {
        $this->withSession(['locale' => 'en'])
            ->from('/login')
            ->post('/login', [
                'email' => 'not-an-email',
                'password' => 'dummy-password',
            ])
            ->assertRedirect('/login')
            ->assertSessionHasErrors('email');

        $this->get('/login')
            ->assertSee('Please enter a valid email address.')
            ->assertDontSee('Format email tidak valid.');
    }
}
