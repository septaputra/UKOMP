@extends('layouts.app')
@section('title', __('users.title'))
@section('content')
<div x-data="usersApp()" class="space-y-4">
    <div class="d-flex flex-wrap users-toolbar justify-content-between align-items-center">
        <div class="input-group users-search">
            <span class="input-group-text"><i class="fa fa-search"></i></span>
            <input x-model="query" placeholder="{{ __('users.search_placeholder') }}" class="form-control shadow-sm">
        </div>
        <div class="users-secondary-text small">{{ __('users.description') }}</div>
    </div>

    <div class="card border-0 rounded-4 shadow-sm users-card">
        <div class="card-body">
            <div class="table-responsive">
                <table class="table align-middle mb-0 users-table">
                    <thead>
                        <tr>
                            <th class="user-profile-column">{{ __('users.profile') }}</th>
                            <th class="user-status-column">{{ __('users.status') }}</th>
                            <th class="user-provider-column">{{ __('users.provider') }}</th>
                            <th class="user-joined-column">{{ __('users.joined') }}</th>
                        </tr>
                    </thead>
                    <tbody>
                        <template x-for="u in filtered" :key="u.id">
                            <tr>
                                <td>
                                    <div class="d-flex align-items-center gap-3">
                                        <img :src="u.foto || defaultAvatar" x-on:error="$el.onerror = null; $el.src = defaultAvatar" alt="{{ __('users.profile') }}" class="rounded-circle" width="48" height="48">
                                        <div>
                                            <div class="fw-semibold users-main-text" x-text="u.nama"></div>
                                            <div class="users-secondary-text small" x-text="u.email"></div>
                                        </div>
                                    </div>
                                </td>
                                <td>
                                    <span class="badge text-bg-success" x-text="statusLabels[u.status] || statusLabels.aktif"></span>
                                </td>
                                <td class="users-main-text" x-text="providerLabels[u.provider] || u.provider"></td>
                                <td class="users-main-text" x-text="u.joined_formatted"></td>
                            </tr>
                        </template>
                    </tbody>
                </table>
            </div>

            <div x-show="filtered.length === 0" class="text-center py-5 users-secondary-text">
                {{ __('users.no_results') }}
            </div>
        </div>
    </div>
</div>

<script>
function usersApp(){
    return {
        items: @json($users),
        statusLabels: @json(__('users.statuses')),
        providerLabels: @json(__('users.providers')),
        defaultAvatar: @js(asset('images/user-placeholder.svg')),
        query:'',
        get filtered(){
            if(!this.query) return this.items;
            return this.items.filter(x=> (x.nama+" "+x.email+" "+x.provider+" "+x.status).toLowerCase().includes(this.query.toLowerCase()));
        }
    }
}
</script>

@endsection
