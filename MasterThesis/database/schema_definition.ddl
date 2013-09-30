delimiter $$

CREATE TABLE `classification_values` (
  `classification_value_pk` int(11) NOT NULL AUTO_INCREMENT,
  `classification_value_value` varchar(45) NOT NULL,
  `classification_fk` int(11) NOT NULL,
  PRIMARY KEY (`classification_value_pk`),
  UNIQUE KEY `classification_value_pk_UNIQUE` (`classification_value_pk`),
  KEY `classification_image_classification_fk` (`classification_fk`),
  CONSTRAINT `classification_image_classification_fk` FOREIGN KEY (`classification_fk`) REFERENCES `t_classifications` (`classification_pk`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8$$


delimiter $$

CREATE TABLE `classified_images` (
  `classified_image_pk` int(11) NOT NULL AUTO_INCREMENT,
  `manipulated_image_fk` int(11) NOT NULL,
  `user_fk` int(11) NOT NULL,
  `classification_value_fk` int(11) NOT NULL,
  PRIMARY KEY (`classified_image_pk`),
  UNIQUE KEY `classified_image_pk_UNIQUE` (`classified_image_pk`),
  KEY `user_fk` (`user_fk`),
  KEY `classification_image_fk` (`manipulated_image_fk`),
  KEY `t_classification_value_fk` (`classification_value_fk`),
  KEY `t_users_fk` (`user_fk`),
  KEY `t_manipulated_image_fk` (`manipulated_image_fk`),
  KEY `clas_vals_fk` (`classification_value_fk`),
  CONSTRAINT `clas_vals_fk` FOREIGN KEY (`classification_value_fk`) REFERENCES `classification_values` (`classification_value_pk`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `t_manipulated_image_fk` FOREIGN KEY (`manipulated_image_fk`) REFERENCES `manipulated_images` (`manipulated_images_pk`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `t_users_fk` FOREIGN KEY (`user_fk`) REFERENCES `users` (`user_pk`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=409 DEFAULT CHARSET=utf8$$


delimiter $$

CREATE TABLE `eigen_faces` (
  `eigen_faces_pk` int(11) NOT NULL AUTO_INCREMENT,
  `eigen_face` longblob NOT NULL,
  `eigen_value` double DEFAULT NULL,
  `transformation_fk` int(11) DEFAULT NULL,
  PRIMARY KEY (`eigen_faces_pk`),
  UNIQUE KEY `eigen_faces_pk_UNIQUE` (`eigen_faces_pk`),
  KEY `transformation_fk` (`transformation_fk`),
  CONSTRAINT `transformation_fk` FOREIGN KEY (`transformation_fk`) REFERENCES `transformations` (`transformations_pk`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1113 DEFAULT CHARSET=utf8$$


delimiter $$

CREATE TABLE `image_groups` (
  `image_group_pk` int(11) NOT NULL AUTO_INCREMENT,
  `image_group_key` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`image_group_pk`),
  UNIQUE KEY `image_group_key_UNIQUE` (`image_group_key`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8$$


delimiter $$

CREATE TABLE `images` (
  `images_pk` int(11) NOT NULL AUTO_INCREMENT,
  `real_image_path` varchar(60) NOT NULL,
  `image_key` varchar(30) NOT NULL,
  `image_group_fk` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`images_pk`),
  UNIQUE KEY `images_pk_UNIQUE` (`images_pk`),
  UNIQUE KEY `key_UNIQUE` (`image_key`),
  KEY `image_group_fk` (`image_group_fk`),
  CONSTRAINT `image_group_fk` FOREIGN KEY (`image_group_fk`) REFERENCES `image_groups` (`image_group_pk`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=460 DEFAULT CHARSET=utf8$$


delimiter $$

CREATE TABLE `manipulated_images` (
  `manipulated_images_pk` int(11) NOT NULL AUTO_INCREMENT,
  `manipulated_img_path` varchar(120) NOT NULL,
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
) ENGINE=InnoDB AUTO_INCREMENT=647 DEFAULT CHARSET=utf8$$


delimiter $$

CREATE TABLE `manipulations` (
  `manipulations_pk` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime DEFAULT NULL,
  `manipulation_index` int(11) NOT NULL,
  PRIMARY KEY (`manipulations_pk`),
  UNIQUE KEY `manipulations_pk_UNIQUE` (`manipulations_pk`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8$$


delimiter $$

CREATE TABLE `pca_coeficients` (
  `pca_coeficients_pk` int(11) NOT NULL AUTO_INCREMENT,
  `eigen_face_fk` int(11) NOT NULL,
  `manipulated_image_fk` int(11) NOT NULL,
  `coeficient` double NOT NULL,
  PRIMARY KEY (`pca_coeficients_pk`),
  UNIQUE KEY `pca_coeficients_pk_UNIQUE` (`pca_coeficients_pk`),
  KEY `eigen_face_fk` (`eigen_face_fk`),
  KEY `pca_subject_fk` (`manipulated_image_fk`),
  CONSTRAINT `eigen_face_fk` FOREIGN KEY (`eigen_face_fk`) REFERENCES `eigen_faces` (`eigen_faces_pk`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `pca_subject_fk` FOREIGN KEY (`manipulated_image_fk`) REFERENCES `manipulated_images` (`manipulated_images_pk`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=149450 DEFAULT CHARSET=utf8$$


delimiter $$

CREATE TABLE `t_classifications` (
  `classification_pk` int(11) NOT NULL AUTO_INCREMENT,
  `classification_key` varchar(32) NOT NULL,
  `classification_name` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`classification_pk`),
  UNIQUE KEY `classification_key_UNIQUE` (`classification_key`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8$$


delimiter $$

CREATE TABLE `transformations` (
  `transformations_pk` int(11) NOT NULL AUTO_INCREMENT,
  `average_face` longblob NOT NULL,
  PRIMARY KEY (`transformations_pk`),
  UNIQUE KEY `transformations_pk_UNIQUE` (`transformations_pk`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8$$


delimiter $$

CREATE TABLE `transformed_images` (
  `transformed_images_pk` int(11) NOT NULL AUTO_INCREMENT,
  `manipulated_image_fk` int(11) NOT NULL,
  `transformation_fk` int(11) NOT NULL,
  PRIMARY KEY (`transformed_images_pk`),
  KEY `transfromed_manipulated_image_fk` (`manipulated_image_fk`),
  KEY `connected_transformation_fk` (`transformation_fk`),
  CONSTRAINT `connected_transformation_fk` FOREIGN KEY (`transformation_fk`) REFERENCES `transformations` (`transformations_pk`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `transfromed_manipulated_image_fk` FOREIGN KEY (`manipulated_image_fk`) REFERENCES `manipulated_images` (`manipulated_images_pk`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=10929 DEFAULT CHARSET=utf8$$


delimiter $$

CREATE TABLE `users` (
  `user_pk` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`user_pk`),
  UNIQUE KEY `user_pk_UNIQUE` (`user_pk`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8$$


