/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.internal.scan;

import org.gradle.internal.scan.clock.BuildScanTimeProvider;
import org.gradle.internal.scan.clock.DefaultBuildScanTimeProvider;
import org.gradle.internal.scan.config.BuildScanConfigServices;
import org.gradle.internal.service.ServiceRegistration;
import org.gradle.internal.service.scopes.AbstractPluginServiceRegistry;
import org.gradle.internal.time.TimeProvider;

public class BuildScanServices extends AbstractPluginServiceRegistry {

    @Override
    public void registerGlobalServices(ServiceRegistration registration) {
        registration.addProvider(new Object() {
            BuildScanTimeProvider createTimeProvider(TimeProvider timeProvider) {
                return new DefaultBuildScanTimeProvider(timeProvider);
            }
        });
    }

    @Override
    public void registerBuildTreeServices(ServiceRegistration registration) {
        registration.addProvider(new BuildScanConfigServices());
    }
}