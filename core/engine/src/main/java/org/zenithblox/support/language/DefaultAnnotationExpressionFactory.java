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
package org.zenithblox.support.language;

import org.zenithblox.ZwangineContext;
import org.zenithblox.Expression;
import org.zenithblox.Predicate;
import org.zenithblox.spi.Language;
import org.zenithblox.support.ObjectHelper;
import org.zenithblox.support.PredicateToExpressionAdapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Default implementation of the {@link AnnotationExpressionFactory}.
 */
public class DefaultAnnotationExpressionFactory implements AnnotationExpressionFactory {

    @Override
    public Expression createExpression(
            ZwangineContext zwangineContext, Annotation annotation, LanguageAnnotation languageAnnotation,
            Class<?> expressionReturnType) {
        String languageName = languageAnnotation.language();
        Language language = zwangineContext.resolveLanguage(languageName);
        if (language == null) {
            throw new IllegalArgumentException("Cannot find the language: " + languageName + " on the classpath");
        }
        String expression = getExpressionFromAnnotation(annotation);

        if (expressionReturnType == Boolean.class || expressionReturnType == boolean.class) {
            Predicate predicate = language.createPredicate(expression);
            return PredicateToExpressionAdapter.toExpression(predicate);
        } else {
            return language.createExpression(expression);
        }
    }

    protected String getExpressionFromAnnotation(Annotation annotation) {
        Object value = getAnnotationObjectValue(annotation, "value");
        if (value == null) {
            throw new IllegalArgumentException("Cannot determine the expression from the annotation: " + annotation);
        }
        return value.toString();
    }

    /**
     * @param  annotation The annotation to get the value of
     * @param  methodName The annotation name
     * @return            The value of the annotation
     */
    protected Object getAnnotationObjectValue(Annotation annotation, String methodName) {
        try {
            Method method = annotation.annotationType().getDeclaredMethod(methodName);
            return ObjectHelper.invokeMethod(method, annotation);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                    "Cannot determine the Object value of the annotation: " + annotation
                                               + " as it does not have the method: " + methodName + "() method",
                    e);
        }
    }
}
