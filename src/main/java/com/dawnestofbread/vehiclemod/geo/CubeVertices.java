package com.dawnestofbread.vehiclemod.geo;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public record CubeVertices(Vertex bottomLeftBack, Vertex bottomRightBack, Vertex topLeftBack,
                           Vertex topRightBack,
                           Vertex topLeftFront, Vertex topRightFront, Vertex bottomLeftFront,
                           Vertex bottomRightFront) {
    public CubeVertices(Vec3 origin, Vec3 vertexSize, double inflation) {
        this(
                new Vertex(origin.x - inflation, origin.y - inflation, origin.z - inflation),
                new Vertex(origin.x - inflation, origin.y - inflation, origin.z + vertexSize.z + inflation),
                new Vertex(origin.x - inflation, origin.y + vertexSize.y + inflation, origin.z - inflation),
                new Vertex(origin.x - inflation, origin.y + vertexSize.y + inflation, origin.z + vertexSize.z + inflation),
                new Vertex(origin.x + vertexSize.x + inflation, origin.y + vertexSize.y + inflation, origin.z - inflation),
                new Vertex(origin.x + vertexSize.x + inflation, origin.y + vertexSize.y + inflation, origin.z + vertexSize.z + inflation),
                new Vertex(origin.x + vertexSize.x + inflation, origin.y - inflation, origin.z - inflation),
                new Vertex(origin.x + vertexSize.x + inflation, origin.y - inflation, origin.z + vertexSize.z + inflation));
    }

    public Vertex[] quadWest() {
        return new Vertex[]{this.topRightBack, this.topLeftBack, this.bottomLeftBack, this.bottomRightBack};
    }

    public Vertex[] quadEast() {
        return new Vertex[]{this.topLeftFront, this.topRightFront, this.bottomRightFront, this.bottomLeftFront};
    }

    public Vertex[] quadNorth() {
        return new Vertex[]{this.topLeftBack, this.topLeftFront, this.bottomLeftFront, this.bottomLeftBack};
    }

    public Vertex[] quadSouth() {
        return new Vertex[]{this.topRightFront, this.topRightBack, this.bottomRightBack, this.bottomRightFront};
    }

    public Vertex[] quadUp() {
        return new Vertex[]{this.topRightBack, this.topRightFront, this.topLeftFront, this.topLeftBack};
    }

    public Vertex[] quadDown() {
        return new Vertex[]{this.bottomLeftBack, this.bottomLeftFront, this.bottomRightFront, this.bottomRightBack};
    }

    public Vertex[] verticesForQuad(Direction direction, boolean boxUv, boolean mirror) {
        return switch (direction) {
            case WEST -> mirror ? quadEast() : quadWest();
            case EAST -> mirror ? quadWest() : quadEast();
            case NORTH -> quadNorth();
            case SOUTH -> quadSouth();
            case UP -> mirror && !boxUv ? quadDown() : quadUp();
            case DOWN -> mirror && !boxUv ? quadUp() : quadDown();
        };
    }
}
