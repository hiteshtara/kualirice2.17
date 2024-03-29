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
package org.kuali.rice.core.api.uif

import static org.junit.Assert.*
import org.junit.Test
import org.kuali.rice.core.test.JAXBAssert;

class RemotableHiddenInputTest {
               	private static final String XML =
        """<hiddenInput xmlns="http://rice.kuali.org/core/v2_0">
          </hiddenInput>""";

    @Test
    void testHappyPath() {
        RemotableHiddenInput o = RemotableHiddenInput.Builder.create().build();
        assertNotNull(o);
    }

    @Test
	void testJAXB() {
		RemotableHiddenInput o = create().build();
		JAXBAssert.assertEqualXmlMarshalUnmarshal(o, XML, RemotableHiddenInput.class);
	}

    private RemotableHiddenInput.Builder create() {
		RemotableHiddenInput.Builder o = RemotableHiddenInput.Builder.create();
        return o
	}
}
