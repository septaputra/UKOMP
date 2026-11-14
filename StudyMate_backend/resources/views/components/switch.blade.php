@props([
    'name' => 'theme-switch',
    'checked' => false,
    'labelOn' => 'ON',
    'labelOff' => 'OFF',
    'description' => null,
    'id' => null,
])

@php
    $id = $id ?? $name.'-'.substr(md5($name), 0, 6);
@endphp

<div {{ $attributes->merge(['class' => 'studymate-switch-wrapper']) }}>
    @if($description)
        <div class="mb-2 text-muted small">{{ $description }}</div>
    @endif

    <label for="{{ $id }}" class="theme-switch-pill theme-switch-settings">
        <input id="{{ $id }}" type="checkbox" name="{{ $name }}" class="theme-switch-pill-input" data-theme-toggle {{ $checked ? 'checked' : '' }}>
        <span class="theme-switch-pill-track">
            <span class="theme-switch-pill-option theme-switch-light">
                <i class="fa fa-sun"></i>
                <span>{{ $labelOn }}</span>
            </span>
            <span class="theme-switch-pill-option theme-switch-dark">
                <i class="fa fa-moon"></i>
                <span>{{ $labelOff }}</span>
            </span>
        </span>
    </label>
</div>