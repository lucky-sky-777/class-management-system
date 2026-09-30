# Tài Liệu Tích Hợp Module Bạn Bè & Theo Dõi (Social Follow Graph API Guide)

Tài liệu kỹ thuật hướng dẫn lập trình viên Frontend tích hợp hệ thống **Người theo dõi (Followers)**, **Đang theo dõi (Followings)** và **Bạn bè (Friends)** theo mô hình mạng xã hội hai chiều (Mutual Follow).

Hệ thống được tổ chức phân rã độc lập thành 3 thư mục tương ứng:
- `/friend/follower`: Quản lý danh sách người theo dõi (`/api/followers`)
- `/friend/following`: Quản lý danh sách đang theo dõi & hành động Follow/Unfollow (`/api/followings`)
- `/friend/friend`: Quản lý bạn bè chính thức (Mutual Follow) & thống kê quan hệ (`/api/friends`)

---

## 1. Cơ Chế Hoạt Động Cốt Lõi (Social Follow Graph)

1. **Theo dõi 1 chiều (One-way Follow)**:
   - Bất kỳ người dùng nào cũng có thể theo dõi người khác ngay lập tức (không cần chờ đối phương duyệt).
   - Khi A follow B: A có thêm 1 `following`, B có thêm 1 `follower`.
2. **Quan hệ Bạn bè (Mutual Follow = Friends)**:
   - Hai người **chỉ trở thành Bạn bè** khi và chỉ khi **cả hai cùng follow lẫn nhau**.
   - Nếu A đã follow B, và B bấm follow lại A -> Hệ thống tự động xác lập quan hệ Bạn bè (`Friend`).
3. **Hủy theo dõi (Unfollow)**:
   - Nếu A unfollow B: Quan hệ Bạn bè (`Friend`) giữa 2 người lập tức bị **hủy bỏ** (xóa khỏi bảng bạn bè). Tuy nhiên, nếu B vẫn đang follow A thì chiều B follow A vẫn được giữ nguyên.
4. **Gỡ người theo dõi (Remove Follower)**:
   - B có quyền gỡ A khỏi danh sách follower của mình. Nếu hai người đang là bạn bè, hành động này cũng tự động hủy quan hệ bạn bè.

---

## 2. Máy Trạng Thái Nút Bấm Trên UI (UI State Machine)

Gọi `GET /api/friends/status/{targetUserId}` để lấy `status` và hiển thị nút bấm tương ứng trên User Card / Profile:

| `status` trả về | Giao diện nút bấm đề xuất | Ý nghĩa | Hành động khi click | Gọi API |
| :--- | :--- | :--- | :--- | :--- |
| `SELF` | Không hiển thị nút | Trang cá nhân chính mình | - | - |
| `NONE` | Nút **"Theo dõi"** (+ Follow) | Chưa ai follow ai | Bắt đầu theo dõi | `POST /api/followings/{targetUserId}` |
| `FOLLOWING` | Nút **"Đang theo dõi"** (Unfollow) | Mình đang follow người đó (1 chiều) | Hủy theo dõi | `DELETE /api/followings/{targetUserId}` |
| `FOLLOWER` | Nút **"Theo dõi lại"** (Follow Back) | Người đó đang follow mình (chưa follow lại) | Follow lại (trở thành Bạn bè) | `POST /api/followings/{targetUserId}` |
| `FRIEND` | Nút **"Bạn bè"** (Mutual Follow) | Cả 2 cùng follow nhau | Hủy kết bạn / Hủy theo dõi | `DELETE /api/friends/{targetUserId}` hoặc `DELETE /api/followings/{targetUserId}` |

---

## 3. Danh Sách Endpoint & Payload Chi Tiết

Mọi API đều yêu cầu Header: `Authorization: Bearer <accessToken>`.

---

### PHẦN I. ĐANG THEO DÕI (`/api/followings` - Thuộc package `/following`)

#### 1. Theo dõi người dùng (Follow)
* **Method**: `POST`
* **Path**: `/api/followings/{targetUserId}`
* **Response Body (`ResponseDTO<FriendshipStatusResponseDto>`)**:
```json
{
    "success": true,
    "code": 200,
    "message": "Followed successfully",
    "data": {
        "target_user_id": 2,
        "status": "FRIEND"
    },
    "time": "2026-09-30T12:00:00Z"
}
```
> [!NOTE]
> - Nếu đối phương chưa follow bạn: `status` trả về là `"FOLLOWING"`.
> - Nếu đối phương đã follow bạn từ trước: `status` trả về là `"FRIEND"` (Mutual Follow thành công).

#### 2. Hủy theo dõi (Unfollow)
* **Method**: `DELETE`
* **Path**: `/api/followings/{targetUserId}`
* **Lưu ý**: Nếu trước đó là Bạn bè (`Friend`), quan hệ bạn bè sẽ tự động bị hủy.
* **Response Body**:
```json
{
    "success": true,
    "code": 200,
    "message": "Unfollowed successfully",
    "time": "2026-09-30T12:00:00Z"
}
```

#### 3. Lấy danh sách những người mình đang theo dõi (My Following)
* **Method**: `GET`
* **Path**: `/api/followings`
* **Query Params**: `page` (default 0), `size` (default 20), `sort`
* **Response Body (`ResponseDTO<Page<FollowingResponseDto>>`)**:
```json
{
    "success": true,
    "code": 200,
    "message": "Fetch following list successfully",
    "data": {
        "content": [
            {
                "id": 1,
                "target_user": {
                    "id": 2,
                    "username": "nguyenvana",
                    "display_name": "Nguyễn Văn A",
                    "avatar_url": "https://avatar.url/a.jpg"
                },
                "sent_at": "2026-09-30T11:00:00Z"
            }
        ],
        "page": {
            "size": 20,
            "number": 0,
            "totalElements": 1,
            "totalPages": 1
        }
    },
    "time": "2026-09-30T12:00:00Z"
}
```

#### 4. Lấy danh sách đang theo dõi của một user bất kỳ
* **Method**: `GET`
* **Path**: `/api/followings/users/{userId}`
* **Query Params**: `page`, `size`
* **Response Body**: Tương tự như mục 3.

#### 5. Đếm số lượng người mình đang theo dõi
* **Method**: `GET`
* **Path**: `/api/followings/count`
* **Response Body**:
```json
{
    "success": true,
    "code": 200,
    "message": "Fetch following count successfully",
    "data": 15,
    "time": "2026-09-30T12:00:00Z"
}
```

---

### PHẦN II. NGƯỜI THEO DÕI (`/api/followers` - Thuộc package `/follower`)

#### 1. Gỡ một người ra khỏi danh sách người theo dõi (Remove Follower)
* **Method**: `DELETE`
* **Path**: `/api/followers/{followerUserId}`
* **Lưu ý**: Nếu trước đó là bạn bè (`Friend`), quan hệ bạn bè sẽ tự động bị hủy.
* **Response Body**:
```json
{
    "success": true,
    "code": 200,
    "message": "Follower removed successfully",
    "time": "2026-09-30T12:00:00Z"
}
```

#### 2. Lấy danh sách người theo dõi mình (My Followers)
* **Method**: `GET`
* **Path**: `/api/followers`
* **Query Params**: `page` (default 0), `size` (default 20), `sort`
* **Response Body (`ResponseDTO<Page<FollowerResponseDto>>`)**:
```json
{
    "success": true,
    "code": 200,
    "message": "Fetch follower list successfully",
    "data": {
        "content": [
            {
                "id": 1,
                "requester": {
                    "id": 3,
                    "username": "tranvanb",
                    "display_name": "Trần Văn B",
                    "avatar_url": "https://avatar.url/b.jpg"
                },
                "received_at": "2026-09-30T10:30:00Z"
            }
        ],
        "page": {
            "size": 20,
            "number": 0,
            "totalElements": 1,
            "totalPages": 1
        }
    },
    "time": "2026-09-30T12:00:00Z"
}
```

#### 3. Lấy danh sách người theo dõi của một user bất kỳ
* **Method**: `GET`
* **Path**: `/api/followers/users/{userId}`
* **Query Params**: `page`, `size`
* **Response Body**: Tương tự như mục 2.

#### 4. Đếm số lượng người theo dõi mình
* **Method**: `GET`
* **Path**: `/api/followers/count`
* **Response Body**:
```json
{
    "success": true,
    "code": 200,
    "message": "Fetch follower count successfully",
    "data": 28,
    "time": "2026-09-30T12:00:00Z"
}
```

---

### PHẦN III. BẠN BÈ CHÍNH THỨC (`/api/friends` - Thuộc package `/friend`)

#### 1. Lấy danh sách Bạn bè chính thức (Mutual Follows)
* **Method**: `GET`
* **Path**: `/api/friends`
* **Query Params**:
  * `query` *(optional)*: Tìm kiếm theo `display_name` hoặc `username`.
  * `page`, `size`, `sort`
* **Response Body (`ResponseDTO<Page<FriendResponseDto>>`)**:
```json
{
    "success": true,
    "code": 200,
    "message": "Fetch friend list successfully",
    "data": {
        "content": [
            {
                "id": 10,
                "friend": {
                    "id": 2,
                    "username": "nguyenvana",
                    "display_name": "Nguyễn Văn A",
                    "avatar_url": "https://avatar.url/a.jpg",
                    "email": "a@example.com"
                },
                "friended_at": "2026-09-30T11:15:00Z"
            }
        ],
        "page": {
            "size": 20,
            "number": 0,
            "totalElements": 1,
            "totalPages": 1
        }
    },
    "time": "2026-09-30T12:00:00Z"
}
```

#### 2. Lấy danh sách bạn bè của một user bất kỳ
* **Method**: `GET`
* **Path**: `/api/friends/users/{userId}`
* **Query Params**: `query`, `page`, `size`
* **Response Body**: Tương tự như mục 1.

#### 3. Kiểm tra trạng thái quan hệ với 1 User bất kỳ
* **Method**: `GET`
* **Path**: `/api/friends/status/{targetUserId}`
* **Response Body (`ResponseDTO<FriendshipStatusResponseDto>>`)**:
```json
{
    "success": true,
    "code": 200,
    "message": "Fetch friendship status successfully",
    "data": {
        "target_user_id": 5,
        "status": "FRIEND"
    },
    "time": "2026-09-30T12:00:00Z"
}
```

#### 4. Lấy thống kê số lượng tổng quan (Summary)
* **Method**: `GET`
* **Path**: `/api/friends/summary`
* **Response Body (`ResponseDTO<FriendSummaryResponseDto>`)**:
```json
{
    "success": true,
    "code": 200,
    "message": "Fetch friend summary successfully",
    "data": {
        "friend_count": 12,
        "follower_count": 28,
        "following_count": 15
    },
    "time": "2026-09-30T12:00:00Z"
}
```

#### 5. Lấy danh sách bạn chung (Mutual Friends)
* **Method**: `GET`
* **Path**: `/api/friends/mutual/{targetUserId}`
* **Response Body (`ResponseDTO<MutualFriendResponseDto>`)**:
```json
{
    "success": true,
    "code": 200,
    "message": "Fetch mutual friends successfully",
    "data": {
        "mutual_count": 1,
        "mutual_friends": [
            {
                "id": 9,
                "username": "hoangnam",
                "display_name": "Hoàng Nam",
                "avatar_url": "https://avatar.url/nam.png"
            }
        ]
    },
    "time": "2026-09-30T12:00:00Z"
}
```

#### 6. Hủy kết bạn (Unfriend)
* **Method**: `DELETE`
* **Path**: `/api/friends/{targetUserId}`
* **Lưu ý**: Xóa mối quan hệ bạn bè và tự động hủy chiều theo dõi của bạn đối với người đó.
* **Response Body**:
```json
{
    "success": true,
    "code": 200,
    "message": "Unfriended successfully",
    "time": "2026-09-30T12:00:00Z"
}
```

---

## 4. Bảng Tra Cứu Mã Lỗi Thường Gặp (Error Reference)

| HTTP Code | GlobalException Message | Nguyên nhân & Hành vi đề xuất ở Frontend |
| :--- | :--- | :--- |
| `400 BAD_REQUEST` | `"Cannot follow yourself"` | Gửi request follow ID của chính mình. |
| `404 NOT_FOUND` | `"User not found"` | `targetUserId` không tồn tại. |
| `404 NOT_FOUND` | `"Not following this user"` | Gọi unfollow một người mà bạn chưa từng follow. |
| `404 NOT_FOUND` | `"Follower not found"` | Gọi gỡ follower một người không hề follow bạn. |
| `404 NOT_FOUND` | `"Friendship not found"` | Gọi unfriend khi hai người chưa từng là bạn bè. |
| `409 CONFLICT` | `"Already following this user"` | Người dùng đã follow đối phương từ trước (tránh double click). |
