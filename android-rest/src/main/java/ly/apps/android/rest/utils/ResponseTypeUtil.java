/*
 * Copyright (C) 2013 47 Degrees, LLC
 * http://47deg.com
 * http://apps.ly
 * hello@47deg.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ly.apps.android.rest.utils;

import ly.apps.android.rest.client.Callback;

import java.lang.reflect.*;

/**
 * Some utils to instrospect generics at runtime
 */
public class ResponseTypeUtil {

    public static Type parseObjectResponseType(Object object) {
        Type responseObjectType = null;
        // Asynchronous methods should have a Callback type as the last argument.
        Type lastArgType = null;
        TypeVariable[] parameterTypes = object.getClass().getTypeParameters();
        if (parameterTypes.length > 0) {
            lastArgType = parameterTypes[parameterTypes.length - 1];
        }
        lastArgType = Types.getSupertype(lastArgType, Types.getRawType(lastArgType), Callback.class);
        if (lastArgType instanceof ParameterizedType) {
            responseObjectType = ResponseTypeUtil.getParameterUpperBound((ParameterizedType) lastArgType);
            return responseObjectType;
        }
        throw new IllegalArgumentException("Callback is not parameterized");
    }

    public static Type parseResponseType(Method method) {
        Type responseObjectType = null;
        // Asynchronous methods should have a Callback type as the last argument.
        Type lastArgType = null;
        Type[] parameterTypes = method.getGenericParameterTypes();
        if (parameterTypes.length > 0) {
            lastArgType = parameterTypes[parameterTypes.length - 1];
        }
        lastArgType = Types.getSupertype(lastArgType, Types.getRawType(lastArgType), Callback.class);
        if (lastArgType instanceof ParameterizedType) {
            responseObjectType = ResponseTypeUtil.getParameterUpperBound((ParameterizedType) lastArgType);
            return responseObjectType;
        }
        throw new IllegalArgumentException("Callback is not parameterized");
    }

    public static Type getParameterUpperBound(ParameterizedType type) {
        Type[] types = type.getActualTypeArguments();
        for (int i = 0; i < types.length; i++) {
            Type paramType = types[i];
            if (paramType instanceof WildcardType) {
                types[i] = ((WildcardType) paramType).getUpperBounds()[0];
            }
        }
        return types[0];
    }

}
