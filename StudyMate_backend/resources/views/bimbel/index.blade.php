@extends('layouts.app')
@section('title', __('tutoring.title'))
@section('content')
<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <div class="text-muted small">{{ __('tutoring.description') }}</div>
        <h4 class="mb-0 fw-semibold">{{ __('tutoring.title') }}</h4>
    </div>
    <button type="button" class="btn btn-warning" data-bs-toggle="modal" data-bs-target="#bimbelModal" id="bimbelCreateBtn">
        <i class="fa fa-plus me-2"></i>{{ __('tutoring.add') }}
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
            <div class="card border-0 rounded-4 shadow-sm h-100">
                <div class="card-body d-flex gap-3">
                    <img src="{{ $item['logo'] ?? 'https://picsum.photos/seed/bimbel-default/120/120' }}" class="rounded-4" width="84" height="84" alt="logo">
                    <div class="flex-grow-1">
                        <div class="fw-semibold">{{ $item['nama'] }}</div>
                        <div class="text-muted small">{{ $item['kota'] }}, {{ $item['provinsi'] }} · {{ $item['jenjang'] }}</div>
                        <div class="small text-muted mt-2">{{ __('tutoring.maps_link') }}: <a href="{{ $item['maps'] ?? 'https://maps.google.com' }}" target="_blank" rel="noopener">{{ __('common.open') }}</a> · {{ __('tutoring.website') }}:
                            @if(!empty($item['website']))
                                <a href="{{ $item['website'] }}" target="_blank" rel="noopener">{{ __('common.open') }}</a>
                            @else
                                <span>{{ __('tutoring.no_website') }}</span>
                            @endif
                        </div>
                        <div class="d-flex gap-2 mt-3">
                            <button type="button" class="btn btn-sm btn-outline-primary" data-bimbel-edit data-item='@json($item)' data-bs-toggle="modal" data-bs-target="#bimbelModal">{{ __('common.edit') }}</button>
                            <form method="POST" action="{{ route('bimbel.delete') }}" onsubmit="return confirm(@json(__('tutoring.delete_confirm')))" class="d-inline">
                                @csrf
                                <input type="hidden" name="id" value="{{ $item['id'] }}">
                                <button type="submit" class="btn btn-sm btn-outline-danger">{{ __('common.delete') }}</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    @empty
        <div class="col-12"><div class="text-center py-5 text-muted">{{ __('common.empty_state') }}</div></div>
    @endforelse
</div>

<div class="modal fade" id="bimbelModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg">
            <form id="bimbelForm" method="POST" action="{{ route('bimbel.add') }}">
                @csrf
                <input type="hidden" name="id" id="bimbel_id">
                <div class="modal-header border-0">
                    <h5 class="modal-title" id="bimbelModalTitle">{{ __('tutoring.create_title') }}</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="{{ __('common.close') }}"></button>
                </div>
                <div class="modal-body">
                    <div class="row g-3">
                        <div class="col-12">
                            <label class="form-label">{{ __('tutoring.institution_name') }}</label>
                            <input type="text" class="form-control" name="nama" id="bimbel_nama" required>
                        </div>
                        <div class="col-12">
                            <label class="form-label">{{ __('tutoring.address') }}</label>
                            <input type="text" class="form-control" name="alamat" id="bimbel_alamat" placeholder="{{ __('tutoring.address_placeholder') }}">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">{{ __('tutoring.province') }}</label>
                            <select class="form-select" name="provinsi" id="bimbel_provinsi" required>
                                <option value="">{{ __('tutoring.choose_province') }}</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">{{ __('tutoring.city') }}</label>
                            <select class="form-select" name="kota" id="bimbel_kota" required>
                                <option value="">{{ __('tutoring.choose_city_after_province') }}</option>
                            </select>
                        </div>
                        <div class="col-12">
                            <label class="form-label">{{ __('tutoring.description_label') }}</label>
                            <textarea class="form-control" name="deskripsi" id="bimbel_deskripsi" rows="3"></textarea>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">{{ __('tutoring.contact') }}</label>
                            <input type="text" class="form-control" name="kontak" id="bimbel_kontak" placeholder="0812-...">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">{{ __('tutoring.level') }}</label>
                            <select class="form-select" name="jenjang" id="bimbel_jenjang" required>
                                <option value="">{{ __('tutoring.choose_level') }}</option>
                                <option value="SD">SD</option>
                                <option value="SMP">SMP</option>
                                <option value="SMA">SMA</option>
                                <option value="SMK">SMK</option>
                                <option value="Kuliah">{{ __('tutoring.college') }}</option>
                                <option value="SD-SMP">SD-SMP</option>
                                <option value="SMP-SMA">SMP-SMA</option>
                                <option value="SD-SMA">SD-SMA</option>
                            </select>
                        </div>
                        <div class="col-12">
                            <label class="form-label">{{ __('tutoring.logo') }}</label>
                            <div class="image-upload-box">
                                <div class="image-upload-preview-wrap">
                                    <img id="bimbel_image_preview" class="image-upload-preview d-none" alt="Preview logo bimbel">
                                    <div id="bimbel_image_placeholder" class="image-upload-placeholder">
                                        <i class="fa fa-image"></i>
                                        <span>{{ __('common.no_image') }}</span>
                                    </div>
                                </div>
                                <div class="image-upload-meta">
                                    <div id="bimbel_image_name" class="image-upload-file-name">{{ __('common.no_file') }}</div>
                                    <div class="image-upload-actions">
                                        <input type="file" id="bimbel_image_file" class="image-upload-input" accept="image/jpeg,image/jpg,image/png,image/webp">
                                        <button type="button" class="image-upload-button" data-trigger="bimbel_image_file"><i class="fa fa-upload"></i> {{ __('common.choose_image') }}</button>
                                        <button type="button" id="bimbel_image_clear" class="image-upload-clear" type="button"><i class="fa fa-trash"></i> {{ __('common.delete') }}</button>
                                    </div>
                                </div>
                                <input type="hidden" name="logo" id="bimbel_logo" value="">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">{{ __('tutoring.maps_link') }}</label>
                            <input type="url" class="form-control" name="maps" id="bimbel_maps" placeholder="https://maps.google.com/...">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">{{ __('tutoring.website') }}</label>
                            <input type="url" class="form-control" name="website" id="bimbel_website" placeholder="https://example.com">
                        </div>
                    </div>
                </div>
                <div class="modal-footer border-0">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">{{ __('common.cancel') }}</button>
                    <button type="submit" class="btn btn-warning" id="bimbelSubmit">{{ __('common.save') }}</button>
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
        'createTitle' => __('tutoring.create_title'),
        'editTitle' => __('tutoring.edit_title'),
        'save' => __('common.save'),
        'update' => __('common.update'),
        'requiredError' => __('tutoring.required_error'),
        'mapsError' => __('tutoring.maps_error'),
        'websiteError' => __('tutoring.website_error'),
        'chooseProvince' => __('tutoring.choose_province'),
        'chooseCity' => __('tutoring.choose_city'),
        'chooseCityAfterProvince' => __('tutoring.choose_city_after_province'),
        'loading' => __('tutoring.loading_regions'),
        'regionLoadError' => __('tutoring.region_load_error'),
    ]) }};
    const form = document.getElementById('bimbelForm');
    const modalTitle = document.getElementById('bimbelModalTitle');
    const submitBtn = document.getElementById('bimbelSubmit');
    const fileInput = document.getElementById('bimbel_image_file');
    const hiddenImage = document.getElementById('bimbel_logo');
    const preview = document.getElementById('bimbel_image_preview');
    const placeholder = document.getElementById('bimbel_image_placeholder');
    const fileName = document.getElementById('bimbel_image_name');
    const fields = {
        id: document.getElementById('bimbel_id'),
        nama: document.getElementById('bimbel_nama'),
        alamat: document.getElementById('bimbel_alamat'),
        kota: document.getElementById('bimbel_kota'),
        provinsi: document.getElementById('bimbel_provinsi'),
        deskripsi: document.getElementById('bimbel_deskripsi'),
        kontak: document.getElementById('bimbel_kontak'),
        jenjang: document.getElementById('bimbel_jenjang'),
        maps: document.getElementById('bimbel_maps'),
        website: document.getElementById('bimbel_website')
    };
    const regenciesUrl = {{ \Illuminate\Support\Js::from(route('bimbel.regencies', ['provinceCode' => '__PROVINCE_CODE__'])) }};
    const fallbackProvinces = [
        { code: '11', name: 'Aceh' }, { code: '12', name: 'Sumatera Utara' }, { code: '13', name: 'Sumatera Barat' },
        { code: '14', name: 'Riau' }, { code: '15', name: 'Jambi' }, { code: '16', name: 'Sumatera Selatan' },
        { code: '17', name: 'Bengkulu' }, { code: '18', name: 'Lampung' }, { code: '19', name: 'Kepulauan Bangka Belitung' },
        { code: '21', name: 'Kepulauan Riau' }, { code: '31', name: 'DKI Jakarta' }, { code: '32', name: 'Jawa Barat' },
        { code: '33', name: 'Jawa Tengah' }, { code: '34', name: 'Daerah Istimewa Yogyakarta' }, { code: '35', name: 'Jawa Timur' },
        { code: '36', name: 'Banten' }, { code: '51', name: 'Bali' }, { code: '52', name: 'Nusa Tenggara Barat' },
        { code: '53', name: 'Nusa Tenggara Timur' }, { code: '61', name: 'Kalimantan Barat' }, { code: '62', name: 'Kalimantan Tengah' },
        { code: '63', name: 'Kalimantan Selatan' }, { code: '64', name: 'Kalimantan Timur' }, { code: '65', name: 'Kalimantan Utara' },
        { code: '71', name: 'Sulawesi Utara' }, { code: '72', name: 'Sulawesi Tengah' }, { code: '73', name: 'Sulawesi Selatan' },
        { code: '74', name: 'Sulawesi Tenggara' }, { code: '75', name: 'Gorontalo' }, { code: '76', name: 'Sulawesi Barat' },
        { code: '81', name: 'Maluku' }, { code: '82', name: 'Maluku Utara' }, { code: '91', name: 'Papua' },
        { code: '92', name: 'Papua Barat' }, { code: '93', name: 'Papua Selatan' }, { code: '94', name: 'Papua Tengah' },
        { code: '95', name: 'Papua Pegunungan' }, { code: '96', name: 'Papua Barat Daya' }
    ];

    function setSelectPlaceholder(select, label) {
        select.replaceChildren(new Option(label, ''));
    }

    function loadProvinces(selectedProvince = '') {
        setSelectPlaceholder(fields.provinsi, text.chooseProvince);
        fallbackProvinces.sort((first, second) => first.name.localeCompare(second.name, 'id'));
        fallbackProvinces.forEach(function (province) {
            const option = new Option(province.name, province.name);
            option.dataset.id = province.code;
            fields.provinsi.add(option);
        });
        const matchedProvince = fallbackProvinces.find(function (province) {
            return province.name.toLocaleLowerCase('id') === selectedProvince.toLocaleLowerCase('id');
        });
        fields.provinsi.value = matchedProvince ? matchedProvince.name : selectedProvince;
        fields.provinsi.disabled = false;
        return Promise.resolve(fallbackProvinces);
    }

    async function loadCities(provinceId, selectedCity = '') {
        setSelectPlaceholder(fields.kota, text.chooseCity);
        if (!provinceId) {
            setSelectPlaceholder(fields.kota, text.chooseCityAfterProvince);
            return;
        }
        fields.kota.options[0].textContent = text.loading;
        try {
            const endpoint = regenciesUrl.replace('__PROVINCE_CODE__', encodeURIComponent(provinceId));
            const response = await fetch(endpoint, { headers: { Accept: 'application/json' } });
            if (!response.ok) throw new Error('City request failed');
            const result = await response.json();
            const cities = result.data;
            if (!Array.isArray(cities) || cities.length === 0) throw new Error('City list is empty');
            cities.sort((first, second) => first.name.localeCompare(second.name, 'id'));
            setSelectPlaceholder(fields.kota, text.chooseCity);
            cities.forEach(function (city) {
                fields.kota.add(new Option(city.name, city.name));
            });
            const matchedCity = cities.find(function (city) {
                return city.name.toLocaleLowerCase('id') === selectedCity.toLocaleLowerCase('id');
            });
            const cityValue = matchedCity ? matchedCity.name : selectedCity;
            if (selectedCity && !matchedCity) {
                fields.kota.add(new Option(selectedCity, selectedCity));
            }
            fields.kota.value = cityValue;
        } catch (error) {
            setSelectPlaceholder(fields.kota, text.regionLoadError);
        }
    }

    const provincesReady = loadProvinces();
    fields.provinsi.addEventListener('change', function () {
        const selectedOption = fields.provinsi.selectedOptions[0];
        loadCities(selectedOption ? selectedOption.dataset.id : '');
    });

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
        document.querySelector('[data-trigger="bimbel_image_file"]').addEventListener('click', function () {
            fileInput.click();
        });

        document.getElementById('bimbel_image_clear').addEventListener('click', function () {
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
        fields.nama.value = '';
        fields.alamat.value = '';
        fields.kota.value = '';
        fields.provinsi.value = '';
        setSelectPlaceholder(fields.kota, text.chooseCityAfterProvince);
        fields.deskripsi.value = '';
        fields.kontak.value = '';
        fields.jenjang.value = '';
        hiddenImage.value = '';
        fields.maps.value = '';
        fields.website.value = '';
        updateImagePreview('');
        form.action = '{{ route('bimbel.add') }}';
        modalTitle.textContent = text.createTitle;
        submitBtn.textContent = text.save;
    }

    setupImageUploader();
    updateImagePreview(hiddenImage.value || '');

    document.getElementById('bimbelCreateBtn').addEventListener('click', function () {
        resetForm();
    });

    document.querySelectorAll('[data-bimbel-edit]').forEach(function (button) {
        button.addEventListener('click', async function () {
            const item = JSON.parse(this.dataset.item || '{}');
            await provincesReady;
            fields.id.value = item.id || '';
            fields.nama.value = item.nama || '';
            fields.alamat.value = item.alamat || '';
            fields.provinsi.value = item.provinsi || '';
            const selectedProvince = fields.provinsi.selectedOptions[0];
            await loadCities(selectedProvince ? selectedProvince.dataset.id : '', item.kota || '');
            fields.deskripsi.value = item.deskripsi || '';
            fields.kontak.value = item.kontak || '';
            fields.jenjang.value = item.jenjang || '';
            hiddenImage.value = item.logo || '';
            fields.maps.value = item.maps || '';
            fields.website.value = item.website || '';
            updateImagePreview(item.logo || '');
            form.action = '{{ route('bimbel.edit') }}';
            modalTitle.textContent = text.editTitle;
            submitBtn.textContent = text.update;
        });
    });

    form.addEventListener('submit', function (event) {
        const nama = fields.nama.value.trim();
        const kota = fields.kota.value.trim();
        const provinsi = fields.provinsi.value.trim();
        const jenjang = fields.jenjang.value.trim();
        const logo = hiddenImage.value.trim();
        if (!nama || !kota || !provinsi || !jenjang || !logo) {
            event.preventDefault();
            alert(text.requiredError);
            return;
        }
        if (fields.maps.value && !/^https?:\/\//i.test(fields.maps.value.trim())) {
            event.preventDefault();
            alert(text.mapsError);
            return;
        }
        if (fields.website.value && !/^https?:\/\//i.test(fields.website.value.trim())) {
            event.preventDefault();
            alert(text.websiteError);
        }
    });
});
</script>
@endsection