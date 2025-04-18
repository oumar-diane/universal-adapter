/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zwangine.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox;

import java.lang.annotation.*;

/**
 * Marks methods as being {@link ExchangePattern#InOnly} for one way asynchronous invocation when using
 * <a href="http://zwangine.zwangine.org/bean-integration.html">Bean Integration</a> to overload the default value which is
 * {@link ExchangePattern#InOut} for request/reply if no annotations are used.
 *
 * This annotation can be added to individual methods or added to a class or interface to act as a default for all
 * methods within the class or interface.
 *
 * See the <a href="using-exchange-pattern-annotations.html">using exchange pattern annotations</a> for more details on
 * how the overloading rules work.
 *
 * @see org.zenithblox.ExchangePattern
 * @see org.zenithblox.Exchange#getPattern()
 * @see InOut
 * @see Pattern
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ ElementType.TYPE, ElementType.METHOD })
@Pattern(ExchangePattern.InOnly)
public @interface InOnly {
}
