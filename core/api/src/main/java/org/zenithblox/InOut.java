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
 * Marks a method as being {@link ExchangePattern#InOut} when a class or interface has been annotated with
 * {@link InOnly} when using <a href="http://zwangine.zwangine.org/bean-integration.html">Bean Integration</a>.
 *
 * This annotation is only intended to be used on methods which the class or interface has been annotated with a default
 * exchange pattern annotation such as {@link InOnly} or {@link Pattern}
 *
 * See the <a href="using-exchange-pattern-annotations.html">using exchange pattern annotations</a> for more details on
 * how the overloading rules work.
 *
 * @see org.zenithblox.ExchangePattern
 * @see org.zenithblox.Exchange#getPattern()
 * @see InOnly
 * @see Pattern
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ ElementType.METHOD })
@Pattern(ExchangePattern.InOut)
public @interface InOut {
}
