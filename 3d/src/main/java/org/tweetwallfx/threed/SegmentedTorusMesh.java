/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2019 TweetWallFX
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.tweetwallfx.threed;

import java.util.Arrays;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.DepthTest;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

/**
 * TweetWallFX - Devoxx 2014 {@literal @}johanvos {@literal @}SvenNB
 * {@literal @}SeanMiPhillips {@literal @}jdub1581 {@literal @}JPeredaDnr
 *
 * SegmentedTorusMesh is based in TorusMesh from F(x)yz, but allows cutting the
 * torus in two directions, in order to have a banner parallel to an uncut
 * torus. Based on a regular 2D TriangleMesh, mapped to a 3D mesh with the torus
 * parametric equations Crop allows cutting/cropping the 2D mesh on the borders
 * If crop ==0 then a regular torus is formed (thought with slight differences
 * from TorusMesh)
 */
public class SegmentedTorusMesh extends MeshView {

    private static final int DEFAULT_DIVISIONS = 64;
    private static final int DEFAULT_T_DIVISIONS = 64;
    private static final int DEFAULT_CROP = 0;
    private static final double DEFAULT_RADIUS = 12.5D;
    private static final double DEFAULT_T_RADIUS = 5.0D;
    private static final double DEFAULT_START_ANGLE = 0.0D;
    private static final double DEFAULT_X_OFFSET = 0.0D;
    private static final double DEFAULT_Y_OFFSET = 0.0D;
    private static final double DEFAULT_Z_OFFSET = 1.0D;

    public SegmentedTorusMesh() {
        this(DEFAULT_DIVISIONS, DEFAULT_T_DIVISIONS, DEFAULT_CROP, DEFAULT_RADIUS, DEFAULT_T_RADIUS);
    }

    public SegmentedTorusMesh(double radius, double tRadius) {
        this(DEFAULT_DIVISIONS, DEFAULT_T_DIVISIONS, DEFAULT_CROP, radius, tRadius);
    }

    public SegmentedTorusMesh(int rDivs, int tDivs, int crop, double radius, double tRadius) {
        setRadiusDivisions(rDivs);
        setTubeDivisions(tDivs);
        setTorusCrop(crop);
        setRadius(radius);
        setTubeRadius(tRadius);

        setDepthTest(DepthTest.ENABLE);
        updateMesh();
    }

    private void updateMesh() {
        setMesh(createTorus(
                getRadiusDivisions(),
                getTubeDivisions(),
                getTorusCrop(),
                (float) getRadius(),
                (float) getTubeRadius(),
                (float) getTubeStartAngleOffset(),
                (float) getxOffset(),
                (float) getyOffset(),
                (float) getzOffset()));
    }

    private final IntegerProperty radiusDivisions = new SimpleIntegerProperty(DEFAULT_DIVISIONS) {

        @Override
        protected void invalidated() {
            setMesh(createTorus(
                    getRadiusDivisions(),
                    getTubeDivisions(),
                    getTorusCrop(),
                    (float) getRadius(),
                    (float) getTubeRadius(),
                    (float) getTubeStartAngleOffset(),
                    (float) getxOffset(),
                    (float) getyOffset(),
                    (float) getzOffset()));
        }
    };

    public final int getRadiusDivisions() {
        return radiusDivisions.get();
    }

    public final void setRadiusDivisions(int value) {
        radiusDivisions.set(value);
    }

    public IntegerProperty radiusDivisionsProperty() {
        return radiusDivisions;
    }

    private final IntegerProperty tubeDivisions = new SimpleIntegerProperty(DEFAULT_T_DIVISIONS) {

        @Override
        protected void invalidated() {
            updateMesh();
        }
    };

    public final int getTubeDivisions() {
        return tubeDivisions.get();
    }

    public final void setTubeDivisions(int value) {
        tubeDivisions.set(value);
    }

    public IntegerProperty tubeDivisionsProperty() {
        return tubeDivisions;
    }

    private final IntegerProperty torusCrop = new SimpleIntegerProperty(DEFAULT_CROP) {

        @Override
        protected void invalidated() {
            updateMesh();
        }
    };

    public final int getTorusCrop() {
        return torusCrop.get();
    }

    public final void setTorusCrop(int value) {
        torusCrop.set(value);
    }

    public IntegerProperty torusCropProperty() {
        return torusCrop;
    }

    private final DoubleProperty radius = new SimpleDoubleProperty(DEFAULT_RADIUS) {

        @Override
        protected void invalidated() {
            updateMesh();
        }
    };

    public final double getRadius() {
        return radius.get();
    }

    public final void setRadius(double value) {
        radius.set(value);
    }

    public DoubleProperty radiusProperty() {
        return radius;
    }

    private final DoubleProperty tubeRadius = new SimpleDoubleProperty(DEFAULT_T_RADIUS) {

        @Override
        protected void invalidated() {
            updateMesh();
        }
    };

    public final double getTubeRadius() {
        return tubeRadius.get();
    }

    public final void setTubeRadius(double value) {
        tubeRadius.set(value);
    }

    public DoubleProperty tubeRadiusProperty() {
        return tubeRadius;
    }

    private final DoubleProperty tubeStartAngleOffset = new SimpleDoubleProperty(DEFAULT_START_ANGLE) {

        @Override
        protected void invalidated() {
            updateMesh();
        }
    };

    public final double getTubeStartAngleOffset() {
        return tubeStartAngleOffset.get();
    }

    public void setTubeStartAngleOffset(double value) {
        tubeStartAngleOffset.set(value);
    }

    public DoubleProperty tubeStartAngleOffsetProperty() {
        return tubeStartAngleOffset;
    }
    private final DoubleProperty xOffset = new SimpleDoubleProperty(DEFAULT_X_OFFSET) {

        @Override
        protected void invalidated() {
            updateMesh();
        }
    };

    public final double getxOffset() {
        return xOffset.get();
    }

    public void setxOffset(double value) {
        xOffset.set(value);
    }

    public DoubleProperty xOffsetProperty() {
        return xOffset;
    }
    private final DoubleProperty yOffset = new SimpleDoubleProperty(DEFAULT_Y_OFFSET) {

        @Override
        protected void invalidated() {
            updateMesh();
        }
    };

    public final double getyOffset() {
        return yOffset.get();
    }

    public void setyOffset(double value) {
        yOffset.set(value);
    }

    public DoubleProperty yOffsetProperty() {
        return yOffset;
    }
    private final DoubleProperty zOffset = new SimpleDoubleProperty(DEFAULT_Z_OFFSET) {

        @Override
        protected void invalidated() {
            updateMesh();
        }
    };

    public final double getzOffset() {
        return zOffset.get();
    }

    public void setzOffset(double value) {
        zOffset.set(value);
    }

    public DoubleProperty zOffsetProperty() {
        return zOffset;
    }

    private TriangleMesh createTorus(int subDivX, int subDivY, int crop, float radius,
            float tRadius, float tubeStartAngle, float xOffset, float yOffset, float zOffset) {

        final int pointSize = 3;
        final int texCoordSize = 2;
        final int faceSize = 6;
        int numDivX = subDivX + 1 - 2 * crop;
        int numDivY = subDivY + 1 - 2 * crop;
        int numVerts = numDivY * numDivX;
        float points[] = new float[numVerts * pointSize];
        float texCoords[] = new float[numVerts * texCoordSize];
        int faceCount = (numDivX - 1) * (numDivY - 1) * 2;
        int faces[] = new int[faceCount * faceSize];

        // Create points and texCoords
        for (int y = crop; y <= subDivY - crop; y++) {
            float dy = (float) y / subDivY;
            if (crop > 0 || (crop == 0 && y < subDivY)) {
                for (int x = crop; x <= subDivX - crop; x++) {
                    float dx = (float) x / subDivX;
                    if (crop > 0 || (crop == 0 && x < subDivX)) {
                        int index = (y - crop) * numDivX * pointSize + ((x - crop) * pointSize);
                        points[index] = (float) ((radius + tRadius * Math.cos((-1d + 2d * dy) * Math.PI)) * (Math.cos((-1d + 2d * dx) * Math.PI) + xOffset));
                        points[index + 2] = (float) ((radius + tRadius * Math.cos((-1d + 2d * dy) * Math.PI)) * (Math.sin((-1d + 2d * dx) * Math.PI) + yOffset));
                        points[index + 1] = (float) (1.5 * tRadius * Math.sin((-1d + 2d * dy) * Math.PI) * zOffset);
                        index = (y - crop) * numDivX * texCoordSize + ((x - crop) * texCoordSize);
                        texCoords[index] = ((float) (x - crop)) / (subDivX - 2f * crop);
                        texCoords[index + 1] = ((float) (y - crop)) / (subDivY - 2f * crop);
                    }
                }
            }
        }
        // Create faces
        for (int y = crop; y < subDivY - crop; y++) {
            for (int x = crop; x < subDivX - crop; x++) {
                int p00 = (y - crop) * numDivX + (x - crop);
                int p01 = p00 + 1;
                if (crop == 0 && x == subDivX - 1) {
                    p01 -= subDivX;
                }
                int p10 = p00 + numDivX;
                if (crop == 0 && y == subDivY - 1) {
                    p10 -= subDivY * numDivX;
                }
                int p11 = p10 + 1;
                if (crop == 0 && x == subDivX - 1) {
                    p11 -= subDivX;
                }

                int tc00 = (y - crop) * numDivX + (x - crop);
                int tc01 = tc00 + 1;
                if (crop == 0 && x == subDivX - 1) {
                    tc01 -= subDivX;
                }
                int tc10 = tc00 + numDivX;
                if (crop == 0 && y == subDivY - 1) {
                    tc10 -= subDivY * subDivX;
                }
                int tc11 = tc10 + 1;
                if (crop == 0 && x == subDivX - 1) {
                    tc11 -= subDivX;
                }
                int index = ((y - crop) * (numDivX - 1) * faceSize + ((x - crop) * faceSize)) * 2;
                faces[index + 0] = p00;
                faces[index + 1] = tc00;
                faces[index + 2] = p10;
                faces[index + 3] = tc10;
                faces[index + 4] = p11;
                faces[index + 5] = tc11;

                index += faceSize;
                faces[index + 0] = p11;
                faces[index + 1] = tc11;
                faces[index + 2] = p01;
                faces[index + 3] = tc01;
                faces[index + 4] = p00;
                faces[index + 5] = tc00;
            }
        }
        int[] faceSmoothingGroups = new int[faceCount];
        Arrays.fill(faceSmoothingGroups, 1);

        TriangleMesh triangleMesh = new TriangleMesh();
        triangleMesh.getPoints().addAll(points);
        triangleMesh.getTexCoords().addAll(texCoords);
        triangleMesh.getFaces().addAll(faces);
        triangleMesh.getFaceSmoothingGroups().addAll(faceSmoothingGroups);
        return triangleMesh;
    }
}
