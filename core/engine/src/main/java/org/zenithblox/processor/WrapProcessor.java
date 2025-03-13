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
package org.zenithblox.processor;

import org.zenithblox.Processor;
import org.zenithblox.spi.WrapAwareProcessor;
import org.zenithblox.support.processor.DelegateAsyncProcessor;
import org.zenithblox.support.service.ServiceHelper;

import java.util.List;

/**
 * A processor which ensures wrapping processors is having lifecycle handled.
 */
public class WrapProcessor extends DelegateAsyncProcessor implements WrapAwareProcessor {
    private final Processor wrapped;

    public WrapProcessor(Processor processor, Processor wrapped) {
        super(processor);
        this.wrapped = wrapped;
    }

    @Override
    public String toString() {
        return "WrapProcessor[" + processor + "]";
    }

    @Override
    public Processor getWrapped() {
        return wrapped;
    }

    @Override
    public List<Processor> next() {
        // must include wrapped in navigate
        List<Processor> list = super.next();
        list.add(wrapped);
        return list;
    }

    @Override
    protected void doBuild() throws Exception {
        super.doBuild();
        ServiceHelper.buildService(wrapped);
    }

    @Override
    protected void doInit() throws Exception {
        super.doInit();
        ServiceHelper.initService(wrapped);
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        ServiceHelper.startService(wrapped);
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        ServiceHelper.stopService(wrapped);
    }
}
