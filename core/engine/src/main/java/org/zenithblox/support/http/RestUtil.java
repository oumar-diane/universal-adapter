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
package org.zenithblox.support.http;

import org.zenithblox.util.StringHelper;

import java.util.Locale;

public final class RestUtil {

    /**
     * Used for validating incoming REST calls whether Zwangine can process according to consumes/produces and
     * Accept/Content-Type headers.
     */
    public static boolean isValidOrAcceptedContentType(String valid, String target) {
        if (valid == null || target == null) {
            return true;
        }

        // Any MIME type
        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Accept#Directives
        if (target.contains("*/*")) {
            return true;
        }

        //  content-type is before optional charset
        target = StringHelper.before(target, ";", target);

        valid = valid.toLowerCase(Locale.ENGLISH);
        target = target.toLowerCase(Locale.ENGLISH);

        if (valid.contains(target)) {
            return true;
        }

        // try each part of the target
        for (String part : target.split(",")) {
            part = part.trim();
            if (valid.contains(part)) {
                return true;
            }
        }

        return false;
    }

}
