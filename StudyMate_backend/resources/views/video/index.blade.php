@extends('layouts.app')
@section('title', __('videos.title'))
@section('content')
<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <div class="text-muted small">{{ __('videos.description') }}</div>
        <h4 class="mb-0 fw-semibold">{{ __('videos.title') }}</h4>
    </div>
    <button type="button" class="btn btn-warning" data-bs-toggle="modal" data-bs-target="#videoModal" id="videoCreateBtn">
        <i class="fa fa-plus me-2"></i>{{ __('videos.add') }}
    </button>
</div>

@if(session('success'))
    <div class="alert alert-success">{{ session('success') }}</div>
@endif

@if($errors->any())
    <div class="alert alert-danger">
        <ul class="mb-0">
            @foreach($errors->all() as $error)
                <li>{{ $error }}</li>
            @endforeach
        </ul>
    </div>
@endif

<div class="row g-4">
    @forelse($items as $item)
        <div class="col-12 col-md-6 col-xl-4">
            <div class="card border-0 rounded-4 shadow-sm h-100 overflow-hidden">
                <img src="{{ $item['thumbnail'] ?? 'https://picsum.photos/seed/video-default/480/320' }}" class="card-img-top" alt="{{ __('videos.title') }}">
                <div class="card-body">
                    <div class="text-muted small">{{ __('videos.internal_label') }}</div>
                    <div class="fw-semibold">{{ $item['judul'] }}</div>
                    <div class="text-muted small mb-3">{{ $item['jenjang'] }} · {{ $item['mapel'] }}</div>
                    <div class="d-flex gap-2 flex-wrap">
                        <a href="{{ !empty($item['youtube']) ? $item['youtube'] : 'https://www.youtube.com' }}" class="btn btn-sm btn-outline-danger" target="_blank">YouTube</a>
                        <button type="button" class="btn btn-sm btn-outline-primary" data-video-edit data-item='@json($item)' data-bs-toggle="modal" data-bs-target="#videoModal">{{ __('common.edit') }}</button>
                        <form method="POST" action="{{ route('videos.delete') }}" onsubmit="return confirm(@json(__('videos.delete_confirm')))" class="d-inline">
                            @csrf
                            <input type="hidden" name="id" value="{{ $item['id'] }}">
                            <button type="submit" class="btn btn-sm btn-outline-danger">{{ __('common.delete') }}</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    @empty
        <div class="col-12"><div class="text-center py-5 text-muted">{{ __('common.empty_state') }}</div></div>
    @endforelse
</div>

<div class="modal fade" id="videoModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg">
            <form id="videoForm" method="POST" action="{{ route('videos.add') }}">
                @csrf
                <input type="hidden" name="id" id="video_id">
                <div class="modal-header border-0">
                    <h5 class="modal-title" id="videoModalTitle">{{ __('videos.create_title') }}</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="{{ __('common.close') }}"></button>
                </div>
                <div class="modal-body">
                    <div class="row g-3">
                        <div class="col-12">
                            <label class="form-label">{{ __('videos.headline') }}</label>
                            <input type="text" class="form-control" name="judul" id="video_judul" placeholder="{{ __('videos.internal_name_placeholder') }}" required>
                            <div class="form-text">{{ __('videos.internal_name_help') }}</div>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">{{ __('videos.level') }}</label>
                            <select class="form-select" name="jenjang" id="video_jenjang" required>
                                <option value="SD">SD</option>
                                <option value="SMP">SMP</option>
                                <option value="SMA">SMA</option>
                                <option value="SMK">SMK</option>
                                <option value="Kuliah">Kuliah</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">{{ __('videos.subject') }}</label>
                            <input type="text" class="form-control" name="mapel" id="video_mapel" placeholder="{{ __('videos.subject_placeholder') }}" required>
                        </div>
                        <div class="col-12">
                            <label class="form-label">{{ __('videos.youtube_url') }}</label>
                            <input type="url" class="form-control" name="youtube" id="video_youtube" placeholder="https://www.youtube.com/watch?v=..." required>
                        </div>
                        <div class="col-12">
                            <label class="form-label">{{ __('videos.thumbnail') }}</label>
                            <div class="image-upload-box">
                                <div class="image-upload-preview-wrap">
                                    <img id="video_image_preview" class="image-upload-preview d-none" alt="Preview thumbnail YouTube">
                                    <div id="video_image_placeholder" class="image-upload-placeholder">
                                        <i class="fa fa-image"></i>
                                        <span>{{ __('common.no_image') }}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer border-0">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">{{ __('common.cancel') }}</button>
                    <button type="submit" class="btn btn-warning" id="videoSubmit">{{ __('common.save') }}</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
document.addEventListener('DOMContentLoaded', function () {
    const text = {{ \Illuminate\Support\Js::from([
        'createTitle' => __('videos.create_title'),
        'editTitle' => __('videos.edit_title'),
        'save' => __('common.save'),
        'update' => __('common.update'),
        'requiredError' => __('videos.required_error'),
        'urlError' => __('videos.url_error'),
    ]) }};
    const form = document.getElementById('videoForm');
    const modalTitle = document.getElementById('videoModalTitle');
    const submitBtn = document.getElementById('videoSubmit');
    const preview = document.getElementById('video_image_preview');
    const placeholder = document.getElementById('video_image_placeholder');
    const fields = {
        id: document.getElementById('video_id'),
        judul: document.getElementById('video_judul'),
        jenjang: document.getElementById('video_jenjang'),
        mapel: document.getElementById('video_mapel'),
        youtube: document.getElementById('video_youtube')
    };

    function extractYouTubeId(value) {
        try {
            const url = new URL(value);
            const host = url.hostname.toLowerCase();
            const allowedHosts = ['youtube.com', 'www.youtube.com', 'm.youtube.com', 'music.youtube.com', 'youtube-nocookie.com', 'www.youtube-nocookie.com', 'youtu.be', 'www.youtu.be'];
            if (!allowedHosts.includes(host)) return null;

            let videoId = null;
            if (host === 'youtu.be' || host === 'www.youtu.be') {
                videoId = url.pathname.split('/').filter(Boolean)[0];
            } else if (url.pathname === '/watch') {
                videoId = url.searchParams.get('v');
            } else {
                const parts = url.pathname.split('/').filter(Boolean);
                if (['embed', 'shorts', 'live'].includes(parts[0])) videoId = parts[1];
            }
            return videoId && /^[A-Za-z0-9_-]{11}$/.test(videoId) ? videoId : null;
        } catch (error) {
            return null;
        }
    }

    function updateThumbnailPreview() {
        const videoId = extractYouTubeId(fields.youtube.value.trim());
        if (videoId) {
            preview.src = 'https://img.youtube.com/vi/' + videoId + '/hqdefault.jpg';
            preview.classList.remove('d-none');
            placeholder.classList.add('d-none');
        } else {
            preview.src = '';
            preview.classList.add('d-none');
            placeholder.classList.remove('d-none');
        }
    }

    function resetForm() {
        fields.id.value = '';
        fields.judul.value = '';
        fields.jenjang.value = 'SMP';
        fields.mapel.value = '';
        fields.youtube.value = '';
        updateThumbnailPreview();
        form.action = '{{ route('videos.add') }}';
        modalTitle.textContent = text.createTitle;
        submitBtn.textContent = text.save;
    }

    updateThumbnailPreview();
    fields.youtube.addEventListener('input', updateThumbnailPreview);

    document.getElementById('videoCreateBtn').addEventListener('click', function () {
        resetForm();
    });

    document.querySelectorAll('[data-video-edit]').forEach(function (button) {
        button.addEventListener('click', function () {
            const item = JSON.parse(this.dataset.item || '{}');
            fields.id.value = item.id || '';
            fields.judul.value = item.judul || '';
            fields.jenjang.value = item.jenjang || 'SMP';
            fields.mapel.value = item.mapel || '';
            fields.youtube.value = item.youtube || '';
            updateThumbnailPreview();
            form.action = '{{ route('videos.edit') }}';
            modalTitle.textContent = text.editTitle;
            submitBtn.textContent = text.update;
        });
    });

    form.addEventListener('submit', function (event) {
        const judul = fields.judul.value.trim();
        const mapel = fields.mapel.value.trim();
        const youtube = fields.youtube.value.trim();

        if (!judul || !mapel || !youtube) {
            event.preventDefault();
            alert(text.requiredError);
            return;
        }

        if (!extractYouTubeId(youtube)) {
            event.preventDefault();
            alert(text.urlError);
        }
    });
});
</script>
@endsection