CREATE TABLE `student` (
  `student_id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '번호',
  `name` VARCHAR(50) NOT NULL COMMENT '이름',
  `grade` INT(11) NULL DEFAULT NULL COMMENT '학년',
  `age` INT(11) NULL DEFAULT NULL COMMENT '나이',
  `hobby` VARCHAR(50) NULL DEFAULT NULL COMMENT '취미',
  PRIMARY KEY (`student_id`)
);


CREATE TABLE `user` (
  `user_id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '번호',
  `name` VARCHAR(50) NOT NULL COMMENT '이름',
  `email` INT(11) NULL DEFAULT NULL COMMENT '학년',
  PRIMARY KEY (`user_id`)
);
