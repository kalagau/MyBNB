CREATE TABLE review(
  review_id INT AUTO_INCREMENT,
  rating INT,
  description TEXT,
  reviewer_is_renter BOOLEAN,
  PRIMARY KEY(review_id)
)