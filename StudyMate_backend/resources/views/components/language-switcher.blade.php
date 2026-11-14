@props(['style' => 'compact'])

<form method="POST" action="{{ route('language.update') }}" class="language-switcher language-switcher-{{ $style }}" aria-label="{{ __('settings.language') }}">
    @csrf
    <button type="submit" name="locale" value="id" class="language-switcher-option {{ app()->getLocale() === 'id' ? 'is-active' : '' }}" aria-pressed="{{ app()->getLocale() === 'id' ? 'true' : 'false' }}">
        <span aria-hidden="true">🇮🇩</span>
        <span>{{ $style === 'full' ? 'Bahasa Indonesia' : 'ID' }}</span>
    </button>
    <button type="submit" name="locale" value="en" class="language-switcher-option {{ app()->getLocale() === 'en' ? 'is-active' : '' }}" aria-pressed="{{ app()->getLocale() === 'en' ? 'true' : 'false' }}">
        <span aria-hidden="true">🇬🇧</span>
        <span>{{ $style === 'full' ? 'English' : 'EN' }}</span>
    </button>
</form>
