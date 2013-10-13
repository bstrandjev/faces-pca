/* Select all classifications along with their corresponding members. Counts of each class representatives will be displayed */

select c.classification_name, cv.classification_value_value, count(*) 
from faces.t_classifications c
join faces.classification_values cv on c.classification_pk = cv.classification_fk
join faces.classified_images ci on ci.classification_value_fk = cv.classification_value_pk
group by c.classification_name, cv.classification_value_value;

/* Setting the nationality classification affiliations. */
SET @classification_id = 5;

SET @image_group_key = 'initial';
SET @classification_value_key = 'bulgarian';

SELECT @classification_value_index := max(cv.classification_value_pk)
from faces.classification_values cv
where cv.classification_fk = @classification_id
   and cv.classification_value_value = @classification_value_key;

SELECT @manipulation_index := max(m.manipulations_pk)
from image_groups ig
join images i on i.image_group_fk = ig.image_group_pk
join manipulated_images mi on mi.image_fk = i.images_pk
join manipulations m on mi.manipulation_fk = m.manipulations_pk
where ig.image_group_key = @image_group_key
group by i.image_group_fk, m.manipulations_pk;

insert into classified_images (manipulated_image_fk, classification_value_fk)
select mi.manipulated_images_pk, @classification_value_index
from faces.manipulated_images mi
where is_good = true and manipulation_fk = @manipulation_index;