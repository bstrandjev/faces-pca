SELECT * FROM faces.images;delimiter $$

CREATE TABLE `classifications` (
  `classification_pk` int(11) NOT NULL AUTO_INCREMENT,
  `beautiful` int(11) NOT NULL,
  `manipualted_image_fk` int(11) NOT NULL,
  `user_fk` int(11) NOT NULL,
  PRIMARY KEY (`classification_pk`),
  UNIQUE KEY `classification_pk_UNIQUE` (`classification_pk`),
  KEY `classification_subject_fk` (`manipualted_image_fk`),
  KEY `user_fk` (`user_fk`),
  CONSTRAINT `classification_subject_fk` FOREIGN KEY (`manipualted_image_fk`) REFERENCES `manipulated_images` (`manipulated_images_pk`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `user_fk` FOREIGN KEY (`user_fk`) REFERENCES `users` (`user_pk`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$


delimiter $$

CREATE TABLE `eigen_faces` (
  `eigen_faces_pk` int(11) NOT NULL AUTO_INCREMENT,
  `eigen_face_img` blob NOT NULL,
  `eigen_value` decimal(10,10) DEFAULT NULL,
  `transformation_fk` int(11) DEFAULT NULL,
  PRIMARY KEY (`eigen_faces_pk`),
  UNIQUE KEY `eigen_faces_pk_UNIQUE` (`eigen_faces_pk`),
  KEY `transformation_fk` (`transformation_fk`),
  CONSTRAINT `transformation_fk` FOREIGN KEY (`transformation_fk`) REFERENCES `transformations` (`transformations_pk`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$


delimiter $$

CREATE TABLE `images` (
  `images_pk` int(11) NOT NULL AUTO_INCREMENT,
  `real image` blob NOT NULL,
  `key` varchar(30) NOT NULL,
  PRIMARY KEY (`images_pk`),
  UNIQUE KEY `images_pk_UNIQUE` (`images_pk`),
  UNIQUE KEY `key_UNIQUE` (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$


delimiter $$

CREATE TABLE `manipulated_images` (
  `manipulated_images_pk` int(11) NOT NULL AUTO_INCREMENT,
  `manipulated_img` blob NOT NULL,
  `is_good` int(11) DEFAULT '1',
  `image_fk` int(11) NOT NULL,
  `manipulation_fk` int(11) NOT NULL,
  `begX` int(11) DEFAULT NULL,
  `begY` int(11) DEFAULT NULL,
  `endX` int(11) DEFAULT NULL,
  `endY` int(11) DEFAULT NULL,
  PRIMARY KEY (`manipulated_images_pk`),
  UNIQUE KEY `manipulated_images_pk_UNIQUE` (`manipulated_images_pk`),
  KEY `manipulated_image_fk` (`image_fk`),
  KEY `manipulation_fk` (`manipulation_fk`),
  CONSTRAINT `manipulated_image_fk` FOREIGN KEY (`image_fk`) REFERENCES `images` (`images_pk`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `manipulation_fk` FOREIGN KEY (`manipulation_fk`) REFERENCES `manipulations` (`manipulations_pk`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$


delimiter $$

CREATE TABLE `manipulations` (
  `manipulations_pk` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime DEFAULT NULL,
  PRIMARY KEY (`manipulations_pk`),
  UNIQUE KEY `manipulations_pk_UNIQUE` (`manipulations_pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$


delimiter $$

CREATE TABLE `pca_coeficients` (
  `pca_coeficients_pk` int(11) NOT NULL AUTO_INCREMENT,
  `coeficient` decimal(10,10) NOT NULL,
  `eigen_face_fk` int(11) NOT NULL,
  `manipulated_image_fk` int(11) NOT NULL,
  PRIMARY KEY (`pca_coeficients_pk`),
  UNIQUE KEY `pca_coeficients_pk_UNIQUE` (`pca_coeficients_pk`),
  KEY `eigen_face_fk` (`eigen_face_fk`),
  KEY `pca_subject_fk` (`manipulated_image_fk`),
  CONSTRAINT `eigen_face_fk` FOREIGN KEY (`eigen_face_fk`) REFERENCES `eigen_faces` (`eigen_faces_pk`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `pca_subject_fk` FOREIGN KEY (`manipulated_image_fk`) REFERENCES `manipulated_images` (`manipulated_images_pk`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$


delimiter $$

CREATE TABLE `transformations` (
  `transformations_pk` int(11) NOT NULL AUTO_INCREMENT,
  `manipulation_pk` int(11) DEFAULT NULL,
  PRIMARY KEY (`transformations_pk`),
  UNIQUE KEY `transformations_pk_UNIQUE` (`transformations_pk`),
  KEY `transformation_manipulation_fk` (`manipulation_pk`),
  CONSTRAINT `transformation_manipulation_fk` FOREIGN KEY (`manipulation_pk`) REFERENCES `manipulations` (`manipulations_pk`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$


delimiter $$

CREATE TABLE `users` (
  `user_pk` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`user_pk`),
  UNIQUE KEY `user_pk_UNIQUE` (`user_pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$


