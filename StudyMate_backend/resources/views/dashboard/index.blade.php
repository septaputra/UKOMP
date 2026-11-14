@extends('layouts.app')
@section('title', __('dashboard.title'))
@section('content')
<div class="row g-3 mb-4">
    @php
        $cards = [
            ['label'=>__('dashboard.total_users'),'value'=>$stats['total_user'] ?? count($users),'icon'=>'users'],
            ['label'=>__('dashboard.active_users'),'value'=>$stats['user_aktif'] ?? 0,'icon'=>'user-check'],
            ['label'=>__('dashboard.active_banners'),'value'=>$stats['banner'] ?? 0,'icon'=>'image'],
            ['label'=>__('dashboard.total_news'),'value'=>$stats['berita'] ?? 0,'icon'=>'newspaper'],
            ['label'=>__('dashboard.total_videos'),'value'=>$stats['video'] ?? 0,'icon'=>'circle-play'],
            ['label'=>__('dashboard.total_tutoring'),'value'=>$stats['tempat_bimbel'] ?? 0,'icon'=>'location-dot'],
        ];
    @endphp
    @foreach($cards as $card)
        <div class="col-12 col-sm-6 col-xl-4">
            <div class="card border-0 rounded-4 shadow-sm h-100 hover-card app-surface">
                <div class="card-body d-flex align-items-center justify-content-between">
                    <div>
                        <div class="text-muted small">{{ $card['label'] }}</div>
                        <div class="fs-3 fw-bold">{{ $card['value'] }}</div>
                    </div>
                    <div class="accent-circle"><i class="fa fa-{{ $card['icon'] }}"></i></div>
                </div>
            </div>
        </div>
    @endforeach
</div>

<div class="row g-4 mb-4">
    <div class="col-lg-7">
        <div class="card border-0 rounded-4 shadow-sm h-100 app-surface">
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <div>
                        <div class="text-muted small">{{ __('dashboard.activity_chart') }}</div>
                        <div class="fw-semibold">{{ __('dashboard.weekly_trend') }}</div>
                    </div>
                    <span class="badge text-bg-warning">{{ __('dashboard.realtime_theme') }}</span>
                </div>
                <div style="height:280px;">
                    <canvas id="dashboardChart"></canvas>
                </div>
            </div>
        </div>
    </div>
    <div class="col-lg-5">
        <div class="card border-0 rounded-4 shadow-sm h-100 app-surface">
            <div class="card-body">
                <div class="text-muted small mb-2">{{ __('dashboard.recent_activity') }}</div>
                <div class="list-group list-group-flush">
                    @foreach($activities as $activity)
                        <div class="list-group-item px-0 py-3 bg-transparent border-0 d-flex gap-3 align-items-start">
                            <div class="status-dot mt-1"></div>
                            <div>{{ $activity }}</div>
                        </div>
                    @endforeach
                </div>
            </div>
        </div>
    </div>
</div>

<div class="row g-4">
    <div class="col-lg-7">
        <div class="card border-0 rounded-4 shadow-sm h-100 app-surface">
            <div class="card-body">
                <div class="fw-semibold mb-3">{{ __('dashboard.quick_actions') }}</div>
                <div class="row g-3">
                    @foreach([
                        ['label'=>__('dashboard.add_banner'),'icon'=>'image','href'=>'/banners'],
                        ['label'=>__('dashboard.add_news'),'icon'=>'newspaper','href'=>'/berita'],
                        ['label'=>__('dashboard.add_video'),'icon'=>'circle-play','href'=>'/videos'],
                        ['label'=>__('dashboard.add_tutoring'),'icon'=>'location-dot','href'=>'/bimbel'],
                        ['label'=>__('dashboard.monitor_users'),'description'=>__('dashboard.monitor_users_description'),'icon'=>'users','href'=>route('users')],
                    ] as $action)
                    <div class="col-12 col-md-6">
                        <a href="{{ $action['href'] }}" class="text-decoration-none">
                            <div class="card border-0 rounded-4 h-100 quick-action-card">
                                <div class="card-body d-flex align-items-center gap-3">
                                    <div class="rounded-circle d-flex align-items-center justify-content-center accent-circle alt"><i class="fa fa-{{ $action['icon'] }}"></i></div>
                                    <div>
                                        <div class="fw-semibold">{{ $action['label'] }}</div>
                                        <div class="text-muted small">{{ $action['description'] ?? __('dashboard.shortcut') }}</div>
                                    </div>
                                </div>
                            </div>
                        </a>
                    </div>
                    @endforeach
                </div>
            </div>
        </div>
    </div>
    <div class="col-lg-5">
        <div class="card border-0 rounded-4 shadow-sm h-100 app-surface">
            <div class="card-body">
                <div class="fw-semibold mb-3">{{ __('dashboard.admin_focus') }}</div>
                <div class="d-flex flex-column gap-3">
                    <div class="p-3 rounded-4 accent-panel">
                        <div class="small text-muted">{{ __('dashboard.core_task') }}</div>
                        <div class="fw-semibold">{{ __('dashboard.core_task_description') }}</div>
                    </div>
                    <div class="p-3 rounded-4 accent-panel alt">
                        <div class="small text-muted">{{ __('dashboard.target') }}</div>
                        <div class="fw-semibold">{{ __('dashboard.target_description') }}</div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
@php
    $chartValues = $chartSeries ?? [72, 58, 66, 84, 91, 63, 77];
@endphp
document.addEventListener('DOMContentLoaded', function(){
    const canvas = document.getElementById('dashboardChart');
    if(!canvas || typeof Chart === 'undefined') return;

    const readThemeColors = () => {
        const styles = getComputedStyle(document.documentElement);
        return {
            textColor: styles.getPropertyValue('--text').trim() || '#E5E7EB',
            mutedColor: styles.getPropertyValue('--muted').trim() || '#9CA3AF',
            borderColor: styles.getPropertyValue('--border').trim() || '#1F2933',
            primary: styles.getPropertyValue('--primary').trim() || '#F97316',
            card: styles.getPropertyValue('--card').trim() || '#111827',
        };
    };

    const colors = readThemeColors();
    const chart = new Chart(canvas, {
        type: 'bar',
        data: {
            labels: @json(array_values(__('dashboard.days'))),
            datasets: [{
                label: @json(__('dashboard.chart_label')),
                data: @json($chartValues),
                backgroundColor: colors.primary,
                borderRadius: 12,
                barThickness: 18,
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: true,
                    labels: { color: colors.textColor }
                },
                tooltip: {
                    backgroundColor: colors.card,
                    titleColor: colors.textColor,
                    bodyColor: colors.textColor,
                    borderColor: colors.borderColor,
                    borderWidth: 1
                }
            },
            scales: {
                x: {
                    grid: { color: colors.borderColor },
                    ticks: { color: colors.mutedColor }
                },
                y: {
                    beginAtZero: true,
                    grid: { color: colors.borderColor },
                    ticks: { color: colors.mutedColor }
                }
            }
        }
    });

    document.addEventListener('studymate:theme-change', () => {
        const next = readThemeColors();
        chart.data.datasets[0].backgroundColor = next.primary;
        chart.options.plugins.legend.labels.color = next.textColor;
        chart.options.plugins.tooltip.backgroundColor = next.card;
        chart.options.plugins.tooltip.titleColor = next.textColor;
        chart.options.plugins.tooltip.bodyColor = next.textColor;
        chart.options.plugins.tooltip.borderColor = next.borderColor;
        chart.options.scales.x.grid.color = next.borderColor;
        chart.options.scales.x.ticks.color = next.mutedColor;
        chart.options.scales.y.grid.color = next.borderColor;
        chart.options.scales.y.ticks.color = next.mutedColor;
        chart.update();
    });
});
</script>

@endsection
