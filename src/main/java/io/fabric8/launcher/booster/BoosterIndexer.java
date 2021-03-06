/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package io.fabric8.launcher.booster;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.fabric8.launcher.booster.catalog.BoosterCatalogService;
import io.fabric8.launcher.booster.catalog.spi.NativeGitBoosterCatalogPathProvider;

/**
 * Indexes a Booster catalog and adds all its contents to a ZIP file
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
class BoosterIndexer {
    public static void main(String... args) throws Exception {
        String catalogRepository = args[0];
        String catalogRef = args[1];
        Path targetDir = Paths.get(args[2]);
        Path targetZip = Paths.get(args[3]);

        BoosterCatalogService build = new BoosterCatalogService.Builder()
                .pathProvider(new NativeGitBoosterCatalogPathProvider(catalogRepository, catalogRef, targetDir))
                .build();

        build.index().get();
        build.prefetchBoosters().get();
        try (OutputStream os = Files.newOutputStream(targetZip)) {
            io.fabric8.launcher.booster.Files.zip("", targetDir, os,
                                                  (path) -> !BoosterCatalogService.EXCLUDED_PROJECT_FILES.contains(path.toFile().getName().toLowerCase()));
        }
    }

}
