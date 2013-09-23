insert into transformed_images
(manipulated_image_fk, transformation_fk)
select mi.manipulated_images_pk, t.transformations_pk
from manipulated_images mi
inner join manipulations m on m.manipulations_pk = mi.manipulation_fk
inner join transformations t on t.manipulation_fk = m.manipulations_pk;