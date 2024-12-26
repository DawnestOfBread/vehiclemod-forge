package com.dawnestofbread.vehiclemod.animation;

import com.dawnestofbread.vehiclemod.geo.Transform;

import static com.dawnestofbread.vehiclemod.utils.MathUtils.vecEaseOutExpo;

public record PlayerPose(Transform head, Transform body, Transform leftArm, Transform rightArm, Transform leftLeg, Transform rightLeg) {
    public PlayerPose() {
        this(new Transform(), new Transform(), new Transform(), new Transform(), new Transform(), new Transform());
    }

    public PlayerPose withHead(Transform t) {
        return new PlayerPose(t, body, leftArm, rightArm, leftLeg, rightLeg);
    }
    public PlayerPose withBody(Transform t) {
        return new PlayerPose(head, t, leftArm, rightArm, leftLeg, rightLeg);
    }
    public PlayerPose withLeftArm(Transform t) {
        return new PlayerPose(head, body, t, rightArm, leftLeg, rightLeg);
    }
    public PlayerPose withRightArm(Transform t) {
        return new PlayerPose(head, body, leftArm, t, leftLeg, rightLeg);
    }
    public PlayerPose withLeftLeg(Transform t) {
        return new PlayerPose(head, body, leftArm, rightArm, t, rightLeg);
    }
    public PlayerPose withRightLeg(Transform t) {
        return new PlayerPose(head, body, leftArm, rightArm, leftLeg, t);
    }

    /**
     * @param p1 target pose
     * @param alpha alpha
     * @return blended pose (0 - start, 1 - target)
     */
    public PlayerPose blend(PlayerPose p1, double alpha) {
        return new PlayerPose()
                .withHead(blendParts(this.head(), p1.head(), alpha))
                .withBody(blendParts(this.body(), p1.body(), alpha))
                .withLeftArm(blendParts(this.leftArm(), p1.leftArm(), alpha))
                .withRightArm(blendParts(this.rightArm(), p1.rightArm(), alpha))
                .withLeftLeg(blendParts(this.leftLeg(), p1.leftLeg(), alpha))
                .withRightLeg(blendParts(this.rightLeg(), p1.rightLeg(), alpha));
    }
    /**
     * @param pn1 target pose for alpha = -1
     * @param p1 target pose for alpha = 1
     * @param alpha alpha
     * @return blended pose (0 - start, +1/-1 - target)
     */
    public PlayerPose blendTwo(PlayerPose pn1, PlayerPose p1, double alpha) {
        return alpha > 0 ? blend(p1, Math.abs(alpha)) : alpha < 0 ? blend(pn1, Math.abs(alpha)) : this;
    }
    public PlayerPose blendClamped(PlayerPose p1, double alpha) {
        return blendIf(p1, alpha, alpha >= 0 && alpha <= 1);
    }
    public PlayerPose blendIf(PlayerPose p1, double alpha, boolean b0) {
        return b0 ? blend(p1,alpha) : this;
    }
    private Transform blendParts(Transform t0, Transform t1, double alpha) {
        return new Transform().setPosition(vecEaseOutExpo(Math.abs(alpha), t0.getPosition(), t1.getPosition().scale(Math.signum(alpha)))).setRotation(vecEaseOutExpo(Math.abs(alpha), t0.getRotation(), t1.getRotation().scale(Math.signum(alpha))));
    }
}
