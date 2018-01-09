--
-- 
-- Copyright (c) 2001 ebXMLsoft Inc.  All rights reserved.
--
--

spool asn.log

alter table Container drop constraint fk_Container_1
/
alter table Product drop constraint fk_Product_1
/
alter table Product drop constraint fk_Product_2
/
alter table FromLocation drop constraint fk_FromLocation_1 
/
alter table ToLocation drop constraint fk_ToLocation_1 
/

--
-- AdvanceShippingNotice
--
drop table AdvanceShippingNotice
/
create table AdvanceShippingNotice (
  id    			                   varchar(32)       not null,
  clientId			                   varchar(32)       not null,
  estimatedDeliveryTime	                           varchar(32),
  estimatedDelivery_TimeZone	                   varchar(16),
  buyerOrderId		                           varchar(32),
  buyerId			                   varchar(32)       not null,
  carrierId                                        varchar(32),
  constraint pk_AdvanceShippingNotice primary key (id) using index
)
/

--
-- FromLocation
--
drop table FromLocation
/
create table FromLocation (
  id			                          number(16)        not null,
  name			                          varchar(64)       not null,
  advanceShippingNoticeId	                  varchar(32)       not null,
  -- Address
  street                                          varchar(64),
  city                                            varchar(64),
  country                                         varchar(64),
  postalCode                                      varchar(64),
  state                                           varchar(64),
   -- TelephoneNumber
  telephone_areaCode                              varchar(4)        not null,
  telephone_countryCode                           varchar(4),
  telephone_extension                             varchar(8),
  telephone_number                                varchar(8)        not null,
  telephone_url                                   varchar(128),
  constraint pk_FromLocation primary key (id,name) using index,
  constraint fk_FromLocation_1 foreign key (advanceShippingNoticeId)
      references AdvanceShippingNotice(id)
)
/


--
-- ToLocation
--
drop table ToLocation
/
create table ToLocation (
  id			                          number(16)        not null,
  advanceShippingNoticeId	                  varchar(32)       not null,	
  name			                          varchar(64)       not null,
  -- Address
  street                                          varchar(64),
  city                                            varchar(64),
  country                                         varchar(64),
  postalCode                                      varchar(64),
  state                                           varchar(64),
   -- TelephoneNumber
  telephone_areaCode                             varchar(4)        not null,
  telephone_countryCode                          varchar(4),
  telephone_extension                            varchar(8),
  telephone_number                               varchar(8)        not null,
  telephone_url                                  varchar(128),
  constraint pk_ToLocation primary key (id,name) using index,
  constraint fk_ToLocation_1 foreign key (advanceShippingNoticeId)
      references AdvanceShippingNotice(id)
)
/


--
-- Container
--
drop table Container
/
create table Container (
  id				                  number(16)        not null,
  type				                  varchar(32)       not null,
  advanceShippingNoticeId		          varchar(32)       not null,
  quantity			                  integer           not null,
  quantity_unitOfMeasure		          varchar(16)       not null,
  netWeight			                  integer,
  netWeight_unitOfMeasure			  varchar(16),
  grossWeight			                  integer,
  grossWeight_unitOfMeasure			  varchar(16),
  length			                  integer,
  length_unitOfMeasure			          varchar(16),
  height			                  integer,
  height_unitOfMeasure			          varchar(16),
  width				                  integer,
  width_unitOfMeasure			          varchar(16),
  buyerOrderId		                          varchar(32),
  constraint pk_Container primary key (id) using index,
  constraint fk_Container_1 foreign key (advanceShippingNoticeId)
      references AdvanceShippingNotice(id)
)
/

--
-- Product
--
drop table Product
/
create table Product (
  id				                  number(16)        not null,
  containerId		                          number(16),
  advanceShippingNoticeId                         varchar(32),
  quantity			                  integer           not null,
  quantity_unitOfMeasure		          varchar(16)       not null,
  netWeight			                  integer,
  netWeight_unitOfMeasure			  varchar(16),
  grossWeight			                  integer,
  grossWeight_unitOfMeasure			  varchar(16),
  length			                  integer,
  length_unitOfMeasure			          varchar(16),
  height			                  integer,
  height_unitOfMeasure			          varchar(16),
  width				                  integer,
  width_unitOfMeasure			          varchar(16),
  serialNumber                                    varchar(64),
  expiryDate                                      varchar(32),
  expiryDate_TimeZone                             varchar(16),
  description                                     varchar(256),
  constraint pk_Product primary key (id) using index,
  constraint fk_Product_1 foreign key (containerId)
      references Container(id),
  constraint fk_Product_2 foreign key (advanceShippingNoticeId)
      references AdvanceShippingNotice(id)
)
/
spool off

