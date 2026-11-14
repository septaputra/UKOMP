<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;

class EnsureAdminAuthenticated
{
    /**
     * Handle an incoming request.
     */
    public function handle(Request $request, Closure $next)
    {
        $isLogged = $request->session()->get('dummy_admin_authenticated') || $request->session()->get('is_logged');

        if (!$isLogged) {
            return redirect('/login')->with('error', __('auth.login_required'));
        }

        return $next($request);
    }
}
