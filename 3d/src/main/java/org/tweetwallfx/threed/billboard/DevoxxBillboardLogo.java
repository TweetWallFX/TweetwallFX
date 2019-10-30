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
package org.tweetwallfx.threed.billboard;

import java.util.Random;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Camera;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import org.fxyz.extras.BillboardBehavior;

/**
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class DevoxxBillboardLogo extends MeshView implements BillboardBehavior<DevoxxBillboardLogo> {

    private final PhongMaterial mat = new PhongMaterial();
    private final Image image = new Image(getClass().getResourceAsStream("devoxx.png"));
    private final FXTimer updateThread = new FXTimer();
    private Camera otherNode;

    public DevoxxBillboardLogo(double width, double height) {
        this();
    }

    public DevoxxBillboardLogo() {
        this.mat.setDiffuseMap(image);

        this.mesh = createMesh();
        this.setMesh(mesh);
        this.setMaterial(mat);
        this.setCullFace(CullFace.NONE);
        this.updateThread.setRunnable(() -> {
            if (isAnimated() && updateThread.getCurrentTime() >= (updateThread.getUpdateDelay())) {
                updateMesh();
                updateThread.setUpdateDelay((updateThread.getCurrentTime() + 80000000));
            }
        });

        updateThread.start();

        this.sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
                if (newValue != null) {
                    otherNode = getScene().getCamera();
                    startBillboardBehavior();
                    sceneProperty().removeListener(this);
                }
            }
        });
    }

    public final void setImage(Image value) {
        mat.setDiffuseMap(value);
    }

    public final Image getImage() {
        return mat.getDiffuseMap();
    }
    //==========================================================================
    private final BooleanProperty animated = new SimpleBooleanProperty(true);

    public final boolean isAnimated() {
        return animated.get();
    }

    public void setAnimated(boolean value) {
        animated.set(value);
    }

    public BooleanProperty animatedProperty() {
        return animated;
    }
    //==========================================================================
    private final FloatProperty prefWidth = new SimpleFloatProperty(1920) {

        @Override
        protected void invalidated() {
            updateProperties();
            needsUpdate = true;
        }
    };

    public float getPrefWidth() {
        return prefWidth.get();
    }

    public void setPrefWidth(float value) {
        prefWidth.set(value);
    }

    public FloatProperty prefWidthProperty() {
        return prefWidth;
    }
    //==========================================================================
    private final FloatProperty prefHeight = new SimpleFloatProperty(1080) {

        @Override
        protected void invalidated() {
            updateProperties();
            needsUpdate = true;
        }
    };

    public float getPrefHeight() {
        return prefHeight.get();
    }

    public void setPrefHeight(float value) {
        prefHeight.set(value);
    }

    public FloatProperty prefHeightProperty() {
        return prefHeight;
    }

    /*==========================================================================
     *          Create Mesh
     *///=======================================================================
    private static final float defaultAmplitude = 75.0f;
    private static final float defaultFrequency = 20.0f;
    private static final float defaultPeriod = 60.5f;
    private static final float defaultWaveLength = 80.25f;

    private int divX = 64;
    private int divY = 64;
    private int subX, subY;

    float[][][] pointCache;

    private float holdPos;

    private TriangleMesh mesh;

    private boolean needsUpdate;

    private Random r = new Random();

    //==========================================================================
    private TriangleMesh createMesh() {

        final TriangleMesh m = newEmptyMesh();
        final float minX = -getPrefWidth() / 2f;
        final float maxX = getPrefWidth() / 2f;
        final float minY = -getPrefHeight() / 2f;
        final float maxY = getPrefHeight() / 2f;

        subX = (int) getPrefWidth() / divX;
        subY = (int) getPrefHeight() / divY;
        if (pointCache == null) {
            pointCache = new float[subX + 2][subY + 2][3];
        }
        final int pointSize = m.getPointElementSize();
        final int texCoordSize = m.getTexCoordElementSize();
        final int faceSize = m.getFaceElementSize();

        int numDivX = subX + 1;
        int numVerts = (subY + 1) * numDivX;

        float points[] = new float[numVerts * pointSize];
        float texCoords[] = new float[numVerts * texCoordSize];
        int faceCount = subX * subY * 2;
        int faces[] = new int[faceCount * faceSize];

        /*======================================================================
         if animated and pointCache needs update
         */
        if (isAnimated() && needsUpdate) {
            for (int y = 0; y <= subY; y++) {

                float currY = (float) y / subY;
                double fy = (1 - currY) * minY + currY * maxY;

                for (int x = 0; x <= subX; x++) {

                    float currX = (float) x / subX;
                    double fx = (1 - currX) * minX + currX * maxX;
                    int index = y * numDivX * pointSize + (x * pointSize);
                    // Apply The Wave To Our Mesh

                    if (r.nextBoolean()) {
                        pointCache[x][y][0] = (float) (fx);
                        pointCache[x][y][1] = (float) (fy);
                        pointCache[x][y][2] = (float) (getAmplitude() * (Math.sin(((waveNumber.get()) * x) - (getFrequency() * currX))) + getAmplitude() * r.nextFloat());
                    } else {
                        pointCache[x][y][0] = (float) (fx);
                        pointCache[x][y][1] = (float) (fy);
                        pointCache[x][y][2] = (float) (getAmplitude() * (Math.sin(((waveNumber.get()) * x) - (getFrequency() * currX))));
                    }

                    points[index + 0] = pointCache[x][y][0];
                    points[index + 1] = pointCache[x][y][1];
                    points[index + 2] = pointCache[x][y][2];

                    index = y * numDivX * texCoordSize + (x * texCoordSize);
                    texCoords[index] = currX;
                    texCoords[index + 1] = currY;
                }
            }
            needsUpdate = false;
        } /*======================================================================
         step values if animated
         */ else if (isAnimated() && !needsUpdate) {
            // update pointPos and texCoords
            int index = 0;
            int texIndex;
            float currX = 0;
            for (int y = 0; y <= subY; y++) {
                float currY = (float) y / subY;
                // Store Current X Value, Left Side Of Wave
                holdPos = pointCache[0][y][2];

                for (int x = 0; x <= subX; x++) {
                    currX = (float) x / subX;
                    index = y * (subX + 1) * 3 + (x * 3);
                    // Current Wave Value Equals Value To The Right
                    pointCache[x][y][2] = pointCache[x + 1][y][2];

                    //setPoints from cache
                    points[index + 0] = pointCache[x][y][0];
                    points[index + 1] = pointCache[x][y][1];
                    points[index + 2] = pointCache[x][y][2];

                    texIndex = y * numDivX * texCoordSize + (x * texCoordSize);
                    texCoords[texIndex] = currX;
                    texCoords[texIndex + 1] = currY;
                }
                // Last Value Becomes The Far Left Stored Value
                pointCache[subX][y][2] = holdPos;
            }
        } /*======================================================================
         else create default points
         */ else {
            // Create pointPos and texCoords
            for (int y = 0; y <= subY; y++) {

                float currY = (float) y / subY;
                double fy = (1 - currY) * minY + currY * maxY;

                for (int x = 0; x <= subX; x++) {

                    float currX = (float) x / subX;
                    double fx = (1 - currX) * minX + currX * maxX;
                    int index = y * numDivX * pointSize + (x * pointSize);

                    // Apply The Wave To Our Mesh on XZ plane
                    pointCache[x][y][0] = (float) (fx);
                    pointCache[x][y][1] = (float) (fy);
                    pointCache[x][y][2] = (float) (getAmplitude() * (Math.sin(((waveNumber.get()) * x) - (getFrequency() * currX))));

                    points[index + 0] = pointCache[x][y][0];
                    points[index + 1] = pointCache[x][y][1];
                    points[index + 2] = pointCache[x][y][2];

                    index = y * numDivX * texCoordSize + (x * texCoordSize);
                    texCoords[index] = currX;
                    texCoords[index + 1] = currY;
                }
            }
        }
        // Create faces
        for (int y = 0; y < subY; y++) {
            for (int x = 0; x < subX; x++) {
                int p00 = y * numDivX + x;
                int p01 = p00 + 1;
                int p10 = p00 + numDivX;
                int p11 = p10 + 1;
                int tc00 = y * numDivX + x;
                int tc01 = tc00 + 1;
                int tc10 = tc00 + numDivX;
                int tc11 = tc10 + 1;

                int index = (y * subX * faceSize + (x * faceSize)) * 2;
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
        //updatePoints();
        m.getPoints().addAll(points);
        m.getTexCoords().addAll(texCoords);
        m.getFaces().addAll(faces);
        return m;
    }

    //==========================================================================
    private TriangleMesh newEmptyMesh() {
        return new TriangleMesh();
    }

    private void updateMesh() {
        mesh = createMesh();
        setMesh(mesh);
    }
    //==========================================================================
    private final FloatProperty pi2 = new SimpleFloatProperty((float) Math.PI * 2);
    //==========================================================================
    private final FloatProperty frequency = new SimpleFloatProperty(defaultFrequency) {

        @Override
        protected void invalidated() {
            updateProperties();
            needsUpdate = true;
        }
    };

    public float getFrequency() {
        return frequency.get();
    }

    public void setFrequency(float value) {
        frequency.set(value);
    }

    public FloatProperty frequencyProperty() {
        return frequency;
    }
    //==========================================================================
    private final FloatProperty period = new SimpleFloatProperty(defaultPeriod) {
        @Override
        protected void invalidated() {
            updateProperties();
            needsUpdate = true;
        }
    };

    public float getPeriod() {
        return period.get();
    }

    public void setPeriod(float value) {
        period.set(value);
    }

    public FloatProperty periodProperty() {
        return period;
    }
    //==========================================================================
    private final FloatProperty waveLength = new SimpleFloatProperty(defaultWaveLength) {
        @Override
        protected void invalidated() {
            updateProperties();
            needsUpdate = true;
        }
    };

    public float getWaveLength() {
        return waveLength.get();
    }

    public void setWaveLength(float value) {
        waveLength.set(value);
    }

    public FloatProperty waveLengthProperty() {
        return waveLength;
    }

    //==========================================================================
    private final FloatProperty amplitude = new SimpleFloatProperty(defaultAmplitude) {
        @Override
        protected void invalidated() {
            updateProperties();
            needsUpdate = true;
        }
    };

    public float getAmplitude() {
        return amplitude.get();
    }

    public void setAmplitude(float value) {
        amplitude.set(value);
    }

    public FloatProperty amplitudeProperty() {
        return amplitude;
    }

    //==========================================================================
    private final FloatProperty waveSpeed = new SimpleFloatProperty();
    //==========================================================================
    private final FloatProperty waveNumber = new SimpleFloatProperty();

    //==========================================================================
    private void updateProperties() {
        waveSpeed.set(waveLength.divide(period).floatValue());
        waveNumber.set((pi2.divide(period)).floatValue());
    }

    public final void start() {
        updateThread.start();
    }

    public void stop() {
        updateThread.stop();
    }
    //**************************************************************************

    @Override
    public DevoxxBillboardLogo getBillboardNode() {
        return this;
    }

    @Override
    public Node getOther() {
        return otherNode;
    }
}
