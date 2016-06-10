/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collector;

import org.jdbi.v3.collector.BuiltInCollectorFactories;
import org.jdbi.v3.collector.CollectorFactory;

/**
 * Registry of collector factories.
 * Contains a set of collector factories, registered by the application.
 */
class CollectorFactoryRegistry {

    private final List<CollectorFactory> factories = new CopyOnWriteArrayList<>();

    CollectorFactoryRegistry() {
        factories.addAll(BuiltInCollectorFactories.get());
    }

    private CollectorFactoryRegistry(CollectorFactoryRegistry that) {
        factories.addAll(that.factories);
    }

    void register(CollectorFactory factory) {
        factories.add(0, factory);
    }

    Optional<Collector<?, ?, ?>> findCollectorFor(Type containerType) {
        return findFactoryFor(containerType)
                .map(f -> f.build(containerType));
    }

    Optional<Type> elementTypeFor(Type containerType) {
        return findFactoryFor(containerType)
                .flatMap(f -> f.elementType(containerType));
    }

    private Optional<CollectorFactory> findFactoryFor(Type containerType) {
        return factories.stream()
                .filter(f -> f.accepts(containerType))
                .findFirst();
    }

    static CollectorFactoryRegistry copyOf(CollectorFactoryRegistry registry) {
        return new CollectorFactoryRegistry(registry);
    }
}
