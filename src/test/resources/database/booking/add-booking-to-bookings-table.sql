INSERT INTO packs (id, title, description, price_per_day, instructor_hourly_price, image_url, is_deleted)
VALUES (5, 'Example pack', 'Example pack description', 5.99, 10, 'https://example.jpg', false);

INSERT INTO bookings (id, full_name, email, phone_number, instructor_hours, pack_id, total_price, is_deleted)
VALUES (4, 'Kelly Slater', 'kelly@gmail.com', '+1-202-555-0123', 4, 5, 125, false)