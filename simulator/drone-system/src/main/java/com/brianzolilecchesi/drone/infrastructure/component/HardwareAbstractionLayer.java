package com.brianzolilecchesi.drone.infrastructure.component;

import com.brianzolilecchesi.drone.domain.component.Altimeter;
import com.brianzolilecchesi.drone.domain.component.Battery;
import com.brianzolilecchesi.drone.domain.component.Camera;
import com.brianzolilecchesi.drone.domain.component.GPS;
import com.brianzolilecchesi.drone.domain.component.Motor;
import com.brianzolilecchesi.drone.domain.component.Radio;

public class HardwareAbstractionLayer {
    
    private final Battery battery;
    private final Radio radio;
    private final Camera camera;
    private final GPS gps;
    private final Altimeter altimeter;
    private final Motor motor;

    public HardwareAbstractionLayer(
            Battery battery,
            Radio radio,
            Camera camera,
            GPS gps,
            Altimeter altimeter,
            Motor motor) {
        this.battery = battery;
        this.radio = radio;
        this.camera = camera;
        this.gps = gps;
        this.altimeter = altimeter;
        this.motor = motor;
    }

    public Battery getBattery() {
        return battery;
    }

    public Radio getRadio() {
        return radio;
    }

    public Camera getCamera() {
        return camera;
    }

    public GPS getGps() {
        return gps;
    }

    public Altimeter getAltimeter() {
        return altimeter;
    }

    public Motor geMotor() {
        return motor;
    }
}
