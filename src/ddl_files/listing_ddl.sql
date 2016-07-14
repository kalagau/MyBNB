CREATE TABLE listing(
  listing_id INT AUTO_INCREMENT,
  PRIMARY KEY(listing_id),
  FOREIGN KEY (location_id) REFERENCES location(location_id)
)