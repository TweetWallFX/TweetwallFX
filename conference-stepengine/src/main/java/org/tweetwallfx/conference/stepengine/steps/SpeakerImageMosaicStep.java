/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 TweetWallFX
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
package org.tweetwallfx.conference.stepengine.steps;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.CacheHint;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.tweetwallfx.conference.stepengine.dataprovider.SpeakerImageProvider;
import org.tweetwallfx.controls.WordleSkin;
import org.tweetwallfx.controls.steps.ImageMosaicStep;
import org.tweetwallfx.stepengine.api.DataProvider;
import org.tweetwallfx.stepengine.api.Step;
import org.tweetwallfx.stepengine.api.StepEngine.MachineContext;
import org.tweetwallfx.stepengine.api.config.AbstractConfig;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;
import org.tweetwallfx.transitions.LocationTransition;
import org.tweetwallfx.transitions.SizeTransition;

public class SpeakerImageMosaicStep implements Step {

    private final Config config;

    private static final Random RANDOM = new SecureRandom();
    private final ImageView[][] rects;
    private final Bounds[][] bounds;
    private final Set<Integer> highlightedIndexes = new HashSet<>();
    private Pane pane;
    private int count = 0;

    private SpeakerImageMosaicStep(Config config) {
        this.config = config;
        this.rects = new ImageView[config.columns][config.rows];
        this.bounds = new Bounds[config.columns][config.rows];
    }

    @Override
    public void doStep(final MachineContext context) {
        WordleSkin wordleSkin = (WordleSkin) context.get("WordleSkin");
        pane = wordleSkin.getPane();
        var speakerImageProvider = context.getDataProvider(SpeakerImageProvider.class);
        Transition createMosaicTransition = createMosaicTransition(speakerImageProvider);
        createMosaicTransition.setOnFinished(event
                -> executeAnimations(context));
        createMosaicTransition.play();
    }

    @Override
    public java.time.Duration preferredStepDuration(final MachineContext context) {
        return java.time.Duration.ofSeconds(config.getStepDuration());
    }

    private void executeAnimations(final MachineContext context) {
        ImageWallAnimationTransition highlightAndZoomTransition
                = createHighlightAndZoomTransition();
        highlightAndZoomTransition.transition.play();
        highlightAndZoomTransition.transition.setOnFinished(event1 -> {
            Transition revert
                    = createReverseHighlightAndZoomTransition(highlightAndZoomTransition.column, highlightAndZoomTransition.row);
            revert.setDelay(Duration.seconds(3));
            revert.play();
            revert.setOnFinished(event -> {
                count++;
                if (count < 3) {
                    executeAnimations(context);
                } else {
                    count = 0;
                    ParallelTransition cleanup = new ParallelTransition();
                    for (int i = 0; i < config.columns; i++) {
                        for (int j = 0; j < config.rows; j++) {
                            FadeTransition ft = new FadeTransition(Duration.seconds(0.1), rects[i][j]);
                            ft.setToValue(0);
                            cleanup.getChildren().addAll(ft);
                        }
                    }
                    cleanup.setOnFinished(cleanUpDown -> {
                        for (int i = 0; i < config.columns; i++) {
                            for (int j = 0; j < config.rows; j++) {
                                pane.getChildren().remove(rects[i][j]);
                            }
                        }
                        highlightedIndexes.clear();
                        context.proceed();
                    });
                    cleanup.play();
                }
            });
        });
    }

    private Transition createMosaicTransition(SpeakerImageProvider speakerImageProvider) {
        final SequentialTransition fadeIn = new SequentialTransition();
        final List<FadeTransition> allFadeIns = new ArrayList<>();
        final double width = config.width / config.columns - 10;
        final double height = config.height / config.rows - 8;
        final List<Image> speakerImageList = new ArrayList<>(speakerImageProvider.getImages().toList());

        for (int i = 0; i < config.columns; i++) {
            for (int j = 0; j < config.rows; j++) {
                int index = RANDOM.nextInt(speakerImageList.size());
                var image = speakerImageList.remove(index);
                ImageView imageView = new ImageView(image);
                imageView.setCache(true);
                imageView.setCacheHint(CacheHint.SPEED);
                if (image.getWidth() > image.getHeight()) {
                    imageView.setFitHeight(height);
                } else {
                    imageView.setFitWidth(width);
                }
                var clip = new Rectangle(width,height);
                imageView.setClip(clip);
                imageView.setPreserveRatio(true);
                imageView.setEffect(new GaussianBlur(0));
                rects[i][j] = imageView;
                bounds[i][j] = new BoundingBox(i * (width + 10) + 5 + config.layoutX, j * (height + 8) + 4 + config.layoutY, width, height);
                rects[i][j].setOpacity(0);
                rects[i][j].setLayoutX(bounds[i][j].getMinX());
                rects[i][j].setLayoutY(bounds[i][j].getMinY());
                pane.getChildren().add(rects[i][j]);
                FadeTransition ft = new FadeTransition(Duration.seconds(0.3), imageView);
                ft.setToValue(1);
                allFadeIns.add(ft);
            }
        }
        Collections.shuffle(allFadeIns);
        fadeIn.getChildren().addAll(allFadeIns);
        return fadeIn;
    }

    private ImageWallAnimationTransition createHighlightAndZoomTransition() {
        // select next random not but not previously shown image
        int index;
        do {
            index = RANDOM.nextInt(30);
        } while (!highlightedIndexes.add(index));

        int column = index % config.columns;
        int row = index / config.rows;

        ImageView randomView = rects[column][row];
        randomView.setClip(null);
        randomView.toFront();
        ParallelTransition firstParallelTransition = new ParallelTransition();
        ParallelTransition secondParallelTransition = new ParallelTransition();

        for (int i = 0; i < config.columns; i++) {
            for (int j = 0; j < config.rows; j++) {
                if ((i == column) && (j == row)) {
                    continue;
                }
                FadeTransition ft = new FadeTransition(Duration.seconds(1), rects[i][j]);
                ft.setToValue(0.3);
                firstParallelTransition.getChildren().add(ft);
            }
        }
        for (int i = 0; i < config.columns; i++) {
            for (int j = 0; j < config.rows; j++) {
                if ((i == column) && (j == row)) {
                    continue;
                }

                GaussianBlur blur = (GaussianBlur) rects[i][j].getEffect();
                if (null == blur) {
                    blur = new GaussianBlur(0);
                    rects[i][j].setEffect(blur);
                }
//                BlurTransition blurTransition = new BlurTransition(Duration.seconds(0.5), blur);
//                blurTransition.setToRadius(10);
//                secondParallelTransition.getChildren().addAll(blurTransition);
            }
        }

        double maxWidth = config.width * 0.8;
        double maxHeight = config.height * 0.8;

        double realWidth = randomView.getImage().getWidth();
        double realHeight = randomView.getImage().getHeight();

        double scaleFactor = Math.min(maxWidth / realWidth, maxHeight / realHeight);

        double targetWidth = realWidth * scaleFactor;
        double targetheight = realHeight * scaleFactor;

        final SizeTransition zoomBox = new SizeTransition(Duration.millis(config.zoomDurationAnimation), randomView.fitWidthProperty(), randomView.fitHeightProperty())
                .withWidth(randomView.getLayoutBounds().getWidth(), targetWidth)
                .withHeight(randomView.getLayoutBounds().getHeight(), targetheight);
        final LocationTransition trans = new LocationTransition(Duration.millis(config.zoomDurationAnimation), randomView)
                .withX(randomView.getLayoutX(), config.width / 2 - targetWidth / 2)
                .withY(randomView.getLayoutY(), config.height / 2 - targetheight / 2);
        secondParallelTransition.getChildren().addAll(trans, zoomBox);

        SequentialTransition seqT = new SequentialTransition();
        seqT.getChildren().addAll(firstParallelTransition, secondParallelTransition);

        firstParallelTransition.setOnFinished(event -> {
//            DropShadow ds = new DropShadow();
//            ds.setOffsetY(10.0);
//            ds.setOffsetX(10.0);
//            ds.setColor(Color.GRAY);
//            randomView.setEffect(ds);
        });

        return new ImageWallAnimationTransition(seqT, column, row);
    }

    private Transition createReverseHighlightAndZoomTransition(final int column, final int row) {
        ImageView randomView = rects[column][row];
        randomView.toFront();
        ParallelTransition firstParallelTransition = new ParallelTransition();
        ParallelTransition secondParallelTransition = new ParallelTransition();

        for (int i = 0; i < config.columns; i++) {
            for (int j = 0; j < config.rows; j++) {
                if ((i == column) && (j == row)) {
                    continue;
                }
                FadeTransition ft = new FadeTransition(Duration.seconds(1), rects[i][j]);
                ft.setFromValue(0.3);
                ft.setToValue(1.0);
                firstParallelTransition.getChildren().add(ft);
            }
        }

        double width = config.width / config.columns - 10;
        double height = config.height / config.rows - 8;

        final SizeTransition zoomBox = new SizeTransition(Duration.millis(config.zoomDurationAnimation), randomView.fitWidthProperty(), randomView.fitHeightProperty())
                .withWidth(randomView.getLayoutBounds().getWidth(), width)
                .withHeight(randomView.getLayoutBounds().getHeight(), height);
        final LocationTransition trans = new LocationTransition(Duration.millis(config.zoomDurationAnimation), randomView)
                .withX(randomView.getLayoutX(), bounds[column][row].getMinX())
                .withY(randomView.getLayoutY(), bounds[column][row].getMinY());
        secondParallelTransition.getChildren().addAll(trans, zoomBox);
        secondParallelTransition.setOnFinished(t -> {
            randomView.setClip(new Rectangle(width, height));
        });

        SequentialTransition seqT = new SequentialTransition();
        seqT.getChildren().addAll(secondParallelTransition, firstParallelTransition);

        secondParallelTransition.setOnFinished(event
                -> randomView.setEffect(null));

        return seqT;
    }

    private static class ImageWallAnimationTransition {

        private final Transition transition;
        private final int column;
        private final int row;

        public ImageWallAnimationTransition(final Transition transition, final int column, final int row) {
            this.transition = transition;
            this.column = column;
            this.row = row;
        }
    }

    /**
     * Implementation of {@link Step.Factory} as Service implementation creating
     * {@link ImageMosaicStep}.
     */
    public static final class FactoryImpl implements Step.Factory {

        @Override
        public SpeakerImageMosaicStep create(final StepEngineSettings.StepDefinition stepDefinition) {
            return new SpeakerImageMosaicStep(stepDefinition.getConfig(Config.class));
        }

        @Override
        public Class<SpeakerImageMosaicStep> getStepClass() {
            return SpeakerImageMosaicStep.class;
        }

        @Override
        public Collection<Class<? extends DataProvider>> getRequiredDataProviders(final StepEngineSettings.StepDefinition stepSettings) {
            return Arrays.asList(SpeakerImageProvider.class);
        }
    }

    public static class Config extends AbstractConfig {

        public double layoutX = 0;
        public double layoutY = 0;
        public double width = 800;
        public double height = 800;
        public int columns = 14;
        public int rows = 12;
        public double zoomDurationAnimation = 2500;
    }
}
