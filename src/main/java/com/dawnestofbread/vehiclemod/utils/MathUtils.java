package com.dawnestofbread.vehiclemod.utils;

import com.dawnestofbread.vehiclemod.geo.Transform;
import net.minecraft.world.phys.Vec3;

public class MathUtils {
    public static float fInterpTo(float currentValue, float targetValue, float interpSpeed, float deltaTime) {
        // Calculate the difference between the current and target values
        float difference = targetValue - currentValue;

        // Calculate the maximum step allowed for this frame
        float maxStep = interpSpeed * deltaTime;

        // Move towards the target value by the step size or the remaining difference
        if (Math.abs(difference) <= maxStep) {
            return targetValue; // We've reached or exceeded the target value
        }

        // Move closer to the target value
        return currentValue + Math.signum(difference) * maxStep;
    }
    public static double dInterpTo(double currentValue, double targetValue, double interpSpeed, double deltaTime) {
        // Calculate the difference between the current and target values
        double difference = targetValue - currentValue;

        // Calculate the maximum step allowed for this frame
        double maxStep = interpSpeed * deltaTime;

        // Move towards the target value by the step size or the remaining difference
        if (Math.abs(difference) <= maxStep) {
            return targetValue; // We've reached or exceeded the target value
        }

        // Move closer to the target value
        return currentValue + Math.signum(difference) * maxStep;
    }

    public static float fInterpToExp(float currentValue, float targetValue, float interpSpeed, float deltaTime) {
        // Exponential decay formula
        float t = 1.0f - (float)Math.pow(0.5, interpSpeed * deltaTime);

        // Interpolate towards the target
        return currentValue + (targetValue - currentValue) * t;
    }

    public static double dInterpToExp(double currentValue, double targetValue, double interpSpeed, double deltaTime) {
        // Exponential decay formula
        float t = 1.0f - (float)Math.pow(0.5, interpSpeed * deltaTime);

        // Interpolate towards the target
        return currentValue + (targetValue - currentValue) * t;
    }

    public static double suspensionEasing(double current, double target, double interpSpeed, double deltaTime) {
        double difference = target - current;
        current += easeOutBounce(deltaTime * interpSpeed / 10) * difference;
        return current;
    }
    public static double easeOutBounce(double x) {
        double n1 = 7.5625;
        double d1 = 2.75;

        if (x < 1 / d1) {
            return n1 * x * x;
        } else if (x < 2 / d1) {
            return n1 * (x -= 1.5 / d1) * x + 0.75;
        } else if (x < 2.5 / d1) {
            return n1 * (x -= 2.25 / d1) * x + 0.9375;
        } else {
            return n1 * (x -= 2.625 / d1) * x + 0.984375;
        }
    }

    public static double easeOutExpo(double x) {
        return x == 1 ? 1 : 1 - Math.pow(2, -10 * x);
    }
    public static Vec3 vecEaseOutExpo(double x, Vec3 start, Vec3 end) {
        Vec3 diff = end.subtract(start);
        return start.add(diff.scale(easeOutExpo(x)));
    }

    public static double mapDoubleRangeClamped(double value, double inMin, double inMax, double outMin, double outMax) {
        // Map the value to the target range
        double mappedValue = (value - inMin) / (inMax - inMin) * (outMax - outMin) + outMin;

        // Clamp the value within the output range
        return Math.max(outMin, Math.min(outMax, mappedValue));
    }

     public static float steeringEasing(float current, float target, float deltaTime) {
        // Clamp target angle to valid range
        target = Math.max(-1.0f, Math.min(1.0f, target));

        // Interpolate current angle towards the target angle
        current += (target - current) * 7.5f * deltaTime;

        return current;
    }
}
