@extends('layouts.app')
@section('title','Notes')
@section('content')
<div x-data="notesApp()" x-init="init()" class="space-y-4">
    <div class="flex items-center justify-between">
        <div class="flex items-center gap-3">
            <input x-model="query" placeholder="Search notes..." class="p-2 border rounded-lg" />
        </div>
        <div>
            <button @click="openAdd=true" class="px-4 py-2 bg-orange-500 text-white rounded-lg">+ Add Note</button>
        </div>
    </div>

    <template x-if="filtered.length==0">
        <div class="p-6 bg-white rounded-xl shadow-sm text-center text-gray-500">Empty state — no notes found.</div>
    </template>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <template x-for="note in filtered" :key="note.id">
            <div class="p-4 bg-white rounded-xl shadow-sm">
                <div class="flex justify-between items-start">
                    <div>
                        <div class="font-semibold" x-text="note.judul"></div>
                        <div class="text-sm text-gray-500" x-text="note.tanggal"></div>
                        <div class="mt-2 text-sm text-gray-700" x-text="note.isi"></div>
                    </div>
                    <div class="flex flex-col gap-2">
                        <button @click="startEdit(note)" class="text-blue-600">✏️</button>
                        <form method="POST" action="/notes/delete" onsubmit="return confirm('Hapus note?')">
                            @csrf
                            <input type="hidden" name="id" :value="note.id">
                            <button class="text-red-600">🗑️</button>
                        </form>
                    </div>
                </div>
            </div>
        </template>
    </div>

    <!-- Add Modal -->
    <div x-show="openAdd" x-cloak class="fixed inset-0 bg-black/40 flex items-center justify-center">
        <div @click.away="openAdd=false" x-transition class="bg-white p-6 rounded-xl w-full max-w-lg">
            <h3 class="font-semibold mb-3">Tambah Note</h3>
            <form method="POST" action="/notes/add" class="space-y-3">
                @csrf
                <div>
                    <label class="text-sm">Judul</label>
                    <input name="judul" required class="w-full p-2 border rounded-lg">
                </div>
                <div>
                    <label class="text-sm">Isi</label>
                    <textarea name="isi" required class="w-full p-2 border rounded-lg"></textarea>
                </div>
                <div>
                    <label class="text-sm">Tanggal</label>
                    <input name="tanggal" type="date" required class="w-full p-2 border rounded-lg">
                </div>
                <div class="flex justify-end gap-2">
                    <button type="button" @click="openAdd=false" class="px-4 py-2">Batal</button>
                    <button class="px-4 py-2 bg-orange-500 text-white rounded-lg">Simpan</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Edit Modal -->
    <div x-show="openEdit" x-cloak class="fixed inset-0 bg-black/40 flex items-center justify-center">
        <div @click.away="openEdit=false" x-transition class="bg-white p-6 rounded-xl w-full max-w-lg">
            <h3 class="font-semibold mb-3">Edit Note</h3>
            <form method="POST" action="/notes/edit" class="space-y-3">
                @csrf
                <input type="hidden" name="id" x-model="editing.id">
                <div>
                    <label class="text-sm">Judul</label>
                    <input name="judul" x-model="editing.judul" required class="w-full p-2 border rounded-lg">
                </div>
                <div>
                    <label class="text-sm">Isi</label>
                    <textarea name="isi" x-model="editing.isi" required class="w-full p-2 border rounded-lg"></textarea>
                </div>
                <div>
                    <label class="text-sm">Tanggal</label>
                    <input name="tanggal" type="date" x-model="editing.tanggal" required class="w-full p-2 border rounded-lg">
                </div>
                <div class="flex justify-end gap-2">
                    <button type="button" @click="openEdit=false" class="px-4 py-2">Batal</button>
                    <button class="px-4 py-2 bg-orange-500 text-white rounded-lg">Simpan</button>
                </div>
            </form>
        </div>
    </div>

</div>

<script>
function notesApp(){
    return {
        notes: @json($notes),
        query: '',
        openAdd:false,
        openEdit:false,
        editing: {id:null, judul:'', isi:'', tanggal:''},
        init(){},
        startEdit(note){ this.editing = JSON.parse(JSON.stringify(note)); this.openEdit = true; },
        get filtered(){
            if(!this.query) return this.notes;
            return this.notes.filter(n=> (n.judul+" "+n.isi).toLowerCase().includes(this.query.toLowerCase()));
        }
    }
}
</script>

@endsection
