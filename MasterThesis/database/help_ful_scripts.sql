/* Select all classifications along with their corresponding members. Counts of each class representatives will be displayed */

select c.classification_name, cv.classification_value_value, count(*) 
from faces.t_classifications c
join faces.classification_values cv on c.classification_pk = cv.classification_fk
join faces.classified_images ci on ci.classification_value_fk = cv.classification_value_pk
group by c.classification_name, cv.classification_value_value;