package com.murtz.customVideoPlayer.datamodel;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Murtuza.Saifee on 29-Jun-18.
 */

@Getter
@Setter
public class LineupsModel {

    private LineupsBean Lineups;

    @Getter
    @Setter
    public class LineupsBean {
        private boolean Success;
        private DatabeanModel Data;
    }
}
