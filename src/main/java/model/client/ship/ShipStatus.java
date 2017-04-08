package model.client.ship;

/**
 * @author Ilya Ivanov
 */
public enum ShipStatus {
    IDLE("Idle"), ON_WAY("On way"), LOADING("Loading"), UNLOADING("Unloading");

    ShipStatus(String message) {
        this.message = message;
    }

    String message;

    public String getMessage() {
        return message;
    }
}
