/*
 * Copyright 2018 the original author or authors.
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

package org.gradle.ide.visualstudio.internal;

import org.gradle.api.file.FileCollection;

import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * A model-agnostic adapter for binary information
 */
public interface VisualStudioTargetBinary {
    /**
     * Returns the project path for this binary
     */
    String getProjectPath();

    /**
     * Returns the name of the component associated with this binary
     */
    String getComponentName();

    /**
     * Returns the visual studio project name associated with this binary
     */
    String getVisualStudioProjectName();

    /**
     * Returns the visual studio project configuration name associated with this binary
     */
    String getVisualStudioConfigurationName();

    /**
     * Returns the project suffix to use when naming Visual Studio projects
     */
    ProjectType getProjectType();

    /**
     * Returns the variant dimensions associated with this binary
     */
    List<String> getVariantDimensions();

    /**
     * Returns the source files associated with this binary
     */
    FileCollection getSourceFiles();

    /**
     * Returns the resource files associated with this binary
     */
    FileCollection getResourceFiles();

    /**
     * Returns the header files associated with this binary
     */
    FileCollection getHeaderFiles();

    /**
     * Returns whether or not this binary represents an executable
     */
    boolean isExecutable();

    /**
     * Returns a task that can be used to build this binary
     */
    String getBuildTaskPath();

    /**
     * Returns a task that can be used to clean the outputs of this binary
     */
    String getCleanTaskPath();

    /**
     * Returns whether or not this binary is a debuggable variant
     */
    boolean isDebuggable();

    /**
     * Returns the main product of this binary (i.e. executable or library file)
     */
    File getOutputFile();

    /**
     * Returns the compiler definitions that should be used with this binary
     */
    List<String> getCompilerDefines();

    /**
     * Returns the include paths that should be used with this binary
     */
    Set<File> getIncludePaths();


    enum ProjectType {
        EXE("Exe"), LIB("Lib"), DLL("Dll"), NONE("");

        private final String suffix;

        ProjectType(String suffix) {
            this.suffix = suffix;
        }

        public String getSuffix() {
            return suffix;
        }
    }
}
