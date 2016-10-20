/*
 *    Copyright (c) 2016 Robert Cooper
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package net.kebernet.skillz.impl;

import com.google.common.collect.Lists;
import org.reflections.vfs.Vfs;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by rcooper on 10/20/16.
 */
public class ReflectFilter {

    /**
     * OSX contains file:// resources on the classpath including .mar and .jnilib files.
     *
     * Reflections use of Vfs doesn't recognize these URLs and logs warns when it sees them. By registering those file endings, we supress the warns.
     */
    public static void registerUrlTypes() {
        final List<Vfs.UrlType> urlTypes = Lists.newArrayList();
        urlTypes.add(new EmptyIfFileEndingsUrlType(".zip", ".jnilib"));
        urlTypes.addAll(Arrays.asList(Vfs.DefaultUrlTypes.values()));

        Vfs.setDefaultURLTypes(urlTypes);
    }

    private static class EmptyIfFileEndingsUrlType implements Vfs.UrlType {

        private final List<String> fileEndings;

        private EmptyIfFileEndingsUrlType(final String... fileEndings) {

            this.fileEndings = Lists.newArrayList(fileEndings);
        }

        public boolean matches(URL url) {
            final String protocol = url.getProtocol();
            final String externalForm = url.toExternalForm();
            if (!protocol.equals("file")) {
                return false;
            }
            for (String fileEnding : fileEndings) {
                if (externalForm.endsWith(fileEnding))
                    return true;
            }
            return false;
        }

        public Vfs.Dir createDir(final URL url) throws Exception {
            return emptyVfsDir(url);
        }

        private static Vfs.Dir emptyVfsDir(final URL url) {
            return new Vfs.Dir() {
                @Override
                public String getPath() {

                    return url.toExternalForm();
                }

                @Override
                public Iterable<Vfs.File> getFiles() {
                    return Collections.EMPTY_LIST;
                }

                @Override
                public void close() {

                }
            };
        }
    }
}
