/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zentihblox.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox.language.csimple;

import org.zenithblox.ZwangineContext;
import org.zenithblox.Exchange;
import org.zenithblox.Message;

/**
 * Method that will be executed for csimple expressions and which has been source code generated to implement the logic
 * in Java code.
 *
 * @see CSimpleExpression
 */
public interface CSimpleMethod {

    Object evaluate(ZwangineContext context, Exchange exchange, Message message, Object body)
            throws Exception;

}
