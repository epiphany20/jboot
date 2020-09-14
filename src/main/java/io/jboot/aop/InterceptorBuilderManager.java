/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.aop;

import com.jfinal.aop.AopFactory;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.InterceptorManager;
import io.jboot.aop.cglib.JbootCglibProxyFactory;
import io.jboot.core.weight.WeightUtil;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InterceptorBuilderManager extends AopFactory {


    private static InterceptorBuilderManager me = new InterceptorBuilderManager();


    public static InterceptorBuilderManager me() {
        return me;
    }


    private List<InterceptorBuilder> interceptorBuilders = new CopyOnWriteArrayList();


    public List<InterceptorBuilder> getInterceptorBuilders() {
        return interceptorBuilders;
    }



    public void addInterceptorBuilder(InterceptorBuilder interceptorBuilder) {
        if (interceptorBuilder == null) {
            throw new NullPointerException("interceptorBuilder must not be null.");
        }
        this.interceptorBuilders.add(interceptorBuilder);
        WeightUtil.sort(this.interceptorBuilders);

        JbootCglibProxyFactory.IntersCache.clear();
    }




    public void addInterceptorBuilders(Collection<InterceptorBuilder> interceptorBuilders) {
        if (interceptorBuilders == null) {
            throw new NullPointerException("interceptorBuilder must not be null.");
        }
        this.interceptorBuilders.addAll(interceptorBuilders);
        WeightUtil.sort(this.interceptorBuilders);

        JbootCglibProxyFactory.IntersCache.clear();
    }




    public Interceptor[] build(Class targetClass, Method method, Interceptor[] interceptors) {
        if (interceptorBuilders != null && interceptorBuilders.size() > 0) {
            LinkedList<Interceptor> list = toLinkedList(interceptors);
            for (InterceptorBuilder builder : interceptorBuilders) {
                builder.build(targetClass, method, list);
            }
            return list.isEmpty() ? InterceptorManager.NULL_INTERS : list.toArray(new Interceptor[0]);
        }

        return interceptors;
    }




    private static LinkedList<Interceptor> toLinkedList(Interceptor[] interceptors) {
        LinkedList<Interceptor> linkedList = new LinkedList<>();
        if (interceptors != null) {
            for (Interceptor interceptor : interceptors) {
                linkedList.add(interceptor);
            }
        }
        return linkedList;
    }

}