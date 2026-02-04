@extends('layouts.app')
@section('title','Schedule')
@section('content')

<div class="d-flex justify-content-between align-items-center mb-4">
    <div class="input-group" style="max-width:360px;">
      <span class="input-group-text bg-white border-0"><i class="fa fa-search text-muted"></i></span>
      <input id="scheduleSearch" type="search" class="form-control shadow-sm rounded-pill border-0" placeholder="Search schedule..." style="box-shadow:none" aria-label="Search schedule">
    </div>
    <button class="btn btn-warning text-white d-flex align-items-center" data-bs-toggle="modal" data-bs-target="#addScheduleModal"><i class="fa fa-plus me-2"></i> Tambah Jadwal</button>
</div>

<div class="table-responsive bg-white rounded-3 shadow-sm p-3">
    <table class="table table-borderless mb-0 align-middle">
        <thead class="small text-muted">
            <tr><th>Mata Pelajaran</th><th>Hari/Tanggal</th><th>Jam</th><th>Catatan</th><th class="text-end">Aksi</th></tr>
        </thead>
        <tbody>
          @foreach($schedule as $s)
          <tr class="align-top border-top">
            <td class="py-3">{{ $s['mata_pelajaran'] }}</td>
            <td class="py-3">
              <div class="fw-semibold">{{ $s['hari'] ?? '' }}</div>
              @if(!empty($s['tanggal']))
                <div class="text-muted small">{{ \Carbon\Carbon::parse($s['tanggal'])->format('d/m/Y') }}</div>
              @endif
            </td>
            <td class="py-3">{{ $s['jam'] }}</td>
            <td class="py-3">{{ $s['catatan'] }}</td>
            <td class="py-3 text-end">
              <button type="button" class="btn btn-sm btn-outline-primary me-2 btn-edit"
                data-id="{{ $s['id'] }}"
                data-mata="{{ $s['mata_pelajaran'] }}"
                data-hari="{{ $s['hari'] ?? '' }}"
                data-tanggal="{{ $s['tanggal'] ?? '' }}"
                data-jam="{{ $s['jam'] }}"
                data-catatan="{{ $s['catatan'] ?? '' }}"
              ><i class="fa fa-edit"></i></button>
              <form method="POST" action="/schedule/delete" style="display:inline" onsubmit="return confirm('Hapus jadwal?')">
                @csrf
                <input type="hidden" name="id" value="{{ $s['id'] }}">
                <button class="btn btn-sm btn-outline-danger"><i class="fa fa-trash"></i></button>
              </form>
            </td>
          </tr>
          @endforeach
        </tbody>
    </table>
</div>

<!-- Modal -->
<div class="modal fade" id="addScheduleModal" tabindex="-1" aria-labelledby="addScheduleLabel" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content rounded-4 shadow">
      <div class="modal-header border-0">
        <h5 class="modal-title" id="addScheduleLabel"><i class="fa fa-calendar-check me-2 text-warning"></i> Tambah Jadwal</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <form id="scheduleForm" action="/schedule/add" method="POST">
        @csrf
        <div class="modal-body">
          <div class="mb-3">
            <label class="form-label">Mata Pelajaran</label>
            <input type="text" name="mata_pelajaran" class="form-control rounded-3" placeholder="Contoh: Matematika" required>
          </div>
          <div class="row g-2">
            <div class="col-md-6 mb-3">
              <label class="form-label">Pilih Hari</label>
              <div class="input-group">
                <span class="input-group-text"><i class="fa fa-calendar-day text-muted"></i></span>
                <select name="hari_select" id="hari_select" class="form-select rounded-3">
                    <option value="">-- Pilih hari --</option>
                    <option>Senin</option>
                    <option>Selasa</option>
                    <option>Rabu</option>
                    <option>Kamis</option>
                    <option>Jumat</option>
                    <option>Sabtu</option>
                    <option>Minggu</option>
                </select>
              </div>
              <div class="form-text">Pilih hari atau tanggal pelaksanaan</div>
            </div>

            <div class="col-md-6 mb-3">
              <label class="form-label">Tanggal (opsional)</label>
              <div class="input-group">
                <span class="input-group-text"><i class="fa fa-calendar text-muted"></i></span>
                <input type="date" name="tanggal" id="tanggal" class="form-control rounded-3" aria-describedby="hariHelp">
              </div>
              <div id="hariHelp" class="form-text">Jika diisi, tanggal akan menentukan hari (override pilihan hari)</div>
            </div>

            <div class="col-md-6 mb-3">
              <label class="form-label">Jam</label>
              <div class="input-group">
                <span class="input-group-text"><i class="fa fa-clock text-muted"></i></span>
                <input type="time" name="jam" id="jam" class="form-control rounded-3" required>
              </div>
              <div class="form-text">Gunakan format 24 jam</div>
            </div>
          </div>

          <div class="mb-3">
            <label class="form-label">Catatan <span class="text-muted small">(opsional)</span></label>
            <textarea name="catatan" class="form-control rounded-3" rows="2" placeholder="Contoh: Bab 1–2 / Praktikum"></textarea>
          </div>
        </div>
        <div class="modal-footer border-0">
          <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Batal</button>
          <button type="submit" class="btn btn-warning text-white">Simpan</button>
        </div>
      </form>
    </div>
  </div>
</div>

<script>
function scheduleApp(){
  return {
    items: @json($schedule),
    query:''
  }
}

document.addEventListener('DOMContentLoaded', function(){
  const scheduleForm = document.getElementById('scheduleForm');
  const addModal = document.getElementById('addScheduleModal');
  const bsModal = new bootstrap.Modal(addModal);

  // handle add button -> clear form
  const addBtn = document.querySelector('[data-bs-target="#addScheduleModal"]');
  if(addBtn){
    addBtn.addEventListener('click', ()=>{
      scheduleForm.action = '/schedule/add';
      scheduleForm.reset();
      const hid = scheduleForm.querySelector('input[name="id"]'); if(hid) hid.remove();
      document.getElementById('addScheduleLabel').innerText = 'Tambah Jadwal';
    });
  }

  // handle edit buttons
  document.querySelectorAll('.btn-edit').forEach(btn=>{
    btn.addEventListener('click', ()=>{
      const id = btn.dataset.id;
      const mata = btn.dataset.mata;
      const hari = btn.dataset.hari;
      const tanggal = btn.dataset.tanggal;
      const jam = btn.dataset.jam;
      const catatan = btn.dataset.catatan;

      scheduleForm.action = '/schedule/edit';
      scheduleForm.querySelector('input[name="mata_pelajaran"]').value = mata;
      if(tanggal){
        document.getElementById('tanggal').value = tanggal;
        document.getElementById('hari_select').value = '';
      } else {
        document.getElementById('tanggal').value = '';
        document.getElementById('hari_select').value = hari || '';
      }
      document.getElementById('jam').value = jam;
      scheduleForm.querySelector('textarea[name="catatan"]').value = catatan;

      // add hidden id
      let hid = scheduleForm.querySelector('input[name="id"]');
      if(!hid){ hid = document.createElement('input'); hid.type='hidden'; hid.name='id'; scheduleForm.appendChild(hid); }
      hid.value = id;

      document.getElementById('addScheduleLabel').innerText = 'Edit Jadwal';
      bsModal.show();
    });
  });

  // client-side: ensure either hari_select or tanggal filled
  scheduleForm.addEventListener('submit', function(e){
    const hariSel = document.getElementById('hari_select').value.trim();
    const tanggalVal = document.getElementById('tanggal').value.trim();
    if(!hariSel && !tanggalVal){
      e.preventDefault();
      alert('Silakan pilih hari atau isi tanggal pelaksanaan.');
      return false;
    }
    return true;
  });

  // client-side search/filter for schedule table
  const searchInput = document.getElementById('scheduleSearch');
  const tableBody = document.querySelector('.table-responsive table tbody');
  if(searchInput && tableBody){
    searchInput.addEventListener('input', function(){
      const q = this.value.trim().toLowerCase();
      const rows = tableBody.querySelectorAll('tr');
      if(q === ''){
        rows.forEach(r=> r.style.display='');
        return;
      }
      rows.forEach(r=>{
        const text = r.textContent.replace(/\s+/g,' ').toLowerCase();
        if(text.indexOf(q) !== -1) r.style.display = '';
        else r.style.display = 'none';
      });
    });
  }
});
</script>

@endsection
