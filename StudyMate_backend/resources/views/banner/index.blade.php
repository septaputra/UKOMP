@extends('layouts.app')
@section('title', __('banners.title'))
@section('content')
<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <div class="text-muted small">{{ __('banners.description') }}</div>
        <h4 class="mb-0 fw-semibold">{{ __('banners.title') }}</h4>
    </div>
    <button type="button" class="btn btn-warning" data-bs-toggle="modal" data-bs-target="#bannerModal" id="bannerCreateBtn">
        <i class="fa fa-plus me-2"></i>{{ __('banners.add') }}
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
                <img src="{{ $item['image'] ?? 'https://picsum.photos/seed/banner-default/800/400' }}" class="card-img-top" alt="banner">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start mb-2">
                        <div>
                            <div class="fw-semibold">{{ $item['judul'] }}</div>
                            <div class="text-muted small">{{ ['Home Hero' => __('banners.home_hero'), 'Sidebar' => __('banners.sidebar'), 'Popup' => __('banners.popup')][$item['posisi']] ?? $item['posisi'] }}</div>
                        </div>
                        <span class="badge text-bg-{{ ($item['status'] ?? 'aktif') === 'aktif' ? 'success' : 'secondary' }}">{{ ($item['status'] ?? 'aktif') === 'aktif' ? __('common.active') : __('common.draft') }}</span>
                    </div>
                    <div class="small text-muted">{{ __('banners.order') }}: {{ $item['urutan'] ?? 1 }}</div>
                    <div class="d-flex gap-2 mt-3">
                        <button type="button" class="btn btn-sm btn-outline-primary" data-banner-edit data-item='@json($item)' data-bs-toggle="modal" data-bs-target="#bannerModal">{{ __('common.edit') }}</button>
                        <form method="POST" action="{{ route('banners.delete') }}" onsubmit="return confirm(@json(__('banners.delete_confirm')))" class="d-inline">
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

<div class="modal fade" id="bannerModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg">
            <form id="bannerForm" method="POST" action="{{ route('banners.add') }}">
                @csrf
                <input type="hidden" name="id" id="banner_id">
                <div class="modal-header border-0">
                    <h5 class="modal-title" id="bannerModalTitle">{{ __('banners.create_title') }}</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="{{ __('common.close') }}"></button>
                </div>
                <div class="modal-body">
                    <div class="row g-3">
                        <div class="col-12">
                            <label class="form-label">{{ __('banners.banner_title') }}</label>
                            <input type="text" class="form-control" name="judul" id="banner_judul" required>
                        </div>
                        <div class="col-12">
                            <label class="form-label">{{ __('banners.image') }}</label>
                            <div class="image-upload-box">
                                <div class="image-upload-preview-wrap">
                                    <img id="banner_image_preview" class="image-upload-preview d-none" alt="Preview banner">
                                    <div id="banner_image_placeholder" class="image-upload-placeholder">
                                        <i class="fa fa-image"></i>
                                        <span>{{ __('common.no_image') }}</span>
                                    </div>
                                </div>
                                <div class="image-upload-meta">
                                    <div id="banner_image_name" class="image-upload-file-name">{{ __('common.no_file') }}</div>
                                    <div class="image-upload-actions">
                                        <input type="file" id="banner_image_file" class="image-upload-input" accept="image/jpeg,image/jpg,image/png,image/webp">
                                        <button type="button" class="image-upload-button" data-trigger="banner_image_file"><i class="fa fa-upload"></i> {{ __('common.choose_image') }}</button>
                                        <button type="button" id="banner_image_clear" class="image-upload-clear" type="button"><i class="fa fa-trash"></i> {{ __('common.delete') }}</button>
                                    </div>
                                </div>
                                <input type="hidden" name="image" id="banner_image" value="">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">{{ __('banners.position') }}</label>
                            <select class="form-select" name="posisi" id="banner_posisi" required>
                                <option value="Home Hero">{{ __('banners.home_hero') }}</option>
                                <option value="Sidebar">{{ __('banners.sidebar') }}</option>
                                <option value="Popup">{{ __('banners.popup') }}</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">{{ __('common.status') }}</label>
                            <select class="form-select" name="status" id="banner_status" required>
                                <option value="aktif">{{ __('common.active') }}</option>
                                <option value="draft">{{ __('common.draft') }}</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">{{ __('banners.order') }}</label>
                            <input type="number" class="form-control" name="urutan" id="banner_urutan" min="1" value="1" required>
                        </div>
                    </div>
                </div>
                <div class="modal-footer border-0">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">{{ __('common.cancel') }}</button>
                    <button type="submit" class="btn btn-warning" id="bannerSubmit">{{ __('common.save') }}</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
document.addEventListener('DOMContentLoaded', function () {
    const text = {{ \Illuminate\Support\Js::from([
        'noFile' => __('common.no_file'),
        'imageNew' => __('common.image_new'),
        'imageCurrent' => __('common.image_current'),
        'imageFormatError' => __('common.image_format_error'),
        'imageSizeError' => __('common.image_size_error'),
        'createTitle' => __('banners.create_title'),
        'editTitle' => __('banners.edit_title'),
        'save' => __('common.save'),
        'update' => __('common.update'),
        'requiredError' => __('banners.required_error'),
    ]) }};
    const modal = document.getElementById('bannerModal');
    const form = document.getElementById('bannerForm');
    const modalTitle = document.getElementById('bannerModalTitle');
    const submitBtn = document.getElementById('bannerSubmit');
    const fileInput = document.getElementById('banner_image_file');
    const hiddenImage = document.getElementById('banner_image');
    const preview = document.getElementById('banner_image_preview');
    const placeholder = document.getElementById('banner_image_placeholder');
    const fileName = document.getElementById('banner_image_name');
    const fields = {
        id: document.getElementById('banner_id'),
        judul: document.getElementById('banner_judul'),
        posisi: document.getElementById('banner_posisi'),
        status: document.getElementById('banner_status'),
        urutan: document.getElementById('banner_urutan')
    };

    function updateImagePreview(value) {
        if (value && /^data:image\//i.test(value)) {
            preview.src = value;
            preview.classList.remove('d-none');
            placeholder.classList.add('d-none');
        } else if (value && /^https?:\/\//i.test(value)) {
            preview.src = value;
            preview.classList.remove('d-none');
            placeholder.classList.add('d-none');
        } else {
            preview.src = '';
            preview.classList.add('d-none');
            placeholder.classList.remove('d-none');
        }

        fileName.textContent = value ? (value.startsWith('data:image') ? text.imageNew : text.imageCurrent) : text.noFile;
    }

    function setupImageUploader() {
        document.querySelector('[data-trigger="banner_image_file"]').addEventListener('click', function () {
            fileInput.click();
        });

        document.getElementById('banner_image_clear').addEventListener('click', function () {
            fileInput.value = '';
            hiddenImage.value = '';
            updateImagePreview('');
        });

        fileInput.addEventListener('change', function () {
            const file = fileInput.files && fileInput.files[0];
            if (!file) return;
            if (!file.type.match(/^image\/(jpeg|png|jpg|webp)$/i)) {
                alert(text.imageFormatError);
                fileInput.value = '';
                return;
            }
            if (file.size > 2 * 1024 * 1024) {
                alert(text.imageSizeError);
                fileInput.value = '';
                return;
            }

            const reader = new FileReader();
            reader.onload = function (event) {
                hiddenImage.value = event.target.result;
                updateImagePreview(event.target.result);
                fileInput.value = '';
            };
            reader.readAsDataURL(file);
        });
    }

    function resetForm() {
        fields.id.value = '';
        fields.judul.value = '';
        hiddenImage.value = '';
        fields.posisi.value = 'Home Hero';
        fields.status.value = 'aktif';
        fields.urutan.value = 1;
        updateImagePreview('');
        form.action = '{{ route('banners.add') }}';
        modalTitle.textContent = text.createTitle;
        submitBtn.textContent = text.save;
    }

    setupImageUploader();
    updateImagePreview(hiddenImage.value || '');

    document.getElementById('bannerCreateBtn').addEventListener('click', function () {
        resetForm();
    });

    document.querySelectorAll('[data-banner-edit]').forEach(function (button) {
        button.addEventListener('click', function () {
            const item = JSON.parse(this.dataset.item || '{}');
            fields.id.value = item.id || '';
            fields.judul.value = item.judul || '';
            hiddenImage.value = item.image || '';
            fields.posisi.value = item.posisi || 'Home Hero';
            fields.status.value = item.status || 'aktif';
            fields.urutan.value = item.urutan || 1;
            updateImagePreview(item.image || '');
            form.action = '{{ route('banners.edit') }}';
            modalTitle.textContent = text.editTitle;
            submitBtn.textContent = text.update;
        });
    });

    form.addEventListener('submit', function (event) {
        const judul = fields.judul.value.trim();
        const image = hiddenImage.value.trim();
        const posisi = fields.posisi.value.trim();
        const urutan = Number(fields.urutan.value);

        if (!judul || !image || !posisi || !fields.status.value || !Number.isInteger(urutan) || urutan < 1) {
            event.preventDefault();
            alert(text.requiredError);
            return;
        }
    });
});
</script>
@endsection