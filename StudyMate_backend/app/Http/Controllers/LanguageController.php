<?php

namespace App\Http\Controllers;

use Illuminate\Http\RedirectResponse;
use Illuminate\Http\Request;

class LanguageController extends Controller
{
    public function update(Request $request): RedirectResponse
    {
        $validated = $request->validate([
            'locale' => ['required', 'in:id,en'],
        ]);

        $request->session()->put('locale', $validated['locale']);

        $referer = $request->headers->get('referer');
        if ($referer) {
            $parts = parse_url($referer);
            $path = $parts['path'] ?? '';
            $host = strtolower($parts['host'] ?? '');

            if ($host === strtolower($request->getHost()) && str_starts_with($path, '/') && !str_starts_with($path, '//')) {
                $destination = $path . (isset($parts['query']) ? '?' . $parts['query'] : '');

                return redirect()->to($destination);
            }
        }

        return redirect()->route('home');
    }
}
