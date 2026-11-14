@extends('layouts.app')
@section('title', __('news.title'))
@section('content')
<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <div class="text-muted small">{{ __('news.description') }}</div>
        <h4 class="mb-0 fw-semibold">{{ __('news.title') }}</h4>
    </div>
    <button type="button" class="btn btn-warning" data-bs-toggle="modal" data-bs-target="#beritaModal" id="beritaCreateBtn">
        <i class="fa fa-plus me-2"></i>{{ __('news.add') }}
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
        <div class="col-12 col-lg-6">
            <div class="card border-0 rounded-4 shadow-sm h-100 overflow-hidden">
                <div class="row g-0">
                    <div class="col-md-4"><img src="{{ $item['thumbnail'] ?? 'https://picsum.photos/seed/news-default/480/320' }}" class="img-fluid h-100 w-100 object-fit-cover" alt="thumb"></div>
                    <div class="col-md-8">
                        <div class="card-body">
                            <div class="d-flex justify-content-between align-items-start mb-2">
                                <div>
                                    <div class="text-muted small">{{ __('news.internal_label') }}</div>
                                    <div class="fw-semibold">{{ $item['judul'] }}</div>
                                </div>
                                <span class="badge text-bg-{{ ($item['status'] ?? 'publish') === 'publish' ? 'success' : 'secondary' }}">{{ ($item['status'] ?? 'publish') === 'publish' ? __('common.published') : __('common.draft') }}</span>
                            </div>
                            <div class="d-flex gap-2 flex-wrap">
                                <a href="{{ $item['link'] ?? '#' }}" class="btn btn-sm btn-outline-primary" target="_blank" rel="noopener">{{ __('news.news_link') }}</a>
                                <button type="button" class="btn btn-sm btn-outline-primary" data-berita-edit data-item='@json($item)' data-bs-toggle="modal" data-bs-target="#beritaModal">{{ __('common.edit') }}</button>
                                <form method="POST" action="{{ route('berita.delete') }}" onsubmit="return confirm(@json(__('news.delete_confirm')))" class="d-inline">
                                    @csrf
                                    <input type="hidden" name="id" value="{{ $item['id'] }}">
                                    <button type="submit" class="btn btn-sm btn-outline-danger">{{ __('common.delete') }}</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    @empty
        <div class="col-12"><div class="text-center py-5 text-muted">{{ __('common.empty_state') }}</div></div>
    @endforelse
</div>

<div class="modal fade" id="beritaModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg">
            <form id="beritaForm" method="POST" action="{{ route('berita.add') }}">
                @csrf
                <input type="hidden" name="id" id="berita_id">
                <div class="modal-header border-0">
                    <h5 class="modal-title" id="beritaModalTitle">{{ __('news.create_title') }}</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="{{ __('common.close') }}"></button>
                </div>
                <div class="modal-body">
                    <div class="row g-3">
                        <div class="col-12">
                            <label class="form-label">{{ __('news.headline') }}</label>
                            <input type="text" class="form-control" name="judul" id="berita_judul" placeholder="{{ __('news.internal_name_placeholder') }}" required>
                            <div class="form-text">{{ __('news.internal_name_help') }}</div>
                        </div>
                        <div class="col-12">
                            <label class="form-label">{{ __('news.status') }}</label>
                            <select class="form-select" name="status" id="berita_status" required>
                                <option value="publish">{{ __('common.published') }}</option>
                                <option value="draft">{{ __('common.draft') }}</option>
                            </select>
                        </div>
                        <div class="col-12">
                            <label class="form-label">{{ __('news.link_label') }}</label>
                            <input type="url" class="form-control" name="link" id="berita_link" placeholder="https://example.com/berita/1" required>
                        </div>
                        <div class="col-12">
                            <label class="form-label">{{ __('news.image') }}</label>
                            <div class="image-upload-box">
                                <div class="image-upload-preview-wrap">
                                    <img id="berita_image_preview" class="image-upload-preview d-none" alt="{{ __('news.preview_alt') }}">
                                    <div id="berita_image_placeholder" class="image-upload-placeholder">
                                        <i class="fa fa-image"></i>
                                        <span>{{ __('common.no_image') }}</span>
                                    </div>
                                </div>
                            </div>
                            <input type="hidden" name="thumbnail" id="berita_thumbnail" value="">
                        </div>
                    </div>
                </div>
                <div class="modal-footer border-0">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">{{ __('common.cancel') }}</button>
                    <button type="submit" class="btn btn-warning" id="beritaSubmit">{{ __('common.save') }}</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
document.addEventListener('DOMContentLoaded', function () {
    const text = {{ \Illuminate\Support\Js::from([
        'createTitle' => __('news.create_title'),
        'editTitle' => __('news.edit_title'),
        'save' => __('common.save'),
        'update' => __('common.update'),
        'requiredError' => __('news.required_error'),
        'urlError' => __('news.url_error'),
        'previewError' => __('news.preview_error'),
    ]) }};
    const form = document.getElementById('beritaForm');
    const modalTitle = document.getElementById('beritaModalTitle');
    const submitBtn = document.getElementById('beritaSubmit');
    const hiddenImage = document.getElementById('berita_thumbnail');
    const preview = document.getElementById('berita_image_preview');
    const placeholder = document.getElementById('berita_image_placeholder');
    const fields = {
        id: document.getElementById('berita_id'),
        judul: document.getElementById('berita_judul'),
        link: document.getElementById('berita_link'),
        status: document.getElementById('berita_status')
    };

    let previewRequest = 0;
    let previewPromise = Promise.resolve();

    function clearPreview() {
        hiddenImage.value = '';
        preview.src = '';
        preview.classList.add('d-none');
        placeholder.classList.remove('d-none');
    }

    function updateArticlePreview() {
        const requestId = ++previewRequest;
        const articleUrl = fields.link.value.trim();
        clearPreview();
        if (!articleUrl) return Promise.resolve();

        previewPromise = (async function () {
            try {
                const url = new URL(articleUrl);
                if (!['http:', 'https:'].includes(url.protocol)) return;

                let imageUrl;
                if (/\.(jpe?g|png|webp|gif)(?:[?#]|$)/i.test(url.pathname + url.search)) {
                    imageUrl = url.href;
                } else {
                    const metadataUrl = new URL('https://api.microlink.io/');
                    metadataUrl.searchParams.set('url', url.href);
                    metadataUrl.searchParams.set('embed', 'image.url');
                    const response = await fetch(metadataUrl.toString(), { headers: { Accept: 'application/json' } });
                    if (!response.ok) throw new Error('Thumbnail metadata request failed');
                    const result = await response.json();
                    imageUrl = result && result.data && result.data.image && result.data.image.url;
                }

                if (requestId !== previewRequest || typeof imageUrl !== 'string' || !/^https?:\/\//i.test(imageUrl)) return;
                preview.src = imageUrl;
                preview.classList.remove('d-none');
                placeholder.classList.add('d-none');
                hiddenImage.value = imageUrl;
            } catch (error) {
                if (requestId === previewRequest) clearPreview();
            }
        })();

        return previewPromise;
    }

    function resetForm() {
        fields.id.value = '';
        fields.judul.value = '';
        fields.link.value = '';
        fields.status.value = 'publish';
        clearPreview();
        form.action = '{{ route('berita.add') }}';
        modalTitle.textContent = text.createTitle;
        submitBtn.textContent = text.save;
    }

    fields.link.addEventListener('input', updateArticlePreview);

    document.getElementById('beritaCreateBtn').addEventListener('click', function () {
        resetForm();
    });

    document.querySelectorAll('[data-berita-edit]').forEach(function (button) {
        button.addEventListener('click', function () {
            const item = JSON.parse(this.dataset.item || '{}');
            fields.id.value = item.id || '';
            fields.judul.value = item.judul || '';
            fields.link.value = item.link || '';
            fields.status.value = item.status || 'publish';
            updateArticlePreview();
            form.action = '{{ route('berita.edit') }}';
            modalTitle.textContent = text.editTitle;
            submitBtn.textContent = text.update;
        });
    });

    form.addEventListener('submit', async function (event) {
        const judul = fields.judul.value.trim();
        const link = fields.link.value.trim();
        if (!judul || !link || !fields.status.value) {
            event.preventDefault();
            alert(text.requiredError);
            return;
        }
        if (!/^https?:\/\//i.test(link)) {
            event.preventDefault();
            alert(text.urlError);
            return;
        }

        event.preventDefault();
        submitBtn.disabled = true;
        await updateArticlePreview();
        if (!hiddenImage.value) {
            alert(text.previewError);
            submitBtn.disabled = false;
            return;
        }
        HTMLFormElement.prototype.submit.call(form);
    });
});
</script>
@endsection