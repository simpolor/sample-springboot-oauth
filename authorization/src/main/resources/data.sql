INSERT INTO user(email, password, name, authorities)
    VALUES ('user@gmail.com', '{bcrypt}$2a$10$eLgAasUm0JsjlZq4jmexNutKT638jyk/lCT3uQn27UOtuAjcpwVwq', '유저', 'ROLE_USER');

INSERT INTO user(email, password, name, authorities)
    VALUES ('admin@gmail.com', '{bcrypt}$2a$10$eLgAasUm0JsjlZq4jmexNutKT638jyk/lCT3uQn27UOtuAjcpwVwq', '관리자', 'ROLE_ADMIN');