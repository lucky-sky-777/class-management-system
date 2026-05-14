# User Authentication & Initialization Guide

## Tổng Quan

Hệ thống quản lý user hiện tại bao gồm:
- **`useFetchCurrentUser`** Hook: Tự động lấy thông tin user từ API `/api/auth/user`
- **`CurrentUserInitializer`** Component: Component wrapper để initialize user data
- **`AppBootstrap`** Component: Component bootstrap cho ứng dụng
- **`useAuthStore`** Store: Global store lưu trữ thông tin user

## Cách Hoạt Động

```
App Start
    ↓
AppProvider (app/providers/index.tsx)
    ↓
AppBootstrap (app/providers/AppBootstrap.tsx)
    ↓
CurrentUserInitializer (features/auth/components/CurrentUserInitializer.tsx)
    ↓
useFetchCurrentUser hook (features/auth/hooks/useFetchCurrentUser.ts)
    ↓
authApi.getMe() → /api/auth/user
    ↓
Save to useAuthStore
```

## Sử Dụng

### 1. Hook: `useFetchCurrentUser`

```typescript
import { useFetchCurrentUser } from '@features/auth';

function MyComponent() {
    const { user, isLoading, error, refetch } = useFetchCurrentUser();

    if (isLoading) return <div>Loading...</div>;
    if (error) return <div>Error: {error.message}</div>;

    return (
        <div>
            <h1>Welcome, {user?.displayName}</h1>
            <button onClick={refetch}>Refresh User</button>
        </div>
    );
}
```

**Return values:**
- `user`: User object từ store (null nếu chưa load)
- `isLoading`: Boolean - đang fetch data
- `error`: Error object nếu có lỗi, null nếu thành công
- `refetch`: Function - fetch lại user data

### 2. Component: `CurrentUserInitializer`

```typescript
import { CurrentUserInitializer } from '@features/auth';

// Thường sử dụng ở mức cao của ứng dụng
export function App() {
    return (
        <CurrentUserInitializer>
            <YourApp />
        </CurrentUserInitializer>
    );
}
```

**Props:**
- `children`: ReactNode - các component bên trong

**Hành vi:**
- Tự động gọi `useFetchCurrentUser` để fetch user khi mount
- Hiển thị loading message nếu setting `if (isLoading)`
- Không block app nếu có error (chỉ log warning)

### 3. Store: `useAuthStore`

```typescript
import { useAuthStore } from '@features/auth/hooks/useAuthStore';

function MyComponent() {
    const { user, isAuthenticated, setUser, logout } = useAuthStore();

    return (
        <div>
            {isAuthenticated ? (
                <>
                    <p>User: {user?.username}</p>
                    <button onClick={logout}>Logout</button>
                </>
            ) : (
                <p>Not authenticated</p>
            )}
        </div>
    );
}
```

**Methods:**
- `setUser(user)`: Cập nhật user vào store
- `logout()`: Xóa user khỏi store

## File Structure

```
frontend/src/features/auth/
├── api/
│   └── index.ts           # authApi.getMe() endpoint
├── components/
│   ├── index.ts           # Exports
│   └── CurrentUserInitializer.tsx    # Initializer component
├── hooks/
│   ├── useAuth.ts         # Public hook
│   ├── useAuthStore.ts    # Zustand store
│   ├── useFetchCurrentUser.ts    # NEW - Fetch user hook
│   └── useAuthInternal.ts
├── types/
│   └── index.ts           # UserResponse type
└── index.ts               # Feature exports

app/providers/
├── index.tsx              # AppProvider (updated)
├── AppBootstrap.tsx       # NEW - Bootstrap component
└── providers.tsx
```

## Integration Points

### 1. **Main Entry** (app/index.tsx)
```
main.tsx → app/index.tsx → AppProvider (app/providers/index.tsx)
                                     ↓
                              AppBootstrap
                                     ↓
                         CurrentUserInitializer
                                     ↓
                        (tự động fetch user)
```

### 2. **Protected Routes**
```typescript
// Nếu sử dụng protected routes, có thể kiểm tra isAuthenticated
import { useAuth } from '@features/auth';

function ProtectedComponent() {
    const { isAuthenticated, user } = useAuth();
    
    if (!isAuthenticated) return <Navigate to="/login" />;
    
    return <div>User: {user?.displayName}</div>;
}
```

## API Response Mapping

Backend trả về `UserResponseDto`:
```json
{
    "id": "123",
    "username": "john_doe",
    "display_name": "John Doe",
    "avatar_url": "https://...",
    "phone": "0123456789",
    "email": "john@example.com",
    "joined_at": "2024-01-01T00:00:00Z"
}
```

Frontend chuyển đổi thành `User` domain object:
```typescript
{
    id: "123",
    username: "john_doe",
    displayName: "John Doe",
    avatarUrl: "https://...",
    phone: "0123456789",
    email: "john@example.com",
    joinedAt: "2024-01-01T00:00:00Z"
}
```

## Error Handling

### API Call Fails
- Hook set `error` state
- Component hiển thị warning (không block app)
- User có thể retry bằng `refetch()`

### Network Issues
- Authorization header tự động thêm token từ localStorage
- Token validation tự động bởi interceptor
- Nếu token hết hạn, tự động logout

## Tùy Chỉnh Loading UI

Nếu muốn tùy chỉnh loading state của `CurrentUserInitializer`, chỉnh sửa file:

```typescript
// features/auth/components/CurrentUserInitializer.tsx
if (isLoading) {
    return <YourCustomLoadingComponent />;
}
```

## Testing

```typescript
import { renderHook, waitFor } from '@testing-library/react';
import { useFetchCurrentUser } from '@features/auth';

test('should fetch user on mount', async () => {
    const { result } = renderHook(() => useFetchCurrentUser());
    
    await waitFor(() => {
        expect(result.current.isLoading).toBe(false);
    });
    
    expect(result.current.user).toBeDefined();
});
```

## Next Steps

1. **Nếu backend yêu cầu refresh token**: Cập nhật interceptor trong `api-client.ts`
2. **Nếu cần handle user roles/permissions**: Mở rộng `User` type
3. **Nếu cần pre-fetch thêm data**: Thêm vào `CurrentUserInitializer`
