INSERT INTO packs (id, title, description, price_per_day, image_url, is_deleted)
VALUES (5, 'Example pack', 'Example pack description', 5.99, 'https://example.jpg', false);

INSERT INTO bookings (id, booking_id, rental_date, issuance_time, full_name, email, phone_number, total_price, status, is_deleted)
VALUES (4, 'SS-2026-4', '2026-08-11', '08:00:00', 'Kelly Slater', 'kelly@gmail.com', '+1-202-555-0123', 45.96, 'PENDING', false);

INSERT INTO participants (id, name, instructor_hours, booking_id, rental_pack_id)
VALUES (1, 'Kelly Slater', 4, 4, 5);

INSERT INTO payments (id, idempotency_key, booking_id, status, amount, card_last_four, card_holder_full_name, billing_country, created_at)
VALUES (7, '123e4567-e89b-12d3-a456-426614174000', 4, 'SUCCESS', 45.96, '0123', 'Kelly Slater', 'USA', '2026-08-11 08:00:00');
