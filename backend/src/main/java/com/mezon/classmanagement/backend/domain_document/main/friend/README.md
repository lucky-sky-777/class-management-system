# Tài Liệu Tích Hợp Module Bạn Bè (Friend System API Guide)

Tài liệu kỹ thuật hướng dẫn lập trình viên Frontend tích hợp toàn bộ tính năng quản lý quan hệ bạn bè, theo dõi và lời mời kết bạn.

---

## 1. Cơ Chế Hoạt Động Cốt Lõi (Dành Cho Frontend)

Hệ thống hoạt động dựa trên 3 trạng thái và thực thể:
1. **Lời mời đã gửi (`Following`)**: Khi bạn gửi lời mời kết bạn cho người khác (`sent_at`).
2. **Lời mời đã nhận (`Follower`)**: Khi người khác gửi lời mời kết bạn cho bạn (`received_at`).
3. **Bạn bè chính thức (`Friend`)**: Quan hệ 2 chiều khi lời mời được chấp nhận (`friended_at`).

### 2 Điểm Lưu Ý Quan Trọng:
* **Tự động chấp nhận (Auto Mutual Accept)**: Nếu User B đã gửi lời mời cho User A (đang chờ duyệt), mà User A lại bấm "Kết bạn" với User B, hệ thống sẽ **tự động biến 2 người thành bạn bè ngay lập tức** (trả về status `FRIEND`), không bắt User A phải vào tab lời mời để bấm "Chấp nhận".
* **Định danh bạn bè tự động trích xuất**: Dù ở Backend lưu theo cặp chuẩn hóa `user_1` / `user_2`, khi trả về cho Frontend, trường `friend` trong DTO luôn tự động trích xuất đúng **thông tin của đối phương** (không bao giờ là chính mình).

---

## 2. Máy Trạng Thái Nút Bấm Trên UI (UI State Machine)

Khi hiển thị trang cá nhân hoặc thẻ người dùng (User Card), gọi `GET /api/friends/status/{targetUserId}` để lấy `status` và render nút bấm tương ứng:

| `status` trả về | Giao diện nút bấm đề xuất | Hành động khi click | Gọi API |
| :--- | :--- | :--- | :--- |
| `SELF` | Không hiển thị nút kết bạn (trang cá nhân chính mình) | - | - |
| `NONE` | Nút **"Thêm bạn bè"** | Gửi lời mời kết bạn | `POST /api/friends/requests/{targetUserId}` |
| `REQUEST_SENT` | Nút **"Hủy lời mời"** | Thu hồi lời mời đã gửi | `DELETE /api/friends/requests/{targetUserId}/cancel` |
| `REQUEST_RECEIVED`| 2 nút: **"Chấp nhận"** & **"Từ chối"** | Đồng ý / Xóa lời mời | `POST .../accept` hoặc `DELETE .../reject` |
| `FRIEND` | Dropdown **"Bạn bè"** -> mục **"Hủy kết bạn"** | Xóa bạn bè | `DELETE /api/friends/{targetUserId}` |

---

## 3. Danh Sách RESTful API Chi Tiết

* **Base URL**: `/api/friends`
* **Xác thực**: Gửi token qua Header: `Authorization: Bearer <accessToken>`

### 3.1. Gửi lời mời kết bạn
* **Method**: `POST`
* **Path**: `/api/friends/requests/{targetUserId}`
* **Request Body**: Không có
* **Response Body (`ResponseDTO<FriendshipStatusResponseDto>`)**:
```json
{
    "success": true,
    "code": 200,
    "message": "Friend request processed successfully",
    "data": {
        "target_user_id": 2,
        "status": "REQUEST_SENT"
    },
    "time": "2026-09-30T12:00:00Z"
}
```
> [!NOTE]
> Nếu đối phương đã gửi lời mời cho bạn từ trước, `status` trả về sẽ là `"FRIEND"` (Auto Mutual Accept).

---

### 3.2. Chấp nhận lời mời kết bạn
* **Method**: `POST`
* **Path**: `/api/friends/requests/{requesterUserId}/accept`
* **Request Body**: Không có
* **Response Body (`ResponseDTO<FriendResponseDto>`)**:
```json
{
    "success": true,
    "code": 200,
    "message": "Friend request accepted successfully",
    "data": {
        "id": 10,
        "friend": {
            "id": 2,
            "username": "nguyenvana",
            "display_name": "Nguyễn Văn A",
            "avatar_url": "https://avatar.url/a.jpg",
            "email": "a@example.com"
        },
        "friended_at": "2026-09-30T12:00:00Z"
    },
    "time": "2026-09-30T12:00:00Z"
}
```

---

### 3.3. Từ chối lời mời kết bạn
* **Method**: `DELETE`
* **Path**: `/api/friends/requests/{requesterUserId}/reject`
* **Response Body**:
```json
{
    "success": true,
    "code": 200,
    "message": "Friend request rejected successfully",
    "time": "2026-09-30T12:00:00Z"
}
```

---

### 3.4. Thu hồi / Hủy lời mời kết bạn đã gửi
* **Method**: `DELETE`
* **Path**: `/api/friends/requests/{targetUserId}/cancel`
* **Response Body**:
```json
{
    "success": true,
    "code": 200,
    "message": "Sent friend request cancelled successfully",
    "time": "2026-09-30T12:00:00Z"
}
```

---

### 3.5. Hủy kết bạn (Unfriend)
* **Method**: `DELETE`
* **Path**: `/api/friends/{targetUserId}`
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

### 3.6. Lấy danh sách bạn bè (Kèm tìm kiếm & Phân trang)
* **Method**: `GET`
* **Path**: `/api/friends`
* **Query Params**:
  * `query` *(optional)*: Tìm kiếm theo `display_name` hoặc `username`.
  * `page` *(optional, mặc định: 0)*: Số trang.
  * `size` *(optional, mặc định: 20)*: Số phần tử trên mỗi trang.
  * `sort` *(optional, ví dụ: `id,desc`)*.
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
                "friended_at": "2026-09-30T10:00:00Z"
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

---

### 3.7. Lấy danh sách lời mời kết bạn đã nhận (Tab "Lời mời kết bạn")
* **Method**: `GET`
* **Path**: `/api/friends/requests/received`
* **Query Params**: `page`, `size`
* **Response Body (`ResponseDTO<Page<FollowerResponseDto>>`)**:
```json
{
    "success": true,
    "code": 200,
    "message": "Fetch received friend requests successfully",
    "data": {
        "content": [
            {
                "id": 5,
                "requester": {
                    "id": 3,
                    "username": "tranvanb",
                    "display_name": "Trần Văn B",
                    "avatar_url": "https://avatar.url/b.jpg"
                },
                "received_at": "2026-09-30T11:00:00Z"
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

---

### 3.8. Lấy danh sách lời mời kết bạn đã gửi (Tab "Đã gửi")
* **Method**: `GET`
* **Path**: `/api/friends/requests/sent`
* **Query Params**: `page`, `size`
* **Response Body (`ResponseDTO<Page<FollowingResponseDto>>`)**:
```json
{
    "success": true,
    "code": 200,
    "message": "Fetch sent friend requests successfully",
    "data": {
        "content": [
            {
                "id": 8,
                "target_user": {
                    "id": 4,
                    "username": "levanc",
                    "display_name": "Lê Văn C",
                    "avatar_url": "https://avatar.url/c.jpg"
                },
                "sent_at": "2026-09-30T11:30:00Z"
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

---

### 3.9. Kiểm tra trạng thái quan hệ với 1 User bất kỳ
* **Method**: `GET`
* **Path**: `/api/friends/status/{targetUserId}`
* **Response Body (`ResponseDTO<FriendshipStatusResponseDto>`)**:
```json
{
    "success": true,
    "code": 200,
    "message": "Fetch friendship status successfully",
    "data": {
        "target_user_id": 5,
        "status": "REQUEST_SENT"
    },
    "time": "2026-09-30T12:00:00Z"
}
```

---

### 3.10. Thống kê tổng số bạn bè & số lời mời (Summary Badge)
* **Method**: `GET`
* **Path**: `/api/friends/summary`
* **Mục đích**: Dùng để hiển thị số lượng badge đỏ trên thanh thông báo / menu bạn bè.
* **Response Body (`ResponseDTO<FriendSummaryResponseDto>`)**:
```json
{
    "success": true,
    "code": 200,
    "message": "Fetch friend summary successfully",
    "data": {
        "friend_count": 28,
        "received_request_count": 3,
        "sent_request_count": 1
    },
    "time": "2026-09-30T12:00:00Z"
}
```

---

### 3.11. Lấy danh sách bạn chung (Mutual Friends)
* **Method**: `GET`
* **Path**: `/api/friends/mutual/{targetUserId}`
* **Response Body (`ResponseDTO<MutualFriendResponseDto>`)**:
```json
{
    "success": true,
    "code": 200,
    "message": "Fetch mutual friends successfully",
    "data": {
        "mutual_count": 2,
        "mutual_friends": [
            {
                "id": 9,
                "username": "hoangd",
                "display_name": "Hoàng D",
                "avatar_url": "https://avatar.url/d.jpg"
            }
        ]
    },
    "time": "2026-09-30T12:00:00Z"
}
```

---

## 4. Xử Lý Mã Lỗi Thường Gặp (Error Handling)

| HTTP Code | GlobalException Message | Nguyên nhân & Hướng xử lý ở Frontend |
| :--- | :--- | :--- |
| `400 BAD_REQUEST` | `"Cannot send friend request to yourself"` | Người dùng gửi request cho chính ID của mình. |
| `404 NOT_FOUND` | `"User not found"` | `targetUserId` không tồn tại trong hệ thống. |
| `404 NOT_FOUND` | `"Friend request not found"` | Lời mời đã bị đối phương hủy hoặc không tồn tại. |
| `404 NOT_FOUND` | `"Friendship not found"` | Hai người chưa kết bạn hoặc đã hủy kết bạn trước đó. |
| `409 CONFLICT` | `"Users are already friends"` | Hai bên đã là bạn bè từ trước. |
| `409 CONFLICT` | `"Friend request already sent"` | Bạn đã gửi lời mời rồi, đang chờ duyệt. |
