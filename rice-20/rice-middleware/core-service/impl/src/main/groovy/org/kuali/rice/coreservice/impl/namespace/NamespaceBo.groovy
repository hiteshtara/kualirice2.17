/**
 * Copyright 2005-2013 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.coreservice.impl.namespace;


import groovy.transform.EqualsAndHashCode
import org.hibernate.annotations.Type
import org.kuali.rice.coreservice.api.namespace.Namespace
import org.kuali.rice.coreservice.framework.namespace.NamespaceEbo
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name="KRCR_NMSPC_T")
@EqualsAndHashCode
class NamespaceBo extends PersistableBusinessObjectBase implements NamespaceEbo {

    private static final long serialVersionUID = 1L;

    @Column(name="APPL_ID")
	def String applicationId;

    @Id
    @Column(name="NMSPC_CD")
    def String code;

    @Column(name="NM")
    def String name;

    @Type(type="yes_no")
    @Column(name="ACTV_IND")
    def boolean active = true;

    /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    static Namespace to(NamespaceBo bo) {
        if (bo == null) {
            return null
        }

        return Namespace.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     * @param im immutable object
     * @return the mutable bo
     */
    static NamespaceBo from(Namespace im) {
        if (im == null) {
            return null
        }

        NamespaceBo bo = new NamespaceBo()
        bo.applicationId = im.applicationId
        bo.active = im.active
        bo.code = im.code
        bo.name = im.name
        bo.versionNumber = im.versionNumber
		bo.objectId = im.objectId

        return bo
    }

    // Using class level annotation instead
//    @Override
//    boolean equals(Object obj) {
//        return EqualsBuilder.reflectionEquals(this, obj);
//    }
//
//    @Override
//    int hashCode() {
//        return HashCodeBuilder.reflectionHashCode(this);
//    }
}

