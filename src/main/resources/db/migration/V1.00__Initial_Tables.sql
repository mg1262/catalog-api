CREATE TABLE organization (
                            id varchar(40) primary key,
                            name varchar(200),
                            address varchar(100),
                            city varchar(100),
                            state varchar(2),
                            zip_code varchar (10)
);

CREATE TABLE subscription (
                            id varchar(40) primary key,
                            discount numeric(3,2),
                            total_price numeric(10,2),
                            organization_id varchar(40),
                            foreign key (organization_id) references organization(id)
);

CREATE TABLE product (
                              id varchar(40) primary key,
                              name varchar(200),
                              description varchar(1000),
                              price numeric(10,2)
);

CREATE TABLE subscription_product (
                         subscription_id varchar(40) not null,
                         product_id varchar(40) not null,
                         primary key(subscription_id, product_id),
                         foreign key (subscription_id) references subscription(id),
                         foreign key (product_id) references product(id)
);

CREATE TABLE product_sub_product (
                                      product_id varchar(40) not null,
                                      sub_product_id varchar(40) not null,
                                      primary key (product_id, sub_product_id),
                                      foreign key (product_id) references product(id),
                                      foreign key (sub_product_id) references product(id)
);


