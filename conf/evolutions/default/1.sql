# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table product (
  id                            bigint auto_increment not null,
  image                         varchar(255),
  title                         varchar(255),
  description                   varchar(255),
  price                         bigint,
  constraint pk_product primary key (id)
);


# --- !Downs

drop table if exists product;

