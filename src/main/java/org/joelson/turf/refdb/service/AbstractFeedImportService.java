package org.joelson.turf.refdb.service;

import org.joelson.turf.turfgame.FeedObject;
import org.joelson.turf.turfgame.util.DefaultFeedContentErrorHandler;
import org.joelson.turf.turfgame.util.DefaultFeedContentLoggerErrorHandler;
import org.joelson.turf.turfgame.util.FeedsReader;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

abstract class AbstractFeedImportService {

    private final FeedsReader feedsReader;
    private final DefaultFeedContentErrorHandler errorHandler;
    private final String version;
    private final Logger logger;

    private int filesHandled = 0;
    private int chatsHandled = 0;
    private int chatsAdded = 0;
    private int medalsHandled = 0;
    private int medalsAdded = 0;
    private int takeoversHandled = 0;
    private int takeoversAdded = 0;
    private int zonesHandled = 0;
    private int zonesAdded = 0;

    protected AbstractFeedImportService(
            String version, Map<String, Class<? extends FeedObject>> typesToHandle, Logger logger) {
        if (Objects.requireNonNull(version).isEmpty()) {
            throw new IllegalArgumentException("String version can not be empty.");
        }
        this.version = version;
        this.logger = Objects.requireNonNull(logger);
        this.errorHandler = new DefaultFeedContentLoggerErrorHandler(logger);
        this.feedsReader = new FeedsReader(typesToHandle, errorHandler);
    }

    public void importFeedPath(Path path) {
        logger.info("Importing data from '{}'", path);
        int filesHandledBefore = filesHandled;
        int chatsHandledBefore = chatsHandled;
        int chatsAddedBefore = chatsAdded;
        int medalsHandledBefore = medalsHandled;
        int medalsAddedBefore = medalsAdded;
        int takeoversHandledBefore = takeoversHandled;
        int takeoversAddedBefore = takeoversAdded;
        int zoneHandledBefore = zonesHandled;
        int zonesAddedBefore = zonesAdded;
        try {
            feedsReader.handleFeedObjectPath(path, this::acceptPath, this::handleFeedObject);
        } catch (IOException e) {
            logger.error("Error importing data from '{}'", path);
        }
        logger.info("    {}",
                progressNumbersToString(filesHandledBefore, chatsHandledBefore, chatsAddedBefore, medalsHandledBefore,
                        medalsAddedBefore, takeoversHandledBefore, takeoversAddedBefore, zoneHandledBefore,
                        zonesAddedBefore));
    }


    private boolean acceptPath(Path path) {
        String pathString = path.toString();
        String version = getLastVersion(pathString);
        if (version != null && version.equals(this.version)) {
            filesHandled += 1;
            if (filesHandled % 100 == 0) {
                logger.info("    reading path {} - {}", path, progressNumbersToString());
            }
            return true;
        } else {
            return false;
        }
    }

    private String getLastVersion(String pathString) {
        int lastIndexOfV = pathString.lastIndexOf('v');
        while (lastIndexOfV >= 0) {
            if (lastIndexOfV + 1 < pathString.length() && Character.isDigit(pathString.charAt(lastIndexOfV + 1))) {
                return pathString.substring(lastIndexOfV, lastIndexOfV + 2);
            }
            lastIndexOfV = pathString.lastIndexOf('v', lastIndexOfV - 1);
        }
        return null;
    }

    protected abstract void handleFeedObject(FeedObject feedObject);

    protected final void increaseChatsHandled() {
        chatsHandled += 1;
    }

    protected final void increaseChatsAdded() {
        chatsAdded += 1;
    }

    protected final void increaseMedalsHandled() {
        medalsHandled += 1;
    }

    protected final void increaseMedalsAdded() {
        medalsAdded += 1;
    }

    protected final void increaseTakeoversHandled() {
        takeoversHandled += 1;
    }

    protected final void increaseTakeoversAdded() {
        takeoversAdded += 1;
    }

    protected final void increaseZonesHandled() {
        zonesHandled += 1;
    }

    protected final void increaseZonesAdded() {
        zonesAdded += 1;
    }

    public void logStatistics() {
        logger.info(progressNumbersToString());
    }

    public void messageErrorPaths(int maxPaths) {
        errorHandler.messageErrorPaths(maxPaths);
    }

    private String progressNumbersToString() {
        return progressNumbersToString(0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    private String progressNumbersToString(
            int filesHandledBefore, int chatsHandledBefore, int chatsAddedBefore, int medalsHandledBefore,
            int medalsAddedBefore, int takeoversHandledBefore, int takeoversAddedBefore, int zonesHandledBefore,
            int zonesAddedBefore) {
        return String.format(
                "filesHandled=%d, chatsHandled=%d, chatsAdded=%d, medalsHandled=%d, medalsAdded=%d, "
                        + "takeoversHandled=%d, takeoversAdded=%d, zonesHandled=%d, zonesAdded=%d",
                filesHandled - filesHandledBefore, chatsHandled - chatsHandledBefore, chatsAdded - chatsAddedBefore,
                medalsHandled - medalsHandledBefore, medalsAdded - medalsAddedBefore,
                takeoversHandled - takeoversHandledBefore, takeoversAdded - takeoversAddedBefore,
                zonesHandled - zonesHandledBefore, zonesAdded - zonesAddedBefore);
    }
}
