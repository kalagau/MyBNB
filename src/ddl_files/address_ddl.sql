CREATE TABLE address(
  address_id INT AUTO_INCREMENT,
  postal_code VARCHAR(255),
  country VARCHAR(255),
  city VARCHAR(255),
  PRIMARY KEY(address_id),
  FOREIGN KEY (listing_id) REFERENCES address(listing_id)
  FOREIGN KEY (listing_id) REFERENCES address(listing_id)
)