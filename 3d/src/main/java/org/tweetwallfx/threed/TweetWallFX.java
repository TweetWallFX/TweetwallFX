/*
 * The MIT License
 *
 * Copyright 2014-2015 TweetWallFX
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

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Point3D;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.fxyz.cameras.AdvancedCamera;
import org.fxyz.cameras.CameraTransformer;
import org.fxyz.extras.Skybox;
import org.fxyz.tests.SkyBoxTest;
import org.tweetwallfx.threed.billboard.DevoxxBillboardLogo;
import org.tweetwallfx.tweet.api.Tweeter;

/**
 * TweetWallFX - Devoxx 2014 {@literal @}johanvos {@literal @}SvenNB
 * {@literal @}SeanMiPhillips {@literal @}jdub1581 {@literal @}JPeredaDnr
 *
 * JavaFX 3D Application that renders an SkyBox cube from F(x)yz library Inside
 * the box there are several tori with rotating banners containing snapshot of
 * tweets related to @devoxx
 *
 * TODO -...
 */
public class TweetWallFX extends Application {

    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;

    private Group root;
    private Skybox skyBox;
    final Rotate rotateY = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
    private AdvancedCamera camera;
    private static final double cameraDistance = 5000;
    private final CameraTransformer cameraTransform = new CameraTransformer();
    private static final int MAX_TORI = 5;
    private final Group toriGroup = new Group();
    private final Group twToriGroup = new Group();

    private final Image top = new Image(SkyBoxTest.class.getResource("res/top.png").toExternalForm()),
            bottom = new Image(SkyBoxTest.class.getResource("res/bottom.png").toExternalForm()),
            left = new Image(SkyBoxTest.class.getResource("res/left.png").toExternalForm()),
            right = new Image(SkyBoxTest.class.getResource("res/right.png").toExternalForm()),
            front = new Image(SkyBoxTest.class.getResource("res/front.png").toExternalForm()),
            back = new Image(SkyBoxTest.class.getResource("res/back.png").toExternalForm());

    private Tweeter tweeter;
    private CLogOut log;
    private static final String hashtag = "#Google";
    private TweetsToTori tweetsTask;

    @Override
    public void start(Stage primaryStage) {
        root = new Group();

        camera = new AdvancedCamera();
        cameraTransform.setTranslate(0, 0, 0);
        cameraTransform.getChildren().addAll(camera);
        camera.setNearClip(0.1);
        camera.setFarClip(1000000.0);
        camera.setFieldOfView(42);
        camera.setTranslateZ(-cameraDistance);
        cameraTransform.rx.setAngle(15.0);

        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateX(camera.getTranslateX());
        light.setTranslateY(camera.getTranslateY());
        light.setTranslateZ(camera.getTranslateZ());
        cameraTransform.getChildren().add(light);

        root.getChildren().add(cameraTransform);
        root.setDepthTest(DepthTest.ENABLE);

        // Load Skybox AFTER camera is initialized
        double size = 100000D;
        skyBox = new Skybox(top, bottom, left, right, front, back,
                size, camera);

        toriGroup.setDepthTest(DepthTest.ENABLE);
        toriGroup.getChildren().add(cameraTransform);

        double tam = 2048;
        List<Point3D> fixPos = FXCollections.observableArrayList(new Point3D(tam / 2, tam / 2, tam / 2),
                new Point3D(-tam / 2, tam / 2, -tam / 2), new Point3D(tam, -tam / 2, tam / 2),
                new Point3D(-tam, -3 * tam / 4, 3 * tam / 4), new Point3D(0, 0, 0));

        IntStream.range(0, MAX_TORI).boxed().forEach(i -> {
            Random r = new Random();
            float randomRadius = (r.nextFloat() * 100f) + 550f;
            float randomTubeRadius = (r.nextFloat() * 100f) + 300f;
            Color randomColor = new Color(r.nextDouble(), r.nextDouble(), r.nextDouble(), r.nextDouble());

            SegmentedTorus torus = new SegmentedTorus(50, 42, 0, randomRadius, randomTubeRadius, randomColor);
            SegmentedTorus twTorus = new SegmentedTorus(50, 42, 14, randomRadius, randomTubeRadius, Color.WHITESMOKE);
            twTorus.setDiffuseMap(new Image(getClass().getResourceAsStream("tweet1.jpg")));

            Translate translate = new Translate(fixPos.get(i).getX(), fixPos.get(i).getY(), fixPos.get(i).getZ());
            Rotate rotateX = new Rotate(Math.random() * 45, Rotate.X_AXIS);
            Rotate rotateY0 = new Rotate(10, Rotate.Y_AXIS);
            Rotate rotateZ = new Rotate(Math.random() * 45, Rotate.Z_AXIS);

            torus.getTransforms().addAll(translate, rotateX, rotateY0, rotateZ, rotateY);
            twTorus.getTransforms().addAll(translate, rotateX, rotateY0, rotateZ, rotateY);
            toriGroup.getChildren().addAll(torus);
            twToriGroup.getChildren().addAll(twTorus);
        });

        DevoxxBillboardLogo logo = new DevoxxBillboardLogo(1500, 700);
        logo.setFrequency(20.0f);
        logo.setPeriod(55.0f);
        logo.setWaveLength(80.0f);
        logo.setAmplitude(35.0f);
        logo.setTranslateZ(1800);
        logo.setTranslateY(-2700);
        PointLight light2 = new PointLight(Color.WHITESMOKE);
        light2.setTranslateY(-250);
        light2.translateZProperty().bind(logo.translateZProperty().subtract(800));

        Scene scene = new Scene(new Group(root), 1024, 720, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.TRANSPARENT);
        scene.setCamera(camera);
        root.getChildren().addAll(camera.getWrapper(), logo, light2);

        scene.setOnMousePressed((MouseEvent me) -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
        });
        scene.setOnMouseDragged((MouseEvent me) -> {
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            cameraTransform.ry.setAngle(((cameraTransform.ry.getAngle() + (mousePosX - mouseOldX) * 2.0) % 360 + 540) % 360 - 180);
            cameraTransform.rx.setAngle(((cameraTransform.rx.getAngle() - (mousePosY - mouseOldY) * 2.0) % 360 + 540) % 360 - 180);

        });

        primaryStage.setTitle("TweetWallFX Test - Devoxx 2014");
        primaryStage.setScene(scene);
        primaryStage.show();

        root.getChildren().addAll(skyBox, toriGroup, twToriGroup);
        root.setAutoSizeChildren(true);

        /* TWITTER */
        log = CLogOut.getInstance();
        log.getMessages().addListener((ob, s, s1) -> System.out.println(s1));

        final Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        tweeter = Tweeter.getInstance();
                        return null;
                    }
                };
                return task;
            }
        };

        service.setOnSucceeded(e -> {
            if (!hashtag.isEmpty() && tweeter != null) {
//                log.info("starting search for " + hashtag);
                tweetsTask = new TweetsToTori(tweeter, hashtag, twToriGroup);
                tweetsTask.start();
            }
        });

        service.start();

        /* ANIMATIONS */
        final Timeline timeTori = new Timeline();
        timeTori.setCycleCount(Timeline.INDEFINITE);
        final KeyValue kv1 = new KeyValue(rotateY.angleProperty(), 360);
        final KeyFrame kf1 = new KeyFrame(Duration.millis(15000), kv1);
        timeTori.getKeyFrames().addAll(kf1);
        timeTori.play();

        final Timeline timeSky = new Timeline();
        timeSky.setCycleCount(Timeline.INDEFINITE);
        final KeyValue kv2 = new KeyValue(skyBox.rotateProperty(), 360);
        final KeyFrame kf2 = new KeyFrame(Duration.millis(100000), kv2);
        timeSky.getKeyFrames().addAll(kf2);
        timeSky.play();
    }

    @Override
    public void stop() {
        if (tweetsTask != null) {
            tweetsTask.stop();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
