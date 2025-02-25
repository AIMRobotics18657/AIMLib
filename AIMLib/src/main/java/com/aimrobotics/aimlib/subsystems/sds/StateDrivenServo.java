package com.aimrobotics.aimlib.subsystems.sds;

import com.aimrobotics.aimlib.gamepad.AIMPad;
import com.aimrobotics.aimlib.util.Mechanism;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class StateDrivenServo extends Mechanism {

    Servo servo;

    String name;

    Servo.Direction direction;

    ServoState activeTargetState;
    ServoState[] states;

    ServoState customState = new ServoState(0);

    double targetPosition;

    private int presetStateChceker = 0;

    public StateDrivenServo(ServoState[] states, ServoState initState, String name) {
        this.states = states;

        activeTargetState = initState;
        targetPosition = activeTargetState.getPosition();

        this.name = name;
        this.direction = Servo.Direction.FORWARD;
    }

    public StateDrivenServo(ServoState[] states, ServoState initState, String name, Servo.Direction direction) {
        this.states = states;

        activeTargetState = initState;
        targetPosition = activeTargetState.getPosition();

        this.name = name;
        this.direction = direction;
    }

    @Override
    public void init(HardwareMap hwMap) {
        servo = hwMap.get(Servo.class, name);
        servo.setDirection(direction);
    }

    @Override
    public void loop(AIMPad aimPad) {
        if (activeTargetState.equals(customState)) {
            targetPosition = activeTargetState.getPosition();
        } else {
            for (ServoState state:
                    states) {
                if (state == activeTargetState) {
                    targetPosition = state.getPosition();
                    break;
                }
            }
        }
        servo.setPosition(targetPosition);
    }

    @Override
    public void telemetry(Telemetry telemetry) {
        telemetry.addData("States:", states);
        telemetry.addData("State: ", activeTargetState);
        telemetry.addData("Target Position", targetPosition);
        telemetry.addData("Active Position", servo.getPosition());
    }

    public void setActiveTargetState(ServoState newActiveState) {
        activeTargetState = newActiveState;
    }

    public ServoState getActiveTargetState() {
        return activeTargetState;
    }

    public void setActiveStateCustom(double position) {
        customState.setPosition(position);
        activeTargetState = customState;
    }

    public void systemsCheck(AIMPad aimpad, Telemetry telemetry) {
        if (aimpad.isAPressed()) {
            presetStateChceker = (presetStateChceker + 1) % states.length;
            activeTargetState = states[(presetStateChceker)];
        } else if (aimpad.isBPressed()) {
            presetStateChceker = (states.length + presetStateChceker - 1) % states.length;
            activeTargetState = states[(presetStateChceker)];
        } else if (Math.abs(aimpad.getLeftStickY()) > 0.05) {
            setActiveStateCustom(aimpad.getLeftStickY());
        }
        loop(aimpad);
        telemetry(telemetry);
    }
}
