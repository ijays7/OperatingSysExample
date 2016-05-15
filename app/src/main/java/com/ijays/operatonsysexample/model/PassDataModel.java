package com.ijays.operatonsysexample.model;

import java.io.Serializable;

/**
 * Created by ijaysdev on 16/5/15.
 */
public class PassDataModel implements Serializable {
    private static final long serialVersionUID = 6386637289833950363L;
    private String passData;

    public PassDataModel(String passData) {
        this.passData = passData;
    }

    public String getPassData() {
        return passData;
    }

    public void setPassData(String passData) {
        this.passData = passData;
    }

    @Override
    public String toString() {
        return passData;
    }
}
