-- MySQL dump 9.11
--
-- Host: mysql2.ibiblio.org    Database: ceccr
-- ------------------------------------------------------
-- Server version	4.0.27-standard-log

DROP TABLE cbench_parameter;
DROP TABLE cbench_ext_pred;
DROP TABLE cbench_db_file;
DROP TABLE cbench_db;
DROP TABLE cbench_dataset;
drop table cbench_prediction_val;
DROP TABLE cbench_prediction;
DROP TABLE cbench_model;
DROP TABLE cbench_predictor;
DROP TABLE cbench_task;
DROP TABLE cbench_user;
DROP TABLE user_information;
--
CREATE TABLE user_information(
username varchar(20) NOT NULL default '0',
firstname varchar(20) default NULL,
lastname varchar(20) default NULL,
organization_type varchar(20) default NULL,
organization_name varchar(40)  default NULL,
position varchar(30) default NULL,
address varchar(60) default NULL,
city varchar(30) default NULL,
state varchar(30) default NULL,
country varchar(30) default NULL,
zipcode varchar(20) default NULL,
phone varchar(30) default NULL,
email varchar(60) default NULL,
password varchar(40) default NULL,
date_registered timestamp default '0000-00-00 00:00:00',
status varchar(16) default 'NOTSET',
PRIMARY KEY  (username)
)TYPE=InnoDB;
--
-- Table structure for table `cbench_user`
--

DROP TABLE cbench_dataSet;
CREATE TABLE cbench_dataSet (
  dataSetID int(12) unsigned NOT NULL auto_increment,
  userName varchar(45) NOT NULL default '',
  sdfName  varchar(100) default NULL,
  actName  varchar(100) default NULL,
  lastAccessTime datetime default NULL, 
  created_datetime datetime default NULL,
  knnType varchar(30) default "",
  size int(8) not null default '0',
  
  PRIMARY KEY  (dataSetID)
) TYPE=InnoDB;

CREATE TABLE cbench_user (
  username varchar(45) NOT NULL default '',
  password varchar(45) default NULL,
  updated_datetime timestamp default NULL,
  created_datetime timestamp default NULL,
  comment varchar(255) default NULL,
  PRIMARY KEY  (username)
) TYPE=InnoDB;


--data for cbench_user

INSERT INTO cbench_user (username, password) VALUES ('alex','ceccr');
INSERT INTO cbench_user (username, password) VALUES ('berk','ceccr');
INSERT INTO cbench_user (username, password) VALUES ('diane','ceccr');
INSERT INTO cbench_user (username, password) VALUES ('grulkc','mystar1');
INSERT INTO cbench_user (username, password) VALUES ('julia','julia');
INSERT INTO cbench_user (username, password) VALUES ('sasha','ceccr');
INSERT INTO cbench_user (username, password) VALUES ('test','test');
INSERT INTO cbench_user (username, password) VALUES ('weifan','ceccr');
INSERT INTO cbench_user (username, password) VALUES ('_all','mystar1');
INSERT INTO cbench_user (username, password) VALUES ('none','mystar1');
INSERT INTO cbench_user (username, password) VALUES ('meduban','ceccr');
INSERT INTO cbench_user (username, password) VALUES ('jswilmes','ceccr');
INSERT INTO cbench_user (username, password) VALUES ('tongan','tongan');
--
-- Table structure for table `cbench_task`
--

CREATE TABLE cbench_task (
  task_id int(10) unsigned NOT NULL auto_increment,
  username varchar(45) NOT NULL default '',
  jobname varchar(45) NOT NULL default '',
  submit datetime NOT NULL default '0000-00-00 00:00:00',
  start datetime default NULL,
  finish datetime default NULL,
  state varchar(45) NOT NULL default '',
  job_type varchar(45) NOT NULL default '',
  model_method varchar(45) default NULL,
  model_descriptors varchar(45) default NULL,
  ACTFileName varchar(45) default NULL,
  SDFileName varchar(45) default NULL,
  num_comp int(8) not null default '0',
  num_models int(8) not null default '0',
  INDEX task_user_ind (username),
  FOREIGN KEY (username) REFERENCES cbench_user (username) on delete restrict on update cascade,
  PRIMARY KEY  (task_id)
) TYPE=InnoDB;

--
-- Table structure for table `cbench_predictor`
--
CREATE TABLE cbench_predictor (
  predictor_id int(10) unsigned NOT NULL auto_increment,
  name varchar(45) NOT NULL default '',
  ACTFileName varchar(45) NOT NULL default '',
  SDFileName varchar(45) NOT NULL default '',
  username varchar(45) NOT NULL default '',
  model_method varchar(45) NOT NULL default '',
  model_descriptors varchar(45) NOT NULL default '',
  updated_datetime timestamp default null,
  created_datetime timestamp default NULL,
  comment varchar(255) default NULL,
  num_models_total int(8) unsigned not null default '0',
  num_models_train int(8) unsigned not null default '0',
  num_models_test int(8) unsigned not null default '0',
  criteria_q_squared float default NULL,
  criteria_r_squared float default NULL,
  criteria_max_slope float default NULL,
  criteria_min_slope float default NULL,
  criteria_relative_diff_r_r0 float default NULL,
  criteria_diff_r01_r02 float default NULL,
  criteria_trainingAcc float default NULL,
  criteria_testAcc float default NULL,
  criteria_normTrainingAcc float default NULL,
  criteria_normTestAcc float default NULL,
  criteria_trainingError float default NULL,
  criteria_testError float default NULL,
  criteria_normTrainingError float default NULL,
  criteria_normTestError float default NULL,
  INDEX predictor_user_ind (username),
  FOREIGN KEY (username) REFERENCES cbench_user (username) on delete restrict on update cascade,
  PRIMARY KEY  (predictor_id)
) TYPE=InnoDB;

--
-- Dumping data for table `cbench_predictor`
--
INSERT INTO cbench_predictor VALUES (1,'48_ANTICONV','aa_48-2.act','aa_48.sdf','_all','CONTINUOUS','MOLCONNZ',20070408175448,null,NULL,0,0,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL, null,null,null,null,null,null);
--
-- Table structure for table `cbench_model`
--
CREATE TABLE cbench_model (
  model_id int(10) unsigned NOT NULL auto_increment,
  nnn float default null,
  q_squared float default null,
  r_squared float default null,
  n float default null,
  b01 float default null,
  b02 float default null,
  b11 float default null,
  b12 float default null,
  r float default null,
  sl_squared float default null,
  F1 float default null,
  s2_squared float default null,
  F2 float default null,
  k1 float default null,
  k2 float default null,
  r01_squared float default null,
  r02_squared float default null,
  s01_squared float default null,
  s02_squared float default null,
  F01 float default null,
  F02 float default null,
  r451_squared float default null,
  r452_squared float default null,
  st45 float default null,
  trainingAcc float default null,
  normTestAcc float default null,
  testAcc float default null,
  normTrainingAcc float default null,
  trainingError float default NULL,
  normTrainingError float default NULL,
  testError float default NULL,
  normTestError float default NULL,
  file varchar(45) default '',
  predictor_id int(10) unsigned not null default '0',
  PRIMARY KEY  (model_id),
  INDEX model_predictor_id_ind (predictor_id),
  FOREIGN KEY (predictor_id) REFERENCES cbench_predictor (predictor_id) on delete cascade on update cascade
) TYPE=InnoDB;

--
-- Dumping data for table `cbench_model`
--

--
-- Table structure for table `cbench_prediction`
--
CREATE TABLE cbench_prediction (
  prediction_id int(10) unsigned NOT NULL auto_increment,
  prediction_name varchar(45) NOT NULL default '',
  predictor_id int(10) unsigned not null default '0',
  prediction_database varchar(45) default NULL,
  cutoff_value float unsigned default NULL,
  username varchar(45) NOT NULL default '',
  updated_datetime timestamp default null,
  created_datetime timestamp default null,
  comment varchar(255) default NULL,
  PRIMARY KEY  (prediction_id),
  INDEX prediction_predictor_id_ind (predictor_id),
  INDEX prediction_user_ind (username),
  FOREIGN KEY (username) REFERENCES cbench_user (username) on delete restrict on update cascade,
  FOREIGN KEY (predictor_id) REFERENCES cbench_predictor (predictor_id) on delete restrict on update cascade
) TYPE=InnoDB;

--
-- Dumping data for table `cbench_prediction`
--

--
-- Table structure for table `cbench_prediction_val`
--
CREATE TABLE cbench_prediction_val (
  pred_val_id int(10) unsigned NOT NULL auto_increment,
  prediction_id int(10) unsigned NOT NULL default '0',
  compound_name varchar(45) NOT NULL default '',
  num_models int(10) unsigned not null default '0',
  predicted_value float default NULL,
  stdev float unsigned default NULL,
  structure_file varchar(100) default NULL,
  PRIMARY KEY  (pred_val_id),
  INDEX pred_val_prediction_id_ind (prediction_id),
  FOREIGN KEY (prediction_id) REFERENCES cbench_prediction (prediction_id) on delete cascade on update cascade
) TYPE=InnoDB;

--
-- Dumping data for table `cbench_prediction_val`
--

--
-- Table structure for table `cbench_db`
--
CREATE TABLE cbench_db (
  database_name varchar(45) NOT NULL default '',
  comp_num int(10) unsigned not null default '0',
  username varchar(45) default NULL,
  updated_datetime timestamp default NULL,
  created_datetime timestamp default NULL,
  comment varchar(255) default NULL,
  INDEX db_user_ind (username),
  FOREIGN KEY (username) REFERENCES cbench_user (username) on delete restrict on update cascade,
  PRIMARY KEY  (database_name)
) TYPE=InnoDB;

--
-- Dumping data for table `cbench_dbs`
--

INSERT INTO cbench_db VALUES ('NCI',237771,'none',20070307144440,null,NULL);
INSERT INTO cbench_db VALUES ('NCI_Diversity_Set',1988,'_all',20070307144440,null,NULL);
INSERT INTO cbench_db VALUES ('ZINC7',6769804,'none',20070307144440,null,NULL);

--
-- Table structure for table `cbench_dbs_files`
--
CREATE TABLE cbench_db_file (
  database_name varchar(45) NOT NULL default '',
  file_location varchar(100) NOT NULL default '',
  PRIMARY KEY  (file_location),
  INDEX db_file_name_ind (database_name),
  FOREIGN KEY (database_name) REFERENCES cbench_db (database_name) on delete restrict on update cascade
) TYPE=InnoDB;

--
-- Dumping data for table `cbench_dbs_files`
--

INSERT INTO cbench_db_file VALUES ('NCI','NCI.x.gz');
INSERT INTO cbench_db_file VALUES ('NCI_Diversity_Set','NCI_Diversity_Set.x.gz');
INSERT INTO cbench_db_file VALUES ('ZINC7','ZINC7_1.x.gz');

--
-- Table structure for table `cbench_datasets`
--
CREATE TABLE cbench_dataset (
  dataset_name varchar(45) NOT NULL default '',
  sdfile_location varchar(100) NOT NULL default '',
  actfile_location varchar(100) NOT NULL default '',
  username varchar(45) default NULL,
  type varchar(45) default NULL,
  comp_num int(10) unsigned NOT NULL default '0',
  updated_datetime timestamp default null,
  created_datetime timestamp default null,
  comment varchar(255) default NULL,
  INDEX db_user_ind (username),
  FOREIGN KEY (username) REFERENCES cbench_user (username) on delete restrict on update cascade,
  PRIMARY KEY  (dataset_name)
) TYPE=InnoDB;

--
-- Dumping data for table `cbench_dataset`
--

INSERT INTO cbench_dataset VALUES ('activator_protein','activator_protein_43.sdf','activator_protein_43.act','_all','CONTINUOUS',43,20070307143926,NULL,null);
INSERT INTO cbench_dataset VALUES ('activator_protein_category','category_activator_protein.sdf','category_activator_protein.act','_all','CATEGORY',303,20070307143926,NULL,null);
INSERT INTO cbench_dataset VALUES ('anticonvulsants','anticonvulsants_91.sdf','anticonvulsants_91.act','_all','CONTINUOUS',91,20070307143926,NULL,null);

--
-- Table structure for table `cbench_ext_pred`
--
CREATE TABLE cbench_ext_pred (
  ext_pred_id int(10) unsigned NOT NULL auto_increment,
  predictor_id int(10) unsigned not null default '0',
  compound_id varchar(50) default NULL,
  pred_value float default NULL,
  act_value float default NULL,
  num_models int(10) unsigned  not null default '0',
  structure_file varchar(100) default NULL,
  PRIMARY KEY  (ext_pred_id),
  INDEX ext_pred_predictor_id_ind (predictor_id),
  FOREIGN KEY (predictor_id) REFERENCES cbench_predictor (predictor_id) on delete cascade on update cascade
) TYPE=InnoDB;

--
-- Dumping data for table `cbench_ext_pred`
--

CREATE TABLE cbench_parameter (
  parameter_id int(10) unsigned NOT NULL auto_increment,
  task_id int(10) unsigned default null,
  predictor_id int(10) unsigned default null,
  descriptor_step int(8) default null,
  min_descriptor int(8) default null,
  max_descriptor int(8) default null,
  num_runs int(8) default null,
  num_nn int(8) default null,
  num_pn int(8) default null,
  num_premutation int(8) default null,
  num_cycles int(8) default null,
  mu float default null,
  optimization_method float default null,
  ad_cutoff float default null,
  num_ext_set int(8) default null,
  num_se_radii int(8) default null,
  num_start_pts int(8) default null,
  se_point_selection varchar(45) default null,
  criteria_q_squared float default NULL,
  criteria_r_squared float default NULL,
  criteria_max_slope float default NULL,
  criteria_min_slope float default NULL,
  criteria_relative_diff_r_r0 float default NULL,
  criteria_diff_r01_r02 float default NULL,
  criteria_trainingAcc float default NULL,
  criteria_testAcc float default NULL,
  criteria_normTrainingAcc float default NULL,
  criteria_normTestAcc float default NULL,
  criteria_trainingError float default NULL,
  criteria_testError float default NULL,
  criteria_normTrainingError float default NULL,
  criteria_normTestError float default NULL,
  PRIMARY KEY  (parameter_id),
  INDEX param_task_id_ind (task_id),
  INDEX param_predictor_id_ind (predictor_id),
  FOREIGN KEY (task_id) REFERENCES cbench_task (task_id) on delete set null on update cascade,
  FOREIGN KEY (predictor_id) REFERENCES cbench_predictor (predictor_id) on delete cascade on update cascade
) TYPE=InnoDB;





CREATE TABLE cbench_mol_file(
  username  varchar(45) default '_all',
  data_set varchar(100)  NOT NULL default '',
  created_datetime Date default '',
  comment varchar(255) default NULL,    
   PRIMARY KEY  (data_set),
   INDEX db_user_ind (username),
   FOREIGN KEY (username) REFERENCES cbench_user (username) on delete restrict on update cascade
) TYPE=InnoDB;



 CREATE TABLE cbench_mol_data(                           
                   local_id int(10) unsigned NOT NULL auto_increment,     
                   data_set varchar(100) default NULL,   
                   compound_id varchar(30) default NULL,                
                   smiles mediumtext default NULL,                                     
                   2Dsdf mediumtext default NULL,                                      
                   3Dsdf mediumtext default NULL,                                      
                   activity float default NULL,                           
                   registry_num varchar(50) default NULL,                 
                   end_pt_type varchar(20) default NULL,                  
                   unit varchar(255) default NULL,                        
                   species varchar(255) default NULL,                     
                   route varchar(255) default NULL,                       
                   value varchar(255) default NULL,                       
                   chemical_name varchar(255) default NULL,               
                   comment varchar(255) default NULL,                     
                   PRIMARY KEY  (local_id),  
                    INDEX mol_dataset_ind (data_set),
                   FOREIGN KEY (data_set) REFERENCES cbench_mol_file (data_set) on delete cascade on update cascade                       
                 ) TYPE=InnoDB;

============================

CREATE TABLE cbench_db (
db_id  int(10) unsigned NOT NULL auto_increment,
  database_name varchar(45) NOT NULL default '',
  comp_num int(10) unsigned NOT NULL default '0',
  username varchar(45) default NULL,
  updated_datetime timestamp(14) NOT NULL,
  created_datetime timestamp(14) NOT NULL default '00000000000000',
  comment varchar(255) default NULL,
  PRIMARY KEY  (db_id),
  KEY db_user_ind (username),
  CONSTRAINT `cbench_db_ibfk_1` FOREIGN KEY (`username`) REFERENCES `cbench_user` (`username`) ON UPDATE 

CASCADE
) TYPE=InnoDB;

--
-- Dumping data for table `cbench_db`
--

INSERT INTO cbench_db VALUES ('1','NCI',237771,'none',20070307144440,20070422223541,NULL);
INSERT INTO cbench_db VALUES ('2','NCI_Diversity_Set',1988,'_all',20070307144440,20070422223541,NULL);
INSERT INTO cbench_db VALUES ('3','ZINC7',6769804,'none',20070307144440,20070422223541,NULL);

--
-- Table structure for table `cbench_db_file`
--

CREATE TABLE cbench_db_file (
 database_name varchar(45) NOT NULL default '',
  file_location varchar(150) NOT NULL default '',
 
 db_id  int(10) unsigned NOT NULL default '',
  PRIMARY KEY  (file_location),
  KEY db_file_name_ind (db_id),
  CONSTRAINT `cbench_db_file_ibfk_1` FOREIGN KEY (`db_id`) REFERENCES `cbench_db` (`db_id`) ON UPDATE 

CASCADE
) TYPE=InnoDB;

--
-- Dumping data for table `cbench_db_file`
--

INSERT INTO cbench_db_file VALUES ('NCI','NCI.x.gz','1');
INSERT INTO cbench_db_file VALUES ('NCI_Diversity_Set','NCI_Diversity_Set.x.gz','2');
INSERT INTO cbench_db_file VALUES ('ZINC7','ZINC7_1.x.gz','3');


CREATE TABLE cbench_dataset (
   fileID  int(10) unsigned NOT NULL auto_increment,
  fileName varchar(50) NOT NULL default '',
  modelType varchar(20) default '',
  username varchar(45) default NULL,
  actFile varchar(100) default NULL,
 sdfFile varchar(100) default NULL,
  numCompound int(10) default '0',
  createdTime date  default NULL,
  
  PRIMARY KEY  (fileID),
   INDEX db_user_ind (username),
  FOREIGN KEY (username) REFERENCES cbench_user (username) on delete restrict on update cascade
) TYPE=InnoDB;

