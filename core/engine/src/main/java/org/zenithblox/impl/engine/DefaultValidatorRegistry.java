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
package org.zenithblox.impl.engine;

import org.zenithblox.ZwangineContext;
import org.zenithblox.spi.DataType;
import org.zenithblox.spi.Validator;
import org.zenithblox.spi.ValidatorKey;
import org.zenithblox.spi.ValidatorRegistry;
import org.zenithblox.support.ZwangineContextHelper;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.util.ObjectHelper;

/**
 * Default implementation of {@link org.zenithblox.spi.ValidatorRegistry}.
 */
public class DefaultValidatorRegistry extends AbstractDynamicRegistry<ValidatorKey, Validator>
        implements ValidatorRegistry {

    public DefaultValidatorRegistry(ZwangineContext context) {
        super(context, ZwangineContextHelper.getMaximumValidatorCacheSize(context));
    }

    @Override
    public Validator resolveValidator(ValidatorKey key) {
        Validator answer = get(key);
        if (answer == null && ObjectHelper.isNotEmpty(key.getType().getName())) {
            answer = get(new ValidatorKey(new DataType(key.getType().getScheme())));
        }
        return answer;
    }

    @Override
    public boolean isStatic(DataType type) {
        return isStatic(new ValidatorKey(type));
    }

    @Override
    public boolean isDynamic(DataType type) {
        return isDynamic(new ValidatorKey(type));
    }

    @Override
    public String toString() {
        return "ValidatorRegistry for " + context.getName() + " [capacity: " + maxCacheSize + "]";
    }

    @Override
    public Validator put(ValidatorKey key, Validator obj) {
        // ensure validator is started before its being used
        ServiceHelper.startService(obj);
        return super.put(key, obj);
    }

}
