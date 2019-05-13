# Tên đề tài: Music Player App

## Các thành viên nhóm:
- Nguyễn Anh Khôi - 1651010083 - Nhóm trưởng
- Trương Trọng Thanh - 1651010128
- Nguyễn Minh Trí - 1651010151
- Vũ Lê Quang Trường - 1651010156

## Mô tả
Đây là 1 music app player với các tính năng chính của 1 chương trình phát nhạc. Bao gồm splash screen bên ngoài nhằm để cho các chức năng chính bên trong load xong. App này đọc external storage của điện thoại (trong file Download/) và sử dụng RecycleView để hiển thị danh sách các nhạc đã đọc được. Ngoài ra, app đã được optimize qua việc sử dụng Data Bindings và BindService. Khi phát nhạc, app sẽ tạo 1 notification để điều khiển service phát nhạc trong app. Notification có thể thu gọn và mở rộng. Phần mở rộng sẽ bao gồm thêm hình ảnh album được gắn trong file nhạc.

## Hướng dẫn
Chương trình yêu cầu thiết bị, hoặc giả lập, có chứa sẵn 1 hoặc nhiều file nhạc trong folder Download/. Nếu là giả lập, kéo thả các file nhạc sẵn có vào giả lập, các file sẽ tự động được add và mục Download/. Nếu không có, có thể sử dụng các file nhạc có sẵn trong folder example/ của project.
