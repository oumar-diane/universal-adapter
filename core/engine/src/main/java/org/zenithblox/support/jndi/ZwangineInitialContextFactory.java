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
package org.zenithblox.support.jndi;

import org.zenithblox.util.CastUtils;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import java.util.Hashtable;

/**
 * A factory of the Zwangine {@link javax.naming.InitialContext} which allows a {@link java.util.Map} to be used to create
 * a JNDI context.
 * <p/>
 * This implementation is prototype based, by creating a <b>new</b> context on each call to
 * {@link #getInitialContext(Hashtable)}.
 */
public class ZwangineInitialContextFactory implements InitialContextFactory {

    /**
     * Creates a new context with the given environment.
     *
     * @param  environment     the environment, must not be <tt>null</tt>
     * @return                 the created context.
     * @throws NamingException is thrown if creation failed.
     */
    @Override
    public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
        try {
            return new JndiContext(CastUtils.cast(environment, String.class, Object.class));
        } catch (Exception e) {
            if (e instanceof NamingException namingException) {
                throw namingException;
            }
            NamingException exception = new NamingException(e.getMessage());
            exception.initCause(e);
            throw exception;
        }
    }
}
