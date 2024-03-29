--
-- Copyright 2005-2013 The Kuali Foundation
--
-- Licensed under the Educational Community License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
-- http://www.opensource.org/licenses/ecl2.php
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

-------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------
--
--  Please see README.TXT for special instructions about the 2.2.0 to 2.2.1 upgrade
--
-------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------

--
-- KULRICE-8573: Add session id to locks and delete these locks when session is destroyed.
--

ALTER TABLE KRNS_PESSIMISTIC_LOCK_T ADD SESN_ID VARCHAR2(40) DEFAULT '' NOT NULL
/
