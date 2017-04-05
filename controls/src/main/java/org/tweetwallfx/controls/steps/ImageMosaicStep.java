/*
 * The MIT License
 *
 * Copyright 2014-2016 TweetWallFX
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
package org.tweetwallfx.controls.steps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.CacheHint;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.tweetwallfx.controls.WordleSkin;
import org.tweetwallfx.controls.dataprovider.ImageMosaicDataProvider;
import org.tweetwallfx.controls.dataprovider.ImageMosaicDataProvider.ImageStore;
import org.tweetwallfx.controls.stepengine.AbstractStep;
import org.tweetwallfx.controls.stepengine.StepEngine;
import org.tweetwallfx.controls.transition.LocationTransition;
import org.tweetwallfx.controls.transition.SizeTransition;

public class ImageMosaicStep extends AbstractStep {

    private static final Random RANDOM = new Random();
    private final ImageView[][] rects = new ImageView[6][5];
    private final Bounds[][] bounds = new Bounds[6][5];
    private final Set<Integer> highlightedIndexes = new HashSet<>();
    private Pane pane;
    private int count = 0;

    @Override
    public void doStep(StepEngine.MachineContext context) {
        WordleSkin wordleSkin = (WordleSkin) context.get("WordleSkin");
        ImageMosaicDataProvider dataProvider = wordleSkin.getSkinnable().getDataProvider(ImageMosaicDataProvider.class);
        pane = wordleSkin.getPane();
        if (dataProvider.getImages().size() < 35) {
            context.proceed();
        } else {
            Transition createMosaicTransition = createMosaicTransition(dataProvider.getImages());
            createMosaicTransition.setOnFinished((ActionEvent event) -> {
                executeAnimations(context);
            });

            createMosaicTransition.play();
        }
    }

    @Override
    public long preferredStepDuration(StepEngine.MachineContext context) {
        return 1000;
    }

    private void executeAnimations(StepEngine.MachineContext context) {
        ImageWallAnimationTransition highlightAndZoomTransition
                = createHighlightAndZoomTransition();
        highlightAndZoomTransition.transition.play();
        highlightAndZoomTransition.transition.setOnFinished((event1) -> {
            Transition revert
                    = createReverseHighlightAndZoomTransition(highlightAndZoomTransition.column, highlightAndZoomTransition.row);
            revert.setDelay(Duration.seconds(3));
            revert.play();
            revert.setOnFinished((ActionEvent event) -> {
                count++;
                if (count < 3) {
                    executeAnimations(context);
                } else {
                    count = 0;
                    ParallelTransition cleanup = new ParallelTransition();
                    for (int i = 0; i < 6; i++) {
                        for (int j = 0; j < 5; j++) {
                            FadeTransition ft = new FadeTransition(Duration.seconds(0.5), rects[i][j]);
                            ft.setToValue(0);
                            cleanup.getChildren().addAll(ft);
                        }
                    }
                    cleanup.setOnFinished((cleanUpDown) -> {
                        for (int i = 0; i < 6; i++) {
                            for (int j = 0; j < 5; j++) {
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

    private Transition createMosaicTransition(List<ImageStore> imageStores) {
        SequentialTransition fadeIn = new SequentialTransition();
        List<FadeTransition> allFadeIns = new ArrayList<>();

        double width = pane.getWidth() / 6.0 - 10;
        double height = pane.getHeight() / 5.0 - 8;
        List<ImageStore> distillingList = new LinkedList<>(imageStores);
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                int index = RANDOM.nextInt(distillingList.size());
                ImageStore selectedImage = distillingList.remove(index);
                ImageView imageView = new ImageView(selectedImage.getImage());
                imageView.setCache(true);
                imageView.setCacheHint(CacheHint.SPEED);
                imageView.setFitWidth(width);
                imageView.setFitHeight(height);
                imageView.setEffect(new GaussianBlur(0));
                rects[i][j] = imageView;
                bounds[i][j] = new BoundingBox(i * (width + 10) + 5, j * (height + 8) + 4, width, height);
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

        int column = index % 6;
        int row = index / 6;

        ImageView randomView = rects[column][row];
        randomView.toFront();
        ParallelTransition firstParallelTransition = new ParallelTransition();
        ParallelTransition secondParallelTransition = new ParallelTransition();

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                if ((i == column) && (j == row)) {
                    continue;
                }
                FadeTransition ft = new FadeTransition(Duration.seconds(1), rects[i][j]);
                ft.setToValue(0.3);
                firstParallelTransition.getChildren().add(ft);
            }
        }
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
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

        LocationTransition trans = new LocationTransition(Duration.seconds(2.5), randomView);
        SizeTransition zoomBox = new SizeTransition(Duration.seconds(2.5), randomView.fitWidthProperty(), randomView.fitHeightProperty());

        double maxWidth = pane.getWidth() * 0.8;
        double maxHeight = pane.getHeight() * 0.8;

        double realWidth = randomView.getImage().getWidth();
        double realHeight = randomView.getImage().getHeight();

        double scaleFactor = Math.min(maxWidth / realWidth, maxHeight / realHeight);

        double targetWidth = realWidth * scaleFactor;
        double targetheight = realHeight * scaleFactor;

        zoomBox.setFromWidth(randomView.getLayoutBounds().getWidth());
        zoomBox.setFromHeight(randomView.getLayoutBounds().getHeight());
        zoomBox.setToWidth(targetWidth);
        zoomBox.setToHeight(targetheight);
        trans.setFromX(randomView.getLayoutX());
        trans.setFromY(randomView.getLayoutY());
        trans.setToX(pane.getWidth() / 2 - targetWidth / 2);
        trans.setToY(pane.getHeight() / 2 - targetheight / 2);
        secondParallelTransition.getChildren().addAll(trans, zoomBox);

        SequentialTransition seqT = new SequentialTransition();
        seqT.getChildren().addAll(firstParallelTransition, secondParallelTransition);

        firstParallelTransition.setOnFinished((event) -> {
//            DropShadow ds = new DropShadow();
//            ds.setOffsetY(10.0);
//            ds.setOffsetX(10.0);
//            ds.setColor(Color.GRAY);
//            randomView.setEffect(ds);
        });

        return new ImageWallAnimationTransition(seqT, column, row);
    }

    private Transition createReverseHighlightAndZoomTransition(int column, int row) {
        ImageView randomView = rects[column][row];
        randomView.toFront();
        ParallelTransition firstParallelTransition = new ParallelTransition();
        ParallelTransition secondParallelTransition = new ParallelTransition();

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                if ((i == column) && (j == row)) {
                    continue;
                }
                FadeTransition ft = new FadeTransition(Duration.seconds(1), rects[i][j]);
                ft.setFromValue(0.3);
                ft.setToValue(1.0);
                firstParallelTransition.getChildren().add(ft);
            }
        }

        LocationTransition trans = new LocationTransition(Duration.seconds(2.5), randomView);
        SizeTransition zoomBox = new SizeTransition(Duration.seconds(2.5), randomView.fitWidthProperty(), randomView.fitHeightProperty());

        double width = pane.getWidth() / 6.0 - 10;
        double height = pane.getHeight() / 5.0 - 8;

        zoomBox.setFromWidth(randomView.getLayoutBounds().getWidth());
        zoomBox.setFromHeight(randomView.getLayoutBounds().getHeight());
        zoomBox.setToWidth(width);
        zoomBox.setToHeight(height);

        trans.setFromX(randomView.getLayoutX());
        trans.setFromY(randomView.getLayoutY());
        trans.setToX(bounds[column][row].getMinX());
        trans.setToY(bounds[column][row].getMinY());
        secondParallelTransition.getChildren().addAll(trans, zoomBox);

        SequentialTransition seqT = new SequentialTransition();
        seqT.getChildren().addAll(secondParallelTransition, firstParallelTransition);

        secondParallelTransition.setOnFinished((event) -> {
            randomView.setEffect(null);
        });

        return seqT;
    }

    private void cleanup() {
    }

    private static class ImageWallAnimationTransition {

        final Transition transition;
        final int column;
        final int row;

        public ImageWallAnimationTransition(Transition transition, int column, int row) {
            this.transition = transition;
            this.column = column;
            this.row = row;
        }
    }
}
