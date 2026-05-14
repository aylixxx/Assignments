import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SmartHomeSystem system = new SmartHomeSystem();

        while (true) {
            String command = scanner.nextLine().trim();
            if (command.equals("end")) {
                break;
            }
            System.out.println(system.executeCommand(command));
        }
    }

    /**
     * Constants with the required values.
     */
    public static class Const {
        public static final int ID_0 = 0;
        public static final int ID_1 = 1;
        public static final int ID_2 = 2;
        public static final int ID_3 = 3;
        public static final int ID_4 = 4;
        public static final int ID_5 = 5;
        public static final int ID_6 = 6;
        public static final int ID_7 = 7;
        public static final int ID_8 = 8;
        public static final int ID_9 = 9;
        public static final int ID_20 = 20;
        public static final int ID_45 = 45;
    }
    enum Status {
        OFF, ON
    }

    enum LightColor {
        WHITE, YELLOW
    }

    enum BrightnessLevel {
        HIGH, MEDIUM, LOW
    }

    interface Controllable {
        boolean turnOff();

        boolean turnOn();

        boolean isOn();
    }

    interface Chargeable {
        boolean isCharging();

        boolean startCharging();

        boolean stopCharging();
    }

    abstract static class SmartDevice implements Controllable {
        private Status status;
        private final int deviceId;
        private int numberOfDevices;
        private final String deviceName;

        public SmartDevice(Status status, int deviceId, String deviceName) {
            this.status = status;
            this.deviceId = deviceId;
            this.deviceName = deviceName;
        }

        public int getDeviceId() {
            return deviceId;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        @Override
        public boolean turnOff() {
            if (status == Status.OFF) {
                return false;
            }
            status = Status.OFF;
            return true;
        }

        @Override
        public boolean turnOn() {
            if (status == Status.ON) {
                return false;
            }
            status = Status.ON;
            return true;
        }

        @Override
        public boolean isOn() {
            return status == Status.ON;
        }

        public abstract String displayStatus();

        public boolean checkStatusAccess() {
            return true;
        }
    }

    static class Heater extends SmartDevice {
        private int temperature;
        private static final int MAX_HEATER_TEMP = 30;
        private static final int MIN_HEATER_TEMP = 15;

        public Heater(Status status, int deviceId, String deviceName, int initialTemperature) {
            super(status, deviceId, deviceName);
            this.temperature = initialTemperature;
        }

        public int getTemperature() {
            return temperature;
        }

        public boolean setTemperature(int temperature) {
            if (temperature < MIN_HEATER_TEMP || temperature > MAX_HEATER_TEMP) {
                return false;
            }
            this.temperature = temperature;
            return true;
        }

        @Override
        public String displayStatus() {
            return String.format("Heater %d is %s and the temperature is %d.", getDeviceId(), getStatus(), temperature);
        }
    }

    static class Camera extends SmartDevice implements Chargeable {
        private static final int MAX_CAMERA_ANGLE = 60;
        private static final int MIN_CAMERA_ANGLE = -60;
        private boolean charging;
        private boolean recording;
        private int angle;

        public Camera(Status status, int deviceId, String deviceName, int initialAngle) {
            super(status, deviceId, deviceName);
            this.charging = false;
            this.recording = false;
            this.angle = initialAngle;
        }

        public boolean isRecording() {
            return recording;
        }

        public boolean startRecording() {
            if (recording) {
                return false;
            }
            recording = true;
            return true;
        }

        public boolean stopRecording() {
            if (!recording) {
                return false;
            }
            recording = false;
            return true;
        }

        public int getAngle() {
            return angle;
        }

        public boolean setCameraAngle(int angle) {
            if (angle < MIN_CAMERA_ANGLE || angle > MAX_CAMERA_ANGLE) {
                return false;
            }
            this.angle = angle;
            return true;
        }

        @Override
        public boolean isCharging() {
            return charging;
        }

        @Override
        public boolean startCharging() {
            if (charging) {
                return false;
            }
            charging = true;
            return true;
        }

        @Override
        public boolean stopCharging() {
            if (!charging) {
                return false;
            }
            charging = false;
            return true;
        }

        @Override
        public String displayStatus() {
            return String.format("Camera %d is %s, the angle is %d, the charging status is %s, and the recording "
                    + "status is %s.", getDeviceId(), getStatus(),
                    angle, charging ? "true" : "false", recording ? "true" : "false");
        }
    }

    static class Light extends SmartDevice implements Chargeable {
        private boolean charging;
        private BrightnessLevel brightnessLevel;
        private LightColor lightColor;

        public Light(Status status, int deviceId, String deviceName, BrightnessLevel initialBrightness,
                     LightColor initialColor) {
            super(status, deviceId, deviceName);
            this.charging = false;
            this.brightnessLevel = initialBrightness;
            this.lightColor = initialColor;
        }

        public BrightnessLevel getBrightnessLevel() {
            return brightnessLevel;
        }

        public void setBrightnessLevel(BrightnessLevel brightnessLevel) {
            this.brightnessLevel = brightnessLevel;
        }

        public LightColor getLightColor() {
            return lightColor;
        }

        public boolean setLightColor(LightColor lightColor) {
            this.lightColor = lightColor;
            return true;
        }

        @Override
        public boolean isCharging() {
            return charging;
        }

        @Override
        public boolean startCharging() {
            if (charging) {
                return false;
            }
            charging = true;
            return true;
        }

        @Override
        public boolean stopCharging() {
            if (!charging) {
                return false;
            }
            charging = false;
            return true;
        }

        @Override
        public String displayStatus() {
            return String.format("Light %d is %s, the color is %s, the charging status is %s,"
                            + " and the brightness level is %s.", getDeviceId(),
                    getStatus(), lightColor, charging ? "true" : "false", brightnessLevel);
        }
    }

    /**
     * The SmartHomeSystem class represents a system that manages multiple smart devices.
     * It provides functionalities to control and monitor various smart devices such as lights, cameras, and heaters.
     */
    static class SmartHomeSystem {
        private final List<SmartDevice> devices;

        public SmartHomeSystem() {
            devices = new ArrayList<>();
            initializeDevices();
        }
        /**
         * Initializes the smart devices with predefined settings.
         */
        private void initializeDevices() {
            devices.add(new Light(Status.ON, Const.ID_0, "Light", BrightnessLevel.LOW, LightColor.YELLOW));
            devices.add(new Light(Status.ON, Const.ID_1, "Light", BrightnessLevel.LOW, LightColor.YELLOW));
            devices.add(new Light(Status.ON, Const.ID_2, "Light", BrightnessLevel.LOW, LightColor.YELLOW));
            devices.add(new Light(Status.ON, Const.ID_3, "Light", BrightnessLevel.LOW, LightColor.YELLOW));

            devices.add(new Camera(Status.ON, Const.ID_4, "Camera", Const.ID_45));
            devices.add(new Camera(Status.ON, Const.ID_5, "Camera", Const.ID_45));

            devices.add(new Heater(Status.ON, Const.ID_6, "Heater", Const.ID_20));
            devices.add(new Heater(Status.ON, Const.ID_7, "Heater", Const.ID_20));
            devices.add(new Heater(Status.ON, Const.ID_8, "Heater", Const.ID_20));
            devices.add(new Heater(Status.ON, Const.ID_9, "Heater", Const.ID_20));
        }

        /**
         * Executes a command to control the smart devices.
         *
         * @param command the command string to be executed
         * @return the result of the command execution
         */
        public String executeCommand(String command) {
            String[] parts = command.split(" ");
            try {
                switch (parts[0]) {
                    case "DisplayAllStatus":
                        if (parts.length != 1) {
                            return "Invalid command";
                        }
                        return displayAllStatus();
                    case "TurnOn":
                        if (parts.length != Const.ID_3) {
                            return "Invalid command";
                        }
                        return turnOn(parts[1], Integer.parseInt(parts[2]));
                    case "TurnOff":
                        if (parts.length != Const.ID_3) {
                            return "Invalid command";
                        }
                        return turnOff(parts[1], Integer.parseInt(parts[2]));
                    case "StartCharging":
                        if (parts.length != Const.ID_3) {
                            return "Invalid command";
                        }
                        return startCharging(parts[1], Integer.parseInt(parts[2]));
                    case "StopCharging":
                        if (parts.length != Const.ID_3) {
                            return "Invalid command";
                        }
                        return stopCharging(parts[1], Integer.parseInt(parts[2]));
                    case "SetTemperature":
                        if (parts.length != Const.ID_4) {
                            return "Invalid command";
                        }
                        return setTemperature(parts[1], Integer.parseInt(parts[2]),
                                Integer.parseInt(parts[Const.ID_3]));
                    case "SetBrightness":
                        if (parts.length != Const.ID_4) {
                            return "Invalid command";
                        }
                        return setBrightness(parts[1], Integer.parseInt(parts[2]), parts[Const.ID_3]);
                    case "SetColor":
                        if (parts.length != Const.ID_4) {
                            return "Invalid command";
                        }
                        return setColor(parts[1], Integer.parseInt(parts[2]), parts[Const.ID_3]);
                    case "SetAngle":
                        if (parts.length != Const.ID_4) {
                            return "Invalid command";
                        }
                        return setAngle(parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[Const.ID_3]));
                    case "StartRecording":
                        if (parts.length != Const.ID_3) {
                            return "Invalid command";
                        }
                        return startRecording(parts[1], Integer.parseInt(parts[2]));
                    case "StopRecording":
                        if (parts.length != Const.ID_3) {
                            return "Invalid command";
                        }
                        return stopRecording(parts[1], Integer.parseInt(parts[2]));
                    default:
                        return "Invalid command";
                }
            } catch (Exception e) {
                return "Invalid command";
            }
        }

        /**
         * Finds a smart device by its name and ID.
         *
         * @param deviceName the name of the device
         * @param deviceId the ID of the device
         * @return the smart device if found, otherwise null
         */
        private SmartDevice findDevice(String deviceName, int deviceId) {
            return devices.stream()
                    .filter(d -> d.getDeviceName().equals(deviceName) && d.getDeviceId() == deviceId)
                    .findFirst()
                    .orElse(null);
        }

        /**
         * Displays the status of all smart devices.
         *
         * @return a string representing the status of all devices
         */
        private String displayAllStatus() {
            StringBuilder status = new StringBuilder();
            for (SmartDevice device : devices) {
                status.append(device.displayStatus()).append("\n");
            }
            return status.toString().trim();
        }
        /**
         * Turns on a smart device.
         *
         * @param deviceName the name of the device
         * @param deviceId the ID of the device
         * @return a message indicating the result of the operation
         */
        private String turnOn(String deviceName, int deviceId) {
            SmartDevice device = findDevice(deviceName, deviceId);
            if (device == null) {
                return "The smart device was not found";
            }
            if (device.isOn()) {
                return deviceName + " " + deviceId + " is already on";
            }
            device.turnOn();
            return deviceName + " " + deviceId + " is on";
        }
        /**
         * Turns off a smart device.
         *
         * @param deviceName the name of the device
         * @param deviceId the ID of the device
         * @return a message indicating the result of the operation
         */
        private String turnOff(String deviceName, int deviceId) {
            SmartDevice device = findDevice(deviceName, deviceId);
            if (device == null) {
                return "The smart device was not found";
            }
            if (!device.isOn()) {
                return deviceName + " " + deviceId + " is already off";
            }
            device.turnOff();
            return deviceName + " " + deviceId + " is off";
        }
        /**
         * Starts charging a smart device.
         *
         * @param deviceName the name of the device
         * @param deviceId the ID of the device
         * @return a message indicating the result of the operation
         */
        private String startCharging(String deviceName, int deviceId) {
            SmartDevice device = findDevice(deviceName, deviceId);
            if (device == null) {
                return "The smart device was not found";
            }
            if (!(device instanceof Chargeable)) {
                return deviceName + " " + deviceId + " is not chargeable";
            }
            Chargeable chargeable = (Chargeable) device;
            if (chargeable.isCharging()) {
                return deviceName + " " + deviceId + " is already charging";
            }
            chargeable.startCharging();
            return deviceName + " " + deviceId + " is charging";
        }
        /**
         * Stops charging a smart device.
         *
         * @param deviceName the name of the device
         * @param deviceId the ID of the device
         * @return a message indicating the result of the operation
         */
        private String stopCharging(String deviceName, int deviceId) {
            SmartDevice device = findDevice(deviceName, deviceId);
            if (device == null) {
                return "The smart device was not found";
            }
            if (!(device instanceof Chargeable)) {
                return deviceName + " " + deviceId + " is not chargeable";
            }
            Chargeable chargeable = (Chargeable) device;
            if (!chargeable.isCharging()) {
                return deviceName + " " + deviceId + " is not charging";
            }
            chargeable.stopCharging();
            return deviceName + " " + deviceId + " stopped charging";
        }
        /**
         * Sets the temperature of a heater.
         *
         * @param deviceName the name of the device
         * @param deviceId the ID of the device
         * @param temperature the temperature to set
         * @return a message indicating the result of the operation
         */
        private String setTemperature(String deviceName, int deviceId, int temperature) {
            SmartDevice device = findDevice(deviceName, deviceId);
            if (device == null) {
                return "The smart device was not found";
            }
            if (!device.isOn()) {
                return "You can't change the status of the " + deviceName + " " + deviceId + " while it is off";
            }
            if (!(device instanceof Heater)) {
                return deviceName + " " + deviceId + " is not a heater";
            }
            Heater heater = (Heater) device;
            if (!heater.setTemperature(temperature)) {
                return deviceName + " " + deviceId + " temperature should be in the range [15, 30]";
            }
            return deviceName + " " + deviceId + " temperature is set to " + temperature;
        }
        /**
         * Sets the brightness level of a light.
         *
         * @param deviceName the name of the device
         * @param deviceId the ID of the device
         * @param brightness the brightness level to set
         * @return a message indicating the result of the operation
         */
        private String setBrightness(String deviceName, int deviceId, String brightness) {
            SmartDevice device = findDevice(deviceName, deviceId);
            if (device == null) {
                return "The smart device was not found";
            }
            if (!device.isOn()) {
                return "You can't change the status of the " + deviceName + " " + deviceId + " while it is off";
            }
            if (!(device instanceof Light)) {
                return deviceName + " " + deviceId + " is not a light";
            }
            Light light = (Light) device;
            BrightnessLevel level;
            try {
                level = BrightnessLevel.valueOf(brightness.toUpperCase());
            } catch (IllegalArgumentException e) {
                return "The brightness can only be one of \"LOW\", \"MEDIUM\", or \"HIGH\"";
            }
            light.setBrightnessLevel(level);
            return deviceName + " " + deviceId + " brightness level is set to " + brightness;
        }
        /**
         * Sets the color of a light.
         *
         * @param deviceName the name of the device
         * @param deviceId the ID of the device
         * @param color the color to set
         * @return a message indicating the result of the operation
         */
        private String setColor(String deviceName, int deviceId, String color) {
            SmartDevice device = findDevice(deviceName, deviceId);
            if (device == null) {
                return "The smart device was not found";
            }
            if (!device.isOn()) {
                return "You can't change the status of the " + deviceName + " " + deviceId + " while it is off";
            }
            if (!(device instanceof Light)) {
                return deviceName + " " + deviceId + " is not a light";
            }

            Light light = (Light) device;
            LightColor lightColor;
            try {
                lightColor = LightColor.valueOf(color.toUpperCase());
            } catch (IllegalArgumentException e) {
                return "The light color can only be \"YELLOW\" or \"WHITE\"";
            }
            light.setLightColor(lightColor);
            return deviceName + " " + deviceId + " color is set to " + color;
        }
        /**
         * Sets the angle of a camera.
         *
         * @param deviceName the name of the device
         * @param deviceId the ID of the device
         * @param angle the angle to set
         * @return a message indicating the result of the operation
         */
        private String setAngle(String deviceName, int deviceId, int angle) {
            SmartDevice device = findDevice(deviceName, deviceId);
            if (device == null) {
                return "The smart device was not found";
            }
            if (!device.isOn()) {
                return "You can't change the status of the " + deviceName + " " + deviceId + " while it is off";
            }
            if (!(device instanceof Camera)) {
                return deviceName + " " + deviceId + " is not a camera";
            }
            Camera camera = (Camera) device;
            if (!camera.setCameraAngle(angle)) {
                return deviceName + " " + deviceId + " angle should be in the range [-60, 60]";
            }
            return deviceName + " " + deviceId + " angle is set to " + angle;
        }
        /**
         * Starts recording on a camera.
         *
         * @param deviceName the name of the device
         * @param deviceId the ID of the device
         * @return a message indicating the result of the operation
         */
        private String startRecording(String deviceName, int deviceId) {
            SmartDevice device = findDevice(deviceName, deviceId);
            if (device == null) {
                return "The smart device was not found";
            }
            if (!device.isOn()) {
                return "You can't change the status of the " + deviceName + " " + deviceId + " while it is off";
            }
            if (!(device instanceof Camera)) {
                return deviceName + " " + deviceId + " is not a camera";
            }
            Camera camera = (Camera) device;
            if (camera.isRecording()) {
                return deviceName + " " + deviceId + " is already recording";
            }
            camera.startRecording();
            return deviceName + " " + deviceId + " started recording";
        }
        /**
         * Stops recording on a camera.
         *
         * @param deviceName the name of the device
         * @param deviceId the ID of the device
         * @return a message indicating the result of the operation
         */
        private String stopRecording(String deviceName, int deviceId) {
            SmartDevice device = findDevice(deviceName, deviceId);
            if (device == null) {
                return "The smart device was not found";
            }
            if (!device.isOn()) {
                return "You can't change the status of the " + deviceName + " " + deviceId + " while it is off";
            }
            if (!(device instanceof Camera)) {
                return deviceName + " " + deviceId + " is not a camera";
            }
            Camera camera = (Camera) device;
            if (!camera.isRecording()) {
                return deviceName + " " + deviceId + " is not recording";
            }
            camera.stopRecording();
            return deviceName + " " + deviceId + " stopped recording";
        }
    }
}
