/*
 *  ====================================================================
 *    Licensed to the Apache Software Foundation (ASF) under one or more
 *    contributor license agreements.  See the NOTICE file distributed with
 *    this work for additional information regarding copyright ownership.
 *    The ASF licenses this file to You under the Apache License, Version 2.0
 *    (the "License"); you may not use this file except in compliance with
 *    the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 * ====================================================================
 */
package org.apache.poi.xslf.usermodel;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * test common operations on containers of shapes (sheets and groups of shapes)
 *
 * @author Yegor Kozlov
 */
public class TestXSLFShapeContainer {

    @SuppressWarnings("unused")
    public void verifyContainer(XSLFShapeContainer container) {
        container.clear();
        assertEquals(0, container.getShapes().size());

        XSLFGroupShape shape1 = container.createGroup();
        assertEquals(1, container.getShapes().size());

        XSLFTextBox shape2 = container.createTextBox();
        assertEquals(2, container.getShapes().size());

        XSLFAutoShape shape3 = container.createAutoShape();
        assertEquals(3, container.getShapes().size());

        XSLFConnectorShape shape4 = container.createConnector();
        assertEquals(4, container.getShapes().size());

        container.clear();
        assertEquals(0, container.getShapes().size());
    }

    @Test
    public void testSheet() {
        XMLSlideShow ppt = new XMLSlideShow();
        XSLFSheet sheet = ppt.createSlide();
        verifyContainer(sheet);


        XSLFGroupShape group = sheet.createGroup();
        verifyContainer(group);

    }
}