package com.dawnestofbread.vehiclemod.collision;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Intersectionf;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static com.dawnestofbread.vehiclemod.utils.VectorUtils.fromVector3f;

public class OBB {
    private final Vector3f halfSize;
    private final Vector3f centre;
    private final Quaternionf orientation;

    public OBB(Vector3f centre, Vector3f halfSize, Quaternionf orientation) {
        this.centre = centre;
        this.halfSize = halfSize;
        this.orientation = orientation;
    }

    public Vector3f getHalfSize() {
        return halfSize;
    }
    public Vector3f getCentre() {
        return centre;
    }
    public Quaternionf getOrientation() {
        return orientation;
    }

    public Vector3f[] getCorners() {
        Vector3f[] corners = new Vector3f[8];

        Vector3f[] relativeCorners = new Vector3f[]{
                new Vector3f(-halfSize.x, -halfSize.y, -halfSize.z),
                new Vector3f(halfSize.x, -halfSize.y, -halfSize.z),
                new Vector3f(halfSize.x, halfSize.y, -halfSize.z),
                new Vector3f(-halfSize.x, halfSize.y, -halfSize.z),
                new Vector3f(-halfSize.x, -halfSize.y, halfSize.z),
                new Vector3f(halfSize.x, -halfSize.y, halfSize.z),
                new Vector3f(halfSize.x, halfSize.y, halfSize.z),
                new Vector3f(-halfSize.x, halfSize.y, halfSize.z)
        };

        for (int i = 0; i < 8; i++) {
            Vector3f corner = relativeCorners[i];
            corner.rotate(orientation);
            corner.add(centre);
            corners[i] = corner;
        }

        return corners;
    }

    public static OBB fromAABB(AABB aabb) {
        Vector3f centre = aabb.getCenter().toVector3f();
        Vector3f halfSize = new Vector3f((float) (aabb.getXsize() / 2.0f), (float) (aabb.getYsize() / 2.0f), (float) (aabb.getZsize() / 2.0f));
        return new OBB(centre, halfSize, new Quaternionf());
    }
    public AABB toAABB() {
        Vec3 halfSize = fromVector3f(this.halfSize);
        Vec3 centre = fromVector3f(this.centre);
        Vec3 start = centre.subtract(halfSize);
        Vec3 end = centre.add(halfSize);
        return new AABB(start, end);
    }
    public boolean intersects(OBB other) {
        return Intersectionf.testObOb(this.getCentre(), this.getAxes()[0],
                this.getAxes()[1], this.getAxes()[2], this.getHalfSize(),
                other.getCentre(), other.getAxes()[0], other.getAxes()[1], other.getAxes()[2],
                other.getHalfSize());
    }

    public Vector3f[] getAxes() {
        Vector3f[] axes = new Vector3f[]{
                new Vector3f(1, 0, 0),
                new Vector3f(0, 1, 0),
                new Vector3f(0, 0, 1)};
        orientation.transform(axes[0]);
        orientation.transform(axes[1]);
        orientation.transform(axes[2]);
        return axes;
    }

    @Override
    public String toString() {
        return "OBB{" +
                "halfSize=" + halfSize +
                ", centre=" + centre +
                ", orientation=" + orientation +
                '}';
    }
}
