INSERT INTO `concept` VALUES (3,0,NULL,NULL,NULL,3,5,0,1,'2012-09-17 13:08:42',NULL,NULL,NULL,NULL,NULL,NULL,'9ceb4021-0053-4d6a-b8f5-028d602cb823'),(4,0,NULL,NULL,NULL,3,5,0,1,'2012-09-17 13:08:56',NULL,NULL,NULL,NULL,NULL,NULL,'997737f3-d35b-4369-b324-f14b18a7a52b'),(5,0,NULL,NULL,NULL,3,5,0,1,'2012-09-17 13:08:56',NULL,NULL,NULL,NULL,NULL,NULL,'5b5e66c5-00ff-11e2-a21f-0800200c9a66'),(6,0,NULL,NULL,NULL,3,5,0,1,'2012-09-17 13:08:56',NULL,NULL,NULL,NULL,NULL,NULL,'84521ad7-00ff-11e2-a21f-0800200c9a66');

INSERT INTO `concept_description` VALUES (1,3,'Motech Concept','en',1,'2012-09-17 13:08:42',NULL,NULL,'7393e7f0-7763-4603-935d-ba03bbafb036'),(2,4,'Death Concept','en',1,'2012-09-17 13:08:56',NULL,NULL,'1b92293c-d2cf-4ff5-98e1-a0167fe9707b'),(3,5,'Search Concept','en',1,'2012-09-17 13:08:56',NULL,NULL,'9ae58610-00ff-11e2-a21f-0800200c9a66'),(4,6,'Voidable Concept','en',1,'2012-09-17 13:08:56',NULL,NULL,'9fb361d0-00ff-11e2-a21f-0800200c9a66');

INSERT INTO `concept_name` VALUES (21,3,'Motech Concept','en',1,1,'2012-09-17 13:08:42','FULLY_SPECIFIED',0,NULL,NULL,NULL,'3fad7e4d-d74a-4981-9c56-9fe76a1a3d0e'),(22,4,'Death Concept','en',1,1,'2012-09-17 13:08:56','FULLY_SPECIFIED',0,NULL,NULL,NULL,'179d90ff-83a8-4c1d-b44e-632e60db1338'),(23,5,'Search Concept','en',1,1,'2012-09-17 13:08:56','FULLY_SPECIFIED',0,NULL,NULL,NULL,'b4c954d0-00ff-11e2-a21f-0800200c9a66'),(24,6,'Voidable Concept','en',1,1,'2012-09-17 13:08:56','FULLY_SPECIFIED',0,NULL,NULL,NULL,'b85d5100-00ff-11e2-a21f-0800200c9a66');

INSERT INTO `concept_word` VALUES (41,3,'CONCEPT','en',21,1.3839285714285714),(42,3,'MOTECH','en',21,2.4642857142857144),(43,4,'CONCEPT','en',22,1.3928571428571428),(44,4,'DEATH','en',22,2.580769230769231),(45,5,'CONCEPT','en',22,2.580769230769231),(46,5,'SEARCH','en',22,2.580769230769231),(47,6,'CONCEPT','en',22,2.580769230769231),(48,6,'VOIDABLE','en',22,2.580769230769231);

INSERT INTO `encounter_type` VALUES (1,'ADULTINITIAL','ADULTINITIAL',1,'2012-09-17 13:09:13',0,NULL,NULL,NULL,'bf3cd250-295a-4a79-988a-f90beb0f0a7f');

INSERT INTO `location` VALUES (2,'Clinic 1','Clinic 1','Test',NULL,'Test','Test',NULL,'Test',NULL,NULL,1,'2012-09-17 13:06:41',NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,'37cc8ab3-cce8-468b-8dbe-6f6ecf8aff99'),(3,'Clinic 2','Clinic 2','Test',NULL,'Test','Test',NULL,'Test',NULL,NULL,1,'2012-09-17 13:06:52',NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,'9a0a9ae4-548a-477a-84b5-0c382d6fca17');

INSERT INTO `person` VALUES (2,'M','1982-01-01',1,0,NULL,NULL,1,'2012-09-17 13:05:13',1,'2012-09-17 13:07:20',0,NULL,NULL,NULL,'c92e8152-4a09-442e-95fe-94d2c16d73c5'),(3,'M','1962-01-01',1,0,NULL,NULL,1,'2012-09-17 13:07:50',NULL,NULL,0,NULL,NULL,NULL,'1e331bdb-d776-4881-ab65-fdb259e53124'),(4,'F','1982-01-01',1,0,NULL,NULL,1,'2012-09-17 13:08:27',NULL,NULL,0,NULL,NULL,NULL,'39a7cf6a-6fac-4eff-86dd-4cd62d28c272');

INSERT INTO `person_address` VALUES (1,2,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'2012-09-17 13:05:13',0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'1ba1a39d-a391-457a-b3af-14a77ce8fc44'),(2,3,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'2012-09-17 13:07:50',0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'0c5b49dd-6845-420f-ab4d-0cab5e44bf7e'),(3,4,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'2012-09-17 13:08:27',0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'f1f03d4d-97ab-4a24-b39f-4cb2b854ece2');

INSERT INTO `person_name` VALUES (2,1,2,NULL,'Chuck',NULL,NULL,'Motech',NULL,NULL,NULL,1,'2012-09-17 13:05:13',0,NULL,NULL,NULL,1,'2012-09-17 13:07:20','a4ed3ddf-04ba-4340-a54a-ffeed19810d8'),(3,1,3,NULL,'Bill',NULL,NULL,'Test',NULL,NULL,NULL,1,'2012-09-17 13:07:50',0,NULL,NULL,NULL,NULL,NULL,'c4512813-fd14-4ccc-9fd1-0aa255e7118f'),(4,1,4,NULL,'Sara',NULL,NULL,'Test',NULL,NULL,NULL,1,'2012-09-17 13:08:27',0,NULL,NULL,NULL,NULL,NULL,'5eed2ab4-fc0d-472d-8cf3-4fd13aaa97c5');

INSERT INTO `patient` VALUES (3,NULL,1,'2012-09-17 13:07:50',NULL,NULL,0,NULL,NULL,NULL),(4,NULL,1,'2012-09-17 13:08:27',NULL,NULL,0,NULL,NULL,NULL);

INSERT INTO `patient_identifier` VALUES (1,3,'700',3,1,NULL,1,'2012-09-17 13:07:50',NULL,NULL,0,NULL,NULL,NULL,'fd0ae3a1-e6cd-4de6-8603-57d4d7e84636'),(2,4,'750',3,1,NULL,1,'2012-09-17 13:08:27',NULL,NULL,0,NULL,NULL,NULL,'adc4d638-46e7-4dd4-920c-78982c6b76d9');

INSERT INTO `provider` VALUES (1,2,NULL,'3-4',1,'2012-09-17 13:04:45',1,'2012-09-17 13:05:51',0,NULL,NULL,NULL,'021d1fd0-c360-4ef3-82f1-9d8a81bd26fd');

INSERT INTO `users` VALUES (3,'3-4','chuck','3007f7dbad4466ed9d76ec10a9aa594e3d89c63e45242784900c498815accb7e2b5cbef133ef56a7b33aa7e8197d4c50f5d7d26d5d5008ca10878c360cb69a2b','3936cae81c1d098f2ee4f0e0f8b96b49f63ee941ff4d8397d809fd78f511ec32028b82f3d7f863f69171972756ce4f0145a76824a92271dee0f34fc28242a2ab',NULL,NULL,1,'2012-09-17 13:07:20',1,'2012-09-17 13:07:20',2,0,NULL,NULL,NULL,'3c3208f3-ef6e-431b-be56-0c96ff4a146e');

INSERT INTO `user_role` VALUES (3,'Provider');

INSERT INTO `encounter` VALUES (1,1,3,2,NULL,'2012-09-03 00:00:00',1,'2012-09-17 13:09:34',0,NULL,NULL,NULL,NULL,NULL,NULL,'e6c138e9-3d10-4037-b4ec-85c256b7e3db'),(2,1,3,2,NULL,'2012-09-07 00:00:00',1,'2012-09-17 13:10:00',0,NULL,NULL,NULL,NULL,NULL,NULL,'7c6d7d1c-fb03-46a1-9895-a8f9ebcb4249'),(3,1,3,2,NULL,'2012-09-04 00:00:00',1,'2012-09-17 13:10:00',0,NULL,NULL,NULL,NULL,NULL,NULL,'41868960-0100-11e2-a21f-0800200c9a66');

INSERT INTO `encounter_provider` VALUES (1,1,1,1,1,'2012-09-17 13:09:34',NULL,NULL,0,NULL,NULL,NULL,'aa9163c0-4f53-4be1-b436-01f0518828d5'),(2,2,1,1,1,'2012-09-17 13:10:00',NULL,NULL,0,NULL,NULL,NULL,'21046f73-4a7d-4be5-afea-9c94518f4e28'),(3,3,1,1,1,'2012-09-17 13:10:00',NULL,NULL,0,NULL,NULL,NULL,'a9c29440-0102-11e2-a21f-0800200c9a66');

INSERT INTO `obs` VALUES (1,3,3,1,NULL,'2012-09-03 00:00:00',2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Teset',NULL,NULL,1,'2012-09-17 13:09:45',0,NULL,NULL,NULL,'5c053c6c-a463-4f17-968d-5a48b512fe74',NULL),(2,3,3,2,NULL,'2012-09-07 00:00:00',2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Test',NULL,NULL,1,'2012-09-17 13:10:07',0,NULL,NULL,NULL,'bd78b048-d220-494d-abd4-d00d66bbf757',NULL),(3,3,5,3,NULL,'2012-09-07 00:00:00',2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Test',NULL,NULL,1,'2012-09-17 13:10:07',0,NULL,NULL,NULL,'600eb2e0-0100-11e2-a21f-0800200c9a66',NULL),(4,3,6,3,NULL,'2012-09-07 00:00:00',2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Test',NULL,NULL,1,'2012-09-17 13:10:07',0,NULL,NULL,NULL,'23c786d0-0101-11e2-a21f-0800200c9a66',NULL);
