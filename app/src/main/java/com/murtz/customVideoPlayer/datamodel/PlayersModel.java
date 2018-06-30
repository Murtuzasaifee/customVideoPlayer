package com.murtz.customVideoPlayer.datamodel;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Murtuza.Saifee on 29-Jun-18.
 */

@Getter
@Setter
public class PlayersModel {

    private int Order;
    private boolean StartInField;
    private String Role;
    private boolean IsCaptain;
    private String JerseyNumber;
    private int Id;
    private String Name;
    private int XCoordinate;
    private int YCoordinate;
}
