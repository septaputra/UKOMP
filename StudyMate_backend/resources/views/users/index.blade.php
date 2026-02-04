@extends('layouts.app')
@section('title','Users')
@section('content')
<div x-data="usersApp()" class="space-y-4">
    <div class="flex items-center justify-between">
        <input x-model="query" placeholder="Search users..." class="p-2 border rounded-lg" />
        <button @click="openAdd=true" class="px-4 py-2 bg-orange-500 text-white rounded-lg">+ Add User</button>
    </div>

    <div class="bg-white rounded-xl shadow-sm p-4">
        <table class="w-full text-sm">
            <thead>
                <tr class="text-left text-gray-500"><th>Nama</th><th>Email</th><th>Role</th><th></th></tr>
            </thead>
            <tbody>
                <template x-for="u in filtered" :key="u.id">
                    <tr class="border-t">
                        <td x-text="u.nama" class="py-2"></td>
                        <td x-text="u.email"></td>
                        <td x-text="u.role"></td>
                        <td class="text-right">
                            <button @click="startEdit(u)" class="text-blue-600 mr-2">✏️</button>
                            <form method="POST" action="/users/delete" style="display:inline" onsubmit="return confirm('Hapus user?')">
                                @csrf
                                <input type="hidden" name="id" :value="u.id">
                                <button class="text-red-600">🗑️</button>
                            </form>
                        </td>
                    </tr>
                </template>
            </tbody>
        </table>
    </div>

    <!-- Add/Edit Modal -->
    <div x-show="openAdd || openEdit" x-cloak class="fixed inset-0 bg-black/40 flex items-center justify-center">
        <div class="bg-white p-6 rounded-xl w-full max-w-lg">
            <h3 class="font-semibold mb-3" x-text="openAdd ? 'Tambah User' : 'Edit User'"></h3>
            <form :action="openAdd ? '/users/add' : '/users/edit'" method="POST" class="space-y-3">
                @csrf
                <input type="hidden" name="id" x-show="openEdit" x-model="editing.id">
                <div>
                    <label class="text-sm">Nama</label>
                    <input name="nama" x-model="editing.nama" required class="w-full p-2 border rounded-lg">
                </div>
                <div>
                    <label class="text-sm">Email</label>
                    <input name="email" x-model="editing.email" type="email" required class="w-full p-2 border rounded-lg">
                </div>
                <div>
                    <label class="text-sm">Role</label>
                    <select name="role" x-model="editing.role" class="w-full p-2 border rounded-lg">
                        <option value="admin">admin</option>
                        <option value="teacher">teacher</option>
                        <option value="student">student</option>
                    </select>
                </div>
                <div class="flex justify-end gap-2">
                    <button type="button" @click="openAdd=false; openEdit=false" class="px-4 py-2">Batal</button>
                    <button class="px-4 py-2 bg-orange-500 text-white rounded-lg">Simpan</button>
                </div>
            </form>
        </div>
    </div>

</div>

<script>
function usersApp(){
    return {
        items: @json($users),
        query:'',
        openAdd:false,
        openEdit:false,
        editing:{id:null,nama:'',email:'',role:'student'},
        startEdit(u){ this.editing = JSON.parse(JSON.stringify(u)); this.openEdit=true; },
        get filtered(){
            if(!this.query) return this.items;
            return this.items.filter(x=> (x.nama+" "+x.email+" "+x.role).toLowerCase().includes(this.query.toLowerCase()));
        }
    }
}
</script>

@endsection
