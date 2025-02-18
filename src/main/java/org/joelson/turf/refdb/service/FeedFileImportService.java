package org.joelson.turf.refdb.service;

import org.joelson.turf.turfgame.util.FeedsPathComparator;
import org.joelson.turf.util.FilesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class FeedFileImportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeedFileImportService.class);

    @Autowired
    FeedV4ImportService feedV4ImportService;

    @Autowired
    FeedV5ImportService feedV5ImportService;

    @Autowired
    FeedV6ImportService feedV6ImportService;

    public void importFeedFiles(String[] filenames) {
        List<Path> failedPaths = new ArrayList<>();
        for (String filename : filenames) {
            Path path = Path.of(filename);
            if (Files.exists(path) && Files.isReadable(path)) {
                if (Files.isRegularFile(path)) {
                    importFeedPath(path);
                } else {
                    try {
                        FilesUtil.forEachFile(path, false, new FeedsPathComparator(), this::importFeedPath);
                    } catch (IOException e) {
                        LOGGER.error("Error reading from path {}.", path, e);
                        failedPaths.add(path);
                    }
                }
            } else {
                LOGGER.warn("Path {} is not a file that can be read.", path);
                failedPaths.add(path);
            }
        }
        if (filenames.length > 0) {
            LOGGER.info("Done importing feed files.");
            feedV4ImportService.logStatistics();
            feedV5ImportService.logStatistics();
            feedV6ImportService.logStatistics();
            messageErrorPaths(failedPaths, Math.min(20, failedPaths.size()));
            feedV4ImportService.messageErrorPaths(20);
            feedV5ImportService.messageErrorPaths(20);
            feedV6ImportService.messageErrorPaths(20);
        }
    }

    private void importFeedPath(Path path) {
        feedV4ImportService.importFeedPath(path);
        feedV5ImportService.importFeedPath(path);
        feedV6ImportService.importFeedPath(path);
    }

    private void messageErrorPaths(List<Path> failedPaths, int maxPaths) {
        if (!failedPaths.isEmpty()) {
            LOGGER.info("Failures in file paths: {}", failedPaths.size());
        }
        failedPaths.stream().limit(maxPaths).forEach(path -> LOGGER.info("    {}", path));
        if (failedPaths.size() > maxPaths) {
            LOGGER.info("    ...");
        }
    }
}
