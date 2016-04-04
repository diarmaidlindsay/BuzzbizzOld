package jp.pulseanddecibels.buzbiz.models;

/**
 * Created by Diarmaid Lindsay on 2016/04/01.
 * Copyright Pulse and Decibels 2016
 */
public enum ConnectionType {
    REMOTE("remote"),
    LOCAL("local");

    String name;

    ConnectionType(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public static ConnectionType getConnectionTypeMatching(String name) {
        ConnectionType type = null;
        try {
            for(ConnectionType ct : ConnectionType.values()) {
                if(ct.name.equals(name)) {
                    type = ct;
                    break;
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return LOCAL;
        }
        return type;
    }
}
