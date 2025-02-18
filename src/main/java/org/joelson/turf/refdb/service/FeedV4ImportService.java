package org.joelson.turf.refdb.service;

import org.joelson.turf.turfgame.FeedObject;
import org.joelson.turf.turfgame.apiv4.FeedChat;
import org.joelson.turf.turfgame.apiv4.FeedMedal;
import org.joelson.turf.turfgame.apiv4.FeedTakeover;
import org.joelson.turf.turfgame.apiv4.FeedZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public final class FeedV4ImportService extends AbstractFeedImportService {

    private final static Logger LOGGER = LoggerFactory.getLogger(FeedV4ImportService.class);

    public static final String VERSION = "v4";

    public static final Map<String, Class<? extends FeedObject>> TYPES_TO_HANDLE = Map.of(
            "chat", FeedChat.class,
            "medal", FeedMedal.class,
            "takeover", FeedTakeover.class,
            "zone", FeedZone.class);

    public FeedV4ImportService() {
        super(VERSION, TYPES_TO_HANDLE, LOGGER);
    }

    @Override
    protected void handleFeedObject(FeedObject feedObject) {
        switch (feedObject) {
            case FeedChat feedChat -> increaseChatsHandled();
            case FeedMedal feedMedal -> increaseMedalsHandled();
            case FeedTakeover feedTakeover -> increaseTakeoversHandled();
            case FeedZone feedZone -> increaseZonesHandled();
            default -> throw new IllegalArgumentException(
                    "Can not handle feedObject of class " + feedObject.getClass().getName());
        }
    }
}
