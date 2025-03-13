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
package org.zenithblox.model.dataformat;

import org.zenithblox.builder.DataFormatBuilder;
import org.zenithblox.model.DataFormatDefinition;
import org.zenithblox.spi.Metadata;

/**
 * Transform from ROME SyndFeed Java Objects to XML and vice-versa.
 */
@Metadata(firstVersion = "2.1.0", label = "dataformat,transformation", title = "RSS")
public class RssDataFormat extends DataFormatDefinition {

    public RssDataFormat() {
        super("rss");
    }

    protected RssDataFormat(RssDataFormat source) {
        super(source);
    }

    @Override
    public RssDataFormat copyDefinition() {
        return new RssDataFormat(this);
    }

    /**
     * {@code Builder} is a specific builder for {@link RssDataFormat}.
     */
    public static class Builder implements DataFormatBuilder<RssDataFormat> {
        @Override
        public RssDataFormat end() {
            return new RssDataFormat();
        }
    }
}
