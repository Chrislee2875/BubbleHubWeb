CREATE DATABASE BubbleHub;
GO

USE BubbleHub;
GO

CREATE TABLE [User] (
    uid INT IDENTITY PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    email NVARCHAR(100) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    role NVARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'CUSTOMER')),
    status BIT DEFAULT 1,
    created_at DATETIME DEFAULT GETDATE()
);

CREATE TABLE Category (
    cid INT IDENTITY PRIMARY KEY,
    name NVARCHAR(100) NOT NULL
);

CREATE TABLE Product (
    pid INT IDENTITY PRIMARY KEY,
    cid INT NOT NULL,
    name NVARCHAR(150) NOT NULL,
    price DECIMAL(10,2) NOT NULL CHECK (price > 0),
    image_path NVARCHAR(255),
    is_available BIT DEFAULT 1,
    CONSTRAINT fk_product_category
        FOREIGN KEY (cid) REFERENCES Category(cid)
);

CREATE TABLE [Order] (
    order_id INT IDENTITY PRIMARY KEY,
    uid INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status NVARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'PAID', 'CANCELLED', 'SERVED')),
    created_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_order_user
        FOREIGN KEY (uid) REFERENCES [User](uid)
);

CREATE TABLE OrderItem (
    order_item_id INT IDENTITY PRIMARY KEY,
    order_id INT NOT NULL,
    pid INT NOT NULL,
    sugar_level INT CHECK (sugar_level IN (0,25,50,75,100)),
    ice_level NVARCHAR(20) CHECK (ice_level IN ('no', 'less', 'normal')),
    quantity INT NOT NULL CHECK (quantity > 0),
    price DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_orderitem_order
        FOREIGN KEY (order_id) REFERENCES [Order](order_id),
    CONSTRAINT fk_orderitem_product
        FOREIGN KEY (pid) REFERENCES Product(pid)
);

CREATE TABLE Payment (
    payment_id INT IDENTITY PRIMARY KEY,
    order_id INT NOT NULL,
    payment_method NVARCHAR(50) NOT NULL,
    payment_status NVARCHAR(20) NOT NULL CHECK (payment_status IN ('PENDING', 'SUCCESS', 'FAILED')),
    amount DECIMAL(10,2) NOT NULL,
    paid_at DATETIME NULL,
    CONSTRAINT fk_payment_order
        FOREIGN KEY (order_id) REFERENCES [Order](order_id)
);
GO

-- =========================
-- DỮ LIỆU MẪU (TIẾNG VIỆT)
-- =========================

INSERT INTO [User] (name, email, password, role, status) VALUES
(N'Quản trị viên', N'admin@bubblehub.com', N'123456', N'ADMIN', 1),
(N'Nguyễn Văn An', N'an@bubblehub.com', N'123456', N'CUSTOMER', 1),
(N'Trần Thị Bình', N'binh@bubblehub.com', N'123456', N'CUSTOMER', 1);

INSERT INTO Category (name) VALUES
(N'Trà sữa'),
(N'Trà trái cây'),
(N'Cà phê'),
(N'Macchiato'),
(N'Sữa chua'),
(N'Đá xay'),
(N'Sữa tươi'),
(N'Topping');

INSERT INTO Product (cid, name, price, image_path, is_available) VALUES
((SELECT cid FROM Category WHERE name = N'Trà sữa'), N'Trà sữa truyền thống', 39000, N'products/tra-sua-truyen-thong.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Trà sữa'), N'Trà sữa hoàng gia', 45000, N'products/tra-sua-hoang-gia.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Trà sữa'), N'Trà sữa khoai môn', 47000, N'products/tra-sua-khoai-mon.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Trà sữa'), N'Trà sữa matcha', 49000, N'products/tra-sua-matcha.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Trà sữa'), N'Trà sữa đường đen', 52000, N'products/tra-sua-duong-den.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Trà trái cây'), N'Trà đào cam sả', 45000, N'products/tra-dao-cam-sa.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Trà trái cây'), N'Trà vải', 43000, N'products/tra-vai.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Trà trái cây'), N'Trà chanh dây', 44000, N'products/tra-chanh-day.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Trà trái cây'), N'Trà tắc mật ong', 46000, N'products/tra-tac-mat-ong.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Trà trái cây'), N'Trà chanh xanh', 40000, N'products/tra-chanh-xanh.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Cà phê'), N'Cà phê đen đá', 32000, N'products/ca-phe-den-da.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Cà phê'), N'Cà phê sữa đá', 35000, N'products/ca-phe-sua-da.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Cà phê'), N'Cà phê latte', 48000, N'products/ca-phe-latte.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Cà phê'), N'Cà phê cappuccino', 50000, N'products/ca-phe-cappuccino.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Cà phê'), N'Cà phê caramel', 52000, N'products/ca-phe-caramel.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Macchiato'), N'Trà nhài macchiato', 49000, N'products/tra-nhai-macchiato.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Macchiato'), N'Trà ô long macchiato', 50000, N'products/tra-olong-macchiato.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Macchiato'), N'Trà xanh macchiato', 48000, N'products/tra-xanh-macchiato.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Macchiato'), N'Trà đen macchiato', 47000, N'products/tra-den-macchiato.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Macchiato'), N'Trà dâu', 53000, N'products/tra-dau.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Sữa chua'), N'Sữa chua uống truyền thống', 42000, N'products/sua-chua-truyen-thong.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Sữa chua'), N'Sữa chua việt quất', 45000, N'products/sua-chua-viet-quat.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Sữa chua'), N'Sữa chua dâu', 45000, N'products/sua-chua-dau.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Sữa chua'), N'Sữa chua xoài', 46000, N'products/sua-chua-xoai.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Sữa chua'), N'Sữa chua chanh dây', 46000, N'products/sua-chua-chanh-day.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Đá xay'), N'Đá xay xoài', 52000, N'products/da-xay-xoai.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Đá xay'), N'Đá xay dâu', 52000, N'products/da-xay-dau.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Đá xay'), N'Đá xay bơ', 55000, N'products/da-xay-bo.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Đá xay'), N'Đá xay socola', 54000, N'products/da-xay-socola.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Đá xay'), N'Đá xay Oreo', 56000, N'products/da-xay-oreo.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Sữa tươi'), N'Sữa tươi đường đen', 50000, N'products/sua-tuoi-duong-den.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Sữa tươi'), N'Sữa tươi matcha', 51000, N'products/sua-tuoi-matcha.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Sữa tươi'), N'Sữa tươi cacao', 50000, N'products/sua-tuoi-cacao.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Sữa tươi'), N'Sữa tươi khoai môn', 50000, N'products/sua-tuoi-khoai-mon.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Sữa tươi'), N'Sữa tươi cà phê', 53000, N'products/sua-tuoi-ca-phe.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Topping'), N'Trân châu đen', 8000, N'products/tran-chau-den.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Topping'), N'Trân châu trắng', 8000, N'products/tran-chau-trang.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Topping'), N'Kem phô mai', 12000, N'products/kem-pho-mai.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Topping'), N'Thạch cà phê', 10000, N'products/thach-ca-phe.jpg', 1),
((SELECT cid FROM Category WHERE name = N'Topping'), N'Nha đam', 9000, N'products/nha-dam.jpg', 1);

DECLARE @uidAn INT = (SELECT uid FROM [User] WHERE email = N'an@bubblehub.com');
DECLARE @uidBinh INT = (SELECT uid FROM [User] WHERE email = N'binh@bubblehub.com');

INSERT INTO [Order] (uid, total_amount, status, created_at)
VALUES (@uidAn, 98000, N'PAID', DATEADD(DAY, -2, GETDATE()));
DECLARE @o1 INT = SCOPE_IDENTITY();

INSERT INTO [Order] (uid, total_amount, status, created_at)
VALUES (@uidBinh, 114000, N'PENDING', DATEADD(DAY, -1, GETDATE()));
DECLARE @o2 INT = SCOPE_IDENTITY();

INSERT INTO [Order] (uid, total_amount, status, created_at)
VALUES (@uidAn, 52000, N'SERVED', GETDATE());
DECLARE @o3 INT = SCOPE_IDENTITY();

INSERT INTO OrderItem (order_id, pid, sugar_level, ice_level, quantity, price) VALUES
(@o1, (SELECT pid FROM Product WHERE name = N'Trà sữa hoàng gia'), 75, N'normal', 1, 45000),
(@o1, (SELECT pid FROM Product WHERE name = N'Trân châu đen'), 100, N'less', 1, 8000),
(@o1, (SELECT pid FROM Product WHERE name = N'Trà đào cam sả'), 50, N'normal', 1, 45000),
(@o2, (SELECT pid FROM Product WHERE name = N'Đá xay dâu'), 50, N'less', 1, 52000),
(@o2, (SELECT pid FROM Product WHERE name = N'Kem phô mai'), 100, N'no', 1, 12000),
(@o2, (SELECT pid FROM Product WHERE name = N'Cà phê đen đá'), 0, N'normal', 1, 32000),
(@o2, (SELECT pid FROM Product WHERE name = N'Thạch cà phê'), 100, N'normal', 1, 10000),
(@o2, (SELECT pid FROM Product WHERE name = N'Trân châu đen'), 100, N'normal', 1, 8000),
(@o3, (SELECT pid FROM Product WHERE name = N'Đá xay xoài'), 50, N'less', 1, 52000);

INSERT INTO Payment (order_id, payment_method, payment_status, amount, paid_at) VALUES
(@o1, N'VNPAY', N'SUCCESS', 98000, DATEADD(DAY, -2, GETDATE())),
(@o2, N'VNPAY', N'PENDING', 114000, NULL),
(@o3, N'VNPAY', N'SUCCESS', 52000, GETDATE());
GO



